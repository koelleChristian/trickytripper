package de.koelle.christian.common.utils;

import android.content.res.Resources;

import java.util.ArrayList;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import de.koelle.christian.trickytripper.R;
import de.koelle.christian.trickytripper.model.CurrencyWithName;

public class CurrencyUtil {

    private static String[] currencyCodes;
    private static String[] currencyFullName;
    private static final Map<Currency, String> currency2SymbolMap = new HashMap<>();
    private static Map<Currency, String> currency2DisplayNameMap = new HashMap<>();
    private static List<String> currenciesWithExpectedExchangeRate = new ArrayList<>();
    private static List<Currency> supportedCurrencies = new ArrayList<>();

    static {
        currenciesWithExpectedExchangeRate.add("AUD");
        currenciesWithExpectedExchangeRate.add("BGN");
        currenciesWithExpectedExchangeRate.add("BRL");
        currenciesWithExpectedExchangeRate.add("CAD");
        currenciesWithExpectedExchangeRate.add("CHF");
        currenciesWithExpectedExchangeRate.add("CNY");
        currenciesWithExpectedExchangeRate.add("CZK");
        currenciesWithExpectedExchangeRate.add("DKK");
        currenciesWithExpectedExchangeRate.add("EUR");
        currenciesWithExpectedExchangeRate.add("GBP");
        currenciesWithExpectedExchangeRate.add("HKD");
        currenciesWithExpectedExchangeRate.add("HRK");
        currenciesWithExpectedExchangeRate.add("HUF");
        currenciesWithExpectedExchangeRate.add("IDR");
        currenciesWithExpectedExchangeRate.add("ILS");
        currenciesWithExpectedExchangeRate.add("INR");
        currenciesWithExpectedExchangeRate.add("JPY");
        currenciesWithExpectedExchangeRate.add("KRW");
        currenciesWithExpectedExchangeRate.add("MXN");
        currenciesWithExpectedExchangeRate.add("MYR");
        currenciesWithExpectedExchangeRate.add("NOK");
        currenciesWithExpectedExchangeRate.add("NZD");
        currenciesWithExpectedExchangeRate.add("PHP");
        currenciesWithExpectedExchangeRate.add("PLN");
        currenciesWithExpectedExchangeRate.add("RON");
        currenciesWithExpectedExchangeRate.add("RUB");
        currenciesWithExpectedExchangeRate.add("SEK");
        currenciesWithExpectedExchangeRate.add("SGD");
        currenciesWithExpectedExchangeRate.add("THB");
        currenciesWithExpectedExchangeRate.add("TRY");
        currenciesWithExpectedExchangeRate.add("USD");
        currenciesWithExpectedExchangeRate.add("ZAR");
    }

    public static boolean isRateProvidedExternally(String currencyCode) {
        return currenciesWithExpectedExchangeRate.contains(currencyCode);
    }

    public static List<Currency> getAllCurrenciesWithRetrievableRate() {
        List<Currency> result = new ArrayList<>();
        for (String code : currenciesWithExpectedExchangeRate) {
            result.add(Currency.getInstance(code));
        }
        return result;
    }

    public static String[] getSupportedCurrencyCodes(Resources resources) {
        initIfRequired(resources);
        return currencyCodes;
    }

    public static String[] getSupportedCurrencyFullNames(Resources resources) {
        initIfRequired(resources);
        return currencyFullName;
    }

    public static Currency getSupportedCurrency(Resources resources, int index) {
        initIfRequired(resources);
        return supportedCurrencies.get(index);
    }

    public static List<Currency> getSupportedCurrencies(Resources resources) {
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

    @SuppressWarnings("LI_LAZY_INIT_UPDATE_STATIC")
    private static void initIfRequired(Resources resources) {
        if (currencyCodes == null) {
            String[] currencyArray = resources.getStringArray(R.array.currencies);

            List<String> currencyCodes = new ArrayList<>();
            List<String> currencyFullName = new ArrayList<>();

            for (String value : currencyArray) {

                String currencyCode = value.substring(0, 3);
                String currencyName = value.substring(4, value.length());
                Currency instance;
                try {
                    instance = Currency.getInstance(currencyCode);
                } catch (IllegalArgumentException e) {
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
                supportedCurrencies.add(instance);

                currency2DisplayNameMap.put(instance, fullNameString);
                currency2SymbolMap.put(instance, symbol);
            }
            int size = currencyCodes.size();

            CurrencyUtil.currencyCodes = new String[size];
            CurrencyUtil.currencyFullName = new String[size];
            CurrencyUtil.currencyCodes = currencyCodes.toArray(CurrencyUtil.currencyCodes);
            CurrencyUtil.currencyFullName = currencyFullName.toArray(CurrencyUtil.currencyFullName);
        }

    }

    public static int calcExpectedAmountOfExchangeRates(int size) {
        int result = 0;
        if (size > 0) {
            for (int i = size - 1; i > 0; i--) {
                result = result + i;
            }
        }
        return result;
    }

    public static Currency getNextOtherCurrency(Currency currencyOther, Resources resources) {
        Currency result = null;
        List<Currency> supportedCurrencies = CurrencyUtil.getSupportedCurrencies(resources);
        for (Currency currency : supportedCurrencies) {
            if (!currencyOther.equals(currency)) {
                result = currency;
                break;
            }
        }
        Assert.notNull(result);
        return result;
    }

    public static List<CurrencyWithName> convertToCurrencyWithName(List<Currency> currenciesMatchingInOrderOfUsage,
            Resources resources) {
        initIfRequired(resources);
        List<CurrencyWithName> result = new ArrayList<>();
        for (Currency c : currenciesMatchingInOrderOfUsage) {
            result.add(new CurrencyWithName(c, currency2DisplayNameMap.get(c)));
        }
        return result;
    }

    public static List<CurrencyWithName> convertOthersToCurrencyWithName(Set<Currency> currenciesToBeExcluded,
            Resources resources) {
        initIfRequired(resources);
        List<CurrencyWithName> result = new ArrayList<>();
        for (Currency c : supportedCurrencies) {
            if (!currenciesToBeExcluded.contains(c)) {
                result.add(new CurrencyWithName(c, currency2DisplayNameMap.get(c)));
            }
        }
        return result;
    }

    public static Currency getFirstOther(Currency currencyToBeExcluded, Resources resources) {
        initIfRequired(resources);
        for (Currency c : supportedCurrencies) {
            if (!c.equals(currencyToBeExcluded)) {
                return c;
            }
        }
        return null;
    }

}
