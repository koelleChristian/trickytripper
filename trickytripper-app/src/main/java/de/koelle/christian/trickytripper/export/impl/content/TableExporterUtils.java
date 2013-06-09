package de.koelle.christian.trickytripper.export.impl.content;

import java.util.Collection;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Set;

import de.koelle.christian.trickytripper.model.Amount;
import de.koelle.christian.trickytripper.model.Participant;
import de.koelle.christian.trickytripper.modelutils.AmountViewUtils;

public class TableExporterUtils {

    public static String getAmount(Locale locale, Amount amount) {
        return AmountViewUtils.getAmountString(locale, (amount != null) ? amount : new Amount(),
                true, /* Just the number */
                false, /* blank if zero */
                false, /* blank if null */
                true, /* force fraction */
                true /* strip off sign */
                );
    }

    public static boolean partOf(Participant p, Set<Entry<Participant, Amount>> entrySet) {
        for (Entry<Participant, Amount> entry : entrySet) {
            if (p.getId() == entry.getKey().getId()) {
                return true;
            }
        }
        return false;
    }

    public static boolean partOf(Collection<Participant> participants, Set<Entry<Participant, Amount>> entrySet) {
        for (Participant p : participants) {
            if (partOf(p, entrySet)) {
                return true;
            }
        }
        return false;
    }

}
