package de.koelle.christian.trickytripper.model;

public enum ImportOrigin {

    /***/
    NONE,
    /***/
    GOOGLE,
    /**/
    ;

    public static ImportOrigin getValueByOrdinal(int ordinal) {
        if (ordinal < 0) {
            return null;
        }
        for (ImportOrigin value : ImportOrigin.values()) {
            if (value.ordinal() == ordinal) {
                return value;
            }
        }
        return null;
    }

}
