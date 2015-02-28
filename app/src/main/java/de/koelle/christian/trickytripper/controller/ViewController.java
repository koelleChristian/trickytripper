package de.koelle.christian.trickytripper.controller;

import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import de.koelle.christian.trickytripper.model.Amount;
import de.koelle.christian.trickytripper.model.ExchangeRate;
import de.koelle.christian.trickytripper.model.Participant;
import de.koelle.christian.trickytripper.model.Payment;
import de.koelle.christian.trickytripper.model.TripSummary;

public interface ViewController {

    void openCreatePayment(Participant participant);

    void openImportExchangeRates(Activity caller, Currency... currencies);

    void openDeleteExchangeRates(Activity caller, Currency... currencies);

    void openEditExchangeRate(Activity caller, ExchangeRate exchangeRate);

    void openCreateExchangeRate(Activity caller);

    void openCreateExchangeRate(Activity caller, Currency fromCurrency);

    void openMoneyCalculatorView(Amount amount, int viewIdForResult, Activity caller);

    void openCurrencySelectionForNewExchangeRate(Activity caller, Currency targetCurrency, int viewIdForResult,
            boolean selectLeftNotRight);

    void openCurrencySelectionForCalculation(Activity caller, Currency targetCurrency, int viewIdForResult);

    void openParticipantSelection(Activity caller, ArrayList<Participant> participantsInUse,
                                  Amount currentTotalAmount, boolean isPayerSelection,
                                  ArrayList<Participant> allRelevantParticipants);

    void openEditPayment(Payment payment);

    void openEditParticipant(Participant participant);
    
    void openEditTrip(Activity caller, TripSummary tripSummary);

    void openCreateParticipant();

    void openTransferMoney(Participant participant);

    void openExport();

    void openSettings();

    void openHelp(FragmentManager fragmentManager);

    void openDatePickerOnActivity(FragmentManager fragmentManager);

    void openDeleteConfirmationOnFragment(FragmentManager fragmentManager,  Bundle bundle, Fragment targetFragment );
    
    void openDeleteConfirmationOnActivity(FragmentManager fragmentManager,  Bundle bundle);

}
