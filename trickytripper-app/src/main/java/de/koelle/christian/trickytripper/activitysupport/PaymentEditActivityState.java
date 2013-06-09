package de.koelle.christian.trickytripper.activitysupport;

import java.io.Serializable;

import de.koelle.christian.trickytripper.model.Payment;

public class PaymentEditActivityState implements Serializable {

    private static final long serialVersionUID = -7479297473373104820L;
    private final Payment payment;
    private final boolean divideEqually;
    private final boolean spendingInputInitialized;

    public PaymentEditActivityState(Payment payment, boolean divideEqually, boolean spendingInputInitialized) {
        this.payment = payment;
        this.divideEqually = divideEqually;
        this.spendingInputInitialized = spendingInputInitialized;
    }

    public Payment getPayment() {
        return payment;
    }

    public boolean isDivideEqually() {
        return divideEqually;
    }

    public boolean isSpendingInputInitialized() {
        return spendingInputInitialized;
    }

}
