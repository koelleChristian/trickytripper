package de.koelle.christian.trickytripper.dataaccess.suite;

import junit.framework.Test;
import junit.framework.TestSuite;
import android.test.suitebuilder.TestSuiteBuilder;

public class AllExchangeRatePersistenceTestSuite extends TestSuite {
    public static Test suite() {
        return new TestSuiteBuilder(AllExchangeRatePersistenceTestSuite.class).
                includePackages("de.koelle.christian.trickytripper.dataaccess.suite.exchange").build();
    }
}
