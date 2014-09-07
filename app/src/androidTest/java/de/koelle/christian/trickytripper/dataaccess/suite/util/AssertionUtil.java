package de.koelle.christian.trickytripper.dataaccess.suite.util;

import java.util.List;

import junit.framework.Assert;
import de.koelle.christian.trickytripper.model.Participant;
import de.koelle.christian.trickytripper.model.Payment;
import de.koelle.christian.trickytripper.model.Trip;
import de.koelle.christian.trickytripper.model.TripSummary;

public class AssertionUtil {
    public static void assertTripEquality(Trip delivery, long expectedId, Trip expectedResult) {
        Assert.assertEquals(expectedId, delivery.getId());
        Assert.assertEquals(expectedResult.getName(), delivery.getName());
        Assert.assertEquals(expectedResult.getBaseCurrency().getCurrencyCode(), delivery.getBaseCurrency()
                .getCurrencyCode());
    }

    public static void assertParticipantEquality(Participant delivery, long expectedId, Participant expectedResult) {
        Assert.assertEquals(expectedId, delivery.getId());
        Assert.assertEquals(expectedResult.getName(), delivery.getName());
        Assert.assertEquals(expectedResult.isActive(), delivery.isActive());
    }

    public static void assertPaymentEquality(Payment delivery, long expectedId, Payment expectedResult) {
        Assert.assertEquals(expectedId, delivery.getId());
        Assert.assertEquals(expectedResult.getDescription(), delivery.getDescription());
        Assert.assertEquals(expectedResult.getCategory(), delivery.getCategory());
        Assert.assertTrue("The persisted Payment does not come along with a payment date, i.e. date of creation.",
                expectedResult.getPaymentDateTime() != null);
    }

    public static void assertTripEquality(Trip delivery, Trip expectedResult) {
        assertTripEquality(delivery, expectedResult.getId(), expectedResult);
    }

    public static void assertTripEquality(TripSummary delivery, Trip expectedResult) {
        Assert.assertEquals(expectedResult.getId(), delivery.getId());
        Assert.assertEquals(expectedResult.getName(), delivery.getName());
    }

    public static void assertTripEquality(List<TripSummary> delivery, List<Trip> expectedResult) {
        Assert.assertEquals(expectedResult.size(), delivery.size());
        for (int i = 0; i < expectedResult.size(); i++) {
            assertTripEquality(delivery.get(i), expectedResult.get(i));
        }
    }

}
