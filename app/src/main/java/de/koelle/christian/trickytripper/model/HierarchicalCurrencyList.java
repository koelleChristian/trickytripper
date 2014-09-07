package de.koelle.christian.trickytripper.model;

import java.util.ArrayList;
import java.util.List;

public class HierarchicalCurrencyList {

    public static final int GROUP_POS_ID_USED_MATCHING = 0;
    public static final int GROUP_POS_ID_IN_RATES_MATCHING = 1;

    public static final int GROUP_POS_ID_USED_UNMATCHED = 2;
    public static final int GROUP_POS_ID_IN_RATES_UNMATCHED = 3;

    public static final int GROUP_POS_ID_TRIPS = 4;
    public static final int GROUP_POS_ID_ELSE = 5;

    /* By date of usage */
    private List<CurrencyWithName> currenciesUsedMatching;
    /* By date of usage */
    private List<CurrencyWithName> currenciesInExchangeRatesMatching;
    /* By date of usage */
    private List<CurrencyWithName> currenciesUsedUnmatched;
    /* By date of usage */
    private List<CurrencyWithName> currenciesInExchangeRatesUnmatched;
    /* Alphabetical */
    private List<CurrencyWithName> currenciesInTrips;
    /* Alphabetical */
    private List<CurrencyWithName> currenciesElse;

    public List<CurrencyWithName> getCurrenciesUsedMatching() {
        return currenciesUsedMatching;
    }

    public void setCurrenciesUsedMatching(List<CurrencyWithName> currenciesUsedMatching) {
        this.currenciesUsedMatching = currenciesUsedMatching;
    }

    public List<CurrencyWithName> getCurrenciesUsedUnmatched() {
        return currenciesUsedUnmatched;
    }

    public void setCurrenciesUsedUnmatched(List<CurrencyWithName> currenciesUsedNotMatching) {
        this.currenciesUsedUnmatched = currenciesUsedNotMatching;
    }

    public List<CurrencyWithName> getCurrenciesInExchangeRatesMatching() {
        return currenciesInExchangeRatesMatching;
    }

    public void setCurrenciesInExchangeRatesMatching(List<CurrencyWithName> currenciesInExchangeRatesMatching) {
        this.currenciesInExchangeRatesMatching = currenciesInExchangeRatesMatching;
    }

    public List<CurrencyWithName> getCurrenciesInExchangeRatesUnmatched() {
        return currenciesInExchangeRatesUnmatched;
    }

    public void setCurrenciesInExchangeRatesUnmatched(List<CurrencyWithName> currenciesInExchangeRatesNotMatching) {
        this.currenciesInExchangeRatesUnmatched = currenciesInExchangeRatesNotMatching;
    }

    public List<CurrencyWithName> getCurrenciesInTrips() {
        return currenciesInTrips;
    }

    public void setCurrenciesInTrips(List<CurrencyWithName> currenciesInTrips) {
        this.currenciesInTrips = currenciesInTrips;
    }

    public List<CurrencyWithName> getCurrenciesElse() {
        return currenciesElse;
    }

    public void setCurrenciesElse(List<CurrencyWithName> currenciesElse) {
        this.currenciesElse = currenciesElse;
    }

    public List<List<CurrencyWithName>> createListWithAllLists() {
        List<List<CurrencyWithName>> result = new ArrayList<List<CurrencyWithName>>();
        result.add(currenciesUsedMatching);
        result.add(currenciesInExchangeRatesMatching);
        result.add(currenciesUsedUnmatched);
        result.add(currenciesInExchangeRatesUnmatched);
        result.add(currenciesInTrips);
        result.add(currenciesElse);
        return result;
    }
}
