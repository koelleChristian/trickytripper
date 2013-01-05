package de.koelle.christian.common.utils;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtils {

    public static String date2String(Date date, Locale locale) {
        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.SHORT, locale);
        return dateFormat.format(date);
    }

}
