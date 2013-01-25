package de.koelle.christian.trickytripper.test.dataaccess.suite;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;
import android.test.ApplicationTestCase;
import de.koelle.christian.trickytripper.TrickyTripperApp;
import de.koelle.christian.trickytripper.dataaccess.impl.DataManagerImpl;
import de.koelle.christian.trickytripper.model.ExchangeRate;
import de.koelle.christian.trickytripper.model.ExchangeRateResult;
import de.koelle.christian.trickytripper.model.ImportOrigin;

public class ExchangeRateSaveAndLoadTest extends ApplicationTestCase<TrickyTripperApp> {

    private static final Currency EUR = Currency.getInstance("EUR");
    private static final Currency USD = Currency.getInstance("USD");
    private static final Currency TRY = Currency.getInstance("TRY");
    private static final Currency GBP = Currency.getInstance("GBP");

    BitSet occuranceFlags = new BitSet(4);

    public static final long ID_A = 1;
    public static final long ID_B = 2;
    public static final long ID_C = 3;
    public static final long ID_D = 4;

    private final Map<Long, ExchangeRate> initalRetrievalResults = new HashMap<Long, ExchangeRate>();

    public ExchangeRateSaveAndLoadTest() {
        super(TrickyTripperApp.class);
    }

    /**
     * Tests the importer persistence, escpecially that the replace or add flag
     * works properly.
     */
    public void testExchangeRateUsedLast() {
        DataManagerImpl dataManager = new DataManagerImpl(getContext());

        dataManager.removeAll();

        ExchangeRate input;

        long expectedId;
        List<ExchangeRate> resultList;
        ExchangeRateResult exchangeRateResult;

        /* ============ create ============ */

        createAndcreateAndAssert4BaseRecords(dataManager);

        /* ============ load ============ */
        // TODO(ckoelle) Testen der Erzeugung durch den Importer, ersetzen,
        // nicht
        // ersetzen.
    }

    /**
     * Tests the importer persistence, escpecially that the replace or add flag
     * works properly.
     */
    public void testImportPersistence() {
        DataManagerImpl dataManager = new DataManagerImpl(getContext());

        dataManager.removeAll();

        ExchangeRate input;

        long expectedId;
        List<ExchangeRate> resultList;
        ExchangeRateResult exchangeRateResult;

        /* ============ create ============ */

        createAndcreateAndAssert4BaseRecords(dataManager);

        /* ============ load ============ */
        // TODO(ckoelle) Testen der Erzeugung durch den Importer, ersetzen,
        // nicht
        // ersetzen.
    }

    /**
     * Tests that a manually created/amended record can not be stored, if the
     * name already exists.
     */
    public void testEnsureNameUniqueOnManualPersit() {
        DataManagerImpl dataManager = new DataManagerImpl(getContext());

        dataManager.removeAll();

        ExchangeRate input;

        long expectedId;
        List<ExchangeRate> resultList;
        ExchangeRateResult exchangeRateResult;

        /* ============ create ============ */

        createAndcreateAndAssert4BaseRecords(dataManager);

        /* ============ load ============ */
        // TODO(ckoelle) Testen, dass ein manueller Datensatz nicht
        // angelegt/geupt werden
        // kann, wenn der Name bereits existiert.
    }

    /**
     * Tests that the update works and that the amendment is represented by a
     * subsequent retrieval.
     */
    public void testCreateAndUpdate() {
        DataManagerImpl dataManager = new DataManagerImpl(getContext());

        dataManager.removeAll();

        ExchangeRate input;

        long expectedId;
        List<ExchangeRate> resultList;
        ExchangeRateResult exchangeRateResult;

        /* ============ create ============ */

        createAndcreateAndAssert4BaseRecords(dataManager);

        /* ============ load ============ */
        // TOOD(ckoelle) Testen, dass der Update funktioniert und die find
        // Methoden
        // die Aktualisierung berücksichtigen.
    }

