package de.koelle.christian.trickytripper.controller.impl;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Currency;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import de.koelle.christian.trickytripper.activities.CurrencyCalculatorActivity;
import de.koelle.christian.trickytripper.activities.CurrencySelectionActivity;
import de.koelle.christian.trickytripper.activities.CurrencySelectionActivity.CurrencySelectionMode;
import de.koelle.christian.trickytripper.activities.ExchangeRateDeleteActivity;
import de.koelle.christian.trickytripper.activities.ExchangeRateEditActivity;
import de.koelle.christian.trickytripper.activities.ExchangeRateImportActivity;
import de.koelle.christian.trickytripper.activities.ExportActivity;
import de.koelle.christian.trickytripper.activities.MoneyTransferActivity;
import de.koelle.christian.trickytripper.activities.ParticipantEditActivity;
import de.koelle.christian.trickytripper.activities.ParticipantSelectionActivity;
import de.koelle.christian.trickytripper.activities.PaymentEditActivity;
import de.koelle.christian.trickytripper.activities.PreferencesActivity;
import de.koelle.christian.trickytripper.activities.TripEditActivity;
import de.koelle.christian.trickytripper.constants.Rc;
import de.koelle.christian.trickytripper.constants.ViewMode;
import de.koelle.christian.trickytripper.controller.ViewController;
import de.koelle.christian.trickytripper.dialogs.DatePickerDialogFragment;
import de.koelle.christian.trickytripper.dialogs.DeleteDialogFragment;
import de.koelle.christian.trickytripper.dialogs.HelpDialogFragment;
import de.koelle.christian.trickytripper.model.Amount;
import de.koelle.christian.trickytripper.model.ExchangeRate;
import de.koelle.christian.trickytripper.model.Participant;
import de.koelle.christian.trickytripper.model.Payment;
import de.koelle.christian.trickytripper.model.TripSummary;

public class ViewControllerImpl implements ViewController {

    private final Context context;

    public ViewControllerImpl(Context context) {
        this.context = context;
    }

    public void openEditParticipant(Participant participant) {
        Class<? extends Activity> activity = ParticipantEditActivity.class;
        Map<String, Serializable> extras = new HashMap<String, Serializable>();
        extras.put(Rc.ACTIVITY_PARAM_PARTICIPANT_EDIT_IN_PARTICIPANT, participant);
        startActivityWithParams(extras, activity, ViewMode.EDIT);
    }

    public void openCreateParticipant() {
        Class<? extends Activity> activity = ParticipantEditActivity.class;
        startActivityWithParams(new HashMap<String, Serializable>(), activity, ViewMode.CREATE);
    }

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

    public void openImportExchangeRates(Activity caller, Currency... currencies) {
        Class<? extends Activity> activity = ExchangeRateImportActivity.class;
        HashMap<String, Serializable> extras = new HashMap<String, Serializable>();
        if (currencies != null && currencies.length > 0) {
            ArrayList<Currency> currencyList = new ArrayList<Currency>();
            Collections.addAll(currencyList, currencies);
            extras.put(Rc.ACTIVITY_PARAM_IMPORT_EXCHANGE_RATES_IN_CURRENCY_LIST, currencyList);
        }
        startActivityWithParamsForResult(extras, activity, ViewMode.NONE,
                Rc.ACTIVITY_REQ_CODE_EXCHANGE_RATE_MANAGEMENT, caller);
    }

    public void openEditTrip(Activity caller, TripSummary tripSummary) {
        Class<? extends Activity> activity = TripEditActivity.class;
        HashMap<String, Serializable> extras = new HashMap<String, Serializable>();
        ViewMode viewMode;
        if (tripSummary == null) {
            viewMode = ViewMode.CREATE;
        } else {
            viewMode = ViewMode.EDIT;
            extras.put(Rc.ACTIVITY_PARAM_TRIP_EDIT_IN_TRIP_SUMMARY, tripSummary);
        }
        startActivityWithParamsForResult(extras, activity, viewMode, Rc.ACTIVITY_REQ_CODE_EDIT_TRIP, caller);

    }

