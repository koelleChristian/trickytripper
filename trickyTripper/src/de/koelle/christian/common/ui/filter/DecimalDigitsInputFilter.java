package de.koelle.christian.common.ui.filter;

import android.text.InputFilter;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import de.koelle.christian.trickytripper.constants.Rc;

/**
 * Input filter that accepts input only in case that matches with the provided
 * matcher.
 */
public class DecimalDigitsInputFilter implements InputFilter {

    private final DecimalNumberInputPatternMatcher amountInputPatternMatcher;

    /**
     * Constructor.
     */
    public DecimalDigitsInputFilter(DecimalNumberInputPatternMatcher amountInputPatternMatcher) {
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

    private void logPotentialResult(StringBuilder potentialResult) {
        if (Log.isLoggable(Rc.LT_INPUT, Log.DEBUG)) {
            Log.d(Rc.LT_INPUT,
                    "potentialResult=" + potentialResult.toString()
                    );
        }
    }

    private void logResult(boolean resultAccepted) {
        if (Log.isLoggable(Rc.LT_INPUT, Log.DEBUG)) {
            Log.d(Rc.LT_INPUT, "resultAccepted=" + resultAccepted);
        }
    }

    private void logStart(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        if (Log.isLoggable(Rc.LT_INPUT, Log.DEBUG)) {
            Log.d(Rc.LT_INPUT, "source=" + source
                    + " start=" + start
                    + " end=" + end
                    + " dest=" + dest
                    + " dstart=" + dstart
                    + " dend=" + dend);
        }
    }
    
            String textToCheck = destination.subSequence(0, destinationStart).  
            toString() + source.subSequence(sourceStart, sourceEnd) +  
            destination.subSequence(  
            destinationEnd, destination.length()).toString(); 

    private StringBuilder createPotentialResult(CharSequence source, int start, int end, Spanned dest, int dstart,
            int dend) {
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
        SpannableString sp = new SpannableString(s);
        TextUtils.copySpansFrom((Spanned) source,
                start, end, null, sp, 0);

        return potentialResult;
    }
    
   private StringBuilder createPotentialResult(CharSequence source, int start, int end, Spanned dest, int dstart,int dend) {
            String potentialResult = destination.subSequence(0, destinationStart).  
            toString() + source.subSequence(sourceStart, sourceEnd) +  
            destination.subSequence(  
            destinationEnd, destination.length()).toString(); 
            return potentialResultﬂ
    }
}
