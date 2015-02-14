package de.koelle.christian.trickytripper.model.modelAdapter;

import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import de.koelle.christian.common.utils.UiUtils;
import de.koelle.christian.trickytripper.R;
import de.koelle.christian.trickytripper.modelutils.AmountViewUtils;
import de.koelle.christian.trickytripper.ui.model.ParticipantRow;

public class ParticipantRowListAdapter extends ArrayAdapter<ParticipantRow> {

    private final List<ParticipantRow> rows;
    private final Context context;

    public ParticipantRowListAdapter(Context context, int textViewResourceId, List<ParticipantRow> objects) {
        super(context, textViewResourceId, objects);
        this.rows = objects;
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View result = convertView;

        if (result == null) {
            LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            result = vi.inflate(R.layout.participant_tab_row_view, null);
        }

        ParticipantRow row = rows.get(position);
        if (row != null) {

            boolean inactive = isInActive(row);

            Locale locale = context.getResources().getConfiguration().locale;

            TextView textView;
            int viewId;
            Object label;
            Object value;

            viewId = R.id.participantTabRowView_output_participantName;
            label = null;

            value = row.getParticipant().getName();
            textView = UiUtils.setLabelAndValueOnTextView(result, viewId, label, value);
            if (textView != null) {
                UiUtils.setFontAndStyle(this.getContext(), textView, inactive, android.R.style.TextAppearance_Medium);
            }

            viewId = R.id.participantTabRowView_output_Balance;
            label = null;
            value = AmountViewUtils.getAmountString(locale, row.getBalance(), true, false, true);
            textView = UiUtils.setLabelAndValueOnTextView(result, viewId, label, value);
            if (textView != null) {
                UiUtils.setFontAndStyle(this.getContext(), textView, inactive, android.R.style.TextAppearance_Medium);
                if (!inactive) {
                    textView.setTextColor(AmountViewUtils.getColor(context, row.getBalance()));
                }
            }

            viewId = R.id.participant_tab_row_view_output_paid;
            label = null;
            value = AmountViewUtils.getAmountString(locale, row.getSumPaid(), true, true, true);
            UiUtils.setLabelAndValueOnTextView(result, viewId, label, value);

            viewId = R.id.participant_tab_row_view_output_spent;
            label = null;
            value = AmountViewUtils.getAmountString(locale, row.getSumSpent(), true, true, true);
            UiUtils.setLabelAndValueOnTextView(result, viewId, label, value);

        }
        return result;
    }

    private boolean isInActive(ParticipantRow row) {
        return !row.getParticipant().isActive();
    }

}
