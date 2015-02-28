package de.koelle.christian.trickytripper.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceScreen;

import java.util.Currency;

import de.koelle.christian.common.utils.CurrencyUtil;
import de.koelle.christian.trickytripper.R;
import de.koelle.christian.trickytripper.TrickyTripperApp;
import de.koelle.christian.trickytripper.constants.Rc;

public class PreferencesFragment extends com.github.machinarius.preferencefragment.PreferenceFragment implements OnSharedPreferenceChangeListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        configurePreferencesToBeUsed();
        setPreferenceScreen(createPreferenceHierarchy());
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
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

        PreferenceScreen root = getPreferenceManager().createPreferenceScreen(getActivity());

        /*
         * =========== Launcher for the exchange rate management =============
         */
        PreferenceScreen intentPref = getPreferenceManager().createPreferenceScreen(getActivity());
        intentPref.setIntent(new Intent().setClass(getActivity(), ExchangeRateManageActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        intentPref.setTitle(R.string.prefs_view_title_exchange_rate_management);

        root.addPreference(intentPref);

        /* =============== Default currency picker ================= */
        Currency defaultBaseCurrency = getApp().getMiscController()
                .getDefaultBaseCurrency();

        ListPreference listPref = new ListPreference(getActivity());
        listPref.setEntries(CurrencyUtil.getSupportedCurrencyFullNames(getResources()));
        listPref.setEntryValues(CurrencyUtil.getSupportedCurrencyCodes(getResources()));
        listPref.setKey(Rc.PREFS_VALUE_ID_BASE_CURRENCY);
        listPref.setDialogTitle(R.string.prefs_view_heading_chooser_currency);
        listPref.setTitle(R.string.prefs_view_title_currency);
        listPref.setDefaultValue(defaultBaseCurrency.getCurrencyCode());
        listPref.setSummary(getDisplayStringForCurrency(defaultBaseCurrency));

        root.addPreference(listPref);

        return root;
    }

    private String getDisplayStringForCurrency(Currency currency) {
        return CurrencyUtil.getFullNameToCurrency(getResources(), currency);
    }

    private TrickyTripperApp getApp() {
        return (TrickyTripperApp) getActivity().getApplication();
    }
}
