package de.koelle.christian.trickytripper.dataaccess.suite.exchange;

import static de.koelle.christian.trickytripper.dataaccess.suite.exchange.ExchangeRateTestSupport.EUR;
import static de.koelle.christian.trickytripper.dataaccess.suite.exchange.ExchangeRateTestSupport.ID_1;
import static de.koelle.christian.trickytripper.dataaccess.suite.exchange.ExchangeRateTestSupport.ID_2;
import static de.koelle.christian.trickytripper.dataaccess.suite.exchange.ExchangeRateTestSupport.ID_3;
import static de.koelle.christian.trickytripper.dataaccess.suite.exchange.ExchangeRateTestSupport.ID_4;
import static de.koelle.christian.trickytripper.dataaccess.suite.exchange.ExchangeRateTestSupport.REC_EUR_USD_01;
import static de.koelle.christian.trickytripper.dataaccess.suite.exchange.ExchangeRateTestSupport.REC_EUR_USD_02;
import static de.koelle.christian.trickytripper.dataaccess.suite.exchange.ExchangeRateTestSupport.REC_EUR_USD_03;
import static de.koelle.christian.trickytripper.dataaccess.suite.exchange.ExchangeRateTestSupport.REC_EUR_USD_04;
import static de.koelle.christian.trickytripper.dataaccess.suite.exchange.ExchangeRateTestSupport.USD;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;
import android.test.ApplicationTestCase;
import de.koelle.christian.trickytripper.TrickyTripperApp;
import de.koelle.christian.trickytripper.apputils.PrefAccessor;
import de.koelle.christian.trickytripper.controller.impl.ExchangeRateControllerImpl;
import de.koelle.christian.trickytripper.dataaccess.impl.DataConstants;
import de.koelle.christian.trickytripper.dataaccess.impl.DataManagerImpl;
import de.koelle.christian.trickytripper.decoupling.PrefsResolver;
import de.koelle.christian.trickytripper.model.ExchangeRate;

public class ExchangeRatePrefsSaveLoadTest extends ApplicationTestCase<TrickyTripperApp> {

    private final Map<Long, ExchangeRate> initialRetrievalResults = new HashMap<Long, ExchangeRate>();
    private DataManagerImpl dataManager;
    private ExchangeRateControllerImpl controller;

    public ExchangeRatePrefsSaveLoadTest() {
        super(TrickyTripperApp.class);
    }

    @Override
    protected void setUp() {
        getContext().deleteDatabase(DataConstants.DATABASE_NAME);
        dataManager = new DataManagerImpl(getContext());
        dataManager.removeAll();
        PrefsResolver prefsResolver = new PrefAccessor(getContext());

        controller = new ExchangeRateControllerImpl(dataManager, prefsResolver);

    }

    @Override
    protected void tearDown() throws Exception {
        dataManager.close();
        super.tearDown();
        getContext().deleteDatabase(DataConstants.DATABASE_NAME);
    }

