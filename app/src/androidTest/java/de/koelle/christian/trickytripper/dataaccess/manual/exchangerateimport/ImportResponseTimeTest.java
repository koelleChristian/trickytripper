package de.koelle.christian.trickytripper.dataaccess.manual.exchangerateimport;

import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

import de.koelle.christian.trickytripper.exchangerates.impl.AsyncExchangeRateResolver;
import de.koelle.christian.trickytripper.exchangerates.impl.AsyncExchangeRateJsonResolverGoogleImpl;

public class ImportResponseTimeTest extends AbstractCurrencyImportTest {

    public ImportResponseTimeTest() {
        super();
    }

    public void testRealCurrencyImportTest() {

        List<Long> responseTimesInMs = new ArrayList<Long>();
        AsyncExchangeRateResolver resolver = new AsyncExchangeRateJsonResolverGoogleImpl(getContext());
        for (int i = 0; i < 10; i++) {
            responseTimesInMs.add(resolver.calculateResponseTime(Currency.getInstance("EUR"),
                    Currency.getInstance("USD")));
        }
        System.out.println("ResponseTimes: " + responseTimesInMs);
    }

}