    public void openDeleteExchangeRates(Activity caller, Currency... currencies) {
        Class<? extends Activity> activity = ExchangeRateDeleteActivity.class;
        HashMap<String, Serializable> extras = new HashMap<String, Serializable>();
        if (currencies != null && currencies.length > 0) {
            ArrayList<Currency> currencyList = new ArrayList<Currency>();
            Collections.addAll(currencyList, currencies);
            extras.put(Rc.ACTIVITY_PARAM_DELETE_EXCHANGE_RATES_IN_CURRENCY_LIST, currencyList);
        }
        startActivityWithParamsForResult(extras, activity, ViewMode.NONE,
                Rc.ACTIVITY_REQ_CODE_EXCHANGE_RATE_MANAGEMENT, caller);

    }

    public void openCreateExchangeRate(Activity caller, Currency fromCurrency) {
        openEditExchangeRate(caller, null, fromCurrency);
    }

    public void openEditExchangeRate(Activity caller, ExchangeRate exchangeRate) {
        openEditExchangeRate(caller, exchangeRate, null);
    }

    public void openCreateExchangeRate(Activity caller) {
        openEditExchangeRate(caller, null, null);
    }

    public void openEditExchangeRate(Activity caller, ExchangeRate exchangeRate, Currency fromCurrency) {
        Class<? extends Activity> activity = ExchangeRateEditActivity.class;
        ViewMode viewMode = ViewMode.CREATE;
        Map<String, Serializable> extras = new HashMap<String, Serializable>();
        if (exchangeRate != null) {
            extras.put(Rc.ACTIVITY_PARAM_EDIT_EXCHANGE_RATE_IN_RATE_TECH_ID, exchangeRate.getId());
            viewMode = ViewMode.EDIT;
        }
        if (fromCurrency != null) {
            extras.put(Rc.ACTIVITY_PARAM_EDIT_EXCHANGE_RATE_IN_SOURCE_CURRENCY, fromCurrency);
        }
        startActivityWithParams(extras, activity, viewMode);
    }

    public void openExport() {
        Class<? extends Activity> activity = ExportActivity.class;
        startActivityWithParams(new HashMap<String, Serializable>(), activity, ViewMode.NONE);
    }

    public void openSettings() {
        Class<? extends Activity> activity = PreferencesActivity.class;
        startActivityWithParams(new HashMap<String, Serializable>(), activity, ViewMode.NONE);
    }

    public void openMoneyCalculatorView(Amount amount, int resultViewId, Activity caller) {
        Class<? extends Activity> activity = CurrencyCalculatorActivity.class;
        Map<String, Serializable> extras = new HashMap<String, Serializable>();
        extras.put(Rc.ACTIVITY_PARAM_CURRENCY_CALCULATOR_IN_VALUE, amount.getValue());
        extras.put(Rc.ACTIVITY_PARAM_CURRENCY_CALCULATOR_IN_RESULT_VIEW_ID, resultViewId);
        startActivityWithParamsForResult(extras, activity, ViewMode.NONE,
                Rc.ACTIVITY_REQ_CODE_CURRENCY_CALCULATOR, caller);
    }

    public void openParticipantSelection(Activity caller, ArrayList<Participant> participantsInUse,
                                         Amount currentTotalAmount, boolean isPayerSelection, ArrayList<Participant> allRelevantParticipants) {
        Class<? extends Activity> activity = ParticipantSelectionActivity.class;
        Map<String, Serializable> extras = new HashMap<>();
        extras.put(Rc.ACTIVITY_PARAM_PARTICIPANT_SEL_IN_PARTICIPANTS_IN_USE, participantsInUse);
        extras.put(Rc.ACTIVITY_PARAM_PARTICIPANT_SEL_IN_TOTAL_PAYMENT_AMOUNT, currentTotalAmount);
        extras.put(Rc.ACTIVITY_PARAM_PARTICIPANT_SEL_IN_IS_PAYMENT, isPayerSelection);
        if(allRelevantParticipants != null){
            extras.put(Rc.ACTIVITY_PARAM_PARTICIPANT_SEL_IN_ALL_RELEVANT_PARTICIPANTS, allRelevantParticipants);
        }
        startActivityWithParamsForResult(extras, activity, ViewMode.NONE,
                Rc.ACTIVITY_REQ_CODE_PARTICIPANT_SELECT, caller);

    }

