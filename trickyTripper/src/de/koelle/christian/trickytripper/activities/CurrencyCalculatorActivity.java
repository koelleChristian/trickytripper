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
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import de.koelle.christian.common.text.BlankTextWatcher;
import de.koelle.christian.common.utils.CurrencyUtil;
import de.koelle.christian.common.utils.NumberUtils;
import de.koelle.christian.common.utils.UiUtils;
import de.koelle.christian.trickytripper.R;
import de.koelle.christian.trickytripper.TrickyTripperApp;
import de.koelle.christian.trickytripper.activitysupport.ImportOptionSupport;
import de.koelle.christian.trickytripper.activitysupport.PopupFactory;
import de.koelle.christian.trickytripper.activitysupport.SpinnerViewSupport;
import de.koelle.christian.trickytripper.constants.Rc;
import de.koelle.christian.trickytripper.model.Amount;
import de.koelle.christian.trickytripper.model.ExchangeRate;
import de.koelle.christian.trickytripper.model.ExchangeRateResult;
import de.koelle.christian.trickytripper.modelutils.AmountViewUtils;
import de.koelle.christian.trickytripper.ui.model.RowObject;
import de.koelle.christian.trickytripper.ui.model.RowObjectCallback;
import de.koelle.christian.trickytripper.ui.utils.UiViewUtils;

public class CurrencyCalculatorActivity extends Activity {

    private Amount inputAmount;
    private Amount resultAmount;
    private int resultViewId;
    private Double exchangeRateInput = Double.valueOf(1.0);
    private ExchangeRate exchangeRateSelected;
    private ImportOptionSupport importOptionSupport;

    /* ============== Menu Shit [BGN] ============== */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.layout.general_options_plus_import, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.general_options_help:
            showDialog(Rc.DIALOG_SHOW_HELP);
            return true;
        case R.id.option_import_exchange_rates:
            return importOptionSupport.onOptionsItemSelected();
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected Dialog onCreateDialog(int id, Bundle args) {
        Dialog dialog;
        switch (id) {
        case Rc.DIALOG_SHOW_HELP:
            dialog = PopupFactory.createHelpDialog(this, getApp(), Rc.DIALOG_SHOW_HELP);
            break;
        default:
            dialog = null;
        }

        return dialog;
    }

    @Override
    protected void onPrepareDialog(int id, Dialog dialog, Bundle args) {
        switch (id) {
        case Rc.DIALOG_SHOW_HELP:
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

        setContentView(R.layout.currency_calculator_view);

        this.importOptionSupport = new ImportOptionSupport(getApp());

        readAndSetInput(getIntent());

        Currency sourceCurrencyUsedLast = getApp().getSourceCurrencyUsedLast();
        initCurrencySpinner(resultAmount.getUnit(), sourceCurrencyUsedLast);
        inputAmount.setUnit(sourceCurrencyUsedLast);

        initAndBindEditText();

        Currency sourceCurrency = inputAmount.getUnit();
        Currency resultCurrency = resultAmount.getUnit();

        loadAndInitExchangeRates(sourceCurrency, resultCurrency);

        updateCalculation();

    }

    private void loadAndInitExchangeRates(Currency sourceCurrency, Currency resultCurrency) {
        ExchangeRateResult rates = getApp().findSuitableRates(sourceCurrency, resultCurrency);
        initExchangeRateSpinner(rates);
    }

    private void readAndSetInput(Intent intent) {
        Double inputValue = intent.getDoubleExtra(Rc.ACTIVITY_PARAM_CURRENCY_CALCULATOR_IN_VALUE,
                Double.valueOf(0.0d));
        inputAmount = createAmount(inputValue, null);

        Bundle extras = getIntent().getExtras();

        Currency resultCurrencySubmitted = (Currency) extras.get(
                Rc.ACTIVITY_PARAM_CURRENCY_CALCULATOR_IN_RESULT_CURRENCY);

        Currency resultCurrency = (resultCurrencySubmitted != null) ? resultCurrencySubmitted : getApp()
                .getTripLoaded().getBaseCurrency();

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

        Locale locale = getLocale();

        UiUtils.makeProperNumberInput(editTextInputValue, locale);
        UiUtils.makeProperNumberInput(editTextInputExchangeRate, locale);

        updateEditFieldsFromModel();

        editTextInputValue.addTextChangedListener(new BlankTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                Double valueInput = NumberUtils.getStringToDouble(getLocale(), s.toString());
                inputAmount.setValue(valueInput);
                updateCalculation();
            }
        });

