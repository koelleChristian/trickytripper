package de.koelle.christian.trickytripper.dataaccess.suite.util;

import java.util.Currency;
import java.util.Map;

import de.koelle.christian.trickytripper.model.Amount;
import de.koelle.christian.trickytripper.model.Participant;
import de.koelle.christian.trickytripper.model.Payment;

public class ModelSetupUtil {
    public static void addAmountToPayment(Payment payment01In, double value, String unit, boolean isPayer,
            Participant participant) {
        Map<Participant, Amount> map;
        if (isPayer) {
            map = payment01In.getParticipantToPayment();
        }
        else {
            map = payment01In.getParticipantToSpending();
        }
        Amount amount = new Amount();
        amount.setUnit(Currency.getInstance(unit));
        amount.setValue(value);
        map.put(participant, amount);
    }
}
