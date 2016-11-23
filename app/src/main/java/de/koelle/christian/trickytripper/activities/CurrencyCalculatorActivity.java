package de.koelle.christian.trickytripper.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

import de.koelle.christian.common.abs.ActionBarSupport;
import de.koelle.christian.common.options.OptionConstraintsInflater;
import de.koelle.christian.common.text.BlankTextWatcher;
import de.koelle.christian.common.ui.filter.DecimalNumberInputUtil;
import de.koelle.christian.common.utils.NumberUtils;
import de.koelle.christian.common.utils.UiUtils;
import de.koelle.christian.trickytripper.R;
import de.koelle.christian.trickytripper.TrickyTripperApp;
import de.koelle.christian.trickytripper.activitysupport.CurrencySelectionResultSupport;
import de.koelle.christian.trickytripper.activitysupport.ImportOptionSupport;
import de.koelle.christian.trickytripper.activitysupport.SpinnerViewSupport;
import de.koelle.christian.trickytripper.constants.Rc;
import de.koelle.christian.trickytripper.model.Amount;
import de.koelle.christian.trickytripper.model.ExchangeRate;
import de.koelle.christian.trickytripper.model.ImportOrigin;
import de.koelle.christian.trickytripper.modelutils.AmountViewUtils;
import de.koelle.christian.trickytripper.ui.model.RowObject;
import de.koelle.christian.trickytripper.ui.model.RowObjectCallback;
import de.koelle.christian.trickytripper.ui.utils.ExchangeRateDescriptionUtils;
import de.koelle.christian.trickytripper.ui.utils.UiAmountViewUtils;

public class CurrencyCalculatorActivity extends AppCompatActivity {

    private Amount inputAmount;
    private Amount resultAmount;
    private boolean checkboxSelectionSaveNewAmendedExchangeRate;
    private int resultViewId;
    private Double exchangeRateInput = 0.0;
    private ExchangeRate exchangeRateSelected;
    private ImportOptionSupport importOptionSupport;
    private ExchangeRateDescriptionUtils exchangeRateDescriptionUtils;
    private BlankTextWatcher inputListenerValue;
    private BlankTextWatcher inputListenerRate;
    @SuppressWarnings("rawtypes")
    private ArrayAdapter<RowObject> adapter;
    private Spinner spinner;
    private OnItemSelectedListener rateSelectionListener;

    private enum UpdateExclusion {
        NONE, RATE_VALUE, INPUT_VALUE
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.currency_calculator_view);

        this.importOptionSupport = new ImportOptionSupport(getApp()
                .getViewController(), getApp().getMiscController(), this);
        this.exchangeRateDescriptionUtils = new ExchangeRateDescriptionUtils(
                this.getResources());

        readAndSetInput(getIntent());

        initAndBindCurrencySelectionButton();
        initAndBindEditText();
        initAndBindCheckbox();
        initExchangeRateSpinner();

        ActionBarSupport.addBackButton(this);

