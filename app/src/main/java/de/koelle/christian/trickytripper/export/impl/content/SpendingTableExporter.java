package de.koelle.christian.trickytripper.export.impl.content;

import java.text.Collator;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import de.koelle.christian.trickytripper.R;
import de.koelle.christian.trickytripper.constants.Rc;
import de.koelle.christian.trickytripper.decoupling.ResourceResolver;
import de.koelle.christian.trickytripper.export.impl.ExportCharResolver;
import de.koelle.christian.trickytripper.export.impl.StyleClass;
import de.koelle.christian.trickytripper.model.Amount;
import de.koelle.christian.trickytripper.model.Participant;
import de.koelle.christian.trickytripper.model.PaymentCategory;
import de.koelle.christian.trickytripper.model.Trip;
import de.koelle.christian.trickytripper.modelutils.CurrencyViewUtils;
import de.koelle.christian.trickytripper.strategies.SumReport;

public class SpendingTableExporter {

    private ExportCharResolver charResolver;

    public StringBuilder prepareContents(Trip trip, final ResourceResolver resourceResolver,
            List<Participant> participants, boolean hideColumnForEntireTrip, boolean ensureTypeConsistency) {

        StringBuilder writer = new StringBuilder();

        Locale locale = resourceResolver.getLocale();

        String currencyCodeInBrackets = " " + CurrencyViewUtils.getCurrencyCodeInBrackets(trip.getBaseCurrency());

        SumReport report = trip.getSumReport();

        writer.append(charResolver.getTablePrefix());

        /* First row */

        writer.append(charResolver.getRowStartDelimiter(StyleClass.HEADING));

        writer.append(charResolver.translateValue(resourceResolver
                .resolve(R.string.fileExportSpendingsHeadingSpendingGlobal)
                + currencyCodeInBrackets));
        writer.append(charResolver.getColumnDelimiter(StyleClass.HEADING));
        if (!hideColumnForEntireTrip) {
            writer.append(charResolver.getColumnDelimiter(StyleClass.HEADING));
            writer.append(charResolver.translateValue(resourceResolver.resolve(R.string.fileExportSpendingsHeadingAll)));
        }
        for (Participant p : participants) {
            writer.append(charResolver.getColumnDelimiter(StyleClass.HEADING));
            writer.append(charResolver.translateValue(p.getName()));
        }
        writer.append(charResolver.getRowEndDelimiter());

        /* Spending sum */

        writer.append(charResolver.getRowStartDelimiter(StyleClass.HEADING));
        writer.append(charResolver.getColumnDelimiter(StyleClass.HEADING));
        writer.append(charResolver.translateValue(resourceResolver
                .resolve(R.string.fileExportSpendingsHeadingTotalSpent)));
        if (!hideColumnForEntireTrip) {
            writer.append(charResolver.getColumnDelimiter(StyleClass.NUMERIC_VALUE));
            writer.append(TableExporterUtils.getAmount(locale, report.getTotalSpending()));
        }
        for (Participant p : participants) {
            writer.append(charResolver.getColumnDelimiter(StyleClass.NUMERIC_VALUE));
            writer.append(TableExporterUtils.getAmount(locale, report.getSpendingByUser().get(p)));
        }
        writer.append(charResolver.getRowEndDelimiter());

        /* Spending count */

        if (!ensureTypeConsistency) {
            writer.append(charResolver.getRowStartDelimiter(StyleClass.HEADING));
            writer.append(charResolver.getColumnDelimiter(StyleClass.HEADING));
            writer.append(charResolver.translateValue(resourceResolver
                    .resolve(R.string.fileExportSpendingsHeadingTotalCount)));
            if (!hideColumnForEntireTrip) {
                writer.append(charResolver.getColumnDelimiter(StyleClass.NUMERIC_VALUE));
                writer.append(report.getTotalSpendingCount());
            }
            for (Participant p : participants) {
                writer.append(charResolver.getColumnDelimiter(StyleClass.NUMERIC_VALUE));
                writer.append(report.getSpendingByUserCount().get(p));
            }
            writer.append(charResolver.getRowEndDelimiter());

            /* Categories heading */

            writer.append(charResolver.getRowStartDelimiter(StyleClass.HEADING));
            writer.append(charResolver.translateValue(resourceResolver
                    .resolve(R.string.fileExportSpendingsHeadingSpendingByCategory)
                    + currencyCodeInBrackets));
            writer.append(charResolver.getColumnDelimiter(StyleClass.HEADING));
            if (!hideColumnForEntireTrip) {
                writer.append(charResolver.getColumnDelimiter(StyleClass.HEADING));
            }
            for (@SuppressWarnings("unused")
            Participant p : participants) {
                writer.append(charResolver.getColumnDelimiter(StyleClass.HEADING));
            }
            writer.append(charResolver.getRowEndDelimiter());
        }
        /* Categories */

        final Collator collator = Collator.getInstance(resourceResolver.getLocale());
        collator.setStrength(Rc.DEFAULT_COLLATOR_STRENGTH);

        Set<PaymentCategory> sortedRelevantCategories = createSortedSet(resourceResolver, collator);
        addAllRelevantCategories(report, sortedRelevantCategories, participants);

        for (PaymentCategory category : sortedRelevantCategories) {

            writer.append(charResolver.getRowStartDelimiter(StyleClass.HEADING));
            writer.append(charResolver.getColumnDelimiter(StyleClass.HEADING));
            writer.append(charResolver.translateValue(resourceResolver.resolve(category.getResourceStringId())));
            if (!hideColumnForEntireTrip) {
                writer.append(charResolver.getColumnDelimiter(StyleClass.NUMERIC_VALUE));
                writer.append(TableExporterUtils
                        .getAmount(locale, report.getTotalSpendingByCategory().get(category)));
            }
            for (Participant p : participants) {
                writer.append(charResolver.getColumnDelimiter(StyleClass.NUMERIC_VALUE));
                writer.append(TableExporterUtils.getAmount(locale,
                        report.getSpendingByUserByCategory().get(p).get(category)));
            }
            writer.append(charResolver.getRowEndDelimiter());
        }

        /* --------- End -------------- */

        writer.append(charResolver.getTablePostfix());

        return writer;
    }

