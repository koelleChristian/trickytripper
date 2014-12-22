package de.koelle.christian.trickytripper.dataaccess.suite;

import junit.framework.Test;
import junit.framework.TestSuite;
import android.test.suitebuilder.TestSuiteBuilder;

public class AllPersistenceTestSuite extends TestSuite {
public static Test suite() {
        return new TestSuiteBuilder(AllPersistenceTestSuite.class).
        includeAllPackagesUnderHere().build();
        }
        }
