package de.koelle.christian.trickytripper.export.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ExporterFileNameUtils {

    public static StringBuilder clean(String input) {
        return toCamelCase(filterSpecialChars(new StringBuilder(input)), " ");
    }

    public static String getTimeStamp(Locale locale) {
        return getTimeStamp(new Date(), locale);
    }

    public static String getTimeStamp(Date timestamp, Locale locale) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm", locale);
        return sdf.format(timestamp);
    }

    public static StringBuilder toCamelCase(StringBuilder input, String delimiter) {
        StringBuilder sb = new StringBuilder();
        for (String oneString : input.toString().split(delimiter))
        {
            if (oneString == null || oneString.length() < 1) {
                continue;
            }
            sb.append(oneString.substring(0, 1).toUpperCase(Locale.US));
            sb.append(oneString.substring(1).toLowerCase(Locale.US));
        }
        return sb;
    }

    public static StringBuilder filterSpecialChars(StringBuilder str) {
        StringBuilder filtered = new StringBuilder(str.length());
        for (int i = 0; i < str.length(); i++) {
            char current = str.charAt(i);
            if ((current >= 0x30 && current <= 0x39) // 0-9
                    || (current >= 0x41 && current <= 0x5A) // A-Z
                    || (current == 0x20) // Space
                    || (current >= 0x61 && current <= 0x7A)) { // a-z
                filtered.append(current);
            }
        }

        return filtered;
    }

}
