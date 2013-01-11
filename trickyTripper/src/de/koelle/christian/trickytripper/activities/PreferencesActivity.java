package de.koelle.christian.trickytripper.activities;

import java.util.Currency;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import de.koelle.christian.common.utils.CurrencyUtil;
import de.koelle.christian.trickytripper.R;
import de.koelle.christian.trickytripper.TrickyTripperApp;
import de.koelle.christian.trickytripper.constants.Rc;

public class PreferencesActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        configurePreferencesToBeUsed();
        setPreferenceScreen(createPreferenceHierarchy());

    }

    @Override
    protected void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(Rc.PREFS_VALUE_ID_BASE_CURRENCY)) {
            Preference currencyDefaultPrefs = findPreference(key);
            // The following is expected to result in a currency string that is
            // supported by the runtime.
            String string = sharedPreferences.getString(key, "");
            currencyDefaultPrefs.setSummary(getDisplayStringForCurrency(Currency.getInstance(string)));
        }
    }

    private void configurePreferencesToBeUsed() {
        getPreferenceManager().setSharedPreferencesMode(Rc.PREFS_MODE);
        getPreferenceManager().setSharedPreferencesName(Rc.PREFS_NAME_ID);
    }

    private PreferenceScreen createPreferenceHierarchy() {

        PreferenceScreen root = getPreferenceManager().createPreferenceScreen(this);

        /*
         * =========== Launcher for the exchange rate management =============
         */
        PreferenceScreen intentPref = getPreferenceManager().createPreferenceScreen(this);
        intentPref.setIntent(new Intent().setClass(this, ManageExchangeRatesActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        intentPref.setTitle(R.string.prefs_view_title_exchange_rate_management);

        root.addPreference(intentPref);

        /* =============== Default currency picker ================= */
        Currency defaultBaseCurrency = ((TrickyTripperApp) getApplication()).getMiscController()
                .getDefaultBaseCurrency();

        ListPreference listPref = new ListPreference(this);
        listPref.setEntries(CurrencyUtil.getSupportedCurrencyFullNames(getResources()));
        listPref.setEntryValues(CurrencyUtil.getSupportedCurrencyCodes(getResources()));
        listPref.setKey(Rc.PREFS_VALUE_ID_BASE_CURRENCY);
        listPref.setDialogTitle(R.string.prefs_view_heading_chooser_currency);
        listPref.setTitle(R.string.prefs_view_title_currency);
        listPref.setDefaultValue(defaultBaseCurrency.getCurrencyCode());
        listPref.setSummary(getDisplayStringForCurrency(defaultBaseCurrency));

        root.addPreference(listPref);

        /* =============== Enabler for Smart Help ================= */
        CheckBoxPreference smartHelpPrefs = new CheckBoxPreference(this);
        smartHelpPrefs.setKey(Rc.PREFS_VALUE_ID_ENABLE_SMART_HELP);
        smartHelpPrefs.setTitle(R.string.prefs_view_title_enable_smart_help);
        smartHelpPrefs.setDefaultValue(Boolean.TRUE);

        root.addPreference(smartHelpPrefs);

        return root;
    }

    private String getDisplayStringForCurrency(Currency currency) {
        return CurrencyUtil.getFullNameToCurrency(getResources(), currency);
    }
}
