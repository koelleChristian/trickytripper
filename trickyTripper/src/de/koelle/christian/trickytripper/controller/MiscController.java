package de.koelle.christian.trickytripper.controller;

import java.text.Collator;
import java.util.Currency;

public interface MiscController {

    Currency getDefaultBaseCurrency();

    boolean isSmartHelpEnabled();

    Collator getDefaultStringCollator();

    boolean checkIfInAssets(String assetName);

    boolean isOnline();

    String getCurrencySymbolOfTripLoaded(boolean wrapInBrackets);

}
