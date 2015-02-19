package de.koelle.christian.common.utils;

import java.util.Locale;

import junit.framework.Assert;

import org.junit.Test;

import de.koelle.christian.common.primitives.DivisionResult;

public class NumberUtilsTest {

    @Test
    public void testMultiply() {
        Assert.assertEquals(39.99d, NumberUtils.multiply(13.33d, 3d));
        Assert.assertEquals(50.01d, NumberUtils.multiply(16.67d, 3));
    }

    @Test
    public void testDivisionJunkFuck() {
        Assert.assertEquals(16.67d, NumberUtils.divide(50d, 3));
        Assert.assertEquals(16.67d, NumberUtils.divide(50.00d, 3));
    }

    @Test
    public void testDegugging() {
        Assert.assertEquals(Double.valueOf(46.67d), Double.valueOf(0d) + 46.67d);
        Assert.assertEquals(Double.valueOf(46.67d), Math.abs(Double.valueOf(0d) + 46.67d));
        Assert.assertEquals(Double.valueOf(46.67d), Math.abs(Double.valueOf(46.67d) + 0d));
        Assert.assertEquals(Double.valueOf(25.32d), NumberUtils.round(Double.valueOf(46.67d) + Double.valueOf(-21.35d)));
    }

    @Test
    public void testRoundingStringToDouble() {
        Assert.assertEquals(14.33d, NumberUtils.getStringToDoubleRounded(Locale.UK, "14.3333333"));
        Assert.assertEquals(14.34d, NumberUtils.getStringToDoubleRounded(Locale.UK, "14.335"));
        Assert.assertEquals(14.33d, NumberUtils.getStringToDoubleRounded(Locale.GERMANY, "14,3333333"));
        Assert.assertEquals(14.34d, NumberUtils.getStringToDoubleRounded(Locale.GERMANY, "14,335"));
        Assert.assertEquals(0d, NumberUtils.getStringToDoubleRounded(Locale.GERMANY, ","));
        Assert.assertEquals(0d, NumberUtils.getStringToDoubleRounded(Locale.GERMANY, ""));
        Assert.assertEquals(0d, NumberUtils.getStringToDoubleRounded(Locale.GERMANY, null));
    }

    @Test
    public void testRoundingStringToDoubleUnrounded() {
        Assert.assertEquals(14.3333333d, NumberUtils.getStringToDoubleNonRounded(Locale.UK, "14.3333333"));
        Assert.assertEquals(14.335d, NumberUtils.getStringToDoubleNonRounded(Locale.UK, "14.335"));
        Assert.assertEquals(14.3333333d, NumberUtils.getStringToDoubleNonRounded(Locale.GERMANY, "14,3333333"));
        Assert.assertEquals(14.335d, NumberUtils.getStringToDoubleNonRounded(Locale.GERMANY, "14,335"));
        Assert.assertEquals(0d, NumberUtils.getStringToDoubleNonRounded(Locale.GERMANY, ","));
        Assert.assertEquals(0d, NumberUtils.getStringToDoubleNonRounded(Locale.GERMANY, ""));
        Assert.assertEquals(0d, NumberUtils.getStringToDoubleNonRounded(Locale.GERMANY, null));
    }

    @Test
    public void testRoundingDivisionDouble() {
        Assert.assertEquals(13.33d, NumberUtils.divide(40d, 3d));
        Assert.assertEquals(33.33d, NumberUtils.divide(100d, 3d));
        Assert.assertEquals(13.33d, NumberUtils.divide(40.00d, 3d));
        Assert.assertEquals(13.35d, NumberUtils.divide(40.05d, 3d));
        Assert.assertEquals(13.35d, NumberUtils.divide(40.06d, 3d));
    }

