package de.koelle.christian.trickytripper.model.modelAdapter;

import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import de.koelle.christian.common.utils.DateUtils;
import de.koelle.christian.common.utils.UiUtils;
import de.koelle.christian.trickytripper.R;
import de.koelle.christian.trickytripper.model.ExchangeRate;

public class ExchangeRateRowListAdapter extends ArrayAdapter<ExchangeRate> {

    public enum DisplayMode {
        SINGLE,
        DOUBLE_WITH_SELECTION;
    }

    private final List<ExchangeRate> rows;
    private final Context context;
    private final DateUtils dateUtils;
    private final DisplayMode mode;

    public ExchangeRateRowListAdapter(Context context, int textViewResourceId, List<ExchangeRate> objects,
            DisplayMode mode) {
        super(context, textViewResourceId, objects);
        this.rows = objects;
        this.context = context;
        dateUtils = new DateUtils(context.getResources().getConfiguration().locale);
        this.mode = mode;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View result = convertView;

        boolean isDouble = DisplayMode.DOUBLE_WITH_SELECTION.equals(mode);

        // if (result == null) {
        int manageExchangeRateRowView = isDouble ?
                R.layout.exchange_rate_delete_row_view :
                R.layout.exchange_rate_manage_row_view;

        LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        result = vi.inflate(manageExchangeRateRowView, null);
        // }

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
            value = row.getExchangeRate();
            UiUtils.setLabelAndValueOnTextView(result, viewId, label, value);

            viewId = R.id.exchangeRateRowView_output_comment;
            label = null;
            value = deriveDescription(row);
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
                value = rowInverted.getExchangeRate();
                UiUtils.setLabelAndValueOnTextView(result, viewId, label, value);

            }

        }
        return result;
    }

    private String deriveDescription(ExchangeRate row) {
        if (row.isImported()) {
            return "Imported " + dateUtils.date2String(row.getUpdateDate());
        }
        else {
            return row.getDescription();
        }
    }

    public Locale getLocale() {
        return context.getResources().getConfiguration().locale;
    }

}
