package de.koelle.christian.trickytripper.controller;

import java.text.Collator;
import java.util.Currency;
import java.util.List;

import de.koelle.christian.common.options.OptionsSupport;
import de.koelle.christian.trickytripper.model.HierarchicalCurrency;

public interface MiscController {

    Currency getDefaultBaseCurrency();

    boolean isSmartHelpEnabled();

    Collator getDefaultStringCollator();

    boolean checkIfInAssets(String assetName);

    boolean isOnline();

    String getCurrencySymbolOfTripLoaded(boolean wrapInBrackets);

    OptionsSupport getOptionSupport();

    List<HierarchicalCurrency> getAllCurrencies();

}
