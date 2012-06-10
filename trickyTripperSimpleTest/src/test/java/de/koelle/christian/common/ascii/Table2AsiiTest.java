package de.koelle.christian.common.ascii;

import java.util.Random;

import org.junit.Test;

import de.koelle.christian.trickytripper.export.impl.model.ReportAsciTableRow;
import de.koelle.christian.trickytripper.export.impl.model.ReportAsciTable;
import de.koelle.christian.trickytripper.export.impl.model.ReportAsciTableWrapper;

public class Table2AsiiTest {

    @Test
    public void testAsciiTableOutput() {
        ReportAsciTableWrapper report = new ReportAsciTableWrapper();

        ReportAsciTable table = null;
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
        ReportAsciTableRow row;
        for (int j = 0; j < rows; j++) {
            row = new ReportAsciTableRow();
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
