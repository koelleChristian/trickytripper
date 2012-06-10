package de.koelle.christian.trickytripper.export.impl.res;

import de.koelle.christian.common.utils.Html;
import de.koelle.christian.trickytripper.export.impl.ExportCharResolver;
import de.koelle.christian.trickytripper.export.impl.StyleClass;

public class HtmlExportCharResolver implements ExportCharResolver {

    public static final String FILE_POSTFIX = "\n" +
            "        </body>" +
            "    </html> ";
    public static final String PLACEHOLDER_LANG = "@Lang@";
    public static final String PLACEHOLDER_TITLE = "@Title@";

    private static final String FILE_PREFIX = "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01//EN\"" +
            "\"http://www.w3.org/TR/html4/strict.dtd\">" +
            "<html lang=\"" + PLACEHOLDER_LANG + "\">" +
            "<style type=\"text/css\">" +
            "body {" +
            "   font-family: Arial, sans-serif;" +
            "}" +

            "table {" +
            "    border-collapse: collapse;" +
            "}" +

            "h1 {" +
            "    color: purple;" +
            "}" +

            "h3 {" +
            "    color: purple;" +
            "}" +

            "td {" +
            "    border: solid;" +
            "    border-color: black;" +
            "    padding: 6px;" +
            " }" +

            "." + StyleClass.HEADING.getClassName() + " {" +
            "    font-weight: bold;" +
            "    color: black;" +
            "    background-color: #FCF6CF;" +
            "}" +

            "." + StyleClass.BACKGROUND_PAYER.getClassName() + " {" +
            "    background-color: #F9E3FC;" +
            "}" +

            "." + StyleClass.BACKGROUND_SPENDER.getClassName() + " {" +
            "    background-color: #E3FCF2;" +
            "}" +

            "." + StyleClass.NUMERIC_VALUE.getClassName() + " {" +
            "    text-align: right;" +
            "}" +
            "</style>" +
            "<head>" +
            "<meta http-equiv=\"content-type\" content=\"text/html; charset=utf-8\">" +
            "<title>" +
            PLACEHOLDER_TITLE +
            "</title>" +
            "</head>" +
            "<body>";

    public static final String FILE_PREFIX_FOR_EMAIL = "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01//EN\"" +
            "\"http://www.w3.org/TR/html4/strict.dtd\">" +
            "<html lang=\"" + PLACEHOLDER_LANG + "\">" +
            "<head>" +
            "<meta http-equiv=\"content-type\" content=\"text/html; charset=utf-8\">" +
            "</head>" +
            "<body>";

    private String title;
    private String lang;

    /* ---------------- Impl ---------------------------- */

    public CharSequence getFilePrefix() {
        return FILE_PREFIX.replace(PLACEHOLDER_LANG, lang).replace(PLACEHOLDER_TITLE, title);
    }

    public CharSequence getFilePrefixForEmail() {
        return FILE_PREFIX_FOR_EMAIL.replace(PLACEHOLDER_LANG, lang);
    }

    public CharSequence getFilePostfix() {
        return FILE_POSTFIX;
    }

    public CharSequence getTablePrefix() {
        return "<table border>";
    }

    public CharSequence getTablePostfix() {
        return "</table>";
    }

    public CharSequence getColumnDelimiter(StyleClass... classes) {
        return "</td><td" + appendStyles(classes) + ">";
    }

    private String appendStyles(StyleClass[] classes) {
        if (classes == null || classes.length == 0) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        builder.append(" class=\"");
        for (int i = 0; i < classes.length; i++) {
            builder.append(classes[i].getClassName());
            if (i != classes.length - 1) {
                builder.append(" ");
            }
            else {
                builder.append("\"");
            }

        }

        return builder.toString();
    }

    public CharSequence getRowStartDelimiter(StyleClass... classes) {
        return "<tr><td" + appendStyles(classes) + ">";
    }

    public CharSequence getRowEndDelimiter() {
        return "</td></tr> ";
    }

    public CharSequence getNewLine() {
        return "<br/>";
    }

    public CharSequence wrapInHeading(String value) {
        return "<h1>" + translateValue(value) + "</h1>";
    }

    public CharSequence wrapInSubHeading(String value) {
        return "<h3>" + translateValue(value) + "</h3>";
    }

    public CharSequence translateValue(String value) {
        return Html.toHtmlChar(value);
    }

    public StringBuilder writeReportMetaInfo(String... strings) {
        StringBuilder result = new StringBuilder();
        result.append("<ul>");
        for (String s : strings) {
            result
                    .append("<li>")
                    .append(translateValue(s))
                    .append("</li>")
            /**/;
        }
        result.append("</ul>");
        return result;
    }

    /*--------------------- setter ---------------*/

    public void setTitle(String title) {
        this.title = title;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

}
