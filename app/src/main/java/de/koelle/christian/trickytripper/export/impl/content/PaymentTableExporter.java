package de.koelle.christian.trickytripper.export.impl.content;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Set;

import de.koelle.christian.common.utils.DateUtils;
import de.koelle.christian.trickytripper.R;
import de.koelle.christian.trickytripper.constants.Rc;
import de.koelle.christian.trickytripper.decoupling.ResourceResolver;
import de.koelle.christian.trickytripper.export.impl.ExportCharResolver;
import de.koelle.christian.trickytripper.export.impl.StyleClass;
import de.koelle.christian.trickytripper.factories.AmountFactory;
import de.koelle.christian.trickytripper.model.Amount;
import de.koelle.christian.trickytripper.model.Participant;
import de.koelle.christian.trickytripper.model.Payment;
import de.koelle.christian.trickytripper.model.Trip;
import de.koelle.christian.trickytripper.model.utils.PaymentComparator;
import de.koelle.christian.trickytripper.modelutils.CurrencyViewUtils;

public class PaymentTableExporter {

    private ExportCharResolver charResolver;
    private final Comparator<Payment> paymentComparator = new PaymentComparator();

    public StringBuilder prepareContents(Trip trip, ResourceResolver resourceResolver,
            Collection<Participant> participants, AmountFactory amountFactory) {
        StringBuilder resultBuilder = new StringBuilder();

        Locale locale = resourceResolver.getLocale();

        resultBuilder.append(charResolver.getTablePrefix());

        List<Payment> relevantPayments = deriveRelevantPayments(trip, participants);
        List<Participant> relevantPayers = new ArrayList<Participant>();
        List<Participant> relevantPayees = new ArrayList<Participant>();

        fillAndSortRelevantParticipants(relevantPayers, relevantPayees, relevantPayments,
                createComparator(resourceResolver));

        String currencyCodeInBrackets = " " + CurrencyViewUtils.getCurrencyCodeInBrackets(trip.getBaseCurrency());

        /* ############################ Line 1 ######################## */
        resultBuilder.append(charResolver.getRowStartDelimiter(StyleClass.HEADING));

        resultBuilder.append(charResolver.translateValue(resourceResolver
                .resolve(R.string.fileExportPaymentsHeadingPaymentDateTime)));
        resultBuilder.append(charResolver.getColumnDelimiter(StyleClass.HEADING));
        resultBuilder.append(charResolver.translateValue(resourceResolver
                .resolve(R.string.fileExportPaymentsHeadingPaymentName)));
        resultBuilder.append(charResolver.getColumnDelimiter(StyleClass.HEADING));
        resultBuilder.append(charResolver.translateValue(resourceResolver
                .resolve(R.string.fileExportPaymentsHeadingCategory)));
        resultBuilder.append(charResolver.getColumnDelimiter(StyleClass.HEADING));
        resultBuilder.append(charResolver.translateValue(resourceResolver.resolve(
                R.string.fileExportPaymentsHeadingAmount) + currencyCodeInBrackets));

        /* First heading */
        for (int i = 0; i < relevantPayers.size(); i++) {
            resultBuilder.append(charResolver.getColumnDelimiter(StyleClass.HEADING, StyleClass.BACKGROUND_PAYER));
            if (i == 0) {
                resultBuilder.append(charResolver.translateValue(resourceResolver
                        .resolve(R.string.fileExportPaymentsHeadingPaidBy)));
                resultBuilder.append(charResolver.translateValue(currencyCodeInBrackets));
            }
        }
        for (int i = 0; i < relevantPayees.size(); i++) {
            resultBuilder.append(charResolver.getColumnDelimiter(StyleClass.HEADING, StyleClass.BACKGROUND_SPENDER));
            if (i == 0) {
                resultBuilder.append(charResolver.translateValue(resourceResolver
                        .resolve(R.string.fileExportPaymentsHeadingDebitedTo)));
                resultBuilder.append(charResolver.translateValue(currencyCodeInBrackets));
            }
        }
        resultBuilder.append(charResolver.getRowEndDelimiter());

        /* ############################ Line 2: Names ######################## */
        resultBuilder.append(charResolver.getRowStartDelimiter(StyleClass.HEADING));
        resultBuilder.append(charResolver.getColumnDelimiter(StyleClass.HEADING));
        resultBuilder.append(charResolver.getColumnDelimiter(StyleClass.HEADING));
        resultBuilder.append(charResolver.getColumnDelimiter(StyleClass.HEADING));

        for (int i = 0; i < relevantPayers.size(); i++) {
            resultBuilder.append(charResolver.getColumnDelimiter(StyleClass.HEADING, StyleClass.BACKGROUND_PAYER));
            resultBuilder.append(charResolver.translateValue(relevantPayers.get(i).getName()));
        }
        for (int i = 0; i < relevantPayees.size(); i++) {
            resultBuilder.append(charResolver.getColumnDelimiter(StyleClass.HEADING, StyleClass.BACKGROUND_SPENDER));
            resultBuilder.append(charResolver.translateValue(relevantPayees.get(i).getName()));
        }
        resultBuilder.append(charResolver.getRowEndDelimiter());

        /* ############################ Line ff values ######################## */

        Collections.sort(relevantPayments, paymentComparator);

        for (Payment p : relevantPayments) {
            resultBuilder.append(charResolver.getRowStartDelimiter());
            resultBuilder.append(charResolver.translateValue(new DateUtils(locale).date2String(p.getPaymentDateTime())));
            resultBuilder.append(charResolver.getColumnDelimiter());
            resultBuilder.append(charResolver.translateValue(p.getDescription()));
            resultBuilder.append(charResolver.getColumnDelimiter());
            resultBuilder.append(charResolver.translateValue(resourceResolver.resolve(p.getCategory()
                    .getResourceStringId())));
            resultBuilder.append(charResolver.getColumnDelimiter(StyleClass.NUMERIC_VALUE));
            Amount totalAmount = amountFactory.createAmount();
            p.getTotalAmount(totalAmount);
            resultBuilder.append(charResolver.translateValue(TableExporterUtils.getAmount(locale,
                    totalAmount)));

            Amount value;

            for (int i = 0; i < relevantPayers.size(); i++) {
                resultBuilder.append(charResolver.getColumnDelimiter(StyleClass.NUMERIC_VALUE));
                value = getAmount(relevantPayers.get(i), p.getParticipantToPayment()
                        .entrySet());
                resultBuilder.append(charResolver.translateValue(TableExporterUtils
                        .getAmount(locale, value)));
            }
            for (int i = 0; i < relevantPayees.size(); i++) {
                resultBuilder.append(charResolver.getColumnDelimiter(StyleClass.NUMERIC_VALUE));
                value = getAmount(relevantPayees.get(i), p.getParticipantToSpending()
                        .entrySet());
                resultBuilder.append(charResolver.translateValue(TableExporterUtils
                        .getAmount(locale, value)));
            }
            resultBuilder.append(charResolver.getRowEndDelimiter());
        }

        /* ################################################################## */

        resultBuilder.append(charResolver.getTablePostfix());
        return resultBuilder;
    }

