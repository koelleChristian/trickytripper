package de.koelle.christian.trickytripper.controller;

import java.text.Collator;
import java.util.Currency;

import de.koelle.christian.common.options.OptionsSupport;
import de.koelle.christian.common.support.DimensionSupport;
import de.koelle.christian.common.ui.filter.DecimalNumberInputUtil;
import de.koelle.christian.trickytripper.model.HierarchicalCurrencyList;

public interface MiscController {

    Currency getDefaultBaseCurrency();

    Collator getDefaultStringCollator();

    boolean checkIfInAssets(String assetName);

    boolean isOnline();

    // String getCurrencySymbolOfTripLoaded(boolean wrapInBrackets);

    OptionsSupport getOptionSupport();

    HierarchicalCurrencyList getAllCurrencies();

    HierarchicalCurrencyList getAllCurrenciesForTarget(Currency currency);

    Currency getCurrencyFavorite(Currency currencyToBeExcluded);

    DecimalNumberInputUtil getDecimalNumberInputUtil();

    DimensionSupport getDimensionSupport();
}
