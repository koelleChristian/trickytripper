package de.koelle.christian.trickytripper.activitysupport;

import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.widget.EditText;
import de.koelle.christian.common.ui.filter.DecimalNumberInputUtil;
import de.koelle.christian.trickytripper.constants.Rc;
import de.koelle.christian.trickytripper.model.Amount;
import de.koelle.christian.trickytripper.ui.utils.UiAmountViewUtils;

public class CurrencyCalculatorResultSupport {

    public static void onActivityResult(int requestCode, int resultCode, Intent resultData, Activity activity,
            Locale locale, DecimalNumberInputUtil decimalNumberInputUtil) {
        if (requestCode == Rc.ACTIVITY_PARAM_CURRENCY_CALCULATOR_REQUEST_CODE
                && resultCode == Activity.RESULT_OK) {
            Amount resultAmount = (Amount) resultData.getExtras().get(Rc.ACTIVITY_PARAM_CURRENCY_CALCULATOR_OUT_AMOUNT);
            int targetViewId = resultData.getIntExtra(Rc.ACTIVITY_PARAM_CURRENCY_CALCULATOR_OUT_VIEW_ID, -1);
            if (targetViewId >= 0) {
                EditText targetEditText = (EditText) activity.findViewById(targetViewId);
                UiAmountViewUtils.writeAmountToEditText(resultAmount, targetEditText, locale, decimalNumberInputUtil);
            }
        }
    }

}
