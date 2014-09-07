package de.koelle.christian.trickytripper.dataaccess.impl.tecbeans;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import de.koelle.christian.trickytripper.model.Amount;
import de.koelle.christian.trickytripper.model.Participant;
import de.koelle.christian.trickytripper.model.Payment;
import de.koelle.christian.trickytripper.model.PaymentCategory;

public class PaymentReference {

    private long trip_id;
    private PaymentCategory category;
    private long id;
    private String description;
    private Date paymentDateTime;
    private List<PaymentParticipantRelationKey> paymentRelationKeys = new ArrayList<PaymentParticipantRelationKey>();

    public PaymentReference() {

    }

    public PaymentReference(long trip_id, Payment payment) {
        this.trip_id = trip_id;
        this.category = payment.getCategory();
        this.description = payment.getDescription();
        this.id = payment.getId();
        this.paymentDateTime = payment.getPaymentDateTime();
        for (Map.Entry<Participant, Amount> entry : payment.getParticipantToPayment().entrySet()) {
            createAndAddEntry(entry, true, paymentRelationKeys, id);
        }
        for (Map.Entry<Participant, Amount> entry : payment.getParticipantToSpending().entrySet()) {
            createAndAddEntry(entry, false, paymentRelationKeys, id);
        }
    }

    private void createAndAddEntry(Entry<Participant, Amount> entry, boolean isPayer,
            List<PaymentParticipantRelationKey> resultList, long paymentId) {
        PaymentParticipantRelationKey result = new PaymentParticipantRelationKey(paymentId, entry.getKey().getId(),
                isPayer, entry.getValue());
        resultList.add(result);
    }

    public long getTrip_id() {
        return trip_id;
    }

    public PaymentCategory getCategory() {
        return category;
    }

    public long getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public Date getPaymentDateTime() {
        return paymentDateTime;
    }

    public List<PaymentParticipantRelationKey> getPaymentRelationKeys() {
        return paymentRelationKeys;
    }

    public void setTrip_id(long trip_id) {
        this.trip_id = trip_id;
    }

    public void setCategory(PaymentCategory category) {
        this.category = category;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPaymentDateTime(Date paymentDateTime) {
        this.paymentDateTime = paymentDateTime;
    }

    public void setPaymentRelationKeys(List<PaymentParticipantRelationKey> paymentRelationKeys) {
        this.paymentRelationKeys = paymentRelationKeys;
    }

}
