package de.koelle.christian.trickytripper.export.impl;

import junit.framework.Assert;
import android.test.ApplicationTestCase;
import de.koelle.christian.trickytripper.TrickyTripperApp;
import de.koelle.christian.trickytripper.export.Exporter;
import de.koelle.christian.trickytripper.factories.AmountFactory;
import de.koelle.christian.trickytripper.model.ExportSettings;
import de.koelle.christian.trickytripper.model.Participant;
import de.koelle.christian.trickytripper.model.Trip;
import de.koelle.christian.trickytripper.strategies.TripReportLogic;
import de.koelle.christian.trickytripper.testsupport.TestDataFactory;
import de.koelle.christian.trickytripper.testsupport.TestDataSet;
import de.koelle.christian.trickytripper.testsupport.TransientDataHelper;

public class ExportImplIntegrationTest extends ApplicationTestCase<TrickyTripperApp> {

    private Exporter exporter;
    private Trip tripToExport;

    private Participant participant;

    public ExportImplIntegrationTest() {
        super(TrickyTripperApp.class);
    }

    @Override
    protected void setUp() {

        exporter = null; // new ExporterImpl(getContext());

        TestDataFactory factory = new TestDataFactory();
        tripToExport = factory.createTestData(TestDataSet.DEFAULT);
        TransientDataHelper.updateAllTransientData(tripToExport, new
                TripReportLogic(), new AmountFactory());

        participant = factory.wolfram;

        createApplication();

    }

    public void testFolderAvailability() {
        Assert.assertNotNull(getContext().getFilesDir());
        // /data/data/de.koelle.christian.trickytripper/files
        Assert.assertNotNull(getContext().getCacheDir());
        // /data/data/de.koelle.christian.trickytripper/cache
    }

    public void testOutputEverythingForAll() {
        // ExportSettings exportSettings = createExportSettings(true, true,
        // true,
        // true);
        // exporter.exportReport(exportSettings, tripToExport.getParticipant(),
        // tripToExport, new ResourceResolverImpl(
        // getContext().getResources()
        // ), null);
    }

    //
    // public void testOutputEverythingOne() {
    // ExportSettings exportSettings = createExportSettings(true, true, true,
    // true, true);
    // List<Participant> participants = new ArrayList<Participant>();
    // participants.add(participant);
    // exporter.exportReport(exportSettings, participants, tripToExport, new
    // ResourceResolverImpl(
    // getContext().getResources(),
    // null));
    // }

    private ExportSettings createExportSettings(boolean exportDebts, boolean exportPayments, boolean formatCsv,
            boolean formatHtml) {
        ExportSettings exportSettings = new ExportSettings();
        exportSettings.setExportDebts(exportDebts);
        exportSettings.setExportSpendings(exportPayments);
        exportSettings.setFormatCsv(formatCsv);
        exportSettings.setFormatHtml(formatHtml);
        return exportSettings;
    }
}
