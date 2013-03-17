package de.koelle.christian.trickytripper.test.dataaccess.suite.exchange;

import static de.koelle.christian.trickytripper.test.dataaccess.suite.exchange.ExchangeRateTestSupport.BAM;
import static de.koelle.christian.trickytripper.test.dataaccess.suite.exchange.ExchangeRateTestSupport.ERN;
import static de.koelle.christian.trickytripper.test.dataaccess.suite.exchange.ExchangeRateTestSupport.EUR;
import static de.koelle.christian.trickytripper.test.dataaccess.suite.exchange.ExchangeRateTestSupport.GBP;
import static de.koelle.christian.trickytripper.test.dataaccess.suite.exchange.ExchangeRateTestSupport.ID_1;
import static de.koelle.christian.trickytripper.test.dataaccess.suite.exchange.ExchangeRateTestSupport.ID_2;
import static de.koelle.christian.trickytripper.test.dataaccess.suite.exchange.ExchangeRateTestSupport.ID_3;
import static de.koelle.christian.trickytripper.test.dataaccess.suite.exchange.ExchangeRateTestSupport.REC_01;
import static de.koelle.christian.trickytripper.test.dataaccess.suite.exchange.ExchangeRateTestSupport.REC_02;
import static de.koelle.christian.trickytripper.test.dataaccess.suite.exchange.ExchangeRateTestSupport.REC_03;
import static de.koelle.christian.trickytripper.test.dataaccess.suite.exchange.ExchangeRateTestSupport.REC_04;
import static de.koelle.christian.trickytripper.test.dataaccess.suite.exchange.ExchangeRateTestSupport.REC_EUR_USD_01;
import static de.koelle.christian.trickytripper.test.dataaccess.suite.exchange.ExchangeRateTestSupport.REC_EUR_USD_02;
import static de.koelle.christian.trickytripper.test.dataaccess.suite.exchange.ExchangeRateTestSupport.REC_EUR_USD_03;
import static de.koelle.christian.trickytripper.test.dataaccess.suite.exchange.ExchangeRateTestSupport.REC_EUR_USD_04;
import static de.koelle.christian.trickytripper.test.dataaccess.suite.exchange.ExchangeRateTestSupport.TRY;
import static de.koelle.christian.trickytripper.test.dataaccess.suite.exchange.ExchangeRateTestSupport.USD;

import java.util.Arrays;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;
import android.test.ApplicationTestCase;
import de.koelle.christian.trickytripper.TrickyTripperApp;
import de.koelle.christian.trickytripper.dataaccess.impl.DataConstants;
import de.koelle.christian.trickytripper.model.CurrencyWithName;
import de.koelle.christian.trickytripper.model.ExchangeRate;
import de.koelle.christian.trickytripper.model.HierarchicalCurrencyList;
import de.koelle.christian.trickytripper.model.TripSummary;

public class CurrencyLastUsedTest extends ApplicationTestCase<TrickyTripperApp> {

    private static final int totalAmountOfAllCurrencies = 190;

    private final Map<Long, ExchangeRate> initalRetrievalResults = new HashMap<Long, ExchangeRate>();

    public CurrencyLastUsedTest() {
        super(TrickyTripperApp.class);
    }

