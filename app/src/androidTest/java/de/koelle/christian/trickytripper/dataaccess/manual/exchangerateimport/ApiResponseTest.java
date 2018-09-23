package de.koelle.christian.trickytripper.dataaccess.manual.exchangerateimport;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.SmallTest;


import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Currency;
import java.util.LinkedHashSet;
import java.util.Set;

import de.koelle.christian.trickytripper.exchangerates.impl.AsyncExchangeRateJsonResolverFccaImpl;
import de.koelle.christian.trickytripper.exchangerates.impl.ExchangeRateImporterImpl;
import de.koelle.christian.trickytripper.exchangerates.impl.ExchangeRateImporterResultCallback;
import de.koelle.christian.trickytripper.exchangerates.impl.ExchangeRateImporterResultContainer;
import de.koelle.christian.trickytripper.exchangerates.impl.ExchangeRateResultExtractorJsonGoogleImpl;

@SmallTest
public class ApiResponseTest {

    ExchangeRateImporterResultContainer resultContainerHere;

    private ExchangeRateImporterImpl importer;
    private Context context;

    @Before
    public void setUp() {
        context = InstrumentationRegistry.getContext();
        importer = new ExchangeRateImporterImpl();
        importer.setAsyncExchangeRateResolver(new AsyncExchangeRateJsonResolverFccaImpl(context));
        importer.setExchangeRateResultExtractor(new ExchangeRateResultExtractorJsonGoogleImpl());
        importer.setChunkDelay(2000);
        importer.setChunkSize(50);
    }

    @Test
    public void testJSONShitTest() throws JSONException {
        String JSON_STRING = "{\"EUR_USD\":{\"val\":1.174549}}";
        JSONObject object = new JSONObject(JSON_STRING.replace("\"", "'"));
        System.out.println(object.get("EUR_USD"));
        Assert.assertEquals("1.174549", new JSONObject(object.get(object.keys().next()).toString()).getString("val"));
    }

    @Test
    public void testCurrencyApiAvailabilityTest() {
        Set<Currency> currencies = new LinkedHashSet<>();
        currencies.add(Currency.getInstance("EUR"));
        currencies.add(Currency.getInstance("USD"));
        importer.importExchangeRates(currencies, new ExchangeRateImporterResultCallback() {
            @Override
            public void deliverResult(ExchangeRateImporterResultContainer resultContainer) {
                resultContainerHere = resultContainer;
            }
        });
        //int iteration = 0;
        //if (iteration < SLEEP_ITERATIONS) {
        try {
            System.out.println("Sleep ...");
            Thread.currentThread().sleep(10000);
            //iteration++;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // }
        System.out.println("Sleeping done.");

        Assert.assertNotNull(resultContainerHere);
        Assert.assertTrue(ExchangeRateImporterResultCallback.ExchangeRateImporterResultState.SUCCESS == resultContainerHere.getResultState());
        Assert.assertNotNull(resultContainerHere.getExchangeRateResult().getExchangeRate());
        Assert.assertTrue(0.0001 < resultContainerHere.getExchangeRateResult().getExchangeRate());
    }
}
