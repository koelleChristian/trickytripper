package de.koelle.christian.trickytripper.test.dataaccess.util;

import android.test.ApplicationTestCase;
import de.koelle.christian.trickytripper.TrickyTripperApp;
import de.koelle.christian.trickytripper.dataaccess.impl.DataManagerImpl;

public class ClearDatabaseTest extends ApplicationTestCase<TrickyTripperApp> {

    public ClearDatabaseTest() {
        super(TrickyTripperApp.class);
    }

    public void testRemoveAll() {
        DataManagerImpl dataManager = new DataManagerImpl(getContext());
        dataManager.removeAll();
    }
}
