package de.koelle.christian.trickytripper.export.impl.content;

import java.util.Collection;
import java.util.Locale;
import java.util.Map.Entry;

import de.koelle.christian.trickytripper.R;
import de.koelle.christian.trickytripper.decoupling.ResourceResolver;
import de.koelle.christian.trickytripper.export.impl.ExportCharResolver;
import de.koelle.christian.trickytripper.export.impl.StyleClass;
import de.koelle.christian.trickytripper.model.Amount;
import de.koelle.christian.trickytripper.model.Debts;
import de.koelle.christian.trickytripper.model.Participant;
import de.koelle.christian.trickytripper.model.Trip;
import de.koelle.christian.trickytripper.modelutils.CurrencyViewUtils;

public class DebtTableExporter {

    private ExportCharResolver charResolver;

    public StringBuilder prepareContents(Trip trip, ResourceResolver resourceResolver,
            Collection<Participant> participants) {
        StringBuilder resultBuilder = new StringBuilder();

        Locale locale = resourceResolver.getLocale();

        resultBuilder.append(charResolver.getTablePrefix());
        resultBuilder.append(charResolver.getRowStartDelimiter(StyleClass.HEADING));

        resultBuilder.append(charResolver.translateValue(resourceResolver
                .resolve(R.string.fileExportOwingDebtsHeadingDebtor)));
        resultBuilder.append(charResolver.getColumnDelimiter(StyleClass.HEADING));
        resultBuilder.append(charResolver.translateValue(resourceResolver
                .resolve(R.string.fileExportOwingDebtsHeadingCreditor)));
        resultBuilder.append(charResolver.getColumnDelimiter(StyleClass.HEADING));
        resultBuilder.append(charResolver.translateValue(resourceResolver.resolve(
                R.string.fileExportOwingDebtsHeadingAmount) +
                " " +
                CurrencyViewUtils.getCurrencyCodeInBrackets(trip.getBaseCurrency())));
        resultBuilder.append(charResolver.getRowEndDelimiter());

        for (Entry<Participant, Debts> entry : trip.getDebts().entrySet()) {
            Debts debts = entry.getValue();
            if (debts != null && debts.getLoanerToDebts() != null) {
                for (Entry<Participant, Amount> debt : debts.getLoanerToDebts().entrySet()) {
                    if (isInReportScope(participants, entry, debt)) {
                        resultBuilder.append(charResolver.getRowStartDelimiter());
                        resultBuilder.append(charResolver.translateValue(entry.getKey().getName()));
                        resultBuilder.append(charResolver.getColumnDelimiter());
                        resultBuilder.append(charResolver.translateValue(debt.getKey().getName()));
                        resultBuilder.append(charResolver.getColumnDelimiter(StyleClass.NUMERIC_VALUE));
                        resultBuilder.append(TableExporterUtils.getAmount(locale, debt.getValue()));
                        resultBuilder.append(charResolver.getRowEndDelimiter());
                    }
                }
            }
        }
        resultBuilder.append(charResolver.getTablePostfix());

        return resultBuilder;
    }

    private boolean isInReportScope(Collection<Participant> participants, Entry<Participant, Debts> entry,
            Entry<Participant, Amount> debt) {
        return participants.contains(entry.getKey()) || participants.contains(debt.getKey());
    }

    public void setCharResolver(ExportCharResolver charResolver) {
        this.charResolver = charResolver;
    }

}
