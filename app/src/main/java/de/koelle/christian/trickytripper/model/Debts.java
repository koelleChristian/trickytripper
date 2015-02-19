package de.koelle.christian.trickytripper.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class Debts {

	private final Map<Participant, Amount> loanerToDebts = new HashMap<>();

	public Map<Participant, Amount> getLoanerToDebts() {
		return loanerToDebts;
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("[");
		for (Entry<Participant, Amount> loaner : loanerToDebts.entrySet()) {
			result.append("\n");
			result.append(loaner.getKey());
			result.append(" <=== ");
			result.append(loaner.getValue());
			result.append("\n");
		}
		if (loanerToDebts.entrySet().isEmpty()) {
			result.append("blank");
		}
		result.append("]");
		return result.toString();
	}

}
