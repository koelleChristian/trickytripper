package de.koelle.christian.trickytripper.dataaccess;

import java.util.Currency;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.koelle.christian.trickytripper.model.ExchangeRate;
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

    Trip persistTripBySummary(TripSummary tripSummary);

    public Participant persistParticipantInTrip(long tripId, Participant participant);

    Payment persistPaymentInTrip(long tripId, Payment payment);

    void deleteTrip(TripSummary tripSummary);

    void deletePayment(long paymentId);

    boolean deleteParticipant(long participantId);

    boolean hasTripPayments(long tripId);

    /* ========= Exchange Rates ============ */

    List<ExchangeRate> findSuitableRates(Currency currencyFrom, Currency currencyTo);

    List<ExchangeRate> getAllExchangeRatesWithoutInversion();

    ExchangeRate getExchangeRateById(Long technicalId);

    boolean deleteExchangeRates(List<ExchangeRate> rows);

    ExchangeRate persistExchangeRate(ExchangeRate rate);

    boolean doesExchangeRateAlreadyExist(ExchangeRate exchangeRate);

    void persistImportedExchangeRate(ExchangeRate rate, boolean replaceWhenAlreadyImported);

    void persistExchangeRateUsedLast(ExchangeRate exchangeRateUsedLast);

    /* ========= Else ============ */

    /** returns a map with one pair of information. */
    Map<Set<Currency>, Set<Currency>> findUsedCurrencies();

}
