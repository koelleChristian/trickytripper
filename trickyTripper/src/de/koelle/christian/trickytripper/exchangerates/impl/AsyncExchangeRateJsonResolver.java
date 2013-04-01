package de.koelle.christian.trickytripper.exchangerates.impl;

import java.util.Currency;

import de.koelle.christian.trickytripper.model.ImportOrigin;

public interface AsyncExchangeRateJsonResolver {

    void getExchangeRate(Currency from, Currency to, AsyncExchangeRateJsonResolverResultCallback callback);

    long calculateResponseTime(Currency from, Currency to);

    ImportOrigin getOriginToBeUsed();
    
    void cancelRunningRequests();
}
