package de.koelle.christian.common.ui.filter;

import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import de.koelle.christian.trickytripper.constants.Rc;

/**
 * Input filter that limits the number of decimal digits that are allowed to be
 * entered.
 */
public class DecimalDigitsInputFilter implements InputFilter {

    private final int decimalDigits;
    private final char decimalDelimiter;

    /**
     * Constructor.
     * 
     * @param decimalDigits
     *            maximum decimal digits
     */
    public DecimalDigitsInputFilter(int decimalDigits, char decimalDelimiter) {
        this.decimalDigits = decimalDigits;
        this.decimalDelimiter = decimalDelimiter;
    }

    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        if (Log.isLoggable(Rc.LT_INPUT, Log.DEBUG)) {
            Log.d(Rc.LT_INPUT, "source=" + source
                    + " start=" + start
                    + " end=" + end
                    + " dest=" + dest
                    + " dstart=" + dstart
                    + " dend=" + dend);
        }

        int delimiterPositionPre = -1;
        int lengthPre = dest.length();
        for (int i = 0; i < lengthPre; i++) {
            char c = dest.charAt(i);
            if (c == decimalDelimiter) {
                delimiterPositionPre = i;
                break;
            }
        }
        StringBuilder potentialResult = new StringBuilder();
        for (int i = 0; i < dest.length(); i++) {
            if (i >= dstart && i <= dend) {
                potentialResult.append(source.subSequence(start, end));
            }
            else {
                potentialResult.append(dest.charAt(i));
            }
        }
        if (dstart > dest.length() - 1) {
            potentialResult.append(source.subSequence(start, end));
        }
        if (Log.isLoggable(Rc.LT_INPUT, Log.DEBUG)) {
            Log.d(Rc.LT_INPUT,
                    "potentialResult=" + potentialResult.toString()
                    );
        }

        if (potentialResult.length() > 12) {
            return "";
        }

        int countDelimiter = 0;
        for (int i = 0; i < potentialResult.length(); i++) {
            char c = potentialResult.charAt(i);
            if (c == decimalDelimiter) {
                countDelimiter++;
            }
        }

        if (countDelimiter > 1) {
            return "";
        }

        if (delimiterPositionPre >= 0) {
            // if the text is entered before the dot
            if (dend <= delimiterPositionPre) {
                return null;
            }
            if (lengthPre - delimiterPositionPre > decimalDigits) {
                return "";
            }
        }

        return null;
    }
}
