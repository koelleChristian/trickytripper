package de.koelle.christian.trickytripper.dataaccess.suite.payment;

import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

import junit.framework.Assert;
import android.test.ApplicationTestCase;
import de.koelle.christian.trickytripper.TrickyTripperApp;
import de.koelle.christian.trickytripper.dataaccess.impl.DataManagerImpl;
import de.koelle.christian.trickytripper.dataaccess.suite.util.AssertionUtil;
import de.koelle.christian.trickytripper.factories.ModelFactory;
import de.koelle.christian.trickytripper.model.Trip;
import de.koelle.christian.trickytripper.model.TripSummary;


public class SaveAndLoadTripTest extends ApplicationTestCase<TrickyTripperApp> {

    public SaveAndLoadTripTest() {
        super(TrickyTripperApp.class);
    }

    public void testSaveAndLoadTrip() {
        DataManagerImpl dataManager = new DataManagerImpl(getContext());

        dataManager.removeAll();

        Trip trip1Out;
        Trip trip2Out;

        String TRIP_1_NAME_ZZZ = "ZZZ";
        String TRIP_2_NAME_CHRISTIAN = "Christian";

        /* Save and load trip 1 */

        long id1Exp = 1L;
        long id2Exp = 2L;

        Trip trip1In = ModelFactory.createNewTrip(TRIP_1_NAME_ZZZ, Currency.getInstance("EUR"));
        trip1Out = dataManager.persistTrip(trip1In);
        AssertionUtil.assertTripEquality(trip1Out, id1Exp, trip1In);
        trip1Out = dataManager.loadTripById(id1Exp);
        AssertionUtil.assertTripEquality(trip1Out, id1Exp, trip1In);

        /* Save and load trip 2 */

        Trip trip2In = ModelFactory.createNewTrip(TRIP_2_NAME_CHRISTIAN, Currency.getInstance("USD"));
        trip2Out = dataManager.persistTrip(trip2In);
        AssertionUtil.assertTripEquality(trip2Out, id2Exp, trip2In);
        trip2Out = dataManager.loadTripById(id2Exp);
        AssertionUtil.assertTripEquality(trip2Out, id2Exp, trip2In);

        /* ===========Load all trips ======== */

        List<TripSummary> tripSummaryResult = dataManager.getAllTripSummaries();

        List<Trip> tripsExpectedInOrder = new ArrayList<Trip>();
        tripsExpectedInOrder.add(trip2In); // Order by name
        tripsExpectedInOrder.add(trip1In);

        trip1In.setId(id1Exp);
        trip2In.setId(id2Exp);

        AssertionUtil.assertTripEquality(tripSummaryResult, tripsExpectedInOrder);

        // /* Existence Check */
        Assert.assertEquals(true, dataManager.doesTripAlreadyExist(TRIP_1_NAME_ZZZ, 0L));
        Assert.assertEquals(true, dataManager.doesTripAlreadyExist(TRIP_2_NAME_CHRISTIAN, 0L));
        Assert.assertEquals(false, dataManager.doesTripAlreadyExist(TRIP_1_NAME_ZZZ, id1Exp));
        Assert.assertEquals(false, dataManager.doesTripAlreadyExist(TRIP_2_NAME_CHRISTIAN, id2Exp));
        Assert.assertEquals(false, dataManager.doesTripAlreadyExist("Something not yet persisted", 0L));
        Assert.assertEquals(false, dataManager.doesTripAlreadyExist(null, 0L));

        /* Update trip 2 */

        trip2In.setName(TRIP_2_NAME_CHRISTIAN + " Update");
        trip2Out = dataManager.persistTrip(trip2In);
        AssertionUtil.assertTripEquality(trip2Out, id2Exp, trip2In);
        trip2Out = dataManager.loadTripById(id2Exp);
        AssertionUtil.assertTripEquality(trip2Out, id2Exp, trip2In);

        dataManager.removeAll();
    }
}