package de.koelle.christian.common.currencyspinner;

import java.util.Currency;

import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Spinner;
import de.koelle.christian.trickytripper.ui.model.RowObject;

public class CurrencySpinnerSelectionListener implements OnItemSelectedListener {
    private final Spinner spinner;
    private SpinnerCurrencySelectionCallback spinnerCurrencySelectionCallback;

    public CurrencySpinnerSelectionListener(Spinner spinner) {
        this.spinner = spinner;
    }

    @SuppressWarnings("unchecked")
    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
        if (position >= 0 && spinnerCurrencySelectionCallback != null) {
            Object o = spinner.getSelectedItem();
            Currency selectedCurrency = ((RowObject<Currency>) o).getRowObject();
            spinnerCurrencySelectionCallback.setSelection(selectedCurrency);
        }
    }

    public void onNothingSelected(AdapterView<?> parentView) {
        // intentionally blank
    }

    public SpinnerCurrencySelectionCallback getSpinnerCurrencySelectionCallback() {
        return spinnerCurrencySelectionCallback;
    }

    public void setSpinnerCurrencySelectionCallback(SpinnerCurrencySelectionCallback spinnerCurrencySelectionCallback) {
        this.spinnerCurrencySelectionCallback = spinnerCurrencySelectionCallback;
    }

}