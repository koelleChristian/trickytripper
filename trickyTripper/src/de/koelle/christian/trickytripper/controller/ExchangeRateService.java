package de.koelle.christian.trickytripper.controller;

import java.util.Currency;
import java.util.List;

import de.koelle.christian.trickytripper.model.ExchangeRate;

public interface ExchangeRateService {

    List<ExchangeRate> getAllExchangeRates(Currency currencyFrom, Currency currencyTo);

    void saveExchangeRate(ExchangeRate exchangeRate);

    ExchangeRate importExchangeRate(Currency currrencyFrom, Currency currencyTo);

    boolean isOnline();
}
