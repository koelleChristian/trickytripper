package de.koelle.christian.trickytripper.dataaccess.impl;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Currency;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import de.koelle.christian.common.utils.Assert;
import de.koelle.christian.trickytripper.constants.Rc;
import de.koelle.christian.trickytripper.dataaccess.DataManager;
import de.koelle.christian.trickytripper.dataaccess.impl.daos.ExchangeRateDao;
import de.koelle.christian.trickytripper.dataaccess.impl.daos.ExchangeRatePrefTable;
import de.koelle.christian.trickytripper.dataaccess.impl.daos.ExchangeRateTable;
import de.koelle.christian.trickytripper.dataaccess.impl.daos.ParticipantDao;
import de.koelle.christian.trickytripper.dataaccess.impl.daos.ParticipantTable;
import de.koelle.christian.trickytripper.dataaccess.impl.daos.PaymentDao;
import de.koelle.christian.trickytripper.dataaccess.impl.daos.PaymentTable;
import de.koelle.christian.trickytripper.dataaccess.impl.daos.RelPaymentParticipantTable;
import de.koelle.christian.trickytripper.dataaccess.impl.daos.TripDao;
import de.koelle.christian.trickytripper.dataaccess.impl.daos.TripTable;
import de.koelle.christian.trickytripper.dataaccess.impl.tecbeans.ParticipantReference;
import de.koelle.christian.trickytripper.dataaccess.impl.tecbeans.PaymentParticipantRelationKey;
import de.koelle.christian.trickytripper.dataaccess.impl.tecbeans.PaymentReference;
import de.koelle.christian.trickytripper.factories.ModelFactory;
import de.koelle.christian.trickytripper.model.CurrenciesUsed;
import de.koelle.christian.trickytripper.model.ExchangeRate;
import de.koelle.christian.trickytripper.model.Participant;
import de.koelle.christian.trickytripper.model.Payment;
import de.koelle.christian.trickytripper.model.Trip;
import de.koelle.christian.trickytripper.model.TripSummary;

public class DataManagerImpl implements DataManager {

    private final SQLiteDatabase db;

    private final TripDao tripDao;
    private final ParticipantDao participantDao;
    private final PaymentDao paymentDao;
    private final ExchangeRateDao exchangeRateDao;

    public DataManagerImpl(Context context) {

        Context context1 = context;

        SQLiteOpenHelper openHelper = new OpenHelper(context1);
        db = openHelper.getWritableDatabase();
        if (Rc.debugOn) {
            Log.d(Rc.LT, "DataManagerImplBackup created, db open status: " + db.isOpen());
        }

        tripDao = new TripDao(db);
        participantDao = new ParticipantDao(db);
        paymentDao = new PaymentDao(db);
        exchangeRateDao = new ExchangeRateDao(db);
    }

    public void close() {
        if (db != null) {
            db.close();
        }
    }

    public void removeAll() {
        db.delete(ExchangeRatePrefTable.TABLE_NAME, null, null);
        db.delete(ExchangeRateTable.TABLE_NAME, null, null);
        db.delete(RelPaymentParticipantTable.TABLE_NAME, null, null);
        db.delete(PaymentTable.TABLE_NAME, null, null);
        db.delete(ParticipantTable.TABLE_NAME, null, null);
        db.delete(TripTable.TABLE_NAME, null, null);
    }

    public List<TripSummary> getAllTripSummaries() {
        return tripDao.getAllTripSummaries();
    }

    public void deleteTrip(TripSummary tripSummary) {

        try {
            db.beginTransaction();
            long tripId = tripSummary.getId();
            if (tripId > 0) {
                paymentDao.deleteAllInTrip(tripId);
                participantDao.deleteAllInTrip(tripId);
                tripDao.delete(tripId);
            }
            db.setTransactionSuccessful();
        } catch (SQLException e) {
            Log.e(Rc.LT, "Error deleting trip (transaction rolled back)", e);
        } finally {
            db.endTransaction();
        }
    }

