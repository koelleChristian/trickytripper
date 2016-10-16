package de.koelle.christian.trickytripper.controller;

import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Map;

import de.koelle.christian.trickytripper.model.Debts;
import de.koelle.christian.trickytripper.model.Participant;
import de.koelle.christian.trickytripper.model.Payment;
import de.koelle.christian.trickytripper.model.Trip;
import de.koelle.christian.trickytripper.model.TripSummary;
import de.koelle.christian.trickytripper.strategies.SumReport;
import de.koelle.christian.trickytripper.ui.model.DialogState;

public interface TripController extends TripResolver {

    /* ========= Participant related functions. ========= */

    boolean persistParticipant(Participant participant);

    boolean deleteParticipant(Participant participant);

    boolean isParticipantDeletable(Participant participant);

    List<Participant> getAllParticipants(boolean onlyActive);

    List<Participant> getAllParticipants(boolean onlyActive, boolean sorted);

    /* ========= Payment related functions. ========= */

    Payment prepareNewPayment(long participantId);

    void persistPayment(Payment newPayment);

    Payment loadPayment(long paymentId);

    void deletePayment(Payment payment);

    boolean hasLoadedTripPayments();

    /* ========= Trip related functions. ========= */

    List<TripSummary> getAllTrips();

    Trip getTripLoaded();

    void loadTrip(TripSummary summary);

    boolean persist(TripSummary summary);

    boolean persistAndLoadTrip(TripSummary summary);

    boolean oneOrLessTripsLeft();

    void deleteTrip(TripSummary tripSummary);

    SumReport getSumReport();

    Map<Participant, Debts> getDebts();

    boolean hasTripPayments(TripSummary selectedTripSummary);

    ArrayList<String> getDescriptions();

    void safeLoadedTripIdToPrefs();

    DialogState getDialogState();

    Currency getLoadedTripBaseCurrency();

    String getLoadedTripCurrencySymbol(boolean wrapInBrackets);

    void reloadTrip();
}
