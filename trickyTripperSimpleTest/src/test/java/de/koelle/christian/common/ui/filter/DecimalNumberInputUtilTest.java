package de.koelle.christian.common.ui.filter;

import java.util.Locale;

import junit.framework.Assert;

import org.junit.Test;

/**
 * Tests the equivalent class.
 */
public class DecimalNumberInputUtilTest {

    DecimalNumberInputUtil decimalNumberInputUtil;

    @Test
    public void testForGermany() {
        DecimalNumberInputUtil decimalNumberInputUtil = new DecimalNumberInputUtil(Locale.GERMANY);
        Assert.assertEquals(',', decimalNumberInputUtil.getLocalizedDelimiter());
        Assert.assertEquals("123,44", decimalNumberInputUtil.fixInputString("123.44"));
        Assert.assertEquals("123,44", decimalNumberInputUtil.fixInputString("123,44"));
        Assert.assertEquals("1234,", decimalNumberInputUtil.fixInputString("1234."));
        Assert.assertEquals("1234,", decimalNumberInputUtil.fixInputString("1234,"));
        Assert.assertEquals(",1234", decimalNumberInputUtil.fixInputString(".1234"));
        Assert.assertEquals(",1234", decimalNumberInputUtil.fixInputString(",1234"));
        Assert.assertEquals("1234", decimalNumberInputUtil.fixInputString("1234"));
    }

    @Test
    public void testForUs() {
        DecimalNumberInputUtil decimalNumberInputUtil = new DecimalNumberInputUtil(Locale.US);
        Assert.assertEquals('.', decimalNumberInputUtil.getLocalizedDelimiter());
        Assert.assertEquals("123.44", decimalNumberInputUtil.fixInputString("123.44"));
        Assert.assertEquals("123.44", decimalNumberInputUtil.fixInputString("123,44"));
        Assert.assertEquals("1234.", decimalNumberInputUtil.fixInputString("1234."));
        Assert.assertEquals("1234.", decimalNumberInputUtil.fixInputString("1234,"));
        Assert.assertEquals(".1234", decimalNumberInputUtil.fixInputString(".1234"));
        Assert.assertEquals(".1234", decimalNumberInputUtil.fixInputString(",1234"));
        Assert.assertEquals("1234", decimalNumberInputUtil.fixInputString("1234"));
    }
}
