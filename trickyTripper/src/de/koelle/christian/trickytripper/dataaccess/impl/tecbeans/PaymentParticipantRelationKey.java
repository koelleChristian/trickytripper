package de.koelle.christian.trickytripper.dataaccess.impl.tecbeans;

import de.koelle.christian.trickytripper.model.Amount;

public class PaymentParticipantRelationKey {

    private long paymentId;
    private final long participantId;
    private final boolean isPayer;
    private final Amount amount;

    public PaymentParticipantRelationKey(long paymentId, long partipantId, boolean isPayer, Amount amount) {
        super();
        this.paymentId = paymentId;
        this.participantId = partipantId;
        this.isPayer = isPayer;
        this.amount = amount;
    }

    public void setPaymentId(long paymentId) {
        this.paymentId = paymentId;
    }

    public long getPaymentId() {
        return paymentId;
    }

    public long getParticipantId() {
        return participantId;
    }

    public boolean isPayer() {
        return isPayer;
    }

    public Amount getAmount() {
        return amount;
    }

}
