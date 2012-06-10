package de.koelle.christian.trickytripper.model;

public class ExportSettings {

    private boolean exportPayments = true;
    private boolean exportSpendings = true;
    private boolean exportDebts = true;
    private boolean formatHtml = true;
    private boolean formatCsv = true;
    private boolean formatTxt = true;
    private boolean separateFilesForIndividuals = false;
    private boolean showGlobalSumsOnIndividualSpendingReport = true;

    public boolean isExportSpendings() {
        return exportSpendings;
    }

    public void setExportSpendings(boolean exportPayments) {
        this.exportSpendings = exportPayments;
    }

    public boolean isExportDebts() {
        return exportDebts;
    }

    public void setExportDebts(boolean exportDebts) {
        this.exportDebts = exportDebts;
    }

    public boolean isFormatHtml() {
        return formatHtml;
    }

    public void setFormatHtml(boolean formatHtml) {
        this.formatHtml = formatHtml;
    }

    public boolean isFormatCsv() {
        return formatCsv;
    }

    public void setFormatCsv(boolean formatCsv) {
        this.formatCsv = formatCsv;
    }

    public boolean isShowGlobalSumsOnIndividualSpendingReport() {
        return showGlobalSumsOnIndividualSpendingReport;
    }

    public void setShowGlobalSumsOnIndividualSpendingReport(boolean showGlobalSumsOnIndividualSpendingReport) {
        this.showGlobalSumsOnIndividualSpendingReport = showGlobalSumsOnIndividualSpendingReport;
    }

    public boolean isSeparateFilesForIndividuals() {
        return separateFilesForIndividuals;
    }

    public void setSeparateFilesForIndividuals(boolean separateFilesForIndividuals) {
        this.separateFilesForIndividuals = separateFilesForIndividuals;
    }

    public boolean isExportPayments() {
        return exportPayments;
    }

    public void setExportPayments(boolean exportPayments) {
        this.exportPayments = exportPayments;
    }

    public boolean isFormatTxt() {
        return formatTxt;
    }

    public void setFormatTxt(boolean formattxt) {
        this.formatTxt = formattxt;
    }

    @Override
    public String toString() {
        return "ExportSettings [exportPayments=" + exportPayments + ", exportSpendings=" + exportSpendings
                + ", exportDebts=" + exportDebts + ", formatHtml=" + formatHtml + ", formatCsv=" + formatCsv
                + ", formatTxt=" + formatTxt + ", separateFilesForIndividuals=" + separateFilesForIndividuals
                + ", showGlobalSumsOnIndividualSpendingReport=" + showGlobalSumsOnIndividualSpendingReport + "]";
    }

}