        editTextInputExchangeRate.addTextChangedListener(new BlankTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                Double valueInput = NumberUtils.getStringToDouble(getLocale(), s.toString());
                exchangeRateInput = valueInput;
                updateCalculation();
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

    protected void updateCalculation() {
        resultAmount.setValue(NumberUtils.multiply(inputAmount.getValue(), exchangeRateInput));
        updateOutputFields();
    }

    private Locale getLocale() {
        Locale locale = getResources().getConfiguration().locale;
        return locale;
    }

    @SuppressWarnings("rawtypes")
    private void initCurrencySpinner(Currency currencySourceExclusion, Currency currencySelectedLastTime) {
        final Spinner spinner = (Spinner) findViewById(R.id.currencyCalculatorView_spinner_inputCurrencySelection);
        final List<RowObject> spinnerObjects = wrapCurrenciesInRowObject(
                CurrencyUtil.getSuportedCurrencies(getResources()),
                currencySourceExclusion);

        ArrayAdapter<RowObject> adapter = new ArrayAdapter<RowObject>(this,
                android.R.layout.simple_spinner_item,
                spinnerObjects) {

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                /* This is the default for the list view. */
                return super.getDropDownView(position, convertView, parent);
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                /* Display currency code only when not in list view. */
                TextView result = (TextView) super.getView(position, convertView, parent);
                result.setText(((Currency) spinnerObjects.get(position).getRowObject()).getCurrencyCode());
                return result;
            }
        };

        adapter.setDropDownViewResource(R.layout.selection_list_medium);
        spinner.setPromptId(R.string.payment_view_spinner_prompt);
        spinner.setAdapter(adapter);

        Currency initialSelection2Be = (currencySelectedLastTime == null) ? (Currency) spinnerObjects.get(0)
                .getRowObject() : currencySelectedLastTime;

        SpinnerViewSupport.setSelection(spinner, initialSelection2Be, adapter);

        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

            @SuppressWarnings("unchecked")
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (position >= 0) {
                    Object o = spinner.getSelectedItem();
                    Currency selectedCurrency = ((RowObject<Currency>) o).getRowObject();
                    if (inputAmount.getUnit() == null || !inputAmount.getUnit().equals(selectedCurrency)) {
                        inputAmount.setUnit(selectedCurrency);
                        loadAndInitExchangeRates(inputAmount.getUnit(), resultAmount.getUnit());
                        updateCalculation();
                    }
                }
            }

            public void onNothingSelected(AdapterView<?> parentView) {
                // intentionally blank
            }

        });
    }

    private void initExchangeRateSpinner(ExchangeRateResult rates) {
        final Spinner spinner = (Spinner) findViewById(R.id.currencyCalculatorView_spinner_exchangeRateSelection);
        List<RowObject> spinnerObjects = wrapExchangeRatesInRowObject(rates.getMatchingExchangeRates());

        ArrayAdapter<RowObject> adapter = new ArrayAdapter<RowObject>(this,
                android.R.layout.simple_spinner_item,
                spinnerObjects);

        adapter.setDropDownViewResource(R.layout.selection_list_medium);
        spinner.setPromptId(R.string.payment_view_spinner_prompt);
        spinner.setAdapter(adapter);

        ExchangeRate initialSelection2Be = (rates.getRateUsedLastTime() == null) ? (ExchangeRate) spinnerObjects.get(0)
                .getRowObject() : rates.getRateUsedLastTime();
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
                        updateCalculation(); // xx
                    }
                }
            }

            public void onNothingSelected(AdapterView<?> parentView) {
                // intentionally blank
            }

        });
    }

    private void fillRateModel(ExchangeRate exchangeRateToBeSet) {
        exchangeRateSelected = exchangeRateToBeSet;
        exchangeRateInput = exchangeRateToBeSet.getExchangeRate();
    }

    @SuppressWarnings("rawtypes")
    private List<RowObject> wrapCurrenciesInRowObject(List<Currency> supportedCurrencies, Currency exclusion) {
        List<RowObject> result = new ArrayList<RowObject>();

        for (final Currency c : supportedCurrencies) {
            if (notExcluded(exclusion, c)) {
                result.add(new RowObject<Currency>(new RowObjectCallback<Currency>() {
                    public String getStringToDisplay(Currency c) {
                        /*
                         * This is the long description intended for the list
                         * view.
                         */
                        return CurrencyUtil.getFullNameToCurrency(getResources(), c);
                    }
                }, c));
            }
        }
        return result;
    }

    @SuppressWarnings("rawtypes")
    private List<RowObject> wrapExchangeRatesInRowObject(List<ExchangeRate> values) {
        List<RowObject> result = new ArrayList<RowObject>();

        for (final ExchangeRate value : values) {
            result.add(new RowObject<ExchangeRate>(new RowObjectCallback<ExchangeRate>() {
                public String getStringToDisplay(ExchangeRate c) {
                    return c.getDescription() + " " + c.getExchangeRate();
                }
            }, value));
        }
        return result;
    }

    private boolean notExcluded(Currency exclusion, final Currency c) {
        return exclusion == null || !exclusion.equals(c);
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
        // TODO(ckoelle) save exchangeRate use last
        Intent resultIntent = new Intent();
        resultIntent.putExtra(Rc.ACTIVITY_PARAM_CURRENCY_CALCULATOR_OUT_AMOUNT, resultAmount);
        resultIntent.putExtra(Rc.ACTIVITY_PARAM_CURRENCY_CALCULATOR_OUT_VIEW_ID, resultViewId);
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    /* ============ btn actions ==================== */

    public void done(View view) {
        prepareResultAndFinish();
    }

    public void cancel(View view) {
        finish();
    }

}
