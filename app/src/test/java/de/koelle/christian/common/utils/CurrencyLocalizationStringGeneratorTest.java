package de.koelle.christian.common.utils;

import android.annotation.TargetApi;

import org.junit.Ignore;
import org.junit.Test;

import java.util.Currency;
import java.util.Locale;
import java.util.TreeSet;

@TargetApi(19)
public class CurrencyLocalizationStringGeneratorTest {

    @Test
    @Ignore("Only used to generate the strings used in preferences.xml")
    public void generateCurrencyStringsForApplication() {
        Locale localeToBeUsed = Locale.UK;

        TreeSet<String> result = new TreeSet<>();

        for (Currency c : Currency.getAvailableCurrencies()) {
            result.add(c.getCurrencyCode() + "-" + c.getDisplayName(localeToBeUsed));
            result.add(c.getCurrencyCode() + "-" + c.getDisplayName(localeToBeUsed));
        }
        for (String s : result) {
            System.out.println(s);
        }
    }

}
