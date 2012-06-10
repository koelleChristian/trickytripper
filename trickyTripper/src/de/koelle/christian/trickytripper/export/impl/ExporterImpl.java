package de.koelle.christian.trickytripper.export.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import de.koelle.christian.common.io.FileWriter;
import de.koelle.christian.common.utils.FileUtils;
import de.koelle.christian.common.utils.StringUtils;
import de.koelle.christian.trickytripper.R;
import de.koelle.christian.trickytripper.constants.Rc;
import de.koelle.christian.trickytripper.decoupling.ActivityResolver;
import de.koelle.christian.trickytripper.decoupling.ResourceResolver;
import de.koelle.christian.trickytripper.export.Exporter;
import de.koelle.christian.trickytripper.export.StreamSender;
import de.koelle.christian.trickytripper.export.impl.content.DebtExporter;
import de.koelle.christian.trickytripper.export.impl.content.PaymentExporter;
import de.koelle.christian.trickytripper.export.impl.content.SpendingExporter;
import de.koelle.christian.trickytripper.export.impl.model.AsciTableRow;
import de.koelle.christian.trickytripper.export.impl.model.ReportAsciTable;
import de.koelle.christian.trickytripper.export.impl.model.ReportAsciTableWrapper;
import de.koelle.christian.trickytripper.export.impl.res.CsvExportCharResolver;
import de.koelle.christian.trickytripper.export.impl.res.HtmlExportCharResolver;
import de.koelle.christian.trickytripper.export.impl.res.TxtExportCharResolver;
import de.koelle.christian.trickytripper.factories.AmountFactory;
import de.koelle.christian.trickytripper.model.ExportSettings;
import de.koelle.christian.trickytripper.model.Participant;
import de.koelle.christian.trickytripper.model.Trip;
import de.koelle.christian.trickytripper.provider.TrickyTripperFileProvider;

public class ExporterImpl implements Exporter {

    private static final String SPACE = " ";
    private static final String FILE_NAME_SEPARATOR = "_";
    private static final String HTML_EXTENSION = ".html";
    private static final String CSV_EXTENSION = ".csv";
    private static final String TXT_EXTENSION = ".txt";

    private final FileWriter fileWriter;

    public ExporterImpl(FileWriter fileWriter) {
        this.fileWriter = fileWriter;

    }

    public List<File> exportReport(ExportSettings settings, List<Participant> participants, Trip trip,
            ResourceResolver resourceResolver, ActivityResolver activityResolver, AmountFactory amountFactory) {

        List<File> filesCreated = new ArrayList<File>();

        StringBuilder fileNamePrefix = new StringBuilder(resourceResolver.resolve(R.string.fileExportPrefix))
                .append(FILE_NAME_SEPARATOR)
                .append(ExporterFileNameUtils.clean(trip.getName()));
        StringBuilder timestamp = new StringBuilder(ExporterFileNameUtils.getTimeStamp(resourceResolver.getLocale()));

        HtmlExportCharResolver htmlExportCharResolver = null;
        CsvExportCharResolver csvExportCharResolver = null;
        TxtExportCharResolver txtExportCharResolver = new TxtExportCharResolver();

        if (settings.isFormatHtml()) {
            htmlExportCharResolver = new HtmlExportCharResolver();
            htmlExportCharResolver.setLang(resourceResolver.getLocale().getLanguage());
        }
        if (settings.isFormatCsv()) {
            csvExportCharResolver = new CsvExportCharResolver();
        }

        StringBuilder singleHtmlFileCollector = null;
        List<ReportAsciTableWrapper> singleTxtFileCollector = new ArrayList<ReportAsciTableWrapper>();

        if (participants.size() > 1 && settings.isSeparateFilesForIndividuals()) {
            ArrayList<Participant> participantsSubset;
            for (Participant p : participants) {
                participantsSubset = new ArrayList<Participant>(1);
                participantsSubset.add(p);

                createAndWriteFiles(settings,
                        participantsSubset,
                        trip,
                        resourceResolver,
                        fileWriter,
                        filesCreated,
                        fileNamePrefix,
                        timestamp,
                        htmlExportCharResolver,
                        csvExportCharResolver,
                        txtExportCharResolver,
                        singleHtmlFileCollector,
                        singleTxtFileCollector,
                        amountFactory);
            }
        }
        else {
            singleHtmlFileCollector = new StringBuilder();
            createAndWriteFiles(settings,
                    participants,
                    trip,
                    resourceResolver,
                    fileWriter,
                    filesCreated,
                    fileNamePrefix,
                    timestamp,
                    htmlExportCharResolver,
                    csvExportCharResolver,
                    txtExportCharResolver,
                    singleHtmlFileCollector,
                    singleTxtFileCollector,
                    amountFactory);
        }

        StreamSender streamSender = new StreamSenderImpl();
        StringBuilder exportSubject = new StringBuilder()
                .append(resourceResolver.resolve(R.string.fileExportEmailSubjectPrefix))
                .append(SPACE)
                .append(trip.getName())
                .append(SPACE)
                .append(timestamp);

        StringBuilder exportContent = buildAllTxtString(singleTxtFileCollector, txtExportCharResolver, resourceResolver);

        if (activityResolver.getActivity() != null) {
            streamSender.sendStream(
                    (Activity) activityResolver.getActivity(),
                    exportSubject.toString(),
                    exportContent.toString(),
                    FileUtils.getContentUrisFromFiles(filesCreated, TrickyTripperFileProvider.AUTHORITY));
        }

        return filesCreated;

    }

