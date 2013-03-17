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
        getContext().deleteDatabase(DataConstants.DATABASE_NAME);
    }

    public void testGetAllCurrenciesForTargetBlank() {
        TrickyTripperApp app = getApplication();
        HierarchicalCurrencyList result = app.getMiscController().getAllCurrenciesForTarget(USD);

        assertOrderAndContentContent(result.getCurrenciesUsedMatching(), new Currency[] {});
        assertOrderAndContentContent(result.getCurrenciesInExchangeRatesMatching(), new Currency[] {});
        assertOrderAndContentContent(result.getCurrenciesUsedUnmatched(), new Currency[] {});
        assertOrderAndContentContent(result.getCurrenciesInExchangeRatesUnmatched(), new Currency[] {});
        assertOrderAndContentContent(result.getCurrenciesInTrips(), new Currency[] {});
        assertTailContent(result.getCurrenciesElse(), totalAmountOfAllCurrencies);

    }

    public void testgetAllCurrenciesBlank() {
        TrickyTripperApp app = getApplication();
        HierarchicalCurrencyList result = app.getMiscController().getAllCurrencies();

        assertOrderAndContentContent(result.getCurrenciesUsedMatching(), new Currency[] {});
        assertOrderAndContentContent(result.getCurrenciesInExchangeRatesMatching(), new Currency[] {});
        assertOrderAndContentContent(result.getCurrenciesUsedUnmatched(), new Currency[] {});
        assertOrderAndContentContent(result.getCurrenciesInExchangeRatesUnmatched(), new Currency[] {});
        assertOrderAndContentContent(result.getCurrenciesInTrips(), new Currency[] { USD });
        assertTailContent(result.getCurrenciesElse(), totalAmountOfAllCurrencies - 1);
    }

    public void testGetAllCurrenciesForTarget() {

        TrickyTripperApp app = getApplication();
        ExchangeRate rateSaved;
        HierarchicalCurrencyList result;

        /* EUR - USD */
        rateSaved = app.getExchangeRateController().persistExchangeRate(REC_01);
        initalRetrievalResults.put(rateSaved.getId(), rateSaved);
        /* EUR - TRY */
        rateSaved = app.getExchangeRateController().persistExchangeRate(REC_02);
        initalRetrievalResults.put(rateSaved.getId(), rateSaved);
        /* TRY GBP */
        rateSaved = app.getExchangeRateController().persistExchangeRate(REC_03);
        initalRetrievalResults.put(rateSaved.getId(), rateSaved);
        /* TRY-GBP */
        rateSaved = app.getExchangeRateController().persistExchangeRate(REC_04);
        initalRetrievalResults.put(rateSaved.getId(), rateSaved);
        /* EUR-USD */
        rateSaved = app.getExchangeRateController().persistExchangeRate(REC_EUR_USD_01);
        initalRetrievalResults.put(rateSaved.getId(), rateSaved);
        /* EUR-USD */
        rateSaved = app.getExchangeRateController().persistExchangeRate(REC_EUR_USD_02);
        initalRetrievalResults.put(rateSaved.getId(), rateSaved);
        /* EUR-USD */
        rateSaved = app.getExchangeRateController().persistExchangeRate(REC_EUR_USD_03);
        initalRetrievalResults.put(rateSaved.getId(), rateSaved);
        /* USD-EUR */
        rateSaved = app.getExchangeRateController().persistExchangeRate(REC_EUR_USD_04);
        initalRetrievalResults.put(rateSaved.getId(), rateSaved);

        /* =============== Step 1 =============== */

        result = app.getMiscController().getAllCurrenciesForTarget(USD);

        assertOrderAndContentContent(result.getCurrenciesUsedMatching(), new Currency[] {});
        assertOrderAndContentContent(result.getCurrenciesInExchangeRatesMatching(), new Currency[] { EUR });
        assertOrderAndContentContent(result.getCurrenciesUsedUnmatched(), new Currency[] {});
        assertOrderAndContentContent(result.getCurrenciesInExchangeRatesUnmatched(), new Currency[] { EUR, GBP, TRY });
        assertOrderAndContentContent(result.getCurrenciesInTrips(), new Currency[] {});
        assertTailContent(result.getCurrenciesElse(), totalAmountOfAllCurrencies - 1); // USD
                                                                                       // ...

        /* =============== Step 2 =============== */

        /* EUR - TRY */
        app.getExchangeRateController().persistExchangeRateUsedLast(initalRetrievalResults.get(ID_2));
        result = app.getMiscController().getAllCurrenciesForTarget(USD);

        assertOrderAndContentContent(result.getCurrenciesUsedMatching(), new Currency[] {});
        assertOrderAndContentContent(result.getCurrenciesInExchangeRatesMatching(), new Currency[] { EUR });
        assertOrderAndContentContent(result.getCurrenciesUsedUnmatched(), new Currency[] { EUR, TRY });
        assertOrderAndContentContent(result.getCurrenciesInExchangeRatesUnmatched(), new Currency[] { EUR, GBP, TRY });
        assertOrderAndContentContent(result.getCurrenciesInTrips(), new Currency[] {});
        assertTailContent(result.getCurrenciesElse(), totalAmountOfAllCurrencies - 1); // USD
                                                                                       // ...

        /* TRY - GBP */
        app.getExchangeRateController().persistExchangeRateUsedLast(initalRetrievalResults.get(ID_3));
        result = app.getMiscController().getAllCurrenciesForTarget(USD);

        assertOrderAndContentContent(result.getCurrenciesUsedMatching(), new Currency[] {});
        assertOrderAndContentContent(result.getCurrenciesInExchangeRatesMatching(), new Currency[] { EUR });
        assertOrderAndContentContent(result.getCurrenciesUsedUnmatched(), new Currency[] { GBP, TRY, EUR });
        assertOrderAndContentContent(result.getCurrenciesInExchangeRatesUnmatched(), new Currency[] { EUR, GBP, TRY });
        assertOrderAndContentContent(result.getCurrenciesInTrips(), new Currency[] {});
        assertTailContent(result.getCurrenciesElse(), totalAmountOfAllCurrencies - 1);

        /* EUR - TRY */
        app.getExchangeRateController().persistExchangeRateUsedLast(initalRetrievalResults.get(ID_2));
        result = app.getMiscController().getAllCurrenciesForTarget(USD);

        assertOrderAndContentContent(result.getCurrenciesUsedMatching(), new Currency[] {});
        assertOrderAndContentContent(result.getCurrenciesInExchangeRatesMatching(), new Currency[] { EUR });
        assertOrderAndContentContent(result.getCurrenciesUsedUnmatched(), new Currency[] { EUR, TRY, GBP });
        assertOrderAndContentContent(result.getCurrenciesInExchangeRatesUnmatched(), new Currency[] { EUR, GBP, TRY });
        assertOrderAndContentContent(result.getCurrenciesInTrips(), new Currency[] {});
        assertTailContent(result.getCurrenciesElse(), totalAmountOfAllCurrencies - 1);

        /* EURO - USD */
        app.getExchangeRateController().persistExchangeRateUsedLast(initalRetrievalResults.get(ID_1));
        result = app.getMiscController().getAllCurrenciesForTarget(USD);

        assertOrderAndContentContent(result.getCurrenciesUsedMatching(), new Currency[] { EUR });
        assertOrderAndContentContent(result.getCurrenciesInExchangeRatesMatching(), new Currency[] { EUR });
        assertOrderAndContentContent(result.getCurrenciesUsedUnmatched(), new Currency[] { EUR, TRY, GBP });
        assertOrderAndContentContent(result.getCurrenciesInExchangeRatesUnmatched(), new Currency[] { EUR, GBP, TRY });
        assertOrderAndContentContent(result.getCurrenciesInTrips(), new Currency[] {});
        assertTailContent(result.getCurrenciesElse(), totalAmountOfAllCurrencies - 1);

        result = app.getMiscController().getAllCurrenciesForTarget(EUR);

        assertOrderAndContentContent(result.getCurrenciesUsedMatching(), new Currency[] { USD, TRY });
        assertOrderAndContentContent(result.getCurrenciesInExchangeRatesMatching(), new Currency[] { USD, TRY });
        assertOrderAndContentContent(result.getCurrenciesUsedUnmatched(), new Currency[] { USD, TRY, GBP });
        assertOrderAndContentContent(result.getCurrenciesInExchangeRatesUnmatched(), new Currency[] { USD, GBP, TRY });
        assertOrderAndContentContent(result.getCurrenciesInTrips(), new Currency[] { USD });
        assertTailContent(result.getCurrenciesElse(), totalAmountOfAllCurrencies - 1);

        app.getExchangeRateController().persistExchangeRateUsedLast(initalRetrievalResults.get(ID_2));
        result = app.getMiscController().getAllCurrenciesForTarget(EUR);

        assertOrderAndContentContent(result.getCurrenciesUsedMatching(), new Currency[] { TRY, USD });
        assertOrderAndContentContent(result.getCurrenciesInExchangeRatesMatching(), new Currency[] { USD, TRY });
        assertOrderAndContentContent(result.getCurrenciesUsedUnmatched(), new Currency[] { TRY, USD, GBP });
        assertOrderAndContentContent(result.getCurrenciesInExchangeRatesUnmatched(), new Currency[] { USD, GBP, TRY });
        assertOrderAndContentContent(result.getCurrenciesInTrips(), new Currency[] { USD });
        assertTailContent(result.getCurrenciesElse(), totalAmountOfAllCurrencies - 1);

        /* =============== Step 3 =============== */

        app.getTripController().persist(new TripSummary("MyTrip01", BAM));

        result = app.getMiscController().getAllCurrenciesForTarget(EUR);

        assertOrderAndContentContent(result.getCurrenciesUsedMatching(), new Currency[] { TRY, USD });
        assertOrderAndContentContent(result.getCurrenciesInExchangeRatesMatching(), new Currency[] { USD, TRY });
        assertOrderAndContentContent(result.getCurrenciesUsedUnmatched(), new Currency[] { TRY, USD, GBP });
        assertOrderAndContentContent(result.getCurrenciesInExchangeRatesUnmatched(), new Currency[] { USD, GBP, TRY });
        assertOrderAndContentContent(result.getCurrenciesInTrips(), new Currency[] { BAM, USD });
        assertTailContent(result.getCurrenciesElse(), totalAmountOfAllCurrencies - 1);

        app.getTripController().persist(new TripSummary("MyTrip02", ERN));

        result = app.getMiscController().getAllCurrenciesForTarget(EUR);

        assertOrderAndContentContent(result.getCurrenciesUsedMatching(), new Currency[] { TRY, USD });
        assertOrderAndContentContent(result.getCurrenciesInExchangeRatesMatching(), new Currency[] { USD, TRY });
        assertOrderAndContentContent(result.getCurrenciesUsedUnmatched(), new Currency[] { TRY, USD, GBP });
        assertOrderAndContentContent(result.getCurrenciesInExchangeRatesUnmatched(), new Currency[] { USD, GBP, TRY });
        assertOrderAndContentContent(result.getCurrenciesInTrips(), new Currency[] { BAM, ERN, USD });
        assertTailContent(result.getCurrenciesElse(), totalAmountOfAllCurrencies - 1);

    }

    public void testgetAllCurrencies() {

        TrickyTripperApp app = getApplication();
        ExchangeRate rateSaved;
        HierarchicalCurrencyList result;

        /* EUR - USD */
        rateSaved = app.getExchangeRateController().persistExchangeRate(REC_01);
        initalRetrievalResults.put(rateSaved.getId(), rateSaved);
        /* EUR - TRY */
        rateSaved = app.getExchangeRateController().persistExchangeRate(REC_02);
        initalRetrievalResults.put(rateSaved.getId(), rateSaved);
        /* TRY GBP */
        rateSaved = app.getExchangeRateController().persistExchangeRate(REC_03);
        initalRetrievalResults.put(rateSaved.getId(), rateSaved);
        /* TRY-GBP */
        rateSaved = app.getExchangeRateController().persistExchangeRate(REC_04);
        initalRetrievalResults.put(rateSaved.getId(), rateSaved);
        /* EUR-USD */
        rateSaved = app.getExchangeRateController().persistExchangeRate(REC_EUR_USD_01);
        initalRetrievalResults.put(rateSaved.getId(), rateSaved);
        /* EUR-USD */
        rateSaved = app.getExchangeRateController().persistExchangeRate(REC_EUR_USD_02);
        initalRetrievalResults.put(rateSaved.getId(), rateSaved);
        /* EUR-USD */
        rateSaved = app.getExchangeRateController().persistExchangeRate(REC_EUR_USD_03);
        initalRetrievalResults.put(rateSaved.getId(), rateSaved);
        /* USD-EUR */
        rateSaved = app.getExchangeRateController().persistExchangeRate(REC_EUR_USD_04);
        initalRetrievalResults.put(rateSaved.getId(), rateSaved);

        /* =============== Step 1 =============== */
        result = app.getMiscController().getAllCurrencies();

        assertOrderAndContentContent(result.getCurrenciesUsedMatching(), new Currency[] {});
        assertOrderAndContentContent(result.getCurrenciesInExchangeRatesMatching(), new Currency[] {});
        assertOrderAndContentContent(result.getCurrenciesUsedUnmatched(), new Currency[] {});
        assertOrderAndContentContent(result.getCurrenciesInExchangeRatesUnmatched(), new Currency[] {
                EUR, USD, GBP, TRY });
        assertOrderAndContentContent(result.getCurrenciesInTrips(), new Currency[] { USD });
        assertTailContent(result.getCurrenciesElse(), totalAmountOfAllCurrencies);

        /* =============== Step 2 =============== */

        /* EUR - TRY */
        app.getExchangeRateController().persistExchangeRateUsedLast(initalRetrievalResults.get(ID_2));
        result = app.getMiscController().getAllCurrencies();

        assertOrderAndContentContent(result.getCurrenciesUsedMatching(), new Currency[] {});
        assertOrderAndContentContent(result.getCurrenciesInExchangeRatesMatching(), new Currency[] {});
        assertOrderAndContentContent(result.getCurrenciesUsedUnmatched(), new Currency[] { EUR, TRY });
        assertOrderAndContentContent(result.getCurrenciesInExchangeRatesUnmatched(), new Currency[] {
                EUR, USD, GBP, TRY });
        assertOrderAndContentContent(result.getCurrenciesInTrips(), new Currency[] { USD });
        assertTailContent(result.getCurrenciesElse(), totalAmountOfAllCurrencies);

        /* TRY - GBP */
        app.getExchangeRateController().persistExchangeRateUsedLast(initalRetrievalResults.get(ID_3));
        result = app.getMiscController().getAllCurrencies();

        assertOrderAndContentContent(result.getCurrenciesUsedMatching(), new Currency[] {});
        assertOrderAndContentContent(result.getCurrenciesInExchangeRatesMatching(), new Currency[] {});
        assertOrderAndContentContent(result.getCurrenciesUsedUnmatched(), new Currency[] { GBP, TRY, EUR });
        assertOrderAndContentContent(result.getCurrenciesInExchangeRatesUnmatched(), new Currency[] {
                EUR, USD, GBP, TRY });
        assertOrderAndContentContent(result.getCurrenciesInTrips(), new Currency[] { USD });
        assertTailContent(result.getCurrenciesElse(), totalAmountOfAllCurrencies);

        /* EUR - TRY */
        app.getExchangeRateController().persistExchangeRateUsedLast(initalRetrievalResults.get(ID_2));
        result = app.getMiscController().getAllCurrencies();

        assertOrderAndContentContent(result.getCurrenciesUsedMatching(), new Currency[] {});
        assertOrderAndContentContent(result.getCurrenciesInExchangeRatesMatching(), new Currency[] {});
        assertOrderAndContentContent(result.getCurrenciesUsedUnmatched(), new Currency[] { EUR, TRY, GBP });
        assertOrderAndContentContent(result.getCurrenciesInExchangeRatesUnmatched(), new Currency[] {
                EUR, USD, GBP, TRY });
        assertOrderAndContentContent(result.getCurrenciesInTrips(), new Currency[] { USD });
        assertTailContent(result.getCurrenciesElse(), totalAmountOfAllCurrencies);

        /* EURO - USD */
        app.getExchangeRateController().persistExchangeRateUsedLast(initalRetrievalResults.get(ID_1));
        result = app.getMiscController().getAllCurrencies();

        assertOrderAndContentContent(result.getCurrenciesUsedMatching(), new Currency[] {});
        assertOrderAndContentContent(result.getCurrenciesInExchangeRatesMatching(), new Currency[] {});
        assertOrderAndContentContent(result.getCurrenciesUsedUnmatched(), new Currency[] { EUR, USD, TRY, GBP });
        assertOrderAndContentContent(result.getCurrenciesInExchangeRatesUnmatched(), new Currency[] {
                EUR, USD, GBP, TRY });
        assertOrderAndContentContent(result.getCurrenciesInTrips(), new Currency[] { USD });
        assertTailContent(result.getCurrenciesElse(), totalAmountOfAllCurrencies);

        app.getExchangeRateController().persistExchangeRateUsedLast(initalRetrievalResults.get(ID_2));
        result = app.getMiscController().getAllCurrencies();

        assertOrderAndContentContent(result.getCurrenciesUsedMatching(), new Currency[] {});
        assertOrderAndContentContent(result.getCurrenciesInExchangeRatesMatching(), new Currency[] {});
        assertOrderAndContentContent(result.getCurrenciesUsedUnmatched(), new Currency[] { EUR, TRY, USD, GBP });
        assertOrderAndContentContent(result.getCurrenciesInExchangeRatesUnmatched(), new Currency[] {
                EUR, USD, GBP, TRY });
        assertOrderAndContentContent(result.getCurrenciesInTrips(), new Currency[] { USD });
        assertTailContent(result.getCurrenciesElse(), totalAmountOfAllCurrencies);

        /* =============== Step 3 =============== */

        app.getTripController().persist(new TripSummary("MyTrip01", BAM));

        result = app.getMiscController().getAllCurrencies();

        assertOrderAndContentContent(result.getCurrenciesUsedMatching(), new Currency[] {});
        assertOrderAndContentContent(result.getCurrenciesInExchangeRatesMatching(), new Currency[] {});
        assertOrderAndContentContent(result.getCurrenciesUsedUnmatched(), new Currency[] { EUR, TRY, USD, GBP });
        assertOrderAndContentContent(result.getCurrenciesInExchangeRatesUnmatched(), new Currency[] {
                EUR, USD, GBP, TRY });
        assertOrderAndContentContent(result.getCurrenciesInTrips(), new Currency[] { BAM, USD });
        assertTailContent(result.getCurrenciesElse(), totalAmountOfAllCurrencies);

        app.getTripController().persist(new TripSummary("MyTrip02", ERN));

        result = app.getMiscController().getAllCurrencies();

        assertOrderAndContentContent(result.getCurrenciesUsedMatching(), new Currency[] {});
        assertOrderAndContentContent(result.getCurrenciesInExchangeRatesMatching(), new Currency[] {});
        assertOrderAndContentContent(result.getCurrenciesUsedUnmatched(), new Currency[] { EUR, TRY, USD, GBP });
        assertOrderAndContentContent(result.getCurrenciesInExchangeRatesUnmatched(), new Currency[] {
                EUR, USD, GBP, TRY });
        assertOrderAndContentContent(result.getCurrenciesInTrips(), new Currency[] { BAM, ERN, USD });
        assertTailContent(result.getCurrenciesElse(), totalAmountOfAllCurrencies);
    }

    public void testgetAllCurrenciesForTargetDeleteExchangeRates() {

        TrickyTripperApp app = getApplication();
        ExchangeRate rateSaved;
        HierarchicalCurrencyList result;

        /* EUR - USD */
        rateSaved = app.getExchangeRateController().persistExchangeRate(REC_01);
        initalRetrievalResults.put(rateSaved.getId(), rateSaved);
        /* EUR - TRY */
        rateSaved = app.getExchangeRateController().persistExchangeRate(REC_02);
        initalRetrievalResults.put(rateSaved.getId(), rateSaved);
        /* TRY GBP */
        rateSaved = app.getExchangeRateController().persistExchangeRate(REC_03);
        initalRetrievalResults.put(rateSaved.getId(), rateSaved);
        /* TRY-GBP */
        rateSaved = app.getExchangeRateController().persistExchangeRate(REC_04);
        initalRetrievalResults.put(rateSaved.getId(), rateSaved);
        /* EUR-USD */
        rateSaved = app.getExchangeRateController().persistExchangeRate(REC_EUR_USD_01);
        initalRetrievalResults.put(rateSaved.getId(), rateSaved);
        /* EUR-USD */
        rateSaved = app.getExchangeRateController().persistExchangeRate(REC_EUR_USD_02);
        initalRetrievalResults.put(rateSaved.getId(), rateSaved);
        /* EUR-USD */
        rateSaved = app.getExchangeRateController().persistExchangeRate(REC_EUR_USD_03);
        initalRetrievalResults.put(rateSaved.getId(), rateSaved);
        /* USD-EUR */
        rateSaved = app.getExchangeRateController().persistExchangeRate(REC_EUR_USD_04);
        initalRetrievalResults.put(rateSaved.getId(), rateSaved);

        /* =============== Step 1 =============== */
        /* EUR - TRY */
        app.getExchangeRateController().persistExchangeRateUsedLast(initalRetrievalResults.get(ID_2));
        /* TRY - GBP */
        app.getExchangeRateController().persistExchangeRateUsedLast(initalRetrievalResults.get(ID_3));

        app.getExchangeRateController().deleteExchangeRates(
                Arrays.asList(new ExchangeRate[] { initalRetrievalResults.get(ID_2) }));

        result = app.getMiscController().getAllCurrenciesForTarget(USD);

        assertOrderAndContentContent(result.getCurrenciesUsedMatching(), new Currency[] {});
        assertOrderAndContentContent(result.getCurrenciesInExchangeRatesMatching(), new Currency[] { EUR });
        assertOrderAndContentContent(result.getCurrenciesUsedUnmatched(), new Currency[] { GBP, TRY });
        assertOrderAndContentContent(result.getCurrenciesInExchangeRatesUnmatched(), new Currency[] {
                EUR, GBP, TRY });
        assertOrderAndContentContent(result.getCurrenciesInTrips(), new Currency[] {});
        assertTailContent(result.getCurrenciesElse(), totalAmountOfAllCurrencies - 1);

        app.getExchangeRateController().deleteExchangeRates(
                Arrays.asList(new ExchangeRate[] { initalRetrievalResults.get(ID_3) }));

        result = app.getMiscController().getAllCurrenciesForTarget(USD);

        assertOrderAndContentContent(result.getCurrenciesUsedMatching(), new Currency[] {});
        assertOrderAndContentContent(result.getCurrenciesInExchangeRatesMatching(), new Currency[] { EUR });
        assertOrderAndContentContent(result.getCurrenciesUsedUnmatched(), new Currency[] {});
        assertOrderAndContentContent(result.getCurrenciesInExchangeRatesUnmatched(), new Currency[] {
                EUR, GBP, TRY });
        assertOrderAndContentContent(result.getCurrenciesInTrips(), new Currency[] {});
        assertTailContent(result.getCurrenciesElse(), totalAmountOfAllCurrencies - 1);

    }

    public void testgetAllCurrenciesDeleteExchangeRates() {

        TrickyTripperApp app = getApplication();
        ExchangeRate rateSaved;
        HierarchicalCurrencyList result;

        /* EUR - USD */
        rateSaved = app.getExchangeRateController().persistExchangeRate(REC_01);
        initalRetrievalResults.put(rateSaved.getId(), rateSaved);
        /* EUR - TRY */
        rateSaved = app.getExchangeRateController().persistExchangeRate(REC_02);
        initalRetrievalResults.put(rateSaved.getId(), rateSaved);
        /* TRY GBP */
        rateSaved = app.getExchangeRateController().persistExchangeRate(REC_03);
        initalRetrievalResults.put(rateSaved.getId(), rateSaved);
        /* TRY-GBP */
        rateSaved = app.getExchangeRateController().persistExchangeRate(REC_04);
        initalRetrievalResults.put(rateSaved.getId(), rateSaved);
        /* EUR-USD */
        rateSaved = app.getExchangeRateController().persistExchangeRate(REC_EUR_USD_01);
        initalRetrievalResults.put(rateSaved.getId(), rateSaved);
        /* EUR-USD */
        rateSaved = app.getExchangeRateController().persistExchangeRate(REC_EUR_USD_02);
        initalRetrievalResults.put(rateSaved.getId(), rateSaved);
        /* EUR-USD */
        rateSaved = app.getExchangeRateController().persistExchangeRate(REC_EUR_USD_03);
        initalRetrievalResults.put(rateSaved.getId(), rateSaved);
        /* USD-EUR */
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

        assertOrderAndContentContent(result.getCurrenciesUsedMatching(), new Currency[] {});
        assertOrderAndContentContent(result.getCurrenciesInExchangeRatesMatching(), new Currency[] {});
        assertOrderAndContentContent(result.getCurrenciesUsedUnmatched(), new Currency[] { GBP, TRY });
        assertOrderAndContentContent(result.getCurrenciesInExchangeRatesUnmatched(), new Currency[] {
                EUR, USD, GBP, TRY });
        assertOrderAndContentContent(result.getCurrenciesInTrips(), new Currency[] { USD });
        assertTailContent(result.getCurrenciesElse(), totalAmountOfAllCurrencies);

        app.getExchangeRateController().deleteExchangeRates(
                Arrays.asList(new ExchangeRate[] { initalRetrievalResults.get(ID_3) }));

        result = app.getMiscController().getAllCurrencies();

        assertOrderAndContentContent(result.getCurrenciesUsedMatching(), new Currency[] {});
        assertOrderAndContentContent(result.getCurrenciesInExchangeRatesMatching(), new Currency[] {});
        assertOrderAndContentContent(result.getCurrenciesUsedUnmatched(), new Currency[] {});
        assertOrderAndContentContent(result.getCurrenciesInExchangeRatesUnmatched(), new Currency[] {
                EUR, USD, GBP, TRY });
        assertOrderAndContentContent(result.getCurrenciesInTrips(), new Currency[] { USD });
        assertTailContent(result.getCurrenciesElse(), totalAmountOfAllCurrencies);

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

        assertOrderAndContentContent(result.getCurrenciesUsedMatching(), new Currency[] {});
        assertOrderAndContentContent(result.getCurrenciesInExchangeRatesMatching(), new Currency[] {});
        assertOrderAndContentContent(result.getCurrenciesUsedUnmatched(), new Currency[] {});
        assertOrderAndContentContent(result.getCurrenciesInExchangeRatesUnmatched(), new Currency[] {});
        assertOrderAndContentContent(result.getCurrenciesInTrips(), new Currency[] { BAM });
        assertTailContent(result.getCurrenciesElse(), totalAmountOfAllCurrencies - 1);

        app.getTripController().persist(new TripSummary(tripName_ERN, ERN));

        result = app.getMiscController().getAllCurrenciesForTarget(USD);

        assertOrderAndContentContent(result.getCurrenciesUsedMatching(), new Currency[] {});
        assertOrderAndContentContent(result.getCurrenciesInExchangeRatesMatching(), new Currency[] {});
        assertOrderAndContentContent(result.getCurrenciesUsedUnmatched(), new Currency[] {});
        assertOrderAndContentContent(result.getCurrenciesInExchangeRatesUnmatched(), new Currency[] {});
        assertOrderAndContentContent(result.getCurrenciesInTrips(), new Currency[] { BAM, ERN });
        assertTailContent(result.getCurrenciesElse(), totalAmountOfAllCurrencies - 1);

        app.getTripController().persist(new TripSummary(tripName_TRY, TRY));
        app.getTripController().persist(new TripSummary(tripName_EUR, EUR));

        result = app.getMiscController().getAllCurrenciesForTarget(USD);

        assertOrderAndContentContent(result.getCurrenciesUsedMatching(), new Currency[] {});
        assertOrderAndContentContent(result.getCurrenciesInExchangeRatesMatching(), new Currency[] {});
        assertOrderAndContentContent(result.getCurrenciesUsedUnmatched(), new Currency[] {});
        assertOrderAndContentContent(result.getCurrenciesInExchangeRatesUnmatched(), new Currency[] {});
        assertOrderAndContentContent(result.getCurrenciesInTrips(), new Currency[] { BAM, ERN, EUR, TRY });
        assertTailContent(result.getCurrenciesElse(), totalAmountOfAllCurrencies - 1);

        result = app.getMiscController().getAllCurrenciesForTarget(EUR);

        assertOrderAndContentContent(result.getCurrenciesUsedMatching(), new Currency[] {});
        assertOrderAndContentContent(result.getCurrenciesInExchangeRatesMatching(), new Currency[] {});
        assertOrderAndContentContent(result.getCurrenciesUsedUnmatched(), new Currency[] {});
        assertOrderAndContentContent(result.getCurrenciesInExchangeRatesUnmatched(), new Currency[] {});
        assertOrderAndContentContent(result.getCurrenciesInTrips(), new Currency[] { BAM, ERN, TRY, USD });
        assertTailContent(result.getCurrenciesElse(), totalAmountOfAllCurrencies - 1);

        List<TripSummary> tripSummaries = app.getTripController().getAllTrips();

        tripSummary = getTripByName(tripSummaries, tripName_TRY);
        app.getTripController().deleteTrip(tripSummary);

        result = app.getMiscController().getAllCurrenciesForTarget(EUR);

        assertOrderAndContentContent(result.getCurrenciesUsedMatching(), new Currency[] {});
        assertOrderAndContentContent(result.getCurrenciesInExchangeRatesMatching(), new Currency[] {});
        assertOrderAndContentContent(result.getCurrenciesUsedUnmatched(), new Currency[] {});
        assertOrderAndContentContent(result.getCurrenciesInExchangeRatesUnmatched(), new Currency[] {});
        assertOrderAndContentContent(result.getCurrenciesInTrips(), new Currency[] { BAM, ERN, USD });
        assertTailContent(result.getCurrenciesElse(), totalAmountOfAllCurrencies - 1);

        app.getTripController().persist(new TripSummary(tripName_TRY, TRY));

        /* =============== Step 2 =============== */

        /* TRY - GBP */
        rateSaved = app.getExchangeRateController().persistExchangeRate(REC_03);
        initalRetrievalResults.put(rateSaved.getId(), rateSaved);
        app.getExchangeRateController().persistExchangeRateUsedLast(initalRetrievalResults.get(rateSaved.getId()));

        result = app.getMiscController().getAllCurrenciesForTarget(EUR);

        assertOrderAndContentContent(result.getCurrenciesUsedMatching(), new Currency[] {});
        assertOrderAndContentContent(result.getCurrenciesInExchangeRatesMatching(), new Currency[] {});
        assertOrderAndContentContent(result.getCurrenciesUsedUnmatched(), new Currency[] { GBP, TRY });
        assertOrderAndContentContent(result.getCurrenciesInExchangeRatesUnmatched(), new Currency[] { GBP, TRY });
        assertOrderAndContentContent(result.getCurrenciesInTrips(), new Currency[] { BAM, ERN, TRY, USD });
        assertTailContent(result.getCurrenciesElse(), totalAmountOfAllCurrencies - 1);

        /* EURO - USD */
        rateSaved = app.getExchangeRateController().persistExchangeRate(REC_01);
        initalRetrievalResults.put(rateSaved.getId(), rateSaved);
        app.getExchangeRateController().persistExchangeRateUsedLast(initalRetrievalResults.get(rateSaved.getId()));

        result = app.getMiscController().getAllCurrenciesForTarget(EUR);

        assertOrderAndContentContent(result.getCurrenciesUsedMatching(), new Currency[] { USD });
        assertOrderAndContentContent(result.getCurrenciesInExchangeRatesMatching(), new Currency[] { USD });
        assertOrderAndContentContent(result.getCurrenciesUsedUnmatched(), new Currency[] { USD, GBP, TRY });
        assertOrderAndContentContent(result.getCurrenciesInExchangeRatesUnmatched(), new Currency[] { USD, GBP, TRY });
        assertOrderAndContentContent(result.getCurrenciesInTrips(), new Currency[] { BAM, ERN, TRY, USD });
        assertTailContent(result.getCurrenciesElse(), totalAmountOfAllCurrencies - 1);

        result = app.getMiscController().getAllCurrenciesForTarget(BAM);

        assertOrderAndContentContent(result.getCurrenciesUsedMatching(), new Currency[] {});
        assertOrderAndContentContent(result.getCurrenciesInExchangeRatesMatching(), new Currency[] {});
        assertOrderAndContentContent(result.getCurrenciesUsedUnmatched(), new Currency[] { EUR, USD, GBP, TRY });
        assertOrderAndContentContent(result.getCurrenciesInExchangeRatesUnmatched(), new Currency[] {
                EUR, USD, GBP, TRY });
        assertOrderAndContentContent(result.getCurrenciesInTrips(), new Currency[] { ERN, EUR, TRY, USD });
        assertTailContent(result.getCurrenciesElse(), totalAmountOfAllCurrencies - 1);

        /* =============== Step 3 =============== */

        tripSummaries = app.getTripController().getAllTrips();
        tripSummary = getTripByName(tripSummaries, tripName_ERN);
        app.getTripController().deleteTrip(tripSummary);

        result = app.getMiscController().getAllCurrenciesForTarget(BAM);

        assertOrderAndContentContent(result.getCurrenciesUsedMatching(), new Currency[] {});
        assertOrderAndContentContent(result.getCurrenciesInExchangeRatesMatching(), new Currency[] {});
        assertOrderAndContentContent(result.getCurrenciesUsedUnmatched(), new Currency[] { EUR, USD, GBP, TRY });
        assertOrderAndContentContent(result.getCurrenciesInExchangeRatesUnmatched(), new Currency[] {
                EUR, USD, GBP, TRY });
        assertOrderAndContentContent(result.getCurrenciesInTrips(), new Currency[] { EUR, TRY, USD });
        assertTailContent(result.getCurrenciesElse(), totalAmountOfAllCurrencies - 1);

        tripSummaries = app.getTripController().getAllTrips();
        tripSummary = getTripByName(tripSummaries, tripName_BAM);
        app.getTripController().deleteTrip(tripSummary);

        result = app.getMiscController().getAllCurrenciesForTarget(BAM);

        assertOrderAndContentContent(result.getCurrenciesUsedMatching(), new Currency[] {});
        assertOrderAndContentContent(result.getCurrenciesInExchangeRatesMatching(), new Currency[] {});
        assertOrderAndContentContent(result.getCurrenciesUsedUnmatched(), new Currency[] { EUR, USD, GBP, TRY });
        assertOrderAndContentContent(result.getCurrenciesInExchangeRatesUnmatched(), new Currency[] {
                EUR, USD, GBP, TRY });
        assertOrderAndContentContent(result.getCurrenciesInTrips(), new Currency[] { EUR, TRY, USD });
        assertTailContent(result.getCurrenciesElse(), totalAmountOfAllCurrencies - 1);

        tripSummaries = app.getTripController().getAllTrips();
        tripSummary = getTripByName(tripSummaries, tripName_TRY);
        app.getTripController().deleteTrip(tripSummary);

        result = app.getMiscController().getAllCurrenciesForTarget(BAM);

        assertOrderAndContentContent(result.getCurrenciesUsedMatching(), new Currency[] {});
        assertOrderAndContentContent(result.getCurrenciesInExchangeRatesMatching(), new Currency[] {});
        assertOrderAndContentContent(result.getCurrenciesUsedUnmatched(), new Currency[] { EUR, USD, GBP, TRY });
        assertOrderAndContentContent(result.getCurrenciesInExchangeRatesUnmatched(), new Currency[] {
                EUR, USD, GBP, TRY });
        assertOrderAndContentContent(result.getCurrenciesInTrips(), new Currency[] { EUR, USD });
        assertTailContent(result.getCurrenciesElse(), totalAmountOfAllCurrencies - 1);
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

        assertOrderAndContentContent(result.getCurrenciesUsedMatching(), new Currency[] {});
        assertOrderAndContentContent(result.getCurrenciesInExchangeRatesMatching(), new Currency[] {});
        assertOrderAndContentContent(result.getCurrenciesUsedUnmatched(), new Currency[] {});
        assertOrderAndContentContent(result.getCurrenciesInExchangeRatesUnmatched(), new Currency[] {});
        assertOrderAndContentContent(result.getCurrenciesInTrips(), new Currency[] { BAM, USD });
        assertTailContent(result.getCurrenciesElse(), totalAmountOfAllCurrencies);

        app.getTripController().persist(new TripSummary(tripName_ERN, ERN));

        result = app.getMiscController().getAllCurrencies();

        assertOrderAndContentContent(result.getCurrenciesUsedMatching(), new Currency[] {});
        assertOrderAndContentContent(result.getCurrenciesInExchangeRatesMatching(), new Currency[] {});
        assertOrderAndContentContent(result.getCurrenciesUsedUnmatched(), new Currency[] {});
        assertOrderAndContentContent(result.getCurrenciesInExchangeRatesUnmatched(), new Currency[] {});
        assertOrderAndContentContent(result.getCurrenciesInTrips(), new Currency[] { BAM, ERN, USD });
        assertTailContent(result.getCurrenciesElse(), totalAmountOfAllCurrencies);

        app.getTripController().persist(new TripSummary(tripName_TRY, TRY));
        app.getTripController().persist(new TripSummary(tripName_EUR, EUR));

        result = app.getMiscController().getAllCurrencies();

        assertOrderAndContentContent(result.getCurrenciesUsedMatching(), new Currency[] {});
        assertOrderAndContentContent(result.getCurrenciesInExchangeRatesMatching(), new Currency[] {});
        assertOrderAndContentContent(result.getCurrenciesUsedUnmatched(), new Currency[] {});
        assertOrderAndContentContent(result.getCurrenciesInExchangeRatesUnmatched(), new Currency[] {});
        assertOrderAndContentContent(result.getCurrenciesInTrips(), new Currency[] { BAM, ERN, EUR, TRY, USD });
        assertTailContent(result.getCurrenciesElse(), totalAmountOfAllCurrencies);

        List<TripSummary> tripSummaries = app.getTripController().getAllTrips();

        tripSummary = getTripByName(tripSummaries, tripName_TRY);
        app.getTripController().deleteTrip(tripSummary);

        result = app.getMiscController().getAllCurrencies();

        assertOrderAndContentContent(result.getCurrenciesUsedMatching(), new Currency[] {});
        assertOrderAndContentContent(result.getCurrenciesInExchangeRatesMatching(), new Currency[] {});
        assertOrderAndContentContent(result.getCurrenciesUsedUnmatched(), new Currency[] {});
        assertOrderAndContentContent(result.getCurrenciesInExchangeRatesUnmatched(), new Currency[] {});
        assertOrderAndContentContent(result.getCurrenciesInTrips(), new Currency[] { BAM, ERN, EUR, USD });
        assertTailContent(result.getCurrenciesElse(), totalAmountOfAllCurrencies);

        app.getTripController().persist(new TripSummary(tripName_TRY, TRY));

        result = app.getMiscController().getAllCurrencies();

        assertOrderAndContentContent(result.getCurrenciesUsedMatching(), new Currency[] {});
        assertOrderAndContentContent(result.getCurrenciesInExchangeRatesMatching(), new Currency[] {});
        assertOrderAndContentContent(result.getCurrenciesUsedUnmatched(), new Currency[] {});
        assertOrderAndContentContent(result.getCurrenciesInExchangeRatesUnmatched(), new Currency[] {});
        assertOrderAndContentContent(result.getCurrenciesInTrips(), new Currency[] { BAM, ERN, EUR, TRY, USD });
        assertTailContent(result.getCurrenciesElse(), totalAmountOfAllCurrencies);

        /* =============== Step 2 =============== */

        /* TRY - GBP */
        rateSaved = app.getExchangeRateController().persistExchangeRate(REC_03);
        initalRetrievalResults.put(rateSaved.getId(), rateSaved);
        app.getExchangeRateController().persistExchangeRateUsedLast(initalRetrievalResults.get(rateSaved.getId()));

        result = app.getMiscController().getAllCurrencies();

        assertOrderAndContentContent(result.getCurrenciesUsedMatching(), new Currency[] {});
        assertOrderAndContentContent(result.getCurrenciesInExchangeRatesMatching(), new Currency[] {});
        assertOrderAndContentContent(result.getCurrenciesUsedUnmatched(), new Currency[] { GBP, TRY });
        assertOrderAndContentContent(result.getCurrenciesInExchangeRatesUnmatched(), new Currency[] { GBP, TRY });
        assertOrderAndContentContent(result.getCurrenciesInTrips(), new Currency[] { BAM, ERN, EUR, TRY, USD });
        assertTailContent(result.getCurrenciesElse(), totalAmountOfAllCurrencies);

        /* EURO - USD */
        rateSaved = app.getExchangeRateController().persistExchangeRate(REC_01);
        initalRetrievalResults.put(rateSaved.getId(), rateSaved);
        app.getExchangeRateController().persistExchangeRateUsedLast(initalRetrievalResults.get(rateSaved.getId()));

        result = app.getMiscController().getAllCurrencies();

        assertOrderAndContentContent(result.getCurrenciesUsedMatching(), new Currency[] {});
        assertOrderAndContentContent(result.getCurrenciesInExchangeRatesMatching(), new Currency[] {});
        assertOrderAndContentContent(result.getCurrenciesUsedUnmatched(), new Currency[] { EUR, USD, GBP, TRY });
        assertOrderAndContentContent(result.getCurrenciesInExchangeRatesUnmatched(), new Currency[] {
                EUR, USD, GBP, TRY });
        assertOrderAndContentContent(result.getCurrenciesInTrips(), new Currency[] { BAM, ERN, EUR, TRY, USD });
        assertTailContent(result.getCurrenciesElse(), totalAmountOfAllCurrencies);

        /* =============== Step 3 =============== */

        tripSummaries = app.getTripController().getAllTrips();
        tripSummary = getTripByName(tripSummaries, tripName_ERN);
        app.getTripController().deleteTrip(tripSummary);

        result = app.getMiscController().getAllCurrencies();

        assertOrderAndContentContent(result.getCurrenciesUsedMatching(), new Currency[] {});
        assertOrderAndContentContent(result.getCurrenciesInExchangeRatesMatching(), new Currency[] {});
        assertOrderAndContentContent(result.getCurrenciesUsedUnmatched(), new Currency[] { EUR, USD, GBP, TRY });
        assertOrderAndContentContent(result.getCurrenciesInExchangeRatesUnmatched(), new Currency[] {
                EUR, USD, GBP, TRY });
        assertOrderAndContentContent(result.getCurrenciesInTrips(), new Currency[] { BAM, EUR, TRY, USD });
        assertTailContent(result.getCurrenciesElse(), totalAmountOfAllCurrencies);

        tripSummaries = app.getTripController().getAllTrips();
        tripSummary = getTripByName(tripSummaries, tripName_BAM);
        app.getTripController().deleteTrip(tripSummary);

        result = app.getMiscController().getAllCurrencies();

        assertOrderAndContentContent(result.getCurrenciesUsedMatching(), new Currency[] {});
        assertOrderAndContentContent(result.getCurrenciesInExchangeRatesMatching(), new Currency[] {});
        assertOrderAndContentContent(result.getCurrenciesUsedUnmatched(), new Currency[] { EUR, USD, GBP, TRY });
        assertOrderAndContentContent(result.getCurrenciesInExchangeRatesUnmatched(), new Currency[] {
                EUR, USD, GBP, TRY });
        assertOrderAndContentContent(result.getCurrenciesInTrips(), new Currency[] { EUR, TRY, USD });
        assertTailContent(result.getCurrenciesElse(), totalAmountOfAllCurrencies);

        tripSummaries = app.getTripController().getAllTrips();
        tripSummary = getTripByName(tripSummaries, tripName_TRY);
        app.getTripController().deleteTrip(tripSummary);

        result = app.getMiscController().getAllCurrencies();

        assertOrderAndContentContent(result.getCurrenciesUsedMatching(), new Currency[] {});
        assertOrderAndContentContent(result.getCurrenciesInExchangeRatesMatching(), new Currency[] {});
        assertOrderAndContentContent(result.getCurrenciesUsedUnmatched(), new Currency[] { EUR, USD, GBP, TRY });
        assertOrderAndContentContent(result.getCurrenciesInExchangeRatesUnmatched(), new Currency[] {
                EUR, USD, GBP, TRY });
        assertOrderAndContentContent(result.getCurrenciesInTrips(), new Currency[] { EUR, USD });
        assertTailContent(result.getCurrenciesElse(), totalAmountOfAllCurrencies);
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
