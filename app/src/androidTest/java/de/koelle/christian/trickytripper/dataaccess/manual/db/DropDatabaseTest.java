package de.koelle.christian.trickytripper.dataaccess.manual.db;

import android.support.test.InstrumentationRegistry;
import android.support.test.filters.SmallTest;

import org.junit.Test;

import de.koelle.christian.trickytripper.dataaccess.impl.DataConstants;

@SmallTest
public class DropDatabaseTest {

    @Test
    public void testRemoveAll() {
        InstrumentationRegistry.getTargetContext().deleteDatabase(DataConstants.DATABASE_NAME);
    }
}
