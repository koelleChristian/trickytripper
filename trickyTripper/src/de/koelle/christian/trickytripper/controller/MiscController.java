package de.koelle.christian.trickytripper.controller;

import java.text.Collator;
import java.util.Currency;

import de.koelle.christian.common.options.OptionsSupport;

public interface MiscController {

    Currency getDefaultBaseCurrency();

    boolean isSmartHelpEnabled();

    Collator getDefaultStringCollator();

    boolean checkIfInAssets(String assetName);

    boolean isOnline();

    String getCurrencySymbolOfTripLoaded(boolean wrapInBrackets);

    OptionsSupport getOptionSupport();

}
