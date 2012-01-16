package de.koelle.christian.trickytripper.factories;

import java.util.Currency;

import de.koelle.christian.trickytripper.model.Amount;

public class AmountFactory {

    private Currency currency;

    public Amount createAmount(Double value) {
        Amount amount;
        amount = new Amount();
        amount.setUnit(currency);
        amount.setValue(value);
        return amount;
    }

    public Amount cloneAmount(Amount value) {
        Amount amount;
        amount = new Amount();
        amount.setUnit(value.getUnit());
        amount.setValue(new Double(value.getValue().doubleValue()));
        return amount;
    }

    public Amount createAmount() {
        return createAmount(0d);
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

}
