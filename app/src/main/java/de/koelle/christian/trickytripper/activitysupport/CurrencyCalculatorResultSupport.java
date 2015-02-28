package de.koelle.christian.trickytripper.activitysupport;

import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.widget.EditText;
import android.widget.Toast;
import de.koelle.christian.common.ui.filter.DecimalNumberInputUtil;
import de.koelle.christian.common.utils.NumberUtils;
import de.koelle.christian.trickytripper.R;
import de.koelle.christian.trickytripper.constants.Rc;
import de.koelle.christian.trickytripper.model.Amount;
import de.koelle.christian.trickytripper.ui.utils.UiAmountViewUtils;

public class CurrencyCalculatorResultSupport {

    public static void onActivityResult(int requestCode, int resultCode, Intent resultData, Activity activity,
            Locale locale, DecimalNumberInputUtil decimalNumberInputUtil) {
        if (requestCode == Rc.ACTIVITY_REQ_CODE_CURRENCY_CALCULATOR
                && resultCode == Activity.RESULT_OK) {
            Amount resultAmount = (Amount) resultData.getExtras().get(Rc.ACTIVITY_PARAM_CURRENCY_CALCULATOR_OUT_AMOUNT);
            int targetViewId = resultData.getIntExtra(Rc.ACTIVITY_PARAM_CURRENCY_CALCULATOR_OUT_VIEW_ID, -1);
            if (targetViewId >= 0) {
                if(NumberUtils.isExceedingAmountLimit(resultAmount.getValue())){
                    Toast.makeText(activity, activity.getResources().getString(R.string.currencyCalculatorViewToastResultExceedsLimit), Toast.LENGTH_SHORT).show();
                } else{                    
                    EditText targetEditText = (EditText) activity.findViewById(targetViewId);
                    UiAmountViewUtils.writeAmountToEditText(resultAmount, targetEditText, locale, decimalNumberInputUtil);
                }
                
            }
        }
    }

}