    public void openCurrencySelectionForNewExchangeRate(Activity caller, Currency targetCurrency, int viewIdForResult,
                                                        boolean selectLeftNotRight) {
        openCurrencySelection(caller, targetCurrency, viewIdForResult, (selectLeftNotRight) ?
                CurrencySelectionMode.SELECT_EXCHANGE_RATE_LEFT :
                CurrencySelectionMode.SELECT_EXCHANGE_RATE_RIGHT);

    }

    public void openCurrencySelectionForCalculation(Activity caller, Currency targetCurrency, int viewIdForResult) {
        openCurrencySelection(caller, targetCurrency, viewIdForResult,
                CurrencySelectionMode.SELECT_FOR_EXCHANGE_CALCULATION);
    }

    public void openCurrencySelection(Activity caller, Currency targetCurrency, int viewIdForResult,
                                      CurrencySelectionMode mode) {
        Class<? extends Activity> activity = CurrencySelectionActivity.class;
        Map<String, Serializable> extras = new HashMap<>();
        extras.put(Rc.ACTIVITY_PARAM_CURRENCY_SELECTION_IN_CURRENCY, targetCurrency);
        extras.put(Rc.ACTIVITY_PARAM_CURRENCY_SELECTION_IN_VIEW_ID, viewIdForResult);
        extras.put(Rc.ACTIVITY_PARAM_CURRENCY_SELECTION_IN_MODE, mode);
        startActivityWithParamsForResult(extras, activity, ViewMode.NONE,
                Rc.ACTIVITY_REQ_CODE_CURRENCY_SELECTION, caller);
    }

    public void openEditPayment(Payment p) {
        Class<? extends Activity> activity = PaymentEditActivity.class;
        Map<String, Serializable> extras = new HashMap<>();
        extras.put(Rc.ACTIVITY_PARAM_KEY_PAYMENT_ID, p.getId());
        startActivityWithParams(extras, activity, ViewMode.EDIT);
    }

    private void startActivityWithParamsForResult(Map<String, Serializable> extras, Class<? extends Activity> activity,
                                                  ViewMode viewMode, int requestCode, Activity caller) {
        Intent intent = new Intent().setClass(context, activity);

        /* No flag like new task for 'forResult' */
        for (Entry<String, Serializable> extra : extras.entrySet()) {
            intent.putExtra(extra.getKey(), extra.getValue());
        }
        if (viewMode != null) {
            intent.putExtra(Rc.ACTIVITY_PARAM_KEY_VIEW_MODE, viewMode);
        }
        caller.startActivityForResult(intent, requestCode);
    }

    private void startActivityWithParams(Map<String, Serializable> extras, Class<? extends Activity> activity,
                                         ViewMode viewMode) {
        Intent intent = new Intent().setClass(context, activity);

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        for (Entry<String, Serializable> extra : extras.entrySet()) {
            intent.putExtra(extra.getKey(), extra.getValue());
        }
        if (viewMode != null) {
            intent.putExtra(Rc.ACTIVITY_PARAM_KEY_VIEW_MODE, viewMode);
        }
        context.startActivity(intent);
    }

    public void openHelp(FragmentManager fragmentManager) {
        HelpDialogFragment helpDialogFragment = new HelpDialogFragment();
        helpDialogFragment.show(fragmentManager, "help");
    }

    public void openDeleteConfirmationOnFragment(FragmentManager fragmentManager, Bundle bundle, Fragment targetFragment) {
        openDeleteConfirmation(fragmentManager, bundle, targetFragment);
    }

    public void openDeleteConfirmationOnActivity(FragmentManager fragmentManager, Bundle bundle) {
        openDeleteConfirmation(fragmentManager, bundle, null);
    }


    private void openDeleteConfirmation(FragmentManager fragmentManager, Bundle bundle, Fragment targetFragment) {
        DeleteDialogFragment dialogFragment = new DeleteDialogFragment();
        dialogFragment.setArguments(bundle);
        if (targetFragment != null) {
            dialogFragment.setTargetFragment(targetFragment, 1);
        }
        dialogFragment.show(fragmentManager, "delete");
    }

    public void openDatePickerOnActivity(FragmentManager fragmentManager) {
        DatePickerDialogFragment dialogFragment = new DatePickerDialogFragment();
        dialogFragment.show(fragmentManager, "datepicker");
    }
}
