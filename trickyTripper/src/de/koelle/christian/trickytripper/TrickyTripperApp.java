package de.koelle.christian.trickytripper;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Currency;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Environment;
import android.util.Log;
import android.util.TypedValue;
import de.koelle.christian.common.changelog.ChangeLog;
import de.koelle.christian.common.io.impl.AppFileWriter;
import de.koelle.christian.common.utils.Assert;
import de.koelle.christian.common.utils.CurrencyUtil;
import de.koelle.christian.common.utils.FileUtils;
import de.koelle.christian.trickytripper.activities.ExportActivity;
import de.koelle.christian.trickytripper.activities.ManageTripsActivity;
import de.koelle.christian.trickytripper.activities.MoneyTransferActivity;
import de.koelle.christian.trickytripper.activities.PaymentEditActivity;
import de.koelle.christian.trickytripper.activities.PreferencesActivity;
import de.koelle.christian.trickytripper.apputils.PrefWritrerReaderUtils;
import de.koelle.christian.trickytripper.constants.Rc;
import de.koelle.christian.trickytripper.constants.Rt;
import de.koelle.christian.trickytripper.constants.ViewMode;
import de.koelle.christian.trickytripper.controller.TripExpensesFktnController;
import de.koelle.christian.trickytripper.controller.TripExpensesViewController;
import de.koelle.christian.trickytripper.dataaccess.DataManager;
import de.koelle.christian.trickytripper.dataaccess.impl.DataManagerImpl;
import de.koelle.christian.trickytripper.decoupling.impl.ActivityResolverImpl;
import de.koelle.christian.trickytripper.decoupling.impl.ResourceResolverImpl;
import de.koelle.christian.trickytripper.export.Exporter;
import de.koelle.christian.trickytripper.export.impl.ExporterImpl;
import de.koelle.christian.trickytripper.factories.AmountFactory;
import de.koelle.christian.trickytripper.factories.ModelFactory;
import de.koelle.christian.trickytripper.model.Amount;
import de.koelle.christian.trickytripper.model.Debts;
import de.koelle.christian.trickytripper.model.ExportSettings;
import de.koelle.christian.trickytripper.model.ExportSettings.ExportOutputChannel;
import de.koelle.christian.trickytripper.model.Participant;
import de.koelle.christian.trickytripper.model.Payment;
import de.koelle.christian.trickytripper.model.PaymentCategory;
import de.koelle.christian.trickytripper.model.Trip;
import de.koelle.christian.trickytripper.model.TripSummary;
import de.koelle.christian.trickytripper.strategies.SumReport;
import de.koelle.christian.trickytripper.strategies.TripReportLogic;
import de.koelle.christian.trickytripper.ui.model.DialogState;

public class TrickyTripperApp extends Application implements TripExpensesViewController, TripExpensesFktnController {

    private static final String TAG_FKTN = "FktnController";

    private Trip tripToBeEdited;

    private final TripReportLogic tripReportLogic = new TripReportLogic();
    private final DialogState dialogState = new DialogState();
    private final AmountFactory amountFactory = new AmountFactory();

    private Collator defaultCollator;

    List<String> allAssetsList = null;

    private DataManager dataManager;
    private Exporter exporter;

