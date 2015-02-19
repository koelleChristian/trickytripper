package de.koelle.christian.trickytripper.dataaccess.suite.payment;

import java.util.Currency;

import junit.framework.Assert;
import android.test.ApplicationTestCase;
import de.koelle.christian.trickytripper.TrickyTripperApp;
import de.koelle.christian.trickytripper.dataaccess.impl.DataConstants;
import de.koelle.christian.trickytripper.dataaccess.impl.DataManagerImpl;
import de.koelle.christian.trickytripper.dataaccess.suite.util.ModelSetupUtil;
import de.koelle.christian.trickytripper.factories.ModelFactory;
import de.koelle.christian.trickytripper.model.Participant;
import de.koelle.christian.trickytripper.model.Payment;
import de.koelle.christian.trickytripper.model.PaymentCategory;
import de.koelle.christian.trickytripper.model.TripSummary;


public class DeleteTripTest extends ApplicationTestCase<TrickyTripperApp> {

    public DeleteTripTest() {
        super(TrickyTripperApp.class);
    }

    @Override
    protected void setUp() {
        getContext().deleteDatabase(DataConstants.DATABASE_NAME);
    }

    public void testDeleteTrip() {
        DataManagerImpl dataManager = new DataManagerImpl(getContext());

        dataManager.removeAll();

        long tripId = dataManager.persistTrip(ModelFactory.createNewTrip("MyTrip", Currency.getInstance("USD")))
                .getId();
        Participant p1 = dataManager.persistParticipantInTrip(tripId, ModelFactory.createNewParticipant("Tony", true));
        Participant p2 = dataManager
                .persistParticipantInTrip(tripId, ModelFactory.createNewParticipant("Steve", false));
        Participant p3 = dataManager
                .persistParticipantInTrip(tripId, ModelFactory.createNewParticipant("Bruce", false));

        /* Payment 01 */
        Payment payment01In = ModelFactory.createNewPayment("MyDescription01", PaymentCategory.BEVERAGES);
        ModelSetupUtil.addAmountToPayment(payment01In, 33.20d, "EUR", true, p1);
        ModelSetupUtil.addAmountToPayment(payment01In, 10.10d, "EUR", false, p1);
        ModelSetupUtil.addAmountToPayment(payment01In, 11.10d, "EUR", false, p2);
        ModelSetupUtil.addAmountToPayment(payment01In, 12d, "EUR", false, p3);

        dataManager.persistPaymentInTrip(tripId, payment01In);

        /* Payment 02 */
        Payment payment02In = ModelFactory.createNewPayment("MyDescription02", PaymentCategory.GAS);
        ModelSetupUtil.addAmountToPayment(payment02In, 10d, "USD", true, p1);
        ModelSetupUtil.addAmountToPayment(payment02In, 10d, "USD", false, p2);

        dataManager.persistPaymentInTrip(tripId, payment02In);

        TripSummary tripSummary = new TripSummary();
        tripSummary.setId(tripId);
        dataManager.deleteTrip(tripSummary);

        Assert.assertEquals(null, dataManager.loadTripById(tripId));

    }

}
