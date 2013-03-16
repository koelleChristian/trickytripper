package de.koelle.christian.trickytripper.model;

import java.util.Currency;

public class CurrencyWithName {

    private Currency currency;
    private String longName;

    public CurrencyWithName(Currency currency, String longName) {
        this.currency = currency;
        this.longName = longName;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public String getLongName() {
        return longName;
    }

    public void setLongName(String longName) {
        this.longName = longName;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((currency == null) ? 0 : currency.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        CurrencyWithName other = (CurrencyWithName) obj;
        if (currency == null) {
            if (other.currency != null)
                return false;
        }
        else if (!currency.equals(other.currency))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return longName;
    }

}