        // --> onResume();
    }

    private void initAndBindCurrencySelectionButton() {
        getCurrencySelectionButton().setText(
                inputAmount.getUnit().getCurrencyCode());
    }

    private void initAndBindEditText() {
        EditText editTextInputValue = getEditTextInputValue();
        EditText editTextInputExchangeRate = getEditTextExchangeRateValue();

        UiUtils.makeProperNumberInput(editTextInputValue,
                getDecimalNumberInputUtil()
                        .getInputPatternMatcher());
        UiUtils.makeProperNumberInput(editTextInputExchangeRate,
                getDecimalNumberInputUtil()
                        .getExchangeRateInputPatternMatcher());

        inputListenerValue = new BlankTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                String widgetInput = getDecimalNumberInputUtil()
                        .fixInputStringWidgetToParser(s.toString());
                Double valueInput = NumberUtils.getStringToDoubleRounded(
                        getLocale(), widgetInput);
                inputAmount.setValue(valueInput);
                updateViewsState(UpdateExclusion.INPUT_VALUE);
            }
        };
        editTextInputValue.addTextChangedListener(inputListenerValue);

        inputListenerRate = new BlankTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                String widgetInput = getDecimalNumberInputUtil()
                        .fixInputStringWidgetToParser(s.toString());
                Double valueInput = NumberUtils.getStringToDoubleNonRounded(
                        getLocale(), widgetInput);
                exchangeRateInput = valueInput;
                updateViewsState(UpdateExclusion.RATE_VALUE);
            }
        };
        editTextInputExchangeRate.addTextChangedListener(inputListenerRate);
    }

    private EditText getEditTextExchangeRateValue() {
        return (EditText) findViewById(R.id.currencyCalculatorView_editText_inputExchangeRate);
    }

    private EditText getEditTextInputValue() {
        return (EditText) findViewById(R.id.currencyCalculatorView_editText_inputValue);
    }

    private void initAndBindCheckbox() {
        CompoundButton.OnCheckedChangeListener checkBoxListener = new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                checkboxSelectionSaveNewAmendedExchangeRate = isChecked;
            }
        };
        getCheckbox().setOnCheckedChangeListener(checkBoxListener);

    }

    private CheckBox getCheckbox() {
        return (CheckBox) findViewById(R.id.currencyCalculatorView_checkbox_saveNewValue);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Currency result = CurrencySelectionResultSupport.onActivityResult(
                requestCode, resultCode, data, this);
        if (result != null) {
            inputAmount.setUnit(result);
        }
        refreshOnResume();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshOnResume();
    }

    private void refreshOnResume() {
        updateExchangeRates();
        updateViewsState(UpdateExclusion.NONE);
        supportInvalidateOptionsMenu();
    }

    private void readAndSetInput(Intent intent) {
        Double inputValue = intent.getDoubleExtra(
                Rc.ACTIVITY_PARAM_CURRENCY_CALCULATOR_IN_VALUE,
                0.0d);
        inputAmount = createAmount(inputValue, null);

        Bundle extras = getIntent().getExtras();

        Currency resultCurrencySubmitted = (Currency) extras
                .get(Rc.ACTIVITY_PARAM_CURRENCY_CALCULATOR_IN_RESULT_CURRENCY);

        Currency resultCurrency = (resultCurrencySubmitted != null) ? resultCurrencySubmitted
                : getApp().getTripController().getLoadedTripBaseCurrency();

        resultViewId = intent.getIntExtra(
                Rc.ACTIVITY_PARAM_CURRENCY_CALCULATOR_IN_RESULT_VIEW_ID, -1);

        resultAmount = createAmount(0.0d, resultCurrency);

        Currency sourceCurrencyToBeUsed = getApp().getMiscController()
                .getCurrencyFavorite(resultCurrency);

        inputAmount.setUnit(sourceCurrencyToBeUsed);
    }

    private void updateOutputFields() {
        TextView outputTargetCurrency = (TextView) findViewById(R.id.currencyCalculatorView_txt_outputCurrency);
        outputTargetCurrency.setText(resultAmount.getUnit().getCurrencyCode());

        TextView outputTargetValue = (TextView) findViewById(R.id.currencyCalculatorView_txt_resultValue);
        outputTargetValue.setText(AmountViewUtils.getAmountString(getLocale(),
                resultAmount, true, false, false, true, true));
    }

    private void updateViewsState(UpdateExclusion updateExclusion) {
        updateEditFieldsFromModel(updateExclusion);
        updateExchangeRateSpinnerEnabled();
        updateCalculation();
        updateCheckboxState();
        invalidateOptionsMenu();
    }

    private void updateEditFieldsFromModel(UpdateExclusion updateExclusion) {
        if (!UpdateExclusion.INPUT_VALUE.equals(updateExclusion)) {
            updateInputAmountFieldFromModel();
        }
        if (!UpdateExclusion.RATE_VALUE.equals(updateExclusion)) {
            updateExchangeRateFieldFromModel();
        }
    }

    private void updateExchangeRateSpinnerEnabled() {
        Spinner spinner = getExchangeRateSpinner();
        spinner.setEnabled(isExchangeRateSelected());
    }

    private void updateExchangeRateFieldFromModel() {
        EditText editTextInputExchangeRate = getEditTextExchangeRateValue();
        editTextInputExchangeRate.removeTextChangedListener(inputListenerRate);
        UiAmountViewUtils.writeDoubleToEditText(exchangeRateInput,
                editTextInputExchangeRate, getLocale(),
                getDecimalNumberInputUtil());
        editTextInputExchangeRate.addTextChangedListener(inputListenerRate);
    }

    private void updateInputAmountFieldFromModel() {
        EditText editTextInputValue = getEditTextInputValue();
        editTextInputValue.removeTextChangedListener(inputListenerValue);
        UiAmountViewUtils.writeAmountToEditText(inputAmount,
                editTextInputValue, getLocale(), getDecimalNumberInputUtil());
        editTextInputValue.addTextChangedListener(inputListenerValue);
    }

    protected void updateCalculation() {
        Double multiply = NumberUtils.multiply(inputAmount.getValue(),exchangeRateInput);
        resultAmount.setValue(multiply);
        updateOutputFields();
    }

    private void updateCheckboxState() {
        boolean dirtyRate = isDirtyRate();
        getCheckbox().setChecked(dirtyRate);
        getCheckbox().setEnabled(dirtyRate);
    }

    private boolean canResultBeCalculated() {
        return resultAmount != null
                && resultAmount.getValue() != null
                && exchangeRateInput != null
                && exchangeRateInput > 0.0d
                && resultAmount.getValue() > 0.0d;
    }

    private Locale getLocale() {
        return getResources().getConfiguration().locale;
    }

    @SuppressWarnings("rawtypes")
    private void initExchangeRateSpinner() {

        spinner = getExchangeRateSpinner();
        List<RowObject> spinnerObjects = new ArrayList<>();

        adapter = new ArrayAdapter<RowObject>(this,
                android.R.layout.simple_spinner_item, spinnerObjects) {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                /* Display currency code only when not in list view. */
                TextView result = (TextView) super.getView(position,
                        convertView, parent);
                result.setText(exchangeRateDescriptionUtils.deriveDescription(
                        ((ExchangeRate) this.getItem(position).getRowObject()))
                        .toString());
                return result;
            }
        };

        adapter.setDropDownViewResource(R.layout.selection_list_medium);
        spinner.setPromptId(R.string.currencyCalculatorViewSpinnerPrompt);
        spinner.setAdapter(adapter);

        rateSelectionListener = new OnItemSelectedListener() {

            @SuppressWarnings("unchecked")
            public void onItemSelected(AdapterView<?> parentView,
                                       View selectedItemView, int position, long id) {
                if (position >= 0) {
                    Object o = spinner.getSelectedItem();
                    ExchangeRate selectedRate = ((RowObject<ExchangeRate>) o)
                            .getRowObject();
                    if (exchangeRateSelected == null
                            || !exchangeRateSelected.equals(selectedRate)) {
                        fillRateModel(selectedRate);
                        updateExchangeRateFieldFromModel(); // xx
                        updateViewsState(UpdateExclusion.NONE);
                    }
                }
            }

            public void onNothingSelected(AdapterView<?> parentView) {
                // intentionally blank
            }
        };
        spinner.setOnItemSelectedListener(rateSelectionListener);
    }

    private Spinner getExchangeRateSpinner() {
        return (Spinner) findViewById(R.id.currencyCalculatorView_spinner_exchangeRateSelection);
    }

    @SuppressWarnings("rawtypes")
    private void updateExchangeRates() {

        Currency sourceCurrency = inputAmount.getUnit();
        Currency resultCurrency = resultAmount.getUnit();

        List<ExchangeRate> rates = getApp().getExchangeRateController()
                .findSuitableRates(sourceCurrency, resultCurrency);

        boolean ratesAreAvailable = !rates.isEmpty();

        adapter.clear();
        List<RowObject> spinnerObjects = wrapExchangeRatesInRowObject(rates);
        for (RowObject ro : spinnerObjects) {
            adapter.add(ro);
        }
        Spinner spinner = getExchangeRateSpinner();

        ExchangeRate initialSelection2Be = (ExchangeRate) spinnerObjects.get(0)
                .getRowObject();

        spinner.setOnItemSelectedListener(null);
        SpinnerViewSupport.setSelection(spinner, initialSelection2Be, adapter);
        spinner.setOnItemSelectedListener(rateSelectionListener);

        if (ratesAreAvailable) {
            fillRateModel(initialSelection2Be);
            updateExchangeRateFieldFromModel();

        } else {
            nullRateModel();
        }
    }

    @SuppressWarnings("rawtypes")
    private List<RowObject> wrapExchangeRatesInRowObject(
            List<ExchangeRate> values) {
        List<RowObject> result = new ArrayList<>();

        for (final ExchangeRate value : values) {
            result.add(new RowObject<>(
                    new RowObjectCallback<ExchangeRate>() {
                        public String getStringToDisplay(ExchangeRate c) {
                            return exchangeRateDescriptionUtils
                                    .deriveDescriptionWithRate(c).toString();
                        }
                    }, value));
        }
        if (values.isEmpty()) {
            result.add(new RowObject<>(
                    new RowObjectCallback<ExchangeRate>() {
                        public String getStringToDisplay(ExchangeRate c) {
                            return getResources()
                                    .getString(
                                            R.string.currencyCalculatorViewNoMatchingRatesAvailable);
                        }
                    }, new ExchangeRate()));
        }
        return result;
    }

    private void fillRateModel(ExchangeRate exchangeRateToBeSet) {
        exchangeRateSelected = exchangeRateToBeSet;
        exchangeRateInput = exchangeRateToBeSet.getExchangeRate();
    }

    private void nullRateModel() {
        exchangeRateSelected = null;
        exchangeRateInput = 0.0;
    }

    private TrickyTripperApp getApp() {
        return (TrickyTripperApp) getApplication();
    }

    private Amount createAmount(Double value, Currency resultCurrency) {
        Amount amount = new Amount();
        amount.setUnit(resultCurrency);
        amount.setValue(value);
        return amount;
    }

    private void prepareResultAndFinish() {
        if (isDirtyRate() && checkboxSelectionSaveNewAmendedExchangeRate) {
            createAndSaveNewExchangeRate();
        } else {
            saveExchangeRateUsedLast(exchangeRateSelected);
        }

        Intent resultIntent = new Intent();
        resultIntent.putExtra(Rc.ACTIVITY_PARAM_CURRENCY_CALCULATOR_OUT_AMOUNT,
                resultAmount);
        resultIntent
                .putExtra(Rc.ACTIVITY_PARAM_CURRENCY_CALCULATOR_OUT_VIEW_ID,
                        resultViewId);
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    private boolean isDirtyRate() {
        return exchangeRateSelected != null
                && exchangeRateSelected.getExchangeRate() != null
                && !exchangeRateSelected.getExchangeRate().equals(
                exchangeRateInput) || exchangeRateSelected == null
                && exchangeRateInput > 0.0d;
    }

    private boolean isExchangeRateSelected() {
        return exchangeRateSelected != null;
    }

    private void saveExchangeRateUsedLast(ExchangeRate exchangeRateSelectedHere) {
        if (exchangeRateSelectedHere != null) {
            getApp().getExchangeRateController().persistExchangeRateUsedLast(
                    exchangeRateSelectedHere);
        }
    }

    /* ============ btn actions ==================== */

    private void createAndSaveNewExchangeRate() {

        ExchangeRate newExchangeRate = new ExchangeRate();
        newExchangeRate.setCurrencyFrom(inputAmount.getUnit());
        newExchangeRate.setCurrencyTo(resultAmount.getUnit());
        newExchangeRate
                .setDescription(new StringBuilder()
                        .append(getResources()
                                .getString(
                                        R.string.currencyCalculatorViewDefaultDescriptionAutoSaveExchangeRate))
                        .append(" ")
                        .append(getApp().getExchangeRateController()
                                .getNextExchangeRateAutoSaveSeqNumber())
                        .toString());
        newExchangeRate.setExchangeRate(exchangeRateInput);
        newExchangeRate.setImportOrigin(ImportOrigin.NONE);

        ExchangeRate persisted = getApp().getExchangeRateController()
                .persistExchangeRate(newExchangeRate);

        saveExchangeRateUsedLast(persisted);
    }

    public void openCurrencySelection(View view) {
        View getCurrencySelectionButton = getCurrencySelectionButton();
        getApp().getViewController().openCurrencySelectionForCalculation(this,
                resultAmount.getUnit(), getCurrencySelectionButton.getId());
    }

    private Button getCurrencySelectionButton() {
        return (Button) findViewById(R.id.currencyCalculatorView_button_inputCurrencySelection);
    }

    public void done() {
        prepareResultAndFinish();
    }

    private DecimalNumberInputUtil getDecimalNumberInputUtil() {
        return getApp().getMiscController().getDecimalNumberInputUtil();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return getApp()
                .getMiscController()
                .getOptionSupport()
                .populateOptionsMenu(
                        new OptionConstraintsInflater()
                                .activity(getMenuInflater())
                                .menu(menu)
                                .options(
                                        new int[]{
                                                R.id.option_accept,
                                                R.id.option_import,
                                                R.id.option_create_exchange_rate_for_source,
                                                R.id.option_help}));
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        boolean enableAccept = canResultBeCalculated();
        MenuItem item = menu.findItem(R.id.option_accept);
        item.setTitle(R.string.currencyCalculatorViewButtonUseResult);
        item.setEnabled(enableAccept);
        item.getIcon().setAlpha((enableAccept) ? 255 : 64);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.option_accept:
                done();
                return true;
            case R.id.option_help:
                getApp().getViewController().openHelp(getSupportFragmentManager());
                return true;
            case R.id.option_import:
                return importOptionSupport.onOptionsItemSelected(this,
                        new Currency[]{inputAmount.getUnit(), resultAmount.getUnit()});
            case R.id.option_create_exchange_rate_for_source:
                getApp().getViewController().openCreateExchangeRate(this, inputAmount.getUnit());
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
