package de.koelle.christian.trickytripper.strategies;

import java.util.List;

import de.koelle.christian.trickytripper.model.Participant;
import de.koelle.christian.trickytripper.model.Payment;

public interface SumAmountStrategy {

//	void refreshSum(List<Participant> participants, List<Payment> payments,
//			Map<Participant, Amount> result);
//
//	void addAmount(List<Participant> participants, List<Payment> payments,
//			Map<Participant, Amount> result);

	 void fillSumReport(SumReport sumReport, List<Participant> participants,
			List<Payment> payments);

}