    /**
     * Tests that the create() works, the persisted data can be obtained, the
     * delete works and the retrieval resprects the deletion.
     */
    public void testCreateLoadDelete() {
        DataManagerImpl dataManager = new DataManagerImpl(getContext());

        dataManager.removeAll();

        ExchangeRate input;
        List<ExchangeRate> deleteList;

        long expectedId;
        List<ExchangeRate> resultList;
        ExchangeRateResult exchangeRateResult;

        /* ============ create ============ */

        createAndcreateAndAssert4BaseRecords(dataManager);

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
        Assert.assertTrue("Record 1 was not part of the resultset.", occuranceFlags.get((int) ID_A));
        Assert.assertTrue("Record 2 was not part of the resultset.", occuranceFlags.get((int) ID_B));
        Assert.assertTrue("Record 3 was not part of the resultset.", occuranceFlags.get((int) ID_C));
        Assert.assertTrue("Record 4 was not part of the resultset.", occuranceFlags.get((int) ID_D));

        /* ============ find ============ */

        /* Record 1 */
        exchangeRateResult = dataManager.findSuitableRates(EUR, USD);
        Assert.assertTrue("findSuitableRates should not result in null.", exchangeRateResult != null);
        resultList = exchangeRateResult.getMatchingExchangeRates();
        Assert.assertEquals(1, resultList.size());
        record = resultList.get(0);
        expectedId = ID_A;
        assertEquality(initalRetrievalResults.get(expectedId), record, expectedId, true);

        exchangeRateResult = dataManager.findSuitableRates(USD, EUR);
        Assert.assertTrue("findSuitableRates should not result in null.", exchangeRateResult != null);
        resultList = exchangeRateResult.getMatchingExchangeRates();
        Assert.assertEquals(1, resultList.size());
        record = resultList.get(0);
        expectedId = ID_A;
        assertEquality(initalRetrievalResults.get(expectedId), record, expectedId, true);

        /* Record 2 */
        exchangeRateResult = dataManager.findSuitableRates(EUR, TRY);
        Assert.assertTrue("findSuitableRates should not result in null.", exchangeRateResult != null);
        resultList = exchangeRateResult.getMatchingExchangeRates();
        Assert.assertEquals(1, resultList.size());
        record = resultList.get(0);
        expectedId = ID_B;
        assertEquality(initalRetrievalResults.get(expectedId), record, expectedId, true);

        exchangeRateResult = dataManager.findSuitableRates(TRY, EUR);
        Assert.assertTrue("findSuitableRates should not result in null.", exchangeRateResult != null);
        resultList = exchangeRateResult.getMatchingExchangeRates();
        Assert.assertEquals(1, resultList.size());
        record = resultList.get(0);
        expectedId = ID_B;
        assertEquality(initalRetrievalResults.get(expectedId), record, expectedId, true);

        /* Record 3 & 4 */
        exchangeRateResult = dataManager.findSuitableRates(TRY, GBP);
        Assert.assertTrue("findSuitableRates should not result in null.", exchangeRateResult != null);
        resultList = exchangeRateResult.getMatchingExchangeRates();
        occuranceFlags.clear();
        Assert.assertEquals(2, resultList.size());
        for (int i = 0; i < 2; i++) {
            record = resultList.get(i);
            idReturned = record.getId();
            occuranceFlags.set((int) idReturned);
            assertEquality(initalRetrievalResults.get(idReturned), record, idReturned, true);

        }
        Assert.assertTrue("Record 1 should not be part of the resultset.", !occuranceFlags.get((int) ID_A));
        Assert.assertTrue("Record 2 should not be part of the resultset.", !occuranceFlags.get((int) ID_B));
        Assert.assertTrue("Record 3 was not part of the resultset.", occuranceFlags.get((int) ID_C));
        Assert.assertTrue("Record 4 was not part of the resultset.", occuranceFlags.get((int) ID_D));

        exchangeRateResult = dataManager.findSuitableRates(GBP, TRY);
        Assert.assertTrue("findSuitableRates should not result in null.", exchangeRateResult != null);
        resultList = exchangeRateResult.getMatchingExchangeRates();
        occuranceFlags.clear();
        Assert.assertEquals(2, resultList.size());
        for (int i = 0; i < 2; i++) {
            record = resultList.get(i);
            idReturned = record.getId();
            occuranceFlags.set((int) idReturned);
            assertEquality(initalRetrievalResults.get(idReturned), record, idReturned, true);
        }
        Assert.assertTrue("Record 1 should not be part of the resultset.", !occuranceFlags.get((int) ID_A));
        Assert.assertTrue("Record 2 should not be part of the resultset.", !occuranceFlags.get((int) ID_B));
        Assert.assertTrue("Record 3 was not part of the resultset.", occuranceFlags.get((int) ID_C));
        Assert.assertTrue("Record 4 was not part of the resultset.", occuranceFlags.get((int) ID_D));

        /* None */
        exchangeRateResult = dataManager.findSuitableRates(EUR, GBP);
        Assert.assertTrue("findSuitableRates should not result in null.", exchangeRateResult != null);
        resultList = exchangeRateResult.getMatchingExchangeRates();
        Assert.assertEquals(0, resultList.size());
        exchangeRateResult = dataManager.findSuitableRates(GBP, EUR);
        Assert.assertTrue("findSuitableRates should not result in null.", exchangeRateResult != null);
        resultList = exchangeRateResult.getMatchingExchangeRates();
        Assert.assertEquals(0, resultList.size());

        /* ============ delete ============ */

        /* Record 1 */
        input = new ExchangeRate();
        input.setId(ID_A);
        deleteList = new ArrayList<ExchangeRate>();
        deleteList.add(input);

        dataManager.deleteExchangeRates(deleteList);

        /* ---------> find post delete */
        exchangeRateResult = dataManager.findSuitableRates(EUR, USD);
        resultList = exchangeRateResult.getMatchingExchangeRates();
        Assert.assertEquals(0, resultList.size());

        exchangeRateResult = dataManager.findSuitableRates(USD, EUR);
        resultList = exchangeRateResult.getMatchingExchangeRates();
        Assert.assertEquals(0, resultList.size());

        exchangeRateResult = dataManager.findSuitableRates(EUR, TRY);
        resultList = exchangeRateResult.getMatchingExchangeRates();
        Assert.assertEquals(1, resultList.size());

        exchangeRateResult = dataManager.findSuitableRates(TRY, EUR);
        resultList = exchangeRateResult.getMatchingExchangeRates();
        Assert.assertEquals(1, resultList.size());

        resultList = dataManager.getAllExchangeRatesWithoutInversion();
        Assert.assertEquals(3, resultList.size());

        occuranceFlags.clear();
        for (int i = 0; i < resultList.size(); i++) {
            occuranceFlags.set((int) resultList.get(i).getId());
        }
        Assert.assertTrue("Record 1 should not be part of the resultset as it has been deleted.",
                !occuranceFlags.get((int) ID_A));

        Assert.assertTrue("Record 2 was not part of the resultset as it has not yet been deleted.",
                occuranceFlags.get((int) ID_B));
        Assert.assertTrue("Record 3 was not part of the resultset as it has not yet been deleted.",
                occuranceFlags.get((int) ID_C));
        Assert.assertTrue("Record 4 was not part of the resultset as it has not yet been deleted.",
                occuranceFlags.get((int) ID_D));

        /* Record 2 */
        input = new ExchangeRate();
        input.setId(ID_B);
        deleteList = new ArrayList<ExchangeRate>();
        deleteList.add(input);

        dataManager.deleteExchangeRates(deleteList);

        /* ---------> find post delete */
        exchangeRateResult = dataManager.findSuitableRates(EUR, TRY);
        resultList = exchangeRateResult.getMatchingExchangeRates();
        Assert.assertEquals(0, resultList.size());

        exchangeRateResult = dataManager.findSuitableRates(TRY, EUR);
        resultList = exchangeRateResult.getMatchingExchangeRates();
        Assert.assertEquals(0, resultList.size());

        resultList = dataManager.getAllExchangeRatesWithoutInversion();
        Assert.assertEquals(2, resultList.size());

        occuranceFlags.clear();
        for (int i = 0; i < resultList.size(); i++) {
            occuranceFlags.set((int) resultList.get(i).getId());
        }
        Assert.assertTrue("Record 1 should not be part of the resultset as it has been deleted.",
                !occuranceFlags.get((int) ID_A));
        Assert.assertTrue("Record 1 should not be part of the resultset as it has been deleted.",
                !occuranceFlags.get((int) ID_B));

        Assert.assertTrue("Record 3 was not part of the resultset as it has not yet been deleted.",
                occuranceFlags.get((int) ID_C));
        Assert.assertTrue("Record 4 was not part of the resultset as it has not yet been deleted.",
                occuranceFlags.get((int) ID_D));

        /* Record 3 */
        input = new ExchangeRate();
        input.setId(ID_C);
        deleteList = new ArrayList<ExchangeRate>();
        deleteList.add(input);

        dataManager.deleteExchangeRates(deleteList);

        /* ---------> find post delete */
        exchangeRateResult = dataManager.findSuitableRates(TRY, GBP);
        resultList = exchangeRateResult.getMatchingExchangeRates();
        Assert.assertEquals(1, resultList.size());
        idReturned = resultList.get(0).getId();
        Assert.assertTrue("findSuitableRates should  deliver record 4 as record three has been it has been deleted.",
                idReturned == ID_D);

        exchangeRateResult = dataManager.findSuitableRates(GBP, TRY);
        resultList = exchangeRateResult.getMatchingExchangeRates();
        Assert.assertEquals(1, resultList.size());
        idReturned = resultList.get(0).getId();
        Assert.assertTrue("findSuitableRates should  deliver record 4 as record three has been it has been deleted.",
                idReturned == ID_D);

        resultList = dataManager.getAllExchangeRatesWithoutInversion();
        Assert.assertEquals(1, resultList.size());
        idReturned = resultList.get(0).getId();
        Assert.assertTrue("findSuitableRates should  deliver record 4 as record three has been it has been deleted.",
                idReturned == ID_D);

        /* ---------> find post delete */
        input = new ExchangeRate();
        input.setId(ID_D);
        deleteList = new ArrayList<ExchangeRate>();
        deleteList.add(input);

        dataManager.deleteExchangeRates(deleteList);

        /* find post delete */
        exchangeRateResult = dataManager.findSuitableRates(TRY, GBP);
        resultList = exchangeRateResult.getMatchingExchangeRates();
        Assert.assertEquals(0, resultList.size());

        exchangeRateResult = dataManager.findSuitableRates(GBP, TRY);
        resultList = exchangeRateResult.getMatchingExchangeRates();
        Assert.assertEquals(0, resultList.size());

        resultList = dataManager.getAllExchangeRatesWithoutInversion();
        Assert.assertEquals(0, resultList.size());

    }

