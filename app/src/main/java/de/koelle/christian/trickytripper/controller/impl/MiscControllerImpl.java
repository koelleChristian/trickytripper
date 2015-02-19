package de.koelle.christian.trickytripper.controller.impl;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.IOException;
import java.text.Collator;
import java.util.Arrays;
import java.util.Currency;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import de.koelle.christian.common.options.OptionsSupport;
import de.koelle.christian.common.support.DimensionSupport;
import de.koelle.christian.common.ui.filter.DecimalNumberInputUtil;
import de.koelle.christian.common.utils.CurrencyUtil;
import de.koelle.christian.common.utils.SystemUtil;
import de.koelle.christian.trickytripper.R;
import de.koelle.christian.trickytripper.apputils.PrefWriterReaderUtils;
import de.koelle.christian.trickytripper.constants.Rc;
import de.koelle.christian.trickytripper.controller.MiscController;
import de.koelle.christian.trickytripper.dataaccess.DataManager;
import de.koelle.christian.trickytripper.decoupling.PrefsResolver;
import de.koelle.christian.trickytripper.model.CurrenciesUsed;
import de.koelle.christian.trickytripper.model.HierarchicalCurrencyList;

public class MiscControllerImpl implements MiscController {

    private final Collator defaultCollator;
    private final DecimalNumberInputUtil decimalNumberInputUtil;
    private final DataManager dataManager;
    private final OptionsSupport optionSupport;
    private final DimensionSupport dimensionSupport;
    private List<String> allAssetsList = null;
    private final Context context;
    private final PrefsResolver prefsResolver;

    public MiscControllerImpl(DataManager dataManager, Context context, PrefsResolver prefsResolver) {
        this.dataManager = dataManager;
        this.context = context;
        this.prefsResolver = prefsResolver;

        this.defaultCollator = Collator.getInstance(getLocale(context));
        this.defaultCollator.setStrength(Rc.DEFAULT_COLLATOR_STRENGTH);
        this.decimalNumberInputUtil = new DecimalNumberInputUtil(getLocale(context));

        this.optionSupport = new OptionsSupport(new int[] {
                R.id.option_add_participant,
                R.id.option_create_trip,
                R.id.option_create_exchange_rate,
                R.id.option_create_exchange_rate_for_source,
                R.id.option_upload,
                R.id.option_edit,
                R.id.option_delete,
                R.id.option_save_create,
                R.id.option_save_edit,
                R.id.option_accept,
                R.id.option_export,
                R.id.option_help,
                R.id.option_import,
                R.id.option_preferences
        });
        this.dimensionSupport = new DimensionSupport(context);
    }

    private Locale getLocale(Context context) {
        return context.getResources().getConfiguration().locale;
    }

    public Currency getDefaultBaseCurrency() {
        return PrefWriterReaderUtils.loadDefaultCurrency(prefsResolver.getPrefs(), context.getResources());
    }

    public Collator getDefaultStringCollator() {
        return defaultCollator;
    }

    public boolean checkIfInAssets(String assetName) {
        if (allAssetsList == null) {
            AssetManager am = context.getAssets();
            try {
                allAssetsList = Arrays.asList(am.list("")); //TODO(ckoelle) Empty Catch
            } catch (IOException e) {
            }
        }
        return allAssetsList.contains(assetName);
    }

    public boolean isOnline() {
        return SystemUtil.isOnline(context);
    }

    public OptionsSupport getOptionSupport() {
        return optionSupport;
    }

    public HierarchicalCurrencyList getAllCurrenciesForTarget(Currency currency) {
        HierarchicalCurrencyList result = new HierarchicalCurrencyList();
        CurrenciesUsed currenciesUsed = dataManager.findUsedCurrenciesForTarget(currency);
        result.setCurrenciesUsedMatching(
                CurrencyUtil.convertToCurrencyWithName(currenciesUsed
                        .getCurrenciesUsedMatching()
                        , context.getResources()));
        result.setCurrenciesUsedUnmatched(
                CurrencyUtil.convertToCurrencyWithName(currenciesUsed
                        .getCurrenciesUsedUnmatched()
                        , null));

        result.setCurrenciesInExchangeRatesMatching(
                CurrencyUtil.convertToCurrencyWithName(currenciesUsed
                        .getCurrenciesInExchangeRatesMatching()
                        , null));

        result.setCurrenciesInExchangeRatesUnmatched(
                CurrencyUtil.convertToCurrencyWithName(currenciesUsed
                        .getCurrenciesInExchangeRatesUnmatched()
                        , null));

        result.setCurrenciesInTrips(
                CurrencyUtil.convertToCurrencyWithName(currenciesUsed
                        .getCurrenciesInTrips()
                        , null));
        Set<Currency> currenciesToBeExcludedFromElseList = new HashSet<>();
        if (currency != null) {
            currenciesToBeExcludedFromElseList.add(currency);
        }
        result.setCurrenciesElse(
                CurrencyUtil.convertOthersToCurrencyWithName(
                        currenciesToBeExcludedFromElseList
                        , null));
        if (Rc.debugOn) {
            Log.d(Rc.LT, "Currencies requested for target=" + currency + ": " + result);
        }
        return result;
    }

    public HierarchicalCurrencyList getAllCurrencies() {
        return getAllCurrenciesForTarget(null);
    }

    public Currency getCurrencyFavorite(Currency currencyToBeExcluded) {
        CurrenciesUsed currenciesUsed = dataManager.findUsedCurrenciesForTarget(currencyToBeExcluded);
        Currency first = currenciesUsed.getFirstImportant();
        return (first != null) ? first : CurrencyUtil.getFirstOther(currencyToBeExcluded, null);
    }

    public DecimalNumberInputUtil getDecimalNumberInputUtil() {
        return decimalNumberInputUtil;
    }

    public DimensionSupport getDimensionSupport() {
        return dimensionSupport;
    }

}