    @Test
    public void testDivisionForExchangeRates() {
        Assert.assertEquals(0.6666666667d, NumberUtils.divideForExchangeRates(1d, 1.5d));
        
        Assert.assertEquals(0.7584951456d, NumberUtils.divideForExchangeRates(1d, 1.3184d));
        Assert.assertEquals(1.3183915623d, NumberUtils.divideForExchangeRates(1d, 0.7585d));
        Assert.assertEquals(0.9999180167d, NumberUtils.divideForExchangeRates(1d, 1.00008199d));

        Assert.assertEquals(0.8100051840d, NumberUtils.divideForExchangeRates(1d, 1.23456d));
        Assert.assertEquals(10.0d, NumberUtils.divideForExchangeRates(1d, 0.1d));
        Assert.assertEquals(1.0d, NumberUtils.divideForExchangeRates(1d, 1.0d));
    }
    @Test
    public void testDivisionForExchangeRatesMinMaxForEntry() {
        Assert.assertEquals(9999999999.999d, NumberUtils.divideForExchangeRates(1d, 0.0000000001d));
        Assert.assertEquals(0.0000000001d, NumberUtils.divideForExchangeRates(1d, 9999999999.999d));
        Assert.assertEquals(0.0010010010d, NumberUtils.divideForExchangeRates(1d, 999.0000000001d));
        Assert.assertEquals(0.000000001d, NumberUtils.divideForExchangeRates(1d, 1000000000.999d));        
    }

    @Test
    public void testDivisionForExchangeRatesFreaky() {
        Assert.assertEquals(1d, NumberUtils.divideForExchangeRates(1d, 1d));
        Assert.assertEquals(0d, NumberUtils.divideForExchangeRates(1d, 0d));
    }

    public void testRoundingDivisionDoubleInt() {
        Assert.assertEquals(13.33d, NumberUtils.divide(40d, 3));
        Assert.assertEquals(33.33d, NumberUtils.divide(100d, 3));
        Assert.assertEquals(13.33d, NumberUtils.divide(40.002d, 3));
        Assert.assertEquals(13.34d, NumberUtils.divide(40.005d, 3));
        Assert.assertEquals(13.34d, NumberUtils.divide(40.008d, 3));
    }

    @Test
    public void testDivisonWithLoss() {

        DivisionResult result;

        result = NumberUtils.divideWithLoss(-100d, 3, true);
        Assert.assertEquals(-33.33d, result.getResult());
        Assert.assertEquals(-0.01d, result.getLoss());

        result = NumberUtils.divideWithLoss(-50d, 3, true);
        Assert.assertEquals(-16.67d, result.getResult());
        Assert.assertEquals(0.01d, result.getLoss());

        result = NumberUtils.divideWithLoss(-100d, 3, false);
        Assert.assertEquals(33.33d, result.getResult());
        Assert.assertEquals(0.01d, result.getLoss());

        result = NumberUtils.divideWithLoss(-50d, 3, false);
        Assert.assertEquals(16.67d, result.getResult());
        Assert.assertEquals(-0.01d, result.getLoss());

        // True

        result = NumberUtils.divideWithLoss(100d, 3, true);
        Assert.assertEquals(-33.33d, result.getResult());
        Assert.assertEquals(-0.01d, result.getLoss());

        result = NumberUtils.divideWithLoss(50d, 3, true);
        Assert.assertEquals(-16.67d, result.getResult());
        Assert.assertEquals(0.01d, result.getLoss());

        result = NumberUtils.divideWithLoss(10000d, 3, true);
        Assert.assertEquals(-3333.33d, result.getResult());
        Assert.assertEquals(-0.01d, result.getLoss());

        result = NumberUtils.divideWithLoss(100000d, 3, true);
        Assert.assertEquals(-33333.33d, result.getResult());
        Assert.assertEquals(-0.01d, result.getLoss());

        result = NumberUtils.divideWithLoss(1000000d, 3, true);
        Assert.assertEquals(-333333.33d, result.getResult());
        Assert.assertEquals(-0.01d, result.getLoss());

        result = NumberUtils.divideWithLoss(10000000d, 3, true);
        Assert.assertEquals(-3333333.33d, result.getResult());
        Assert.assertEquals(-0.01d, result.getLoss());

        result = NumberUtils.divideWithLoss(100000000d, 3, true);
        Assert.assertEquals(-33333333.33d, result.getResult());
        Assert.assertEquals(-0.01d, result.getLoss());

        result = NumberUtils.divideWithLoss(999999999999d, 3, true);
        Assert.assertEquals(-333333333333d, result.getResult());
        Assert.assertEquals(-0d, result.getLoss());

        result = NumberUtils.divideWithLoss(999999999.99d, 3, true);
        Assert.assertEquals(-333333333.33d, result.getResult());
        Assert.assertEquals(-0d, result.getLoss());

        result = NumberUtils.divideWithLoss(999999999.98d, 3, true);
        Assert.assertEquals(-333333333.33d, result.getResult());
        Assert.assertEquals(0.01d, result.getLoss());

    }
}
