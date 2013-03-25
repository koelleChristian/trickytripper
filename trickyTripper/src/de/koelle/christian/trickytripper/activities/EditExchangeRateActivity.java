package de.koelle.christian.trickytripper.activities;

import java.util.Currency;
import java.util.Locale;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import de.koelle.christian.common.options.OptionContraints;
import de.koelle.christian.common.text.BlankTextWatcher;
import de.koelle.christian.common.ui.filter.DecimalNumberInputUtil;
import de.koelle.christian.common.utils.NumberUtils;
import de.koelle.christian.common.utils.UiUtils;
import de.koelle.christian.trickytripper.R;
import de.koelle.christian.trickytripper.TrickyTripperApp;
import de.koelle.christian.trickytripper.activitysupport.CurrencySelectionResultSupport;
import de.koelle.christian.trickytripper.activitysupport.PopupFactory;
import de.koelle.christian.trickytripper.constants.Rc;
import de.koelle.christian.trickytripper.constants.Rd;
import de.koelle.christian.trickytripper.constants.ViewMode;
import de.koelle.christian.trickytripper.model.ExchangeRate;
import de.koelle.christian.trickytripper.model.ImportOrigin;
import de.koelle.christian.trickytripper.ui.utils.UiAmountViewUtils;

public class EditExchangeRateActivity extends Activity {

