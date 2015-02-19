package de.koelle.christian.trickytripper.model;

import java.util.Currency;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CurrenciesUsed {

    private List<Currency> currenciesUsedMatching;
    private List<Currency> currenciesUsedUnmatched;
    private List<Currency> currenciesInExchangeRatesMatching;
    private List<Currency> currenciesInExchangeRatesUnmatched;
    private List<Currency> currenciesInTrips;

    public List<Currency> getCurrenciesUsedMatching() {
        return currenciesUsedMatching;
    }

    public void setCurrenciesUsedMatching(List<Currency> currenciesMatchingInUse) {
        this.currenciesUsedMatching = currenciesMatchingInUse;
    }

    public List<Currency> getCurrenciesUsedUnmatched() {
        return currenciesUsedUnmatched;
    }

    public void setCurrenciesUsedUnmatched(List<Currency> currenciesNotMatchingInUse) {
        this.currenciesUsedUnmatched = currenciesNotMatchingInUse;
    }

    public List<Currency> getCurrenciesInExchangeRatesMatching() {
        return currenciesInExchangeRatesMatching;
    }

    public void setCurrenciesInExchangeRatesMatching(List<Currency> currenciesInExchangeRatesUnusedMatching) {
        this.currenciesInExchangeRatesMatching = currenciesInExchangeRatesUnusedMatching;
    }

    public List<Currency> getCurrenciesInExchangeRatesUnmatched() {
        return currenciesInExchangeRatesUnmatched;
    }

    public void setCurrenciesInExchangeRatesUnmatched(List<Currency> currenciesInExchangeRatesUnusedNotMatching) {
        this.currenciesInExchangeRatesUnmatched = currenciesInExchangeRatesUnusedNotMatching;
    }

    public List<Currency> getCurrenciesInTrips() {
        return currenciesInTrips;
    }

    public void setCurrenciesInTrips(List<Currency> currenciesInProject) {
        this.currenciesInTrips = currenciesInProject;
    }

    public Set<Currency> getCurrenciesAlreadyFilled() {
        Set<Currency> result = new HashSet<>();
        result.addAll(currenciesUsedMatching);
        result.addAll(currenciesUsedUnmatched);
        if (currenciesInExchangeRatesMatching != null) {
            result.addAll(currenciesInExchangeRatesMatching);
        }
        if (currenciesInExchangeRatesUnmatched != null) {
            result.addAll(currenciesInExchangeRatesUnmatched);
        }
        if (currenciesInTrips != null) {
            result.addAll(currenciesInTrips);
        }
        return result;
    }

    @Override
    public String toString() {
        return "CurrenciesUsed [currenciesUsedMatching=" + currenciesUsedMatching
                + ", currenciesUsedUnmatched=" + currenciesUsedUnmatched
                + ", currenciesInExchangeRatesUnmatched="
                + currenciesInExchangeRatesUnmatched + ", currenciesInTrips=" + currenciesInTrips + "]";
    }

    public Currency getFirstImportant() {
        if (currenciesUsedMatching != null && !currenciesUsedMatching.isEmpty()) {
            return currenciesUsedMatching.get(0);
        }
        else if (currenciesInExchangeRatesMatching != null && !currenciesInExchangeRatesMatching.isEmpty()) {
            return currenciesInExchangeRatesMatching.get(0);
        }
        else if (currenciesUsedUnmatched != null && !currenciesUsedUnmatched.isEmpty()) {
            return currenciesUsedUnmatched.get(0);
        }
        else if (currenciesInExchangeRatesUnmatched != null
                && !currenciesInExchangeRatesUnmatched.isEmpty()) {
            return currenciesInExchangeRatesUnmatched.get(0);
        }
        else if (currenciesInTrips != null && !currenciesInTrips.isEmpty()) {
            return currenciesInTrips.get(0);
        }
        return null;
    }
}
