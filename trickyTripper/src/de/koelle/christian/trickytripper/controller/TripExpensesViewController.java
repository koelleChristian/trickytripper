package de.koelle.christian.trickytripper.controller;

import de.koelle.christian.trickytripper.constants.Rt;
import de.koelle.christian.trickytripper.model.Participant;
import de.koelle.christian.trickytripper.model.Payment;

public interface TripExpensesViewController {

    void openCreatePayment(Participant participant);

    void openManageTrips();

    void openEditPayment(Payment payment);

    void switchTabs(Rt tabId);

    void openTransferMoney(Participant participant);

    void openExport();

    void openSettings();

}
