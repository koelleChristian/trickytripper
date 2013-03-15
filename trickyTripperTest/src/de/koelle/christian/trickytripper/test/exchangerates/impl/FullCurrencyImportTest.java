package de.koelle.christian.trickytripper.test.exchangerates.impl;

import java.util.Currency;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import de.koelle.christian.common.utils.CurrencyUtil;

public class FullCurrencyImportTest extends AbstractCurrencyImportTest {

    public FullCurrencyImportTest() {
        super();
    }

    /**
     * Removed from automatic execution as this test runs for a very long time.
     */
    public void testExchangeRateAvailabilityAll() {

        Set<Currency> input;
        input = new LinkedHashSet<Currency>(CurrencyUtil.getAllCurrenciesAlive());

        int ceiling = CurrencyUtil.calcExpectedAmountOfExchangeRates(input.size());

        getImporter().importExchangeRates(input, new ResultCollectingExchangeRateImporterResultCallback());

        while (getResultCollector().size() < ceiling) {
            waitForResult();
        }

    }

    /**
     * Removed from automatic execution as this test runs for a very long time.
     * Takes even longer than (test)ExchangeRateAvailabilityAll().
     */

    public void exchangeRateAvailabilityAllCombinationsSeparately() {

        Set<Currency> input;

        List<Currency> allCurrenciesAlive = CurrencyUtil.getAllCurrenciesAlive();
        for (int i = 0; i < allCurrenciesAlive.size() - 1; i++) {
            Currency from = allCurrenciesAlive.get(i);
            for (int j = 1; j < allCurrenciesAlive.size(); j++) {
                if (i == j) {
                    continue;
                }

                input = new LinkedHashSet<Currency>();
                Currency to = allCurrenciesAlive.get(j);

                input.add(from);
                input.add(to);

                getImporter().importExchangeRates(input, new ResultCollectingExchangeRateImporterResultCallback());

                while (getResultCollector().size() < 1) {
                    waitForResult();
                }
            }
        }

    }

}
