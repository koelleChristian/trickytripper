package de.koelle.christian.trickytripper.model.modelAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;

import java.util.List;

import de.koelle.christian.common.utils.UiUtils;
import de.koelle.christian.trickytripper.R;
import de.koelle.christian.trickytripper.model.ExchangeRate;
import de.koelle.christian.trickytripper.modelutils.AmountViewUtils;
import de.koelle.christian.trickytripper.ui.utils.ExchangeRateDescriptionUtils;

public class ExchangeRateRowListAdapter extends ArrayAdapter<ExchangeRate> {

    public enum DisplayMode {
        SINGLE,
        DOUBLE_WITH_SELECTION
    }

    private final List<ExchangeRate> rows;
    private final Context context;
    private final DisplayMode mode;
    private final ExchangeRateDescriptionUtils exchangeRateDescUtils;

    public ExchangeRateRowListAdapter(Context context, int textViewResourceId, List<ExchangeRate> objects,
                                      DisplayMode mode) {
        super(context, textViewResourceId, objects);
        this.rows = objects;
        this.context = context;
        this.mode = mode;
        this.exchangeRateDescUtils = new ExchangeRateDescriptionUtils(context.getResources());
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View result;

        boolean isDouble = DisplayMode.DOUBLE_WITH_SELECTION.equals(mode);

        int manageExchangeRateRowView = isDouble ?
                R.layout.exchange_rate_delete_row_view :
                R.layout.exchange_rate_manage_row_view;

        LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        result = vi.inflate(manageExchangeRateRowView, null);

        ExchangeRate row = rows.get(position);
        if (row != null) {

            int viewId;
            Object label;
            Object value;

            viewId = R.id.exchangeRateRowView_output_from;
            label = null;
            value = row.getCurrencyFrom().getCurrencyCode();
            UiUtils.setLabelAndValueOnTextView(result, viewId, label, value);

            viewId = R.id.exchangeRateRowView_output_to;
            label = null;
            value = row.getCurrencyTo().getCurrencyCode();
            UiUtils.setLabelAndValueOnTextView(result, viewId, label, value);

            viewId = R.id.exchangeRateRowView_output_direction_indicator;
            label = null;
            value = " > ";
            UiUtils.setLabelAndValueOnTextView(result, viewId, label, value);

            viewId = R.id.exchangeRateRowView_output_main_rate;
            label = null;
            value = AmountViewUtils.getDoubleString(context.getResources().getConfiguration().locale, row.getExchangeRate());
            UiUtils.setLabelAndValueOnTextView(result, viewId, label, value);

            viewId = R.id.exchangeRateRowView_output_comment;
            label = null;
            value = exchangeRateDescUtils.deriveDescription(row);
            UiUtils.setLabelAndValueOnTextView(result, viewId, label, value);

            if (isDouble) {
                viewId = R.id.exchangeRateRowView_checkbox;

                CheckBox checkbox = (CheckBox) result.findViewById(viewId);
                checkbox.setChecked(row.isSelected());

                ExchangeRate rowInverted = row.cloneToInversion();

                viewId = R.id.exchangeRateRowView_output_from2;
                label = null;
                value = rowInverted.getCurrencyFrom().getCurrencyCode();
                UiUtils.setLabelAndValueOnTextView(result, viewId, label, value);

                viewId = R.id.exchangeRateRowView_output_to2;
                label = null;
                value = rowInverted.getCurrencyTo().getCurrencyCode();
                UiUtils.setLabelAndValueOnTextView(result, viewId, label, value);

                viewId = R.id.exchangeRateRowView_output_direction_indicator2;
                label = null;
                value = " > ";
                UiUtils.setLabelAndValueOnTextView(result, viewId, label, value);

                viewId = R.id.exchangeRateRowView_output_main_rate2;
                label = null;
                value = AmountViewUtils.getDoubleString(context.getResources().getConfiguration().locale, rowInverted.getExchangeRate());
                UiUtils.setLabelAndValueOnTextView(result, viewId, label, value);

            }

        }
        return result;
    }
}
