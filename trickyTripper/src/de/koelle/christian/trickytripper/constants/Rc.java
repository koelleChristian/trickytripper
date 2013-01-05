package de.koelle.christian.trickytripper.constants;

import java.text.Collator;

import android.content.Context;
import android.content.Intent;

public class Rc {

    /**
     * Log-tags.
     */
    public static final String LT = "TT";
    public static final String LT_INPUT = "TT_INPUT";
    public static final String LT_DB = "TT_DB";
    public static final String LT_IO = "TT_IO";
    public static final String LT_PROV = "TT_PROV";

    public static final String TAB_SPEC_ID_PAYMENT = "payment";
    public static final String TAB_SPEC_ID_PARTICIPANTS = "participants";
    public static final String TAB_SPEC_ID_REPORT = "report";

    public static final String ACTIVITY_PARAM_KEY_PARTICIPANT = "participant";
    public static final String ACTIVITY_PARAM_KEY_PARTICIPANT_ID = "activityParamParticipantId";
    public static final String ACTIVITY_PARAM_KEY_PAYMENT_ID = "activityParamPaymentId";
    public static final String ACTIVITY_PARAM_KEY_VIEW_MODE = "viewMode";

    public static final String ACTIVITY_PARAM_VIEW_MODE_EDIT_MODE = "edit";
    public static final String ACTIVITY_PARAM_VIEW_MODE_CREATE_MODE = "create";

    public static final String ACTIVITY_PARAM_CURRENCY_CALCULATOR_IN_VALUE = "activityParamCurrencyCalcInValue";
    public static final String ACTIVITY_PARAM_CURRENCY_CALCULATOR_IN_RESULT_CURRENCY = "activityParamCurrencyCalcInResultCurrency";
    public static final String ACTIVITY_PARAM_CURRENCY_CALCULATOR_IN_RESULT_VIEW_ID = "activityParamCurrencyCalcInResultViewId";
    public static final String ACTIVITY_PARAM_CURRENCY_CALCULATOR_OUT_VIEW_ID = "activityParamCurrencyCalcOutViewId";
    public static final String ACTIVITY_PARAM_CURRENCY_CALCULATOR_OUT_AMOUNT = "activityParamCurrencyCalcOutAmount";
    public static final int ACTIVITY_PARAM_CURRENCY_CALCULATOR_REQUEST_CODE = 532147439;

    public static final String ACTIVITY_PARAM_IMPORT_EXCHANGE_RATES_IN_CURRENCY_LIST = "activityParamImportExchangeRatesInCurrencyList";

    public static final int DIALOG_SHOW_HELP = 100;

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
    public static final String PREFS_VALUE_ID_ENABLE_SMART_HELP = "PREFS_VALUE_ID_ENABLE_SMART_HELP";

}
