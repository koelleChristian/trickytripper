package de.koelle.christian.trickytripper.strategies;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import de.koelle.christian.common.utils.NumberUtils;
import de.koelle.christian.trickytripper.factories.AmountFactory;
import de.koelle.christian.trickytripper.model.Amount;
import de.koelle.christian.trickytripper.model.Debts;
import de.koelle.christian.trickytripper.model.Participant;
import de.koelle.christian.trickytripper.model.Payment;
import de.koelle.christian.trickytripper.model.PaymentCategory;
import de.koelle.christian.trickytripper.strategies.impl.AbstractSumAmountStrategy;
import de.koelle.christian.trickytripper.strategies.impl.PaymentSumAmountStrategy;
import de.koelle.christian.trickytripper.strategies.impl.SpendingSumAmountStrategy;

public class TripReportLogic {

    private AmountFactory amountFactory;

    private final AbstractSumAmountStrategy spendingsStrategy = new SpendingSumAmountStrategy();
    private final AbstractSumAmountStrategy paymentsStrategy = new PaymentSumAmountStrategy();

    public SumReport createSumReport(List<Participant> participants, List<Payment> paymentPosition) {
        SumReport result = new SumReport();

        result.setPaymentByUser(new HashMap<Participant, Amount>());
        result.setPaymentByUserCount(new HashMap<Participant, Integer>());

        result.setSpendingByUser(new HashMap<Participant, Amount>());
        result.setSpendingByUserCount(new HashMap<Participant, Integer>());

        result.setBalanceByUser(new HashMap<Participant, Amount>());

        Map<Participant, Map<PaymentCategory, Amount>> categoryMap;
        Map<Participant, Map<PaymentCategory, Integer>> categoryCountMap;

        categoryMap = initCategoryMap(participants);
        result.setSpendingByUserByCategory(categoryMap);
        categoryCountMap = initCategoryCountMap(participants);
        result.setSpendingByUserByCategoryCount(categoryCountMap);

        categoryMap = initCategoryMap(participants);
        result.setPaymentByUserByCategory(categoryMap);
        categoryCountMap = initCategoryCountMap(participants);
        result.setPaymentByUserByCategoryCount(categoryCountMap);

        result.setTotalSpending(amountFactory.createAmount());
        result.setTotalSpendingCount(0);
        result.setTotalSpendingByCategory(new HashMap<PaymentCategory, Amount>());
        result.setTotalSpendingByCategoryCount(new HashMap<PaymentCategory, Integer>());

        AbstractSumAmountStrategy.initParticipants(result, participants, amountFactory);
        paymentsStrategy.fillSumReport(result, participants, paymentPosition);
        spendingsStrategy.fillSumReport(result, participants, paymentPosition);
        return result;
    }

    private Map<Participant, Map<PaymentCategory, Integer>> initCategoryCountMap(List<Participant> participants) {
        Map<Participant, Map<PaymentCategory, Integer>> categoryMap;
        categoryMap = new HashMap<Participant, Map<PaymentCategory, Integer>>();
        for (Participant p : participants) {
            categoryMap.put(p, new HashMap<PaymentCategory, Integer>());
        }
        return categoryMap;
    }

    private Map<Participant, Map<PaymentCategory, Amount>> initCategoryMap(List<Participant> participants) {
        Map<Participant, Map<PaymentCategory, Amount>> categoryMap;
        categoryMap = new HashMap<Participant, Map<PaymentCategory, Amount>>();
        for (Participant p : participants) {
            categoryMap.put(p, new HashMap<PaymentCategory, Amount>());
        }
        return categoryMap;
    }

    /* ================= Debt logic ============================ */

    public Map<Participant, Debts> createDebts2(List<Participant> participants, Map<Participant, Amount> balanceByUser) {
        Map<Participant, Debts> result = new HashMap<Participant, Debts>();
        Map<Participant, Amount> balanceClone = new HashMap<Participant, Amount>();
        for (Entry<Participant, Amount> entry : balanceByUser.entrySet()) {
            balanceClone.put(entry.getKey(), entry.getValue().doClone());
        }
        refreshDebts2(participants, balanceClone, result);
        return result;
    }

    public void refreshDebts(List<Participant> participants, List<Payment> payments, Map<Participant, Debts> result) {
        result.clear();
        updateDebts(participants, payments, result);
    }

    public void refreshDebts2(List<Participant> participants, Map<Participant, Amount> balanceClone,
            Map<Participant, Debts> result) {
        result.clear();
        fillResultWithBlankDebts(participants, result);
        updateDebts2(participants, balanceClone, result);
    }