    private void addAllRelevantCategories(SumReport report, Set<PaymentCategory> sortedCategories,
            List<Participant> participants) {
        for (Entry<PaymentCategory, Amount> catAmountMapEntry : report.getTotalSpendingByCategory().entrySet()) {
            PaymentCategory category = catAmountMapEntry.getKey();
            if (!category.isInternal()
                    && (isReportForAll(participants) || isSingleAndRelevant(report, participants, category))) {
                sortedCategories.add(catAmountMapEntry.getKey());
            }
        }
    }

    private boolean isReportForAll(List<Participant> participants) {
        return participants.size() > 1;
    }

    private boolean isSingleAndRelevant(SumReport report, List<Participant> participants, PaymentCategory category) {
        return report.getSpendingByUserByCategory().get(participants.get(0))
                .get(category).getValue() != null
                && report.getSpendingByUserByCategory().get(participants.get(0))
                .get(category).getValue() < 0d;
    }

    private Set<PaymentCategory> createSortedSet(final ResourceResolver resourceResolver, final Collator collator) {
        Set<PaymentCategory> sortedCategories = new TreeSet<PaymentCategory>(new Comparator<PaymentCategory>() {

            public int compare(PaymentCategory object1, PaymentCategory object2) {
                return collator.compare(resourceResolver.resolve(object1.getResourceStringId()),
                        resourceResolver.resolve(object2.getResourceStringId()));
            }

        });
        return sortedCategories;
    }

    /*--------------------- below only setter -------------------*/

    public void setCharResolver(ExportCharResolver charResolver) {
        this.charResolver = charResolver;
    }

}
