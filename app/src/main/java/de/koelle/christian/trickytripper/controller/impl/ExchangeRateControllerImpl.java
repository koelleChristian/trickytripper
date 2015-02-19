package de.koelle.christian.trickytripper.controller.impl;

import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

import de.koelle.christian.trickytripper.apputils.PrefWriterReaderUtils;
import de.koelle.christian.trickytripper.controller.ExchangeRateController;
import de.koelle.christian.trickytripper.dataaccess.DataManager;
import de.koelle.christian.trickytripper.decoupling.PrefsResolver;
import de.koelle.christian.trickytripper.model.ExchangeRate;
import de.koelle.christian.trickytripper.model.ImportSettings;

public class ExchangeRateControllerImpl implements ExchangeRateController {

    private final DataManager dataManager;
    private final PrefsResolver prefsResolver;

    public ExchangeRateControllerImpl(DataManager dataManager, PrefsResolver prefsResolver) {
        this.dataManager = dataManager;
        this.prefsResolver = prefsResolver;
    }

    public List<ExchangeRate> findSuitableRates(Currency currencyFrom, Currency currencyTo) {
        List<ExchangeRate> result = new ArrayList<>();
        for (ExchangeRate rate : dataManager.findSuitableRates(currencyFrom, currencyTo)) {
            if (currencyFrom.equals(rate.getCurrencyFrom())) {
                result.add(rate);
            }
            else {
                result.add(rate.cloneToInversion());
            }
        }
        return result;
    }

    public List<ExchangeRate> getAllExchangeRatesWithoutInversion() {
        return dataManager.getAllExchangeRatesWithoutInversion();
    }

    public ExchangeRate getExchangeRateById(Long technicalId) {
        return dataManager.getExchangeRateById(technicalId);
    }

    public boolean deleteExchangeRates(List<ExchangeRate> rows) {
        return dataManager.deleteExchangeRates(rows);
    }

    public ExchangeRate persistExchangeRate(ExchangeRate rate) {
        return dataManager.persistExchangeRate(rate);
    }

    public boolean doesExchangeRateAlreadyExist(ExchangeRate exchangeRate) {
        return dataManager.doesExchangeRateAlreadyExist(exchangeRate);
    }

    public void persistImportedExchangeRate(ExchangeRate rate, boolean replaceWhenAlreadyImported) {
        dataManager.persistImportedExchangeRate(rate, replaceWhenAlreadyImported);
    }

    public void persistExchangeRateUsedLast(ExchangeRate exchangeRateUsedLast) {
        dataManager.persistExchangeRateUsedLast(exchangeRateUsedLast);
    }

    public ImportSettings getImportSettingsUsedLast() {
        return PrefWriterReaderUtils.loadImportSettings(prefsResolver.getPrefs());
    }

    public long getNextExchangeRateAutoSaveSeqNumber() {
        long nextNumber = PrefWriterReaderUtils.loadExchangeRateAutoSaveSeq(prefsResolver.getPrefs());
        PrefWriterReaderUtils.saveExchangeRateAutoSaveSeq(prefsResolver.getEditingPrefsEditor(), nextNumber + 1);
        return nextNumber;
    }

    public void saveImportSettingsUsedLast(ImportSettings importSettings) {
        PrefWriterReaderUtils
                .saveImportSettings(prefsResolver.getEditingPrefsEditor(), importSettings);
    }

}
