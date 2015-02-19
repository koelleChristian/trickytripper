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

        /* 89 currencies */

        currenciesWithExpectedExchangeRate.add("AED");
        currenciesWithExpectedExchangeRate.add("ANG");
        currenciesWithExpectedExchangeRate.add("ARS");
        currenciesWithExpectedExchangeRate.add("AUD");
        /* BDT leads to errors, although the web-frontend delivers a result. */
        // currenciesWithExpectedExchangeRate.add("BDT");
        currenciesWithExpectedExchangeRate.add("BGN");
        currenciesWithExpectedExchangeRate.add("BHD");
        currenciesWithExpectedExchangeRate.add("BND");
        currenciesWithExpectedExchangeRate.add("BOB");
        currenciesWithExpectedExchangeRate.add("BRL");
        currenciesWithExpectedExchangeRate.add("BWP");
        currenciesWithExpectedExchangeRate.add("CAD");
        currenciesWithExpectedExchangeRate.add("CHF");
        currenciesWithExpectedExchangeRate.add("CLP");
        currenciesWithExpectedExchangeRate.add("CNY");
        currenciesWithExpectedExchangeRate.add("COP");
        currenciesWithExpectedExchangeRate.add("CRC");
        currenciesWithExpectedExchangeRate.add("CZK");
        currenciesWithExpectedExchangeRate.add("DKK");
        currenciesWithExpectedExchangeRate.add("DOP");
        currenciesWithExpectedExchangeRate.add("DZD");
        currenciesWithExpectedExchangeRate.add("EEK");
        currenciesWithExpectedExchangeRate.add("EGP");
        currenciesWithExpectedExchangeRate.add("EUR");
        currenciesWithExpectedExchangeRate.add("FJD");
        currenciesWithExpectedExchangeRate.add("GBP");
        currenciesWithExpectedExchangeRate.add("HKD");
        currenciesWithExpectedExchangeRate.add("HNL");
        currenciesWithExpectedExchangeRate.add("HRK");
        currenciesWithExpectedExchangeRate.add("HUF");
        currenciesWithExpectedExchangeRate.add("IDR");
        currenciesWithExpectedExchangeRate.add("ILS");
        currenciesWithExpectedExchangeRate.add("INR");
        currenciesWithExpectedExchangeRate.add("JMD");
        currenciesWithExpectedExchangeRate.add("JOD");
        currenciesWithExpectedExchangeRate.add("JPY");
        currenciesWithExpectedExchangeRate.add("KES");
        currenciesWithExpectedExchangeRate.add("KRW");
        currenciesWithExpectedExchangeRate.add("KWD");
        currenciesWithExpectedExchangeRate.add("KYD");
        currenciesWithExpectedExchangeRate.add("KZT");
        currenciesWithExpectedExchangeRate.add("LBP");
        currenciesWithExpectedExchangeRate.add("LKR");
        currenciesWithExpectedExchangeRate.add("LTL");
        currenciesWithExpectedExchangeRate.add("LVL");
        currenciesWithExpectedExchangeRate.add("MAD");
        currenciesWithExpectedExchangeRate.add("MDL");
        currenciesWithExpectedExchangeRate.add("MKD");
        currenciesWithExpectedExchangeRate.add("MUR");
        /* MVR leads to errors, although the web-frontend delivers a result. */
        // currenciesWithExpectedExchangeRate.add("MVR");
        currenciesWithExpectedExchangeRate.add("MXN");
        currenciesWithExpectedExchangeRate.add("MYR");
        currenciesWithExpectedExchangeRate.add("NAD");
        currenciesWithExpectedExchangeRate.add("NGN");
        currenciesWithExpectedExchangeRate.add("NIO");
        currenciesWithExpectedExchangeRate.add("NOK");
        currenciesWithExpectedExchangeRate.add("NPR");
        currenciesWithExpectedExchangeRate.add("NZD");
        currenciesWithExpectedExchangeRate.add("OMR");
        currenciesWithExpectedExchangeRate.add("PEN");
        currenciesWithExpectedExchangeRate.add("PGK");
        currenciesWithExpectedExchangeRate.add("PHP");
        currenciesWithExpectedExchangeRate.add("PKR");
        currenciesWithExpectedExchangeRate.add("PLN");
        currenciesWithExpectedExchangeRate.add("PYG");
        currenciesWithExpectedExchangeRate.add("QAR");
        currenciesWithExpectedExchangeRate.add("RON");
        currenciesWithExpectedExchangeRate.add("RSD");
        currenciesWithExpectedExchangeRate.add("RUB");
        currenciesWithExpectedExchangeRate.add("SAR");
        currenciesWithExpectedExchangeRate.add("SCR");
        currenciesWithExpectedExchangeRate.add("SEK");
        currenciesWithExpectedExchangeRate.add("SGD");
        currenciesWithExpectedExchangeRate.add("SKK");
        currenciesWithExpectedExchangeRate.add("SLL");
        currenciesWithExpectedExchangeRate.add("SVC");
        currenciesWithExpectedExchangeRate.add("THB");
        currenciesWithExpectedExchangeRate.add("TND");
        currenciesWithExpectedExchangeRate.add("TRY");
        currenciesWithExpectedExchangeRate.add("TTD");
        currenciesWithExpectedExchangeRate.add("TWD");
        currenciesWithExpectedExchangeRate.add("TZS");
        currenciesWithExpectedExchangeRate.add("UAH");
        currenciesWithExpectedExchangeRate.add("UGX");
        currenciesWithExpectedExchangeRate.add("USD");
        currenciesWithExpectedExchangeRate.add("UYU");
        currenciesWithExpectedExchangeRate.add("UZS");
        currenciesWithExpectedExchangeRate.add("VEF");
        /*
         * VND has been removed as it is to extreme. The web frontend is only
         * capable to do one direction.
         */
        // currenciesWithExpectedExchangeRate.add("VND");
        /* XOF leads to errors, although the web-frontend delivers a result. */
        // currenciesWithExpectedExchangeRate.add("XOF");
        currenciesWithExpectedExchangeRate.add("YER");
        currenciesWithExpectedExchangeRate.add("ZAR");
        currenciesWithExpectedExchangeRate.add("ZMK");
    }

    public static boolean isAlive(String currencyCode) {
        return currenciesWithExpectedExchangeRate.contains(currencyCode);
    }

    public static List<Currency> getAllCurrenciesAlive() {
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
