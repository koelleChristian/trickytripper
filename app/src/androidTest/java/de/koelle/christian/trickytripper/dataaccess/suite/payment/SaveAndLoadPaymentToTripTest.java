package de.koelle.christian.trickytripper.dataaccess.suite.payment;

import java.util.Currency;

import junit.framework.Assert;
import android.test.ApplicationTestCase;
import de.koelle.christian.trickytripper.TrickyTripperApp;
import de.koelle.christian.trickytripper.dataaccess.impl.DataConstants;
import de.koelle.christian.trickytripper.dataaccess.impl.DataManagerImpl;
import de.koelle.christian.trickytripper.dataaccess.suite.util.AssertionUtil;
import de.koelle.christian.trickytripper.dataaccess.suite.util.ModelSetupUtil;
import de.koelle.christian.trickytripper.factories.ModelFactory;
import de.koelle.christian.trickytripper.model.Participant;
import de.koelle.christian.trickytripper.model.Payment;
import de.koelle.christian.trickytripper.model.PaymentCategory;
import de.koelle.christian.trickytripper.model.Trip;


public class SaveAndLoadPaymentToTripTest extends ApplicationTestCase<TrickyTripperApp> {

    public SaveAndLoadPaymentToTripTest() {
        super(TrickyTripperApp.class);
    }

    @Override
    protected void setUp() {
        getContext().deleteDatabase(DataConstants.DATABASE_NAME);
    }

    public void testSaveAndLoadPaymentToTrip() {

        DataManagerImpl dataManager = new DataManagerImpl(getContext());

        dataManager.removeAll();

        Payment payment1Out;
        Payment payment2Out;

        long id1Exp = 1L;
        long id2Exp = 2L;

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

        payment1Out = dataManager.persistPaymentInTrip(tripId, payment01In);
        AssertionUtil.assertPaymentEquality(payment1Out, id1Exp, payment01In);

        /* Payment 02 */
        Payment payment02In = ModelFactory.createNewPayment("MyDescription02", PaymentCategory.GAS);
        ModelSetupUtil.addAmountToPayment(payment02In, 10d, "USD", true, p1);
        ModelSetupUtil.addAmountToPayment(payment02In, 10d, "USD", false, p2);

        payment2Out = dataManager.persistPaymentInTrip(tripId, payment02In);
        AssertionUtil.assertPaymentEquality(payment2Out, id2Exp, payment02In);

        /* Payment 02 - Update */
        payment02In.setCategory(PaymentCategory.HOUSING);
        payment02In.setDescription("Update");
        payment02In.getParticipantToPayment().clear();
        payment02In.getParticipantToSpending().clear();
        ModelSetupUtil.addAmountToPayment(payment02In, 30d, "USD", true, p1);
        ModelSetupUtil.addAmountToPayment(payment02In, 10d, "USD", false, p2);
        ModelSetupUtil.addAmountToPayment(payment02In, 10d, "USD", false, p2);
        ModelSetupUtil.addAmountToPayment(payment02In, 10d, "USD", false, p3);

        payment2Out = dataManager.persistPaymentInTrip(tripId, payment02In);
        AssertionUtil.assertPaymentEquality(payment2Out, id2Exp, payment02In);

        /* Test data load */
        Trip loadedTripFull = dataManager.loadTripById(tripId);
        Assert.assertEquals(2, loadedTripFull.getPayments().size());
        for (Payment p : loadedTripFull.getPayments()) {
            if (p.getId() == id1Exp) {
                AssertionUtil.assertPaymentEquality(p, id1Exp, payment01In);
            }
            else if (p.getId() == id2Exp) {
                AssertionUtil.assertPaymentEquality(p, id2Exp, payment02In);
            }
            else {
                Assert.fail();
            }
        }

        dataManager.removeAll();
    }

}
