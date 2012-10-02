package de.koelle.christian.trickytripper.modelutils;

import java.text.NumberFormat;
import java.util.Locale;

import android.content.Context;
import de.koelle.christian.common.utils.CurrencyUtil;
import de.koelle.christian.trickytripper.R;
import de.koelle.christian.trickytripper.model.Amount;

public class AmountViewUtils {

    public static int getColor(Context context, Amount amount) {
        if (amount == null || amount.getValue() == null || amount.getValue() == 0) {
            return context.getResources().getColor(android.R.color.white);
        }
        else if (amount.getValue() < 0) {
            return context.getResources().getColor(R.color.red);
        }

        return context.getResources().getColor(R.color.green);
    }

    public static final String getAmountString(Locale locale, Amount amount, boolean justTheNumber,
            boolean blankIfZero, boolean blankIfNull, boolean forceFraction, boolean stripOffSign) {
        if (amount != null) {
            Double value = (stripOffSign) ? Math.abs(amount.getValue()) : amount.getValue();

            if (blankIfZero && value.equals(Double.valueOf(0))) {
                return "";
            }

            StringBuilder result = new StringBuilder();
            NumberFormat nf = NumberFormat.getNumberInstance(locale);
            if (!justTheNumber) {
                result.append(CurrencyUtil.getSymbolToCurrency(null, amount.getUnit())).append(" ");
            }
            if (forceFraction || value % 1 != 0) {
                nf.setMinimumFractionDigits(2);
            }
            result.append(nf.format(value));
            return result.toString();
        }
        return (blankIfNull) ? "" : null;
    }

    public static final String getAmountString(Locale locale, Amount amount, boolean justTheNumber,
            boolean stripOffSign, boolean forceFraction) {
        return getAmountString(locale, amount, justTheNumber, false, true, forceFraction, stripOffSign);
    }

    public static final String getAmountString(Locale locale, Amount amount, boolean justTheNumber, boolean stripOffSign) {
        return getAmountString(locale, amount, justTheNumber, false, true, false, stripOffSign);
    }

    public static final String getAmountString(Locale locale, Amount amount, boolean justTheNumber) {
        return getAmountString(locale, amount, justTheNumber, false, true, false, false);
    }

    public static final String getAmountString(Locale locale, Amount amount) {
        return getAmountString(locale, amount, false);
    }
}