    private void createAndcreateAndAssert4BaseRecords(DataManagerImpl dataManager) {

        ExchangeRate input;
        ExchangeRate output;

        long expectedId;

        input = new ExchangeRate();
        input.setCurrencyFrom(EUR);
        input.setCurrencyTo(USD);
        input.setDescription("My first exchange rate");
        input.setExchangeRate(0.7539);
        input.setImportOrigin(ImportOrigin.NONE);
        expectedId = ID_A;

        output = dataManager.persistExchangeRate(input);
        assertEquality(input, output, expectedId, false);
        initalRetrievalResults.put(expectedId, output);

        input = new ExchangeRate();
        input.setCurrencyFrom(EUR);
        input.setCurrencyTo(TRY);
        input.setDescription("My second exchange rate");
        input.setExchangeRate(134.0001);
        input.setImportOrigin(ImportOrigin.NONE);
        expectedId = ID_B;

        output = dataManager.persistExchangeRate(input);
        assertEquality(input, output, expectedId, false);
        initalRetrievalResults.put(expectedId, output);

        input = new ExchangeRate();
        input.setCurrencyFrom(TRY);
        input.setCurrencyTo(GBP);
        input.setDescription("My third exchange rate");
        input.setExchangeRate(6666.0);
        input.setImportOrigin(ImportOrigin.NONE);
        expectedId = ID_C;

        output = dataManager.persistExchangeRate(input);
        assertEquality(input, output, expectedId, false);
        initalRetrievalResults.put(expectedId, output);

        input = new ExchangeRate();
        input.setCurrencyFrom(TRY);
        input.setCurrencyTo(GBP);
        input.setDescription("My fourth exchange rate");
        input.setExchangeRate(6666.7777);
        input.setImportOrigin(ImportOrigin.NONE);
        expectedId = ID_D;

        output = dataManager.persistExchangeRate(input);
        assertEquality(input, output, expectedId, false);
        initalRetrievalResults.put(expectedId, output);
    }

    private void assertEquality(ExchangeRate input, ExchangeRate output, long expectedId, boolean checkDateEquality) {
        Assert.assertEquals(expectedId, output.getId());
        Assert.assertEquals(input.getCurrencyFrom(), output.getCurrencyFrom());
        Assert.assertEquals(input.getCurrencyTo(), output.getCurrencyTo());
        Assert.assertEquals(input.getDescription(), output.getDescription());
        Assert.assertEquals(input.getExchangeRate(), output.getExchangeRate());
        Assert.assertEquals(input.getImportOrigin(), output.getImportOrigin());
        if (checkDateEquality) {
            Assert.assertEquals(input.getCreationDate(), output.getCreationDate());
            Assert.assertEquals(input.getUpdateDate(), output.getUpdateDate());
        }
        else {
            Assert.assertTrue("A persisted exchange rate should come alone with a creation date.",
                    output.getCreationDate() != null);
            Assert.assertTrue("A persisted exchange rate should come alone with an update date.",
                    output.getUpdateDate() != null);
        }
    }

}