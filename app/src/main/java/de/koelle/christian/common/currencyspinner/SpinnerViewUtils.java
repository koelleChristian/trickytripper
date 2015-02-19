package de.koelle.christian.common.currencyspinner;

import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import de.koelle.christian.common.utils.CurrencyUtil;
import de.koelle.christian.trickytripper.R;
import de.koelle.christian.trickytripper.activitysupport.SpinnerViewSupport;
import de.koelle.christian.trickytripper.ui.model.RowObject;
import de.koelle.christian.trickytripper.ui.model.RowObjectCallback;

public class SpinnerViewUtils {

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static void initCurrencySpinner(Currency currencyToBeExcluded,
            Currency currencyToBeSelected,
            final Spinner spinner, Context context) {

        List<Currency> supportedCurrencies = CurrencyUtil.getSupportedCurrencies(context.getResources());
        final List<RowObject> spinnerObjects = wrapCurrenciesInRowObject(supportedCurrencies, currencyToBeExcluded,
                context);

        ArrayAdapter<RowObject> adapterInUse = (ArrayAdapter<RowObject>) spinner.getAdapter();

        if (adapterInUse == null) {
            ArrayAdapter<RowObject> adapter = new ArrayAdapter<RowObject>(context,
                    android.R.layout.simple_spinner_item,
                    spinnerObjects) {

                @Override
                public View getDropDownView(int position, View convertView, ViewGroup parent) {
                    /* This is the default for the list view. */
                    return super.getDropDownView(position, convertView, parent);
                }

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    /* Display currency code only when not in list view. */
                    TextView result = (TextView) super.getView(position, convertView, parent);
                    result.setText(((Currency) getItem(position).getRowObject()).getCurrencyCode());
                    return result;
                }
            };
            adapter.setDropDownViewResource(R.layout.selection_list_medium);
            spinner.setPromptId(R.string.payment_view_spinner_prompt);
            spinner.setAdapter(adapter);
            adapterInUse = adapter;
        }
        else {
            adapterInUse.clear();
            for (RowObject o : spinnerObjects) {
                adapterInUse.add(o);
            }
        }

        Currency initialSelection2Be = (currencyToBeSelected == null) ? (Currency) spinnerObjects.get(0)
                .getRowObject() : currencyToBeSelected;

        SpinnerViewSupport.setSelection(spinner, initialSelection2Be, adapterInUse);

    }

    @SuppressWarnings("rawtypes")
    private static List<RowObject> wrapCurrenciesInRowObject(List<Currency> supportedCurrencies, Currency exclusion,
            final Context context) {
        List<RowObject> result = new ArrayList<RowObject>();

        for (final Currency c : supportedCurrencies) {
            if (notExcluded(exclusion, c)) {
                result.add(new RowObject<Currency>(new RowObjectCallback<Currency>() {
                    public String getStringToDisplay(Currency c) {
                        /*
                         * This is the long description intended for the list
                         * view.
                         */
                        return CurrencyUtil.getFullNameToCurrency(context.getResources(), c);
                    }
                }, c));
            }
        }
        return result;
    }

    private static boolean notExcluded(Currency exclusion, final Currency c) {
        return exclusion == null || !exclusion.equals(c);
    }
}