    private Comparator<Participant> createComparator(ResourceResolver resourceResolver) {
        final Collator collator = Collator.getInstance(resourceResolver.getLocale());
        collator.setStrength(Rc.DEFAULT_COLLATOR_STRENGTH);

        return new Comparator<Participant>() {
            public int compare(Participant lhs, Participant rhs) {
                return collator.compare(lhs.getName(), rhs.getName());
            }
        };
    }

    private List<Payment> deriveRelevantPayments(Trip trip, Collection<Participant> participants) {
        List<Payment> result = new ArrayList<Payment>();
        if (participants.size() > 1) {
            for (Payment payment : trip.getPayments()) {
                if (!payment.getCategory().isInternal()) {
                    result.add(payment);
                }
            }

        }
        else {
            Participant p = participants.iterator().next();
            for (Payment payment : trip.getPayments()) {
                if (!payment.getCategory().isInternal()
                        && (TableExporterUtils.partOf(p, payment.getParticipantToPayment().entrySet())
                        || TableExporterUtils.partOf(p, payment.getParticipantToSpending().entrySet()))) {
                    result.add(payment);
                }
            }
        }
        return result;
    }

    private Amount getAmount(Participant p, Set<Entry<Participant, Amount>> entrySet) {
        for (Entry<Participant, Amount> entry : entrySet) {
            if (p.getId() == entry.getKey().getId()) {
                return entry.getValue();
            }
        }
        return null;
    }

    private void fillAndSortRelevantParticipants(List<Participant> payerResult, List<Participant> payeeResult,
            List<Payment> relevantPayments,
            Comparator<Participant> comparator) {
        Set<Participant> payerSet = new HashSet<Participant>();
        Set<Participant> payeeSet = new HashSet<Participant>();
        for (Payment p : relevantPayments) {
            payerSet.addAll(p.getParticipantToPayment().keySet());
            payeeSet.addAll(p.getParticipantToSpending().keySet());
        }
        payerResult.addAll(payerSet);
        payeeResult.addAll(payeeSet);
        Collections.sort(payerResult, comparator);
        Collections.sort(payeeResult, comparator);
    }

    public void setCharResolver(ExportCharResolver charResolver) {
        this.charResolver = charResolver;
    }

}
