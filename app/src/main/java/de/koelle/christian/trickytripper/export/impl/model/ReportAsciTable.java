package de.koelle.christian.trickytripper.export.impl.model;

import java.util.ArrayList;
import java.util.List;

import de.koelle.christian.common.ascii.AsciTableLayoutableInterface;
import de.koelle.christian.common.ascii.AsciToStringHelper;

public class ReportAsciTable implements ReportAsciTableHeadingCallback {

    private final List<String> headings = new ArrayList<String>();
    private final List<ReportAsciTableRow> rows = new ArrayList<ReportAsciTableRow>();

    @Override
    public String toString() {
        return AsciToStringHelper.asciToString(rows.toArray(new AsciTableLayoutableInterface[rows.size()]));
    }

    public void addRow(ReportAsciTableRow row) {
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
