package de.koelle.christian.common.filter;

import java.util.Locale;

import junit.framework.Assert;

import org.junit.Test;

import de.koelle.christian.common.ui.filter.DecimalNumberInputUtil;

/**
 * Tests the equivalent class.
 */
public class DecimalNumberInputUtilTest {

    DecimalNumberInputUtil decimalNumberInputUtil;

    @Test
    public void testModelToWidgetFix() {
        DecimalNumberInputUtil decimalNumberInputUtil;

        decimalNumberInputUtil = new DecimalNumberInputUtil(Locale.GERMANY);
        Assert.assertEquals("100045345,84", decimalNumberInputUtil.fixInputStringModelToWidget("100.045.345,84"));

        decimalNumberInputUtil = new DecimalNumberInputUtil(Locale.US);
        Assert.assertEquals("100045345.84", decimalNumberInputUtil.fixInputStringModelToWidget("100,045,345.84"));
    }

    @Test
    public void testForGermany() {
        DecimalNumberInputUtil decimalNumberInputUtil = new DecimalNumberInputUtil(Locale.GERMANY);
        Assert.assertEquals(',', decimalNumberInputUtil.getLocalizedDelimiter());
        Assert.assertEquals("123,44", decimalNumberInputUtil.fixInputStringWidgetToParser("123.44"));
        Assert.assertEquals("123,44", decimalNumberInputUtil.fixInputStringWidgetToParser("123,44"));
        Assert.assertEquals("1234,", decimalNumberInputUtil.fixInputStringWidgetToParser("1234."));
        Assert.assertEquals("1234,", decimalNumberInputUtil.fixInputStringWidgetToParser("1234,"));
        Assert.assertEquals(",1234", decimalNumberInputUtil.fixInputStringWidgetToParser(".1234"));
        Assert.assertEquals(",1234", decimalNumberInputUtil.fixInputStringWidgetToParser(",1234"));
        Assert.assertEquals("1234", decimalNumberInputUtil.fixInputStringWidgetToParser("1234"));
    }

    @Test
    public void testForUs() {
        DecimalNumberInputUtil decimalNumberInputUtil = new DecimalNumberInputUtil(Locale.US);
        Assert.assertEquals('.', decimalNumberInputUtil.getLocalizedDelimiter());
        Assert.assertEquals("123.44", decimalNumberInputUtil.fixInputStringWidgetToParser("123.44"));
        Assert.assertEquals("123.44", decimalNumberInputUtil.fixInputStringWidgetToParser("123,44"));
        Assert.assertEquals("1234.", decimalNumberInputUtil.fixInputStringWidgetToParser("1234."));
        Assert.assertEquals("1234.", decimalNumberInputUtil.fixInputStringWidgetToParser("1234,"));
        Assert.assertEquals(".1234", decimalNumberInputUtil.fixInputStringWidgetToParser(".1234"));
        Assert.assertEquals(".1234", decimalNumberInputUtil.fixInputStringWidgetToParser(",1234"));
        Assert.assertEquals("1234", decimalNumberInputUtil.fixInputStringWidgetToParser("1234"));
    }
}
