package de.koelle.christian.common.utils;

import java.util.ArrayList;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import junit.framework.Assert;

import org.junit.Test;

import de.koelle.christian.trickytripper.activitysupport.MathUtils;
import de.koelle.christian.trickytripper.factories.AmountFactory;
import de.koelle.christian.trickytripper.factories.ModelFactory;
import de.koelle.christian.trickytripper.model.Amount;
import de.koelle.christian.trickytripper.model.Participant;

public class MathUtilsTest {

    @Test
    public void testDivision() {
        AmountFactory amountFactory = new AmountFactory();
        amountFactory.setCurrency(Currency.getInstance("EUR"));

        Amount amountTotal;
        Map<Participant, Amount> targetMap;

        targetMap = new HashMap<Participant, Amount>();

        List<Participant> participants = new ArrayList<Participant>();
        Participant p;

        p = ModelFactory.createNewParticipant("Christian", true);
        p.setId(1);
        participants.add(p);

        p = ModelFactory.createNewParticipant("Wolle", true);
        p.setId(2);
        participants.add(p);

        p = ModelFactory.createNewParticipant("Niko", true);
        p.setId(3);
        participants.add(p);

        /* Round down */

        amountTotal = amountFactory.createAmount(100d);

        MathUtils.divideAndSetOnMap(amountTotal, participants, targetMap, true, amountFactory);
        Double totalSumOnResult = 0d;
        for (Entry<Participant, Amount> entry : targetMap.entrySet()) {
            Assert.assertTrue("The result for " + entry.getKey().getName() + " is neither -33.33 nor -33.34.", entry
                    .getValue().getValue() == -33.33d || entry.getValue().getValue() == -33.34d);
            totalSumOnResult = totalSumOnResult + entry.getValue().getValue();
        }
        Assert.assertEquals(amountTotal.getValue(), Math.abs(totalSumOnResult));

        /* Round up */

        amountTotal = amountFactory.createAmount(50d);

        MathUtils.divideAndSetOnMap(amountTotal, participants, targetMap, true, amountFactory);
        totalSumOnResult = 0d;
        for (Entry<Participant, Amount> entry : targetMap.entrySet()) {
            Assert.assertTrue("The result for " + entry.getKey().getName() + " is neither -16,66 nor -16.67 but: "
                    + entry.getValue().getValue(), entry
                    .getValue().getValue() == -16.66d || entry.getValue().getValue() == -16.67d);
            totalSumOnResult = totalSumOnResult + entry.getValue().getValue();
        }
        Assert.assertEquals(amountTotal.getValue(), Math.abs(totalSumOnResult));
    }

}