    private ExchangeRate exchangeRate;
    private Double exchangeRateValueInverted;
    private ViewMode viewMode;
    private BlankTextWatcher editTextListenerRight;
    private BlankTextWatcher editTextListenerLeft;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_exchange_rate_view);
        readAndSetInput(getIntent());

        initAndBindStateDependingWidget();
        initAndBindEditText();
        updateButtonState();
    }

    private void initAndBindStateDependingWidget() {
        Button buttonLeft = getCurrencySelectionButtonLeft();
        Button buttonRight = getCurrencySelectionButtonRight();
        TextView labelLeft = (TextView) findViewById(R.id.editExchangeRateViewLabelCurrencyLeft);
        TextView labelRight = (TextView) findViewById(R.id.editExchangeRateViewLabelCurrencyRight);
        Button saveButton = getSaveButton();

        boolean editMode = viewMode == ViewMode.EDIT;

        UiUtils.setViewVisibility(buttonLeft, !editMode);
        UiUtils.setViewVisibility(buttonRight, !editMode);
        UiUtils.setViewVisibility(labelLeft, editMode);
        UiUtils.setViewVisibility(labelRight, editMode);

        if (editMode) {
            setTitle(getResources().getString(R.string.editExchangeRateViewHeadingEdit));
            labelLeft.setText(exchangeRate.getCurrencyFrom().getCurrencyCode());
            labelRight.setText(exchangeRate.getCurrencyTo().getCurrencyCode());
            saveButton.setText(R.string.common_button_save);
        }
        else {
            setTitle(getResources().getString(R.string.editExchangeRateViewHeadingCreate));
            saveButton.setText(R.string.common_button_create);

            buttonLeft.setText(exchangeRate.getCurrencyFrom().getCurrencyCode());
            buttonRight.setText(exchangeRate.getCurrencyTo().getCurrencyCode());
        }
    }

    private Button getCurrencySelectionButtonRight() {
        return (Button) findViewById(R.id.editExchangeRateViewButtonCurrencyRight);
    }

    private Button getCurrencySelectionButtonLeft() {
        return (Button) findViewById(R.id.editExchangeRateViewButtonCurrencyLeft);
    }

    private Button getSaveButton() {
        return (Button) findViewById(R.id.editExchangeRateViewButtonSave);
    }

    private void readAndSetInput(Intent intent) {
        viewMode = (ViewMode) getIntent().getExtras().get(Rc.ACTIVITY_PARAM_KEY_VIEW_MODE);

        if (ViewMode.EDIT == viewMode) {
            Long technicalId = intent.getLongExtra(Rc.ACTIVITY_PARAM_EDIT_EXCHANGE_RATE_IN_RATE_TECH_ID,
                    Long.valueOf(-1L));
            this.exchangeRate = (technicalId <= 0) ? createFreshExchangeRate() : loadExchangeRate(technicalId);
        }
        else {
            this.exchangeRate = createFreshExchangeRate();
        }
        exchangeRateValueInverted = NumberUtils.invertExchangeRateDouble(exchangeRate.getExchangeRate());
    }

    private ExchangeRate loadExchangeRate(Long technicalId) {
        ExchangeRate result = getApp().getExchangeRateController().getExchangeRateById(technicalId);
        return (result == null) ? createFreshExchangeRate() : result;
    }

    private ExchangeRate createFreshExchangeRate() {
        Currency currencyTo = getApp().getTripController().getLoadedTripBaseCurrency();
        Currency currencyFrom = getApp().getMiscController().getCurrencyFavorite(currencyTo);

        ExchangeRate exchangeRate2 = new ExchangeRate();
        exchangeRate2.setImportOrigin(ImportOrigin.NONE);
        exchangeRate2.setExchangeRate(Double.valueOf(0d));
        exchangeRate2.setCurrencyFrom(currencyFrom);
        exchangeRate2.setCurrencyTo(currencyTo);
        exchangeRate2.setInversion(false);
        return exchangeRate2;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

    }

    private void initAndBindEditText() {
        EditText editTextInputDescription = (EditText) findViewById(R.id.editExchangeRateViewInputDescription);
        EditText editTextInputRateL2R = getInputWidgetL2R();
        EditText editTextInputRateR2L = getInputWidgetR2L();

        UiUtils.makeProperNumberInput(editTextInputRateL2R, getDecimalNumberInputUtil()
                .getExchangeRateInputPatternMatcher());
        UiUtils.makeProperNumberInput(editTextInputRateR2L, getDecimalNumberInputUtil()
                .getExchangeRateInputPatternMatcher());

        editTextInputDescription.setText(exchangeRate.getDescription());

        editTextInputDescription.addTextChangedListener(new BlankTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                exchangeRate.setDescription(s.toString());
                updateButtonState();
            }

        });

        editTextListenerLeft = new BlankTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                String widgetInput = getDecimalNumberInputUtil().fixInputStringWidgetToParser(s.toString());
                Double valueInput = NumberUtils.getStringToDoubleUnrounded(getLocale(), widgetInput);
                exchangeRate.setExchangeRate(valueInput);
                recalculateAndUpdateOtherSide(true);
                updateButtonState();
            }
        };
        editTextListenerRight = new BlankTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                String widgetInput = getDecimalNumberInputUtil().fixInputStringWidgetToParser(s.toString());
                Double valueInput = NumberUtils.getStringToDoubleUnrounded(getLocale(), widgetInput);
                exchangeRateValueInverted = valueInput;
                recalculateAndUpdateOtherSide(false);
                updateButtonState();
            }
        };

        editTextInputRateL2R.addTextChangedListener(editTextListenerLeft);
        editTextInputRateR2L.addTextChangedListener(editTextListenerRight);

        updatInputWidget(editTextInputRateL2R, getLocale(),
                exchangeRate.getExchangeRate(), editTextListenerLeft);
        updatInputWidget(editTextInputRateR2L, getLocale(),
                exchangeRateValueInverted, editTextListenerRight);
    }

    /* ============== Menu Shit [BGN] ============== */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return getApp().getMiscController().getOptionSupport().populateOptionsMenu(
                new OptionContraints().activity(this).menu(menu)
                        .options(new int[] {
                                R.id.option_help
                        }));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.option_help:
            showDialog(Rd.DIALOG_HELP);
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    /* ============== Menu Shit [END] ============== */
    /* ============== Dialog Shit [END] ============== */
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

    /* ============== Dialog Shit [END] ============== */
    protected void recalculateAndUpdateOtherSide(boolean leftToRight) {
        if (leftToRight) {
            exchangeRateValueInverted = NumberUtils.invertExchangeRateDouble(exchangeRate.getExchangeRate());
            updatInputWidget(getInputWidgetR2L(), getLocale(), exchangeRateValueInverted, editTextListenerRight);
        }
        else {
            exchangeRate.setExchangeRate(NumberUtils.invertExchangeRateDouble(exchangeRateValueInverted));
            updatInputWidget(getInputWidgetL2R(), getLocale(), exchangeRate.getExchangeRate(), editTextListenerLeft);
        }
    }

    private EditText getInputWidgetR2L() {
        return (EditText) findViewById(R.id.editExchangeRateViewInputRateR2L);
    }

    private EditText getInputWidgetL2R() {
        return (EditText) findViewById(R.id.editExchangeRateViewInputRateL2R);
    }

    private void updatInputWidget(EditText editTextField, Locale locale, Double value,
            TextWatcher watcher) {
        editTextField.removeTextChangedListener(watcher);
        UiAmountViewUtils.writeDoubleToEditText(value, editTextField, locale, getDecimalNumberInputUtil());
        editTextField.addTextChangedListener(watcher);
    }

    protected void updateButtonState() {
        getSaveButton().setEnabled(canBeSaved());
    }

    private boolean canBeSaved() {
        return exchangeRate.getExchangeRate() != null
                && exchangeRate.getExchangeRate() > 0
                && exchangeRate.getDescription() != null
                && exchangeRate.getDescription().length() > 0
                && exchangeRate.getCurrencyFrom() != null
                && exchangeRate.getCurrencyTo() != null
                && !exchangeRate.getCurrencyFrom().equals(exchangeRate.getCurrencyTo());
    }

    private Locale getLocale() {
        Locale locale = getResources().getConfiguration().locale;
        return locale;
    }

    private TrickyTripperApp getApp() {
        return (TrickyTripperApp) getApplication();
    }

    /* ============ btn actions ==================== */

    @SuppressWarnings("unused")
    public void openCurrencySelectionLeft(View view) {
        View button = getCurrencySelectionButtonLeft();
        getApp().getViewController()
                .openCurrencySelectionForNewExchangeRate(this, exchangeRate.getCurrencyTo(), button.getId(), true);
    }

    @SuppressWarnings("unused")
    public void openCurrencySelectionRight(View view) {
        View button = getCurrencySelectionButtonRight();
        getApp().getViewController()
                .openCurrencySelectionForNewExchangeRate(this, exchangeRate.getCurrencyTo(), button.getId(), false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Currency result = CurrencySelectionResultSupport.onActivityResult(requestCode, resultCode, data, this);
        if (result != null) {
            boolean isLeftNotRight = data.getBooleanExtra(Rc.ACTIVITY_PARAM_CURRENCY_SELECTION_OUT_WAS_LEFT_NOT_RIGHT,
                    true);
            if (isLeftNotRight) {
                exchangeRate.setCurrencyFrom(result);
            }
            else {
                exchangeRate.setCurrencyTo(result);
            }
            updateButtonState();
        }
    }

    @SuppressWarnings("unused")
    public void done(View view) {
        if (getApp().getExchangeRateController().doesExchangeRateAlreadyExist(exchangeRate)) {
            Toast.makeText(getApplicationContext(), R.string.editExchangeRateViewMsgSaveDenialDuplicate,
                    Toast.LENGTH_SHORT).show();
        }
        else {
            getApp().getExchangeRateController().persistExchangeRate(exchangeRate);
            finish();
        }
    }

    @SuppressWarnings("unused")
    public void cancel(View view) {
        finish();
    }

    private DecimalNumberInputUtil getDecimalNumberInputUtil() {
        return getApp().getMiscController().getDecimalNumberInputUtil();
    }

}
