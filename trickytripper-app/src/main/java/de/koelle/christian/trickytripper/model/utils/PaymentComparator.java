package de.koelle.christian.trickytripper.model.utils;

import de.koelle.christian.trickytripper.model.Payment;

import java.util.Comparator;

/**
 * Comparator for payments: Newest on top.
 */
public class PaymentComparator implements Comparator<Payment> {
    @Override

    public int compare(Payment lhs, Payment rhs) {
        return lhs.getPaymentDateTime().compareTo(rhs.getPaymentDateTime()) * -1;
    }
}
