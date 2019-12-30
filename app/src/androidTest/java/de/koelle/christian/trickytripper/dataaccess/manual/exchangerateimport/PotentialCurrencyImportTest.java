package de.koelle.christian.trickytripper.dataaccess.manual.exchangerateimport;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Currency;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import de.koelle.christian.common.utils.CurrencyUtil;
import de.koelle.christian.trickytripper.exchangerates.impl.ExchangeRateImporterResultContainer;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class PotentialCurrencyImportTest extends AbstractCurrencyImportTest {

    private static final int maxSleepIterations = 100;


    @Test
    public void listNonOnlineSupportedCurrencies() {
        Set<String> supported = CurrencyUtil.getSupportedCurrencies(context.getResources())
                .stream()
                .map(i -> i.getCurrencyCode())
                .collect(Collectors.toSet());
        List<Currency> retrieveables = CurrencyUtil.getAllCurrenciesWithRetrievableRate();
        for (Currency retrievable : retrieveables) {
            if (!supported.contains(retrievable.getCurrencyCode())) {
                System.out.println("Nicht downloadbar: " + retrievable.getDisplayName());
            }
        }
    }

    /**
     * Removed from automatic execution as this test runs for a very long time.
     */
    public void checkExternalExchangeRateAvailability() {

        Set<Currency> input;
        input = new LinkedHashSet<>(CurrencyUtil.getSupportedCurrencies(context.getResources()));

        int ceiling = CurrencyUtil.calcExpectedAmountOfExchangeRates(input.size());

        ResultCollectingExchangeRateImporterResultCallback callback = new ResultCollectingExchangeRateImporterResultCallback();
        getImporter().importExchangeRates(input, callback);
        waitForResult(ceiling);
        Set<String> externallySupportedCurrencyCodes = new TreeSet<>();
        Set<ExchangeRateImporterResultContainer> resultCollector = callback.getResultCollector();
        externallySupportedCurrencyCodes.addAll(resultCollector
                .stream()
                .filter(i -> i.requestWasSuccess())
                .map(i -> i.from.getCurrencyCode())
                .collect(Collectors.toSet()));
        externallySupportedCurrencyCodes.addAll(resultCollector
                .stream()
                .filter(i -> i.requestWasSuccess())
                .map(i -> i.to.getCurrencyCode())
                .collect(Collectors.toSet()));
        System.out.println(externallySupportedCurrencyCodes);
    }

    private void waitForResult(int ceiling) {
        int counter = 0;
        while (getResultCollector().size() < ceiling) {
            waitForResult(maxSleepIterations, counter);
            counter++;
        }
        if (counter >= maxSleepIterations) {
            Assert.fail("No response from exchange rate resolution service.");
        }
    }

    /**
     * Removed from automatic execution as this test runs for a very long time.
     * Takes even longer than (test)ExchangeRateAvailabilityAll().
     */

    public void exchangeRateAvailabilityAllCombinationsSeparately() {

        Set<Currency> input;

        List<Currency> allCurrenciesAlive = CurrencyUtil.getAllCurrenciesWithRetrievableRate();
        for (int i = 0; i < allCurrenciesAlive.size() - 1; i++) {
            Currency from = allCurrenciesAlive.get(i);
            for (int j = 1; j < allCurrenciesAlive.size(); j++) {
                if (i == j) {
                    continue;
                }

                input = new LinkedHashSet<>();
                Currency to = allCurrenciesAlive.get(j);

                input.add(from);
                input.add(to);

                getImporter().importExchangeRates(input, new ResultCollectingAssertingExchangeRateImporterResultCallback());

                waitForResult(1);
            }
        }

    }

}
