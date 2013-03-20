package de.koelle.christian.trickytripper.activities;

import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
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
import de.koelle.christian.common.options.OptionContraints;
import de.koelle.christian.common.text.BlankTextWatcher;
import de.koelle.christian.common.ui.filter.DecimalNumberInputUtil;
import de.koelle.christian.common.utils.NumberUtils;
import de.koelle.christian.common.utils.UiUtils;
import de.koelle.christian.trickytripper.R;
import de.koelle.christian.trickytripper.TrickyTripperApp;
import de.koelle.christian.trickytripper.activitysupport.CurrencySelectionResultSupport;
import de.koelle.christian.trickytripper.activitysupport.ImportOptionSupport;
import de.koelle.christian.trickytripper.activitysupport.PopupFactory;
import de.koelle.christian.trickytripper.activitysupport.SpinnerViewSupport;
import de.koelle.christian.trickytripper.constants.Rc;
import de.koelle.christian.trickytripper.constants.Rd;
import de.koelle.christian.trickytripper.model.Amount;
import de.koelle.christian.trickytripper.model.ExchangeRate;
import de.koelle.christian.trickytripper.model.ImportOrigin;
import de.koelle.christian.trickytripper.modelutils.AmountViewUtils;
import de.koelle.christian.trickytripper.ui.model.RowObject;
import de.koelle.christian.trickytripper.ui.model.RowObjectCallback;
import de.koelle.christian.trickytripper.ui.utils.ExchangeRateDescriptionUtils;
import de.koelle.christian.trickytripper.ui.utils.UiViewUtils;

public class CurrencyCalculatorActivity extends Activity {

    private Amount inputAmount;
    private Amount resultAmount;
    private boolean checkboxSelectionSaveNewAmendedExchangeRate;
    private int resultViewId;
    private Double exchangeRateInput = Double.valueOf(0.0);
    private ExchangeRate exchangeRateSelected;
    private ImportOptionSupport importOptionSupport;
    private ExchangeRateDescriptionUtils exchangeRateDescriptionUtils;
    private Currency resultCurrency;
    private boolean ratesAreAvailable;

    /* ============== Menu Shit [BGN] ============== */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return getApp().getMiscController().getOptionSupport().populateOptionsMenu(
                new OptionContraints().activity(this).menu(menu)
                        .options(new int[] {
                                R.id.option_import,
                                R.id.option_help
                        }));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.option_help:
            showDialog(Rd.DIALOG_HELP);
            return true;
        case R.id.option_import:
            return importOptionSupport.onOptionsItemSelected(this);
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected Dialog onCreateDialog(int id, Bundle args) {
        Dialog dialog;
        switch (id) {
        case Rd.DIALOG_HELP:
            dialog = PopupFactory.createHelpDialog(this, getApp().getMiscController(), Rd.DIALOG_HELP);
            break;
        default:
            dialog = null;
        }

        return dialog;
    }

    @Override
    protected void onPrepareDialog(int id, Dialog dialog, Bundle args) {
        switch (id) {
        case Rd.DIALOG_HELP:
            // intentionally blank
            break;
        default:
            dialog = null;
        }
        super.onPrepareDialog(id, dialog, args);
    }

    /* ============== Menu Shit [END] ============== */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("On create()");

        setContentView(R.layout.currency_calculator_view);

        this.importOptionSupport = new ImportOptionSupport(getApp().getViewController(), getApp().getMiscController(),
                this);
        this.exchangeRateDescriptionUtils = new ExchangeRateDescriptionUtils(this.getResources());

        readAndSetInput(getIntent());

        Currency sourceCurrencyToBeUsed = getApp().getMiscController().getCurrencyFavorite(resultCurrency);

        inputAmount.setUnit(sourceCurrencyToBeUsed);

        getCurrencySelectionButton().setText(sourceCurrencyToBeUsed.getCurrencyCode());

        initAndBindEditText();

