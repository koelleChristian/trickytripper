package de.koelle.christian.trickytripper.controller.impl;

import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

import android.content.Context;
import de.koelle.christian.trickytripper.controller.ExchangeRateController;
import de.koelle.christian.trickytripper.dataaccess.DataManager;
import de.koelle.christian.trickytripper.model.ExchangeRate;
import de.koelle.christian.trickytripper.model.ExchangeRateResult;

public class ExchangeRateControllerImpl implements ExchangeRateController {

    private final List<ExchangeRate> exchangeRateMockResult = new ArrayList<ExchangeRate>();

    public ExchangeRateControllerImpl(Context baseContext, DataManager dataManager) {
        // TODO Auto-generated constructor stub
    }

    public ExchangeRateResult findSuitableRates(Currency currencyFrom, Currency currencyTo) {
        List<ExchangeRate> exchangeRateMockResult2 = filter(currencyTo);
        return new ExchangeRateResult(exchangeRateMockResult2, exchangeRateMockResult.get(0));
    }

    public List<ExchangeRate> getAllExchangeRatesWithoutInversion() {
        return exchangeRateMockResult;
    }

    public void persistExchangeRates(List<ExchangeRate> rates) {
        for (ExchangeRate rate : rates) {
            System.out.println(rate);
        }
    }

    public boolean deleteExchangeRate(ExchangeRate row) {
        for (ExchangeRate rate : exchangeRateMockResult) {
            if (row.getId() == rate.getId()) {
                exchangeRateMockResult.remove(rate);
                return true;
            }
        }
        throw new RuntimeException("should not be here.");
    }

    public boolean persistExchangeRate(ExchangeRate rate) {
        // TODO Auto-generated method stub
        return false;
    }

    public void saveExchangeRate(ExchangeRate exchangeRate) {
        exchangeRate.setId(exchangeRateMockResult.size() + 1);
        exchangeRateMockResult.add(exchangeRate);
    }

    public ExchangeRate importExchangeRate(Currency currrencyFrom, Currency currencyTo) {
        // TODO Auto-generated method stub
        return null;
    }

    public Currency getSourceCurrencyUsedLast() {
        return Currency.getInstance("TRY");
    }

    private List<ExchangeRate> filter(Currency currencyTo) {
        List<ExchangeRate> result = new ArrayList<ExchangeRate>();
        for (ExchangeRate rate : exchangeRateMockResult) {
            if (currencyTo.getCurrencyCode().equals(rate.getCurrencyTo())) {
                result.add(rate);
            }
        }
        return result;
    }
}
