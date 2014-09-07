package de.koelle.christian.trickytripper.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.koelle.christian.trickytripper.R;

public enum PaymentCategory implements ResourceLabelAwareEnumeration {
    /**
     * Housing.
     */
    HOUSING(R.string.label_enum_payment_category_housing, false),
    /**
     * Food.
     */
    FOOD(R.string.label_enum_payment_category_food, false),
    /**
     * Something to drink.
     */
    BEVERAGES(R.string.label_enum_payment_category_beverages, false),
    /**
     * Gasoline.
     */
    GAS(R.string.label_enum_payment_category_gas, false),
    /**
     * Public transport.
     */
    PUBLIC_TRANSPORT(R.string.label_enum_payment_category_public_transport, false),
    /**
     * Various.
     */
    OTHER(R.string.label_enum_payment_category_other, false),
    /**
     * Other rentals.
     */
    RENTALS(R.string.label_enum_payment_category_rentals, false),
    /**
     * Other rentals.
     */
    CULTURE(R.string.label_enum_payment_category_culture, false),
    /**
     * Other rentals.
     */
    ENTRANCE_FEE(R.string.label_enum_payment_category_entrance_fee, false),
    /**
     * Other rentals.
     */
    MONEY_TRANSFER(R.string.label_enum_payment_category_money_transfer, true),
    /**/;

    private final int resourceId;
    private final boolean internal;

    private PaymentCategory(int resourceId, boolean internal) {
        this.resourceId = resourceId;
        this.internal = internal;
    }

    public int getResourceStringId() {
        return resourceId;
    }

    public boolean isInternal() {
        return internal;
    }

    public List<ResourceLabelAwareEnumeration> getAllValues() {
        List<ResourceLabelAwareEnumeration> result = new ArrayList<ResourceLabelAwareEnumeration>();
        result.addAll(Arrays.asList(values()));
        return result;
    }

    public static PaymentCategory getValueByOrdinal(int ordinal) {
        if (ordinal < 0) {
            return null;
        }
        for (PaymentCategory value : PaymentCategory.values()) {
            if (value.ordinal() == ordinal) {
                return value;
            }
        }
        return null;
    }
}
