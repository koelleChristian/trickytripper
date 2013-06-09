package de.koelle.christian.common.ui.filter;

import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import de.koelle.christian.trickytripper.constants.Rc;

/**
 * Input filter that accepts input only in case that matches with the provided
 * matcher.
 */
public class DecimalNumberInputFilter implements InputFilter {

    private final DecimalNumberInputPatternMatcher amountInputPatternMatcher;

    /**
     * Constructor.
     */
    public DecimalNumberInputFilter(DecimalNumberInputPatternMatcher amountInputPatternMatcher) {
        this.amountInputPatternMatcher = amountInputPatternMatcher;
    }

    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

        logStart(source, start, end, dest, dstart, dend);

        StringBuilder potentialResult = createPotentialResult(source, start, end, dest, dstart, dend);

        logPotentialResult(potentialResult);

        boolean resultAccepted = amountInputPatternMatcher.matches(potentialResult.toString());

        logResult(resultAccepted);

        return resultAccepted ? null : "";

    }

    private StringBuilder createPotentialResult(CharSequence source, int sourceStart, int sourceEnd,
            Spanned destination, int destinationStart, int destinationEnd) {
        return new StringBuilder()
                .append(destination.subSequence(0, destinationStart))
                .append(source.subSequence(sourceStart, sourceEnd))
                .append(destination.subSequence(destinationEnd, destination.length()));
    }

    private void logPotentialResult(StringBuilder potentialResult) {
        if (Rc.debugOn) {
            Log.d(Rc.LT_INPUT, "potentialResult=" + potentialResult.toString());
        }
    }

    private void logResult(boolean resultAccepted) {
        if (Rc.debugOn) {
            Log.d(Rc.LT_INPUT, "resultAccepted=" + resultAccepted);
        }
    }

    private void logStart(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        if (Rc.debugOn) {
            Log.d(Rc.LT_INPUT, "source=" + source
                    + " start=" + start
                    + " end=" + end
                    + " dest=" + dest
                    + " dstart=" + dstart
                    + " dend=" + dend);
        }
    }

}
