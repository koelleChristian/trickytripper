package de.koelle.christian.trickytripper.export.impl;

public interface ExportCharResolver {

    CharSequence getTablePrefix();

    CharSequence getTablePostfix();

    CharSequence getColumnDelimiter(StyleClass... classes);

    CharSequence getRowStartDelimiter(StyleClass... classes);

    CharSequence getRowEndDelimiter();

    CharSequence translateValue(String value);

}
