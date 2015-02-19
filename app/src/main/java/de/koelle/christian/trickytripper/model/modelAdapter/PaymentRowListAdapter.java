package de.koelle.christian.trickytripper.model.modelAdapter;

import java.text.Collator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.TreeMap;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import de.koelle.christian.common.utils.DateUtils;
import de.koelle.christian.common.utils.UiUtils;
import de.koelle.christian.trickytripper.R;
import de.koelle.christian.trickytripper.factories.AmountFactory;
import de.koelle.christian.trickytripper.model.Amount;
import de.koelle.christian.trickytripper.model.Participant;
import de.koelle.christian.trickytripper.model.Payment;
import de.koelle.christian.trickytripper.modelutils.AmountViewUtils;

public class PaymentRowListAdapter extends ArrayAdapter<Payment> {

    private final List<Payment> rows;
    private final Context context;
    private final AmountFactory amountFactory;
    private final Locale locale;
    private final Collator collator;
    private DateUtils dateUtils;


    public PaymentRowListAdapter(Context context, int textViewResourceId, List<Payment> objects,
            AmountFactory amountFactory, Collator collator) {
        super(context, textViewResourceId, objects);
        this.rows = objects;
        this.context = context;
        this.amountFactory = amountFactory;
        this.locale = context.getResources().getConfiguration().locale;
        this.collator = collator;
        this.dateUtils = new DateUtils(locale);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View result = convertView;

        if (result == null) {
            LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            result = vi.inflate(R.layout.payment_tab_row_view, null);
        }

        Payment row = rows.get(position);
        if (row != null) {

            int viewId;
            Object value;

            if (!row.getCategory().isInternal()) {
                PaymentInlineReport report = createInlineReport(row);

                UiUtils.removeFromView(result, R.id.paymentTabRowView_label_transfer_from);
                UiUtils.removeFromView(result, R.id.paymentTabRowView_output_transfer_from);
                UiUtils.removeFromView(result, R.id.paymentTabRowView_label_transfer_to);
                UiUtils.removeFromView(result, R.id.paymentTabRowView_output_transfer_to);

                UiUtils.showInView(result, R.id.paymentTabRowView_date_label);
                UiUtils.showInView(result, R.id.paymentTabRowView_output_date_time);
                UiUtils.showInView(result, R.id.paymentTabRowView_category_label);
                UiUtils.showInView(result, R.id.paymentTabRowView_output_category);
                UiUtils.showInView(result, R.id.paymentTabRowView_label_payed_by);
                UiUtils.showInView(result, R.id.paymentTabRowView_output_payed_by);
                UiUtils.showInView(result, R.id.paymentTabRowView_label_debited_to);
                UiUtils.showInView(result, R.id.paymentTabRowView_output_debited_to);




                viewId = R.id.paymentTabRowView_output_payment_amount;
                value = report.getTotal();
                UiUtils.setLabelAndValueOnTextView(result, viewId, null, value);

                viewId = R.id.paymentTabRowView_output_payment_name;
                value = (row.getDescription() == null) ? " " : row.getDescription();
                UiUtils.setLabelAndValueOnTextView(result, viewId, null, value);

                viewId = R.id.paymentTabRowView_output_category;
                value = (row.getCategory() != null) ? context.getResources().getString(
                        row.getCategory().getResourceStringId()) : "";
                UiUtils.setLabelAndValueOnTextView(result, viewId, null, value);

                viewId = R.id.paymentTabRowView_output_debited_to;
                value = report.getDebitedTo();
                UiUtils.setLabelAndValueOnTextView(result, viewId, null, value);

                viewId = R.id.paymentTabRowView_output_payed_by;
                value = report.getPayedBy();
                UiUtils.setLabelAndValueOnTextView(result, viewId, null, value);

            }
            else {
                UiUtils.showInView(result, R.id.paymentTabRowView_label_transfer_from);
                UiUtils.showInView(result, R.id.paymentTabRowView_output_transfer_from);
                UiUtils.showInView(result, R.id.paymentTabRowView_label_transfer_to);
                UiUtils.showInView(result, R.id.paymentTabRowView_output_transfer_to);


                UiUtils.showInView(result, R.id.paymentTabRowView_date_label);
                UiUtils.showInView(result, R.id.paymentTabRowView_output_date_time);

                UiUtils.removeFromView(result, R.id.paymentTabRowView_category_label);
                UiUtils.removeFromView(result, R.id.paymentTabRowView_output_category);
                UiUtils.removeFromView(result, R.id.paymentTabRowView_label_payed_by);
                UiUtils.removeFromView(result, R.id.paymentTabRowView_output_payed_by);
                UiUtils.removeFromView(result, R.id.paymentTabRowView_label_debited_to);
                UiUtils.removeFromView(result, R.id.paymentTabRowView_output_debited_to);

                viewId = R.id.paymentTabRowView_output_payment_name;
                value = (row.getCategory() != null) ? context.getResources().getString(
                        row.getCategory().getResourceStringId()) : "";
                UiUtils.setLabelAndValueOnTextView(result, viewId, null, value);

                viewId = R.id.paymentTabRowView_output_transfer_to;
                value = row.getParticipantToPayment().entrySet().iterator().next().getKey().getName();
                UiUtils.setLabelAndValueOnTextView(result, viewId, null, value);

                Entry<Participant, Amount> transfererEntry = row.getParticipantToSpending().entrySet().iterator()
                        .next();

                viewId = R.id.paymentTabRowView_output_transfer_from;
                value = transfererEntry.getKey().getName();
                UiUtils.setLabelAndValueOnTextView(result, viewId, null, value);

                viewId = R.id.paymentTabRowView_output_payment_amount;
                value = AmountViewUtils.getAmountString(locale, transfererEntry.getValue(), true, true, true);
                UiUtils.setLabelAndValueOnTextView(result, viewId, null, value);
            }

            viewId = R.id.paymentTabRowView_output_date_time;
            value = dateUtils.date2String(row.getPaymentDateTime());
            UiUtils.setLabelAndValueOnTextView(result, viewId, null, value);
        }
        return result;
    }

