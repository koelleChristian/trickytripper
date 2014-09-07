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
import static de.koelle.christian.trickytripper.dataaccess.suite.exchange.ExchangeRateTestSupport.assertEquality;

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

public class ExchangeRateSaveAndLoadTest extends ApplicationTestCase<TrickyTripperApp> {

    BitSet occuranceFlags = new BitSet(4);

    private final Map<Long, ExchangeRate> initalRetrievalResults = new HashMap<Long, ExchangeRate>();
    private DataManagerImpl dataManager;

    public ExchangeRateSaveAndLoadTest() {
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
     * delete works and the retrieval resprects the deletion.
     */
    public void testCreateLoadDelete() {

        ExchangeRate input;
        List<ExchangeRate> deleteList;
        ExchangeRate output;

        long expectedId;
        List<ExchangeRate> resultList;
        List<ExchangeRate> exchangeRateResult;

        /* ============ create ============ */

        input = REC_01;
        expectedId = ID_1;

        output = dataManager.persistExchangeRate(input);
        assertEquality(input, output, expectedId, false);
        initalRetrievalResults.put(expectedId, output);

        input = REC_02;
        expectedId = ID_2;

        output = dataManager.persistExchangeRate(input);
        assertEquality(input, output, expectedId, false);
        initalRetrievalResults.put(expectedId, output);

        input = REC_03;
        expectedId = ID_3;

        output = dataManager.persistExchangeRate(input);
        assertEquality(input, output, expectedId, false);
        initalRetrievalResults.put(expectedId, output);

        input = REC_04;
        expectedId = ID_4;

        output = dataManager.persistExchangeRate(input);
        assertEquality(input, output, expectedId, false);
        initalRetrievalResults.put(expectedId, output);

        /* ============ load ============ */
        ExchangeRate record;
        long idReturned;

        resultList = dataManager.getAllExchangeRatesWithoutInversion();
        Assert.assertTrue("The exchangeRateRetrieval should not result in null.", resultList != null);
        Assert.assertEquals(4, resultList.size());

        occuranceFlags.clear();
        for (int i = 0; i < resultList.size(); i++) {
            record = resultList.get(i);
            idReturned = record.getId();
            occuranceFlags.set((int) idReturned);
            assertEquality(initalRetrievalResults.get(idReturned), record, idReturned, true);
        }
        Assert.assertTrue("Record 1 was not part of the resultset.",
                occuranceFlags.get((int) ExchangeRateTestSupport.ID_1));
        Assert.assertTrue("Record 2 was not part of the resultset.", occuranceFlags.get((int) ID_2));
        Assert.assertTrue("Record 3 was not part of the resultset.", occuranceFlags.get((int) ID_3));
        Assert.assertTrue("Record 4 was not part of the resultset.", occuranceFlags.get((int) ID_4));

        /* ============ find ============ */

        /* Record 1 */
        exchangeRateResult = dataManager.findSuitableRates(EUR, USD);
        Assert.assertTrue("findSuitableRates should not result in null.", exchangeRateResult != null);
        resultList = exchangeRateResult;
        Assert.assertEquals(1, resultList.size());
        record = resultList.get(0);
        expectedId = ID_1;
        assertEquality(initalRetrievalResults.get(expectedId), record, expectedId, true);

        exchangeRateResult = dataManager.findSuitableRates(USD, EUR);
        Assert.assertTrue("findSuitableRates should not result in null.", exchangeRateResult != null);
        resultList = exchangeRateResult;
        Assert.assertEquals(1, resultList.size());
        record = resultList.get(0);
        expectedId = ID_1;
        assertEquality(initalRetrievalResults.get(expectedId), record, expectedId, true);

        /* Record 2 */
        exchangeRateResult = dataManager.findSuitableRates(EUR, TRY);
        Assert.assertTrue("findSuitableRates should not result in null.", exchangeRateResult != null);
        resultList = exchangeRateResult;
        Assert.assertEquals(1, resultList.size());
        record = resultList.get(0);
        expectedId = ID_2;
        assertEquality(initalRetrievalResults.get(expectedId), record, expectedId, true);

        exchangeRateResult = dataManager.findSuitableRates(TRY, EUR);
        Assert.assertTrue("findSuitableRates should not result in null.", exchangeRateResult != null);
        resultList = exchangeRateResult;
        Assert.assertEquals(1, resultList.size());
        record = resultList.get(0);
        expectedId = ID_2;
        assertEquality(initalRetrievalResults.get(expectedId), record, expectedId, true);

        /* Record 3 & 4 */
        exchangeRateResult = dataManager.findSuitableRates(TRY, GBP);
        Assert.assertTrue("findSuitableRates should not result in null.", exchangeRateResult != null);
        resultList = exchangeRateResult;
        occuranceFlags.clear();
        Assert.assertEquals(2, resultList.size());
        for (int i = 0; i < 2; i++) {
            record = resultList.get(i);
            idReturned = record.getId();
            occuranceFlags.set((int) idReturned);
            assertEquality(initalRetrievalResults.get(idReturned), record, idReturned, true);

        }
        Assert.assertTrue("Record 1 should not be part of the resultset.", !occuranceFlags.get((int) ID_1));
        Assert.assertTrue("Record 2 should not be part of the resultset.", !occuranceFlags.get((int) ID_2));
        Assert.assertTrue("Record 3 was not part of the resultset.", occuranceFlags.get((int) ID_3));
        Assert.assertTrue("Record 4 was not part of the resultset.", occuranceFlags.get((int) ID_4));

        exchangeRateResult = dataManager.findSuitableRates(GBP, TRY);
        Assert.assertTrue("findSuitableRates should not result in null.", exchangeRateResult != null);
        resultList = exchangeRateResult;
        occuranceFlags.clear();
        Assert.assertEquals(2, resultList.size());
        for (int i = 0; i < 2; i++) {
            record = resultList.get(i);
            idReturned = record.getId();
            occuranceFlags.set((int) idReturned);
            assertEquality(initalRetrievalResults.get(idReturned), record, idReturned, true);
        }
        Assert.assertTrue("Record 1 should not be part of the resultset.", !occuranceFlags.get((int) ID_1));
        Assert.assertTrue("Record 2 should not be part of the resultset.", !occuranceFlags.get((int) ID_2));
        Assert.assertTrue("Record 3 was not part of the resultset.", occuranceFlags.get((int) ID_3));
        Assert.assertTrue("Record 4 was not part of the resultset.", occuranceFlags.get((int) ID_4));

        /* None */
        exchangeRateResult = dataManager.findSuitableRates(EUR, GBP);
        Assert.assertTrue("findSuitableRates should not result in null.", exchangeRateResult != null);
        resultList = exchangeRateResult;
        Assert.assertEquals(0, resultList.size());
        exchangeRateResult = dataManager.findSuitableRates(GBP, EUR);
        Assert.assertTrue("findSuitableRates should not result in null.", exchangeRateResult != null);
        resultList = exchangeRateResult;
        Assert.assertEquals(0, resultList.size());

        /* ============ delete ============ */

        /* Record 1 */
        input = new ExchangeRate();
        input.setId(ID_1);
        deleteList = new ArrayList<ExchangeRate>();
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

        resultList = dataManager.getAllExchangeRatesWithoutInversion();
        Assert.assertEquals(3, resultList.size());

        occuranceFlags.clear();
        for (int i = 0; i < resultList.size(); i++) {
            occuranceFlags.set((int) resultList.get(i).getId());
        }
        Assert.assertTrue("Record 1 should not be part of the resultset as it has been deleted.",
                !occuranceFlags.get((int) ID_1));

        Assert.assertTrue("Record 2 was not part of the resultset as it has not yet been deleted.",
                occuranceFlags.get((int) ID_2));
        Assert.assertTrue("Record 3 was not part of the resultset as it has not yet been deleted.",
                occuranceFlags.get((int) ID_3));
        Assert.assertTrue("Record 4 was not part of the resultset as it has not yet been deleted.",
                occuranceFlags.get((int) ID_4));

        /* Record 2 */
        input = new ExchangeRate();
        input.setId(ID_2);
        deleteList = new ArrayList<ExchangeRate>();
        deleteList.add(input);

        dataManager.deleteExchangeRates(deleteList);

        /* ---------> find post delete */
        exchangeRateResult = dataManager.findSuitableRates(EUR, TRY);
        resultList = exchangeRateResult;
        Assert.assertEquals(0, resultList.size());

        exchangeRateResult = dataManager.findSuitableRates(TRY, EUR);
        resultList = exchangeRateResult;
        Assert.assertEquals(0, resultList.size());

        resultList = dataManager.getAllExchangeRatesWithoutInversion();
        Assert.assertEquals(2, resultList.size());

        occuranceFlags.clear();
        for (int i = 0; i < resultList.size(); i++) {
            occuranceFlags.set((int) resultList.get(i).getId());
        }
        Assert.assertTrue("Record 1 should not be part of the resultset as it has been deleted.",
                !occuranceFlags.get((int) ID_1));
        Assert.assertTrue("Record 1 should not be part of the resultset as it has been deleted.",
                !occuranceFlags.get((int) ID_2));

        Assert.assertTrue("Record 3 was not part of the resultset as it has not yet been deleted.",
                occuranceFlags.get((int) ID_3));
        Assert.assertTrue("Record 4 was not part of the resultset as it has not yet been deleted.",
                occuranceFlags.get((int) ID_4));

        /* Record 3 */
        input = new ExchangeRate();
        input.setId(ID_3);
        deleteList = new ArrayList<ExchangeRate>();
        deleteList.add(input);

        dataManager.deleteExchangeRates(deleteList);

        /* ---------> find post delete */
        exchangeRateResult = dataManager.findSuitableRates(TRY, GBP);
        resultList = exchangeRateResult;
        Assert.assertEquals(1, resultList.size());
        idReturned = resultList.get(0).getId();
        Assert.assertTrue("findSuitableRates should  deliver record 4 as record three has been it has been deleted.",
                idReturned == ID_4);

        exchangeRateResult = dataManager.findSuitableRates(GBP, TRY);
        resultList = exchangeRateResult;
        Assert.assertEquals(1, resultList.size());
        idReturned = resultList.get(0).getId();
        Assert.assertTrue("findSuitableRates should  deliver record 4 as record three has been it has been deleted.",
                idReturned == ID_4);

        resultList = dataManager.getAllExchangeRatesWithoutInversion();
        Assert.assertEquals(1, resultList.size());
        idReturned = resultList.get(0).getId();
        Assert.assertTrue("findSuitableRates should  deliver record 4 as record three has been it has been deleted.",
                idReturned == ID_4);

        /* ---------> find post delete */
        input = new ExchangeRate();
        input.setId(ID_4);
        deleteList = new ArrayList<ExchangeRate>();
        deleteList.add(input);

        dataManager.deleteExchangeRates(deleteList);

        /* find post delete */
        exchangeRateResult = dataManager.findSuitableRates(TRY, GBP);
        resultList = exchangeRateResult;
        Assert.assertEquals(0, resultList.size());

        exchangeRateResult = dataManager.findSuitableRates(GBP, TRY);
        resultList = exchangeRateResult;
        Assert.assertEquals(0, resultList.size());

        resultList = dataManager.getAllExchangeRatesWithoutInversion();
        Assert.assertEquals(0, resultList.size());

        dataManager.removeAll();

    }

}