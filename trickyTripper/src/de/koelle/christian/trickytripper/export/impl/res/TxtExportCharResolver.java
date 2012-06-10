package de.koelle.christian.trickytripper.export.impl.res;

import de.koelle.christian.common.utils.StringUtils;
import de.koelle.christian.trickytripper.constants.Rc;
import de.koelle.christian.trickytripper.export.impl.ExportCharResolver;
import de.koelle.christian.trickytripper.export.impl.StyleClass;

public class TxtExportCharResolver implements ExportCharResolver {

    public static final String TXT_ROW_END_DELIMITER = "@##@";
    public static final String TXT_VALUE_DELIMITER = "%";
    public static final String TXT_HEADING_SYMBOL = "#";

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
        return TXT_VALUE_DELIMITER;
    }

    public CharSequence getRowStartDelimiter(StyleClass... classes) {
        return "";
    }

    public CharSequence getRowEndDelimiter() {
        /* Blank to allow splitting */
        return " " + TXT_ROW_END_DELIMITER;
    }

    public CharSequence translateValue(String value) {
        return value;
    }

    public StringBuilder wrapInHeading(String headingTxt) {
        StringBuilder result = new StringBuilder();

        Object frameString = StringUtils.generateString(headingTxt.length() + 4, TXT_HEADING_SYMBOL);
        result

                .append(frameString)
                .append(Rc.LINE_FEED)

                .append(TXT_HEADING_SYMBOL)
                .append(" ")
                .append(headingTxt)
                .append(" ")
                .append(TXT_HEADING_SYMBOL)
                .append(Rc.LINE_FEED)

                .append(frameString)
                .append(Rc.LINE_FEED)
                .append(Rc.LINE_FEED)
        /**/;
        return result;
    }

    public StringBuilder writeReportMetaInfo(String[] reportMetaInfo) {
        StringBuilder result = new StringBuilder();
        for (String value : reportMetaInfo) {
            result
                    .append("\t\t")
                    .append("* ")
                    .append(value)
                    .append(Rc.LINE_FEED)
            /**/;
        }
        result
                .append(Rc.LINE_FEED)
                .append(Rc.LINE_FEED)
        /**/;
        return result;
    }

}
