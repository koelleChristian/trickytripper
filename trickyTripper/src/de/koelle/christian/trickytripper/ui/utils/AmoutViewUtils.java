package de.koelle.christian.trickytripper.ui.utils;

import java.util.Locale;

import android.widget.EditText;
import de.koelle.christian.common.ui.filter.DecimalNumberInputUtil;
import de.koelle.christian.trickytripper.model.Amount;
import de.koelle.christian.trickytripper.modelutils.AmountViewUtils;

public class AmoutViewUtils {

    public static void writeAmountToEditText(Amount amount, EditText editText, Locale locale,
            DecimalNumberInputUtil decimalNumberInputUtil) {
        editText.setText(decimalNumberInputUtil.fixInputStringModelToWidget(
                AmountViewUtils.getAmountString(locale, amount, true, true, true, false, true)));
    }

}
