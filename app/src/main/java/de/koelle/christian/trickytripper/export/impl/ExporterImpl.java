package de.koelle.christian.trickytripper.export.impl;

import android.app.Activity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.koelle.christian.common.io.FileWriter;
import de.koelle.christian.common.utils.FileUtils;
import de.koelle.christian.trickytripper.R;
import de.koelle.christian.trickytripper.constants.Rc;
import de.koelle.christian.trickytripper.decoupling.ActivityResolver;
import de.koelle.christian.trickytripper.decoupling.ResourceResolver;
import de.koelle.christian.trickytripper.export.Exporter;
import de.koelle.christian.trickytripper.export.StreamSender;
import de.koelle.christian.trickytripper.export.impl.content.DebtTableExporter;
import de.koelle.christian.trickytripper.export.impl.content.PaymentTableExporter;
import de.koelle.christian.trickytripper.export.impl.content.SpendingTableExporter;
import de.koelle.christian.trickytripper.export.impl.content.TransferTableExporter;
import de.koelle.christian.trickytripper.export.impl.model.ReportAsciTableUtils;
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
        TxtExportCharResolver txtExportCharResolver = null;

        if (settings.isFormatHtml()) {
            htmlExportCharResolver = new HtmlExportCharResolver();
            htmlExportCharResolver.setLang(resourceResolver.getLocale().getLanguage());
        }
        if (settings.isFormatCsv()) {
            csvExportCharResolver = new CsvExportCharResolver();
        }
        if (settings.isFormatTxt()) {
            txtExportCharResolver = new TxtExportCharResolver();
        }

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
                        amountFactory);
            }
        } else {
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
                    amountFactory);
        }

        StreamSender streamSender = new StreamSenderImpl();
        StringBuilder exportSubject = new StringBuilder()
                .append(resourceResolver.resolve(R.string.fileExportEmailSubjectPrefix))
                .append(SPACE)
                .append(trip.getName())
                .append(SPACE)
                .append(timestamp);

        if (activityResolver.getActivity() != null) {
            streamSender.sendStream(
                    (Activity) activityResolver.getActivity(),
                    exportSubject.toString(),
                    resourceResolver.resolve(R.string.fileExportEmailContent),
                    FileUtils.getContentUrisFromFiles(filesCreated, TrickyTripperFileProvider.AUTHORITY),
                    settings.getOutputChannel());
        }

        return filesCreated;

    }

    private void createAndWriteFiles(ExportSettings settings, List<Participant> participants, Trip trip,
                                     ResourceResolver resourceResolver, FileWriter fileWriter, List<File> filesCreated,
                                     StringBuilder fileNamePrefix, StringBuilder timestamp, HtmlExportCharResolver htmlExportCharResolver,
                                     CsvExportCharResolver csvExportCharResolver, TxtExportCharResolver txtExportCharResolver,
                                     AmountFactory amountFactory) {
        // reuse
        StringBuilder contents;
        File writtenFile;

        StringBuilder htmlCollector = null;
        ReportAsciTableWrapper txtCollector = null;

        if (settings.isFormatTxt()) {
            txtCollector = new ReportAsciTableWrapper();
        }

        if (settings.isFormatHtml()) {
            htmlCollector = new StringBuilder();
        }
        StringBuilder fileNamePrefix2 = buildFileName(fileNamePrefix, timestamp, participants, resourceResolver);

        if (settings.isExportPayments()) {

            PaymentTableExporter paymentExporter = new PaymentTableExporter();

            if (settings.isFormatCsv()) {

                paymentExporter.setCharResolver(csvExportCharResolver);
                contents = paymentExporter.prepareContents(trip, resourceResolver, participants, amountFactory);
                StringBuilder cvsFileName = new StringBuilder()
                        .append(fileNamePrefix2)
                        .append(FILE_NAME_SEPARATOR)
                        .append(resourceResolver.resolve(R.string.fileExportPostfix_Payments))
                        .append(Rc.CSV_EXTENSION);
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
            if (settings.isFormatTxt()) {

                paymentExporter.setCharResolver(txtExportCharResolver);
                contents = paymentExporter.prepareContents(trip, resourceResolver, participants, amountFactory);
                txtCollector.addTable(resourceResolver
                                .resolve(R.string.fileExportTableHeadingPayments),
                        ReportAsciTableUtils.buildReportAsciiTable(contents));
            }

        }
        if (settings.isExportTransfers()) {

            TransferTableExporter transferExporter = new TransferTableExporter();

            if (settings.isFormatCsv()) {

                transferExporter.setCharResolver(csvExportCharResolver);
                contents = transferExporter.prepareContents(trip, resourceResolver, participants);
                StringBuilder cvsFileName = new StringBuilder()
                        .append(fileNamePrefix2)
                        .append(FILE_NAME_SEPARATOR)
                        .append(resourceResolver.resolve(R.string.fileExportPostfix_Transfers))
                        .append(Rc.CSV_EXTENSION);
                writtenFile = fileWriter.write(cvsFileName.toString(), contents);
                filesCreated.add(writtenFile);
            }
            if (settings.isFormatHtml()) {

                transferExporter.setCharResolver(htmlExportCharResolver);
                contents = transferExporter.prepareContents(trip, resourceResolver, participants);
                htmlCollector
                        .append(htmlExportCharResolver.wrapInSubHeading(resourceResolver
                                .resolve(R.string.fileExportTableHeadingTransfers)))
                        .append(contents)
                        .append(htmlExportCharResolver.getNewLine())
                        .append(htmlExportCharResolver.getNewLine())
                /**/;
            }
            if (settings.isFormatTxt()) {

                transferExporter.setCharResolver(txtExportCharResolver);
                contents = transferExporter.prepareContents(trip, resourceResolver, participants);
                txtCollector.addTable(resourceResolver
                                .resolve(R.string.fileExportTableHeadingTransfers),
                        ReportAsciTableUtils.buildReportAsciiTable(contents));
            }
        }

        if (settings.isExportSpending()) {

            SpendingTableExporter spendingExporter = new SpendingTableExporter();

            boolean hideTotalSum = participants.size() == 1
                    && !settings.isShowGlobalSumsOnIndividualSpendingReport();

            if (settings.isFormatCsv()) {

                spendingExporter.setCharResolver(csvExportCharResolver);
                contents = spendingExporter.prepareContents(trip, resourceResolver, participants, hideTotalSum, true);
                StringBuilder cvsFileName = new StringBuilder()
                        .append(fileNamePrefix2)
                        .append(FILE_NAME_SEPARATOR)
                        .append(resourceResolver.resolve(R.string.fileExportPostfix_Spendings))
                        .append(Rc.CSV_EXTENSION);
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
            if (settings.isFormatTxt()) {

                spendingExporter.setCharResolver(txtExportCharResolver);
                contents = spendingExporter.prepareContents(trip, resourceResolver, participants, hideTotalSum, false);
                txtCollector.addTable(resourceResolver
                                .resolve(R.string.fileExportTableHeadingSpendings),
                        ReportAsciTableUtils.buildReportAsciiTable(contents));
            }
        }
        if (settings.isExportDebts()) {

            DebtTableExporter debtsExporter = new DebtTableExporter();

            if (settings.isFormatCsv()) {

                debtsExporter.setCharResolver(csvExportCharResolver);
                contents = debtsExporter.prepareContents(trip, resourceResolver, participants);
                StringBuilder cvsFileName = new StringBuilder()
                        .append(fileNamePrefix2)
                        .append(FILE_NAME_SEPARATOR)
                        .append(resourceResolver.resolve(R.string.fileExportPostfix_Debts))
                        .append(Rc.CSV_EXTENSION);
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
            if (settings.isFormatTxt()) {

                debtsExporter.setCharResolver(txtExportCharResolver);
                contents = debtsExporter.prepareContents(trip, resourceResolver, participants);
                txtCollector.addTable(resourceResolver
                                .resolve(R.string.fileExportTableHeadingDebts),
                        ReportAsciTableUtils.buildReportAsciiTable(contents));
            }
        }
        if (settings.isFormatHtml() || settings.isFormatTxt()) {

            String[] reportMetaInfo = getReportMetaInfo(participants, trip, resourceResolver);
            String fileHeadingString = resourceResolver.resolve(R.string.fileExportFileHeading);

            if (settings.isFormatHtml()) {
                htmlExportCharResolver.setTitle(fileNamePrefix2.toString());
                htmlExportCharResolver.setLang(resourceResolver.getLocale().getLanguage());

                StringBuilder finalHtmlFile = new StringBuilder();
                finalHtmlFile.append(htmlExportCharResolver.getFilePrefix());
                finalHtmlFile.append(htmlExportCharResolver.wrapInHeading(fileHeadingString));

                finalHtmlFile.append(htmlExportCharResolver.writeReportMetaInfo(reportMetaInfo));
                finalHtmlFile.append(htmlCollector);
                finalHtmlFile.append(htmlExportCharResolver.getFilePostfix());

                StringBuilder htmlFileName = new StringBuilder()
                        .append(fileNamePrefix2)
                        .append(Rc.HTML_EXTENSION);

                writtenFile = fileWriter.write(htmlFileName.toString(), finalHtmlFile);
                filesCreated.add(writtenFile);
            }
            if (settings.isFormatTxt()) {

                StringBuilder finalTxtFile = new StringBuilder();
                finalTxtFile.append(txtExportCharResolver.wrapInHeading(fileHeadingString));

                txtCollector.setReportMetaInfo(txtExportCharResolver.writeReportMetaInfo(reportMetaInfo).toString());
                finalTxtFile.append(txtCollector.getOutput());

                StringBuilder txtFileName = new StringBuilder()
                        .append(fileNamePrefix2)
                        .append(Rc.TXT_EXTENSION);

                writtenFile = fileWriter.write(txtFileName.toString(), finalTxtFile);
                filesCreated.add(writtenFile);
            }
        }
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
