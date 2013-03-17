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

    public Currency getFirstImportant() {
        if (currenciesMatchingInOrderOfUsage != null && !currenciesMatchingInOrderOfUsage.isEmpty()) {
            return currenciesMatchingInOrderOfUsage.get(0);
        }
        else if (currenciesUsedByDate != null && !currenciesUsedByDate.isEmpty()) {
            return currenciesUsedByDate.get(0);
        }
        else if (currenciesInProject != null && !currenciesInProject.isEmpty()) {
            return currenciesInProject.get(0);
        }
        return null;
    }

    public Currency getSecondImportant() {
        if (currenciesMatchingInOrderOfUsage != null && currenciesMatchingInOrderOfUsage.size() >= 2) {
            return currenciesMatchingInOrderOfUsage.get(1);
        }
        else if (currenciesUsedByDate != null && currenciesUsedByDate.size() >= 2) {
            return currenciesUsedByDate.get(1);
        }
        else if (currenciesInProject != null && currenciesInProject.size() >= 2) {
            return currenciesInProject.get(1);
        }

        return null;
    }
}
