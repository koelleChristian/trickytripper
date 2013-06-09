package de.koelle.christian.common.utils;

import java.text.DecimalFormatSymbols;
import java.util.Locale;

import org.junit.Ignore;
import org.junit.Test;

public class LocaleTest {

    @Ignore("Only used to verify certain aspects of the localized decimal separator")
    @Test
    public void testDecimalSeparatorProvisionBySystem() {
        for (Locale l : Locale.getAvailableLocales()) {
            System.out.println("locale.country=" + l.getCountry() + " >"
                    + new DecimalFormatSymbols(l).getDecimalSeparator() + "<");
        }
    }
}
