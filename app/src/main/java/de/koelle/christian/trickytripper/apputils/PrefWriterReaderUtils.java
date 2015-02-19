package de.koelle.christian.trickytripper.apputils;

import java.util.Currency;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import de.koelle.christian.common.utils.CurrencyUtil;
import de.koelle.christian.trickytripper.constants.Rc;
import de.koelle.christian.trickytripper.model.ExportSettings;
import de.koelle.christian.trickytripper.model.ExportSettings.ExportOutputChannel;
import de.koelle.christian.trickytripper.model.ImportSettings;

public class PrefWriterReaderUtils {

    private static final String NULL_VALUE_CURRENCY = "@nothing@";

    private static final String PREFS_VALUE_ID_TRIP_LAST_EDITED_ID = "PREFS_VALUE_ID_TRIP_LAST_EDITED_ID";

    private static final String PREFS_VALUE_IMPORT_SETTINGS_REPLACE_EXISTING = "PREFS_VALUE_IMPORT_SETTINGS_REPLACE_EXISTING";

    private static final String PREFS_VALUE_EXCHANGE_RATE_AUTO_SAVE_SEQ = "PREFS_VALUE_EXCHANGE_RATE_AUTO_SAVE_SEQ";

    private static final String PREFS_VALUE_EXPORT_SETTINGS_EXPORT_PAYMENTS = "PREFS_VALUE_EXPORT_SETTINGS_EXPORT_PAYMENTS";
    private static final String PREFS_VALUE_EXPORT_SETTINGS_EXPORT_TRANSFERS = "PREFS_VALUE_EXPORT_SETTINGS_EXPORT_TRANSFERS";
    private static final String PREFS_VALUE_EXPORT_SETTINGS_EXPORT_SPENDINGS = "PREFS_VALUE_EXPORT_SETTINGS_EXPORT_SPENDINGS";
    private static final String PREFS_VALUE_EXPORT_SETTINGS_EXPORT_DEBTS = "PREFS_VALUE_EXPORT_SETTINGS_EXPORT_DEBTS";
    private static final String PREFS_VALUE_EXPORT_SETTINGS_EXPORT_FORMAT_HTML = "PREFS_VALUE_EXPORT_SETTINGS_EXPORT_FORMAT_HTML";
    private static final String PREFS_VALUE_EXPORT_SETTINGS_EXPORT_FORMAT_CSV = "PREFS_VALUE_EXPORT_SETTINGS_EXPORT_FORMAT_CSV";
    private static final String PREFS_VALUE_EXPORT_SETTINGS_EXPORT_FORMAT_TXT = "PREFS_VALUE_EXPORT_SETTINGS_EXPORT_FORMAT_TXT";
    private static final String PREFS_VALUE_EXPORT_SETTINGS_EXPORT_FORMAT_SEPARATE_FILES_FOR_INDIVIDUALS = "PREFS_VALUE_EXPORT_SETTINGS_EXPORT_FORMAT_SEPARATE_FILES_FOR_INDIVIDUALS";
    private static final String PREFS_VALUE_EXPORT_SETTINGS_EXPORT_FORMAT_SHOW_GLOBAL_SUMS_ON_INDIVIDUAL_SPENDING_REPORT = "PREFS_VALUE_EXPORT_SETTINGS_EXPORT_FORMAT_SHOW_GLOBAL_SUMS_ON_INDIVIDUAL_SPENDING_REPORT";
    private static final String PREFS_VALUE_EXPORT_SETTINGS_OUTPUT_CHANNEL = "PREFS_VALUE_EXPORT_SETTINGS_OUTPUT_CHANNEL";

    public static void saveExportSettings(Editor prefsEditor, ExportSettings settings) {
        prefsEditor.putBoolean(PREFS_VALUE_EXPORT_SETTINGS_EXPORT_PAYMENTS, settings.isExportPayments());
        prefsEditor.putBoolean(PREFS_VALUE_EXPORT_SETTINGS_EXPORT_TRANSFERS, settings.isExportTransfers());
        prefsEditor.putBoolean(PREFS_VALUE_EXPORT_SETTINGS_EXPORT_SPENDINGS, settings.isExportSpending());
        prefsEditor.putBoolean(PREFS_VALUE_EXPORT_SETTINGS_EXPORT_DEBTS, settings.isExportDebts());
        prefsEditor.putBoolean(PREFS_VALUE_EXPORT_SETTINGS_EXPORT_FORMAT_HTML, settings.isFormatHtml());
        prefsEditor.putBoolean(PREFS_VALUE_EXPORT_SETTINGS_EXPORT_FORMAT_CSV, settings.isFormatCsv());
        prefsEditor.putBoolean(PREFS_VALUE_EXPORT_SETTINGS_EXPORT_FORMAT_TXT, settings.isFormatTxt());
        prefsEditor.putBoolean(PREFS_VALUE_EXPORT_SETTINGS_EXPORT_FORMAT_SEPARATE_FILES_FOR_INDIVIDUALS,
                settings.isSeparateFilesForIndividuals());
        prefsEditor
                .putBoolean(
                        PREFS_VALUE_EXPORT_SETTINGS_EXPORT_FORMAT_SHOW_GLOBAL_SUMS_ON_INDIVIDUAL_SPENDING_REPORT,
                        settings.isShowGlobalSumsOnIndividualSpendingReport());
        prefsEditor.putString(PREFS_VALUE_EXPORT_SETTINGS_OUTPUT_CHANNEL, settings.getOutputChannel().toString());
        prefsEditor.commit();
    }

