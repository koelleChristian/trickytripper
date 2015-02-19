package de.koelle.christian.trickytripper.dataaccess.suite.payment;

import android.test.ApplicationTestCase;

import de.koelle.christian.trickytripper.TrickyTripperApp;
import de.koelle.christian.trickytripper.dataaccess.impl.DataConstants;
import de.koelle.christian.trickytripper.dataaccess.impl.DataManagerImpl;

public class InitialDataAvailabilityTest extends ApplicationTestCase<TrickyTripperApp> {

    public InitialDataAvailabilityTest() {
        super(TrickyTripperApp.class);
    }

    public void testTestDataAvailability() {
        getContext().deleteDatabase(DataConstants.DATABASE_NAME);

        DataManagerImpl dataManager = new DataManagerImpl(getContext());

//        Assert.assertEquals(true, dataManager.doesTripAlreadyExist(getContext().getResources().getString(
//                R.string.initial_data_trip_name), 0L));
        dataManager.removeAll();
    }
}