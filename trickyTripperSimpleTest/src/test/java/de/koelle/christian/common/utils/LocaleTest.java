package de.koelle.christian.common.utils;

import java.text.DecimalFormatSymbols;
import java.util.Locale;

import org.junit.Test;

public class LocaleTest {

    @Test
    public void testDivision() {
        for (Locale l : Locale.getAvailableLocales()) {
            System.out.println("locale.country=" + l.getCountry() + " >"
                    + new DecimalFormatSymbols(l).getDecimalSeparator() + "<");
        }
    }
}
