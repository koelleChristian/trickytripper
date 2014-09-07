package de.koelle.christian.trickytripper.strategies.impl;

import java.util.Map.Entry;

import de.koelle.christian.trickytripper.model.Amount;
import de.koelle.christian.trickytripper.model.Participant;
import de.koelle.christian.trickytripper.model.Payment;
import de.koelle.christian.trickytripper.strategies.SumReport;

public class PaymentSumAmountStrategy extends AbstractSumAmountStrategy {

    @Override
    protected void doAddAmountParticipantResult(SumReport sumReport, Participant participant, Payment payment) {
        for (Entry<Participant, Amount> entry : payment.getParticipantToPayment().entrySet()) {

            Amount amount = sumReport.getPaymentByUser().get(participant);
            Amount amountBalance = sumReport.getBalanceByUser().get(participant);
            Integer count = sumReport.getPaymentByUserCount().get(participant);

            Amount amountForCategory = (sumReport.getPaymentByUserByCategory().get(participant)
                    .get(payment.getCategory()) == null) ? getAmountFactory().createAmount() : sumReport
                    .getPaymentByUserByCategory().get(participant).get(payment.getCategory());

            Integer countCategory = (sumReport.getPaymentByUserByCategoryCount().get(participant)
                    .get(payment.getCategory()) == null) ? Integer.valueOf(0) : sumReport
                    .getPaymentByUserByCategoryCount().get(participant).get(payment.getCategory());

            if (participant.equals(entry.getKey())) {
                if (!payment.getCategory().isInternal()) {
                    amountForCategory.addAmount(entry.getValue());

                    count = count + 1;

                    countCategory = countCategory + 1;
                }
                amount.addAmount(entry.getValue());
                amountBalance.addAmount(entry.getValue());
            }
            sumReport.getBalanceByUser().put(participant, amountBalance);
            sumReport.getPaymentByUser().put(participant, amount);
            sumReport.getPaymentByUserByCategory().get(participant).put(payment.getCategory(), amountForCategory);
            sumReport.getPaymentByUserCount().put(participant, count);
            sumReport.getPaymentByUserByCategoryCount().get(participant).put(payment.getCategory(), countCategory);
        }

    }

    @Override
    protected void doAddAmountTotalResult(SumReport sumReport, Payment payment) {
        // Noting to do here.

    }

}
