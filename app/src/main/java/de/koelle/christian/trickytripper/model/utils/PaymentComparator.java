package de.koelle.christian.trickytripper.model.utils;

import java.util.Comparator;

import de.koelle.christian.trickytripper.model.Payment;

/**
 * Comparator for payments: Newest on top.
 */
public class PaymentComparator implements Comparator<Payment> {

    public int compare(Payment lhs, Payment rhs) {
        return lhs.getPaymentDateTime().compareTo(rhs.getPaymentDateTime()) * -1;
    }
}
