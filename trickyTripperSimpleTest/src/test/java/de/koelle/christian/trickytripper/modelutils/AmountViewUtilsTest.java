package de.koelle.christian.trickytripper.modelutils;

import java.util.Currency;
import java.util.Locale;

import junit.framework.Assert;

import org.junit.Test;

import de.koelle.christian.trickytripper.model.Amount;

public class AmountViewUtilsTest {

    @Test
    public void testGetAmountStringLocale() {
        Locale locale;
        boolean justTheNumber;
        boolean blankIfZero;
        boolean blankIfNull;
        boolean forceFraction;
        boolean stripOffSign;

        locale = Locale.UK;
        justTheNumber = true;
        blankIfZero = true;
        blankIfNull = true;
        forceFraction = true;
        stripOffSign = true;
        Amount inputAmount = createAmount("EUR", 9.19);

        Assert.assertEquals("9.19", AmountViewUtils.getAmountString(locale, inputAmount, justTheNumber,
                blankIfZero, blankIfNull, forceFraction, stripOffSign));

        locale = Locale.GERMANY;

        Assert.assertEquals("9,19", AmountViewUtils.getAmountString(locale, inputAmount, justTheNumber,
                blankIfZero, blankIfNull, forceFraction, stripOffSign));
    }

    @Test
    public void testGetAmountStringFraction() {
        Locale locale;
        boolean justTheNumber;
        boolean blankIfZero;
        boolean blankIfNull;
        boolean forceFraction;
        boolean stripOffSign;

        locale = Locale.UK;
        justTheNumber = true;
        blankIfZero = true;
        blankIfNull = true;
        forceFraction = true;
        stripOffSign = true;
        Amount inputAmount = createAmount("EUR", 9.00);

        Assert.assertEquals("9.00", AmountViewUtils.getAmountString(locale, inputAmount, justTheNumber,
                blankIfZero, blankIfNull, forceFraction, stripOffSign));

        forceFraction = false;

        Assert.assertEquals("9", AmountViewUtils.getAmountString(locale, inputAmount, justTheNumber,
                blankIfZero, blankIfNull, forceFraction, stripOffSign));
    }

    @Test
    public void testGetDoubleStringLocale() {
        Locale locale;
        boolean blankIfZero;
        boolean blankIfNull;
        boolean forceFraction;
        boolean stripOffSign;

        locale = Locale.UK;
        blankIfZero = true;
        blankIfNull = true;
        forceFraction = true;
        stripOffSign = true;

        Double inputValue = Double.valueOf(9.00);

        Assert.assertEquals("9.00", AmountViewUtils.getDoubleString(locale, inputValue,
                blankIfZero, blankIfNull, forceFraction, stripOffSign));

        locale = Locale.GERMANY;

        Assert.assertEquals("9,00", AmountViewUtils.getDoubleString(locale, inputValue,
                blankIfZero, blankIfNull, forceFraction, stripOffSign));

    }

    private Amount createAmount(String currencyCode, double value) {
        Amount amount = new Amount();
        amount.setUnit(Currency.getInstance(currencyCode));
        amount.setValue(Double.valueOf(value));
        return amount;
    }
}
