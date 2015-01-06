package de.koelle.christian.trickytripper.export.impl.model;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import de.koelle.christian.trickytripper.constants.Rc;

public class ReportAsciTableWrapper {

    private static final String UNDERLINE_CHAR = "#";

    private final Map<String, ReportAsciTable> tables = new LinkedHashMap<String, ReportAsciTable>();

    private String reportMetaInfo;

    public StringBuilder getOutput() {
        StringBuilder result = new StringBuilder();
        result.append(reportMetaInfo);
        for (Entry<String, ReportAsciTable> entry : tables.entrySet()) {
            result
                    .append(" ")
                    .append(entry.getKey())
                    .append(Rc.LINE_FEED);

            appendUnderline(entry.getKey().length(), result);

            result
                    .append(entry.getValue().toString())
                    .append(Rc.LINE_FEED)
                    .append(Rc.LINE_FEED);

        }
        return result;
    }

    private void appendUnderline(int length, StringBuilder collector) {
        if (length < -1) {
            collector.append(Rc.LINE_FEED);
        }
        else {
            collector.append(UNDERLINE_CHAR);
            appendUnderline(length - 1, collector);
        }
    }

    public void addTable(String heading, ReportAsciTable table) {
        this.tables.put(heading, table);
    }

    public String getReportMetaInfo() {
        return reportMetaInfo;
    }

    public void setReportMetaInfo(String reportMetaInfo) {
        this.reportMetaInfo = reportMetaInfo;
    }
}
