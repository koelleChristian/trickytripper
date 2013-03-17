package de.koelle.christian.trickytripper.controller.impl;

import java.io.IOException;
import java.text.Collator;
import java.util.Arrays;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;
import de.koelle.christian.common.options.OptionsSupport;
import de.koelle.christian.common.ui.filter.DecimalNumberInputUtil;
import de.koelle.christian.common.utils.CurrencyUtil;
import de.koelle.christian.common.utils.SystemUtil;
import de.koelle.christian.trickytripper.R;
import de.koelle.christian.trickytripper.apputils.PrefWritrerReaderUtils;
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
                R.id.option_create_participant,
                R.id.option_create_trip,
                R.id.option_create_exchange_rate,
                R.id.option_delete,
                R.id.option_export,
                R.id.option_help,
                R.id.option_import,
                R.id.option_preferences
        });
    }

    private Locale getLocale(Context context) {
        return context.getResources().getConfiguration().locale;
    }

    public Currency getDefaultBaseCurrency() {
        return PrefWritrerReaderUtils.loadDefaultCurrency(prefsResolver.getPrefs(), context.getResources());
    }

    public Collator getDefaultStringCollator() {
        return defaultCollator;
    }

    public boolean checkIfInAssets(String assetName) {
        if (allAssetsList == null) {
            AssetManager am = context.getAssets();
            try {
                allAssetsList = Arrays.asList(am.list(""));
            }
            catch (IOException e) {
            }
        }
        return allAssetsList.contains(assetName) ? true : false;
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
        result.setCurrenciesMatchingInOrderOfUsage(
                CurrencyUtil.convertToCurrencyWithName(currenciesUsed
                        .getCurrenciesMatchingInOrderOfUsage()
                        , context.getResources()));
        result.setCurrenciesUsedByDate(
                CurrencyUtil.convertToCurrencyWithName(currenciesUsed
                        .getCurrenciesUsedByDate()
                        , null));
        result.setCurrenciesInProject(
                CurrencyUtil.convertToCurrencyWithName(currenciesUsed
                        .getCurrenciesInProject()
                        , null));
        result.setCurrenciesElse(
                CurrencyUtil.convertOthersToCurrencyWithName(
                        currenciesUsed.getCurrenciesAlreadyFilled()
                        , null));
        if (Log.isLoggable(Rc.LT, Log.DEBUG)) {
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

}
