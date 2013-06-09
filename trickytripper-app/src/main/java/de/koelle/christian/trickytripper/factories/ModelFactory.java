package de.koelle.christian.trickytripper.factories;

import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

import de.koelle.christian.trickytripper.model.Participant;
import de.koelle.christian.trickytripper.model.Payment;
import de.koelle.christian.trickytripper.model.PaymentCategory;
import de.koelle.christian.trickytripper.model.Trip;

public class ModelFactory {

    public static Trip createTrip(Currency currency, String name) {
        Trip trip = new Trip();
        trip.setName(name);

        List<Participant> participants = new ArrayList<Participant>();
        List<Payment> payments = new ArrayList<Payment>();

        trip.setParticipant(participants);
        trip.setPayments(payments);
        trip.setBaseCurrency(currency);
        return trip;
    }

    public static Trip createNewTrip(String name, Currency baseCurrency) {
        Trip result = new Trip();
        result.setName(name);
        result.setBaseCurrency(baseCurrency);
        return result;
    }

    public static Participant createNewParticipant(String name, boolean active) {
        Participant result = new Participant();
        result.setActive(active);
        result.setName(name);
        return result;
    }

    public static Payment createNewPayment(String description, PaymentCategory category) {
        Payment result = new Payment();
        result.setCategory(category);
        result.setDescription(description);
        return result;
    }

}
