package de.koelle.christian.common.ascii;

import org.junit.Ignore;
import org.junit.Test;

import java.util.Random;

import de.koelle.christian.trickytripper.export.impl.model.ReportAsciTable;
import de.koelle.christian.trickytripper.export.impl.model.ReportAsciTableLayoutTableRow;
import de.koelle.christian.trickytripper.export.impl.model.ReportAsciTableWrapper;

public class Table2AsiiTest {

    @Ignore("This is a manual test, which outputs the table on system.out. It can be enabled on demand.")
    @Test
    public void testAsciiTableOutput() {
        ReportAsciTableWrapper report = new ReportAsciTableWrapper();

        ReportAsciTable table;
        String heading = null;
        int rows;
        int columns;
        Random randomContent = new Random();

        heading = "My first table";

        table = new ReportAsciTable();
        table.addHeading("A");
        table.addHeading("Whatever");
        table.addHeading("Very long, long, long, long heading");

        rows = 10;
        columns = 3;

        addRows(table, rows, columns, randomContent);

        report.addTable(heading, table);

        heading = "My second table";

        table = new ReportAsciTable();
        table.addHeading("xxxxxxxxxxxx");
        table.addHeading("zzzzz");

        rows = 5;
        columns = 2;

        addRows(table, rows, columns, randomContent);

        report.addTable(heading, table);

        System.out.println(report.getOutput());
    }

    private void addRows(ReportAsciTable table, int rows, int columns, Random randomContent) {
        ReportAsciTableLayoutTableRow row;
        for (int j = 0; j < rows; j++) {
            row = new ReportAsciTableLayoutTableRow();
            for (int i = 0; i < columns; i++) {
                if (i % 3 == 2) {
                    row.addContent(randomContent.nextInt(10000) + ",00");
                }
                row.addContent(i + "_" + randomContent.nextLong());
            }
            table.addRow(row);
        }
    }
}
