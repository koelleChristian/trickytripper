package de.koelle.christian.trickytripper.model.modelAdapter;

import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import de.koelle.christian.common.utils.DateUtils;
import de.koelle.christian.common.utils.UiUtils;
import de.koelle.christian.trickytripper.R;
import de.koelle.christian.trickytripper.model.ExchangeRate;
import de.koelle.christian.trickytripper.model.ImportOrigin;
import de.koelle.christian.trickytripper.ui.model.ParticipantRow;

public class ExchangeRateRowListAdapter extends ArrayAdapter<ExchangeRate> {

    private final List<ExchangeRate> rows;
    private final Context context;

    public ExchangeRateRowListAdapter(Context context, int textViewResourceId, List<ExchangeRate> objects) {
        super(context, textViewResourceId, objects);
        this.rows = objects;
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View result = convertView;

        if (result == null) {
            LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            result = vi.inflate(R.layout.manage_exchange_rate_row_view, null);
        }

        ExchangeRate row = rows.get(position);
        if (row != null) {

            int viewId;
            Object label;
            Object value;

            viewId = R.id.manageExchangeRateRowView_output_from;
            label = null;
            value = row.getCurrencyFrom().getCurrencyCode();
            UiUtils.setLabelAndValueOnTextView(result, viewId, label, value);

            viewId = R.id.manageExchangeRateRowView_output_to;
            label = null;
            value = row.getCurrencyTo().getCurrencyCode();
            UiUtils.setLabelAndValueOnTextView(result, viewId, label, value);

            viewId = R.id.manageExchangeRateRowView_output_direction_indicator;
            label = null;
            value = " > ";
            UiUtils.setLabelAndValueOnTextView(result, viewId, label, value);

            viewId = R.id.manageExchangeRateRowView_output_main_rate;
            label = null;
            value = row.getExchangeRate();
            UiUtils.setLabelAndValueOnTextView(result, viewId, label, value);

            viewId = R.id.manageExchangeRateRowView_output_description;
            label = null;
            value = deriveDescription(row);
            UiUtils.setLabelAndValueOnTextView(result, viewId, label, value);

            viewId = R.id.manageExchangeRateRowView_output_date;
            label = null;
            value = "";// row.getUpdateDate();
            UiUtils.setLabelAndValueOnTextView(result, viewId, label, value);

        }
        return result;
    }

    private String deriveDescription(ExchangeRate row) {
        if (ImportOrigin.GOOGLE.equals(row.getImportOrigin())) {
            return "Imported " + DateUtils.date2String(row.getUpdateDate(), getLocale());
        }
        else {
            return row.getDescription();
        }
    }

    public Locale getLocale() {
        return context.getResources().getConfiguration().locale;
    }

    // private String createCurr2CurrOutputString(ExchangeRate row, boolean
    // invert) {
    // StringBuilder result = new StringBuilder();
    // result
    // .append(row.getCurrencyFrom().getCurrencyCode())
    // .append((invert) ? "<" : ">")
    // .append(row.getCurrencyTo().getCurrencyCode());
    // return result.toString();
    // }

    private boolean isInActive(ParticipantRow row) {
        return !row.getParticipant().isActive();
    }

}
