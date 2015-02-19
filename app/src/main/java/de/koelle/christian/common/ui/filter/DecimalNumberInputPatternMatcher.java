package de.koelle.christian.common.ui.filter;

import java.util.regex.Pattern;

import de.koelle.christian.common.constants.Rglob;

/**
 * Matcher for the decimal input.
 */
public class DecimalNumberInputPatternMatcher {

    private final Pattern pattern;
    private final int maxLength;

    public DecimalNumberInputPatternMatcher(int amountOfLeftHandDigits, int amountOfDecimalDigits) {
        this(amountOfLeftHandDigits, amountOfDecimalDigits, -1);
    }


    public DecimalNumberInputPatternMatcher(int amountOfLeftHandDigits, int amountOfDecimalDigits, int maxLength) {
        String patternString = "\\d{0," + amountOfLeftHandDigits + "}([" + Rglob.DECIMAL_DEL_DOT
                + Rglob.DECIMAL_DEL_COMMA
                + "]{1}\\d{0," + amountOfDecimalDigits + "})?";
        pattern = Pattern.compile(patternString);
        this.maxLength = maxLength;
    }

    public DecimalNumberInputPatternMatcher() {
        this(9, 2);
    }

    public boolean matches(String input) {
        return (maxLength <= 0) ?
                matchesPattern(input) :
                matchesPattern(input) && input.length() <= maxLength;
    }

    private boolean matchesPattern(String input) {
        return pattern.matcher(input).matches();
    }
}
