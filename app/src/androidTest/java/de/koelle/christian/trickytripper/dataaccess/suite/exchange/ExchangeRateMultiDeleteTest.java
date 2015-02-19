package de.koelle.christian.trickytripper.dataaccess.suite.exchange;

import static de.koelle.christian.trickytripper.dataaccess.suite.exchange.ExchangeRateTestSupport.EUR;
import static de.koelle.christian.trickytripper.dataaccess.suite.exchange.ExchangeRateTestSupport.GBP;
import static de.koelle.christian.trickytripper.dataaccess.suite.exchange.ExchangeRateTestSupport.ID_1;
import static de.koelle.christian.trickytripper.dataaccess.suite.exchange.ExchangeRateTestSupport.ID_2;
import static de.koelle.christian.trickytripper.dataaccess.suite.exchange.ExchangeRateTestSupport.ID_3;
import static de.koelle.christian.trickytripper.dataaccess.suite.exchange.ExchangeRateTestSupport.ID_4;
import static de.koelle.christian.trickytripper.dataaccess.suite.exchange.ExchangeRateTestSupport.REC_01;
import static de.koelle.christian.trickytripper.dataaccess.suite.exchange.ExchangeRateTestSupport.REC_02;
import static de.koelle.christian.trickytripper.dataaccess.suite.exchange.ExchangeRateTestSupport.REC_03;
import static de.koelle.christian.trickytripper.dataaccess.suite.exchange.ExchangeRateTestSupport.REC_04;
import static de.koelle.christian.trickytripper.dataaccess.suite.exchange.ExchangeRateTestSupport.TRY;
import static de.koelle.christian.trickytripper.dataaccess.suite.exchange.ExchangeRateTestSupport.USD;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;
import android.test.ApplicationTestCase;
import de.koelle.christian.trickytripper.TrickyTripperApp;
import de.koelle.christian.trickytripper.dataaccess.impl.DataConstants;
import de.koelle.christian.trickytripper.dataaccess.impl.DataManagerImpl;
import de.koelle.christian.trickytripper.model.ExchangeRate;

public class ExchangeRateMultiDeleteTest extends ApplicationTestCase<TrickyTripperApp> {

    BitSet occuranceFlags = new BitSet(4);

    private final Map<Long, ExchangeRate> initialRetrievalResults = new HashMap<Long, ExchangeRate>();
    private DataManagerImpl dataManager;

    public ExchangeRateMultiDeleteTest() {
        super(TrickyTripperApp.class);
    }

    @Override
    protected void setUp() {
        getContext().deleteDatabase(DataConstants.DATABASE_NAME);
        dataManager = new DataManagerImpl(getContext());
        dataManager.removeAll();

    }

    @Override
    protected void tearDown() throws Exception {
        dataManager.close();
        super.tearDown();
        getContext().deleteDatabase(DataConstants.DATABASE_NAME);
    }

    /**
     * Tests that the create() works, the persisted data can be obtained, the
     * delete works and the retrieval respects the deletion.
     */
    public void testDeleteMoreThanOne() {

        ExchangeRate input;
        List<ExchangeRate> deleteList;

        long expectedId;
        List<ExchangeRate> resultList;
        List<ExchangeRate> exchangeRateResult;

        /* ============ create ============ */

        input = REC_01;
        expectedId = ID_1;
        initialRetrievalResults.put(expectedId, dataManager.persistExchangeRate(input));

        input = REC_02;
        expectedId = ID_2;
        initialRetrievalResults.put(expectedId, dataManager.persistExchangeRate(input));

        input = REC_03;
        expectedId = ID_3;
        initialRetrievalResults.put(expectedId, dataManager.persistExchangeRate(input));

        input = REC_04;
        expectedId = ID_4;
        initialRetrievalResults.put(expectedId, dataManager.persistExchangeRate(input));

        /* ============ delete ============ */

        /* Record 1, 3 & 4 */
        deleteList = new ArrayList<ExchangeRate>();

        input = new ExchangeRate();
        input.setId(ID_1);
        deleteList.add(input);
        input = new ExchangeRate();
        input.setId(ID_3);
        deleteList.add(input);
        input = new ExchangeRate();
        input.setId(ID_4);
        deleteList.add(input);

        dataManager.deleteExchangeRates(deleteList);

        /* ---------> find post delete */
        exchangeRateResult = dataManager.findSuitableRates(EUR, USD);
        resultList = exchangeRateResult;
        Assert.assertEquals(0, resultList.size());

        exchangeRateResult = dataManager.findSuitableRates(USD, EUR);
        resultList = exchangeRateResult;
        Assert.assertEquals(0, resultList.size());

        exchangeRateResult = dataManager.findSuitableRates(EUR, TRY);
        resultList = exchangeRateResult;
        Assert.assertEquals(1, resultList.size());

        exchangeRateResult = dataManager.findSuitableRates(TRY, EUR);
        resultList = exchangeRateResult;
        Assert.assertEquals(1, resultList.size());

        exchangeRateResult = dataManager.findSuitableRates(TRY, GBP);
        resultList = exchangeRateResult;
        Assert.assertEquals(0, resultList.size());

        exchangeRateResult = dataManager.findSuitableRates(GBP, TRY);
        resultList = exchangeRateResult;
        Assert.assertEquals(0, resultList.size());

        resultList = dataManager.getAllExchangeRatesWithoutInversion();
        Assert.assertEquals(1, resultList.size());

        occuranceFlags.clear();
        for (int i = 0; i < resultList.size(); i++) {
            occuranceFlags.set((int) resultList.get(i).getId());
        }
        Assert.assertTrue("Record 1 should not be part of the resultset as it has been deleted.",
                !occuranceFlags.get((int) ID_1));

        Assert.assertTrue("Record 2 was not part of the resultset as it has not yet been deleted.",
                occuranceFlags.get((int) ID_2));
        Assert.assertTrue("Record 3 should not be part of the resultset as it has been deleted.",
                !occuranceFlags.get((int) ID_3));
        Assert.assertTrue("Record 4 should not be part of the resultset as it has been deleted.",
                !occuranceFlags.get((int) ID_4));

        dataManager.removeAll();
    }
}