    public void deletePayment(long paymentId) {

        try {
            db.beginTransaction();
            paymentDao.deletePayment(paymentId);
            db.setTransactionSuccessful();
        } catch (SQLException e) {
            Log.e(Rc.LT, "Error deleting payment (transaction rolled back)", e);
        } finally {
            db.endTransaction();
        }
    }

    public boolean deleteParticipant(long participantId) {
        boolean result = true;
        try {
            db.beginTransaction();
            participantDao.deleteParticipant(participantId);
            db.setTransactionSuccessful();
        } catch (SQLException e) {
            Log.e(Rc.LT, "Error deleting participant (transaction rolled back)", e);
            result = false;
        } finally {
            db.endTransaction();
        }
        return result;
    }

    public Trip loadTripById(long tripId) {
        Trip result = tripDao.get(tripId);
        if (result == null) {
            return null;
        }
        result.setParticipant(new ArrayList<Participant>());
        result.setPayments(new ArrayList<Payment>());

        List<Participant> participantsInTrip = participantDao.getAllParticipantsInTrip(tripId);
        result.getParticipant().addAll(participantsInTrip);

        List<PaymentReference> interimPaymentResult = paymentDao.getAllPaymentsInTrip(tripId);
        List<Payment> payments = convertPaymentReferenceToPayment(interimPaymentResult, participantsInTrip);
        result.getPayments().addAll(payments);

        return result;
    }

    private List<Payment> convertPaymentReferenceToPayment(List<PaymentReference> interimPaymentResult,
            List<Participant> participants) {
        List<Payment> result = new ArrayList<>();
        if (interimPaymentResult == null) {
            return result;
        }
        Map<Long, Participant> participantById = new HashMap<>();
        for (Participant p : participants) {
            participantById.put(p.getId(), p);
        }
        for (PaymentReference ref : interimPaymentResult) {
            Payment payment = new Payment();
            payment.setId(ref.getId());
            payment.setDescription(ref.getDescription());
            payment.setCategory(ref.getCategory());
            payment.setPaymentDateTime(ref.getPaymentDateTime());
            if (ref.getPaymentRelationKeys() != null) {
                for (PaymentParticipantRelationKey rel : ref.getPaymentRelationKeys()) {
                    Participant p = participantById.get(rel.getParticipantId());
                    if (rel.isPayer()) {
                        payment.getParticipantToPayment().put(p, rel.getAmount());
                    }
                    else {
                        payment.getParticipantToSpending().put(p, rel.getAmount());
                    }
                }
            }
            result.add(payment);
        }
        return result;
    }

    public boolean hasTripPayments(long tripId) {
        return (paymentDao.countPaymentsInTrip(tripId) > 0);
    }

    public ArrayList<String> getAllPaymentDescriptionsInTrip(long tripId) {
        return paymentDao.getAllPaymentDescriptionsInTrip(tripId);
    }

    public Trip persistTripBySummary(TripSummary tripSummary) {
        boolean isNew = (1 > tripSummary.getId());
        Trip trip;
        if (isNew) {
            trip = ModelFactory.createTrip(tripSummary.getBaseCurrency(), tripSummary.getName());
        }
        else {
            trip = loadTripById(tripSummary.getId());
            trip.setName(tripSummary.getName());
            trip.setBaseCurrency(tripSummary.getBaseCurrency());
        }

        Trip persistedTrip = persistTrip(trip);
        return persistedTrip;
    }

    public Trip persistTrip(Trip trip) {

        boolean isNew = (1 > trip.getId());

        long tripId = 0L;

        try {
            db.beginTransaction();

            if (isNew) {
                tripId = tripDao.create(trip);
            }
            else {
                tripDao.update(trip);
            }

            // On trip creation there will be neither participants nor payments.

            db.setTransactionSuccessful();
        } catch (SQLException e) {
            Log.e(Rc.LT, "Error saving trip (transaction rolled back)", e);
            tripId = 0L;
        } finally {
            db.endTransaction();
        }
        if (isNew) {
            trip.setId(tripId);
        }
        return trip;
    }

