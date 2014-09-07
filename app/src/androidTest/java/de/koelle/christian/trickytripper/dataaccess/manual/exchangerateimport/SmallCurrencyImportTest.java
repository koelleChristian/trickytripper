package de.koelle.christian.trickytripper.dataaccess.manual.exchangerateimport;

import java.util.Currency;
import java.util.LinkedHashSet;
import java.util.Set;

import junit.framework.Assert;

import de.koelle.christian.common.utils.CurrencyUtil;

public class SmallCurrencyImportTest extends AbstractCurrencyImportTest {

    private static final int maxSleepIterations = 25;
    private int counter;

    public SmallCurrencyImportTest() {
        super();
    }

    public void testExchangeRateAvailability10() {
        conductBiggerTest(0, 20);
    }

    public void testExchangeRateAvailability20() {
        conductBiggerTest(10, 20);
    }

    public void testExchangeRateAvailability30() {
        conductBiggerTest(20, 30);
    }

    public void testExchangeRateAvailability40() {
        conductBiggerTest(30, 40);
    }

    public void testExchangeRateAvailability50() {
        conductBiggerTest(40, 50);
    }

    public void testExchangeRateAvailability60() {
        conductBiggerTest(50, 60);
    }

    public void testExchangeRateAvailability70() {
        conductBiggerTest(70, 80);
    }

    public void testExchangeRateAvailability80() {
        conductBiggerTest(80, 89);
    }

    private void conductBiggerTest(int from, int to) {

        Set<Currency> input;
        input = new LinkedHashSet<Currency>();
        for (int i = from; i < to; i++) {
            input.add(CurrencyUtil.getAllCurrenciesAlive().get(i));
        }

        int ceiling = CurrencyUtil.calcExpectedAmountOfExchangeRates(input.size());

        getImporter().importExchangeRates(input, new ResultCollectingExchangeRateImporterResultCallback());

        waitForResult(ceiling);
    }

    private void waitForResult(int ceiling) {
        while (getResultCollector().size() < ceiling) {
            waitForResult(maxSleepIterations, counter);
            counter++;
        }
        if(counter >= maxSleepIterations){
            Assert.fail("No response from exchange rate resolution service.");
        }
    }

    /**
     * Removed from automatic execution, as this is a manual test.
     */
    public void realCurrencyImportManual() {

        Set<Currency> input;

        input = new LinkedHashSet<Currency>();
        input.add(Currency.getInstance("BND"));
        input.add(Currency.getInstance("SGD"));

        int ceiling = CurrencyUtil.calcExpectedAmountOfExchangeRates(input.size());

        getImporter().importExchangeRates(input, new ResultCollectingExchangeRateImporterResultCallback());
        waitForResult(ceiling);

    }

    public void testRealCurrencyImportTest() {

        Set<Currency> input;

        input = new LinkedHashSet<Currency>();
        input.add(Currency.getInstance("EUR"));
        input.add(Currency.getInstance("USD"));
        input.add(Currency.getInstance("TRY"));

        getImporter().importExchangeRates(input, new ResultCollectingExchangeRateImporterResultCallback());

        int expectedAmountOfResults;

        expectedAmountOfResults = 3;
        waitForResult(expectedAmountOfResults);
        resetResultFields();

        input = new LinkedHashSet<Currency>();
        input.add(Currency.getInstance("EUR"));
        input.add(Currency.getInstance("USD"));
        input.add(Currency.getInstance("TRY"));
        input.add(Currency.getInstance("GBP"));

        getImporter().importExchangeRates(input, new ResultCollectingExchangeRateImporterResultCallback());
        expectedAmountOfResults = 6;
        waitForResult(expectedAmountOfResults);
    }

}