    /**
     * Tests that persistExchangeRateUsedLast() works. This tests expects that
     * the general exchange rate persistence works and therefore only tests the
     * order of the retrieved results and its existence.
     */
    public void testCreateLoadDelete() {

        ExchangeRate output;
        List<ExchangeRate> exchangeRateResult;

        /* ============ create ============ */

        output = dataManager.persistExchangeRate(REC_EUR_USD_01);
        initialRetrievalResults.put(output.getId(), output);
        output = dataManager.persistExchangeRate(REC_EUR_USD_02);
        initialRetrievalResults.put(output.getId(), output);
        output = dataManager.persistExchangeRate(REC_EUR_USD_03);
        initialRetrievalResults.put(output.getId(), output);
        output = dataManager.persistExchangeRate(REC_EUR_USD_04);
        initialRetrievalResults.put(output.getId(), output);

        /* ============ load ============ */
        exchangeRateResult = controller.findSuitableRates(EUR, USD);
        Assert.assertTrue("findSuitableRates should not result in null.", exchangeRateResult != null);
        assertOrder(exchangeRateResult, new Long[] { ID_4, ID_3, ID_2, ID_1 });

        controller.persistExchangeRateUsedLast(initialRetrievalResults.get(ID_1));

        exchangeRateResult = controller.findSuitableRates(EUR, USD);
        Assert.assertTrue("findSuitableRates should not result in null.", exchangeRateResult != null);
        assertOrder(exchangeRateResult, new Long[] { ID_1, ID_4, ID_3, ID_2 });

        controller.persistExchangeRateUsedLast(initialRetrievalResults.get(ID_3));

        exchangeRateResult = controller.findSuitableRates(EUR, USD);
        Assert.assertTrue("findSuitableRates should not result in null.", exchangeRateResult != null);
        assertOrder(exchangeRateResult, new Long[] { ID_3, ID_1, ID_4, ID_2 });

        /* Update */
        controller.persistExchangeRateUsedLast(initialRetrievalResults.get(ID_1));

        exchangeRateResult = controller.findSuitableRates(EUR, USD);
        Assert.assertTrue("findSuitableRates should not result in null.", exchangeRateResult != null);
        assertOrder(exchangeRateResult, new Long[] { ID_1, ID_3, ID_4, ID_2 });

        /*
         * This one would be inverted in real life, but the initial retrieval
         * results do not hold potential inversions.
         */
        controller.persistExchangeRateUsedLast(initialRetrievalResults.get(ID_4).cloneToInversion());

        exchangeRateResult = controller.findSuitableRates(EUR, USD);
        Assert.assertTrue("findSuitableRates should not result in null.", exchangeRateResult != null);
        assertOrder(exchangeRateResult, new Long[] { ID_4, ID_1, ID_3, ID_2 });

        controller.persistExchangeRateUsedLast(initialRetrievalResults.get(ID_2));

        exchangeRateResult = controller.findSuitableRates(EUR, USD);
        Assert.assertTrue("findSuitableRates should not result in null.", exchangeRateResult != null);
        assertOrder(exchangeRateResult, new Long[] { ID_2, ID_4, ID_1, ID_3 });

        controller.persistExchangeRateUsedLast(initialRetrievalResults.get(ID_2));

        exchangeRateResult = controller.findSuitableRates(EUR, USD);
        Assert.assertTrue("findSuitableRates should not result in null.", exchangeRateResult != null);
        assertOrder(exchangeRateResult, new Long[] { ID_2, ID_4, ID_1, ID_3 });

        /* ============== Delete ================= */

        controller.deleteExchangeRates(Arrays.asList(initialRetrievalResults.get(ID_2) ));

        exchangeRateResult = controller.findSuitableRates(EUR, USD);
        Assert.assertTrue("findSuitableRates should not result in null.", exchangeRateResult != null);
        Assert.assertEquals(3, exchangeRateResult.size());
        assertOrder(exchangeRateResult, new Long[] { ID_4, ID_1, ID_3 });

        controller.deleteExchangeRates(Arrays.asList(initialRetrievalResults.get(ID_1) ));

        exchangeRateResult = controller.findSuitableRates(EUR, USD);
        Assert.assertTrue("findSuitableRates should not result in null.", exchangeRateResult != null);
        Assert.assertEquals(2, exchangeRateResult.size());
        assertOrder(exchangeRateResult, new Long[] { ID_4, ID_3 });

        controller.deleteExchangeRates(Arrays.asList( initialRetrievalResults.get(ID_4) ));

        exchangeRateResult = controller.findSuitableRates(EUR, USD);
        Assert.assertTrue("findSuitableRates should not result in null.", exchangeRateResult != null);
        Assert.assertEquals(1, exchangeRateResult.size());
        assertOrder(exchangeRateResult, new Long[] { ID_3 });

        controller.deleteExchangeRates(Arrays.asList(initialRetrievalResults.get(ID_3)));

        exchangeRateResult = controller.findSuitableRates(EUR, USD);
        Assert.assertTrue("findSuitableRates should not result in null.", exchangeRateResult != null);
        Assert.assertEquals(0, exchangeRateResult.size());
    }

    private void assertOrder(List<ExchangeRate> exchangeRateResult, Long[] idsInExpectedOrder) {
        Assert.assertEquals(idsInExpectedOrder.length, exchangeRateResult.size());
        for (int i = 0; i < exchangeRateResult.size(); i++) {
            Assert.assertEquals(idsInExpectedOrder[i], (Long) exchangeRateResult.get(i).getId());
        }
    }
}
