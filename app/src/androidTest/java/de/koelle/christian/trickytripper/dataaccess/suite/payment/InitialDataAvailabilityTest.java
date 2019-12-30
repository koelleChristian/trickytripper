package de.koelle.christian.trickytripper.dataaccess.suite.payment;

import android.content.Context;

import org.junit.Test;

import de.koelle.christian.trickytripper.dataaccess.impl.DataConstants;
import de.koelle.christian.trickytripper.dataaccess.impl.DataManagerImpl;

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;



public class InitialDataAvailabilityTest {

    @Test
    public void testTestDataAvailability() {

        Context targetContext = getInstrumentation().getTargetContext();
        targetContext.deleteDatabase(DataConstants.DATABASE_NAME);
        DataManagerImpl dataManager = new DataManagerImpl(targetContext);

//        Assert.assertEquals(true, dataManager.doesTripAlreadyExist(getContext().getResources().getString(
//                R.string.initial_data_trip_name), 0L));
        dataManager.removeAll();
    }
}