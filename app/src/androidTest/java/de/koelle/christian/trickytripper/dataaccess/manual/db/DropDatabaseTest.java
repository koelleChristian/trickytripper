package de.koelle.christian.trickytripper.dataaccess.manual.db;

import android.test.ApplicationTestCase;
import de.koelle.christian.trickytripper.TrickyTripperApp;
import de.koelle.christian.trickytripper.dataaccess.impl.DataConstants;

public class DropDatabaseTest extends ApplicationTestCase<TrickyTripperApp> {

    public DropDatabaseTest() {
        super(TrickyTripperApp.class);
    }

    public void testRemoveAll() {
        getContext().deleteDatabase(DataConstants.DATABASE_NAME);
    }
}
