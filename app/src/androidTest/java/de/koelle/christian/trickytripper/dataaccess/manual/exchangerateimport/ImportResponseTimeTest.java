package de.koelle.christian.trickytripper.dataaccess.manual.exchangerateimport;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

import de.koelle.christian.trickytripper.exchangerates.impl.AsyncExchangeRateJsonResolverExchangeratesapiIoImpl;
import de.koelle.christian.trickytripper.exchangerates.impl.AsyncExchangeRateResolver;

public class ImportResponseTimeTest extends AbstractCurrencyImportTest {

    public ImportResponseTimeTest() {
        super();
    }

    @Test
    public void testRealCurrencyImportTest() {
        List<Long> responseTimesInMs = new ArrayList<>();
        AsyncExchangeRateResolver resolver = new AsyncExchangeRateJsonResolverExchangeratesapiIoImpl(context);
        for (int i = 0; i < 10; i++) {
            responseTimesInMs.add(resolver.calculateResponseTime(Currency.getInstance("EUR"),
                    Currency.getInstance("USD")));
        }
        System.out.println("ResponseTimes: " + responseTimesInMs);
    }

}
