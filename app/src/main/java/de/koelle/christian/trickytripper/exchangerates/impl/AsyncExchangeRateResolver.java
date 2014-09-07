package de.koelle.christian.trickytripper.exchangerates.impl;

import java.util.Currency;

import de.koelle.christian.trickytripper.model.ImportOrigin;

public interface AsyncExchangeRateResolver {

    void getExchangeRate(Currency from, Currency to, AsyncExchangeRateResolverResultCallback callback);

    long calculateResponseTime(Currency from, Currency to);

    ImportOrigin getOriginToBeUsed();
    
    void cancelRunningRequests();
}
