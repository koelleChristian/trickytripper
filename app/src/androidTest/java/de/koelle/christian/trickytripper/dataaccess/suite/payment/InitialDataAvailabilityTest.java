package de.koelle.christian.trickytripper.dataaccess.suite.payment;


import android.support.test.filters.SmallTest;

import org.junit.Test;

import de.koelle.christian.trickytripper.dataaccess.impl.DataConstants;
import de.koelle.christian.trickytripper.dataaccess.impl.DataManagerImpl;

import static android.support.test.InstrumentationRegistry.getTargetContext;

@SmallTest
public class InitialDataAvailabilityTest {

    @Test
    public void testTestDataAvailability() {

        getTargetContext().deleteDatabase(DataConstants.DATABASE_NAME);

        DataManagerImpl dataManager = new DataManagerImpl(getTargetContext());

//        Assert.assertEquals(true, dataManager.doesTripAlreadyExist(getContext().getResources().getString(
//                R.string.initial_data_trip_name), 0L));
        dataManager.removeAll();
    }
}