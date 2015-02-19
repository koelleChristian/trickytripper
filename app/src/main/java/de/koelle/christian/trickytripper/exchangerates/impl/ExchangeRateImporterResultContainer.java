package de.koelle.christian.trickytripper.exchangerates.impl;

import java.util.Currency;

import de.koelle.christian.trickytripper.exchangerates.impl.ExchangeRateImporterResultCallback.ExchangeRateImporterResultState;
import de.koelle.christian.trickytripper.model.ExchangeRate;

public class ExchangeRateImporterResultContainer {
    public final ExchangeRate exchangeRateResult;
    public final Currency from;
    public final Currency to;
    public final ExchangeRateImporterResultState resultState;
    public final String stateComment;

    public ExchangeRateImporterResultContainer(ExchangeRate exchangeRateResult, Currency from, Currency to,
            ExchangeRateImporterResultState resultState, String stateComment) {
        this.exchangeRateResult = exchangeRateResult;
        this.from = from;
        this.to = to;
        this.resultState = resultState;
        this.stateComment = stateComment;
    }

    public boolean requestWasSuccess() {
        return ExchangeRateImporterResultState.SUCCESS.equals(resultState);
    }

    public boolean requestFailed() {
        return !requestWasSuccess();
    }

    public ExchangeRate getExchangeRateResult() {
        return exchangeRateResult;
    }

    public Currency getFrom() {
        return from;
    }

    public Currency getTo() {
        return to;
    }

    public ExchangeRateImporterResultState getResultState() {
        return resultState;
    }

    public String getStateComment() {
        return stateComment;
    }

    @Override
    public String toString() {
        return "ExchangeRateImporterResultContainer [exchangeRateResult=" + exchangeRateResult + ", from=" + from
                + ", to=" + to + ", resultState=" + resultState + ", stateComment=" + stateComment + "]";
    }

}