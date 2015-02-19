package de.koelle.christian.trickytripper.strategies.impl;

import java.util.Map.Entry;

import de.koelle.christian.trickytripper.model.Amount;
import de.koelle.christian.trickytripper.model.Participant;
import de.koelle.christian.trickytripper.model.Payment;
import de.koelle.christian.trickytripper.model.PaymentCategory;
import de.koelle.christian.trickytripper.strategies.SumReport;

public class SpendingSumAmountStrategy extends AbstractSumAmountStrategy {

    @Override
    protected void doAddAmountParticipantResult(SumReport sumReport, Participant participant, Payment payment) {
        for (Entry<Participant, Amount> entry : payment.getParticipantToSpending().entrySet()) {

            Amount amount = sumReport.getSpendingByUser().get(participant);
            Amount amountBalance = sumReport.getBalanceByUser().get(participant);
            Integer count = sumReport.getSpendingByUserCount().get(participant);

            Amount amountForCategory = (sumReport.getSpendingByUserByCategory().get(participant)
                    .get(payment.getCategory()) == null) ? getAmountFactory().createAmount() : sumReport
                    .getSpendingByUserByCategory().get(participant).get(payment.getCategory());

            Integer countCategory = (sumReport.getSpendingByUserByCategoryCount().get(participant)
                    .get(payment.getCategory()) == null) ? Integer.valueOf(0) : sumReport
                    .getSpendingByUserByCategoryCount().get(participant).get(payment.getCategory());

            if (participant.equals(entry.getKey())) {
                if (!payment.getCategory().isInternal()) {
                    amount.addAmount(entry.getValue());
                    amountForCategory.addAmount(entry.getValue());

                    count = count + 1;
                    countCategory = countCategory + 1;

                }
                else if (payment.getCategory().isInternal() &&
                        entry.getValue().getValue() > 0) {
                    sumReport.getPaymentByUser().get(participant).addValue(entry.getValue().getValue());
                }
                amountBalance.addAmount(entry.getValue());

            }
            sumReport.getBalanceByUser().put(participant, amountBalance);
            sumReport.getSpendingByUser().put(participant, amount);
            sumReport.getSpendingByUserByCategory().get(participant).put(payment.getCategory(), amountForCategory);
            sumReport.getSpendingByUserCount().put(participant, count);
            sumReport.getSpendingByUserByCategoryCount().get(participant).put(payment.getCategory(), countCategory);
        }

    }

    @Override
    protected void doAddAmountTotalResult(SumReport sumReport, Payment payment) {
        final PaymentCategory category = payment.getCategory();
        if (!category.isInternal()) {
            for (Entry<Participant, Amount> entry : payment.getParticipantToSpending().entrySet()) {

                Amount amountTotal = (sumReport.getTotalSpending() == null) ? getAmountFactory().createAmount()
                        : sumReport.getTotalSpending();
                amountTotal.addAmount(entry.getValue());

                Amount amountTotalForCategory = (sumReport.getTotalSpendingByCategory().get(category) == null) ? getAmountFactory()
                        .createAmount()
                        : sumReport.getTotalSpendingByCategory().get(category);
                amountTotalForCategory.addAmount(entry.getValue());

                sumReport.setTotalSpending(amountTotal);
                sumReport.getTotalSpendingByCategory().put(category, amountTotalForCategory);

            }
            Integer countCategory = (sumReport.getTotalSpendingByCategoryCount().get(category) == null) ? Integer
                    .valueOf(0) : sumReport.getTotalSpendingByCategoryCount().get(category);
            countCategory = countCategory + 1;

            int countTotal = sumReport.getTotalSpendingCount() + 1;
            sumReport.getTotalSpendingByCategoryCount().put(category, countCategory);
            sumReport.setTotalSpendingCount(countTotal);
        }

    }
}
