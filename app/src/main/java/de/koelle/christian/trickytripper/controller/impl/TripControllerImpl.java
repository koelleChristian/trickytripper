package de.koelle.christian.trickytripper.controller.impl;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Currency;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import de.koelle.christian.common.utils.Assert;
import de.koelle.christian.common.utils.CurrencyUtil;
import de.koelle.christian.trickytripper.apputils.PrefWriterReaderUtils;
import de.koelle.christian.trickytripper.constants.Rc;
import de.koelle.christian.trickytripper.controller.MiscController;
import de.koelle.christian.trickytripper.controller.TripController;
import de.koelle.christian.trickytripper.controller.TripResolver;
import de.koelle.christian.trickytripper.dataaccess.DataManager;
import de.koelle.christian.trickytripper.decoupling.PrefsResolver;
import de.koelle.christian.trickytripper.factories.AmountFactory;
import de.koelle.christian.trickytripper.factories.ModelFactory;
import de.koelle.christian.trickytripper.model.Amount;
import de.koelle.christian.trickytripper.model.Debts;
import de.koelle.christian.trickytripper.model.Participant;
import de.koelle.christian.trickytripper.model.Payment;
import de.koelle.christian.trickytripper.model.PaymentCategory;
import de.koelle.christian.trickytripper.model.Trip;
import de.koelle.christian.trickytripper.model.TripSummary;
import de.koelle.christian.trickytripper.strategies.SumReport;
import de.koelle.christian.trickytripper.strategies.TripReportLogic;
import de.koelle.christian.trickytripper.ui.model.DialogState;

public class TripControllerImpl implements TripController, TripResolver {

    private final DataManager dataManager;

    private final TripReportLogic tripReportLogic = new TripReportLogic();
    private final DialogState dialogState = new DialogState();
    private final AmountFactory amountFactory = new AmountFactory();
    private final PrefsResolver prefsResolver;
    private final MiscController miscController;
    private final Context context;

    private Trip tripToBeEdited;

    public TripControllerImpl(Context context, DataManager dataManager, PrefsResolver prefsResolver,
                              MiscController miscController) {
        this.context = context;
        this.dataManager = dataManager;
        this.prefsResolver = prefsResolver;
        this.miscController = miscController;

        SharedPreferences prefs = prefsResolver.getPrefs();
        long tripId = PrefWriterReaderUtils.loadIdOfTripLastEdited(prefs);

        if (Rc.debugOn) {
            Log.d(Rc.LT, "init() id of last trip=" + tripId);
        }
        tripToBeEdited = dataManager.loadTripById(tripId);

        if (tripToBeEdited == null) {
            List<TripSummary> allSummaries = dataManager.getAllTripSummaries();
            if (allSummaries.size() > 0) {
                tripToBeEdited = dataManager.loadTripById(dataManager.getAllTripSummaries().get(0).getId());
                if (tripToBeEdited == null) {
                    return;
                }
            }
        }
        initPostTripLoad();
    }

    private void initPostTripLoad() {
        if (tripToBeEdited != null) {
            updateOtherAspectsInAppPostTripLoad();
            createTransientData();
            safeLoadedTripIdToPrefs();
        }
    }

    private void updateOtherAspectsInAppPostTripLoad() {
        amountFactory.setCurrency(tripToBeEdited.getBaseCurrency());
        tripReportLogic.setAmountFactory(amountFactory);
        getDialogState().setParticipantReporting(null);
    }

    private void createTransientData() {
        updateAllTransientData(tripToBeEdited, tripReportLogic);
    }

    public void safeLoadedTripIdToPrefs() {

        if (tripToBeEdited != null) {
            if (Rc.debugOn) {
                Log.d(Rc.LT,
                        "safeLoadedTripIdToPrefs() id of last trip="
                                + tripToBeEdited.getId());
            }
            PrefWriterReaderUtils
                    .saveIdOfTripLastEdited(prefsResolver.getEditingPrefsEditor(), tripToBeEdited.getId());
        }
    }

    public boolean hasLoadedTripPayments() {
        return !(tripToBeEdited == null || tripToBeEdited.getPayments() == null || tripToBeEdited.getPayments()
                .isEmpty());
    }

    public Currency getLoadedTripBaseCurrency() {
        return getTripLoaded().getBaseCurrency();
    }

