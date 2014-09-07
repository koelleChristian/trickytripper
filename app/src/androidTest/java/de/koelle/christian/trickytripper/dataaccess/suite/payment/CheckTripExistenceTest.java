package de.koelle.christian.trickytripper.dataaccess.suite.payment;

import java.util.Currency;

import junit.framework.Assert;
import android.test.ApplicationTestCase;
import de.koelle.christian.trickytripper.TrickyTripperApp;
import de.koelle.christian.trickytripper.dataaccess.impl.DataConstants;
import de.koelle.christian.trickytripper.dataaccess.impl.DataManagerImpl;
import de.koelle.christian.trickytripper.factories.ModelFactory;
import de.koelle.christian.trickytripper.model.Trip;

public class CheckTripExistenceTest extends ApplicationTestCase<TrickyTripperApp> {

    public CheckTripExistenceTest() {
        super(TrickyTripperApp.class);
    }

    @Override
    protected void setUp() {
        getContext().deleteDatabase(DataConstants.DATABASE_NAME);
    }

    public void testTripExistenceCheck() {
        DataManagerImpl dataManager = new DataManagerImpl(getContext());

        dataManager.removeAll();

        String TRIP_1_NAME_ZZZ = "ZZZ";
        String TRIP_2_NAME_CHRISTIAN = "Christian";

        Assert.assertEquals(true, dataManager.oneOrLessTripsLeft());

        /* Save and load trip 1 */

        Trip trip1In = ModelFactory.createNewTrip(TRIP_1_NAME_ZZZ, Currency.getInstance("EUR"));
        dataManager.persistTrip(trip1In);

        Assert.assertEquals(true, dataManager.oneOrLessTripsLeft());

        /* Save and load trip 2 */

        Trip trip2In = ModelFactory.createNewTrip(TRIP_2_NAME_CHRISTIAN, Currency.getInstance("USD"));
        dataManager.persistTrip(trip2In);

        Assert.assertEquals(false, dataManager.oneOrLessTripsLeft());

        dataManager.removeAll();
    }
}
