package de.koelle.christian.trickytripper.strategies.impl;

import java.util.List;

import de.koelle.christian.trickytripper.factories.AmountFactory;
import de.koelle.christian.trickytripper.model.Amount;
import de.koelle.christian.trickytripper.model.Participant;
import de.koelle.christian.trickytripper.model.Payment;
import de.koelle.christian.trickytripper.strategies.SumAmountStrategy;
import de.koelle.christian.trickytripper.strategies.SumReport;

public abstract class AbstractSumAmountStrategy implements SumAmountStrategy {

    private AmountFactory amountFactory;

    public void fillSumReport(SumReport sumReport, List<Participant> participants, List<Payment> payments) {
        addAmount(sumReport, participants, payments);
    }

    public static void initParticipants(SumReport sumReport, List<Participant> participants, AmountFactory amountFactory) {
        for (Participant participant : participants) {
            Amount paymentByUserAmount = amountFactory.createAmount();
            Integer paymentCount = 0;
            Amount spendingByUserAmount = amountFactory.createAmount();
            Integer spendingCount = 0;
            Amount balanceByUser = amountFactory.createAmount();

            sumReport.getPaymentByUser().put(participant, paymentByUserAmount);
            sumReport.getPaymentByUserCount().put(participant, paymentCount);
            sumReport.getSpendingByUser().put(participant, spendingByUserAmount);
            sumReport.getSpendingByUserCount().put(participant, spendingCount);
            sumReport.getBalanceByUser().put(participant, balanceByUser);
        }
    }

    public void addAmount(SumReport sumReport, List<Participant> participants, List<Payment> payments) {
        for (Participant participant : participants) {

            for (Payment payment : payments) {
                doAddAmountParticipantResult(sumReport, participant, payment);
            }
        }
        for (Payment payment : payments) {
            doAddAmountTotalResult(sumReport, payment);
        }
    }

    protected abstract void doAddAmountParticipantResult(SumReport sumReport, Participant participant, Payment payment);

    protected abstract void doAddAmountTotalResult(SumReport sumReport, Payment payment);

    public void setAmountFactory(AmountFactory amountFactory) {
        this.amountFactory = amountFactory;
    }

    public AmountFactory getAmountFactory() {
        return amountFactory;
    }

}