    public boolean hasTripPayments(TripSummary tripSummary) {
        Assert.notNull(tripSummary);
        Assert.notNull(tripSummary.getId());
        return dataManager.hasTripPayments(tripSummary.getId());
    }

    public ArrayList<String> getDescriptions() {
        return dataManager.getAllPaymentDescriptionsInTrip(getTripLoaded().getId());
    }

    public boolean persistParticipant(Participant participant) {

        boolean isNew = (1 > participant.getId());

        if (dataManager.doesParticipantAlreadyExist(participant.getName(), tripToBeEdited.getId(),
                participant.getId())) {
            return false;
        }
        Participant participantPersisted = dataManager.persistParticipantInTrip(tripToBeEdited.getId(), participant);

        if (!getTripToBeEdited().getParticipant().contains(participantPersisted)) {
            getTripToBeEdited().getParticipant().add(participant);
        }
        if (isNew) {
            getTripToBeEdited().getDebts().put(participant, new Debts());
            getTripToBeEdited().getSumReport().addNewParticipant(participant, amountFactory.createAmount());
        } else {
            int index = getTripToBeEdited().getParticipant().indexOf(participantPersisted);
            getTripToBeEdited().getParticipant().set(index, participantPersisted);
        }

        return true;
    }

    public boolean deleteParticipant(Participant participant) {
        if (!isParticipantDeletable(participant)) {
            return false;
        }
        dataManager.deleteParticipant(participant.getId());
        getTripToBeEdited().getParticipant().remove(participant);
        getTripToBeEdited().getDebts().remove(participant);
        getTripToBeEdited().getSumReport().removeParticipant(participant);
        return true;
    }

    public boolean isParticipantDeletable(Participant participant) {
        return !tripToBeEdited.partOfPayments(participant);
    }

    public boolean oneOrLessTripsLeft() {
        return dataManager.oneOrLessTripsLeft();
    }

    public void deleteTrip(TripSummary tripSummary) {
        long id = tripSummary.getId();
        dataManager.deleteTrip(tripSummary);
        if (tripToBeEdited != null && id == tripToBeEdited.getId()) {
            tripToBeEdited = null;
            TripSummary firstRemainingTrip = getAllTrips().get(0);// There has to be at least one.
            loadTrip(firstRemainingTrip);
        }
    }

    public void deletePayment(Payment payment) {
        dataManager.deletePayment(payment.getId());
        tripToBeEdited.getPayments().remove(payment);
        updateAllTransientData(tripToBeEdited, tripReportLogic);
    }

    private void logPayment(String tag, String addition, Payment newPayment) {
        if (Rc.debugOn) {
            Log.d(tag, addition + " Cat=" + newPayment.getCategory().toString());
            for (Entry<Participant, Amount> entry : newPayment.getParticipantToPayment().entrySet()) {
                Log.d(tag,
                        addition + " payment[" + " participant=" + entry.getKey().getName() + ", amount="
                                + entry.getValue() + "]");
            }
            for (Entry<Participant, Amount> entry : newPayment.getParticipantToSpending().entrySet()) {
                Log.d(tag,
                        addition + " spending[" + " participant=" + entry.getKey().getName() + ", amount="
                                + entry.getValue() + "]");
            }
        }

    }

    public Payment prepareNewPayment(long idParticipant) {
        Participant participant = findParticipantByUuid(idParticipant);
        Payment result = new Payment();
        result.setCategory(PaymentCategory.OTHER);
        result.setPaymentDateTime(new Date());
        result.getParticipantToPayment().put(participant, amountFactory.createAmount());
        return result;
    }

    private Participant findParticipantByUuid(long idParticipant) {
        for (Participant p : tripToBeEdited.getParticipant()) {
            if (p.getId() == idParticipant) {
                return p;
            }
        }
        return null;
    }

    public Payment loadPayment(long paymentId) {
        for (Payment payment : tripToBeEdited.getPayments()) {
            if (payment.getId() == paymentId) {
                return payment;
            }
        }
        return null;
    }

    public List<Participant> getAllParticipants(boolean onlyActive) {
        List<Participant> result = new ArrayList<Participant>();
        for (Participant p : this.tripToBeEdited.getParticipant()) {
            if ((onlyActive && p.isActive()) || !onlyActive) {
                result.add(p);
            }
        }
        return result;

    }

