package de.koelle.christian.trickytripper.apputils;

import java.util.Currency;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import de.koelle.christian.common.utils.CurrencyUtil;
import de.koelle.christian.trickytripper.constants.Rc;
import de.koelle.christian.trickytripper.model.ExportSettings;
import de.koelle.christian.trickytripper.model.ExportSettings.ExportOutputChannel;

public class PrefWritrerReaderUtils {

    private static final String NULL_VALUE_CURRENCY = "@nothing@";

    private static final String PREFS_VALUE_ID_TRIP_LAST_EDITED_ID = "PREFS_VALUE_ID_TRIP_LAST_EDITED_ID";

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

    public static void saveExportSettings(Editor prefsEditor, ExportSettings exportSettings) {
        prefsEditor.putBoolean(PREFS_VALUE_EXPORT_SETTINGS_EXPORT_PAYMENTS, exportSettings.isExportPayments());
        prefsEditor.putBoolean(PREFS_VALUE_EXPORT_SETTINGS_EXPORT_TRANSFERS, exportSettings.isExportTransfers());
        prefsEditor.putBoolean(PREFS_VALUE_EXPORT_SETTINGS_EXPORT_SPENDINGS, exportSettings.isExportSpendings());
        prefsEditor.putBoolean(PREFS_VALUE_EXPORT_SETTINGS_EXPORT_DEBTS, exportSettings.isExportDebts());
        prefsEditor.putBoolean(PREFS_VALUE_EXPORT_SETTINGS_EXPORT_FORMAT_HTML, exportSettings.isFormatHtml());
        prefsEditor.putBoolean(PREFS_VALUE_EXPORT_SETTINGS_EXPORT_FORMAT_CSV, exportSettings.isFormatCsv());
        prefsEditor.putBoolean(PREFS_VALUE_EXPORT_SETTINGS_EXPORT_FORMAT_TXT, exportSettings.isFormatTxt());
        prefsEditor.putBoolean(PREFS_VALUE_EXPORT_SETTINGS_EXPORT_FORMAT_SEPARATE_FILES_FOR_INDIVIDUALS,
                exportSettings.isSeparateFilesForIndividuals());
        prefsEditor
                .putBoolean(
                        PREFS_VALUE_EXPORT_SETTINGS_EXPORT_FORMAT_SHOW_GLOBAL_SUMS_ON_INDIVIDUAL_SPENDING_REPORT,
                        exportSettings.isShowGlobalSumsOnIndividualSpendingReport());
        prefsEditor.putString(PREFS_VALUE_EXPORT_SETTINGS_OUTPUT_CHANNEL, exportSettings.getOutputChannel().toString());
        prefsEditor.commit();
    }

    public static ExportSettings loadExportSettings(SharedPreferences prefs) {
        ExportSettings exportSettings = new ExportSettings();
        exportSettings.setExportPayments(prefs.getBoolean(PREFS_VALUE_EXPORT_SETTINGS_EXPORT_PAYMENTS, Boolean.TRUE));
        exportSettings.setExportTransfers(prefs.getBoolean(PREFS_VALUE_EXPORT_SETTINGS_EXPORT_TRANSFERS, Boolean.TRUE));
        exportSettings.setExportSpendings(prefs.getBoolean(PREFS_VALUE_EXPORT_SETTINGS_EXPORT_SPENDINGS, Boolean.TRUE));
        exportSettings.setExportDebts(prefs.getBoolean(PREFS_VALUE_EXPORT_SETTINGS_EXPORT_DEBTS, Boolean.TRUE));
        exportSettings.setFormatHtml(prefs.getBoolean(PREFS_VALUE_EXPORT_SETTINGS_EXPORT_FORMAT_HTML, Boolean.TRUE));
        exportSettings.setFormatCsv(prefs.getBoolean(PREFS_VALUE_EXPORT_SETTINGS_EXPORT_FORMAT_CSV, Boolean.FALSE));
        exportSettings.setFormatTxt(prefs.getBoolean(PREFS_VALUE_EXPORT_SETTINGS_EXPORT_FORMAT_TXT, Boolean.FALSE));
        exportSettings.setSeparateFilesForIndividuals(prefs.getBoolean(
                PREFS_VALUE_EXPORT_SETTINGS_EXPORT_FORMAT_SEPARATE_FILES_FOR_INDIVIDUALS, Boolean.FALSE));
        exportSettings.setShowGlobalSumsOnIndividualSpendingReport(prefs
                .getBoolean(PREFS_VALUE_EXPORT_SETTINGS_EXPORT_FORMAT_SHOW_GLOBAL_SUMS_ON_INDIVIDUAL_SPENDING_REPORT,
                        Boolean.TRUE));
        exportSettings.setOutputChannel(ExportOutputChannel.valueOf(prefs.getString(
                PREFS_VALUE_EXPORT_SETTINGS_OUTPUT_CHANNEL, ExportOutputChannel.MAIL.toString())));

        return exportSettings;
    }

    public static long getIdOfTripLastEdited(SharedPreferences prefs) {
        long result = prefs.getLong(PREFS_VALUE_ID_TRIP_LAST_EDITED_ID, 1);
        return result;
    }

    public static Currency loadDefaultCurrency(SharedPreferences prefs, Resources resources) {

        String currencyCodeFromPrefs = prefs.getString(Rc.PREFS_VALUE_ID_BASE_CURRENCY, NULL_VALUE_CURRENCY);

        Currency result = (NULL_VALUE_CURRENCY.equals(currencyCodeFromPrefs))
                ? Currency.getInstance(resources.getConfiguration().locale)
                : Currency.getInstance(currencyCodeFromPrefs);

        result = ensureCurrencyIsSupported(result, resources);
        return result;
    }

    private static Currency ensureCurrencyIsSupported(Currency currency, Resources resources) {
        boolean isSupported = CurrencyUtil.getSuportedCurrencies(resources).contains(currency);
        return isSupported ? currency : Currency.getInstance("EUR");
    }

    public static void saveIdOfTripLastEdited(Editor prefsEditor, long id) {
        prefsEditor.putLong(PREFS_VALUE_ID_TRIP_LAST_EDITED_ID, id);
        prefsEditor.commit();
    }
}
