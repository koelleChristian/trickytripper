package de.koelle.christian.trickytripper.dataaccess.manual.db;

import android.support.test.InstrumentationRegistry;
import android.support.test.filters.SmallTest;

import org.junit.Test;

import de.koelle.christian.trickytripper.dataaccess.impl.DataManagerImpl;

@SmallTest
public class ClearDatabaseTest {

    @Test
    public void testRemoveAll() {
        DataManagerImpl dataManager = new DataManagerImpl(InstrumentationRegistry.getTargetContext());
        dataManager.removeAll();
    }
}
