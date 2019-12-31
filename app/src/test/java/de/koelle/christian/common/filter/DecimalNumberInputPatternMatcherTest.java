package de.koelle.christian.common.filter;

import org.junit.Assert;
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

        Assert.assertTrue(matcher.matches(""));
        Assert.assertTrue(matcher.matches("1"));
        Assert.assertTrue(matcher.matches("12345"));

        Assert.assertTrue(matcher.matches("1."));
        Assert.assertTrue(matcher.matches("."));
        Assert.assertTrue(matcher.matches(".1"));
        Assert.assertTrue(matcher.matches(".12"));
        Assert.assertTrue(matcher.matches("123."));

        Assert.assertFalse(matcher.matches(".123"));

        Assert.assertTrue(matcher.matches("1,"));
        Assert.assertTrue(matcher.matches(","));
        Assert.assertTrue(matcher.matches(",1"));
        Assert.assertTrue(matcher.matches(",12"));
        Assert.assertTrue(matcher.matches("123,"));
        Assert.assertFalse(matcher.matches(",123"));

        Assert.assertFalse(matcher.matches(",."));
        Assert.assertFalse(matcher.matches(",1."));
        Assert.assertFalse(matcher.matches("1,1.2"));

        Assert.assertTrue(matcher.matches("123456789"));
        Assert.assertTrue(matcher.matches("123456789."));
        Assert.assertTrue(matcher.matches("123456789.1"));
        Assert.assertTrue(matcher.matches("123456789.12"));
        Assert.assertFalse(matcher.matches("123456789.123"));
        Assert.assertTrue(matcher.matches("123456789,1"));
        Assert.assertTrue(matcher.matches("123456789,1"));
        Assert.assertTrue(matcher.matches("123456789,12"));
        Assert.assertFalse(matcher.matches("123456789,123"));
        Assert.assertFalse(matcher.matches("1234567891"));

    }

}
