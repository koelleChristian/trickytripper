package de.koelle.christian.trickytripper.exchangerates;

import java.util.Currency;
import java.util.Set;

import de.koelle.christian.trickytripper.exchangerates.impl.ExchangeRateImporterResultCallback;

public interface ExchangeRateImporter {

    void importExchangeRates(Set<Currency> currencies, ExchangeRateImporterResultCallback callback);
    
    void cancelRunningRequests();

}
