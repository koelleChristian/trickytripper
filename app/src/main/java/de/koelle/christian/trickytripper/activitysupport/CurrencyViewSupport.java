package de.koelle.christian.trickytripper.activitysupport;

import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

import android.content.res.Resources;
import de.koelle.christian.common.utils.CurrencyUtil;
import de.koelle.christian.trickytripper.ui.model.RowObject;
import de.koelle.christian.trickytripper.ui.model.RowObjectCallback;

public class CurrencyViewSupport {
    @SuppressWarnings("rawtypes")
    public static List<RowObject> wrapCurrenciesInRowObject(List<Currency> supportedCurrencies,
            final Resources resources) {
        List<RowObject> result = new ArrayList<>();

        for (final Currency c : supportedCurrencies) {
            result.add(new RowObject<>(new RowObjectCallback<Currency>() {
                public String getStringToDisplay(Currency c) {
                    return CurrencyUtil.getFullNameToCurrency(resources, c);
                }
            }, c));
        }
        return result;
    }
}
