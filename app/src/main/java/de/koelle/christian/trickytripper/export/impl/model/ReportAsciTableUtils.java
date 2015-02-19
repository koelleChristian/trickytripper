package de.koelle.christian.trickytripper.export.impl.model;

import de.koelle.christian.trickytripper.export.impl.res.TxtExportCharResolver;

public class ReportAsciTableUtils {
    public static ReportAsciTable buildReportAsciiTable(StringBuilder contents) {
        ReportAsciTable result = new ReportAsciTable();

        String[] rows = contents.toString().split(TxtExportCharResolver.TXT_ROW_END_DELIMITER);
        for (int i = 0; i < rows.length; i++) {
            String[] rowValues = rows[i].split(TxtExportCharResolver.TXT_VALUE_DELIMITER);
            ReportAsciTableLayoutTableRow rowObj = null;
            if (i != 0) {
                rowObj = new ReportAsciTableLayoutTableRow();
            }

            for (String val : rowValues) {
                if (i == 0) {
                    result.addHeading(val);
                }
                else {
                    rowObj.addContent(val);
                }
            }
            if (rowObj != null) {
                result.addRow(rowObj);
            }
        }
        return result;
    }
}
