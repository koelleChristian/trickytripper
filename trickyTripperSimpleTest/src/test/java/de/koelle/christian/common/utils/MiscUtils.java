package de.koelle.christian.common.utils;

import java.text.Collator;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import org.junit.Ignore;
import org.junit.Test;

public class MiscUtils {

    @Ignore("This is a manual test, intended to investigate aspects of sorting.")
    @Test
    public void testSorting() {
        final Collator collatorSingle = Collator.getInstance(Locale.GERMAN);
        collatorSingle.setStrength(Collator.PRIMARY);

        final Collator collatorDuo = Collator.getInstance(Locale.GERMAN);
        collatorDuo.setStrength(Collator.SECONDARY);

        final Collator collatorTriple = Collator.getInstance(Locale.GERMAN);
        collatorTriple.setStrength(Collator.TERTIARY);

        String[] input = new String[] {
                "Christian",
                "Christian A",
                "christian A",
                "christian",
                "Christian K",
                "Christian Ko",
                "Christian Kolle",
                "Christian Koelle",
                "Christian Koelle A",
                "Christian Koelle B",
                "Christian Koelle c",
                "Christian Kölle",
                "Christian Koalle",
                "Christian Koolle",
                "Christian Z"
        };
        List<String> strings1 = Arrays.asList(input);
        List<String> strings2 = Arrays.asList(input);
        List<String> strings3 = Arrays.asList(input);

        Collections.sort(strings1, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return collatorSingle.compare(o1, o2);
            }
        });

        Collections.sort(strings2, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return collatorDuo.compare(o1, o2);
            }

        });
        Collections.sort(strings3, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return collatorTriple.compare(o1, o2);
            }
        });

        for (int i = 0; i < strings1.size(); i++) {
            System.out.println(strings1.get(i) + "\t\t" + strings2.get(i) + "\t\t" + strings3.get(i) + "\n");
        }
    }
}
