package de.koelle.christian.trickytripper.activitysupport;

import java.util.Currency;

import android.app.Activity;
import android.content.Intent;
import android.widget.Button;
import de.koelle.christian.trickytripper.constants.Rc;

public class CurrencySelectionResultSupport {
    public static Currency onActivityResult(int requestCode, int resultCode, Intent resultData, Activity activity) {
        if (requestCode == Rc.ACTIVITY_REQ_CODE_CURRENCY_SELECTION
                && resultCode == Activity.RESULT_OK) {
            Currency result = (Currency) resultData.getExtras().get(Rc.ACTIVITY_PARAM_CURRENCY_SELECTION_OUT_CURRENCY);
            int targetViewId = resultData.getIntExtra(Rc.ACTIVITY_PARAM_CURRENCY_SELECTION_OUT_VIEW_ID, -1);
            if (targetViewId >= 0) {
                ((Button) activity.findViewById(targetViewId)).setText(result.getCurrencyCode());
            }
            return result;
        }
        return null;
    }
}
