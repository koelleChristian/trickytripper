package de.koelle.christian.trickytripper.model;

import java.util.List;

public class HierarchicalCurrencyList {

    /* By date of usage */
    private List<CurrencyWithName> currenciesMatchingInOrderOfUsage;
    /* By date of usage */
    private List<CurrencyWithName> currenciesUsedByDate;
    /* Alphabetical */
    private List<CurrencyWithName> currenciesInProject;
    /* Alphabetical */
    private List<CurrencyWithName> currenciesElse;

    public List<CurrencyWithName> getCurrenciesMatchingInOrderOfUsage() {
        return currenciesMatchingInOrderOfUsage;
    }

    public void setCurrenciesMatchingInOrderOfUsage(List<CurrencyWithName> currenciesMatchingInOrderOfUsage) {
        this.currenciesMatchingInOrderOfUsage = currenciesMatchingInOrderOfUsage;
    }

    public List<CurrencyWithName> getCurrenciesUsedByDate() {
        return currenciesUsedByDate;
    }

    public void setCurrenciesUsedByDate(List<CurrencyWithName> currenciesUsedByDate) {
        this.currenciesUsedByDate = currenciesUsedByDate;
    }

    public List<CurrencyWithName> getCurrenciesInProject() {
        return currenciesInProject;
    }

    public void setCurrenciesInProject(List<CurrencyWithName> currenciesInProject) {
        this.currenciesInProject = currenciesInProject;
    }

    public List<CurrencyWithName> getCurrenciesElse() {
        return currenciesElse;
    }

    public void setCurrenciesElse(List<CurrencyWithName> currenciesElse) {
        this.currenciesElse = currenciesElse;
    }

}
