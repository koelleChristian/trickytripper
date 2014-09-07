package de.koelle.christian.common.utils;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import junit.framework.Assert;

import org.junit.Test;

import de.koelle.christian.common.utils.DateUtils;

public class DateUtilsTest {

    @Test
    public void testDecimalSeparatorProvisionBySystem() {

        DateUtils dateUtils;
        Date date = new Date(createrTestDate(2013, 01, 28, 10, 45, 17));

        dateUtils = new DateUtils(Locale.US);
        Assert.assertEquals("03/28/2013 10:45", dateUtils.date2String(date));
        dateUtils = new DateUtils(Locale.UK);
        Assert.assertEquals("28/03/2013 10:45", dateUtils.date2String(date));
        dateUtils = new DateUtils(Locale.ENGLISH);
        Assert.assertEquals("03/28/2013 10:45", dateUtils.date2String(date));
        dateUtils = new DateUtils(Locale.GERMANY);
        Assert.assertEquals("28.03.2013 10:45", dateUtils.date2String(date));
    }

    private long createrTestDate(int year, int month, int day, int hour, int minutes, int sec) {
        GregorianCalendar gregorianCalendar = new GregorianCalendar(Locale.GERMANY);
        gregorianCalendar.set(year, month + 1, day, hour, minutes, sec);
        long timeInMillis = gregorianCalendar.getTimeInMillis();
        return timeInMillis;
    }
}
