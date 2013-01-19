package de.koelle.christian.trickytripper.controller;

import java.util.Currency;
import java.util.List;

import de.koelle.christian.trickytripper.model.ExchangeRate;
import de.koelle.christian.trickytripper.model.ExchangeRateResult;

public interface ExchangeRateController {

    ExchangeRateResult findSuitableRates(Currency currencyFrom, Currency currencyTo);

    List<ExchangeRate> getAllExchangeRatesWithoutInversion();

    void persitImportedExchangeRate(ExchangeRate rate, boolean replaceWhenAlreadyImported);

    ExchangeRate persistExchangeRate(ExchangeRate rate);

    boolean doesExchangeRateAlreadyExist(ExchangeRate exchangeRate);

    boolean deleteExchangeRate(ExchangeRate row);

    /**
     * Returns the source currency of the last calculation, if any.
     * 
     * @return Null if there has not been anything used yet.
     */
    Currency getSourceCurrencyUsedLast();

    void persistLastExchangeRateUsageSettings(Currency sourceCurrencyLastUsed, ExchangeRate exchangeRateUsedLast);

}
