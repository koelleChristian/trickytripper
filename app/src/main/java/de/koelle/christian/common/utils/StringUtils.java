package de.koelle.christian.common.utils;

import android.text.Editable;

public class StringUtils {

    public static String clearInput(Editable s) {
        String input = s.toString();
        if (input != null) {
            input = input.trim();
            if (input.length() <= 0) {
                input = null;
            }
        }
        return input;
    }

    public static boolean isBlank(String textInput) {
        if (textInput == null) {
            return true;
        }
        String clone = textInput;
        clone.trim(); //TODO(ckoelle)
        return clone.length() < 1;
    }

    public static StringBuilder generateString(int amount, String txtHeadingSymbol) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < amount; i++) {
            result.append(txtHeadingSymbol);
        }
        return result;
    }
}
