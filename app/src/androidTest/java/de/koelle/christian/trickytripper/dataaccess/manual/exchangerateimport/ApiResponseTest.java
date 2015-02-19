package de.koelle.christian.trickytripper.dataaccess.manual.exchangerateimport;

import android.test.ApplicationTestCase;

import junit.framework.Assert;

import java.util.Currency;
import java.util.LinkedHashSet;
import java.util.Set;

import de.koelle.christian.trickytripper.TrickyTripperApp;
import de.koelle.christian.trickytripper.exchangerates.impl.AsyncExchangeRateHttpResolverGoogleImpl;
import de.koelle.christian.trickytripper.exchangerates.impl.ExchangeRateImporterImpl;
import de.koelle.christian.trickytripper.exchangerates.impl.ExchangeRateImporterResultCallback;
import de.koelle.christian.trickytripper.exchangerates.impl.ExchangeRateImporterResultContainer;
import de.koelle.christian.trickytripper.exchangerates.impl.ExchangeRateResultExtractorHttpGoogleImpl;

public class ApiResponseTest extends ApplicationTestCase<TrickyTripperApp> {

    public static final int SLEEP_ITERATIONS = 10;

    ExchangeRateImporterResultContainer resultContainerHere;

    private ExchangeRateImporterImpl importer;

    public ApiResponseTest() {
        super(TrickyTripperApp.class);
    }

    @Override
    protected void setUp() {
        importer = new ExchangeRateImporterImpl();
        importer.setAsyncExchangeRateResolver(new AsyncExchangeRateHttpResolverGoogleImpl(getContext()));
        importer.setExchangeRateResultExtractor(new ExchangeRateResultExtractorHttpGoogleImpl());
        importer.setChunkDelay(2000);
        importer.setChunkSize(50);
    }


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

    /*public void testThreadedDesign() {
        final CountDownLatch latch = new CountDownLatch(1);

        HandlerThread testThread = new HandlerThread("testThreadedDesign thread");
        testThread.start();

        Set<Currency> currencies = new LinkedHashSet<Currency>();
        currencies.add(Currency.getInstance("EUR"));
        currencies.add(Currency.getInstance("USD"));
        importer.importSelectedExchangeRates(currencies, new ExchangeRateImporterResultCallback() {
            @Override
            public void deliverResult(ExchangeRateImporterResultContainer resultContainer) {
                resultContainerHere = resultContainer;
            }
        });

        new ThingThatOperatesInTheBackground().doYourWorst(testThread.getLooper(),
                new SomeListenerThatTotallyShouldExist() {
                    public void onComplete() {
                        result.success = true;
                        finished();
                    }

                    public void onFizzBarError() {
                        result.success = false;
                        finished();
                    }

                    private void finished() {
                        latch.countDown();
                    }
                });

        try {
            latch.await();
        } catch (InterruptedException e) {
           Assert.fail();
        }

        testThread.getLooper().quit();

        Assert.assertNotNull(resultContainerHere);
        Assert.assertTrue(ExchangeRateImporterResultCallback.ExchangeRateImporterResultState.SUCCESS == resultContainerHere.getResultState());
        Assert.assertNotNull(resultContainerHere.getExchangeRateResult().getExchangeRate());
        Assert.assertTrue(Double.valueOf(0.0001) < resultContainerHere.getExchangeRateResult().getExchangeRate());
    }*/
}
