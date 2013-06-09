package de.koelle.christian.trickytripper.ui.utils;

import java.util.Locale;

import android.content.res.Resources;
import de.koelle.christian.common.utils.DateUtils;
import de.koelle.christian.trickytripper.R;
import de.koelle.christian.trickytripper.model.ExchangeRate;
import de.koelle.christian.trickytripper.modelutils.AmountViewUtils;

public class ExchangeRateDescriptionUtils {

    private final String prefixForImportedRecords;
    private final DateUtils dateUtils;
    private final Resources resources;

    public ExchangeRateDescriptionUtils(Resources resources) {
        this.resources = resources;
        this.dateUtils = new DateUtils(getLocale(resources));
        this.prefixForImportedRecords = resources.getString(R.string.exchangeRate_common_import_description_prefix)
                + " ";
    }

    private Locale getLocale(Resources resources) {
        return resources.getConfiguration().locale;
    }

    public StringBuilder deriveDescription(ExchangeRate row) {
        if (row.isImported()) {
            return new StringBuilder()
                    .append(prefixForImportedRecords)
                    .append(dateUtils.date2String(row.getUpdateDate()));
        }
        else {
            String description = row.getDescription();
            if(description == null){
            	description = resources.getString(R.string.currencyCalculatorViewNoMatchingRatesAvailable);
            }
			return new StringBuilder().append(description);
        }
    }

    public StringBuilder deriveDescriptionWithRate(ExchangeRate row) {
        return new StringBuilder()
                .append(resources.getString(R.string.currencyCalculatorViewTextViewHeadLabelRate))
                .append(": ")
                .append(AmountViewUtils.getDoubleString(getLocale(resources), row.getExchangeRate()))
                .append("\n")
                .append(deriveDescription(row));
    }
}
