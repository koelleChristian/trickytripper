package de.koelle.christian.trickytripper.export.impl.content;

import java.util.Collection;
import java.util.Locale;

import de.koelle.christian.trickytripper.R;
import de.koelle.christian.trickytripper.decoupling.ResourceResolver;
import de.koelle.christian.trickytripper.export.impl.ExportCharResolver;
import de.koelle.christian.trickytripper.export.impl.StyleClass;
import de.koelle.christian.trickytripper.model.Participant;
import de.koelle.christian.trickytripper.model.Payment;
import de.koelle.christian.trickytripper.model.PaymentCategory;
import de.koelle.christian.trickytripper.model.Trip;
import de.koelle.christian.trickytripper.modelutils.CurrencyViewUtils;

public class TransferTableExporter {

    private ExportCharResolver charResolver;

    public StringBuilder prepareContents(Trip trip, ResourceResolver resourceResolver,
            Collection<Participant> participants) {
        StringBuilder resultBuilder = new StringBuilder();

        Locale locale = resourceResolver.getLocale();

        resultBuilder.append(charResolver.getTablePrefix());
        resultBuilder.append(charResolver.getRowStartDelimiter(StyleClass.HEADING));

        resultBuilder.append(charResolver.translateValue(resourceResolver
                .resolve(R.string.fileExportTransferHeadingPayer)));
        resultBuilder.append(charResolver.getColumnDelimiter(StyleClass.HEADING));
        resultBuilder.append(charResolver.translateValue(resourceResolver
                .resolve(R.string.fileExportTransferHeadingReceiver)));
        resultBuilder.append(charResolver.getColumnDelimiter(StyleClass.HEADING));
        resultBuilder.append(charResolver.translateValue(resourceResolver.resolve(
                R.string.fileExportTransferHeadingAmount) +
                " " +
                CurrencyViewUtils.getCurrencyCodeInBrackets(trip.getBaseCurrency())));
        resultBuilder.append(charResolver.getRowEndDelimiter());

        for (Payment payment : trip.getPayments()) {
            if (PaymentCategory.MONEY_TRANSFER.equals(payment.getCategory())
                    && (
                    TableExporterUtils.partOf(participants, payment.getParticipantToPayment().entrySet()) ||
                    TableExporterUtils.partOf(participants, payment.getParticipantToSpending().entrySet()))) {

                resultBuilder.append(charResolver.getRowStartDelimiter());
                resultBuilder.append(charResolver.translateValue(payment.getParticipantToSpending().entrySet()
                        .iterator().next().getKey().getName()));
                resultBuilder.append(charResolver.getColumnDelimiter());
                resultBuilder.append(charResolver.translateValue(payment.getParticipantToPayment().entrySet()
                        .iterator().next().getKey().getName()));
                resultBuilder.append(charResolver.getColumnDelimiter(StyleClass.NUMERIC_VALUE));
                resultBuilder.append(TableExporterUtils.getAmount(locale, payment.getParticipantToSpending()
                        .entrySet()
                        .iterator().next().getValue()));
                resultBuilder.append(charResolver.getRowEndDelimiter());

            }

        }
        resultBuilder.append(charResolver.getTablePostfix());

        return resultBuilder;
    }

    public void setCharResolver(ExportCharResolver charResolver) {
        this.charResolver = charResolver;
    }

}
