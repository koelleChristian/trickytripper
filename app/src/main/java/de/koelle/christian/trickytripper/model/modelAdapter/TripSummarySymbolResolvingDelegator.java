package de.koelle.christian.trickytripper.model.modelAdapter;

import java.util.Currency;

import android.content.res.Resources;
import de.koelle.christian.common.utils.CurrencyUtil;
import de.koelle.christian.trickytripper.model.TripSummary;

public class TripSummarySymbolResolvingDelegator extends TripSummary {

    private static final long serialVersionUID = -5240274280785431424L;

    private final TripSummary nested;
    private final Resources resources;

    public TripSummarySymbolResolvingDelegator(TripSummary nested, Resources resources) {
        super();
        this.nested = nested;
        this.resources = resources;
    }

    @Override
    public String getName() {
        return nested.getName();
    }

    @Override
    public void setName(String name) {
        nested.setName(name);
    }

    @Override
    public long getId() {
        return nested.getId();
    }

    @Override
    public void setId(long id) {
        nested.setId(id);
    }

    @Override
    public Currency getBaseCurrency() {
        return nested.getBaseCurrency();
    }

    @Override
    public void setBaseCurrency(Currency currency) {
        nested.setBaseCurrency(currency);
    }

    @Override
    public int compareTo(TripSummary another) {
        return nested.compareTo(another);
    }
    

    public TripSummary getNested() {
        return nested;
    }

    @Override
    public String toString() {
        return nested.getName() + " " + CurrencyUtil.getSymbolToCurrency(resources, nested.getBaseCurrency(), true);
    }
}
