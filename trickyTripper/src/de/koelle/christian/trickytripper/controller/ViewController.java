package de.koelle.christian.trickytripper.controller;

import java.util.Currency;

import android.app.Activity;
import de.koelle.christian.trickytripper.model.Amount;
import de.koelle.christian.trickytripper.model.ExchangeRate;
import de.koelle.christian.trickytripper.model.Participant;
import de.koelle.christian.trickytripper.model.Payment;

public interface ViewController {

    void openCreatePayment(Participant participant);

    void openManageTrips();

    void openImportExchangeRates(Activity caller, Currency... currencies);

    void openDeleteExchangeRates(Activity caller, Currency... currencies);

    void openEditExchangeRate(Activity caller, ExchangeRate exchangeRate);

    void openCreateExchangeRate(Activity caller);
    
    void openCreateExchangeRate(Activity caller,  Currency fromCurrency);

    void openMoneyCalculatorView(Amount amount, int viewIdForResult, Activity caller);

    void openCurrencySelectionForNewExchangeRate(Activity caller, Currency targetCurrency, int viewIdForResult,
            boolean selectLeftNotRight);

    void openCurrencySelectionForCalculation(Activity caller, Currency targetCurrency, int viewIdForResult);

    void openEditPayment(Payment payment);

    void openTransferMoney(Participant participant);

    void openExport();

    void openSettings();

}
