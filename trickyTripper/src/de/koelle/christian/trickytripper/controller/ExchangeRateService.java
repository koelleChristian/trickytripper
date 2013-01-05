package de.koelle.christian.trickytripper.controller;

import java.util.Currency;
import java.util.List;

import de.koelle.christian.trickytripper.model.ExchangeRate;
import de.koelle.christian.trickytripper.model.ExchangeRateResult;

public interface ExchangeRateService {

    ExchangeRateResult findSuitableRates(Currency currencyFrom, Currency currencyTo);

    List<ExchangeRate> getAllExchangeRates();

    void persistExchangeRates(List<ExchangeRate> rates);

    boolean persistExchangeRate(ExchangeRate rate);

    /**
     * Returns the source currency of the last calculation, if any.
     * 
     * @return Null if there has not been anything used yet.
     */
    Currency getSourceCurrencyUsedLast();

    void saveExchangeRate(ExchangeRate exchangeRate);

    ExchangeRate importExchangeRate(Currency currrencyFrom, Currency currencyTo);

    boolean isOnline();

    // public boolean isOnline() {
    // ConnectivityManager cm =
    // (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
    // NetworkInfo netInfo = cm.getActiveNetworkInfo();
    // if (netInfo != null && netInfo.isConnectedOrConnecting()) {
    // return true;
    // }
    // return false;
    // }
}
