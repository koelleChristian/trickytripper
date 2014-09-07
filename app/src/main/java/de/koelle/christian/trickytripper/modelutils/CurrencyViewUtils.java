package de.koelle.christian.trickytripper.modelutils;

import java.util.Currency;

public class CurrencyViewUtils {

    private static final String OPEN = "[";
    private static final String CLOSE = "]";

    public static String getCurrencySymbolInBrackets(Currency currency) {
        return OPEN + currency.getSymbol() + CLOSE;
    }

    public static String getCurrencyCodeInBrackets(Currency currency) {
        return OPEN + currency.getCurrencyCode() + CLOSE;
    }

}
