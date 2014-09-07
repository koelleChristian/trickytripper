package de.koelle.christian.trickytripper.dataaccess.suite.exchange;

import java.util.Currency;

import junit.framework.Assert;
import de.koelle.christian.trickytripper.model.ExchangeRate;
import de.koelle.christian.trickytripper.model.ImportOrigin;

public class ExchangeRateTestSupport {

    public static final Currency EUR = Currency.getInstance("EUR");
    public static final Currency USD = Currency.getInstance("USD");
    public static final Currency TRY = Currency.getInstance("TRY");
    public static final Currency GBP = Currency.getInstance("GBP");

    public static final Currency BAM = Currency.getInstance("BAM");
    public static final Currency ERN = Currency.getInstance("ERN");

    public static final long ID_1 = 1;
    public static final long ID_2 = 2;
    public static final long ID_3 = 3;
    public static final long ID_4 = 4;
    public static final long ID_5 = 5;
    public static final long ID_6 = 6;
    public static final long ID_7 = 7;
    public static final long ID_8 = 8;

    public static final ExchangeRate REC_01;
    public static final ExchangeRate REC_02;
    public static final ExchangeRate REC_03;
    public static final ExchangeRate REC_04;

    public static final ExchangeRate REC_EUR_USD_01;
    public static final ExchangeRate REC_EUR_USD_02;
    public static final ExchangeRate REC_EUR_USD_03;
    public static final ExchangeRate REC_EUR_USD_04;

    static {
        ExchangeRate input;

        input = new ExchangeRate();
        input.setCurrencyFrom(EUR);
        input.setCurrencyTo(USD);
        input.setDescription("My first exchange rate");
        input.setExchangeRate(0.7539);
        input.setImportOrigin(ImportOrigin.NONE);

        REC_01 = input;

        input = new ExchangeRate();
        input.setCurrencyFrom(EUR);
        input.setCurrencyTo(TRY);
        input.setDescription("My second exchange rate");
        input.setExchangeRate(134.0001);
        input.setImportOrigin(ImportOrigin.NONE);

        REC_02 = input;

        input = new ExchangeRate();
        input.setCurrencyFrom(TRY);
        input.setCurrencyTo(GBP);
        input.setDescription("My third exchange rate");
        input.setExchangeRate(6666.0);
        input.setImportOrigin(ImportOrigin.NONE);

        REC_03 = input;

        input = new ExchangeRate();
        input.setCurrencyFrom(TRY);
        input.setCurrencyTo(GBP);
        input.setDescription("My fourth exchange rate");
        input.setExchangeRate(6666.7777);
        input.setImportOrigin(ImportOrigin.NONE);

        REC_04 = input;

        /**/

        input = new ExchangeRate();
        input.setCurrencyFrom(EUR);
        input.setCurrencyTo(USD);
        input.setDescription("Eur to USD 01");
        input.setExchangeRate(1.55);
        input.setImportOrigin(ImportOrigin.NONE);

        REC_EUR_USD_01 = input;

        input = new ExchangeRate();
        input.setCurrencyFrom(EUR);
        input.setCurrencyTo(USD);
        input.setDescription("Eur to USD 02");
        input.setExchangeRate(1.66);
        input.setImportOrigin(ImportOrigin.NONE);

        REC_EUR_USD_02 = input;

        input = new ExchangeRate();
        input.setCurrencyFrom(EUR);
        input.setCurrencyTo(USD);
        input.setDescription("Eur to USD 03");
        input.setExchangeRate(1.77);
        input.setImportOrigin(ImportOrigin.NONE);

        REC_EUR_USD_03 = input;

        input = new ExchangeRate();
        input.setCurrencyFrom(USD);
        input.setCurrencyTo(EUR);
        input.setDescription("Usd to Eur 01");
        input.setExchangeRate(0.7575);
        input.setImportOrigin(ImportOrigin.NONE);

        REC_EUR_USD_04 = input;
    }

    public static void assertEquality(ExchangeRate input, ExchangeRate output, long expectedId,
            boolean checkDateEquality) {
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
