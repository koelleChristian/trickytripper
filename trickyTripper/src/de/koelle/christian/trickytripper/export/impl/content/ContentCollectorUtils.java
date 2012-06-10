package de.koelle.christian.trickytripper.export.impl.content;

import java.util.Locale;

import de.koelle.christian.trickytripper.model.Amount;
import de.koelle.christian.trickytripper.modelutils.AmountViewUtils;

public class ContentCollectorUtils {

    public static String getAmount(Locale locale, Amount amount) {
        return AmountViewUtils.getAmountString(locale, (amount != null) ? amount : new Amount(),
                true, /* Just the numer */
                false, /* blank if zero */
                false, /* blank if null */
                true, /* force fraction */
                true /* strip off sign */
                );
    }

}
