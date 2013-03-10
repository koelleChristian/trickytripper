package de.koelle.christian.trickytripper.model;

import java.util.Currency;

public class HierarchicalCurrency {

    public static final int L1 = 1;
    public static final int L2 = 2;
    public static final int L3 = 3;

    private int level;
    private Currency currency;
    private String longName;

    public HierarchicalCurrency(int level, Currency currency, String longName) {
        super();
        this.level = level;
        this.currency = currency;
        this.longName = longName;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
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
        HierarchicalCurrency other = (HierarchicalCurrency) obj;
        if (currency == null) {
            if (other.currency != null)
                return false;
        }
        else if (!currency.equals(other.currency))
            return false;
        return true;
    }

}
