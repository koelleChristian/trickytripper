package de.koelle.christian.trickytripper.controller;

import java.io.File;
import java.text.Collator;
import java.util.Currency;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import de.koelle.christian.common.ui.filter.DecimalNumberInputUtil;
import de.koelle.christian.trickytripper.model.Debts;
import de.koelle.christian.trickytripper.model.ExportSettings;
import de.koelle.christian.trickytripper.model.ExportSettings.ExportOutputChannel;
import de.koelle.christian.trickytripper.model.Participant;
import de.koelle.christian.trickytripper.model.Payment;
import de.koelle.christian.trickytripper.model.Trip;
import de.koelle.christian.trickytripper.model.TripSummary;
import de.koelle.christian.trickytripper.strategies.SumReport;

public interface TripExpensesFktnController {

    /* ========= Participant related functions. ========= */

    boolean persistParticipant(Participant participant);

    boolean deleteParticipant(Participant participant);

    public boolean isParticipantDeleteable(Participant participant);

    List<Participant> getAllParticipants(boolean onlyActive);

    List<Participant> getAllParticipants(boolean onlyActive, boolean sorted);

    /* ========= Payment related functions. ========= */

    Payment prepareNewPayment(long participantId);

    void persistPayment(Payment newPayment);

    Payment loadPayment(long paymentId);

    void deletePayment(Payment payment);

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

    /* ================ Export ==================== */
    ExportSettings getDefaultExportSettings();

    List<File> exportReport(ExportSettings settings, Participant selectedParticipant, Activity activity);

    List<ExportOutputChannel> getEnabledExportOutputChannel();

    /* ================ Misc ==================== */

    Currency getDefaultBaseCurrency();

    String getCurrencySymbolOfTripLoaded(boolean wrapInBrackets);

    boolean isSmartHelpEnabled();

    Collator getDefaultStringCollator();

    DecimalNumberInputUtil getDecimalNumberInputUtil();

    boolean checkIfInAssets(String assetName);

    boolean hasLoadedTripPayments();

}
