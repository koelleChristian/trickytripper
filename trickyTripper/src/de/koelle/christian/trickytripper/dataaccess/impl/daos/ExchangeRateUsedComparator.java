package de.koelle.christian.trickytripper.dataaccess.impl.daos;

import java.util.Comparator;
import java.util.List;

import de.koelle.christian.trickytripper.model.ExchangeRate;

public class ExchangeRateUsedComparator implements Comparator<ExchangeRate> {

    private final List<Long> sortedIdListUsedLast;

    public ExchangeRateUsedComparator(List<Long> sortedIdListUsedLast) {
        this.sortedIdListUsedLast = sortedIdListUsedLast;
    }

    public int compare(ExchangeRate object1, ExchangeRate object2) {
        int pos01 = sortedIdListUsedLast.indexOf(object1.getId());
        int pos02 = sortedIdListUsedLast.indexOf(object2.getId());
        if (pos01 >= 0 && pos02 >= 0) {
            return (pos01 < pos02) ? -1 : 1;
        }
        else if (pos01 >= 0) {
            return -1;
        }
        else if (pos02 >= 0) {
            return 1;
        }
        else {
            return 0;
        }
    }

}