    public Participant persistParticipantInTrip(long tripId, Participant
            participant) {
        Assert.notNull(tripId);

        boolean isNew = (1 > participant.getId());

        long participantId = 0L;
        ParticipantReference participantReference = new ParticipantReference(tripId, participant);
        try {
            db.beginTransaction();

            if (isNew) {
                participantId = participantDao.create(participantReference);
            }
            else {
                participantDao.update(participantReference);
            }

            db.setTransactionSuccessful();
        } catch (SQLException e) {
            Log.e(Rc.LT, "Error saving participant (transaction rolled back)", e);
            participantId = 0L;
        } finally {
            db.endTransaction();
        }
        if (isNew) {
            participant.setId(participantId);
        }
        return participant;
    }

    public Payment persistPaymentInTrip(long tripId, Payment payment) {

        if (Rc.debugOn) {
            Log.d(Rc.LT_DB, "persistPaymentInTrip()" + payment);
        }

        Assert.notNull(tripId);

        boolean isNew = (1 > payment.getId());


        if (payment.getPaymentDateTime() == null) {
            payment.setPaymentDateTime(new Date());
        }

        long paymentId = 0L;
        PaymentReference paymentReference = new PaymentReference(tripId, payment);
        try {
            db.beginTransaction();

            if (isNew) {
                paymentId = paymentDao.create(paymentReference);
            }
            else {
                paymentDao.update(paymentReference);
            }
            db.setTransactionSuccessful();
        } catch (SQLException e) {
            Log.e(Rc.LT, "Error saving participant (transaction rolled back)", e);
            paymentId = 0L;
        } finally {
            db.endTransaction();
        }
        if (isNew) {
            payment.setId(paymentId);
        }
        return payment;
    }

    public boolean doesTripAlreadyExist(String nameToCheck, long tripId) {
        return tripDao.doesTripAlreadyExist(nameToCheck, tripId);
    }

    public boolean oneOrLessTripsLeft() {
        return tripDao.onlyOneTripLeft();
    }

    public boolean doesParticipantAlreadyExist(String nameToCheck, long tripId, long participantId) {
        return participantDao.doesParticipantAlreadyExist(nameToCheck, tripId, participantId);
    }

    public List<ExchangeRate> findSuitableRates(Currency currencyFrom, Currency currencyTo) {
        return exchangeRateDao.findSuitableRates(currencyFrom, currencyTo);
    }

    public List<ExchangeRate> getAllExchangeRatesWithoutInversion() {
        return exchangeRateDao.getAllExchangeRatesWithoutInversion();
    }

    public ExchangeRate getExchangeRateById(Long technicalId) {
        return exchangeRateDao.getExchangeRateById(technicalId);
    }

    public boolean doesExchangeRateAlreadyExist(ExchangeRate exchangeRate) {
        return exchangeRateDao.doesExchangeRateAlreadyExist(exchangeRate);
    }

    public void persistExchangeRateUsedLast(ExchangeRate exchangeRateUsedLast) {
        try {
            db.beginTransaction();
            exchangeRateDao.persistExchangeRateUsedLast(exchangeRateUsedLast);
            db.setTransactionSuccessful();
        } catch (SQLException e) {
            Log.e(Rc.LT, "Error saving exchange rate preferences (transaction rolled back)", e);
        } finally {
            db.endTransaction();
        }
    }

    public boolean deleteExchangeRates(List<ExchangeRate> rows) {
        List<Long> idsToBeDeleted = new ArrayList<>();
        for (ExchangeRate rate : rows) {
            idsToBeDeleted.add(rate.getId());
        }
        return deleteExchangeRatesById(idsToBeDeleted);
    }

    public boolean deleteExchangeRatesById(List<Long> idsToBeDeleted) {
        boolean result = true;
        try {
            db.beginTransaction();
            exchangeRateDao.delete(idsToBeDeleted);
            db.setTransactionSuccessful();
        } catch (SQLException e) {
            Log.e(Rc.LT, "Error deleting exchange rates (transaction rolled back)", e);
            result = false;
        } finally {
            db.endTransaction();
        }
        return result;
    }

