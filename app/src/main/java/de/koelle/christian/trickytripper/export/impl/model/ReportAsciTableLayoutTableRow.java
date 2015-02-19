package de.koelle.christian.trickytripper.export.impl.model;

import java.util.ArrayList;
import java.util.List;

import de.koelle.christian.common.ascii.AsciTableLayoutTableInterface;

public class ReportAsciTableLayoutTableRow implements AsciTableLayoutTableInterface {
    private ReportAsciTableHeadingCallback callback;
    private final List<String> content = new ArrayList<String>();

    public String[] asciTableColumnNames() {
        return callback.getHeadings();
    }

    public Object asciTableColumnContent(int pColIndex) {
        return content.get(pColIndex);
    }

    /**/
    public ReportAsciTableHeadingCallback getCallback() {
        return callback;
    }

    public void setCallback(ReportAsciTableHeadingCallback callback) {
        this.callback = callback;
    }

    public void addContent(String columnValue) {
        content.add(columnValue);
    }

}
