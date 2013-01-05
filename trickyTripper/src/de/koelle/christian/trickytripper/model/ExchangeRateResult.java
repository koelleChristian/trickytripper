package de.koelle.christian.trickytripper.model;

import java.util.List;

public class ExchangeRateResult {

    private List<ExchangeRate> matchingExchangeRates;
    private ExchangeRate rateUsedLastTime;

    public ExchangeRateResult(List<ExchangeRate> matchingExchangeRates, ExchangeRate rateUsedLastTime) {
        super();
        this.matchingExchangeRates = matchingExchangeRates;
        this.rateUsedLastTime = rateUsedLastTime;
    }

    public List<ExchangeRate> getMatchingExchangeRates() {
        return matchingExchangeRates;
    }

    public void setMatchingExchangeRates(List<ExchangeRate> matchingExchangeRates) {
        this.matchingExchangeRates = matchingExchangeRates;
    }

    public ExchangeRate getRateUsedLastTime() {
        return rateUsedLastTime;
    }

    public void setRateUsedLastTime(ExchangeRate rateUsedLastTime) {
        this.rateUsedLastTime = rateUsedLastTime;
    }

}
