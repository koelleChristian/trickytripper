package de.koelle.christian.trickytripper.dataaccess.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import de.koelle.christian.common.utils.Assert;
import de.koelle.christian.trickytripper.constants.Rc;
import de.koelle.christian.trickytripper.dataaccess.DataManager;
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
import de.koelle.christian.trickytripper.model.Participant;
import de.koelle.christian.trickytripper.model.Payment;
import de.koelle.christian.trickytripper.model.Trip;
import de.koelle.christian.trickytripper.model.TripSummary;

public class DataManagerImpl implements DataManager {
    private final Context context;

    private final SQLiteDatabase db;

    private final TripDao tripDao;
    private final ParticipantDao participantDao;
    private final PaymentDao paymentDao;

    public DataManagerImpl(Context context) {

        this.context = context;

        SQLiteOpenHelper openHelper = new OpenHelper(this.context);
        db = openHelper.getWritableDatabase();
        if (Log.isLoggable(Rc.LT, Log.DEBUG)) {
            Log.d(Rc.LT, "DataManagerImplBackup created, db open status: " + db.isOpen());
        }

        tripDao = new TripDao(db);
        participantDao = new ParticipantDao(db);
        paymentDao = new PaymentDao(db);
    }

    public void removeAll() {
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
        }
        catch (SQLException e) {
            Log.e(Rc.LT, "Error deleting trip (transaction rolled back)", e);
        }
        finally {
            db.endTransaction();
        }
    }

    public void deletePayment(long paymentId) {

        try {
            db.beginTransaction();
            paymentDao.deletePayment(paymentId);
            db.setTransactionSuccessful();
        }
        catch (SQLException e) {
            Log.e(Rc.LT, "Error deleting payment (transaction rolled back)", e);
        }
        finally {
            db.endTransaction();
        }
    }

    public boolean deleteParticipant(long participantId) {
        boolean result = true;
        try {
            db.beginTransaction();
            participantDao.deleteParticipant(participantId);
            db.setTransactionSuccessful();
        }
        catch (SQLException e) {
            Log.e(Rc.LT, "Error deleting participant (transaction rolled back)", e);
            result = false;
        }
        finally {
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
        List<Payment> result = new ArrayList<Payment>();
        if (interimPaymentResult == null) {
            return result;
        }
        Map<Long, Participant> participantById = new HashMap<Long, Participant>();
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
        }
        catch (SQLException e) {
            Log.e(Rc.LT, "Error saving trip (transaction rolled back)", e);
            tripId = 0L;
        }
        finally {
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
        }
        catch (SQLException e) {
            Log.e(Rc.LT, "Error saving participant (transaction rolled back)", e);
            participantId = 0L;
        }
        finally {
            db.endTransaction();
        }
        if (isNew) {
            participant.setId(participantId);
        }
        return participant;
    }

    public Payment persistPaymentInTrip(long tripId, Payment payment) {

        if (Log.isLoggable(Rc.LT_DB, Log.DEBUG)) {
            Log.d(Rc.LT_DB, "persistPaymentInTrip()" + payment);
        }

        Assert.notNull(tripId);

        boolean isNew = (1 > payment.getId());

        if (isNew) {
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
        }
        catch (SQLException e) {
            Log.e(Rc.LT, "Error saving participant (transaction rolled back)", e);
            paymentId = 0L;
        }
        finally {
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
}
