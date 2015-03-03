package de.koelle.christian.trickytripper.exchangerates.impl;

import java.util.Currency;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import de.koelle.christian.trickytripper.exchangerates.impl.ExchangeRateImporterResultCallback.ExchangeRateImporterResultState;
import de.koelle.christian.trickytripper.model.ImportOrigin;

public class DetachedCurrencyImportTest {

    private int failurecallbackResultAmountCounter = 0;
    private Set<ExchangeRateImporterResultContainer> resultCollector;
    private ExchangeRateImporterImpl importer;

    @Before
    public void init() {
        importer = new ExchangeRateImporterImpl();
        importer.setExchangeRateResultExtractor(new ExchangeRateResultExtractorJsonGoogleImpl());
        resetResultFields();
    }

    @Test
    public void testImporterIgnoreUnsupportedCurrencies() {
        importer.setAsyncExchangeRateResolver(new DetachedTestAsyncExchangeRateResolver("0.9876 French Francs",
                false));

        Set<Currency> input;

        input = new HashSet<Currency>();
        input.add(Currency.getInstance("EUR"));
        input.add(Currency.getInstance("FRF"));

        importer.importExchangeRates(input, new ExchangeRateImporterResultCallback() {

            @Override
            public void deliverResult(ExchangeRateImporterResultContainer resultContainer) {
                resultCollector.add(resultContainer);
            }
        });

        Assert.assertEquals(1, resultCollector.size());
        ExchangeRateImporterResultContainer result = resultCollector.iterator().next();
        Assert.assertEquals(true, result.requestFailed());
        Assert.assertEquals(ExchangeRateImporterResultState.CURRENCY_NOT_ALIVE, result.getResultState());
    }

    @Test
    public void testImporterWithLessThanOne() {
        importer.setAsyncExchangeRateResolver(new DetachedTestAsyncExchangeRateResolver("0.9876 XYs", false));

        Set<Currency> input;

        input = new HashSet<Currency>();
        input.add(Currency.getInstance("EUR"));
        input.add(Currency.getInstance("USD"));
        input.add(Currency.getInstance("TRY"));

        importer.importExchangeRates(input, new ExchangeRateImporterResultCallback() {

            @Override
            public void deliverResult(ExchangeRateImporterResultContainer resultContainer) {
                resultCollector.add(resultContainer);
                if (resultContainer.requestFailed()) {
                    failurecallbackResultAmountCounter++;
                }
            }
        });

        Assert.assertEquals(0, failurecallbackResultAmountCounter);
        Assert.assertEquals(3, resultCollector.size());

        resetResultFields();

        input = new HashSet<Currency>();
        input.add(Currency.getInstance("EUR"));
        input.add(Currency.getInstance("USD"));
        input.add(Currency.getInstance("TRY"));
        input.add(Currency.getInstance("GBP"));

        importer.importExchangeRates(input, new ExchangeRateImporterResultCallback() {

            @Override
            public void deliverResult(ExchangeRateImporterResultContainer resultContainer) {
                resultCollector.add(resultContainer);
                if (resultContainer.requestFailed()) {
                    failurecallbackResultAmountCounter++;
                }
            }
        });

        Assert.assertEquals(0, failurecallbackResultAmountCounter);
        Assert.assertEquals(6, resultCollector.size());
        resetResultFields();
    }

    @Test
    public void testImporterWithMoreThanOne() {

        importer.setAsyncExchangeRateResolver(new DetachedTestAsyncExchangeRateResolver("17.9876 XYs", false));

        Set<Currency> input;

        input = new HashSet<Currency>();
        input.add(Currency.getInstance("EUR"));
        input.add(Currency.getInstance("USD"));
        input.add(Currency.getInstance("TRY"));

        importer.importExchangeRates(input, new ExchangeRateImporterResultCallback() {

            @Override
            public void deliverResult(ExchangeRateImporterResultContainer resultContainer) {
                resultCollector.add(resultContainer);
                if (resultContainer.requestFailed()) {
                    failurecallbackResultAmountCounter++;
                }
            }
        });

        Assert.assertEquals(0, failurecallbackResultAmountCounter);
        Assert.assertEquals(3, resultCollector.size());
        resetResultFields();

        input = new HashSet<Currency>();
        input.add(Currency.getInstance("EUR"));
        input.add(Currency.getInstance("USD"));
        input.add(Currency.getInstance("TRY"));
        input.add(Currency.getInstance("GBP"));

        importer.importExchangeRates(input, new ExchangeRateImporterResultCallback() {

            @Override
            public void deliverResult(ExchangeRateImporterResultContainer resultContainer) {
                resultCollector.add(resultContainer);
                if (resultContainer.requestFailed()) {
                    failurecallbackResultAmountCounter++;
                }
            }
        });

        Assert.assertEquals(0, failurecallbackResultAmountCounter);
        Assert.assertEquals(6, resultCollector.size());
        resetResultFields();
    }

