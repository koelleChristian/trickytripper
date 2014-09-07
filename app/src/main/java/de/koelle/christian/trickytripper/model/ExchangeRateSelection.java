package de.koelle.christian.trickytripper.model;

import de.koelle.christian.trickytripper.R;

public enum ExchangeRateSelection {

    NONE(R.string.label_enum_select_rates_none),
    ALL(R.string.label_enum_select_rates_all),
    ALL_IMPORTED(R.string.label_enum_select_rates_all_imported),
    ALL_CUSTOM(R.string.label_enum_select_rates_all_custom),
    /**/
    ;

    private final int resourceId;

    private ExchangeRateSelection(int resourceId) {
        this.resourceId = resourceId;
    }

    public int getResourceId() {
        return resourceId;
    }

    public static ExchangeRateSelection getByResourceId(int resourceId) {
        for (ExchangeRateSelection value : values()) {
            if (value.getResourceId() == resourceId) {
                return value;
            }
        }
        return null;
    }
}
