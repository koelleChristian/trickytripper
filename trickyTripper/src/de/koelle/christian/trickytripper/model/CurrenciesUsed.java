package de.koelle.christian.trickytripper.model;

import java.util.Currency;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CurrenciesUsed {

    private List<Currency> currenciesMatchingInOrderOfUsage;
    private List<Currency> currenciesUsedByDate;
    private List<Currency> currenciesInProject;

    public List<Currency> getCurrenciesMatchingInOrderOfUsage() {
        return currenciesMatchingInOrderOfUsage;
    }

    public void setCurrenciesMatchingInOrderOfUsage(List<Currency> currienciesMatchingInOrderOfUsage) {
        this.currenciesMatchingInOrderOfUsage = currienciesMatchingInOrderOfUsage;
    }

    public List<Currency> getCurrenciesUsedByDate() {
        return currenciesUsedByDate;
    }

    public void setCurrenciesUsedByDate(List<Currency> currienciesUsedByDate) {
        this.currenciesUsedByDate = currienciesUsedByDate;
    }

    public List<Currency> getCurrenciesInProject() {
        return currenciesInProject;
    }

    public void setCurrenciesInProject(List<Currency> currienciesInProject) {
        this.currenciesInProject = currienciesInProject;
    }

    public Set<Currency> getCurrenciesAlreadyFilled() {
        Set<Currency> result = new HashSet<Currency>();
        result.addAll(currenciesMatchingInOrderOfUsage);
        result.addAll(currenciesUsedByDate);
        if (currenciesInProject != null) {
            result.addAll(currenciesInProject);
        }
        return result;
    }

    @Override
    public String toString() {
        return "CurrenciesUsed [currenciesMatchingInOrderOfUsage=" + currenciesMatchingInOrderOfUsage
                + ", currenciesUsedByDate=" + currenciesUsedByDate + ", currenciesInProject=" + currenciesInProject
                + "]";
    }

}
