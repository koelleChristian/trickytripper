package de.koelle.christian.trickytripper.model;

import java.io.Serializable;
import java.util.Currency;

public class TripSummary implements Comparable<TripSummary>, Serializable {

    private static final long serialVersionUID = -353980935866281846L;
    private String name;
    private Currency baseCurrency;
    private long id;

    public TripSummary(String name, Currency baseCurrency) {
        super();
        this.name = name;
        this.baseCurrency = baseCurrency;
    }

    public TripSummary() {
        super();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Currency getBaseCurrency() {
        return baseCurrency;
    }

    public void setBaseCurrency(Currency currency) {
        this.baseCurrency = currency;
    }

    public int compareTo(TripSummary another) {
        if (another == null) {
            return -1;
        }
        return name.compareTo(another.getName());
    }
}
