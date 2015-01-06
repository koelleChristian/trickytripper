package de.koelle.christian.trickytripper.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

public class Payment implements Serializable {

    private static final long serialVersionUID = -585812949353435247L;

    private long id;
    private PaymentCategory category;
    private Date paymentDateTime;
    private String description;

    private Map<Participant, Amount> payerToPayment = new TreeMap<Participant, Amount>();
    private Map<Participant, Amount> debitorToAmount = new TreeMap<Participant, Amount>();

    public PaymentCategory getCategory() {
        return category;
    }

    public void setCategory(PaymentCategory category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Map<Participant, Amount> getParticipantToPayment() {
        return payerToPayment;
    }

    public void setPayerToPayment(Map<Participant, Amount> payers) {
        this.payerToPayment = payers;
    }

    public Map<Participant, Amount> getParticipantToSpending() {
        return debitorToAmount;
    }

    public void setDebitorToAmount(
            Map<Participant, Amount> participantsConcerned) {
        this.debitorToAmount = participantsConcerned;
    }

    public Date getPaymentDateTime() {
        return paymentDateTime;
    }

    public void setPaymentDateTime(Date paymentDateTime) {
        this.paymentDateTime = paymentDateTime;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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
        Payment other = (Payment) obj;
        return id == other.id;
    }

    @Override
    public String toString() {
        return "Payment [id=" + id + ", category=" + category + ", paymentDateTime=" + paymentDateTime
                + ", description=" + description + ", payerToPayment=" + payerToPayment + ", debitorToAmount="
                + debitorToAmount + "]";
    }

    public void removeBlankEntries() {
        removeBlanksFromMap(payerToPayment);
        removeBlanksFromMap(debitorToAmount);
    }

    private void removeBlanksFromMap(Map<Participant, Amount> map) {
        List<Participant> toBeRemoved = new ArrayList<Participant>();
        for (Entry<Participant, Amount> entry : map.entrySet()) {
            if (entry.getValue() != null && (0d == entry.getValue().getValue() || -0d == entry.getValue().getValue())) {
                toBeRemoved.add(entry.getKey());
            }
        }
        for (Participant p : toBeRemoved) {
            map.remove(p);
        }
    }

    public void getTotalAmount(Amount result) {
        for (Entry<Participant, Amount> entry : payerToPayment.entrySet()) {
            result.addValue(entry.getValue().getValue());
        }
    }
    public boolean isMoneyTransfer(){
        return PaymentCategory.MONEY_TRANSFER.equals(this.getCategory());
    }
}
