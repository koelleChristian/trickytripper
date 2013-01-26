package de.koelle.christian.trickytripper.controller;

import java.util.Currency;

import android.app.Activity;
import de.koelle.christian.trickytripper.constants.Rt;
import de.koelle.christian.trickytripper.model.Amount;
import de.koelle.christian.trickytripper.model.ExchangeRate;
import de.koelle.christian.trickytripper.model.Participant;
import de.koelle.christian.trickytripper.model.Payment;

public interface TripExpensesViewController {

    void openCreatePayment(Participant participant);

    void openManageTrips();

    void openImportExchangeRates(Activity caller, Currency... currencies);

    void openMoneyCalculatorView(Amount amount, int viewIdForResult, Activity caller);

    void openEditPayment(Payment payment);

    void openEditExchangeRate(ExchangeRate exchangeRate);

    void switchTabs(Rt tabId);

    void openTransferMoney(Participant participant);

    void openExport();

    void openSettings();

}
