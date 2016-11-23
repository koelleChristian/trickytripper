package de.koelle.christian.trickytripper.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Currency;
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
import de.koelle.christian.trickytripper.constants.Rc;
import de.koelle.christian.trickytripper.constants.ViewMode;
import de.koelle.christian.trickytripper.model.ExchangeRate;
import de.koelle.christian.trickytripper.model.ImportOrigin;
import de.koelle.christian.trickytripper.ui.utils.UiAmountViewUtils;

public class ExchangeRateEditActivity extends AppCompatActivity {

    private ExchangeRate exchangeRate;
    private Double exchangeRateValueInverted;
    private ViewMode viewMode;
    private BlankTextWatcher editTextListenerRight;
    private BlankTextWatcher editTextListenerLeft;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.exchange_rate_edit_view);
        boolean sourceCurrencyForCreation = readAndSetInput(getIntent());

        initAndBindStateDependingWidget(sourceCurrencyForCreation);
        initAndBindEditText();
        supportInvalidateOptionsMenu();

        ActionBarSupport.addBackButton(this);
    }

    private void initAndBindStateDependingWidget(boolean sourceCurrencyForCreation) {
        Button buttonLeft = getCurrencySelectionButtonLeft();
        Button buttonRight = getCurrencySelectionButtonRight();
        TextView labelLeft = (TextView) findViewById(R.id.editExchangeRateViewLabelCurrencyLeft);
        TextView labelRight = (TextView) findViewById(R.id.editExchangeRateViewLabelCurrencyRight);

        boolean editMode = viewMode == ViewMode.EDIT;
        boolean form2BeEditable = !(editMode || sourceCurrencyForCreation);

        int titleId = (editMode)?
                R.string.exchangeRateEditViewHeadingEdit :
                R.string.exchangeRateEditViewHeadingCreate;
        setTitle(getResources().getString(titleId));


        UiUtils.setViewVisibility(buttonLeft, form2BeEditable);
        UiUtils.setViewVisibility(buttonRight, form2BeEditable);
        UiUtils.setViewVisibility(labelLeft, !form2BeEditable);
        UiUtils.setViewVisibility(labelRight, !form2BeEditable);

        if (form2BeEditable) {
            buttonLeft.setText(exchangeRate.getCurrencyFrom().getCurrencyCode());
            buttonRight.setText(exchangeRate.getCurrencyTo().getCurrencyCode());
        } else {
            labelLeft.setText(exchangeRate.getCurrencyFrom().getCurrencyCode());
            labelRight.setText(exchangeRate.getCurrencyTo().getCurrencyCode());
        }
    }

    private Button getCurrencySelectionButtonRight() {
        return (Button) findViewById(R.id.editExchangeRateViewButtonCurrencyRight);
    }

    private Button getCurrencySelectionButtonLeft() {
        return (Button) findViewById(R.id.editExchangeRateViewButtonCurrencyLeft);
    }


    private boolean readAndSetInput(Intent intent) {
        Currency sourceCurrencyForCreation = null;
        viewMode = (ViewMode) getIntent().getExtras().get(
                Rc.ACTIVITY_PARAM_KEY_VIEW_MODE);

        if (ViewMode.EDIT == viewMode) {
            Long technicalId = intent.getLongExtra(
                    Rc.ACTIVITY_PARAM_EDIT_EXCHANGE_RATE_IN_RATE_TECH_ID,
                    Long.valueOf(-1L));
            this.exchangeRate = loadExchangeRate(technicalId);
        } else {
            sourceCurrencyForCreation = (Currency) intent
                    .getSerializableExtra(Rc.ACTIVITY_PARAM_EDIT_EXCHANGE_RATE_IN_SOURCE_CURRENCY);
            this.exchangeRate = createFreshExchangeRate(sourceCurrencyForCreation);
        }
        exchangeRateValueInverted = NumberUtils
                .invertExchangeRateDouble(exchangeRate.getExchangeRate());

        return sourceCurrencyForCreation != null;
    }

    private ExchangeRate loadExchangeRate(Long technicalId) {
        ExchangeRate result = getApp().getExchangeRateController()
                .getExchangeRateById(technicalId);
        return (result == null) ? createFreshExchangeRate(null) : result;
    }

    private ExchangeRate createFreshExchangeRate(Currency sourceCurrencyProvided) {
        Currency currencyTo = getApp().getTripController()
                .getLoadedTripBaseCurrency();
        Currency currencyFrom = (sourceCurrencyProvided == null) ?
                getApp().getMiscController().getCurrencyFavorite(currencyTo) :
                sourceCurrencyProvided;

        ExchangeRate exchangeRate2 = new ExchangeRate();
        exchangeRate2.setImportOrigin(ImportOrigin.NONE);
        exchangeRate2.setExchangeRate(Double.valueOf(0d));
        exchangeRate2.setCurrencyFrom(currencyFrom);
        exchangeRate2.setCurrencyTo(currencyTo);
        exchangeRate2.setInversion(false);
        return exchangeRate2;
    }


    private void initAndBindEditText() {
        EditText editTextInputDescription = (EditText) findViewById(R.id.editExchangeRateViewInputDescription);
        EditText editTextInputRateL2R = getInputWidgetL2R();
        EditText editTextInputRateR2L = getInputWidgetR2L();

        UiUtils.makeProperNumberInput(editTextInputRateL2R,
                getDecimalNumberInputUtil()
                        .getExchangeRateInputPatternMatcher());
        UiUtils.makeProperNumberInput(editTextInputRateR2L,
                getDecimalNumberInputUtil()
                        .getExchangeRateInputPatternMatcher());

        editTextInputDescription.setText(exchangeRate.getDescription());

        editTextInputDescription.addTextChangedListener(new BlankTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                exchangeRate.setDescription(s.toString());
                supportInvalidateOptionsMenu();
            }

        });

        editTextListenerLeft = new BlankTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                Double valueInput = getWidgetDoubleInput(s);
                exchangeRate.setExchangeRate(valueInput);
                recalculateAndUpdateOtherSide(true);
                supportInvalidateOptionsMenu();
            }
        };
        editTextListenerRight = new BlankTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                Double valueInput = getWidgetDoubleInput(s);
                exchangeRateValueInverted = valueInput;
                recalculateAndUpdateOtherSide(false);
                supportInvalidateOptionsMenu();
            }

        };

        editTextInputRateL2R.addTextChangedListener(editTextListenerLeft);
        editTextInputRateR2L.addTextChangedListener(editTextListenerRight);

        updateInputWidget(editTextInputRateL2R, getLocale(),
                exchangeRate.getExchangeRate(), editTextListenerLeft);
        updateInputWidget(editTextInputRateR2L, getLocale(),
                exchangeRateValueInverted, editTextListenerRight);
    }

    private Double getWidgetDoubleInput(Editable s) {
        String widgetInput = getDecimalNumberInputUtil()
                .fixInputStringWidgetToParser(s.toString());
        Double valueInput = NumberUtils.getStringToDoubleNonRounded(
                getLocale(), widgetInput);
        return valueInput;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        int[] optionIds;

        if (viewMode == ViewMode.EDIT) {
            optionIds = new int[]{
                    R.id.option_save_edit,
                    R.id.option_help
            };
        } else {
            optionIds = new int[]{
                    R.id.option_save_create,
                    R.id.option_help
            };
        }
        return getApp()
                .getMiscController()
                .getOptionSupport()
                .populateOptionsMenu(
                        new OptionConstraintsInflater()
                                .activity(getMenuInflater()).menu(menu)
                                .options(optionIds));
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        boolean saveEnabled = canBeSaved();
        int itemId = (ViewMode.CREATE.equals(viewMode)) ? R.id.option_save_create : R.id.option_save_edit;
        MenuItem item = menu.findItem(itemId);
        item.setEnabled(saveEnabled);
        item.getIcon().setAlpha((saveEnabled) ? 255 : 64);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.option_save_edit:
                done();
                return true;
            case R.id.option_save_create:
                done();
                return true;
            case R.id.option_help:
                getApp().getViewController().openHelp(getSupportFragmentManager());
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /* ============== Menu Shit [END] ============== */

    protected void recalculateAndUpdateOtherSide(boolean leftToRight) {
        if (leftToRight) {
            exchangeRateValueInverted = NumberUtils
                    .invertExchangeRateDouble(exchangeRate.getExchangeRate());
            updateInputWidget(getInputWidgetR2L(), getLocale(),
                    exchangeRateValueInverted, editTextListenerRight);
        } else {
            exchangeRate.setExchangeRate(NumberUtils
                    .invertExchangeRateDouble(exchangeRateValueInverted));
            updateInputWidget(getInputWidgetL2R(), getLocale(),
                    exchangeRate.getExchangeRate(), editTextListenerLeft);
        }
    }

    private EditText getInputWidgetR2L() {
        return (EditText) findViewById(R.id.editExchangeRateViewInputRateR2L);
    }

    private EditText getInputWidgetL2R() {
        return (EditText) findViewById(R.id.editExchangeRateViewInputRateL2R);
    }

    private void updateInputWidget(EditText editTextField, Locale locale,
                                   Double value,
                                   TextWatcher watcher) {
        editTextField.removeTextChangedListener(watcher);
        UiAmountViewUtils.writeDoubleToEditText(value, editTextField, locale,
                getDecimalNumberInputUtil());
        editTextField.addTextChangedListener(watcher);
    }

    private boolean canBeSaved() {
        return exchangeRate.getExchangeRate() != null
                && exchangeRate.getExchangeRate() > 0
                && exchangeRate.getDescription() != null
                && exchangeRate.getDescription().length() > 0
                && exchangeRate.getCurrencyFrom() != null
                && exchangeRate.getCurrencyTo() != null
                && !exchangeRate.getCurrencyFrom().equals(
                exchangeRate.getCurrencyTo());
    }

    private Locale getLocale() {
        Locale locale = getResources().getConfiguration().locale;
        return locale;
    }

    private TrickyTripperApp getApp() {
        return (TrickyTripperApp) getApplication();
    }

    /* ============ btn actions ==================== */

    public void openCurrencySelectionLeft(View view) {
        View button = getCurrencySelectionButtonLeft();
        getApp().getViewController()
                .openCurrencySelectionForNewExchangeRate(this,
                        exchangeRate.getCurrencyTo(), button.getId(), true);
    }

    public void openCurrencySelectionRight(View view) {
        View button = getCurrencySelectionButtonRight();
        getApp().getViewController()
                .openCurrencySelectionForNewExchangeRate(this,
                        exchangeRate.getCurrencyTo(), button.getId(), false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Currency result = CurrencySelectionResultSupport.onActivityResult(
                requestCode, resultCode, data, this);
        if (result != null) {
            boolean isLeftNotRight = data
                    .getBooleanExtra(
                            Rc.ACTIVITY_PARAM_CURRENCY_SELECTION_OUT_WAS_LEFT_NOT_RIGHT,
                            true);
            if (isLeftNotRight) {
                exchangeRate.setCurrencyFrom(result);
            } else {
                exchangeRate.setCurrencyTo(result);
            }
            supportInvalidateOptionsMenu();
        }
    }

    public void done() {
        if (getApp().getExchangeRateController().doesExchangeRateAlreadyExist(
                exchangeRate)) {
            Toast.makeText(getApplicationContext(),
                    R.string.exchangeRateEditViewMsgSaveDenialDuplicate,
                    Toast.LENGTH_SHORT).show();
        } else {
            String inputLeft = getDecimalNumberInputUtil().fixInputStringWidgetToParser(
                    getInputWidgetL2R().getText().toString());
            String inputRight = getDecimalNumberInputUtil().fixInputStringWidgetToParser(
                    getInputWidgetR2L().getText().toString());
            if (inputRight.length() < inputLeft.length()) {
                flipRatesToAvoidRoundingConfusion(exchangeRate, inputRight);
            }

            getApp().getExchangeRateController().persistExchangeRate(
                    exchangeRate);
            finish();
        }
    }

    private void flipRatesToAvoidRoundingConfusion(ExchangeRate exchangeRate2, String inputRight) {
        Currency interim = exchangeRate2.getCurrencyFrom();
        exchangeRate2.setCurrencyFrom(exchangeRate2.getCurrencyTo());
        exchangeRate2.setCurrencyTo(interim);
        Double valueInput = NumberUtils.getStringToDoubleNonRounded(getLocale(), inputRight);
        exchangeRate2.setExchangeRate(valueInput);

    }

    private DecimalNumberInputUtil getDecimalNumberInputUtil() {
        return getApp().getMiscController().getDecimalNumberInputUtil();
    }

}
