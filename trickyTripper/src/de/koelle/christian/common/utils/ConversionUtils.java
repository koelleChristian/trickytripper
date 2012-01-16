package de.koelle.christian.common.utils;

import java.util.Date;
import java.util.GregorianCalendar;

public class ConversionUtils {

    public static int bool2Int(boolean in) {
        return in ? 1 : 0;
    }

    public static boolean int2bool(int in) {
        return (in > 0) ? true : false;
    }

    public static String nullSafe(String in) {
        return (in == null) ? "" : in;
    }

    private ConversionUtils() {
    }

    public static Date getDateByLong(long timeInMilisecs) {
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTimeInMillis(timeInMilisecs);
        return calendar.getTime();
    }

}
