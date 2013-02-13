package de.koelle.christian.trickytripper.controller.impl;

import java.util.Currency;
import java.util.List;

import android.content.res.Resources;
import de.koelle.christian.trickytripper.apputils.PrefWritrerReaderUtils;
import de.koelle.christian.trickytripper.controller.ExchangeRateController;
import de.koelle.christian.trickytripper.dataaccess.DataManager;
import de.koelle.christian.trickytripper.decoupling.PrefsResolver;
import de.koelle.christian.trickytripper.model.ExchangeRate;
import de.koelle.christian.trickytripper.model.ExchangeRateResult;
import de.koelle.christian.trickytripper.model.ImportSettings;

public class ExchangeRateControllerImpl implements ExchangeRateController {

    private final DataManager dataManager;
    private final PrefsResolver prefsResolver;
    private final Resources resources;

    public ExchangeRateControllerImpl(DataManager dataManager, PrefsResolver prefsResolver, Resources resources) {
        this.dataManager = dataManager;
        this.prefsResolver = prefsResolver;
        this.resources = resources;
    }

    public ExchangeRateResult findSuitableRates(Currency currencyFrom, Currency currencyTo) {
        return dataManager.findSuitableRates(currencyFrom, currencyTo);
    }

    public List<ExchangeRate> getAllExchangeRatesWithoutInversion() {
        return dataManager.getAllExchangeRatesWithoutInversion();
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

    public void persitImportedExchangeRate(ExchangeRate rate, boolean replaceWhenAlreadyImported) {
        dataManager.persistImportedExchangeRate(rate, replaceWhenAlreadyImported);
    }

    public Currency getSourceCurrencyUsedLast() {
        return PrefWritrerReaderUtils.loadSourceCurrencyUsedLast(prefsResolver.getPrefs(), resources);
    }

    public void persistLastExchangeRateUsageSettings(Currency sourceCurrencyLastUsed, ExchangeRate exchangeRateUsedLast) {
        PrefWritrerReaderUtils
                .saveSourceCurrencyUsedLast(prefsResolver.getEditingPrefsEditor(), sourceCurrencyLastUsed);
        dataManager.persistExchangeRateUsedLast(exchangeRateUsedLast);
    }

    public ImportSettings getImportSettingsUsedLast() {
        return PrefWritrerReaderUtils.loadImportSettings(prefsResolver.getPrefs());
    }

    public void saveImportSettingsUsedLast(ImportSettings importSettings) {
        PrefWritrerReaderUtils
                .saveImportSettings(prefsResolver.getEditingPrefsEditor(), importSettings);
    }

}
