package de.koelle.christian.trickytripper.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class Debts {

	private Map<Participant, Amount> loanerToDepts = new HashMap<Participant, Amount>();

	public Map<Participant, Amount> getLoanerToDepts() {
		return loanerToDepts;
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("[");
		for (Entry<Participant, Amount> loaner : loanerToDepts.entrySet()) {
			result.append("\n");
			result.append(loaner.getKey());
			result.append(" <=== ");
			result.append(loaner.getValue());
			result.append("\n");
		}
		if (loanerToDepts.entrySet().isEmpty()) {
			result.append("blank");
		}
		result.append("]");
		return result.toString();
	}

}
