package de.koelle.christian.common.ascii;

import java.util.Random;

import org.junit.Test;

import de.koelle.christian.trickytripper.export.impl.model.AsciTableRow;
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

    @Test
    public void myJunk() {
        String s = "Zahlung%Kategorie%Betrag [EUR]%Zahlung von [EUR]%%%Belastet [EUR]%% ";
        String[] array = s.split("%");
        for (String s1 : array) {
            System.out.println(">" + s1 + "<");
        }
    }

    private void addRows(ReportAsciTable table, int rows, int columns, Random randomContent) {
        AsciTableRow row;
        for (int j = 0; j < rows; j++) {
            row = new AsciTableRow();
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