    @Override
    public void onCreate() {
        super.onCreate();
        init();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        safeLoadedTripIdToPrefs();
        FileUtils.deleteAllFiles(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        safeLoadedTripIdToPrefs();
        FileUtils.deleteAllFiles(this);
    }

    public SumReport getSumReport() {
        return getTripToBeEdited().getSumReport();
    }

    public Map<Participant, Debts> getDebts() {
        return getTripToBeEdited().getDebts();
    }

    public void init() {

        FileUtils.deleteAllFiles(this);
        defaultCollator = Collator.getInstance(getLocale());
        defaultCollator.setStrength(Rc.DEFAULT_COLLATOR_STRENGTH);

        SharedPreferences prefs = getPrefs();
        long tripId = PrefWritrerReaderUtils.getIdOfTripLastEdited(prefs);

        if (Log.isLoggable(Rc.LT, Log.DEBUG)) {
            Log.d(Rc.LT, "init() id of last trip=" + tripId);
        }

        dataManager = new DataManagerImpl(getBaseContext());
        tripToBeEdited = dataManager.loadTripById(tripId);

        exporter = new ExporterImpl(new AppFileWriter(getApplicationContext()));

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

    private Locale getLocale() {
        return getResources().getConfiguration().locale;
    }

    private SharedPreferences getPrefs() {
        return getSharedPreferences(Rc.PREFS_NAME_ID, Rc.PREFS_MODE);
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

    private void safeLoadedTripIdToPrefs() {
        if (Log.isLoggable(Rc.LT, Log.DEBUG)) {
            Log.d(Rc.LT, "safeLoadedTripIdToPrefs() id of last trip=" + tripToBeEdited.getId());
        }
        PrefWritrerReaderUtils.saveIdOfTripLastEdited(getEditingPrefsEditor(), tripToBeEdited.getId());
    }

    private Editor getEditingPrefsEditor() {
        SharedPreferences prefs = getPrefs();
        Editor prefsEditor = prefs.edit();
        return prefsEditor;
    }

    public Currency getDefaultBaseCurrency() {
        return PrefWritrerReaderUtils.loadDefaultCurrency(getPrefs(), getResources());
    }

    public String getCurrencySymbolOfTripLoaded(boolean wrapInBrackets) {
        return CurrencyUtil.getSymbolToCurrency(getResources(), tripToBeEdited.getBaseCurrency(), wrapInBrackets);
    }

    public boolean isSmartHelpEnabled() {
        return PrefWritrerReaderUtils.isSmartHelpEnabled(getPrefs(), getEditingPrefsEditor())
                && !(new ChangeLog(this).firstRun());
    }

    public Collator getDefaultStringCollator() {
        return defaultCollator;
    }

    public boolean checkIfInAssets(String assetName) {
        if (allAssetsList == null) {
            AssetManager am = getAssets();
            try {
                allAssetsList = Arrays.asList(am.list(""));
            }
            catch (IOException e) {
            }
        }
        return allAssetsList.contains(assetName) ? true : false;
    }

    public boolean hasLoadedTripPayments() {
        return !(tripToBeEdited == null || tripToBeEdited.getPayments() == null || tripToBeEdited.getPayments()
                .isEmpty());
    }

    public boolean hasTripPayments(TripSummary tripSummary) {
        Assert.notNull(tripSummary);
        Assert.notNull(tripSummary.getId());
        return dataManager.hasTripPayments(tripSummary.getId());
    }

    /* ========================= my interfaces ======================== */
    public void openCreatePayment(Participant p) {
        Class<? extends Activity> activity = PaymentEditActivity.class;
        Map<String, Serializable> extras = new HashMap<String, Serializable>();
        extras.put(Rc.ACTIVITY_PARAM_KEY_PARTICIPANT_ID, p.getId());
        startActivityWithParams(extras, activity, ViewMode.CREATE);
    }

    public void openTransferMoney(Participant participant) {
        Class<? extends Activity> activity = MoneyTransferActivity.class;
        Map<String, Serializable> extras = new HashMap<String, Serializable>();
        extras.put(Rc.ACTIVITY_PARAM_KEY_PARTICIPANT, participant);
        startActivityWithParams(extras, activity, ViewMode.NONE);
    }

    public void openExport() {
        Class<? extends Activity> activity = ExportActivity.class;
        startActivityWithParams(new HashMap<String, Serializable>(), activity, ViewMode.NONE);
    }

    public void openSettings() {
        Class<? extends Activity> activity = PreferencesActivity.class;
        startActivityWithParams(new HashMap<String, Serializable>(), activity, ViewMode.NONE);
    }

    public void openManageTrips() {
        Class<? extends Activity> activity = ManageTripsActivity.class;
        Map<String, Serializable> extras = new HashMap<String, Serializable>();
        startActivityWithParams(extras, activity, ViewMode.CREATE);
    }

    public void openEditPayment(Payment p) {
        Class<? extends Activity> activity = PaymentEditActivity.class;
        Map<String, Serializable> extras = new HashMap<String, Serializable>();
        extras.put(Rc.ACTIVITY_PARAM_KEY_PAYMENT_ID, p.getId());
        startActivityWithParams(extras, activity, ViewMode.EDIT);
    }

    public void switchTabs(Rt tabId) {

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
        }
        else {
            int index = getTripToBeEdited().getParticipant().indexOf(participantPersisted);
            getTripToBeEdited().getParticipant().set(index, participantPersisted);
        }

        return true;
    }

    public boolean deleteParticipant(Participant participant) {
        if (!isParticipantDeleteable(participant)) {
            return false;
        }
        dataManager.deleteParticipant(participant.getId());
        getTripToBeEdited().getParticipant().remove(participant);
        getTripToBeEdited().getDebts().remove(participant);
        getTripToBeEdited().getSumReport().removeParticipant(participant);
        return true;
    }

    public boolean isParticipantDeleteable(Participant participant) {
        return !tripToBeEdited.partOfPayments(participant);
    }

    public TripExpensesViewController getViewController() {
        return this;
    }

    public TripExpensesFktnController getFktnController() {
        return this;
    }

    public boolean oneOrLessTripsLeft() {
        return dataManager.oneOrLessTripsLeft();
    }

    public void deleteTrip(TripSummary tripSummary) {
        long id = tripSummary.getId();
        dataManager.deleteTrip(tripSummary);
        if (tripToBeEdited != null && id == tripToBeEdited.getId()) {
            tripToBeEdited = null;
        }
    }

    public void deletePayment(Payment payment) {
        dataManager.deletePayment(payment.getId());
        tripToBeEdited.getPayments().remove(payment);
        updateAllTransientData(tripToBeEdited, tripReportLogic);
    }

    private void logPayment(String tag, String addition, Payment newPayment) {
        if (Log.isLoggable(tag, Log.DEBUG)) {
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

            final Collator collator = getDefaultStringCollator();
            Collections.sort(result, new Comparator<Participant>() {
                public int compare(Participant object1, Participant object2) {
                    return collator.compare(object1.getName(), object2.getName());
                }
            });
        }
        return result;

    }

    private void startActivityWithParams(Map<String, Serializable> extras, Class<? extends Activity> activity,
            ViewMode viewMode) {
        Intent intent = new Intent().setClass(this, activity);

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        for (Entry<String, Serializable> extra : extras.entrySet()) {
            intent.putExtra(extra.getKey(), extra.getValue());
        }
        if (viewMode != null) {
            intent.putExtra(Rc.ACTIVITY_PARAM_KEY_VIEW_MODE, viewMode);
        }
        startActivity(intent);
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
        Trip trip = null;
        if (isNew) {
            trip = ModelFactory.createTrip(summary.getBaseCurrency(), summary.getName());
        }
        else {
            trip = dataManager.loadTripById(summary.getId());
            trip.setName(summary.getName());
            trip.setBaseCurrency(summary.getBaseCurrency());
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

        logPayment(TAG_FKTN, "persistPayment()", payment);

        long incomingId = payment.getId();
        boolean isNew = (1 > incomingId);

        Payment persistendPayment = dataManager.persistPaymentInTrip(tripToBeEdited.getId(), payment);
        if (!isNew) {
            removePaymentFromList(incomingId, this.tripToBeEdited.getPayments());
        }
        this.tripToBeEdited.getPayments().add(persistendPayment);
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

    public int convertDipToPixels(int dipValue) {
        Resources r = getResources();
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, r.getDisplayMetrics());
        return (int) px;
    }

    /* ======================== export ============================== */
    public ExportSettings getDefaultExportSettings() {
        return PrefWritrerReaderUtils.loadExportSettings(getPrefs());
    }

    public List<ExportOutputChannel> getEnabledExportOutputChannel() {
        List<ExportOutputChannel> result = new ArrayList<ExportSettings.ExportOutputChannel>();
        boolean testExport = false;
        if (testExport) {
            result.add(ExportOutputChannel.SD_CARD);
        }
        else {
            Intent tweetIntent = new Intent(Rc.STREAM_SENDING_INTENT);
            tweetIntent.setType(Rc.STREAM_SENDING_MIME);
            final PackageManager packageManager = getPackageManager();
            List<ResolveInfo> list = packageManager.queryIntentActivities(
                    tweetIntent, PackageManager.MATCH_DEFAULT_ONLY);
            for (ExportOutputChannel channel : ExportOutputChannel.values()) {
                for (ResolveInfo info : list) {
                    if (channel.getPackageName().startsWith(info.activityInfo.packageName)) {
                        result.add(channel);
                    }
                }
            }
        }
        File externalStorageDirectory = Environment.getExternalStorageDirectory();
        if (result.contains(ExportOutputChannel.SD_CARD) && (externalStorageDirectory == null
                || !externalStorageDirectory.canRead())) {
            result.remove(ExportOutputChannel.SD_CARD);
        }

        return result;
    }

    public List<File> exportReport(ExportSettings settings, Participant selectedParticipant, Activity activity) {
        if (Log.isLoggable(Rc.LT, Log.DEBUG)) {
            Log.d(Rc.LT_INPUT, "exportReport() settings=" + settings + " selected=" + selectedParticipant);
        }
        List<Participant> participantsForReport = new ArrayList<Participant>();
        if (selectedParticipant != null) {
            participantsForReport.add(selectedParticipant);
        }
        else {
            participantsForReport.addAll(tripToBeEdited.getParticipant());
        }
        PrefWritrerReaderUtils.saveExportSettings(getEditingPrefsEditor(), settings);
        return exporter.exportReport(settings,
                participantsForReport,
                tripToBeEdited,
                new ResourceResolverImpl(this.getResources()), new ActivityResolverImpl(activity),
                getAmountFactory());

    }

    /* ======================= getter/setter ======================= */

    public Trip getTripToBeEdited() {
        return tripToBeEdited;
    }

    public void setTripToBeEdited(Trip tripToBeEdited) {
        this.tripToBeEdited = tripToBeEdited;
    }

    public DialogState getDialogState() {
        return dialogState;
    }

    public AmountFactory getAmountFactory() {
        return amountFactory;
    }

}
