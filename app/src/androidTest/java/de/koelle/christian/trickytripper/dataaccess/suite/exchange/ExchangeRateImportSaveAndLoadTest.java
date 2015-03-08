package de.koelle.christian.trickytripper.dataaccess.suite.exchange;

import android.test.ApplicationTestCase;

import junit.framework.Assert;

import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.koelle.christian.trickytripper.TrickyTripperApp;
import de.koelle.christian.trickytripper.dataaccess.impl.DataConstants;
import de.koelle.christian.trickytripper.dataaccess.impl.DataManagerImpl;
import de.koelle.christian.trickytripper.model.ExchangeRate;

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

public class ExchangeRateImportSaveAndLoadTest extends ApplicationTestCase<TrickyTripperApp> {

    BitSet occuranceFlags = new BitSet(4);

    private final Map<Long, ExchangeRate> initialRetrievalResults = new HashMap<Long, ExchangeRate>();

    private DataManagerImpl dataManager;

    public ExchangeRateImportSaveAndLoadTest() {
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
    public void testImportWithoutReplace() {

        ExchangeRate input;

        long expectedId;
        List<ExchangeRate> resultList;
        List<ExchangeRate> exchangeRateResult;
        ExchangeRate clone;
        boolean replace = false;

        /* ============ create ============ */

        input = REC_01;
        dataManager.persistImportedExchangeRate(input, replace);
        clone = REC_01.doClone();
        clone.setId(ID_1);
        initialRetrievalResults.put(ID_1, clone);

        input = REC_02;
        dataManager.persistImportedExchangeRate(input, replace);
        clone = REC_02.doClone();
        clone.setId(ID_2);
        initialRetrievalResults.put(ID_2, clone);

        input = REC_03;
        dataManager.persistImportedExchangeRate(input, replace);
        clone = REC_03.doClone();
        clone.setId(ID_3);
        initialRetrievalResults.put(ID_3, clone);

        input = REC_04;
        dataManager.persistImportedExchangeRate(input, replace);
        clone = REC_04.doClone();
        clone.setId(ID_4);
        initialRetrievalResults.put(ID_4, clone);

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
            initialRetrievalResults.put(idReturned, record);
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
        assertEquality(initialRetrievalResults.get(expectedId), record, expectedId, false);

        exchangeRateResult = dataManager.findSuitableRates(USD, EUR);
        Assert.assertTrue("findSuitableRates should not result in null.", exchangeRateResult != null);
        resultList = exchangeRateResult;
        Assert.assertEquals(1, resultList.size());
        record = resultList.get(0);
        expectedId = ID_1;
        assertEquality(initialRetrievalResults.get(expectedId), record, expectedId, false);

        /* Record 2 */
        exchangeRateResult = dataManager.findSuitableRates(EUR, TRY);
        Assert.assertTrue("findSuitableRates should not result in null.", exchangeRateResult != null);
        resultList = exchangeRateResult;
        Assert.assertEquals(1, resultList.size());
        record = resultList.get(0);
        expectedId = ID_2;
        assertEquality(initialRetrievalResults.get(expectedId), record, expectedId, false);

        exchangeRateResult = dataManager.findSuitableRates(TRY, EUR);
        Assert.assertTrue("findSuitableRates should not result in null.", exchangeRateResult != null);
        resultList = exchangeRateResult;
        Assert.assertEquals(1, resultList.size());
        record = resultList.get(0);
        expectedId = ID_2;
        assertEquality(initialRetrievalResults.get(expectedId), record, expectedId, false);

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
            assertEquality(initialRetrievalResults.get(idReturned), record, idReturned, false);

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
            assertEquality(initialRetrievalResults.get(idReturned), record, idReturned, false);
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

        /*
         * Import an exchange rate that has not any difference compared to an
         * existing record. --> Update
         */

        boolean recordThere;

        input = REC_01.doClone();
        dataManager.persistImportedExchangeRate(input, replace);
        resultList = dataManager.getAllExchangeRatesWithoutInversion();
        Assert.assertEquals(4, resultList.size());
        recordThere = false;
        for (int i = 0; i < resultList.size(); i++) {
            record = resultList.get(i);
            if (record.getId() == ID_1) {
                ExchangeRate initialRetrievalResult = initialRetrievalResults.get(ID_1);
                assertEquality(initialRetrievalResult, record, ID_1, false);
                Assert.assertTrue(
                        "A equal record was found and was expected to be updated. Failure as updateTime was not bigger than before.",
                        record.getUpdateDate().getTime() > initialRetrievalResult.getUpdateDate().getTime());

                Assert.assertTrue("A equals record was found. The creation date was expected to remain the same.",
                        record
                                .getCreationDate().getTime() == initialRetrievalResult.getCreationDate().getTime());
                recordThere = true;
                break;
            }
        }
        if (!recordThere) {
            Assert.fail();
        }

        /*
         * Import an exchange rate that has a difference compared to an existing
         * record. --> Create
         */

        input = REC_01.doClone();
        input.setExchangeRate(input.getExchangeRate() + 0.5321);
        dataManager.persistImportedExchangeRate(input, replace);
        resultList = dataManager.getAllExchangeRatesWithoutInversion();
        Assert.assertEquals(5, resultList.size());
        recordThere = false;
        for (int i = 0; i < resultList.size(); i++) {
            record = resultList.get(i);
            if (record.getId() == 5) {
                Assert.assertEquals(input.getExchangeRate(), record.getExchangeRate());
                Assert.assertEquals(input.getCurrencyFrom(), record.getCurrencyFrom());
                Assert.assertEquals(input.getCurrencyTo(), record.getCurrencyTo());
                recordThere = true;
                break;
            }
        }
        if (!recordThere) {
            Assert.fail();
        }
    }

    /**
     * Tests that the create() works, the persisted data can be obtained, the
     * delete works and the retrieval respects the deletion.
     */
    public void testImportWithReplace() {

        ExchangeRate input;

        long expectedId;
        List<ExchangeRate> resultList;
        List<ExchangeRate> exchangeRateResult;
        ExchangeRate clone;
        boolean replace = true;

        /* ============ create ============ */

        input = REC_01;
        dataManager.persistImportedExchangeRate(input, replace);
        clone = REC_01.doClone();
        clone.setId(ID_1);
        initialRetrievalResults.put(ID_1, clone);

        input = REC_02;
        dataManager.persistImportedExchangeRate(input, replace);
        clone = REC_02.doClone();
        clone.setId(ID_2);
        initialRetrievalResults.put(ID_2, clone);

        input = REC_03;
        dataManager.persistImportedExchangeRate(input, replace);
        clone = REC_03.doClone();
        clone.setId(ID_3);
        initialRetrievalResults.put(ID_3, clone);

        input = REC_04;
        /* We intentionally add this. */
        dataManager.persistImportedExchangeRate(input, false);
        clone = REC_04.doClone();
        clone.setId(ID_4);
        initialRetrievalResults.put(ID_4, clone);

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
            initialRetrievalResults.put(idReturned, record);
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
        assertEquality(initialRetrievalResults.get(expectedId), record, expectedId, false);

        exchangeRateResult = dataManager.findSuitableRates(USD, EUR);
        Assert.assertTrue("findSuitableRates should not result in null.", exchangeRateResult != null);
        resultList = exchangeRateResult;
        Assert.assertEquals(1, resultList.size());
        record = resultList.get(0);
        expectedId = ID_1;
        assertEquality(initialRetrievalResults.get(expectedId), record, expectedId, false);

        /* Record 2 */
        exchangeRateResult = dataManager.findSuitableRates(EUR, TRY);
        Assert.assertTrue("findSuitableRates should not result in null.", exchangeRateResult != null);
        resultList = exchangeRateResult;
        Assert.assertEquals(1, resultList.size());
        record = resultList.get(0);
        expectedId = ID_2;
        assertEquality(initialRetrievalResults.get(expectedId), record, expectedId, false);

        exchangeRateResult = dataManager.findSuitableRates(TRY, EUR);
        Assert.assertTrue("findSuitableRates should not result in null.", exchangeRateResult != null);
        resultList = exchangeRateResult;
        Assert.assertEquals(1, resultList.size());
        record = resultList.get(0);
        expectedId = ID_2;
        assertEquality(initialRetrievalResults.get(expectedId), record, expectedId, false);

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
            assertEquality(initialRetrievalResults.get(idReturned), record, idReturned, false);

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
            assertEquality(initialRetrievalResults.get(idReturned), record, idReturned, false);
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

        /*
         * Import an exchange rate that has not any difference compared to an
         * existing record. --> Update
         */

        boolean recordThere;

        input = REC_03.doClone();
        dataManager.persistImportedExchangeRate(input, replace);
        resultList = dataManager.getAllExchangeRatesWithoutInversion();
        /* Expected two have been replaced. */
        Assert.assertEquals(3, resultList.size());
        ExchangeRate updatedRecord03 = null;
        recordThere = false;
        for (int i = 0; i < resultList.size(); i++) {
            record = resultList.get(i);
            if (record.getId() == ID_3) {
                updatedRecord03 = record;
                Assert.assertEquals(input.getExchangeRate(), record.getExchangeRate());
                Assert.assertEquals(input.getCurrencyFrom(), record.getCurrencyFrom());
                Assert.assertEquals(input.getCurrencyTo(), record.getCurrencyTo());
                ExchangeRate initialRetrievalResult = initialRetrievalResults.get(ID_3);
                Assert.assertTrue(
                        "A equal record was found and was expected to be updated. Failure as updateTime was not bigger than before.",
                        record.getUpdateDate().getTime() > initialRetrievalResult.getUpdateDate().getTime());

                Assert.assertTrue("A equals record was found. The creation date was expected to remain the same.",
                        record
                                .getCreationDate().getTime() == initialRetrievalResult.getCreationDate().getTime());
                recordThere = true;
                break;
            }
        }
        if (!recordThere) {
            Assert.fail();
        }

        input = REC_04;
        /* We intentionally add this. */
        dataManager.persistImportedExchangeRate(input, false);
        resultList = dataManager.getAllExchangeRatesWithoutInversion();
        /* Expected two have been replaced. */
        Assert.assertEquals(4, resultList.size());

        /*
         * Import an exchange rate that has a difference compared to an existing
         * record. --> Update
         */

        input = REC_03.doClone();
        input.setExchangeRate(input.getExchangeRate() + 0.5321);
        dataManager.persistImportedExchangeRate(input, replace);
        resultList = dataManager.getAllExchangeRatesWithoutInversion();
        /* Expected two have been replaced. */
        Assert.assertEquals(3, resultList.size());
        recordThere = false;
        for (int i = 0; i < resultList.size(); i++) {
            record = resultList.get(i);
            if (record.getId() == ID_3) {
                Assert.assertEquals(input.getExchangeRate(), record.getExchangeRate());
                Assert.assertEquals(input.getCurrencyFrom(), record.getCurrencyFrom());
                Assert.assertEquals(input.getCurrencyTo(), record.getCurrencyTo());
                Assert.assertTrue(
                        "A equal record was found and was expected to be updated. Failure as updateTime was not bigger than before.",
                        record.getUpdateDate().getTime() > updatedRecord03.getUpdateDate().getTime());

                Assert.assertTrue("A equals record was found. The creation date was expected to remain the same.",
                        record
                                .getCreationDate().getTime() == updatedRecord03.getCreationDate().getTime());
                recordThere = true;
                break;
            }
        }
        if (!recordThere) {
            Assert.fail();
        }
    }
}