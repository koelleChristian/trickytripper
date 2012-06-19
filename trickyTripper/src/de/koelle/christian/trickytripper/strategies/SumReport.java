package de.koelle.christian.trickytripper.strategies;

import java.util.HashMap;
import java.util.Map;

import de.koelle.christian.trickytripper.model.Amount;
import de.koelle.christian.trickytripper.model.Participant;
import de.koelle.christian.trickytripper.model.PaymentCategory;

public class SumReport {

    private Map<Participant, Amount> spendingByUser;
    private Map<Participant, Map<PaymentCategory, Amount>> spendingByUserByCategory;
    private Map<Participant, Integer> spendingByUserCount;
    private Map<Participant, Map<PaymentCategory, Integer>> spendingByUserByCategoryCount;

    private Map<Participant, Amount> paymentByUser;
    private Map<Participant, Map<PaymentCategory, Amount>> paymentByUserByCategory;
    private Map<Participant, Integer> paymentByUserCount;
    private Map<Participant, Map<PaymentCategory, Integer>> paymentByUserByCategoryCount;

    private Map<Participant, Amount> balanceByUser;

    private Amount totalSpendings;
    private int totalSpendingCount;
    private Map<PaymentCategory, Amount> totalSpendingByCategory;
    private Map<PaymentCategory, Integer> totalSpendingByCategoryCount;

    public Map<Participant, Amount> getSpendingByUser() {
        return spendingByUser;
    }

    public void setSpendingByUser(Map<Participant, Amount> spendingByUser) {
        this.spendingByUser = spendingByUser;
    }

    public Map<Participant, Map<PaymentCategory, Amount>> getSpendingByUserByCategory() {
        return spendingByUserByCategory;
    }

    public void setSpendingByUserByCategory(Map<Participant, Map<PaymentCategory, Amount>> spendingsByUserByCategory) {
        this.spendingByUserByCategory = spendingsByUserByCategory;
    }

    public Map<Participant, Amount> getPaymentByUser() {
        return paymentByUser;
    }

    public void setPaymentByUser(Map<Participant, Amount> paymentsByUser) {
        this.paymentByUser = paymentsByUser;
    }

    public Map<Participant, Map<PaymentCategory, Amount>> getPaymentByUserByCategory() {
        return paymentByUserByCategory;
    }

    public void setPaymentByUserByCategory(Map<Participant, Map<PaymentCategory, Amount>> paymentsByUserByCategory) {
        this.paymentByUserByCategory = paymentsByUserByCategory;
    }

    public Amount getTotalSpendings() {
        return totalSpendings;
    }

    public void setTotalSpendings(Amount totalSpendings) {
        this.totalSpendings = totalSpendings;
    }

    public int getTotalSpendingCount() {
        return totalSpendingCount;
    }

    public void setTotalSpendingCount(int totalSpendingstCount) {
        this.totalSpendingCount = totalSpendingstCount;
    }

    public Map<PaymentCategory, Amount> getTotalSpendingByCategory() {
        return totalSpendingByCategory;
    }

    public void setTotalSpendingByCategory(Map<PaymentCategory, Amount> spendingsByCategory) {
        this.totalSpendingByCategory = spendingsByCategory;
    }

    public Map<PaymentCategory, Integer> getTotalSpendingByCategoryCount() {
        return totalSpendingByCategoryCount;
    }

    public void setTotalSpendingByCategoryCount(Map<PaymentCategory, Integer> spendingsByCategoryCount) {
        this.totalSpendingByCategoryCount = spendingsByCategoryCount;
    }

    public Map<Participant, Integer> getSpendingByUserCount() {
        return spendingByUserCount;
    }

    public void setSpendingByUserCount(Map<Participant, Integer> spendingsByUserCount) {
        this.spendingByUserCount = spendingsByUserCount;
    }

    public Map<Participant, Integer> getPaymentByUserCount() {
        return paymentByUserCount;
    }

    public void setPaymentByUserCount(Map<Participant, Integer> paymentsByUserCount) {
        this.paymentByUserCount = paymentsByUserCount;
    }

    public Map<Participant, Map<PaymentCategory, Integer>> getSpendingByUserByCategoryCount() {
        return spendingByUserByCategoryCount;
    }

    public void setSpendingByUserByCategoryCount(
            Map<Participant, Map<PaymentCategory, Integer>> spendingByUserByCategoryCount) {
        this.spendingByUserByCategoryCount = spendingByUserByCategoryCount;
    }

    public Map<Participant, Map<PaymentCategory, Integer>> getPaymentByUserByCategoryCount() {
        return paymentByUserByCategoryCount;
    }

    public void setPaymentByUserByCategoryCount(
            Map<Participant, Map<PaymentCategory, Integer>> paymentByUserByCategoryCount) {
        this.paymentByUserByCategoryCount = paymentByUserByCategoryCount;
    }

    public Map<Participant, Amount> getBalanceByUser() {
        return balanceByUser;
    }

    public void setBalanceByUser(Map<Participant, Amount> balanceByUser) {
        this.balanceByUser = balanceByUser;
    }

    public void addNewParticipant(Participant participant, Amount freshBlankAmount) {
        spendingByUser.put(participant, freshBlankAmount.clone());
        spendingByUserCount.put(participant, Integer.valueOf(0));
        spendingByUserByCategory.put(participant, new HashMap<PaymentCategory, Amount>());
        spendingByUserByCategoryCount.put(participant, new HashMap<PaymentCategory, Integer>());

        paymentByUser.put(participant, freshBlankAmount.clone());
        paymentByUserCount.put(participant, Integer.valueOf(0));
        paymentByUserByCategory.put(participant, new HashMap<PaymentCategory, Amount>());
        paymentByUserByCategoryCount.put(participant, new HashMap<PaymentCategory, Integer>());

        balanceByUser.put(participant, freshBlankAmount.clone());

    }

    public void removeParticipant(Participant participant) {
        spendingByUser.remove(participant);
        spendingByUserCount.remove(participant);
        spendingByUserByCategory.remove(participant);
        spendingByUserByCategoryCount.remove(participant);

        paymentByUser.remove(participant);
        paymentByUserCount.remove(participant);
        paymentByUserByCategory.remove(participant);
        paymentByUserByCategoryCount.remove(participant);

        balanceByUser.remove(participant);
    }

}