    @Test
    public void testExchangeRateResultExtractorWithFreakValue01() {

        /* This is returned sometimes instead of 1.00008, whatever. */
        importer.setAsyncExchangeRateResolver(new DetachedTestAsyncExchangeRateResolver("1 Singapore dollar",
                true));

        Set<Currency> input;

        input = new HashSet<Currency>();
        input.add(Currency.getInstance("BND"));
        input.add(Currency.getInstance("SGD"));

        importer.importExchangeRates(input, new ExchangeRateImporterResultCallback() {

            @Override
            public void deliverResult(ExchangeRateImporterResultContainer resultContainer) {
                resultCollector.add(resultContainer);
                if (resultContainer.requestFailed()) {
                    failurecallbackResultAmountCounter++;
                }
            }
        });
        Assert.assertEquals(0, failurecallbackResultAmountCounter);
        Assert.assertEquals(1, resultCollector.size());
        Assert.assertEquals(Double.valueOf(1.0), resultCollector.iterator().next().getExchangeRateResult()
                .getExchangeRate());
    }

    @Test
    public void testExchangeRateResultExtractorWithFreakValue02() {

        importer.setAsyncExchangeRateResolver(new DetachedTestAsyncExchangeRateResolver("1"
                + new String(new char[]{0xA0}) + "123.43 Singapore dollar",
                true));

        Set<Currency> input;

        input = new HashSet<Currency>();
        input.add(Currency.getInstance("BND"));
        input.add(Currency.getInstance("SGD"));

        importer.importExchangeRates(input, new ExchangeRateImporterResultCallback() {

            @Override
            public void deliverResult(ExchangeRateImporterResultContainer resultContainer) {
                resultCollector.add(resultContainer);
                if (resultContainer.requestFailed()) {
                    failurecallbackResultAmountCounter++;
                }
            }
        });
        Assert.assertEquals(0, failurecallbackResultAmountCounter);
        Assert.assertEquals(1, resultCollector.size());
        Assert.assertEquals(Double.valueOf(1123.43), resultCollector.iterator().next().getExchangeRateResult()
                .getExchangeRate());
    }

    @Test
    public void testExchangeRateResultExtractorWithUnparsableValue() {
        importer.setAsyncExchangeRateResolver(new DetachedTestAsyncExchangeRateResolver("Hello", false));

        Set<Currency> input;

        input = new HashSet<Currency>();
        input.add(Currency.getInstance("EUR"));
        input.add(Currency.getInstance("USD"));

        importer.importExchangeRates(input, new ExchangeRateImporterResultCallback() {

            @Override
            public void deliverResult(ExchangeRateImporterResultContainer resultContainer) {
                resultCollector.add(resultContainer);
            }
        });

        Assert.assertEquals(1, resultCollector.size());
        ExchangeRateImporterResultContainer result = resultCollector.iterator().next();
        Assert.assertEquals(true, result.requestFailed());
        Assert.assertEquals(ExchangeRateImporterResultState.NON_PARSABLE_JSON_RESULT, result.getResultState());
    }

    private void resetResultFields() {
        failurecallbackResultAmountCounter = 0;
        resultCollector = new LinkedHashSet<ExchangeRateImporterResultContainer>();
    }

    /**
     * Resolver to be used in conjunction with this test only. *
     */
    private final class DetachedTestAsyncExchangeRateResolver implements AsyncExchangeRateResolver {

        private int counter = 0;
        private final String resultPrefix;
        private final boolean returnPrefixOnly;
        private boolean stopped;

        public DetachedTestAsyncExchangeRateResolver(String resultPrefix, boolean returnPrefixOnly) {
            this.resultPrefix = resultPrefix;
            this.returnPrefixOnly = returnPrefixOnly;
        }

        @Override
        public void getExchangeRate(Currency from, Currency to, AsyncExchangeRateResolverResultCallback callback) {
            stopped = false;
            counter++;
            String result = resultPrefix;
            if (!returnPrefixOnly) {
                result = result + counter + " XXX";
            }
            if (!stopped) {
                callback.deliverResult(result);
            }
        }

        @Override
        public ImportOrigin getOriginToBeUsed() {
            return ImportOrigin.GOOGLE;
        }

        @Override
        public long calculateResponseTime(Currency from, Currency to) {
            return 50;
        }

        @Override
        public void cancelRunningRequests() {
           stopped = true;
        }

    }
}
