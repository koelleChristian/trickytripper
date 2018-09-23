package de.koelle.christian.trickytripper.dataaccess.suite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import de.koelle.christian.trickytripper.dataaccess.suite.payment.CheckTripExistenceTest;

@RunWith(Suite.class)
@Suite.SuiteClasses(CheckTripExistenceTest.class)
public class AllPaymentPersistenceTestSuite {
}
