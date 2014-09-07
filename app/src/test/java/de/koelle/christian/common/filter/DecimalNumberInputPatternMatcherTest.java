package de.koelle.christian.common.filter;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import de.koelle.christian.common.ui.filter.DecimalNumberInputPatternMatcher;

/**
 * Tests the equivalent class.
 */
public class DecimalNumberInputPatternMatcherTest {

    DecimalNumberInputPatternMatcher matcher;

    @Before
    public void init() {
        matcher = new DecimalNumberInputPatternMatcher();
    }

    @Test
    public void testAmountStringMatching() {

        Assert.assertEquals(true, matcher.matches(""));
        Assert.assertEquals(true, matcher.matches("1"));
        Assert.assertEquals(true, matcher.matches("12345"));

        Assert.assertEquals(true, matcher.matches("1."));
        Assert.assertEquals(true, matcher.matches("."));
        Assert.assertEquals(true, matcher.matches(".1"));
        Assert.assertEquals(true, matcher.matches(".12"));
        Assert.assertEquals(true, matcher.matches("123."));

        Assert.assertEquals(false, matcher.matches(".123"));

        Assert.assertEquals(true, matcher.matches("1,"));
        Assert.assertEquals(true, matcher.matches(","));
        Assert.assertEquals(true, matcher.matches(",1"));
        Assert.assertEquals(true, matcher.matches(",12"));
        Assert.assertEquals(true, matcher.matches("123,"));
        Assert.assertEquals(false, matcher.matches(",123"));

        Assert.assertEquals(false, matcher.matches(",."));
        Assert.assertEquals(false, matcher.matches(",1."));
        Assert.assertEquals(false, matcher.matches("1,1.2"));

        Assert.assertEquals(true, matcher.matches("123456789"));
        Assert.assertEquals(true, matcher.matches("123456789."));
        Assert.assertEquals(true, matcher.matches("123456789.1"));
        Assert.assertEquals(true, matcher.matches("123456789.12"));
        Assert.assertEquals(false, matcher.matches("123456789.123"));
        Assert.assertEquals(true, matcher.matches("123456789,1"));
        Assert.assertEquals(true, matcher.matches("123456789,1"));
        Assert.assertEquals(true, matcher.matches("123456789,12"));
        Assert.assertEquals(false, matcher.matches("123456789,123"));
        Assert.assertEquals(false, matcher.matches("1234567891"));

    }

}
