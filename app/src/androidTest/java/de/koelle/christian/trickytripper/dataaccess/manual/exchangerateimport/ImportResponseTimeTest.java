package de.koelle.christian.trickytripper.dataaccess.manual.exchangerateimport;

import android.support.test.filters.SmallTest;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

import de.koelle.christian.trickytripper.exchangerates.impl.AsyncExchangeRateResolver;
import de.koelle.christian.trickytripper.exchangerates.impl.AsyncExchangeRateJsonResolverFccaImpl;

@SmallTest
public class ImportResponseTimeTest extends AbstractCurrencyImportTest {

    public ImportResponseTimeTest() {
        super();
    }

    @Test
    public void testRealCurrencyImportTest() {
        List<Long> responseTimesInMs = new ArrayList<Long>();
        AsyncExchangeRateResolver resolver = new AsyncExchangeRateJsonResolverFccaImpl(context);
        for (int i = 0; i < 10; i++) {
            responseTimesInMs.add(resolver.calculateResponseTime(Currency.getInstance("EUR"),
                    Currency.getInstance("USD")));
        }
        System.out.println("ResponseTimes: " + responseTimesInMs);
    }

}
