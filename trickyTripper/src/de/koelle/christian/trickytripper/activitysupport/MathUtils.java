package de.koelle.christian.trickytripper.activitysupport;

import java.util.List;
import java.util.Map;
import java.util.Random;

import de.koelle.christian.common.utils.NumberUtils;
import de.koelle.christian.trickytripper.factories.AmountFactory;
import de.koelle.christian.trickytripper.model.Amount;
import de.koelle.christian.trickytripper.model.Participant;

public class MathUtils {

    public static void divideAndSetOnMap(Amount amountTotal, List<Participant> participants,
            Map<Participant, Amount> targetMap, boolean resultToBeNegative, AmountFactory amountFactory) {
        Double amountValue = Math.abs(amountTotal.getValue());
        int participantCount = participants.size();

        DivisionResult divisionResult = NumberUtils.divideWithLoss(amountValue, participantCount, resultToBeNegative);
        targetMap.clear();
        for (Participant p : participants) {
            targetMap.put(p, amountFactory.createAmount(divisionResult.getResult()));
        }
        if (divisionResult.getLoss() != 0) {
            getRandomAmountFromMap(participantCount, targetMap).addValue(divisionResult.getLoss());
        }

    }

    private static Amount getRandomAmountFromMap(int participantCount, Map<Participant, Amount> map) {
        Amount randomAmount;
        Object[] entries = map.values().toArray();
        randomAmount = ((Amount) entries[new Random().nextInt(participantCount - 1)]);
        return randomAmount;
    }
}
