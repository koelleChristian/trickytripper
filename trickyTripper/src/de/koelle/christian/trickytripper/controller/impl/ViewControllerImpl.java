package de.koelle.christian.trickytripper.controller.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Currency;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import de.koelle.christian.trickytripper.activities.CurrencyCalculatorActivity;
import de.koelle.christian.trickytripper.activities.CurrencySelectionActivity;
import de.koelle.christian.trickytripper.activities.CurrencySelectionActivity.CurrencySelectionMode;
import de.koelle.christian.trickytripper.activities.DeleteExchangeRatesActivity;
import de.koelle.christian.trickytripper.activities.EditExchangeRateActivity;
import de.koelle.christian.trickytripper.activities.ExportActivity;
import de.koelle.christian.trickytripper.activities.ImportExchangeRatesActivity;
import de.koelle.christian.trickytripper.activities.ManageTripsActivity;
import de.koelle.christian.trickytripper.activities.MoneyTransferActivity;
import de.koelle.christian.trickytripper.activities.PaymentEditActivity;
import de.koelle.christian.trickytripper.activities.PreferencesActivity;
import de.koelle.christian.trickytripper.constants.Rc;
import de.koelle.christian.trickytripper.constants.ViewMode;
import de.koelle.christian.trickytripper.controller.ViewController;
import de.koelle.christian.trickytripper.model.Amount;
import de.koelle.christian.trickytripper.model.ExchangeRate;
import de.koelle.christian.trickytripper.model.Participant;
import de.koelle.christian.trickytripper.model.Payment;

public class ViewControllerImpl implements ViewController {

    private final Context context;

    public ViewControllerImpl(Context context) {
        this.context = context;
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
        Class<? extends Activity> activity = ImportExchangeRatesActivity.class;
        HashMap<String, Serializable> extras = new HashMap<String, Serializable>();
        if (currencies != null && currencies.length > 0) {
            ArrayList<Currency> currencyList = new ArrayList<Currency>();
            for (Currency cur : currencies) {
                currencyList.add(cur);
            }
            extras.put(Rc.ACTIVITY_PARAM_IMPORT_EXCHANGE_RATES_IN_CURRENCY_LIST, currencyList);
        }
        startActivityWithParamsForResult(extras, activity, ViewMode.NONE,
                Rc.ACTIVITY_PARAM_EXCHANGE_RATE_MANAGEMENT_CODE, caller);
    }

    public void openDeleteExchangeRates(Activity caller, Currency... currencies) {
        Class<? extends Activity> activity = DeleteExchangeRatesActivity.class;
        HashMap<String, Serializable> extras = new HashMap<String, Serializable>();
        if (currencies != null && currencies.length > 0) {
            ArrayList<Currency> currencyList = new ArrayList<Currency>();
            for (Currency cur : currencies) {
                currencyList.add(cur);
            }
            extras.put(Rc.ACTIVITY_PARAM_DELETE_EXCHANGE_RATES_IN_CURRENCY_LIST, currencyList);
        }
        startActivityWithParamsForResult(extras, activity, ViewMode.NONE,
                Rc.ACTIVITY_PARAM_EXCHANGE_RATE_MANAGEMENT_CODE, caller);

    }

    public void openEditExchangeRate(Activity caller, ExchangeRate exchangeRate) {
        Class<? extends Activity> activity = EditExchangeRateActivity.class;
        ViewMode viewMode = ViewMode.CREATE;
        Map<String, Serializable> extras = new HashMap<String, Serializable>();
        if (exchangeRate != null) {
            extras.put(Rc.ACTIVITY_PARAM_EDIT_EXCHANGE_RATE_IN_RATE_TECH_ID, exchangeRate.getId());
            viewMode = ViewMode.EDIT;
        }
        startActivityWithParams(extras, activity, viewMode);
    }

    public void openCreateExchangeRate(Activity caller) {
        openEditExchangeRate(caller, null);
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

    public void openMoneyCalculatorView(Amount amount, int resultViewId, Activity caller) {
        Class<? extends Activity> activity = CurrencyCalculatorActivity.class;
        Map<String, Serializable> extras = new HashMap<String, Serializable>();
        extras.put(Rc.ACTIVITY_PARAM_CURRENCY_CALCULATOR_IN_VALUE, amount.getValue());
        extras.put(Rc.ACTIVITY_PARAM_CURRENCY_CALCULATOR_IN_RESULT_VIEW_ID, resultViewId);
        startActivityWithParamsForResult(extras, activity, ViewMode.NONE,
                Rc.ACTIVITY_PARAM_CURRENCY_CALCULATOR_REQUEST_CODE, caller);
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
        Map<String, Serializable> extras = new HashMap<String, Serializable>();
        extras.put(Rc.ACTIVITY_PARAM_CURRENCY_SELECTION_IN_CURRENCY, targetCurrency);
        extras.put(Rc.ACTIVITY_PARAM_CURRENCY_SELECTION_IN_VIEW_ID, viewIdForResult);
        extras.put(Rc.ACTIVITY_PARAM_CURRENCY_SELECTION_IN_MODE, mode);
        startActivityWithParamsForResult(extras, activity, ViewMode.NONE,
                Rc.ACTIVITY_PARAM_CURRENCY_SELECTION_REQUEST_CODE, caller);

    }

    public void openEditPayment(Payment p) {
        Class<? extends Activity> activity = PaymentEditActivity.class;
        Map<String, Serializable> extras = new HashMap<String, Serializable>();
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

}
