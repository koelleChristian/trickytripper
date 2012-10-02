package de.koelle.christian.common.utils;

import java.util.ArrayList;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.content.res.Resources;
import de.koelle.christian.trickytripper.R;

public class CurrencyUtil {

    private static String[] currencyCodes;
    private static String[] currencyFullName;
    private static Map<Currency, String> currency2SymbolMap = new HashMap<Currency, String>();
    private static Map<Currency, String> currency2DisplayNameMap = new HashMap<Currency, String>();
    private static List<Currency> supportedCurrencies = new ArrayList<Currency>();

    public static String[] getSupportedCurrencyCodes(Resources resources) {
        initIfRequired(resources);
        return currencyCodes;
    }

    public static String[] getSupportedCurrencyFullNames(Resources resources) {
        initIfRequired(resources);
        return currencyFullName;
    }

    public static List<Currency> getSuportedCurrencies(Resources resources) {
        initIfRequired(resources);
        return supportedCurrencies;
    }

    public static String getFullNameToCurrency(Resources resources, Currency currency) {
        initIfRequired(resources);
        return currency2DisplayNameMap.get(currency);
    }

    public static String getSymbolToCurrency(Resources resources, Currency currency) {
        initIfRequired(resources);
        return currency2SymbolMap.get(currency);
    }

    public static String getSymbolToCurrency(Resources resources, Currency currency, boolean withBrackets) {
        return (withBrackets) ? "[" + getSymbolToCurrency(resources, currency) + "]" : getSymbolToCurrency(resources,
                currency);
    }

    private static void initIfRequired(Resources resources) {
        if (currencyCodes == null) {
            String[] currencyArray = resources.getStringArray(R.array.currencies);

            List<String> currencyCodes = new ArrayList<String>();
            List<String> currencyFullName = new ArrayList<String>();

            for (int i = 0; i < currencyArray.length; i++) {

                String value = currencyArray[i];

                String currencyCode = value.substring(0, 3);
                String currencyName = value.substring(4, value.length());
                Currency instance = null;
                try {
                    instance = Currency.getInstance(currencyCode);
                }
                catch (IllegalArgumentException e) {
                    continue;
                }

                String symbol = instance.getSymbol(Locale.UK);
                String currencyCode2 = instance.getCurrencyCode();
                StringBuilder fullName = new StringBuilder()
                        .append(currencyCode2);
                if (!currencyCode2.equals(symbol)) {
                    fullName
                            .append(", ")
                            .append(symbol);
                }
                fullName.append(", ")
                        .append(currencyName);

                String fullNameString = fullName.toString();
                currencyFullName.add(fullNameString);
                currencyCodes.add(currencyCode);
                currency2DisplayNameMap.put(instance, fullNameString);
                currency2SymbolMap.put(instance, symbol);
                supportedCurrencies.add(instance);
            }
            int size = currencyCodes.size();
            CurrencyUtil.currencyCodes = new String[size];
            CurrencyUtil.currencyFullName = new String[size];
            CurrencyUtil.currencyCodes = currencyCodes.toArray(CurrencyUtil.currencyCodes);
            CurrencyUtil.currencyFullName = currencyFullName.toArray(CurrencyUtil.currencyFullName);
        }

    }

}
