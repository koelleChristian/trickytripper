package de.koelle.christian.trickytripper.constants;

import java.text.Collator;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class Rc {

    /**
     * Log-tags.
     */
    public static final String LT = "TT";
    public static final String LT_INPUT = "TT_INPUT";
    public static final String LT_DB = "TT_DB";
    public static final String LT_IO = "TT_IO";
    public static final String LT_PROV = "TT_PROV";

    /*
     * So that the compiler can remove the statements, instead of
     * Log.isLoggable(Rc.LT, Log.DEBUG)
     */
    public static final boolean debugOn = false;

    public static final String TAB_SPEC_ID_PAYMENT = "payment";
    public static final String TAB_SPEC_ID_PARTICIPANTS = "participants";
    public static final String TAB_SPEC_ID_REPORT = "report";

    public static final String ACTIVITY_PARAM_KEY_PARTICIPANT = "participant";
    public static final String ACTIVITY_PARAM_KEY_PARTICIPANT_ID = "activityParamParticipantId";
    public static final String ACTIVITY_PARAM_KEY_PAYMENT_ID = "activityParamPaymentId";
    public static final String ACTIVITY_PARAM_KEY_VIEW_MODE = "viewMode";

    public static final String ACTIVITY_PARAM_VIEW_MODE_EDIT_MODE = "edit";
    public static final String ACTIVITY_PARAM_VIEW_MODE_CREATE_MODE = "create";

    public static final String ACTIVITY_PARAM_EDIT_EXCHANGE_RATE_IN_RATE_TECH_ID = "activityParamExchangeRateInRateTechId";
    public static final String ACTIVITY_PARAM_EDIT_EXCHANGE_RATE_IN_SOURCE_CURRENCY = "activityParamExchangeRateInScourceCurrrency";

    public static final String ACTIVITY_PARAM_CURRENCY_CALCULATOR_IN_VALUE = "activityParamCurrencyCalcInValue";
    public static final String ACTIVITY_PARAM_CURRENCY_CALCULATOR_IN_RESULT_CURRENCY = "activityParamCurrencyCalcInResultCurrency";
    public static final String ACTIVITY_PARAM_CURRENCY_CALCULATOR_IN_RESULT_VIEW_ID = "activityParamCurrencyCalcInResultViewId";
    public static final String ACTIVITY_PARAM_CURRENCY_CALCULATOR_OUT_VIEW_ID = "activityParamCurrencyCalcOutViewId";
    public static final String ACTIVITY_PARAM_CURRENCY_CALCULATOR_OUT_AMOUNT = "activityParamCurrencyCalcOutAmount";

    public static final String ACTIVITY_PARAM_CURRENCY_SELECTION_IN_MODE = "activityParamCurrencySelectionInMode";
    public static final String ACTIVITY_PARAM_CURRENCY_SELECTION_IN_CURRENCY = "activityParamCurrencySelectionInCurrency";
    public static final String ACTIVITY_PARAM_CURRENCY_SELECTION_IN_VIEW_ID = "activityParamCurrencySelectionInViewId";
    public static final String ACTIVITY_PARAM_CURRENCY_SELECTION_OUT_VIEW_ID = "activityParamCurrencySelectionOutViewId";
    public static final String ACTIVITY_PARAM_CURRENCY_SELECTION_OUT_CURRENCY = "activityParamCurrencySelectionOutCurrency";
    public static final String ACTIVITY_PARAM_CURRENCY_SELECTION_OUT_WAS_LEFT_NOT_RIGHT = "activityParamCurrencySelectionOutWasLeftNotRight";

    public static final int ACTIVITY_PARAM_CURRENCY_CALCULATOR_REQUEST_CODE = 532147439;
    public static final int ACTIVITY_PARAM_CURRENCY_SELECTION_REQUEST_CODE = 622147448;
    /* TODO(ckoelle) I think this is not for result anymore. */
    public static final int ACTIVITY_PARAM_EXCHANGE_RATE_MANAGEMENT_CODE = 49494949;

    public static final String ACTIVITY_PARAM_DELETE_EXCHANGE_RATES_IN_CURRENCY_LIST = "activityParamImportExchangeRatesInCurrencyList";

    public static final String ACTIVITY_PARAM_IMPORT_EXCHANGE_RATES_IN_CURRENCY_LIST = "activityParamImportExchangeRatesInCurrencyList";

    public static final int DEFAULT_COLLATOR_STRENGTH = Collator.TERTIARY;

    public static boolean USE_CACHE_DIR_NOT_FILE_DIR_FOR_REPORTS = true;

    public static final String LINE_FEED = "\n";

    public static final String HTML_EXTENSION = ".html";
    public static final String CSV_EXTENSION = ".csv";
    public static final String TXT_EXTENSION = ".txt";

    public static final String STREAM_SENDING_MIME = "*/*";
    public static final String STREAM_SENDING_INTENT = Intent.ACTION_SEND_MULTIPLE;

    public static final String PREFS_NAME_ID = "PREFS_NAME_ID";
    public static final int PREFS_MODE = Context.MODE_PRIVATE;

    public static final String PREFS_VALUE_ID_BASE_CURRENCY = "PREFS_VALUE_ID_BASE_CURRENCY";

}
