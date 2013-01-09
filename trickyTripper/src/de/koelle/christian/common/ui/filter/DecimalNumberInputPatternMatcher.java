package de.koelle.christian.common.ui.filter;

import java.util.regex.Pattern;

import de.koelle.christian.common.constants.Rglob;

/**
 * Matcher for the decimal input.
 */
public class DecimalNumberInputPatternMatcher {

    private final Pattern pattern;

    /**
     * Constructor
     * 
     * @param amoutOfDecimalDigits
     * @param amountOfLeftHandDigits
     */
    public DecimalNumberInputPatternMatcher(int amountOfLeftHandDigits, int amoutOfDecimalDigits) {
        String patternString = "\\d{0," + amountOfLeftHandDigits + "}([" + Rglob.DECIMAL_DEL_DOT
                + Rglob.DECIMAL_DEL_COMMA
                + "]{1}\\d{0," + amoutOfDecimalDigits + "})?";
        pattern = Pattern.compile(patternString);
    }

    public DecimalNumberInputPatternMatcher() {
        this(9, 2);
    }

    public boolean matches(String input) {
        return pattern.matcher(input).matches();
    }
}
