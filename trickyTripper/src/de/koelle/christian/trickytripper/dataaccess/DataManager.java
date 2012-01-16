package de.koelle.christian.trickytripper.dataaccess;

import java.util.List;

import de.koelle.christian.trickytripper.model.Participant;
import de.koelle.christian.trickytripper.model.Payment;
import de.koelle.christian.trickytripper.model.Trip;
import de.koelle.christian.trickytripper.model.TripSummary;

public interface DataManager {

    boolean doesTripAlreadyExist(String nameToCheck, long tripId);

    boolean doesParticipantAlreadyExist(String nameToCheck, long tripId, long participantId);

    boolean oneOrLessTripsLeft();

    List<TripSummary> getAllTripSummaries();

    Trip loadTripById(long id);

    Trip persistTrip(Trip trip);

    public Participant persistParticipantInTrip(long tripId, Participant participant);

    Payment persistPaymentInTrip(long tripId, Payment payment);

    void deleteTrip(TripSummary tripSummary);

    void deletePayment(long paymentId);

    boolean deleteParticipant(long participantId);

}