    private StringBuilder buildAllTxtString(List<ReportAsciTableWrapper> singleTxtFileCollector,
            TxtExportCharResolver txtExportCharResolver, ResourceResolver resourceResolver) {
        StringBuilder result = new StringBuilder();

        result.append(HtmlExportCharResolver.FILE_PREFIX_FOR_EMAIL.replace(HtmlExportCharResolver.PLACEHOLDER_LANG,
                "en"));
        // result.append("<code>");

        List<String> txtTables = new ArrayList<String>();
        int maxLineLength = -1;

        for (ReportAsciTableWrapper wrapper : singleTxtFileCollector) {
            String txtTable = wrapper.getOutput().toString();
            if (singleTxtFileCollector.size() > 1) {
                String[] lines = txtTable.split(Rc.LINE_FEED);
                for (String line : lines) {
                    if (line.length() > maxLineLength) {
                        maxLineLength = line.length();
                    }
                }
            }
            txtTables.add(txtTable);
        }
        // result.append("</code>");
        result.append(txtExportCharResolver.wrapInHeading(resourceResolver
                .resolve(R.string.fileExportFileHeading)));

        for (String txtTable : txtTables) {
            if (maxLineLength > -1) {
                result.append(StringUtils.generateString(maxLineLength, "-"));
            }
            result.append(txtTable);
        }

        result.append(HtmlExportCharResolver.FILE_POSTFIX);
        return result;
    }

