package de.koelle.christian.common.utils;

import android.text.Editable;

public class StringUtils {

    public static String clearInput(Editable s) {
        String result = null;
        if (s != null ) {
            result = s.toString().trim();
            if (result.length() <= 0) {
                result = null;
            }
        }
        return result;
    }

    public static boolean isNotBlank(String textInput) {
        if (textInput == null) {
            return false;
        }
        String trimmedClone = textInput.trim();
        return trimmedClone.length() >= 1;
    }

    public static StringBuilder generateString(int amount, String txtHeadingSymbol) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < amount; i++) {
            result.append(txtHeadingSymbol);
        }
        return result;
    }
}
