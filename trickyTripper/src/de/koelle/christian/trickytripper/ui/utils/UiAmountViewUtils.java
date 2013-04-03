package de.koelle.christian.trickytripper.ui.utils;

import java.util.Locale;

import android.widget.EditText;
import de.koelle.christian.common.ui.filter.DecimalNumberInputUtil;
import de.koelle.christian.trickytripper.model.Amount;
import de.koelle.christian.trickytripper.modelutils.AmountViewUtils;

public class UiAmountViewUtils {

    public static void writeAmountToEditText(Amount amount, EditText editText, Locale locale,
            DecimalNumberInputUtil decimalNumberInputUtil) {
        String amountString = AmountViewUtils.getAmountString(locale, amount, true, true, true, false, true);
        writeValueToEditText(editText, decimalNumberInputUtil, amountString);
    }

    public static void writeDoubleToEditText(Double value, EditText editText, Locale locale,
            DecimalNumberInputUtil decimalNumberInputUtil) {
        String amountString = AmountViewUtils.getDoubleString(locale, value);
        writeValueToEditText(editText, decimalNumberInputUtil, amountString);
    }

    private static void writeValueToEditText(EditText editText, DecimalNumberInputUtil decimalNumberInputUtil,
            String amountString) {
        editText.setText(decimalNumberInputUtil.fixInputStringModelToWidget(amountString));
    }
}
