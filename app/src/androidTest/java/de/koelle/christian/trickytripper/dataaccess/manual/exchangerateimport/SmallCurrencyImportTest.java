package de.koelle.christian.trickytripper.dataaccess.manual.exchangerateimport;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Currency;
import java.util.LinkedHashSet;
import java.util.Set;

import de.koelle.christian.common.utils.CurrencyUtil;

@RunWith(AndroidJUnit4.class)
@SmallTest
public class SmallCurrencyImportTest extends AbstractCurrencyImportTest {

    private static final int maxSleepIterations = 25;
    private int counter;


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
        input = new LinkedHashSet<>();
        for (int i = from; i < to; i++) {
            input.add(CurrencyUtil.getAllCurrenciesWithRetrievableRate().get(i));
        }

        int ceiling = CurrencyUtil.calcExpectedAmountOfExchangeRates(input.size());

        getImporter().importExchangeRates(input, new ResultCollectingAssertingExchangeRateImporterResultCallback());

        waitForResult(ceiling);
    }

    private void waitForResult(int ceiling) {
        while (getResultCollector().size() < ceiling) {
            waitForResult(maxSleepIterations, counter);
            counter++;
        }
        if (counter >= maxSleepIterations) {
            Assert.fail("No response from exchange rate resolution service.");
        }
    }

    /**
     * Removed from automatic execution, as this is a manual test.
     */
    public void realCurrencyImportManual() {

        Set<Currency> input;

        input = new LinkedHashSet<>();
        input.add(Currency.getInstance("BND"));
        input.add(Currency.getInstance("SGD"));

        int ceiling = CurrencyUtil.calcExpectedAmountOfExchangeRates(input.size());

        getImporter().importExchangeRates(input, new ResultCollectingAssertingExchangeRateImporterResultCallback());
        waitForResult(ceiling);

    }

    @Test
    public void testRealCurrencyImportTest() {

        Set<Currency> input;

        input = new LinkedHashSet<>();
        input.add(Currency.getInstance("EUR"));
        input.add(Currency.getInstance("USD"));
        input.add(Currency.getInstance("TRY"));

        getImporter().importExchangeRates(input, new ResultCollectingAssertingExchangeRateImporterResultCallback());

        int expectedAmountOfResults;

        expectedAmountOfResults = 3;
        waitForResult(expectedAmountOfResults);
        resetResultFields();

        input = new LinkedHashSet<>();
        input.add(Currency.getInstance("EUR"));
        input.add(Currency.getInstance("USD"));
        input.add(Currency.getInstance("TRY"));
        input.add(Currency.getInstance("GBP"));

        getImporter().importExchangeRates(input, new ResultCollectingAssertingExchangeRateImporterResultCallback());
        expectedAmountOfResults = 6;
        waitForResult(expectedAmountOfResults);
    }

}