    private void updateDebts2(List<Participant> participants, Map<Participant, Amount> balanceClone,
            Map<Participant, Debts> result) {

        if (isBalanceEven(balanceClone)) {
            /* Recursion end */
            return;
        }

        Entry<Participant, Amount> biggestDue = getMaxDeviation(true, balanceClone);
        Entry<Participant, Amount> biggestDebt = getMaxDeviation(false, balanceClone);
        Amount biggestDueA = biggestDue.getValue();
        Amount biggestDebtA = biggestDebt.getValue();

        Double newAmountDouble = Math
                .abs(NumberUtils.round(biggestDueA.getValue() - Math.abs(biggestDebtA.getValue())));

        if (Math.abs(biggestDueA.getValue()) > Math.abs(biggestDebtA.getValue())) {
            result.get(biggestDebt.getKey()).getLoanerToDebts()
                    .put(biggestDue.getKey(), amountFactory.createAmount(Math.abs(biggestDebtA.getValue()))); // 110
            biggestDueA.setValue(newAmountDouble); // 80
            biggestDebtA.setValue(0d); // 0
        }
        else {
            result.get(biggestDebt.getKey()).getLoanerToDebts()
                    .put(biggestDue.getKey(), amountFactory.createAmount(biggestDueA.getValue())); // 110
                                                                                                   // |
            Double valueToBeUsed = (newAmountDouble != 0.0d) ? NumberUtils.neg(newAmountDouble) : 0.0d; // 5
            biggestDueA.setValue(0d); // 0 | 0
            biggestDebtA.setValue(valueToBeUsed); // - 80 | 0
        }
        /* Recursion */
        updateDebts2(participants, balanceClone, result);

    }

    private Entry<Participant, Amount> getMaxDeviation(boolean positive, Map<Participant, Amount> balanceByUser) {
        Entry<Participant, Amount> result = null;
        for (Entry<Participant, Amount> e : balanceByUser.entrySet()) {
            if (positive) {
                if (result == null || e.getValue().getValue() > result.getValue().getValue()) {
                    result = e;
                }
            }
            else {
                if (result == null || e.getValue().getValue() < result.getValue().getValue()) {
                    result = e;
                }
            }
        }
        return result;
    }

    private boolean isBalanceEven(Map<Participant, Amount> balanceByUser) {
        for (Entry<Participant, Amount> e : balanceByUser.entrySet()) {
            if (e.getValue().getValue() != 0.0d) {
                return false;
            }
        }
        return true;
    }

    public void updateDebts(List<Participant> participants, List<Payment> payments, Map<Participant, Debts> result) {

        fillResultWithBlankDebts(participants, result);

        for (Payment payment : payments) {
            Map<Participant, Amount> paymentsClone = createPaymentValueClone(payment.getParticipantToPayment());
            Map<Participant, Amount> debitorsClone = createPaymentValueClone(payment.getParticipantToSpending());
            removeSelfDebts(paymentsClone, debitorsClone);
            deduceDebtsResult(result, paymentsClone, debitorsClone);
            if (payment.getCategory().isInternal()) {
                Entry<Participant, Amount> receiverToAmount = paymentsClone.entrySet().iterator().next();
                Entry<Participant, Amount> transfererToAmount = debitorsClone.entrySet().iterator().next();
                Debts debts = result.get(receiverToAmount.getKey());
                Amount amountForResult = getNullsafeAmountEntryForParticipant(debts, transfererToAmount.getKey());
                amountForResult.addValue(transfererToAmount.getValue().getValue());
                debts.getLoanerToDebts().put(transfererToAmount.getKey(), amountForResult);
            }
            balanceDebts(result, participants);
        }
    }

    private void fillResultWithBlankDebts(List<Participant> participants, Map<Participant, Debts> result) {
        for (Participant p : participants) {
            if (!result.containsKey(p)) {
                result.put(p, new Debts());
            }
        }
    }

