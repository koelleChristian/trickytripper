package de.koelle.christian.trickytripper.exchangerates.impl;

import junit.framework.Assert;

import org.junit.Test;

import java.util.Arrays;
import java.util.Currency;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    @Test
    public void testNonOnlineSupportedCurrencies() {
        Set<String> supported = new HashSet<>(Arrays.asList("ALL", "XCD", "EUR", "BBD", "BTN", "BND", "XAF", "CUP", "USD", "FKP", "GIP", "HUF", "IRR", "JMD", "AUD", "LAK", "LYD", "MKD", "XOF", "NZD", "OMR", "PGK", "RWF", "WST", "RSD", "SEK", "TZS", "AMD", "BSD", "BAM", "CVE", "CNY", "CRC", "CZK", "ERN", "GEL", "HTG", "INR", "JOD", "KRW", "LBP", "MWK", "MRO", "MZN", "ANG", "PEN", "QAR", "STD", "SLL", "SOS", "SDG", "SYP", "AOA", "AWG", "BHD", "BZD", "BWP", "BIF", "KYD", "COP", "DKK", "GTQ", "HNL", "IDR", "ILS", "KZT", "KWD", "LSL", "MYR", "MUR", "MNT", "MMK", "NGN", "PAB", "PHP", "RON", "SAR", "SGD", "ZAR", "SRD", "TWD", "TOP", "VEF", "DZD", "ARS", "AZN", "BYR", "BOB", "BGN", "CAD", "CLP", "CDF", "DOP", "FJD", "GMD", "GYD", "ISK", "IQD", "JPY", "KPW", "LVL", "CHF", "MGA", "MDL", "MAD", "NPR", "NIO", "PKR", "PYG", "SHP", "SCR", "SBD", "LKR", "THB", "TRY", "AED", "VUV", "YER", "AFN", "BDT", "BRL", "KHR", "KMF", "HRK", "DJF", "EGP", "ETB", "XPF", "GHS", "GNF", "HKD", "XDR", "KES", "KGS", "LRD", "MOP", "MVR", "MXN", "NAD", "NOK", "PLN", "RUB", "SZL", "TJS", "TTD", "UGX", "UYU", "VND", "TND", "UAH", "UZS", "TMT", "GBP", "ZMW", "BTC", "BYN"));
        List<Currency> allCurrenciesAlive = CurrencyUtil.getAllCurrenciesAlive();
        for (Currency o : allCurrenciesAlive) {
            if(!supported.contains(o.getCurrencyCode())){
                System.out.println(o.getDisplayName());
            }
        }
    }


}
