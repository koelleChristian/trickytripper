package de.koelle.christian.trickytripper.export.impl.model;

import java.util.ArrayList;
import java.util.List;

import de.koelle.christian.common.ascii.AsciTableLayoutableInterface;

public class AsciTableRow implements AsciTableLayoutableInterface {
    private AsciTableHeadingCallback callback;
    private final List<String> content = new ArrayList<String>();

    public String[] asciTableColumnNames() {
        return callback.getHeadings();
    }

    public Object asciTableColumnContent(int pColIndex) {
        return content.get(pColIndex);
    }

    /**/
    public AsciTableHeadingCallback getCallback() {
        return callback;
    }

    public void setCallback(AsciTableHeadingCallback callback) {
        this.callback = callback;
    }

    public void addContent(String columnValue) {
        content.add(columnValue);
    }

}
