package de.koelle.christian.common.utils;

import java.text.DecimalFormatSymbols;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.text.InputFilter;
import android.text.InputType;
import android.text.method.DigitsKeyListener;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import de.koelle.christian.common.ui.filter.DecimalDigitsInputFilter;

public class UiUtils {

    public static void makeProperNumberInput(final EditText editText, final Locale locale) {
        editText.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
        editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        editText.setFilters(new InputFilter[] { new DecimalDigitsInputFilter(2, new DecimalFormatSymbols(locale)
                .getDecimalSeparator()) });
        StringBuilder pattern = new StringBuilder("0123456789");
        pattern.append(new
                DecimalFormatSymbols(locale).getDecimalSeparator());
        editText.setKeyListener(DigitsKeyListener.getInstance(pattern.toString()));
    }

    public static TextView setLabelAndValueOnTextView(View result, int viewId, Object label, Object value) {
        TextView textView = (TextView) result.findViewById(viewId);
        setValue(label, value, textView);
        return textView;
    }

    public static void removeFromView(View view, int viewId) {
        View subview = view.findViewById(viewId);
        setViewVisibility(subview, false);
    }

    public static void showInView(View view, int viewId) {
        View subview = view.findViewById(viewId);
        setViewVisibility(subview, true);
    }

    public static void setViewVisibility(View view, boolean visible) {
        int visibility = (visible) ? View.VISIBLE : View.GONE;
        view.setVisibility(visibility);
    }

    public static TextView setLabelAndValueOnTextView(Activity activity, int viewId, Object label, Object value) {
        TextView textView = (TextView) activity.findViewById(viewId);
        setValue(label, value, textView);
        return textView;
    }

    private static void setValue(Object label, Object value, TextView textView) {
        StringBuilder builder = new StringBuilder();
        if (label != null) {
            builder.append(label);
            builder.append(": ");
        }
        builder.append((value == null) ? " " : value);
        if (textView != null) {
            textView.setText(builder.toString());
        }
    }

    public static void setFontAndStyle(Context context, TextView textView, boolean inactive, int textAppearanceId) {
        if (inactive) {
            textView.setTextColor(getInactiveColor(context));
            textView.setTypeface(Typeface.DEFAULT, Typeface.ITALIC);
        }
        else {
            textView.setTextAppearance(context, textAppearanceId);
        }
    }

    public static int getInactiveColor(Context context) {
        return context.getResources().getColor(android.R.color.darker_gray);
    }
}