    private PaymentInlineReport createInlineReport(Payment row) {
        PaymentInlineReport result = new PaymentInlineReport();

        Amount amountTotal = amountFactory.createAmount();
        StringBuilder builderPayments = new StringBuilder();
        StringBuilder builderDebitedTo = new StringBuilder();

        TreeMap<String, Amount> payments = new TreeMap<String, Amount>(collator);
        TreeMap<String, Amount> debitedTo = new TreeMap<String, Amount>(collator);

        for (Entry<Participant, Amount> entry : row.getParticipantToPayment().entrySet()) {
            payments.put(entry.getKey().getName(), entry.getValue());
        }
        for (Entry<Participant, Amount> entry : row.getParticipantToSpending().entrySet()) {
            debitedTo.put(entry.getKey().getName(), entry.getValue());
        }

        for (Iterator<Entry<String, Amount>> i = payments.entrySet().iterator(); i.hasNext();) {
            Entry<String, Amount> payment = i.next();
            amountTotal.addAmount(payment.getValue());
            appendUserAndAmount(locale, builderPayments, i, payment);
        }
        for (Iterator<Entry<String, Amount>> i = debitedTo.entrySet().iterator(); i.hasNext();) {
            Entry<String, Amount> payment = i.next();
            appendUserAndAmount(locale, builderDebitedTo, i, payment);
        }
        result.setTotal(AmountViewUtils.getAmountString(locale, amountTotal, true, true, true));
        result.setDebitedTo(builderDebitedTo.toString());
        result.setPayedBy(builderPayments.toString());
        return result;
    }

    private void appendUserAndAmount(Locale locale, StringBuilder builderPayments,
            Iterator<Entry<String, Amount>> i, Entry<String, Amount> payment) {
        builderPayments.append(payment.getKey());
        builderPayments.append(" ");
        builderPayments.append(AmountViewUtils.getAmountString(locale, payment.getValue(), true, true, true));
        if (i.hasNext()) {
            builderPayments.append("|");
        }
    }

}