    private void createAndWriteFiles(ExportSettings settings, List<Participant> participants, Trip trip,
            ResourceResolver resourceResolver, FileWriter fileWriter, List<File> filesCreated,
            StringBuilder fileNamePrefix, StringBuilder timestamp, HtmlExportCharResolver htmlExportCharResolver,
            CsvExportCharResolver csvExportCharResolver, TxtExportCharResolver txtExportCharResolver,
            StringBuilder singleHtmlFileCollector,
            List<ReportAsciTableWrapper> singleTxtFileCollector, AmountFactory amountFactory) {
        // reuse
        StringBuilder contents;
        File writtenFile;

        StringBuilder htmlCollector = null;
        ReportAsciTableWrapper txtCollector = new ReportAsciTableWrapper();

        if (settings.isFormatHtml()) {
            htmlCollector = new StringBuilder();
        }
        StringBuilder fileNamePrefix2 = buildFileName(fileNamePrefix, timestamp, participants, resourceResolver);

        if (settings.isExportPayments()) {

            PaymentExporter paymentExporter = new PaymentExporter();

            if (settings.isFormatCsv()) {

                paymentExporter.setCharResolver(csvExportCharResolver);
                contents = paymentExporter.prepareContents(trip, resourceResolver, participants, amountFactory);
                StringBuilder cvsFileName = new StringBuilder()
                        .append(fileNamePrefix2)
                        .append(FILE_NAME_SEPARATOR)
                        .append(resourceResolver.resolve(R.string.fileExportPostfix_Payments))
                        .append(CSV_EXTENSION);
                writtenFile = fileWriter.write(cvsFileName.toString(), contents);
                filesCreated.add(writtenFile);
            }
            if (settings.isFormatHtml()) {
                paymentExporter.setCharResolver(htmlExportCharResolver);
                contents = paymentExporter.prepareContents(trip, resourceResolver, participants, amountFactory);
                htmlCollector
                        .append(htmlExportCharResolver.wrapInSubHeading(resourceResolver
                                .resolve(R.string.fileExportTableHeadingPayments)))
                        .append(contents)
                        .append(htmlExportCharResolver.getNewLine())
                        .append(htmlExportCharResolver.getNewLine())
                /**/;
            }
            /* Text is allways prepared. */{
                paymentExporter.setCharResolver(txtExportCharResolver);
                contents = paymentExporter.prepareContents(trip, resourceResolver, participants, amountFactory);
                txtCollector.addTable(resourceResolver
                        .resolve(R.string.fileExportTableHeadingPayments), buildReportAsciiTable(contents));
                /**/;
            }
        }

        if (settings.isExportSpendings()) {

            SpendingExporter spendingExporter = new SpendingExporter();

            boolean hideTotalSum = participants.size() == 1
                    && !settings.isShowGlobalSumsOnIndividualSpendingReport();
            if (settings.isFormatCsv()) {
                spendingExporter.setCharResolver(csvExportCharResolver);
                contents = spendingExporter.prepareContents(trip, resourceResolver, participants, hideTotalSum, true);
                StringBuilder cvsFileName = new StringBuilder()
                        .append(fileNamePrefix2)
                        .append(FILE_NAME_SEPARATOR)
                        .append(resourceResolver.resolve(R.string.fileExportPostfix_Spendings))
                        .append(CSV_EXTENSION);
                writtenFile = fileWriter.write(cvsFileName.toString(), contents);
                filesCreated.add(writtenFile);
            }
            if (settings.isFormatHtml()) {
                spendingExporter.setCharResolver(htmlExportCharResolver);
                contents = spendingExporter.prepareContents(trip, resourceResolver, participants, hideTotalSum, false);
                htmlCollector
                        .append(htmlExportCharResolver.wrapInSubHeading(resourceResolver
                                .resolve(R.string.fileExportTableHeadingSpendings)))
                        .append(contents)
                        .append(htmlExportCharResolver.getNewLine())
                        .append(htmlExportCharResolver.getNewLine());
            }

            /* Text is allways prepared. */{
                spendingExporter.setCharResolver(txtExportCharResolver);
                contents = spendingExporter.prepareContents(trip, resourceResolver, participants, hideTotalSum, false);
                txtCollector.addTable(resourceResolver
                        .resolve(R.string.fileExportTableHeadingSpendings), buildReportAsciiTable(contents));
                /**/;
            }
        }
        if (settings.isExportDebts()) {

            DebtExporter debtsExporter = new DebtExporter();

            if (settings.isFormatCsv()) {

                debtsExporter.setCharResolver(csvExportCharResolver);
                contents = debtsExporter.prepareContents(trip, resourceResolver, participants);
                StringBuilder cvsFileName = new StringBuilder()
                        .append(fileNamePrefix2)
                        .append(FILE_NAME_SEPARATOR)
                        .append(resourceResolver.resolve(R.string.fileExportPostfix_Debts))
                        .append(CSV_EXTENSION);
                writtenFile = fileWriter.write(cvsFileName.toString(), contents);
                filesCreated.add(writtenFile);
            }
            if (settings.isFormatHtml()) {
                debtsExporter.setCharResolver(htmlExportCharResolver);
                contents = debtsExporter.prepareContents(trip, resourceResolver, participants);
                htmlCollector
                        .append(htmlExportCharResolver.wrapInSubHeading(resourceResolver
                                .resolve(R.string.fileExportTableHeadingDebts)))
                        .append(contents)
                        .append(htmlExportCharResolver.getNewLine())
                        .append(htmlExportCharResolver.getNewLine());
            }

            /* Text is allways prepared. */{
                debtsExporter.setCharResolver(txtExportCharResolver);
                contents = debtsExporter.prepareContents(trip, resourceResolver, participants);
                txtCollector.addTable(resourceResolver
                        .resolve(R.string.fileExportTableHeadingDebts), buildReportAsciiTable(contents));
                /**/;
            }
        }
        String[] reportMetaInfo = getReportMetaInfo(participants, trip, resourceResolver);
        txtCollector.setReportMetaInfo(txtExportCharResolver.writeReportMetaInfo(reportMetaInfo).toString());

        if (settings.isFormatHtml()) {
            htmlExportCharResolver.setTitle(fileNamePrefix2.toString());
            htmlExportCharResolver.setLang(resourceResolver.getLocale().getLanguage());

            StringBuilder finalHtmlFile = new StringBuilder();
            finalHtmlFile.append(htmlExportCharResolver.getFilePrefix());
            finalHtmlFile.append(htmlExportCharResolver.wrapInHeading(resourceResolver
                    .resolve(R.string.fileExportFileHeading)));

            finalHtmlFile.append(htmlExportCharResolver.writeReportMetaInfo(reportMetaInfo));
            finalHtmlFile.append(htmlCollector);
            finalHtmlFile.append(htmlExportCharResolver.getFilePostfix());

            StringBuilder htmlFileName = new StringBuilder()
                    .append(fileNamePrefix2)
                    .append(HTML_EXTENSION);

            if (isOnlyOneHtmlFileOutput(singleHtmlFileCollector)) {
                singleHtmlFileCollector.append(finalHtmlFile);
            }
            writtenFile = fileWriter.write(htmlFileName.toString(), finalHtmlFile);
            filesCreated.add(writtenFile);
        }
        if (settings.isFormatTxt()) {

            StringBuilder finalTxtFile = new StringBuilder();
            finalTxtFile.append(txtExportCharResolver.wrapInHeading(resourceResolver
                    .resolve(R.string.fileExportFileHeading)));

            finalTxtFile.append(txtCollector.getOutput());

            StringBuilder txtFileName = new StringBuilder()
                    .append(fileNamePrefix2)
                    .append(TXT_EXTENSION);

            writtenFile = fileWriter.write(txtFileName.toString(), finalTxtFile);
            filesCreated.add(writtenFile);
        }
        singleTxtFileCollector.add(txtCollector);
    }