    public List<Participant> getAllParticipants(boolean onlyActive, boolean sorted) {
        List<Participant> result = getAllParticipants(onlyActive);
        if (sorted) {

            final Collator collator = miscController.getDefaultStringCollator();
            Collections.sort(result, new Comparator<Participant>() {
                public int compare(Participant object1, Participant object2) {
                    return collator.compare(object1.getName(), object2.getName());
                }
            });
        }
        return result;

    }

    public List<TripSummary> getAllTrips() {
        return dataManager.getAllTripSummaries();
    }

    public Trip getTripLoaded() {
        return tripToBeEdited;
    }

    public boolean persist(TripSummary summary) {
        boolean isNew = (1 > summary.getId());

        if (dataManager.doesTripAlreadyExist(summary.getName(), summary.getId())) {
            return false;
        }
        Trip trip;
        if (isNew) {
            trip = ModelFactory.createTrip(summary.getBaseCurrency(), summary.getName());
        } else {
            trip = dataManager.loadTripById(summary.getId());
            trip.setName(summary.getName());
            trip.setBaseCurrency(summary.getBaseCurrency());
            if (tripToBeEdited != null && tripToBeEdited.getId() == summary.getId()) {
                tripToBeEdited.setName(summary.getName());
                tripToBeEdited.setBaseCurrency(summary.getBaseCurrency());
            }
        }
        dataManager.persistTrip(trip);


        return true;
    }

    public void loadTrip(TripSummary summary) {
        tripToBeEdited = dataManager.loadTripById(summary.getId());
        initPostTripLoad();
    }

    public boolean persistAndLoadTrip(TripSummary summary) {

        if (dataManager.doesTripAlreadyExist(summary.getName(), summary.getId())) {
            return false;
        }
        Trip persistedTrip = dataManager.persistTripBySummary(summary);
        tripToBeEdited = persistedTrip;
        initPostTripLoad();

        return true;
    }

    /**
     * Note: If a existing record comes it, it is a clone, otherwise input would
     * amend the transient data.
     */
    public void persistPayment(Payment payment) {
        payment.removeBlankEntries();

        logPayment(Rc.LT, "persistPayment()", payment);

        long incomingId = payment.getId();
        boolean isNew = (1 > incomingId);

        Payment persistedPayment = dataManager.persistPaymentInTrip(tripToBeEdited.getId(), payment);
        if (!isNew) {
            removePaymentFromList(incomingId, this.tripToBeEdited.getPayments());
        }
        this.tripToBeEdited.getPayments().add(persistedPayment);
        updateAllTransientData(tripToBeEdited, tripReportLogic);
    }

    private void removePaymentFromList(long incomingId, List<Payment> transientPayments) {
        Payment toBeRemoved = null;
        for (Payment p : transientPayments) {
            if (p.getId() == incomingId) {
                toBeRemoved = p;
                break;
            }
        }
        Assert.notNull(toBeRemoved);
        transientPayments.remove(toBeRemoved);
    }

    private void updateAllTransientData(Trip tripToBeEdited2, TripReportLogic tripReportLogic) {

        List<Participant> participants = tripToBeEdited2.getParticipant();
        List<Payment> payments = tripToBeEdited2.getPayments();

        SumReport sumReport = tripReportLogic.createSumReport(participants, payments);
        Map<Participant, Debts> debts = tripReportLogic.createDebts2(participants, sumReport.getBalanceByUser());
        tripToBeEdited2.setDebts(debts);
        tripToBeEdited2.setSumReport(sumReport);
    }

    public Trip getTripToBeEdited() {
        return tripToBeEdited;
    }

    public void setTripToBeEdited(Trip tripToBeEdited) {
        this.tripToBeEdited = tripToBeEdited;
    }

    public SumReport getSumReport() {
        return getTripToBeEdited().getSumReport();
    }

    public Map<Participant, Debts> getDebts() {
        return getTripToBeEdited().getDebts();
    }

    public Trip getTripInEditing() {
        return tripToBeEdited;
    }

    public DialogState getDialogState() {
        return dialogState;
    }

    public AmountFactory getAmountFactory() {
        return amountFactory;
    }

    public String getLoadedTripCurrencySymbol(boolean wrapInBrackets) {
        return CurrencyUtil.getSymbolToCurrency(context.getResources(), tripToBeEdited.getBaseCurrency(),
                wrapInBrackets);
    }

    public void reloadTrip() {
        TripSummary summary = new TripSummary();
        summary.setId(getTripLoaded().getId());
        loadTrip(summary);
    }


}
