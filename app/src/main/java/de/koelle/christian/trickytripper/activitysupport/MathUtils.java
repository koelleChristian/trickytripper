package de.koelle.christian.trickytripper.activitysupport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import de.koelle.christian.common.primitives.DivisionResult;
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
            Double cents = (divisionResult.getLoss() < 0) ? -0.01d : 0.01d;
            List<Integer> randomParticipantIndices = getRandomizedIndices(participantCount);
            int noOfIterations = (int) Math.abs(divisionResult.getLoss() / cents);
            Object[] entries = targetMap.values().toArray();
            for (int i = 0; i < noOfIterations; i++) {
                Integer index = randomParticipantIndices.get(i);
                ((Amount) entries[index]).addValue(cents);
            }
        }
    }

    private static List<Integer> getRandomizedIndices(int participantCount) {
        List<Integer> participantIndices = new ArrayList<Integer>();
        for (int i = 0; i < participantCount; i++) {
            participantIndices.add(i);
        }
        Collections.shuffle(participantIndices);
        return participantIndices;
    }

}
