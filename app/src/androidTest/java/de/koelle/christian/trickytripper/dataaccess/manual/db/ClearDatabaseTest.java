package de.koelle.christian.trickytripper.dataaccess.manual.db;

import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Test;

import de.koelle.christian.trickytripper.dataaccess.impl.DataManagerImpl;

public class ClearDatabaseTest {

    @Test
    public void testRemoveAll() {
        DataManagerImpl dataManager = new DataManagerImpl(InstrumentationRegistry.getInstrumentation().getTargetContext());
        dataManager.removeAll();
    }
}
