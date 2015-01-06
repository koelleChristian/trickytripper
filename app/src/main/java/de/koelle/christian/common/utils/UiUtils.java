package de.koelle.christian.common.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.text.InputFilter;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import de.koelle.christian.common.ui.filter.DecimalNumberInputFilter;
import de.koelle.christian.common.ui.filter.DecimalNumberInputPatternMatcher;

public class UiUtils {

    public static void makeProperNumberInput(final EditText editText,
            DecimalNumberInputPatternMatcher amountInputPatternMatcher) {
        editText.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
        editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        editText.setFilters(new InputFilter[] { new DecimalNumberInputFilter(amountInputPatternMatcher) });
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
    public static int dpi2px(Resources resources, int dpi) {
        final float scale = resources.getDisplayMetrics().density;
        return (int) (dpi * scale + 0.5f);
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
        return getInactiveColor(context.getResources());
    }

    public static int getInactiveColor(Resources resources) {
        return resources.getColor(android.R.color.darker_gray);
    }

    public static void setActiveOrInactive(boolean enabled, TextView result, int extensionStringId, Resources resources, int resid) {
        if (!enabled) {
            result.setTextColor(UiUtils.getInactiveColor(resources));
            result.setTypeface(Typeface.DEFAULT, Typeface.ITALIC);
            if (extensionStringId > 0) {
                result.setText(result.getText() + " (" + resources.getString(extensionStringId) + ")");
            }

        }
        else {
            
            result.setTextColor(resid);
            result.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
        }
    }
}