    @Override
    protected void setUp() {
        getContext().deleteDatabase(DataConstants.DATABASE_NAME);
        createApplication();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testGetAllCurrenciesForTargetBlank() {
        TrickyTripperApp app = getApplication();
        HierarchicalCurrencyList result = app.getMiscController().getAllCurrenciesForTarget(USD);

        assertOrderAndContentContent(result.getCurrenciesMatchingInOrderOfUsage(), new Currency[] {});
        assertOrderAndContentContent(result.getCurrenciesUsedByDate(), new Currency[] {});
        assertOrderAndContentContent(result.getCurrenciesInProject(), new Currency[] {});
        assertTailContent(result.getCurrenciesElse(), totalAmountOfAllCurrencies);

    }

    public void testgetAllCurrenciesBlank() {
        TrickyTripperApp app = getApplication();
        HierarchicalCurrencyList result = app.getMiscController().getAllCurrencies();

        assertOrderAndContentContent(result.getCurrenciesMatchingInOrderOfUsage(), new Currency[] {});
        assertOrderAndContentContent(result.getCurrenciesUsedByDate(), new Currency[] {});
        assertOrderAndContentContent(result.getCurrenciesInProject(), new Currency[] { USD });
        assertTailContent(result.getCurrenciesElse(), totalAmountOfAllCurrencies - 1);
    }

    public void testGetAllCurrenciesForTarget() {

        TrickyTripperApp app = getApplication();
        ExchangeRate rateSaved;
        HierarchicalCurrencyList result;

        rateSaved = app.getExchangeRateController().persistExchangeRate(REC_01);
        initalRetrievalResults.put(rateSaved.getId(), rateSaved);
        rateSaved = app.getExchangeRateController().persistExchangeRate(REC_02);
        initalRetrievalResults.put(rateSaved.getId(), rateSaved);
        rateSaved = app.getExchangeRateController().persistExchangeRate(REC_03);
        initalRetrievalResults.put(rateSaved.getId(), rateSaved);
        rateSaved = app.getExchangeRateController().persistExchangeRate(REC_04);
        initalRetrievalResults.put(rateSaved.getId(), rateSaved);
        rateSaved = app.getExchangeRateController().persistExchangeRate(REC_EUR_USD_01);
        initalRetrievalResults.put(rateSaved.getId(), rateSaved);
        rateSaved = app.getExchangeRateController().persistExchangeRate(REC_EUR_USD_02);
        initalRetrievalResults.put(rateSaved.getId(), rateSaved);
        rateSaved = app.getExchangeRateController().persistExchangeRate(REC_EUR_USD_03);
        initalRetrievalResults.put(rateSaved.getId(), rateSaved);
        rateSaved = app.getExchangeRateController().persistExchangeRate(REC_EUR_USD_04);
        initalRetrievalResults.put(rateSaved.getId(), rateSaved);

        /* =============== Step 1 =============== */

        result = app.getMiscController().getAllCurrenciesForTarget(USD);

        assertOrderAndContentContent(result.getCurrenciesMatchingInOrderOfUsage(), new Currency[] {});
        assertOrderAndContentContent(result.getCurrenciesUsedByDate(), new Currency[] {});
        assertOrderAndContentContent(result.getCurrenciesInProject(), new Currency[] {});
        assertTailContent(result.getCurrenciesElse(), totalAmountOfAllCurrencies);

        /* =============== Step 2 =============== */

        /* EUR - TRY */
        app.getExchangeRateController().persistExchangeRateUsedLast(initalRetrievalResults.get(ID_2));
        result = app.getMiscController().getAllCurrenciesForTarget(USD);

        assertOrderAndContentContent(result.getCurrenciesMatchingInOrderOfUsage(), new Currency[] {});
        assertOrderAndContentContent(result.getCurrenciesUsedByDate(), new Currency[] { EUR, TRY });
        assertOrderAndContentContent(result.getCurrenciesInProject(), new Currency[] {});
        assertTailContent(result.getCurrenciesElse(), totalAmountOfAllCurrencies - 2);

        /* TRY - GBP */
        app.getExchangeRateController().persistExchangeRateUsedLast(initalRetrievalResults.get(ID_3));
        result = app.getMiscController().getAllCurrenciesForTarget(USD);

        assertOrderAndContentContent(result.getCurrenciesMatchingInOrderOfUsage(), new Currency[] {});
        assertOrderAndContentContent(result.getCurrenciesUsedByDate(), new Currency[] { GBP, TRY, EUR });
        assertOrderAndContentContent(result.getCurrenciesInProject(), new Currency[] {});
        assertTailContent(result.getCurrenciesElse(), totalAmountOfAllCurrencies - 3);

        /* EUR - TRY */
        app.getExchangeRateController().persistExchangeRateUsedLast(initalRetrievalResults.get(ID_2));
        result = app.getMiscController().getAllCurrenciesForTarget(USD);

        assertOrderAndContentContent(result.getCurrenciesMatchingInOrderOfUsage(), new Currency[] {});
        assertOrderAndContentContent(result.getCurrenciesUsedByDate(), new Currency[] { EUR, TRY, GBP });
        assertOrderAndContentContent(result.getCurrenciesInProject(), new Currency[] {});
        assertTailContent(result.getCurrenciesElse(), totalAmountOfAllCurrencies - 3);

        /* EURO - USD */
        app.getExchangeRateController().persistExchangeRateUsedLast(initalRetrievalResults.get(ID_1));
        result = app.getMiscController().getAllCurrenciesForTarget(USD);

        assertOrderAndContentContent(result.getCurrenciesMatchingInOrderOfUsage(), new Currency[] { EUR });
        assertOrderAndContentContent(result.getCurrenciesUsedByDate(), new Currency[] { TRY, GBP });
        assertOrderAndContentContent(result.getCurrenciesInProject(), new Currency[] {});
        assertTailContent(result.getCurrenciesElse(), totalAmountOfAllCurrencies - 3);

        result = app.getMiscController().getAllCurrenciesForTarget(EUR);

        assertOrderAndContentContent(result.getCurrenciesMatchingInOrderOfUsage(), new Currency[] { USD, TRY });
        assertOrderAndContentContent(result.getCurrenciesUsedByDate(), new Currency[] { GBP });
        assertOrderAndContentContent(result.getCurrenciesInProject(), new Currency[] {});
        assertTailContent(result.getCurrenciesElse(), totalAmountOfAllCurrencies - 3);

        app.getExchangeRateController().persistExchangeRateUsedLast(initalRetrievalResults.get(ID_2));
        result = app.getMiscController().getAllCurrenciesForTarget(EUR);

        assertOrderAndContentContent(result.getCurrenciesMatchingInOrderOfUsage(), new Currency[] { TRY, USD });
        assertOrderAndContentContent(result.getCurrenciesUsedByDate(), new Currency[] { GBP });
        assertOrderAndContentContent(result.getCurrenciesInProject(), new Currency[] {});
        assertTailContent(result.getCurrenciesElse(), totalAmountOfAllCurrencies - 3);

        /* =============== Step 3 =============== */

        app.getTripController().persist(new TripSummary("MyTrip01", BAM));

        result = app.getMiscController().getAllCurrenciesForTarget(EUR);

        assertOrderAndContentContent(result.getCurrenciesMatchingInOrderOfUsage(), new Currency[] { TRY, USD });
        assertOrderAndContentContent(result.getCurrenciesUsedByDate(), new Currency[] { GBP });
        assertOrderAndContentContent(result.getCurrenciesInProject(), new Currency[] { BAM });
        assertTailContent(result.getCurrenciesElse(), totalAmountOfAllCurrencies - 4);

        app.getTripController().persist(new TripSummary("MyTrip02", ERN));

        result = app.getMiscController().getAllCurrenciesForTarget(EUR);

        assertOrderAndContentContent(result.getCurrenciesMatchingInOrderOfUsage(), new Currency[] { TRY, USD });
        assertOrderAndContentContent(result.getCurrenciesUsedByDate(), new Currency[] { GBP });
        assertOrderAndContentContent(result.getCurrenciesInProject(), new Currency[] { BAM, ERN });
        assertTailContent(result.getCurrenciesElse(), totalAmountOfAllCurrencies - 5);

    }

    public void testgetAllCurrencies() {

        TrickyTripperApp app = getApplication();
        ExchangeRate rateSaved;
        HierarchicalCurrencyList result;

        rateSaved = app.getExchangeRateController().persistExchangeRate(REC_01);
        initalRetrievalResults.put(rateSaved.getId(), rateSaved);
        rateSaved = app.getExchangeRateController().persistExchangeRate(REC_02);
        initalRetrievalResults.put(rateSaved.getId(), rateSaved);
        rateSaved = app.getExchangeRateController().persistExchangeRate(REC_03);
        initalRetrievalResults.put(rateSaved.getId(), rateSaved);
        rateSaved = app.getExchangeRateController().persistExchangeRate(REC_04);
        initalRetrievalResults.put(rateSaved.getId(), rateSaved);
        rateSaved = app.getExchangeRateController().persistExchangeRate(REC_EUR_USD_01);
        initalRetrievalResults.put(rateSaved.getId(), rateSaved);
        rateSaved = app.getExchangeRateController().persistExchangeRate(REC_EUR_USD_02);
        initalRetrievalResults.put(rateSaved.getId(), rateSaved);
        rateSaved = app.getExchangeRateController().persistExchangeRate(REC_EUR_USD_03);
        initalRetrievalResults.put(rateSaved.getId(), rateSaved);
        rateSaved = app.getExchangeRateController().persistExchangeRate(REC_EUR_USD_04);
        initalRetrievalResults.put(rateSaved.getId(), rateSaved);

        /* =============== Step 1 =============== */
        result = app.getMiscController().getAllCurrencies();

        assertOrderAndContentContent(result.getCurrenciesMatchingInOrderOfUsage(), new Currency[] {});
        assertOrderAndContentContent(result.getCurrenciesUsedByDate(), new Currency[] {});
        assertOrderAndContentContent(result.getCurrenciesInProject(), new Currency[] { USD });
        assertTailContent(result.getCurrenciesElse(), 189);

        /* =============== Step 2 =============== */

        /* EUR - TRY */
        app.getExchangeRateController().persistExchangeRateUsedLast(initalRetrievalResults.get(ID_2));
        result = app.getMiscController().getAllCurrencies();

        assertOrderAndContentContent(result.getCurrenciesMatchingInOrderOfUsage(), new Currency[] {});
        assertOrderAndContentContent(result.getCurrenciesUsedByDate(), new Currency[] { EUR, TRY });
        assertOrderAndContentContent(result.getCurrenciesInProject(), new Currency[] { USD });
        assertTailContent(result.getCurrenciesElse(), totalAmountOfAllCurrencies - 3);

        /* TRY - GBP */
        app.getExchangeRateController().persistExchangeRateUsedLast(initalRetrievalResults.get(ID_3));
        result = app.getMiscController().getAllCurrencies();

        assertOrderAndContentContent(result.getCurrenciesMatchingInOrderOfUsage(), new Currency[] {});
        assertOrderAndContentContent(result.getCurrenciesUsedByDate(), new Currency[] { GBP, TRY, EUR });
        assertOrderAndContentContent(result.getCurrenciesInProject(), new Currency[] { USD });
        assertTailContent(result.getCurrenciesElse(), totalAmountOfAllCurrencies - 4);

        /* EUR - TRY */
        app.getExchangeRateController().persistExchangeRateUsedLast(initalRetrievalResults.get(ID_2));
        result = app.getMiscController().getAllCurrencies();

        assertOrderAndContentContent(result.getCurrenciesMatchingInOrderOfUsage(), new Currency[] {});
        assertOrderAndContentContent(result.getCurrenciesUsedByDate(), new Currency[] { EUR, TRY, GBP });
        assertOrderAndContentContent(result.getCurrenciesInProject(), new Currency[] { USD });
        assertTailContent(result.getCurrenciesElse(), totalAmountOfAllCurrencies - 4);

        /* EURO - USD */
        app.getExchangeRateController().persistExchangeRateUsedLast(initalRetrievalResults.get(ID_1));
        result = app.getMiscController().getAllCurrencies();

        assertOrderAndContentContent(result.getCurrenciesMatchingInOrderOfUsage(), new Currency[] {});
        assertOrderAndContentContent(result.getCurrenciesUsedByDate(), new Currency[] { EUR, USD, TRY, GBP });
        assertOrderAndContentContent(result.getCurrenciesInProject(), new Currency[] {});
        assertTailContent(result.getCurrenciesElse(), totalAmountOfAllCurrencies - 4);

        app.getExchangeRateController().persistExchangeRateUsedLast(initalRetrievalResults.get(ID_2));
        result = app.getMiscController().getAllCurrencies();

        assertOrderAndContentContent(result.getCurrenciesMatchingInOrderOfUsage(), new Currency[] {});
        assertOrderAndContentContent(result.getCurrenciesUsedByDate(), new Currency[] { EUR, TRY, USD, GBP });
        assertOrderAndContentContent(result.getCurrenciesInProject(), new Currency[] {});
        assertTailContent(result.getCurrenciesElse(), totalAmountOfAllCurrencies - 4);

        /* =============== Step 3 =============== */

        app.getTripController().persist(new TripSummary("MyTrip01", BAM));

        result = app.getMiscController().getAllCurrencies();

        assertOrderAndContentContent(result.getCurrenciesMatchingInOrderOfUsage(), new Currency[] {});
        assertOrderAndContentContent(result.getCurrenciesUsedByDate(), new Currency[] { EUR, TRY, USD, GBP });
        assertOrderAndContentContent(result.getCurrenciesInProject(), new Currency[] { BAM });
        assertTailContent(result.getCurrenciesElse(), totalAmountOfAllCurrencies - 5);

        app.getTripController().persist(new TripSummary("MyTrip02", ERN));

        result = app.getMiscController().getAllCurrencies();

        assertOrderAndContentContent(result.getCurrenciesMatchingInOrderOfUsage(), new Currency[] {});
        assertOrderAndContentContent(result.getCurrenciesUsedByDate(), new Currency[] { EUR, TRY, USD, GBP });
        assertOrderAndContentContent(result.getCurrenciesInProject(), new Currency[] { BAM, ERN });
        assertTailContent(result.getCurrenciesElse(), totalAmountOfAllCurrencies - 6);
    }

    public void testgetAllCurrenciesForTargetDeleteExchangeRates() {

        TrickyTripperApp app = getApplication();
        ExchangeRate rateSaved;
        HierarchicalCurrencyList result;

        rateSaved = app.getExchangeRateController().persistExchangeRate(REC_01);
        initalRetrievalResults.put(rateSaved.getId(), rateSaved);
        rateSaved = app.getExchangeRateController().persistExchangeRate(REC_02);
        initalRetrievalResults.put(rateSaved.getId(), rateSaved);
        rateSaved = app.getExchangeRateController().persistExchangeRate(REC_03);
        initalRetrievalResults.put(rateSaved.getId(), rateSaved);
        rateSaved = app.getExchangeRateController().persistExchangeRate(REC_04);
        initalRetrievalResults.put(rateSaved.getId(), rateSaved);
        rateSaved = app.getExchangeRateController().persistExchangeRate(REC_EUR_USD_01);
        initalRetrievalResults.put(rateSaved.getId(), rateSaved);
        rateSaved = app.getExchangeRateController().persistExchangeRate(REC_EUR_USD_02);
        initalRetrievalResults.put(rateSaved.getId(), rateSaved);
        rateSaved = app.getExchangeRateController().persistExchangeRate(REC_EUR_USD_03);
        initalRetrievalResults.put(rateSaved.getId(), rateSaved);
        rateSaved = app.getExchangeRateController().persistExchangeRate(REC_EUR_USD_04);
        initalRetrievalResults.put(rateSaved.getId(), rateSaved);

        /* =============== Step 1 =============== */
        /* EUR - TRY */
        app.getExchangeRateController().persistExchangeRateUsedLast(initalRetrievalResults.get(ID_2));
        /* TRY - GBP */
        app.getExchangeRateController().persistExchangeRateUsedLast(initalRetrievalResults.get(ID_3));

        app.getExchangeRateController().deleteExchangeRates(
                Arrays.asList(new ExchangeRate[] { initalRetrievalResults.get(ID_2) }));

        result = app.getMiscController().getAllCurrencies();

        assertOrderAndContentContent(result.getCurrenciesMatchingInOrderOfUsage(), new Currency[] {});
        assertOrderAndContentContent(result.getCurrenciesUsedByDate(), new Currency[] { GBP, TRY });
        assertOrderAndContentContent(result.getCurrenciesInProject(), new Currency[] { USD });
        assertTailContent(result.getCurrenciesElse(), totalAmountOfAllCurrencies - 3);

        app.getExchangeRateController().deleteExchangeRates(
                Arrays.asList(new ExchangeRate[] { initalRetrievalResults.get(ID_3) }));

        result = app.getMiscController().getAllCurrencies();

        assertOrderAndContentContent(result.getCurrenciesMatchingInOrderOfUsage(), new Currency[] {});
        assertOrderAndContentContent(result.getCurrenciesUsedByDate(), new Currency[] {});
        assertOrderAndContentContent(result.getCurrenciesInProject(), new Currency[] { USD });
        assertTailContent(result.getCurrenciesElse(), totalAmountOfAllCurrencies - 1);

    }

    public void testgetAllCurrenciesDeleteExchangeRates() {

        TrickyTripperApp app = getApplication();
        ExchangeRate rateSaved;
        HierarchicalCurrencyList result;

        rateSaved = app.getExchangeRateController().persistExchangeRate(REC_01);
        initalRetrievalResults.put(rateSaved.getId(), rateSaved);
        rateSaved = app.getExchangeRateController().persistExchangeRate(REC_02);
        initalRetrievalResults.put(rateSaved.getId(), rateSaved);
        rateSaved = app.getExchangeRateController().persistExchangeRate(REC_03);
        initalRetrievalResults.put(rateSaved.getId(), rateSaved);
        rateSaved = app.getExchangeRateController().persistExchangeRate(REC_04);
        initalRetrievalResults.put(rateSaved.getId(), rateSaved);
        rateSaved = app.getExchangeRateController().persistExchangeRate(REC_EUR_USD_01);
        initalRetrievalResults.put(rateSaved.getId(), rateSaved);
        rateSaved = app.getExchangeRateController().persistExchangeRate(REC_EUR_USD_02);
        initalRetrievalResults.put(rateSaved.getId(), rateSaved);
        rateSaved = app.getExchangeRateController().persistExchangeRate(REC_EUR_USD_03);
        initalRetrievalResults.put(rateSaved.getId(), rateSaved);
        rateSaved = app.getExchangeRateController().persistExchangeRate(REC_EUR_USD_04);
        initalRetrievalResults.put(rateSaved.getId(), rateSaved);

        /* =============== Step 1 =============== */
        result = app.getMiscController().getAllCurrencies();
        /* EUR - TRY */
        app.getExchangeRateController().persistExchangeRateUsedLast(initalRetrievalResults.get(ID_2));
        /* TRY - GBP */
        app.getExchangeRateController().persistExchangeRateUsedLast(initalRetrievalResults.get(ID_3));

        app.getExchangeRateController().deleteExchangeRates(
                Arrays.asList(new ExchangeRate[] { initalRetrievalResults.get(ID_2) }));

        result = app.getMiscController().getAllCurrencies();

        assertOrderAndContentContent(result.getCurrenciesMatchingInOrderOfUsage(), new Currency[] {});
        assertOrderAndContentContent(result.getCurrenciesUsedByDate(), new Currency[] { GBP, TRY });
        assertOrderAndContentContent(result.getCurrenciesInProject(), new Currency[] { USD });
        assertTailContent(result.getCurrenciesElse(), totalAmountOfAllCurrencies - 3);

        app.getExchangeRateController().deleteExchangeRates(
                Arrays.asList(new ExchangeRate[] { initalRetrievalResults.get(ID_3) }));

        result = app.getMiscController().getAllCurrencies();

        assertOrderAndContentContent(result.getCurrenciesMatchingInOrderOfUsage(), new Currency[] {});
        assertOrderAndContentContent(result.getCurrenciesUsedByDate(), new Currency[] {});
        assertOrderAndContentContent(result.getCurrenciesInProject(), new Currency[] { USD });
        assertTailContent(result.getCurrenciesElse(), totalAmountOfAllCurrencies - 1);

    }

    public void testGetAllCurrenciesForTagetTripFirst() {

        TrickyTripperApp app = getApplication();
        HierarchicalCurrencyList result;
        ExchangeRate rateSaved;
        TripSummary tripSummary;

        /* =============== Step 1 =============== */

        String tripName_BAM = "MyTrip01";
        String tripName_ERN = "MyTrip02";
        String tripName_TRY = "MyTrip03";
        String tripName_EUR = "MyTrip04";

        app.getTripController().persist(new TripSummary(tripName_BAM, BAM));

        result = app.getMiscController().getAllCurrenciesForTarget(USD);

        assertOrderAndContentContent(result.getCurrenciesMatchingInOrderOfUsage(), new Currency[] {});
        assertOrderAndContentContent(result.getCurrenciesUsedByDate(), new Currency[] {});
        assertOrderAndContentContent(result.getCurrenciesInProject(), new Currency[] { BAM });
        assertTailContent(result.getCurrenciesElse(), totalAmountOfAllCurrencies - 1);

        app.getTripController().persist(new TripSummary(tripName_ERN, ERN));

        result = app.getMiscController().getAllCurrenciesForTarget(USD);

        assertOrderAndContentContent(result.getCurrenciesMatchingInOrderOfUsage(), new Currency[] {});
        assertOrderAndContentContent(result.getCurrenciesUsedByDate(), new Currency[] {});
        assertOrderAndContentContent(result.getCurrenciesInProject(), new Currency[] { BAM, ERN });
        assertTailContent(result.getCurrenciesElse(), totalAmountOfAllCurrencies - 2);

        app.getTripController().persist(new TripSummary(tripName_TRY, TRY));
        app.getTripController().persist(new TripSummary(tripName_EUR, EUR));

        result = app.getMiscController().getAllCurrenciesForTarget(USD);

        assertOrderAndContentContent(result.getCurrenciesMatchingInOrderOfUsage(), new Currency[] {});
        assertOrderAndContentContent(result.getCurrenciesUsedByDate(), new Currency[] {});
        assertOrderAndContentContent(result.getCurrenciesInProject(), new Currency[] { BAM, ERN, EUR, TRY });
        assertTailContent(result.getCurrenciesElse(), totalAmountOfAllCurrencies - 4);

        result = app.getMiscController().getAllCurrenciesForTarget(EUR);

        assertOrderAndContentContent(result.getCurrenciesMatchingInOrderOfUsage(), new Currency[] {});
        assertOrderAndContentContent(result.getCurrenciesUsedByDate(), new Currency[] {});
        assertOrderAndContentContent(result.getCurrenciesInProject(), new Currency[] { BAM, ERN, TRY, USD });
        assertTailContent(result.getCurrenciesElse(), totalAmountOfAllCurrencies - 4);

        List<TripSummary> tripSummaries = app.getTripController().getAllTrips();

        tripSummary = getTripByName(tripSummaries, tripName_TRY);
        app.getTripController().deleteTrip(tripSummary);

        result = app.getMiscController().getAllCurrenciesForTarget(EUR);

        assertOrderAndContentContent(result.getCurrenciesMatchingInOrderOfUsage(), new Currency[] {});
        assertOrderAndContentContent(result.getCurrenciesUsedByDate(), new Currency[] {});
        assertOrderAndContentContent(result.getCurrenciesInProject(), new Currency[] { BAM, ERN, USD });
        assertTailContent(result.getCurrenciesElse(), totalAmountOfAllCurrencies - 3);

        app.getTripController().persist(new TripSummary(tripName_TRY, TRY));

        /* =============== Step 2 =============== */

        /* TRY - GBP */
        rateSaved = app.getExchangeRateController().persistExchangeRate(REC_03);
        initalRetrievalResults.put(rateSaved.getId(), rateSaved);
        app.getExchangeRateController().persistExchangeRateUsedLast(initalRetrievalResults.get(rateSaved.getId()));

        result = app.getMiscController().getAllCurrenciesForTarget(EUR);

        assertOrderAndContentContent(result.getCurrenciesMatchingInOrderOfUsage(), new Currency[] {});
        assertOrderAndContentContent(result.getCurrenciesUsedByDate(), new Currency[] { GBP, TRY });
        assertOrderAndContentContent(result.getCurrenciesInProject(), new Currency[] { BAM, ERN, USD });
        assertTailContent(result.getCurrenciesElse(), totalAmountOfAllCurrencies - 5);

        /* EURO - USD */
        rateSaved = app.getExchangeRateController().persistExchangeRate(REC_01);
        initalRetrievalResults.put(rateSaved.getId(), rateSaved);
        app.getExchangeRateController().persistExchangeRateUsedLast(initalRetrievalResults.get(rateSaved.getId()));

        result = app.getMiscController().getAllCurrenciesForTarget(EUR);

        assertOrderAndContentContent(result.getCurrenciesMatchingInOrderOfUsage(), new Currency[] { USD });
        assertOrderAndContentContent(result.getCurrenciesUsedByDate(), new Currency[] { GBP, TRY });
        assertOrderAndContentContent(result.getCurrenciesInProject(), new Currency[] { BAM, ERN });
        assertTailContent(result.getCurrenciesElse(), totalAmountOfAllCurrencies - 5);

        result = app.getMiscController().getAllCurrenciesForTarget(BAM);

        assertOrderAndContentContent(result.getCurrenciesMatchingInOrderOfUsage(), new Currency[] {});
        assertOrderAndContentContent(result.getCurrenciesUsedByDate(), new Currency[] { EUR, USD, GBP, TRY });
        assertOrderAndContentContent(result.getCurrenciesInProject(), new Currency[] { ERN });
        assertTailContent(result.getCurrenciesElse(), totalAmountOfAllCurrencies - 5);

        /* =============== Step 3 =============== */

        tripSummary = getTripByName(tripSummaries, tripName_ERN);
        app.getTripController().deleteTrip(tripSummary);

        result = app.getMiscController().getAllCurrenciesForTarget(BAM);

        assertOrderAndContentContent(result.getCurrenciesMatchingInOrderOfUsage(), new Currency[] {});
        assertOrderAndContentContent(result.getCurrenciesUsedByDate(), new Currency[] { EUR, USD, GBP, TRY });
        assertOrderAndContentContent(result.getCurrenciesInProject(), new Currency[] {});
        assertTailContent(result.getCurrenciesElse(), totalAmountOfAllCurrencies - 4);

        tripSummary = getTripByName(tripSummaries, tripName_BAM);
        app.getTripController().deleteTrip(tripSummary);

        result = app.getMiscController().getAllCurrenciesForTarget(BAM);

        assertOrderAndContentContent(result.getCurrenciesMatchingInOrderOfUsage(), new Currency[] {});
        assertOrderAndContentContent(result.getCurrenciesUsedByDate(), new Currency[] { EUR, USD, GBP, TRY });
        assertOrderAndContentContent(result.getCurrenciesInProject(), new Currency[] {});
        assertTailContent(result.getCurrenciesElse(), totalAmountOfAllCurrencies - 4);

        tripSummary = getTripByName(tripSummaries, tripName_TRY);
        app.getTripController().deleteTrip(tripSummary);

        result = app.getMiscController().getAllCurrenciesForTarget(BAM);

        assertOrderAndContentContent(result.getCurrenciesMatchingInOrderOfUsage(), new Currency[] {});
        assertOrderAndContentContent(result.getCurrenciesUsedByDate(), new Currency[] { EUR, USD, GBP, TRY });
        assertOrderAndContentContent(result.getCurrenciesInProject(), new Currency[] {});
        assertTailContent(result.getCurrenciesElse(), totalAmountOfAllCurrencies - 4);
    }

    public void testGetAllCurrenciesTripFirst() {

        TrickyTripperApp app = getApplication();
        HierarchicalCurrencyList result;
        ExchangeRate rateSaved;
        TripSummary tripSummary;

        /* =============== Step 1 =============== */

        String tripName_BAM = "MyTrip01";
        String tripName_ERN = "MyTrip02";
        String tripName_TRY = "MyTrip03";
        String tripName_EUR = "MyTrip04";

        app.getTripController().persist(new TripSummary(tripName_BAM, BAM));

        result = app.getMiscController().getAllCurrencies();

        assertOrderAndContentContent(result.getCurrenciesMatchingInOrderOfUsage(), new Currency[] {});
        assertOrderAndContentContent(result.getCurrenciesUsedByDate(), new Currency[] {});
        assertOrderAndContentContent(result.getCurrenciesInProject(), new Currency[] { BAM, USD });
        assertTailContent(result.getCurrenciesElse(), totalAmountOfAllCurrencies - 2);

        app.getTripController().persist(new TripSummary(tripName_ERN, ERN));

        result = app.getMiscController().getAllCurrencies();

        assertOrderAndContentContent(result.getCurrenciesMatchingInOrderOfUsage(), new Currency[] {});
        assertOrderAndContentContent(result.getCurrenciesUsedByDate(), new Currency[] {});
        assertOrderAndContentContent(result.getCurrenciesInProject(), new Currency[] { BAM, ERN, USD });
        assertTailContent(result.getCurrenciesElse(), totalAmountOfAllCurrencies - 3);

        app.getTripController().persist(new TripSummary(tripName_TRY, TRY));
        app.getTripController().persist(new TripSummary(tripName_EUR, EUR));

        result = app.getMiscController().getAllCurrencies();

        assertOrderAndContentContent(result.getCurrenciesMatchingInOrderOfUsage(), new Currency[] {});
        assertOrderAndContentContent(result.getCurrenciesUsedByDate(), new Currency[] {});
        assertOrderAndContentContent(result.getCurrenciesInProject(), new Currency[] { BAM, ERN, EUR, TRY, USD });
        assertTailContent(result.getCurrenciesElse(), totalAmountOfAllCurrencies - 5);

        List<TripSummary> tripSummaries = app.getTripController().getAllTrips();

        tripSummary = getTripByName(tripSummaries, tripName_TRY);
        app.getTripController().deleteTrip(tripSummary);

        result = app.getMiscController().getAllCurrencies();

        assertOrderAndContentContent(result.getCurrenciesMatchingInOrderOfUsage(), new Currency[] {});
        assertOrderAndContentContent(result.getCurrenciesUsedByDate(), new Currency[] {});
        assertOrderAndContentContent(result.getCurrenciesInProject(), new Currency[] { BAM, ERN, EUR, USD });
        assertTailContent(result.getCurrenciesElse(), totalAmountOfAllCurrencies - 4);

        app.getTripController().persist(new TripSummary(tripName_TRY, TRY));

        result = app.getMiscController().getAllCurrencies();

        assertOrderAndContentContent(result.getCurrenciesMatchingInOrderOfUsage(), new Currency[] {});
        assertOrderAndContentContent(result.getCurrenciesUsedByDate(), new Currency[] {});
        assertOrderAndContentContent(result.getCurrenciesInProject(), new Currency[] { BAM, ERN, EUR, TRY, USD });
        assertTailContent(result.getCurrenciesElse(), totalAmountOfAllCurrencies - 5);

        /* =============== Step 2 =============== */

        /* TRY - GBP */
        rateSaved = app.getExchangeRateController().persistExchangeRate(REC_03);
        initalRetrievalResults.put(rateSaved.getId(), rateSaved);
        app.getExchangeRateController().persistExchangeRateUsedLast(initalRetrievalResults.get(rateSaved.getId()));

        result = app.getMiscController().getAllCurrencies();

        assertOrderAndContentContent(result.getCurrenciesMatchingInOrderOfUsage(), new Currency[] {});
        assertOrderAndContentContent(result.getCurrenciesUsedByDate(), new Currency[] { GBP, TRY });
        assertOrderAndContentContent(result.getCurrenciesInProject(), new Currency[] { BAM, ERN, EUR, USD });
        assertTailContent(result.getCurrenciesElse(), totalAmountOfAllCurrencies - 6);

        /* EURO - USD */
        rateSaved = app.getExchangeRateController().persistExchangeRate(REC_01);
        initalRetrievalResults.put(rateSaved.getId(), rateSaved);
        app.getExchangeRateController().persistExchangeRateUsedLast(initalRetrievalResults.get(rateSaved.getId()));

        result = app.getMiscController().getAllCurrencies();

        assertOrderAndContentContent(result.getCurrenciesMatchingInOrderOfUsage(), new Currency[] {});
        assertOrderAndContentContent(result.getCurrenciesUsedByDate(), new Currency[] { EUR, USD, GBP, TRY });
        assertOrderAndContentContent(result.getCurrenciesInProject(), new Currency[] { BAM, ERN });
        assertTailContent(result.getCurrenciesElse(), totalAmountOfAllCurrencies - 6);

        /* =============== Step 3 =============== */

        tripSummary = getTripByName(tripSummaries, tripName_ERN);
        app.getTripController().deleteTrip(tripSummary);

        result = app.getMiscController().getAllCurrencies();

        assertOrderAndContentContent(result.getCurrenciesMatchingInOrderOfUsage(), new Currency[] {});
        assertOrderAndContentContent(result.getCurrenciesUsedByDate(), new Currency[] { EUR, USD, GBP, TRY });
        assertOrderAndContentContent(result.getCurrenciesInProject(), new Currency[] { BAM });
        assertTailContent(result.getCurrenciesElse(), totalAmountOfAllCurrencies - 5);

        tripSummary = getTripByName(tripSummaries, tripName_BAM);
        app.getTripController().deleteTrip(tripSummary);

        result = app.getMiscController().getAllCurrencies();

        assertOrderAndContentContent(result.getCurrenciesMatchingInOrderOfUsage(), new Currency[] {});
        assertOrderAndContentContent(result.getCurrenciesUsedByDate(), new Currency[] { EUR, USD, GBP, TRY });
        assertOrderAndContentContent(result.getCurrenciesInProject(), new Currency[] {});
        assertTailContent(result.getCurrenciesElse(), totalAmountOfAllCurrencies - 4);

        tripSummary = getTripByName(tripSummaries, tripName_TRY);
        app.getTripController().deleteTrip(tripSummary);

        result = app.getMiscController().getAllCurrencies();

        assertOrderAndContentContent(result.getCurrenciesMatchingInOrderOfUsage(), new Currency[] {});
        assertOrderAndContentContent(result.getCurrenciesUsedByDate(), new Currency[] { EUR, USD, GBP, TRY });
        assertOrderAndContentContent(result.getCurrenciesInProject(), new Currency[] {});
        assertTailContent(result.getCurrenciesElse(), totalAmountOfAllCurrencies - 4);
    }

    private TripSummary getTripByName(List<TripSummary> tripSummaries, String string) {
        for (TripSummary ts : tripSummaries) {
            if (string.equals(ts.getName())) {
                return ts;
            }
        }
        throw new RuntimeException("Invalid");
    }

    private void assertTailContent(List<CurrencyWithName> tail, int expectedSize) {
        Assert.assertEquals(expectedSize, tail.size());

        Assert.assertEquals(
                new CurrencyWithName(Currency.getInstance("ADP"), "whatever"),
                tail.get(0));
        Assert.assertEquals(
                new CurrencyWithName(Currency.getInstance("ZWN"), "whatever"),
                tail.get(expectedSize - 1));
    }

    private void assertOrderAndContentContent(List<CurrencyWithName> resultList,
            Currency[] currenciesInExpectedOrder) {
        Assert.assertEquals(resultList + "", currenciesInExpectedOrder.length, resultList.size());
        for (int i = 0; i < resultList.size(); i++) {
            Assert.assertEquals(currenciesInExpectedOrder[i], resultList.get(i).getCurrency());
        }
    }
}
