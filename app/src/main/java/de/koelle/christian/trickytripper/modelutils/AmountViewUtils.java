package de.koelle.christian.trickytripper.modelutils;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.Locale;

import de.koelle.christian.common.utils.CurrencyUtil;
import de.koelle.christian.trickytripper.R;
import de.koelle.christian.trickytripper.model.Amount;

public class AmountViewUtils {

    public static int getColor(Context context, Amount amount) {

        if (amount == null || amount.getValue() == null || amount.getValue() == 0) {
            return ContextCompat.getColor(context, android.R.color.primary_text_light);
        }
        else if (amount.getValue() < 0) {
            return ContextCompat.getColor(context, R.color.red);
        }

        return ContextCompat.getColor(context, R.color.green);
    }

    public static String getAmountString(Locale locale, Amount amount, boolean justTheNumber,
            boolean blankIfZero, boolean blankIfNull, boolean forceFraction, boolean stripOffSign) {
        if (amount != null) {

            StringBuilder result = new StringBuilder();
            if (!justTheNumber) {
                result.append(CurrencyUtil.getSymbolToCurrency(null, amount.getUnit())).append(" ");
            }
            return getDoubleString(locale, amount.getValue(), blankIfZero, blankIfNull, forceFraction, stripOffSign,
                    result);
        }
        return (blankIfNull) ? "" : null;
    }

    public static String getDoubleString(Locale locale, Double value) {
        return getDoubleString(locale, value, true, true, false, true,
                new StringBuilder());
    }

    private static String getDoubleString(Locale locale, Double value, boolean blankIfZero, boolean blankIfNull,
            boolean forceFraction,
            boolean stripOffSign, StringBuilder result) {
        if (value == null) {
            return (blankIfNull) ? "" : null;
        }
        Double valueInternal = (stripOffSign) ? Math.abs(value) : value;

        if (blankIfZero && valueInternal.equals(Double.valueOf(0))) {
            return "";
        }
        NumberFormat nf = NumberFormat.getNumberInstance(locale);

        if (forceFraction || valueInternal % 1 != 0) {
            nf.setMinimumFractionDigits(2);
            nf.setMaximumFractionDigits(12);
        }
        String numberFormat = nf.format(valueInternal);

        result.append(numberFormat);
        return result.toString();
    }

    public static String getAmountString(Locale locale, Amount amount, boolean justTheNumber,
            boolean stripOffSign, boolean forceFraction) {
        return getAmountString(locale, amount, justTheNumber, false, true, forceFraction, stripOffSign);
    }

    public static String getAmountString(Locale locale, Amount amount, boolean justTheNumber, boolean stripOffSign) {
        return getAmountString(locale, amount, justTheNumber, false, true, false, stripOffSign);
    }

    public static String getAmountString(Locale locale, Amount amount, boolean justTheNumber) {
        return getAmountString(locale, amount, justTheNumber, false, true, false, false);
    }

    public static String getAmountString(Locale locale, Amount amount) {
        return getAmountString(locale, amount, false);
    }
}
