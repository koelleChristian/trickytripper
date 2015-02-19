package de.koelle.christian.trickytripper.export.impl.model;

import java.util.ArrayList;
import java.util.List;

import de.koelle.christian.common.ascii.AsciTableLayoutTableInterface;
import de.koelle.christian.common.ascii.AsciToStringHelper;

public class ReportAsciTable implements ReportAsciTableHeadingCallback {

    private final List<String> headings = new ArrayList<String>();
    private final List<ReportAsciTableLayoutTableRow> rows = new ArrayList<ReportAsciTableLayoutTableRow>();

    @Override
    public String toString() {
        return AsciToStringHelper.asciToString(rows.toArray(new AsciTableLayoutTableInterface[rows.size()]));
    }

    public void addRow(ReportAsciTableLayoutTableRow row) {
        row.setCallback(this);
        this.rows.add(row);
    }

    public void addHeading(String heading) {
        this.headings.add(heading);
    }

    public String[] getHeadings() {
        return headings.toArray(new String[headings.size()]);
    }
}
