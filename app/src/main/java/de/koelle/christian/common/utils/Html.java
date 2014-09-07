package de.koelle.christian.common.utils;

public class Html {

    public static CharSequence toHtmlChar(String value) {
        if (value == null) {
            return null;
        }
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);

            if (c == '<') {
                result.append("&lt;");
            }
            else if (c == '>') {
                result.append("&gt;");
            }
            else if (c == '&') {
                result.append("&amp;");
            }
            else if (c > 0x7E || c < ' ') {
                result.append("&#" + ((int) c) + ";");
            }
            else if (c == ' ') {
                while (i + 1 < value.length() && value.charAt(i + 1) == ' ') {
                    result.append("&nbsp;");
                    i++;
                }

                result.append(' ');
            }
            else {
                result.append(c);
            }
        }
        return result;
    }

}