        Currency sourceCurrency = inputAmount.getUnit();
        Currency resultCurrency = resultAmount.getUnit();

        loadAndInitExchangeRates(sourceCurrency, resultCurrency);

        initAndBindCheckbox();

        updateViews();

    }

    private void initAndBindCheckbox() {
        CheckBox checkbox = getCheckbox();

        checkbox.setChecked(checkboxSelectionSaveNewAmendedExchangeRate);

        checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                checkboxSelectionSaveNewAmendedExchangeRate = isChecked;
            }
        });

    }

    private CheckBox getCheckbox() {
        return (CheckBox) findViewById(R.id.currencyCalculatorView_checkbox_saveNewValue);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Currency result = CurrencySelectionResultSupport.onActivityResult(requestCode, resultCode, data, this);
        if (result != null) {
            inputAmount.setUnit(result);
            loadAndInitExchangeRates(inputAmount.getUnit(), resultAmount.getUnit());
            System.out.println("on result");
            updateViews();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("On resume()");
    }

    private void loadAndInitExchangeRates(Currency sourceCurrency, Currency resultCurrency) {
        List<ExchangeRate> rates = getApp().getExchangeRateController().findSuitableRates(sourceCurrency,
                resultCurrency);

        ratesAreAvailable = !rates.isEmpty();

        initExchangeRateSpinner(rates);
    }

    private void readAndSetInput(Intent intent) {
        Double inputValue = intent.getDoubleExtra(Rc.ACTIVITY_PARAM_CURRENCY_CALCULATOR_IN_VALUE,
                Double.valueOf(0.0d));
        inputAmount = createAmount(inputValue, null);

        Bundle extras = getIntent().getExtras();

        Currency resultCurrencySubmitted = (Currency) extras.get(
                Rc.ACTIVITY_PARAM_CURRENCY_CALCULATOR_IN_RESULT_CURRENCY);

        resultCurrency = (resultCurrencySubmitted != null) ? resultCurrencySubmitted : getApp().getTripController()
                .getLoadedTripBaseCurrency();

        resultViewId = intent.getIntExtra(Rc.ACTIVITY_PARAM_CURRENCY_CALCULATOR_IN_RESULT_VIEW_ID, -1);

        resultAmount = createAmount(Double.valueOf(0.0d), resultCurrency);
    }

    private void updateOutputFields() {
        TextView outputTargetCurrency = (TextView) findViewById(R.id.currencyCalculatorView_txt_outputCurrency);
        outputTargetCurrency.setText(resultAmount.getUnit().getCurrencyCode());

        TextView outputTargetValue = (TextView) findViewById(R.id.currencyCalculatorView_txt_resultValue);
        outputTargetValue.setText(AmountViewUtils.getAmountString(getLocale(), resultAmount, true, false, false, true,
                true));
    }

    private void initAndBindEditText() {
        EditText editTextInputValue = (EditText) findViewById(R.id.currencyCalculatorView_editText_inputValue);
        EditText editTextInputExchangeRate = (EditText) findViewById(R.id.currencyCalculatorView_editText_inputExchangeRate);

        UiUtils.makeProperNumberInput(editTextInputValue, getDecimalNumberInputUtil()
                .getExchangeRateInputPatternMatcher());
        UiUtils.makeProperNumberInput(editTextInputExchangeRate, getDecimalNumberInputUtil()
                .getExchangeRateInputPatternMatcher());

        updateEditFieldsFromModel();

        editTextInputValue.addTextChangedListener(new BlankTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                Double valueInput = NumberUtils.getStringToDoubleRounded(getLocale(), s.toString());
                inputAmount.setValue(valueInput);
                System.out.println("afterTextChanged input value");
                updateViews();
            }
        });

        editTextInputExchangeRate.addTextChangedListener(new BlankTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                Double valueInput = NumberUtils.getStringToDoubleRounded(getLocale(), s.toString());
                System.out.println("afterTextChanged rate value");
                exchangeRateInput = valueInput;
                updateViews();
            }
        });
    }

    private void updateEditFieldsFromModel() {
        updateInputAmountFieldFromModel();
        updateExchangeRateFieldFromModel();
    }

    private void updateInputAmountFieldFromModel() {
        EditText editTextInputValue = (EditText) findViewById(R.id.currencyCalculatorView_editText_inputValue);
        UiViewUtils.writeAmountToEditText(inputAmount, editTextInputValue, getLocale());
    }

    private void updateExchangeRateFieldFromModel() {
        EditText editTextInputExchangeRate = (EditText) findViewById(R.id.currencyCalculatorView_editText_inputExchangeRate);
        editTextInputExchangeRate.setText(AmountViewUtils.getDoubleString(getLocale(), exchangeRateInput, true, true,
                true, false));
    }

    private void updateViews() {
        updateCalculation();
        updateCheckboxState();
        updateButtonState();
    }

    protected void updateCalculation() {
        resultAmount.setValue(NumberUtils.multiply(inputAmount.getValue(), exchangeRateInput));
        updateOutputFields();
    }

    private void updateCheckboxState() {
        boolean dirtyRate = isDirtyRate();
        getCheckbox().setChecked(dirtyRate);
        getCheckbox().setEnabled(dirtyRate);
    }

    private void updateButtonState() {
        Button doneButton = (Button) findViewById(R.id.currencyCalculatorView_btn_useExchangedValue);
        doneButton.setEnabled(canResultBeCalculated());
    }

    private boolean canResultBeCalculated() {
        return exchangeRateInput > Double.valueOf(0.0d) && resultAmount.getValue() > Double.valueOf(0.0d);
    }

    private Locale getLocale() {
        Locale locale = getResources().getConfiguration().locale;
        return locale;
    }

    @SuppressWarnings("rawtypes")
    private void initExchangeRateSpinner(List<ExchangeRate> rates) {

        final Spinner spinner = (Spinner) findViewById(R.id.currencyCalculatorView_spinner_exchangeRateSelection);
        List<RowObject> spinnerObjects = wrapExchangeRatesInRowObject(rates);

        ArrayAdapter<RowObject> adapter = new ArrayAdapter<RowObject>(this,
                android.R.layout.simple_spinner_item,
                spinnerObjects) {

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                /* nothing special here. */
                return super.getDropDownView(position, convertView, parent);
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                /* Display currency code only when not in list view. */
                TextView result = (TextView) super.getView(position, convertView, parent);
                result.setText(exchangeRateDescriptionUtils.deriveDescription(
                        ((ExchangeRate) this.getItem(position).getRowObject())).toString());
                return result;
            }
        };

        adapter.setDropDownViewResource(R.layout.selection_list_medium);
        spinner.setPromptId(R.string.currencyCalculatorViewSpinnerPrompt);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(null);

        if (ratesAreAvailable) {

            ExchangeRate initialSelection2Be = (ExchangeRate) spinnerObjects.get(0).getRowObject();
            SpinnerViewSupport.setSelection(spinner, initialSelection2Be, adapter);

            fillRateModel(initialSelection2Be);
            updateExchangeRateFieldFromModel();

            spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

                @SuppressWarnings("unchecked")
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    if (position >= 0) {
                        Object o = spinner.getSelectedItem();
                        ExchangeRate selectedRate = ((RowObject<ExchangeRate>) o).getRowObject();
                        if (exchangeRateSelected == null || !exchangeRateSelected.equals(selectedRate)) {
                            fillRateModel(selectedRate);
                            updateExchangeRateFieldFromModel(); // xx
                            System.out.println("OnItemSelected Rate Spinner");
                            updateViews();
                        }
                    }
                }

                public void onNothingSelected(AdapterView<?> parentView) {
                    // intentionally blank
                }

            });
        }
        else {
            spinner.setEnabled(false);
            nullRateModel();
            updateViews();
        }
    }

    @SuppressWarnings("rawtypes")
    private List<RowObject> wrapExchangeRatesInRowObject(List<ExchangeRate> values) {
        List<RowObject> result = new ArrayList<RowObject>();

        for (final ExchangeRate value : values) {
            result.add(new RowObject<ExchangeRate>(new RowObjectCallback<ExchangeRate>() {
                public String getStringToDisplay(ExchangeRate c) {
                    return exchangeRateDescriptionUtils.deriveDescriptionWithRate(c).toString();
                }
            }, value));
        }
        if (values.isEmpty()) {
            result.add(new RowObject<ExchangeRate>(new RowObjectCallback<ExchangeRate>() {
                public String getStringToDisplay(ExchangeRate c) {
                    return getResources().getString(R.string.currencyCalculatorViewNoMatchingRatesAvailable);
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
        exchangeRateInput = Double.valueOf(0.0);
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
            System.out.println("Dirty and to be saved");
            createAndSaveNewExchangeRate();
        }
        else {
            System.out.println("Not Dirty and not to be saved");
            saveExchangeRateUsedLast(exchangeRateSelected);
        }

        Intent resultIntent = new Intent();
        resultIntent.putExtra(Rc.ACTIVITY_PARAM_CURRENCY_CALCULATOR_OUT_AMOUNT, resultAmount);
        resultIntent.putExtra(Rc.ACTIVITY_PARAM_CURRENCY_CALCULATOR_OUT_VIEW_ID, resultViewId);
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    private boolean isDirtyRate() {
        return exchangeRateSelected != null &&
                exchangeRateSelected.getExchangeRate() != null &&
                !exchangeRateSelected.getExchangeRate().equals(exchangeRateInput)
                || exchangeRateSelected == null
                && exchangeRateInput > 0.0d;
    }

    /* ============ btn actions ==================== */

    private void saveExchangeRateUsedLast(ExchangeRate exchangeRateSelectedHere) {
        if (exchangeRateSelectedHere != null) {
            getApp().getExchangeRateController()
                    .persistExchangeRateUsedLast(exchangeRateSelectedHere);
        }
    }

    private void createAndSaveNewExchangeRate() {

        ExchangeRate newExchangeRate = new ExchangeRate();
        newExchangeRate.setCurrencyFrom(inputAmount.getUnit());
        newExchangeRate.setCurrencyTo(resultAmount.getUnit());
        newExchangeRate.setDescription(new StringBuilder()
                .append(getResources().getString(
                        R.string.currencyCalculatorViewDefaultDescriptionAutoSaveExchangeRate))
                .append(" ")
                .append(getApp().getExchangeRateController().getNextExchangeRateAutoSaveSeqNumber())
                .toString());
        newExchangeRate.setExchangeRate(exchangeRateInput);
        newExchangeRate.setImportOrigin(ImportOrigin.NONE);

        ExchangeRate persisted = getApp().getExchangeRateController().persistExchangeRate(newExchangeRate);

        saveExchangeRateUsedLast(persisted);
    }

    @SuppressWarnings("unused")
    public void openCurrencySelection(View view) {
        View getCurrencySelectionButton = getCurrencySelectionButton();
        getApp().getViewController().openCurrencySelectionForCalculation(this, resultCurrency,
                getCurrencySelectionButton.getId());
    }

    private Button getCurrencySelectionButton() {
        return (Button) findViewById(R.id.currencyCalculatorView_button_inputCurrencySelection);
    }

    @SuppressWarnings("unused")
    public void done(View view) {
        prepareResultAndFinish();
    }

    @SuppressWarnings("unused")
    public void cancel(View view) {
        finish();
    }

    private DecimalNumberInputUtil getDecimalNumberInputUtil() {
        return getApp().getMiscController().getDecimalNumberInputUtil();
    }
}