    public ExchangeRate persistExchangeRate(ExchangeRate rate) {
        ExchangeRate result = rate.doClone();
        boolean isNew = result.isNew();
        long rateId = 0L;
        /* TODO(ckoelle) Check for consistency how creating date is inserted. */
        Date currentDateTime = new Date();
        result.setUpdateDate(currentDateTime);
        try {
            db.beginTransaction();
            if (isNew) {
                result.setCreationDate(currentDateTime);
                rateId = exchangeRateDao.create(result);
            }
            else {
                exchangeRateDao.update(result);
            }
            db.setTransactionSuccessful();
        } catch (SQLException e) {
            rateId = 0L;
        } finally {
            db.endTransaction();
        }
        if (isNew) {
            result.setId(rateId);
        }
        return result;
    }

    public void persistImportedExchangeRate(ExchangeRate rate, boolean replaceWhenAlreadyImported) {
        if (rate == null) {
            if (Rc.debugOn) {
                Log.d(Rc.LT_IO, "persistImportedExchangeRate(): incoming value is null");
            }
            return;
        }
        ExchangeRate thingToBePersisted = null;

        List<ExchangeRate> existingRecords = exchangeRateDao.findExistingImportedRecords(rate);
        if (existingRecords.size() == 0) {
            rate.setId(0);
            thingToBePersisted = rate;
        }
        else {
            if (replaceWhenAlreadyImported) {
                List<Long> recordsToBeDeleted = new ArrayList<>();
                for (int i = 0; i < existingRecords.size(); i++) {
                    long idOfExistingRecord = existingRecords.get(i).getId();
                    if (thingToBePersisted == null) {
                        rate.setId(idOfExistingRecord);
                        thingToBePersisted = rate;
                    }
                    else {
                        recordsToBeDeleted.add(idOfExistingRecord);
                    }

                }
                if (recordsToBeDeleted.size() > 0) {
                    deleteExchangeRatesById(recordsToBeDeleted);
                }
            }
            // No replacing
            else {
                for (int i = 0; i < existingRecords.size(); i++) {
                    ExchangeRate existingRate = existingRecords.get(i);
                    if (existingRate.equalsFromImportPointOfView(rate)) {
                        rate.setId(existingRate.getId());
                        thingToBePersisted = rate;
                    }
                    if (thingToBePersisted != null) {
                        break;
                    }
                }
                if (thingToBePersisted == null) {
                    rate.setId(0);
                    thingToBePersisted = rate;
                }
            }
        }
        persistExchangeRate(thingToBePersisted);
    }

    /**
     * @param currency
     *            Can be null, if there is no target currency.
     */
    public CurrenciesUsed findUsedCurrenciesForTarget(Currency currency) {
        CurrenciesUsed result = new CurrenciesUsed();

        Entry<List<Currency>, List<Currency>> exchangeRateDaoResult = exchangeRateDao
                .findUsedCurrencies(currency).entrySet().iterator().next();
        result.setCurrenciesUsedMatching(exchangeRateDaoResult.getKey());
        result.setCurrenciesUsedUnmatched(exchangeRateDaoResult.getValue());

        exchangeRateDaoResult = exchangeRateDao
                .findCurrenciesInExchangeRates(currency).entrySet().iterator().next();
        result.setCurrenciesInExchangeRatesMatching(exchangeRateDaoResult.getKey());
        result.setCurrenciesInExchangeRatesUnmatched(exchangeRateDaoResult.getValue());

        if (Rc.debugOn) {
            Log.d(Rc.LT_DB, "Currencies fetched from ExchangeRateDao: " + result);
        }

        List<Currency> tripDaoResult = tripDao.findAllCurrenciesUsedInTrips();
        if (tripDaoResult.contains(currency)) {
            tripDaoResult.remove(currency);
        }
        result.setCurrenciesInTrips(tripDaoResult);
        if (Rc.debugOn) {
            Log.d(Rc.LT_DB, "Currencies fetched from ExchangeRateDao and TripDao: " + result);
        }
        return result;
    }

}
