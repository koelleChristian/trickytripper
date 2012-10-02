package de.koelle.christian.common.utils;

import java.util.Currency;
import java.util.Locale;
import java.util.TreeSet;

import org.junit.Ignore;
import org.junit.Test;

public class CurrencyLocalizationStringGeneratorTest {

    @Test
    @Ignore("Only used to generate the strings used in preferences.xml")
    public void generateCurrencyStringsForApplication() {
        Locale localeToBeUsed = Locale.UK;

        TreeSet<String> result = new TreeSet<String>();

        // This is new in Java 7 ...
        for (Currency c : Currency.getAvailableCurrencies()) {
            result.add(c.getCurrencyCode() + "-" + c.getDisplayName(localeToBeUsed));
        }
        for (String s : result) {
            System.out.println(s);
        }
    }

}