    private ReportAsciTable buildReportAsciiTable(StringBuilder contents) {
        ReportAsciTable result = new ReportAsciTable();

        String[] rows = contents.toString().split(TxtExportCharResolver.TXT_ROW_END_DELIMITER);
        for (int i = 0; i < rows.length; i++) {
            String[] rowValues = rows[i].split(TxtExportCharResolver.TXT_VALUE_DELIMITER);
            AsciTableRow rowObj = null;
            if (i != 0) {
                rowObj = new AsciTableRow();
            }

            for (String val : rowValues) {
                if (i == 0) {
                    result.addHeading(val);
                }
                else {
                    rowObj.addContent(val);
                }
            }
            if (rowObj != null) {
                result.addRow(rowObj);
            }
        }
        return result;
    }

    private String[] getReportMetaInfo(List<Participant> participants, Trip trip, ResourceResolver resourceResolver) {
        String tripDescription = resourceResolver.resolve(R.string.fileExportFileSummaryEventPrefix) + " "
                + trip.getName();
        String reportIsForPostFix = (participants.size() > 1) ? resourceResolver
                .resolve(R.string.fileExportFileSummaryReportForPrefixAll) : participants.get(0).getName();
        String reportIsForDescription = resourceResolver.resolve(R.string.fileExportFileSummaryReportForPrefix)
                + " "
                + reportIsForPostFix;

        String[] reportMetaInfo = new String[2];
        reportMetaInfo[0] = tripDescription;
        reportMetaInfo[1] = reportIsForDescription;
        return reportMetaInfo;
    }

    private boolean isOnlyOneHtmlFileOutput(StringBuilder singleHtmlFileCollector) {
        return singleHtmlFileCollector != null;
    }

    private StringBuilder buildFileName(StringBuilder fileNamePrefix, StringBuilder timestamp,
            List<Participant> participants, ResourceResolver resourceResolver) {
        return new StringBuilder()
                .
                append(fileNamePrefix)
                .
                append(FILE_NAME_SEPARATOR)
                .
                append(
                        (participants.size() > 1) ?
                                resourceResolver.resolve(R.string.fileExportrFix_Scope_All) :
                                ExporterFileNameUtils.clean(participants.get(0).getName()))
                .
                append(FILE_NAME_SEPARATOR)
                .
                append(timestamp);
    }
}
