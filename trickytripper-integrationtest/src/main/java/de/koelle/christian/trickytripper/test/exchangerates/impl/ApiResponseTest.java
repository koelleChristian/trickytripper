package de.koelle.christian.trickytripper.test.exchangerates.impl;

import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

import de.koelle.christian.trickytripper.exchangerates.impl.AsyncExchangeRateJsonResolver;
import de.koelle.christian.trickytripper.exchangerates.impl.AsyncExchangeRateJsonResolverGoogleImpl;

public class ApiResponseTest extends AbstractCurrencyImportTest {

    public ApiResponseTest() {
        super();
    }

    public void testRealCurrencyImportTest() {

        List<Long> responseTimesInMs = new ArrayList<Long>();
        AsyncExchangeRateJsonResolver resolver = new AsyncExchangeRateJsonResolverGoogleImpl(getContext());
        for (int i = 0; i < 10; i++) {
            responseTimesInMs.add(resolver.calculateResponseTime(Currency.getInstance("EUR"),
                    Currency.getInstance("USD")));
        }
        System.out.println("ResponseTimes: " + responseTimesInMs);
    }

}
