package de.koelle.christian.trickytripper.ui.model;

import de.koelle.christian.trickytripper.model.Amount;
import de.koelle.christian.trickytripper.model.Participant;

public class ParticipantRow {

	private Participant participant;
	
	private int amountOfPaymentLines;
	private Amount sumPaid;
	private Amount sumSpent;
	private Amount balance;

	public int getAmountOfPaymentLines() {
		return amountOfPaymentLines;
	}

	public void setAmountOfPaymentLines(int amountOfPaymentLines) {
		this.amountOfPaymentLines = amountOfPaymentLines;
	}

	public Amount getSumPaid() {
		return sumPaid;
	}

	public void setSumPaid(Amount moneyPaid) {
		this.sumPaid = moneyPaid;
	}

	public Amount getSumSpent() {
		return sumSpent;
	}

	public void setSumSpent(Amount moneySpent) {
		this.sumSpent = moneySpent;
	}

	public Participant getParticipant() {
		return participant;
	}

	public void setParticipant(Participant participant) {
		this.participant = participant;
	}

	public Amount getBalance() {
		return balance;
	}

	public void setBalance(Amount balance) {
		this.balance = balance;
	}
	
	
}
