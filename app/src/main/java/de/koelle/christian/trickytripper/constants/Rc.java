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

    public static final int TAB_ID_PARTICIPANTS = 0;
    public static final int TAB_ID_PAYMENTS = 1;
    public static final int TAB_ID_REPORT = 2;

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
    public static final String ACTIVITY_PARAM_EDIT_EXCHANGE_RATE_IN_SOURCE_CURRENCY = "activityParamExchangeRateInSourceCurrency";

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

    public static final String ACTIVITY_PARAM_PARTICIPANT_SEL_IN_PARTICIPANTS_IN_USE = "dialogParamMapInUse";
    public static final String ACTIVITY_PARAM_PARTICIPANT_SEL_IN_ALL_RELEVANT_PARTICIPANTS = "actParamParticipantSelInAllRelevantParticipants";
    public static final String ACTIVITY_PARAM_PARTICIPANT_SEL_IN_TOTAL_PAYMENT_AMOUNT = "dialogParamTotalPaymentAmount";
    public static final String ACTIVITY_PARAM_PARTICIPANT_SEL_IN_IS_PAYMENT = "dialogParamIsPayment";

    public static final String ACTIVITY_PARAM_PARTICIPANT_SEL_OUT_SELECTED_PARTICIPANTS = "actParamParticipantSelOutSelParticipants";
    public static final String ACTIVITY_PARAM_PARTICIPANT_SEL_OUT_DIVIDE_AMOUNT = "actParamParticipantSelOutDivideAmount";
    public static final String ACTIVITY_PARAM_PARTICIPANT_SEL_OUT_IS_PAYMENT = "actParamParticipantSelOutIsPayment";

    // Maximum 16 bits, i.e. 65535
    public static final int ACTIVITY_REQ_CODE_CURRENCY_CALCULATOR = 53214;
    public static final int ACTIVITY_REQ_CODE_CURRENCY_SELECTION = 62214;
    public static final int ACTIVITY_REQ_CODE_PARTICIPANT_SELECT = 3452;
    /* TODO(ckoelle) I think this is not for result anymore. */
    public static final int ACTIVITY_REQ_CODE_EXCHANGE_RATE_MANAGEMENT = 49499;
    public static final int ACTIVITY_REQ_CODE_EDIT_TRIP = 36214;


    public static final String ACTIVITY_PARAM_DELETE_EXCHANGE_RATES_IN_CURRENCY_LIST = "activityParamImportExchangeRatesInCurrencyList";

    public static final String ACTIVITY_PARAM_IMPORT_EXCHANGE_RATES_IN_CURRENCY_LIST = "activityParamImportExchangeRatesInCurrencyList";

    public static final String ACTIVITY_PARAM_PARTICIPANT_EDIT_IN_PARTICIPANT = "activityParamParticipantEditInParticipant";

    public static final String ACTIVITY_PARAM_TRIP_EDIT_IN_TRIP_SUMMARY = "activityParamTripEditInTripSummary";

    public static final int DEFAULT_COLLATOR_STRENGTH = Collator.TERTIARY;
    public static final String LINE_FEED = "\n";
    public static final String HTML_EXTENSION = ".html";
    public static final String CSV_EXTENSION = ".csv";
    public static final String TXT_EXTENSION = ".txt";
    public static final String STREAM_SENDING_MIME = "*/*";
    public static final String STREAM_SENDING_INTENT = Intent.ACTION_SEND_MULTIPLE;
    public static final String PREFS_NAME_ID = "PREFS_NAME_ID";
    public static final int PREFS_MODE = Context.MODE_PRIVATE;
    public static final String PREFS_VALUE_ID_BASE_CURRENCY = "PREFS_VALUE_ID_BASE_CURRENCY";
    public static boolean USE_CACHE_DIR_NOT_FILE_DIR_FOR_REPORTS = true;


}
