package de.koelle.christian.trickytripper.export.impl;

public enum StyleClass {

    NUMERIC_VALUE("valueNumeric"),
    HEADING("heading"),
    BACKGROUND_PAYER("bgPayer"),
    BACKGROUND_SPENDER("bgSpender"),
    /**/;

    private final String className;

    private StyleClass(String className) {
        this.className = className;
    }

    public String getClassName() {
        return this.className;
    }

}
