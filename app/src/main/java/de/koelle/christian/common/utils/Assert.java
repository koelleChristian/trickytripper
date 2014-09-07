package de.koelle.christian.common.utils;

public class Assert {

    public static void notNull(Object o) {
        if (o == null) {
            throw new RuntimeException("Not null constraint violation.");
        }
    }

    public static void notNull(long o) {
        if (o <= 0) {
            throw new RuntimeException("Not null constraint violation.");
        }
    }

}
