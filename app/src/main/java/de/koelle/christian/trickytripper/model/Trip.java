package de.koelle.christian.trickytripper.model;

import java.util.Currency;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import de.koelle.christian.trickytripper.strategies.SumReport;

public class Trip {

    private long id;
    private String name;
    private List<Participant> participant;
    private List<Payment> payments;
    private Currency baseCurrency;

    /* Transient information */
    private Map<Participant, Debts> debts;
    private SumReport sumReport;

    /* =========== Getter/Setter =============== */

    public String getName() {
        return name;
    }

    public Map<Participant, Debts> getDebts() {
        return debts;
    }

    public void setDebts(Map<Participant, Debts> debts) {
        this.debts = debts;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<Participant> getParticipant() {
        return participant;
    }

    public void setParticipant(List<Participant> participant) {
        this.participant = participant;
    }

    public List<Payment> getPayments() {
        return payments;
    }

    public void setPayments(List<Payment> payments) {
        this.payments = payments;
    }

    public SumReport getSumReport() {
        return sumReport;
    }

    public void setSumReport(SumReport sumReport) {
        this.sumReport = sumReport;
    }

    public Currency getBaseCurrency() {
        return baseCurrency;
    }

    public void setBaseCurrency(Currency baseCurrency) {
        this.baseCurrency = baseCurrency;
    }

    public boolean partOfPayments(Participant p) {
        for (Payment payment : payments) {
            for (Entry<Participant, Amount> entry : payment.getParticipantToPayment().entrySet()) {
                if (entry.getKey().equals(p)) {
                    return true;
                }
            }
            for (Entry<Participant, Amount> entry : payment.getParticipantToSpending().entrySet()) {
                if (entry.getKey().equals(p)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (id ^ (id >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Trip other = (Trip) obj;
        return id == other.id;
    }

}
