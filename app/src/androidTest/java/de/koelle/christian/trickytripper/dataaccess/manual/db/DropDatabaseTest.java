package de.koelle.christian.trickytripper.dataaccess.manual.db;

import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Test;

import de.koelle.christian.trickytripper.dataaccess.impl.DataConstants;

public class DropDatabaseTest {

    @Test
    public void testRemoveAll() {
        InstrumentationRegistry.getInstrumentation().getTargetContext().deleteDatabase(DataConstants.DATABASE_NAME);
    }
}
