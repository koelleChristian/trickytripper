package de.koelle.christian.trickytripper.controller;

import java.util.Currency;
import java.util.List;

import de.koelle.christian.trickytripper.model.ExchangeRate;
import de.koelle.christian.trickytripper.model.ImportSettings;

public interface ExchangeRateController {

    List<ExchangeRate> findSuitableRates(Currency currencyFrom, Currency currencyTo);

    List<ExchangeRate> getAllExchangeRatesWithoutInversion();

    void persistImportedExchangeRate(ExchangeRate rate, boolean replaceWhenAlreadyImported);

    ExchangeRate persistExchangeRate(ExchangeRate rate);

    boolean doesExchangeRateAlreadyExist(ExchangeRate rate);

    boolean deleteExchangeRates(List<ExchangeRate> rates);

    void persistExchangeRateUsedLast(ExchangeRate exchangeRateUsedLast);

    ImportSettings getImportSettingsUsedLast();

    void saveImportSettingsUsedLast(ImportSettings importSettings);

    ExchangeRate getExchangeRateById(Long technicalId);

    long getNextExchangeRateAutoSaveSeqNumber();

}
