package de.koelle.christian.trickytripper.export.impl.res;

import de.koelle.christian.trickytripper.constants.Rc;
import de.koelle.christian.trickytripper.export.impl.ExportCharResolver;
import de.koelle.christian.trickytripper.export.impl.StyleClass;

public class CsvExportCharResolver implements ExportCharResolver {

    public static final String CSV_DELIMITER = ";";

    public CharSequence getFilePrefix() {
        return "";
    }

    public CharSequence getFilePostfix() {
        return "";
    }

    public CharSequence getTablePrefix() {
        return "";
    }

    public CharSequence getTablePostfix() {
        return "";
    }

    public CharSequence getColumnDelimiter(StyleClass... classes) {
        return CSV_DELIMITER;
    }

    public CharSequence getRowStartDelimiter(StyleClass... classes) {
        return "";
    }

    public CharSequence getRowEndDelimiter() {
        return Rc.LINE_FEED;
    }

    public CharSequence translateValue(String value) {
        return value;
    }

}
