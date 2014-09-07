package de.koelle.christian.common.ascii;

import junit.framework.Assert;

import org.junit.Test;

import de.koelle.christian.common.ascii.AsciToStringHelper;

public class AsciToStringHelperTest {

    @Test
    public void testNumberMatchingForAlignment() {
        Assert.assertEquals(false, AsciToStringHelper.isNumeric2(""));
        Assert.assertEquals(true, AsciToStringHelper.isNumeric2("0"));
        Assert.assertEquals(true, AsciToStringHelper.isNumeric2("10"));
        Assert.assertEquals(true, AsciToStringHelper.isNumeric2("100"));
        Assert.assertEquals(true, AsciToStringHelper.isNumeric2("1,100"));
        Assert.assertEquals(true, AsciToStringHelper.isNumeric2("1.100"));

        Assert.assertEquals(true, AsciToStringHelper.isNumeric2("100,99"));
        Assert.assertEquals(true, AsciToStringHelper.isNumeric2("100.99"));

        Assert.assertEquals(true, AsciToStringHelper.isNumeric2("1.100,99"));
        Assert.assertEquals(true, AsciToStringHelper.isNumeric2("1,100.99"));
        Assert.assertEquals(true, AsciToStringHelper.isNumeric2("12.100,99"));
        Assert.assertEquals(true, AsciToStringHelper.isNumeric2("12,100.99"));
        Assert.assertEquals(true, AsciToStringHelper.isNumeric2("123.100,99"));
        Assert.assertEquals(true, AsciToStringHelper.isNumeric2("123,100.99"));
        Assert.assertEquals(true, AsciToStringHelper.isNumeric2("1.123.100,99"));
        Assert.assertEquals(true, AsciToStringHelper.isNumeric2("1,123,100.99"));

        Assert.assertEquals(false, AsciToStringHelper.isNumeric2(","));
        Assert.assertEquals(false, AsciToStringHelper.isNumeric2("."));
        Assert.assertEquals(false, AsciToStringHelper.isNumeric2("1,"));
        Assert.assertEquals(false, AsciToStringHelper.isNumeric2("1."));
        Assert.assertEquals(false, AsciToStringHelper.isNumeric2(",1"));
        Assert.assertEquals(false, AsciToStringHelper.isNumeric2(".1"));

        Assert.assertEquals(true, AsciToStringHelper.isNumeric2("100,9"));
        Assert.assertEquals(true, AsciToStringHelper.isNumeric2("100.9"));
        Assert.assertEquals(true, AsciToStringHelper.isNumeric2("100,999"));
        Assert.assertEquals(true, AsciToStringHelper.isNumeric2("100.999"));
        Assert.assertEquals(true, AsciToStringHelper.isNumeric2("100,999999"));
        Assert.assertEquals(true, AsciToStringHelper.isNumeric2("100.999999"));
    }

}
