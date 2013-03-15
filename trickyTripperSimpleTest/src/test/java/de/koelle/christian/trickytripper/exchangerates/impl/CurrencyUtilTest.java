package de.koelle.christian.trickytripper.exchangerates.impl;

import junit.framework.Assert;

import org.junit.Test;

import de.koelle.christian.common.utils.CurrencyUtil;

public class CurrencyUtilTest {

    @Test
    public void testCalcExpectedAmountOfExchangeRates() {
        Assert.assertEquals(3, CurrencyUtil.calcExpectedAmountOfExchangeRates(3));
        Assert.assertEquals(6, CurrencyUtil.calcExpectedAmountOfExchangeRates(4));
        Assert.assertEquals(10, CurrencyUtil.calcExpectedAmountOfExchangeRates(5));
        Assert.assertEquals(15, CurrencyUtil.calcExpectedAmountOfExchangeRates(6));
        Assert.assertEquals(45, CurrencyUtil.calcExpectedAmountOfExchangeRates(10));
        Assert.assertEquals(190, CurrencyUtil.calcExpectedAmountOfExchangeRates(20));
        Assert.assertEquals(300, CurrencyUtil.calcExpectedAmountOfExchangeRates(25));
        Assert.assertEquals(435, CurrencyUtil.calcExpectedAmountOfExchangeRates(30));
        Assert.assertEquals(780, CurrencyUtil.calcExpectedAmountOfExchangeRates(40));
        Assert.assertEquals(3916, CurrencyUtil.calcExpectedAmountOfExchangeRates(
                CurrencyUtil.getAllCurrenciesAlive().size()));
    }

}