    private void balanceDebts(Map<Participant, Debts> result, List<Participant> participants) {

        for (Participant p1 : participants) {
            for (Participant p2 : participants) {
                if (p1.equals(p2)) {
                    continue;
                }

                Amount amountP1 = result.get(p1).getLoanerToDebts().get(p2);
                Amount amountP2 = result.get(p2).getLoanerToDebts().get(p1);

                if (amountP1 == null) {
                    amountP1 = amountFactory.createAmount();
                }
                if (amountP2 == null) {
                    amountP2 = amountFactory.createAmount();
                }

                Double newAmountDouble = Math.abs(NumberUtils.round(Math.abs(amountP1.getValue())
                        - Math.abs(amountP2.getValue())));
                Amount newAmountAmount = amountFactory.createAmount(newAmountDouble);

                if (newAmountDouble.equals(Double.valueOf(0f))) {
                    result.get(p1).getLoanerToDebts().remove(p2);
                    result.get(p2).getLoanerToDebts().remove(p1);
                }
                else if (amountP1.getValue() > amountP2.getValue()) {
                    result.get(p1).getLoanerToDebts().put(p2, newAmountAmount);
                    result.get(p2).getLoanerToDebts().remove(p1);
                }
                else {
                    result.get(p1).getLoanerToDebts().remove(p2);
                    result.get(p2).getLoanerToDebts().put(p1, newAmountAmount);
                }

            }
        }
    }

    private void deduceDebtsResult(Map<Participant, Debts> result, Map<Participant, Amount> paymentsClone,
            Map<Participant, Amount> debitorsClone) {
        for (Entry<Participant, Amount> debitor : debitorsClone.entrySet()) {

            Debts debts = result.get(debitor.getKey());

            while (debitor.getValue().getValue() < 0) {
                for (Entry<Participant, Amount> payEntry : paymentsClone.entrySet()) {
                    Participant payEntryParticipant = payEntry.getKey();

                    Double dueVal = debitor.getValue().getValue();

                    Double payedVal = payEntry.getValue().getValue();

                    Double dueValNew;
                    Double dueForResult;
                    Double payedValNew;

                    if (payedVal == 0) {
                        continue;
                    }
                    else if (payedVal < Math.abs(dueVal) && payedVal > 0) {
                        dueValNew = NumberUtils.round(payedVal + dueVal);
                        dueForResult = payedVal;
                        payedValNew = 0d;
                    }
                    else {
                        dueValNew = 0d;
                        dueForResult = Math.abs(dueVal);
                        payedValNew = NumberUtils.round(payedVal + dueVal);
                    }
                    payEntry.getValue().setValue(payedValNew);
                    debitor.getValue().setValue(dueValNew);

                    Amount amountForResult = getNullsafeAmountEntryForParticipant(debts, payEntryParticipant);

                    amountForResult.addValue(dueForResult);

                    debts.getLoanerToDebts().put(payEntry.getKey(), amountForResult);

                }
            }
            result.put(debitor.getKey(), debts);
        }
    }

    private Amount getNullsafeAmountEntryForParticipant(Debts debts, Participant payEntryParticipant) {
        Amount amountForResult = (debts.getLoanerToDebts().get(payEntryParticipant) == null) ? amountFactory
                .createAmount() : debts.getLoanerToDebts().get(payEntryParticipant);
        return amountForResult;
    }

    private void removeSelfDebts(Map<Participant, Amount> paymentsClone, Map<Participant, Amount> debitorsClone) {
        for (Entry<Participant, Amount> debitor : debitorsClone.entrySet()) {

            if (paymentsClone.containsKey(debitor.getKey())) {
                Amount payValue = paymentsClone.get(debitor.getKey());

                Double dueVal = debitor.getValue().getValue();
                Double payedVal = payValue.getValue();

                Double dueValNew;
                Double payedValNew;

                if (payedVal < Math.abs(dueVal) && payedVal > 0) {
                    dueValNew = NumberUtils.round(payedVal + dueVal);
                    payedValNew = 0d;
                }
                else {
                    dueValNew = 0d;
                    payedValNew = NumberUtils.round(payedVal + dueVal);
                }
                payValue.setValue(payedValNew);
                debitor.getValue().setValue(dueValNew);
            }
        }

    }

    private Map<Participant, Amount> createPaymentValueClone(Map<Participant, Amount> map) {
        Map<Participant, Amount> result = new HashMap<Participant, Amount>();
        for (Entry<Participant, Amount> paymentEntry : map.entrySet()) {
            result.put(paymentEntry.getKey(), cloneAmount(paymentEntry.getValue()));
        }
        return result;
    }

    private Amount cloneAmount(Amount value) {
        Amount result = new Amount();
        result.setUnit(value.getUnit());
        result.setValue(value.getValue());
        return result;
    }

    public void setAmountFactory(AmountFactory amountFactory) {
        this.amountFactory = amountFactory;
        this.paymentsStrategy.setAmountFactory(amountFactory);
        this.spendingsStrategy.setAmountFactory(amountFactory);
    }

}