    public static ExportSettings loadExportSettings(SharedPreferences prefs) {
        ExportSettings settings = new ExportSettings();
        settings.setExportPayments(prefs.getBoolean(PREFS_VALUE_EXPORT_SETTINGS_EXPORT_PAYMENTS, Boolean.TRUE));
        settings.setExportTransfers(prefs.getBoolean(PREFS_VALUE_EXPORT_SETTINGS_EXPORT_TRANSFERS, Boolean.TRUE));
        settings.setExportSpending(prefs.getBoolean(PREFS_VALUE_EXPORT_SETTINGS_EXPORT_SPENDINGS, Boolean.TRUE));
        settings.setExportDebts(prefs.getBoolean(PREFS_VALUE_EXPORT_SETTINGS_EXPORT_DEBTS, Boolean.TRUE));
        settings.setFormatHtml(prefs.getBoolean(PREFS_VALUE_EXPORT_SETTINGS_EXPORT_FORMAT_HTML, Boolean.TRUE));
        settings.setFormatCsv(prefs.getBoolean(PREFS_VALUE_EXPORT_SETTINGS_EXPORT_FORMAT_CSV, Boolean.FALSE));
        settings.setFormatTxt(prefs.getBoolean(PREFS_VALUE_EXPORT_SETTINGS_EXPORT_FORMAT_TXT, Boolean.FALSE));
        settings.setSeparateFilesForIndividuals(prefs.getBoolean(
                PREFS_VALUE_EXPORT_SETTINGS_EXPORT_FORMAT_SEPARATE_FILES_FOR_INDIVIDUALS, Boolean.FALSE));
        settings.setShowGlobalSumsOnIndividualSpendingReport(prefs
                .getBoolean(PREFS_VALUE_EXPORT_SETTINGS_EXPORT_FORMAT_SHOW_GLOBAL_SUMS_ON_INDIVIDUAL_SPENDING_REPORT,
                        Boolean.TRUE));
        settings.setOutputChannel(ExportOutputChannel.valueOf(prefs.getString(
                PREFS_VALUE_EXPORT_SETTINGS_OUTPUT_CHANNEL, ExportOutputChannel.MAIL.toString())));

        return settings;
    }

    public static void saveImportSettings(Editor prefsEditor, ImportSettings settings) {
        prefsEditor.putBoolean(PREFS_VALUE_IMPORT_SETTINGS_REPLACE_EXISTING,
                settings.isCreateNewRateOnValueChange());
        prefsEditor.commit();
    }

    public static ImportSettings loadImportSettings(SharedPreferences prefs) {
        ImportSettings settings = new ImportSettings();
        settings.setCreateNewRateOnValueChange(prefs.getBoolean(
                PREFS_VALUE_IMPORT_SETTINGS_REPLACE_EXISTING, Boolean.FALSE));
        return settings;
    }

    public static long loadIdOfTripLastEdited(SharedPreferences prefs) {
        long result = prefs.getLong(PREFS_VALUE_ID_TRIP_LAST_EDITED_ID, 1);
        return result;
    }

    public static long loadExchangeRateAutoSaveSeq(SharedPreferences prefs) {
        long result = prefs.getLong(PREFS_VALUE_EXCHANGE_RATE_AUTO_SAVE_SEQ, 1);
        return result;
    }

    public static void saveExchangeRateAutoSaveSeq(Editor prefsEditor, long id) {
        prefsEditor.putLong(PREFS_VALUE_EXCHANGE_RATE_AUTO_SAVE_SEQ, id);
        prefsEditor.commit();
    }

    public static Currency loadDefaultCurrency(SharedPreferences prefs, Resources resources) {
        String currencyCodeFromPrefs = prefs.getString(Rc.PREFS_VALUE_ID_BASE_CURRENCY, NULL_VALUE_CURRENCY);
        return currencyCode2Currency(resources, currencyCodeFromPrefs);
    }

    private static Currency currencyCode2Currency(Resources resources, String currencyCodeFromPrefs) {
        Currency result = Currency.getInstance("EUR");
        try {
            result = (NULL_VALUE_CURRENCY.equals(currencyCodeFromPrefs))
                    ? Currency.getInstance(resources.getConfiguration().locale)
                    : Currency.getInstance(currencyCodeFromPrefs);
        }
        catch (IllegalArgumentException e) {
            // Intentionally caught.
        }

        result = ensureCurrencyIsSupported(result, resources);
        return result;
    }

    private static Currency ensureCurrencyIsSupported(Currency currency, Resources resources) {
        boolean isSupported = CurrencyUtil.getSupportedCurrencies(resources).contains(currency);
        return isSupported ? currency : Currency.getInstance("EUR");
    }

    public static void saveIdOfTripLastEdited(Editor prefsEditor, long id) {
        prefsEditor.putLong(PREFS_VALUE_ID_TRIP_LAST_EDITED_ID, id);
        prefsEditor.commit();
    }
}
