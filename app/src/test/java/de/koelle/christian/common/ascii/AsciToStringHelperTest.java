package de.koelle.christian.common.ascii;

import org.junit.Assert;
import org.junit.Test;

public class AsciToStringHelperTest {

    @Test
    public void testNumberMatchingForAlignment() {
        Assert.assertFalse(AsciToStringHelper.isNumeric2(""));
        Assert.assertTrue(AsciToStringHelper.isNumeric2("0"));
        Assert.assertTrue(AsciToStringHelper.isNumeric2("10"));
        Assert.assertTrue(AsciToStringHelper.isNumeric2("100"));
        Assert.assertTrue(AsciToStringHelper.isNumeric2("1,100"));
        Assert.assertTrue(AsciToStringHelper.isNumeric2("1.100"));

        Assert.assertTrue(AsciToStringHelper.isNumeric2("100,99"));
        Assert.assertTrue(AsciToStringHelper.isNumeric2("100.99"));

        Assert.assertTrue(AsciToStringHelper.isNumeric2("1.100,99"));
        Assert.assertTrue(AsciToStringHelper.isNumeric2("1,100.99"));
        Assert.assertTrue(AsciToStringHelper.isNumeric2("12.100,99"));
        Assert.assertTrue(AsciToStringHelper.isNumeric2("12,100.99"));
        Assert.assertTrue(AsciToStringHelper.isNumeric2("123.100,99"));
        Assert.assertTrue(AsciToStringHelper.isNumeric2("123,100.99"));
        Assert.assertTrue(AsciToStringHelper.isNumeric2("1.123.100,99"));
        Assert.assertTrue(AsciToStringHelper.isNumeric2("1,123,100.99"));

        Assert.assertFalse(AsciToStringHelper.isNumeric2(","));
        Assert.assertFalse(AsciToStringHelper.isNumeric2("."));
        Assert.assertFalse(AsciToStringHelper.isNumeric2("1,"));
        Assert.assertFalse(AsciToStringHelper.isNumeric2("1."));
        Assert.assertFalse(AsciToStringHelper.isNumeric2(",1"));
        Assert.assertFalse(AsciToStringHelper.isNumeric2(".1"));

        Assert.assertTrue(AsciToStringHelper.isNumeric2("100,9"));
        Assert.assertTrue(AsciToStringHelper.isNumeric2("100.9"));
        Assert.assertTrue(AsciToStringHelper.isNumeric2("100,999"));
        Assert.assertTrue(AsciToStringHelper.isNumeric2("100.999"));
        Assert.assertTrue(AsciToStringHelper.isNumeric2("100,999999"));
        Assert.assertTrue(AsciToStringHelper.isNumeric2("100.999999"));
    }

}
