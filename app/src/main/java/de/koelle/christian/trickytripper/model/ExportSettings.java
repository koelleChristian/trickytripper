package de.koelle.christian.trickytripper.model;

import java.util.Arrays;
import java.util.List;

import de.koelle.christian.trickytripper.R;

public class ExportSettings {

    public enum ExportOutputChannel implements ResourceLabelAwareEnumeration {
        /***/
        SD_CARD(R.string.exportOutputChannelEnumSave2Sd, "de.koelle.christian.trickytripper"),
        /***/
        MAIL(R.string.exportOutputChannelMail, "com.google.android.gm"),
        /***/
        DROPBOX(R.string.exportOutputChannelDropbox, "com.dropbox.android"),
        /***/
        EVERNOTE(R.string.exportOutputChannelEvernote, "com.evernote"),
        /***/
        K9(R.string.exportOutputChannelK9, "com.fsck.k9"),
        /***/
        OWNCLOUD(R.string.exportOutputChannelOwncloud, "com.owncloud.android"),
        /***/
        BOXER(R.string.exportOutputChannelBoxer, "com.boxer.email"),
        /**/
        ;
        // com.android.bluetooth
        // com.google.android.apps.uploader
        // com.google.android.apps.uploader

        private final int resourceId;
        private final String packageName;

        private ExportOutputChannel(int resourceId, String packageName) {
            this.resourceId = resourceId;
            this.packageName = packageName;
        }

        public int getResourceStringId() {
            return this.resourceId;
        }

        public List<ResourceLabelAwareEnumeration> getAllValues() {
            return Arrays.asList((ResourceLabelAwareEnumeration[]) values());
        }

        public String getPackageName() {
            return packageName;
        }

    }

    private boolean exportPayments = true;
    private boolean exportTransfers = true;
    private boolean exportSpending = true;
    private boolean exportDebts = true;
    private boolean formatHtml = true;
    private boolean formatCsv = false;
    private boolean formatTxt = false;
    private boolean separateFilesForIndividuals = false;
    private boolean showGlobalSumsOnIndividualSpendingReport = true;
    private ExportOutputChannel outputChannel = ExportOutputChannel.MAIL;

    public boolean isExportSpending() {
        return exportSpending;
    }

    public void setExportSpending(boolean exportPayments) {
        this.exportSpending = exportPayments;
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

    public void setFormatTxt(boolean formatTxt) {
        this.formatTxt = formatTxt;
    }

    public ExportOutputChannel getOutputChannel() {
        return outputChannel;
    }

    public void setOutputChannel(ExportOutputChannel outputChannel) {
        this.outputChannel = outputChannel;
    }

    public boolean isExportTransfers() {
        return exportTransfers;
    }

    public void setExportTransfers(boolean exportTransfers) {
        this.exportTransfers = exportTransfers;
    }

    @Override
    public String toString() {
        return "ExportSettings [exportPayments=" + exportPayments + ", exportTransfers=" + exportTransfers
                + ", exportSpending=" + exportSpending + ", exportDebts=" + exportDebts + ", formatHtml="
                + formatHtml + ", formatCsv=" + formatCsv + ", formatTxt=" + formatTxt
                + ", separateFilesForIndividuals=" + separateFilesForIndividuals
                + ", showGlobalSumsOnIndividualSpendingReport=" + showGlobalSumsOnIndividualSpendingReport
                + ", outputChannel=" + outputChannel + "]";
    }

}
