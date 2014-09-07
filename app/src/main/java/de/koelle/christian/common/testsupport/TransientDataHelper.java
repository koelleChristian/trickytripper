package de.koelle.christian.common.testsupport;

import java.util.List;

import de.koelle.christian.trickytripper.factories.AmountFactory;
import de.koelle.christian.trickytripper.model.Participant;
import de.koelle.christian.trickytripper.model.Payment;
import de.koelle.christian.trickytripper.model.Trip;
import de.koelle.christian.trickytripper.strategies.SumReport;
import de.koelle.christian.trickytripper.strategies.TripReportLogic;

public class TransientDataHelper {

    public static void updateAllTransientData(Trip tripToBeEdited2, TripReportLogic tripReportLogic,
            AmountFactory amountFactory2) {
        amountFactory2.setCurrency(tripToBeEdited2.getBaseCurrency());
        tripReportLogic.setAmountFactory(amountFactory2);

        List<Participant> participants = tripToBeEdited2.getParticipant();
        List<Payment> payments = tripToBeEdited2.getPayments();

        SumReport sumReport = tripReportLogic.createSumReport(participants, payments);
        tripToBeEdited2.setSumReport(sumReport);
        tripToBeEdited2.setDebts(tripReportLogic.createDebts2(participants, sumReport.getBalanceByUser()));

    }
}
