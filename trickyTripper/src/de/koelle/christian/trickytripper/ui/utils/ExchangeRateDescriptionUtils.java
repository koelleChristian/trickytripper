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
    private final Locale locale;

    public ExchangeRateDescriptionUtils(Resources resources) {
        this.locale = resources.getConfiguration().locale;
        this.dateUtils = new DateUtils(locale);
        this.prefixForImportedRecords = resources.getString(R.string.exchangeRate_common_import_description_prefix)
                + " ";
    }

    public StringBuilder deriveDescriptionForList(ExchangeRate row) {
        if (row.isImported()) {
            return new StringBuilder()
                    .append(prefixForImportedRecords)
                    .append(dateUtils.date2String(row.getUpdateDate()));
        }
        else {
            return new StringBuilder()
                    .append(row.getDescription());
        }
    }

    public StringBuilder deriveDescription2(ExchangeRate row) {
        return new StringBuilder()
                .append(AmountViewUtils.getDoubleString(locale, row.getExchangeRate(), true, true, false, true))
                .append(" ")
                .append(deriveDescriptionForList(row));
    }
}
