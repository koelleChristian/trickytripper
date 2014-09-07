package de.koelle.christian.common.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtils {

    private static final String defaultTimePattern = "HH:mm";
    private final Locale locale;
    private final String datePattern;

    public DateUtils(Locale locale) {
        this.locale = locale;
        this.datePattern = getPattern(locale);
    }

    public String date2String(Date date) {
        DateFormat dateFormat = new SimpleDateFormat(datePattern, locale);
        return new StringBuilder().append(dateFormat.format(date)).toString();
    }

    public String getPattern(Locale locale) {

        String localizedDatePattern = ((SimpleDateFormat) DateFormat.getDateInstance(DateFormat.SHORT, locale))
                .toPattern();
        if (!localizedDatePattern.contains("yyyy")) {
            localizedDatePattern = localizedDatePattern.replace("yy", "yyyy");
        }
        if (!localizedDatePattern.contains("dd")) {
            localizedDatePattern = localizedDatePattern.replace("d", "dd");
        }
        if (!localizedDatePattern.contains("MM")) {
            localizedDatePattern = localizedDatePattern.replace("M", "MM");
        }
        return localizedDatePattern + " " + defaultTimePattern;
    }
}
