package de.koelle.christian.trickytripper.dataaccess.suite.payment;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Currency;

import de.koelle.christian.trickytripper.dataaccess.impl.DataConstants;
import de.koelle.christian.trickytripper.dataaccess.impl.DataManagerImpl;
import de.koelle.christian.trickytripper.factories.ModelFactory;
import de.koelle.christian.trickytripper.model.Trip;

public class CheckTripExistenceTest {

    private Context context;

    @Before
    public void setUp() {
        context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        context.deleteDatabase(DataConstants.DATABASE_NAME);
    }

    @Test
    public void testTripExistenceCheck() {
        DataManagerImpl dataManager = new DataManagerImpl(context);

        dataManager.removeAll();

        String TRIP_1_NAME_ZZZ = "ZZZ";
        String TRIP_2_NAME_CHRISTIAN = "Christian";

        Assert.assertTrue(dataManager.oneOrLessTripsLeft());

        /* Save and load trip 1 */

        Trip trip1In = ModelFactory.createNewTrip(TRIP_1_NAME_ZZZ, Currency.getInstance("EUR"));
        dataManager.persistTrip(trip1In);

        Assert.assertTrue(dataManager.oneOrLessTripsLeft());

        /* Save and load trip 2 */

        Trip trip2In = ModelFactory.createNewTrip(TRIP_2_NAME_CHRISTIAN, Currency.getInstance("USD"));
        dataManager.persistTrip(trip2In);

        Assert.assertFalse(dataManager.oneOrLessTripsLeft());

        dataManager.removeAll();
    }
}
