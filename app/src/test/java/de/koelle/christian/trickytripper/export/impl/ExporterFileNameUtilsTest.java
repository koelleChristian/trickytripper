package de.koelle.christian.trickytripper.export.impl;

import java.util.Date;
import java.util.Locale;

import junit.framework.Assert;

import org.junit.Test;

public class ExporterFileNameUtilsTest {

    @Test
    public void testTripNameCleaning() {
        Assert.assertEquals("Christian", ExporterFileNameUtils.clean("Christian").toString());
        Assert.assertEquals("ChristianKolle", ExporterFileNameUtils.clean("Christian Kolle ").toString());
        Assert.assertEquals("ChristianKlle", ExporterFileNameUtils.clean("Christian Kölle").toString());
        Assert.assertEquals("GreeceWater2009", ExporterFileNameUtils.clean("Greece Water 2009").toString());
        Assert.assertEquals("MyFeakyWeirdoTrip", ExporterFileNameUtils.clean("My �$%Feaky%$� Weirdo T@@@rip")
                .toString());
    }

    @Test
    public void testTimestamp() {
        Assert.assertEquals("201205292002",
                ExporterFileNameUtils.getTimeStamp(new Date(1338314522376L), Locale.GERMANY));

        Assert.assertEquals("201205292010", ExporterFileNameUtils.getTimeStamp(new Date(1338315008925L
                ), Locale.GERMANY));
    }
}
