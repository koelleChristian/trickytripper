package de.koelle.christian.trickytripper.exchangerates.impl;

import junit.framework.Assert;

import org.junit.Test;

import java.util.Currency;

import de.koelle.christian.trickytripper.model.ExchangeRate;

public class ExchangeRateEqualityTest {
    private static final Currency EUR = Currency.getInstance("EUR");
    private static final Currency USD = Currency.getInstance("USD");
    private static final Currency TRY = Currency.getInstance("TRY");

    @Test
    public void testEqualityForImport() {

        ExchangeRate rate1 = createExchangeRateForThisTest(EUR, USD, 1.23456);
        ExchangeRate rate2;

        rate2 = createExchangeRateForThisTest(EUR, USD, 1.23456);
        Assert.assertEquals(true, rate1.equalsFromImportPointOfView(rate2));

        rate2 = createExchangeRateForThisTest(TRY, USD, 1.23456);
        Assert.assertEquals(false, rate1.equalsFromImportPointOfView(rate2));

        rate2 = createExchangeRateForThisTest(EUR, TRY, 1.23456);
        Assert.assertEquals(false, rate1.equalsFromImportPointOfView(rate2));

        rate2 = createExchangeRateForThisTest(EUR, USD, 1.2222);
        Assert.assertEquals(false, rate1.equalsFromImportPointOfView(rate2));

        rate2 = createExchangeRateForThisTest(USD, EUR, 1.23456);
        Assert.assertEquals(false, rate1.equalsFromImportPointOfView(rate2));

    }

    private ExchangeRate createExchangeRateForThisTest(Currency instance, Currency instance2, double exchangeRate) {
        ExchangeRate rate1 = new ExchangeRate();
        rate1.setCurrencyFrom(instance);
        rate1.setCurrencyTo(instance2);
        rate1.setExchangeRate(exchangeRate);
        return rate1;
    }

}
