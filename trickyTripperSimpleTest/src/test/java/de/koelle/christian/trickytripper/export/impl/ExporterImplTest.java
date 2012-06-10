package de.koelle.christian.trickytripper.export.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import de.koelle.christian.common.io.FileWriter;
import de.koelle.christian.trickytripper.R;
import de.koelle.christian.trickytripper.decoupling.ActivityResolver;
import de.koelle.christian.trickytripper.decoupling.ResourceResolver;
import de.koelle.christian.trickytripper.export.Exporter;
import de.koelle.christian.trickytripper.factories.AmountFactory;
import de.koelle.christian.trickytripper.model.ExportSettings;
import de.koelle.christian.trickytripper.model.Participant;
import de.koelle.christian.trickytripper.model.Trip;
import de.koelle.christian.trickytripper.strategies.TripReportLogic;
import de.koelle.christian.trickytripper.testsupport.TestDataFactory;
import de.koelle.christian.trickytripper.testsupport.TestDataSet;
import de.koelle.christian.trickytripper.testsupport.TransientDataHelper;

public class ExporterImplTest {

    /* 2131034159_Greece2010Summer_Wolfram_201206031502_2131034161.csv */
    /* TTE_MyFirstTrip_Christian_201205102350_P.cvs */

    private final Pattern patternCsvIndividual = Pattern.compile(
            "^\\d{10}_\\w+_\\w+_\\d{12}_\\d{10}\\.csv$");
    private final Pattern patternCsvAll = Pattern.compile(
            "^\\d{10}_\\w+_\\d{10}_\\d{12}_\\d{10}\\.csv$");
    private final Pattern patternHtmlIndividual = Pattern.compile(
            "^\\d{10}_\\w+_\\w+_\\d{12}\\.html$");
    private final Pattern patternHtmlAll = Pattern.compile(
            "^\\d{10}_\\w+\\d{10}_\\d{12}\\.html$");

    private final boolean deleteCreatedFiles = false;

    private Exporter exporter;
    private Trip tripToExport;
    private ResourceResolver resourceResolver;

    private Participant participant;

    private AmountFactory amountFactory;
    private TestDataFactory factory;

    @Before
    public void init() {
        exporter = new ExporterImpl(new FileWriter() {

            public File write(String filenName, StringBuilder contents) {
                FileOutputStream fos = null;
                File file = new File(filenName);
                try {
                    fos = new FileOutputStream(file);
                    fos.write(contents.toString().getBytes());
                }
                catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
                finally {
                    try {
                        fos.close();
                        // fos.getFD().sync(); // comes with exceptions in tests
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return file;
            }
        });
        factory = new TestDataFactory();
        amountFactory = factory.amountFactory;
        tripToExport = factory.createTestData(TestDataSet.DEFAULT);
        TransientDataHelper.updateAllTransientData(tripToExport, new
                TripReportLogic(), amountFactory);

        participant = factory.wolfram;

        resourceResolver = Mockito.mock(ResourceResolver.class);
        Mockito.when(resourceResolver.getLocale()).thenReturn(Locale.GERMANY);
        Mockito.when(resourceResolver.resolve(Mockito.anyInt())).thenAnswer(new
                Answer<String>() {

                    public String answer(InvocationOnMock invocation) throws Throwable {
                        Object[] args = invocation.getArguments();
                        return args[0] + "";
                    }

                });
    }

    @Test
    public void testOutputEverythingForAll() {
        ExportSettings exportSettings = createExportSettings(true, true, true, true, true, false, false);
        List<File> result = exporter.exportReport(exportSettings, tripToExport.getParticipant(), tripToExport,
                resourceResolver,
                new ActivityResolver() {

                    public Object getActivity() {
                        return null;
                    }
                },
                amountFactory);

        Throwable exceptionCaught = null;
        try {
            Assert.assertEquals(4, result.size());
            Assert.assertTrue(patternCsvAll.matcher(result.get(0).getName()).matches());
            Assert.assertTrue(patternCsvAll.matcher(result.get(1).getName()).matches());
            Assert.assertTrue(patternCsvAll.matcher(result.get(2).getName()).matches());
            Assert.assertTrue(patternHtmlAll.matcher(result.get(3).getName()).matches());
            for (File f : result) {
                Assert.assertTrue(nameContains(f, resourceResolver.resolve(R.string.fileExportrFix_Scope_All)));
            }
        }
        catch (Exception e) {
            exceptionCaught = e;
        }
        finally {
            deleteCreatedFiles(result);
        }
        if (exceptionCaught != null) {
            throw new RuntimeException(exceptionCaught);
        }
    }

    @Test
    public void testOutputCsvForAll() {
        ExportSettings exportSettings = createExportSettings(true, true, true, true, false, false, false);
        List<File> result = exporter.exportReport(exportSettings, tripToExport.getParticipant(), tripToExport,
                resourceResolver,
                new ActivityResolver() {

                    public Object getActivity() {
                        return null;
                    }
                },
                amountFactory);

        Throwable exceptionCaught = null;
        try {
            Assert.assertEquals(3, result.size());
            Assert.assertTrue(patternCsvAll.matcher(result.get(0).getName()).matches());
            Assert.assertTrue(patternCsvAll.matcher(result.get(1).getName()).matches());
            Assert.assertTrue(patternCsvAll.matcher(result.get(2).getName()).matches());
            for (File f : result) {
                Assert.assertTrue(nameContains(f, resourceResolver.resolve(R.string.fileExportrFix_Scope_All)));
            }
        }
        catch (Exception e) {
            exceptionCaught = e;
        }
        finally {
            deleteCreatedFiles(result);
        }
        if (exceptionCaught != null) {
            throw new RuntimeException(exceptionCaught);
        }
    }

    @Test
    public void testOutputHtmlForAll() {
        ExportSettings exportSettings = createExportSettings(true, true, true, false, true, false, false);
        List<File> result = exporter.exportReport(exportSettings, tripToExport.getParticipant(), tripToExport,
                resourceResolver,
                new ActivityResolver() {

                    public Object getActivity() {
                        return null;
                    }
                },
                amountFactory);

        Throwable exceptionCaught = null;
        try {
            Assert.assertEquals(1, result.size());
            Assert.assertTrue(patternHtmlAll.matcher(result.get(0).getName()).matches());
            Assert.assertTrue(nameContains(result.get(0), resourceResolver.resolve(R.string.fileExportrFix_Scope_All)));
        }
        catch (Exception e) {
            exceptionCaught = e;
        }
        finally {
            deleteCreatedFiles(result);
        }
        if (exceptionCaught != null) {
            throw new RuntimeException(exceptionCaught);
        }
    }

    @Test
    public void testOutputPaymentsForAll() {
        ExportSettings exportSettings = createExportSettings(false, false, true, true, true, false, false);
        List<File> result = exporter.exportReport(exportSettings, tripToExport.getParticipant(), tripToExport,
                resourceResolver,
                new ActivityResolver() {

                    public Object getActivity() {
                        return null;
                    }
                },
                amountFactory);

        Throwable exceptionCaught = null;
        try {
            Assert.assertEquals(2, result.size());
            Assert.assertTrue(patternCsvAll.matcher(result.get(0).getName()).matches());
            Assert.assertTrue(patternHtmlAll.matcher(result.get(1).getName()).matches());
            for (File f : result) {
                Assert.assertTrue(nameContains(f, resourceResolver.resolve(R.string.fileExportrFix_Scope_All)));
            }
            Assert.assertTrue(nameContains(result.get(0), resourceResolver.resolve(R.string.fileExportPostfix_Payments)));
        }
        catch (Exception e) {
            exceptionCaught = e;
        }
        finally {
            deleteCreatedFiles(result);
        }
        if (exceptionCaught != null) {
            throw new RuntimeException(exceptionCaught);
        }
    }

    @Test
    public void testOutputSpendingsForAll() {
        ExportSettings exportSettings = createExportSettings(false, true, false, true, true, false, false);
        List<File> result = exporter.exportReport(exportSettings, tripToExport.getParticipant(), tripToExport,
                resourceResolver,
                new ActivityResolver() {

                    public Object getActivity() {
                        return null;
                    }
                },
                amountFactory);

        Throwable exceptionCaught = null;
        try {
            Assert.assertEquals(2, result.size());
            Assert.assertTrue(patternCsvAll.matcher(result.get(0).getName()).matches());
            Assert.assertTrue(patternHtmlAll.matcher(result.get(1).getName()).matches());
            for (File f : result) {
                Assert.assertTrue(nameContains(f, resourceResolver.resolve(R.string.fileExportrFix_Scope_All)));
            }
            Assert.assertTrue(nameContains(result.get(0),
                    resourceResolver.resolve(R.string.fileExportPostfix_Spendings)));
        }
        catch (Exception e) {
            exceptionCaught = e;
        }
        finally {
            deleteCreatedFiles(result);
        }
        if (exceptionCaught != null) {
            throw new RuntimeException(exceptionCaught);
        }
    }

    @Test
    public void testOutputDebtsForAll() {
        ExportSettings exportSettings = createExportSettings(true, false, false, true, true, false, false);
        List<File> result = exporter.exportReport(exportSettings, tripToExport.getParticipant(), tripToExport,
                resourceResolver,
                new ActivityResolver() {

                    public Object getActivity() {
                        return null;
                    }
                },
                amountFactory);

        Throwable exceptionCaught = null;
        try {
            Assert.assertEquals(2, result.size());
            Assert.assertTrue(patternCsvAll.matcher(result.get(0).getName()).matches());
            Assert.assertTrue(patternHtmlAll.matcher(result.get(1).getName()).matches());
            for (File f : result) {
                Assert.assertTrue(nameContains(f, resourceResolver.resolve(R.string.fileExportrFix_Scope_All)));
            }
            Assert.assertTrue(nameContains(result.get(0),
                    resourceResolver.resolve(R.string.fileExportPostfix_Debts)));
        }
        catch (Exception e) {
            exceptionCaught = e;
        }
        finally {
            deleteCreatedFiles(result);
        }
        if (exceptionCaught != null) {
            throw new RuntimeException(exceptionCaught);
        }
    }

    @Test
    public void testOutputSpendingsAndDebtsForAll() {
        ExportSettings exportSettings = createExportSettings(true, true, false, true, true, false, false);
        List<File> result = exporter.exportReport(exportSettings, tripToExport.getParticipant(), tripToExport,
                resourceResolver,
                new ActivityResolver() {

                    public Object getActivity() {
                        return null;
                    }
                },
                amountFactory);

        Throwable exceptionCaught = null;
        try {
            Assert.assertEquals(3, result.size());
            Assert.assertTrue(patternCsvAll.matcher(result.get(0).getName()).matches());
            Assert.assertTrue(patternCsvAll.matcher(result.get(1).getName()).matches());
            Assert.assertTrue(patternHtmlAll.matcher(result.get(2).getName()).matches());
            for (File f : result) {
                Assert.assertTrue(nameContains(f, resourceResolver.resolve(R.string.fileExportrFix_Scope_All)));
            }
            Assert.assertTrue(nameContains(result.get(0),
                    resourceResolver.resolve(R.string.fileExportPostfix_Spendings)));
            Assert.assertTrue(nameContains(result.get(1),
                    resourceResolver.resolve(R.string.fileExportPostfix_Debts)));
        }
        catch (Exception e) {
            exceptionCaught = e;
        }
        finally {
            deleteCreatedFiles(result);
        }
        if (exceptionCaught != null) {
            throw new RuntimeException(exceptionCaught);
        }
    }

    @Test
    public void testOutputSpendingsAndDebtsOnlyHtmlForAll() {
        ExportSettings exportSettings = createExportSettings(true, true, false, false, true, false, false);
        List<File> result = exporter.exportReport(exportSettings, tripToExport.getParticipant(), tripToExport,
                resourceResolver,
                new ActivityResolver() {

                    public Object getActivity() {
                        return null;
                    }
                },
                amountFactory);

        Throwable exceptionCaught = null;
        try {
            Assert.assertEquals(1, result.size());
            Assert.assertTrue(patternHtmlAll.matcher(result.get(0).getName()).matches());
            Assert.assertTrue(nameContains(result.get(0), resourceResolver.resolve(R.string.fileExportrFix_Scope_All)));
        }
        catch (Exception e) {
            exceptionCaught = e;
        }
        finally {
            deleteCreatedFiles(result);
        }
        if (exceptionCaught != null) {
            throw new RuntimeException(exceptionCaught);
        }
    }

    @Test
    public void testOutputSpendingsAndDebtsOnlyCsvForAll() {
        ExportSettings exportSettings = createExportSettings(true, true, false, true, false, false, false);
        List<File> result = exporter.exportReport(exportSettings, tripToExport.getParticipant(), tripToExport,
                resourceResolver,
                new ActivityResolver() {

                    public Object getActivity() {
                        return null;
                    }
                },
                amountFactory);

        Throwable exceptionCaught = null;
        try {
            Assert.assertEquals(2, result.size());
            Assert.assertTrue(patternCsvAll.matcher(result.get(0).getName()).matches());
            Assert.assertTrue(patternCsvAll.matcher(result.get(1).getName()).matches());
            for (File f : result) {
                Assert.assertTrue(nameContains(f, resourceResolver.resolve(R.string.fileExportrFix_Scope_All)));
            }
            Assert.assertTrue(nameContains(result.get(0),
                    resourceResolver.resolve(R.string.fileExportPostfix_Spendings)));
            Assert.assertTrue(nameContains(result.get(1),
                    resourceResolver.resolve(R.string.fileExportPostfix_Debts)));
        }
        catch (Exception e) {
            exceptionCaught = e;
        }
        finally {
            deleteCreatedFiles(result);
        }
        if (exceptionCaught != null) {
            throw new RuntimeException(exceptionCaught);
        }
    }

    @Test
    public void testOutputEverythingForAllSplit() {
        ExportSettings exportSettings = createExportSettings(true, true, true, true, true, true, false);
        List<File> result = exporter.exportReport(exportSettings, tripToExport.getParticipant(), tripToExport,
                resourceResolver,
                new ActivityResolver() {

                    public Object getActivity() {
                        return null;
                    }
                },
                amountFactory);

        Throwable exceptionCaught = null;
        try {
            Assert.assertEquals(12, result.size());
            Assert.assertTrue(patternCsvIndividual.matcher(result.get(0).getName()).matches());
            Assert.assertTrue(patternCsvIndividual.matcher(result.get(1).getName()).matches());
            Assert.assertTrue(patternCsvIndividual.matcher(result.get(2).getName()).matches());
            Assert.assertTrue(patternHtmlIndividual.matcher(result.get(3).getName()).matches());

            Assert.assertTrue(patternCsvIndividual.matcher(result.get(4).getName()).matches());
            Assert.assertTrue(patternCsvIndividual.matcher(result.get(5).getName()).matches());
            Assert.assertTrue(patternCsvIndividual.matcher(result.get(6).getName()).matches());
            Assert.assertTrue(patternHtmlIndividual.matcher(result.get(7).getName()).matches());

            Assert.assertTrue(patternCsvIndividual.matcher(result.get(8).getName()).matches());
            Assert.assertTrue(patternCsvIndividual.matcher(result.get(9).getName()).matches());
            Assert.assertTrue(patternCsvIndividual.matcher(result.get(10).getName()).matches());
            Assert.assertTrue(patternHtmlIndividual.matcher(result.get(11).getName()).matches());

            for (int i = 0; i < result.size(); i++) {
                String name = null;
                if (i <= 3) {
                    name = factory.chris.getName();
                }
                else if (i <= 7) {
                    name = factory.niko.getName();
                }
                else {
                    name = factory.wolfram.getName();
                }
                Assert.assertTrue(nameContains(result.get(i), name));
            }
        }
        catch (Exception e) {
            exceptionCaught = e;
        }
        finally {
            deleteCreatedFiles(result);
        }
        if (exceptionCaught != null) {
            throw new RuntimeException(exceptionCaught);
        }
    }

    @Test
    public void testOutputPaymentsForAllSplit() {
        ExportSettings exportSettings = createExportSettings(false, false, true, true, true, true, false);
        List<File> result = exporter.exportReport(exportSettings, tripToExport.getParticipant(), tripToExport,
                resourceResolver,
                new ActivityResolver() {

                    public Object getActivity() {
                        return null;
                    }
                },
                amountFactory);

        Throwable exceptionCaught = null;
        try {
            Assert.assertEquals(6, result.size());
            Assert.assertTrue(patternCsvIndividual.matcher(result.get(0).getName()).matches());
            Assert.assertTrue(patternHtmlIndividual.matcher(result.get(1).getName()).matches());

            Assert.assertTrue(patternCsvIndividual.matcher(result.get(2).getName()).matches());
            Assert.assertTrue(patternHtmlIndividual.matcher(result.get(3).getName()).matches());

            Assert.assertTrue(patternCsvIndividual.matcher(result.get(4).getName()).matches());
            Assert.assertTrue(patternHtmlIndividual.matcher(result.get(5).getName()).matches());

            for (int i = 0; i < result.size(); i++) {
                String name = null;
                if (i <= 1) {
                    name = factory.chris.getName();
                }
                else if (i <= 3) {
                    name = factory.niko.getName();
                }
                else {
                    name = factory.wolfram.getName();
                }
                Assert.assertTrue(nameContains(result.get(i), name));
                if (i % 2 == 0) {
                    Assert.assertTrue(nameContains(result.get(i),
                            resourceResolver.resolve(R.string.fileExportPostfix_Payments)));
                }
            }
        }
        catch (Exception e) {
            exceptionCaught = e;
        }
        finally {
            deleteCreatedFiles(result);
        }
        if (exceptionCaught != null) {
            throw new RuntimeException(exceptionCaught);
        }
    }

    @Test
    public void testOutputPaymentsAndSpendingsForAllSplit() {
        ExportSettings exportSettings = createExportSettings(false, true, true, true, true, true, false);
        List<File> result = exporter.exportReport(exportSettings, tripToExport.getParticipant(), tripToExport,
                resourceResolver,
                new ActivityResolver() {

                    public Object getActivity() {
                        return null;
                    }
                },
                amountFactory);

        Throwable exceptionCaught = null;
        try {
            Assert.assertEquals(9, result.size());
            Assert.assertTrue(patternCsvIndividual.matcher(result.get(0).getName()).matches());
            Assert.assertTrue(patternCsvIndividual.matcher(result.get(1).getName()).matches());
            Assert.assertTrue(patternHtmlIndividual.matcher(result.get(2).getName()).matches());

            Assert.assertTrue(patternCsvIndividual.matcher(result.get(3).getName()).matches());
            Assert.assertTrue(patternCsvIndividual.matcher(result.get(4).getName()).matches());
            Assert.assertTrue(patternHtmlIndividual.matcher(result.get(5).getName()).matches());

            Assert.assertTrue(patternCsvIndividual.matcher(result.get(6).getName()).matches());
            Assert.assertTrue(patternCsvIndividual.matcher(result.get(7).getName()).matches());
            Assert.assertTrue(patternHtmlIndividual.matcher(result.get(8).getName()).matches());

            for (int i = 0; i < result.size(); i++) {
                String name = null;
                if (i <= 2) {
                    name = factory.chris.getName();
                }
                else if (i <= 5) {
                    name = factory.niko.getName();
                }
                else {
                    name = factory.wolfram.getName();
                }
                Assert.assertTrue(nameContains(result.get(i), name));
                if (i % 3 == 0) {
                    Assert.assertTrue(nameContains(result.get(i),
                            resourceResolver.resolve(R.string.fileExportPostfix_Payments)));
                }
                else if (i % 3 == 1) {
                    Assert.assertTrue(nameContains(result.get(i),
                            resourceResolver.resolve(R.string.fileExportPostfix_Spendings)));
                }
            }
        }
        catch (Exception e) {
            exceptionCaught = e;
        }
        finally {
            deleteCreatedFiles(result);
        }
        if (exceptionCaught != null) {
            throw new RuntimeException(exceptionCaught);
        }
    }

    @Test
    public void testOutputPaymentsAndSpendingsOnlyHtmlForAllSplit() {
        ExportSettings exportSettings = createExportSettings(false, true, true, false, true, true, false);
        List<File> result = exporter.exportReport(exportSettings, tripToExport.getParticipant(), tripToExport,
                resourceResolver,
                new ActivityResolver() {

                    public Object getActivity() {
                        return null;
                    }
                },
                amountFactory);

        Throwable exceptionCaught = null;
        try {
            Assert.assertEquals(3, result.size());
            Assert.assertTrue(patternHtmlIndividual.matcher(result.get(0).getName()).matches());
            Assert.assertTrue(patternHtmlIndividual.matcher(result.get(1).getName()).matches());
            Assert.assertTrue(patternHtmlIndividual.matcher(result.get(2).getName()).matches());

            for (int i = 0; i < result.size(); i++) {
                String name = null;
                if (i == 0) {
                    name = factory.chris.getName();
                }
                else if (i == 1) {
                    name = factory.niko.getName();
                }
                else {
                    name = factory.wolfram.getName();
                }
                Assert.assertTrue(nameContains(result.get(i), name));
            }
        }
        catch (Exception e) {
            exceptionCaught = e;
        }
        finally {
            deleteCreatedFiles(result);
        }
        if (exceptionCaught != null) {
            throw new RuntimeException(exceptionCaught);
        }
    }

    @Test
    public void testOutputPaymentsAndSpendingsOnlyCsvForAllSplit() {
        ExportSettings exportSettings = createExportSettings(false, true, true, true, false, true, false);
        List<File> result = exporter.exportReport(exportSettings, tripToExport.getParticipant(), tripToExport,
                resourceResolver,
                new ActivityResolver() {

                    public Object getActivity() {
                        return null;
                    }
                },
                amountFactory);

        Throwable exceptionCaught = null;
        try {
            Assert.assertEquals(6, result.size());
            Assert.assertTrue(patternCsvIndividual.matcher(result.get(0).getName()).matches());
            Assert.assertTrue(patternCsvIndividual.matcher(result.get(1).getName()).matches());

            Assert.assertTrue(patternCsvIndividual.matcher(result.get(2).getName()).matches());
            Assert.assertTrue(patternCsvIndividual.matcher(result.get(3).getName()).matches());

            Assert.assertTrue(patternCsvIndividual.matcher(result.get(4).getName()).matches());
            Assert.assertTrue(patternCsvIndividual.matcher(result.get(5).getName()).matches());

            for (int i = 0; i < result.size(); i++) {
                String name = null;
                if (i <= 1) {
                    name = factory.chris.getName();
                }
                else if (i <= 3) {
                    name = factory.niko.getName();
                }
                else {
                    name = factory.wolfram.getName();
                }
                Assert.assertTrue(nameContains(result.get(i), name));
                if (i % 2 == 0) {
                    Assert.assertTrue(nameContains(result.get(i),
                            resourceResolver.resolve(R.string.fileExportPostfix_Payments)));
                }
                else if (i % 2 == 1) {
                    Assert.assertTrue(nameContains(result.get(i),
                            resourceResolver.resolve(R.string.fileExportPostfix_Spendings)));
                }
            }
        }
        catch (Exception e) {
            exceptionCaught = e;
        }
        finally {
            deleteCreatedFiles(result);
        }
        if (exceptionCaught != null) {
            throw new RuntimeException(exceptionCaught);
        }
    }

    @Test
    public void testOutputCsvForAllSplit() {
        ExportSettings exportSettings = createExportSettings(true, true, true, true, false, true, false);
        List<File> result = exporter.exportReport(exportSettings, tripToExport.getParticipant(), tripToExport,
                resourceResolver,
                new ActivityResolver() {

                    public Object getActivity() {
                        return null;
                    }
                },
                amountFactory);

        Throwable exceptionCaught = null;
        try {
            Assert.assertEquals(9, result.size());
            Assert.assertTrue(patternCsvIndividual.matcher(result.get(0).getName()).matches());
            Assert.assertTrue(patternCsvIndividual.matcher(result.get(1).getName()).matches());
            Assert.assertTrue(patternCsvIndividual.matcher(result.get(2).getName()).matches());

            Assert.assertTrue(patternCsvIndividual.matcher(result.get(3).getName()).matches());
            Assert.assertTrue(patternCsvIndividual.matcher(result.get(4).getName()).matches());
            Assert.assertTrue(patternCsvIndividual.matcher(result.get(5).getName()).matches());

            Assert.assertTrue(patternCsvIndividual.matcher(result.get(6).getName()).matches());
            Assert.assertTrue(patternCsvIndividual.matcher(result.get(7).getName()).matches());
            Assert.assertTrue(patternCsvIndividual.matcher(result.get(8).getName()).matches());

            for (int i = 0; i < result.size(); i++) {
                String name = null;
                if (i <= 2) {
                    name = factory.chris.getName();
                }
                else if (i <= 5) {
                    name = factory.niko.getName();
                }
                else {
                    name = factory.wolfram.getName();
                }
                Assert.assertTrue(nameContains(result.get(i), name));
            }
        }
        catch (Exception e) {
            exceptionCaught = e;
        }
        finally {
            deleteCreatedFiles(result);
        }
        if (exceptionCaught != null) {
            throw new RuntimeException(exceptionCaught);
        }
    }

    @Test
    public void testOutputHtmlForAllSplit() {
        ExportSettings exportSettings = createExportSettings(true, true, true, false, true, true, false);
        List<File> result = exporter.exportReport(exportSettings, tripToExport.getParticipant(), tripToExport,
                resourceResolver,
                new ActivityResolver() {

                    public Object getActivity() {
                        return null;
                    }
                },
                amountFactory);

        Throwable exceptionCaught = null;
        try {
            Assert.assertEquals(3, result.size());
            Assert.assertTrue(patternHtmlIndividual.matcher(result.get(0).getName()).matches());
            Assert.assertTrue(patternHtmlIndividual.matcher(result.get(1).getName()).matches());
            Assert.assertTrue(patternHtmlIndividual.matcher(result.get(2).getName()).matches());

            for (int i = 0; i < result.size(); i++) {
                String name = null;
                if (i == 0) {
                    name = factory.chris.getName();
                }
                else if (i == 1) {
                    name = factory.niko.getName();
                }
                else {
                    name = factory.wolfram.getName();
                }
                Assert.assertTrue(nameContains(result.get(i), name));
            }
        }
        catch (Exception e) {
            exceptionCaught = e;
        }
        finally {
            deleteCreatedFiles(result);
        }
        if (exceptionCaught != null) {
            throw new RuntimeException(exceptionCaught);
        }
    }

    @Test
    public void testOutputEverythingForOneParticipant() {
        ExportSettings exportSettings = createExportSettings(true, true, true, true, true, false, false);
        List<Participant> participants = new ArrayList<Participant>();
        participants.add(participant);
        List<File> result = exporter.exportReport(exportSettings, participants, tripToExport, resourceResolver,
                new ActivityResolver() {

                    public Object getActivity() {
                        return null;
                    }
                },
                amountFactory);

        Throwable exceptionCaught = null;
        try {
            Assert.assertEquals(4, result.size());
            Assert.assertTrue(match(result.get(0), patternCsvIndividual));
            Assert.assertTrue(match(result.get(1), patternCsvIndividual));
            Assert.assertTrue(match(result.get(2), patternCsvIndividual));
            Assert.assertTrue(match(result.get(3), patternHtmlIndividual));
            for (File f : result) {
                Assert.assertTrue(nameContains(f, participant.getName()));
            }
        }
        catch (Exception e) {
            exceptionCaught = e;
        }
        finally {
            deleteCreatedFiles(result);
        }
        if (exceptionCaught != null) {
            throw new RuntimeException(exceptionCaught);
        }
    }

    @Test
    public void testOutputPaymentsForOneParticipant() {
        ExportSettings exportSettings = createExportSettings(false, false, true, true, true, false, false);
        List<Participant> participants = new ArrayList<Participant>();
        participants.add(participant);
        List<File> result = exporter.exportReport(exportSettings, participants, tripToExport, resourceResolver,
                new ActivityResolver() {

                    public Object getActivity() {
                        return null;
                    }
                },
                amountFactory);

        Throwable exceptionCaught = null;
        try {
            Assert.assertEquals(2, result.size());
            Assert.assertTrue(match(result.get(0), patternCsvIndividual));
            Assert.assertTrue(match(result.get(1), patternHtmlIndividual));
            for (File f : result) {
                Assert.assertTrue(nameContains(f, participant.getName()));
            }
            Assert.assertTrue(nameContains(result.get(0), resourceResolver.resolve(R.string.fileExportPostfix_Payments)));

        }
        catch (Exception e) {
            exceptionCaught = e;
        }
        finally {
            deleteCreatedFiles(result);
        }
        if (exceptionCaught != null) {
            throw new RuntimeException(exceptionCaught);
        }
    }

    @Test
    public void testOutputSpendingsForOneParticipant() {
        ExportSettings exportSettings = createExportSettings(false, true, false, true, true, false, false);
        List<Participant> participants = new ArrayList<Participant>();
        participants.add(participant);
        List<File> result = exporter.exportReport(exportSettings, participants, tripToExport, resourceResolver,
                new ActivityResolver() {

                    public Object getActivity() {
                        return null;
                    }
                },
                amountFactory);

        Throwable exceptionCaught = null;
        try {
            Assert.assertEquals(2, result.size());
            Assert.assertTrue(match(result.get(0), patternCsvIndividual));
            Assert.assertTrue(match(result.get(1), patternHtmlIndividual));
            for (File f : result) {
                Assert.assertTrue(nameContains(f, participant.getName()));
            }
            Assert.assertTrue(nameContains(result.get(0),
                    resourceResolver.resolve(R.string.fileExportPostfix_Spendings)));

        }
        catch (Exception e) {
            exceptionCaught = e;
        }
        finally {
            deleteCreatedFiles(result);
        }
        if (exceptionCaught != null) {
            throw new RuntimeException(exceptionCaught);
        }
    }

    @Test
    public void testOutputDebtsForOneParticipant() {
        ExportSettings exportSettings = createExportSettings(true, false, false, true, true, false, false);
        List<Participant> participants = new ArrayList<Participant>();
        participants.add(participant);
        List<File> result = exporter.exportReport(exportSettings, participants, tripToExport, resourceResolver,
                new ActivityResolver() {

                    public Object getActivity() {
                        return null;
                    }
                },
                amountFactory);

        Throwable exceptionCaught = null;
        try {
            Assert.assertEquals(2, result.size());
            Assert.assertTrue(match(result.get(0), patternCsvIndividual));
            Assert.assertTrue(match(result.get(1), patternHtmlIndividual));
            for (File f : result) {
                Assert.assertTrue(nameContains(f, participant.getName()));
            }
            Assert.assertTrue(nameContains(result.get(0), resourceResolver.resolve(R.string.fileExportPostfix_Debts)));

        }
        catch (Exception e) {
            exceptionCaught = e;
        }
        finally {
            deleteCreatedFiles(result);
        }
        if (exceptionCaught != null) {
            throw new RuntimeException(exceptionCaught);
        }
    }

    @Test
    public void testOutputPaymentsAndDebtsForOneParticipant() {
        ExportSettings exportSettings = createExportSettings(true, false, true, true, true, false, false);
        List<Participant> participants = new ArrayList<Participant>();
        participants.add(participant);
        List<File> result = exporter.exportReport(exportSettings, participants, tripToExport, resourceResolver,
                new ActivityResolver() {

                    public Object getActivity() {
                        return null;
                    }
                },
                amountFactory);

        Throwable exceptionCaught = null;
        try {
            Assert.assertEquals(3, result.size());
            Assert.assertTrue(match(result.get(0), patternCsvIndividual));
            Assert.assertTrue(match(result.get(1), patternCsvIndividual));
            Assert.assertTrue(match(result.get(2), patternHtmlIndividual));
            for (File f : result) {
                Assert.assertTrue(nameContains(f, participant.getName()));
            }
            Assert.assertTrue(nameContains(result.get(0), resourceResolver.resolve(R.string.fileExportPostfix_Payments)));
            Assert.assertTrue(nameContains(result.get(1), resourceResolver.resolve(R.string.fileExportPostfix_Debts)));
        }
        catch (Exception e) {
            exceptionCaught = e;
        }
        finally {
            deleteCreatedFiles(result);
        }
        if (exceptionCaught != null) {
            throw new RuntimeException(exceptionCaught);
        }
    }

    @Test
    public void testOutputPaymentsAndDebtsOnlyHtmlForOneParticipant() {
        ExportSettings exportSettings = createExportSettings(true, false, true, false, true, false, false);
        List<Participant> participants = new ArrayList<Participant>();
        participants.add(participant);
        List<File> result = exporter.exportReport(exportSettings, participants, tripToExport, resourceResolver,
                new ActivityResolver() {

                    public Object getActivity() {
                        return null;
                    }
                },
                amountFactory);

        Throwable exceptionCaught = null;
        try {
            Assert.assertEquals(1, result.size());
            Assert.assertTrue(match(result.get(0), patternHtmlIndividual));
            Assert.assertTrue(nameContains(result.get(0), participant.getName()));
        }
        catch (Exception e) {
            exceptionCaught = e;
        }
        finally {
            deleteCreatedFiles(result);
        }
        if (exceptionCaught != null) {
            throw new RuntimeException(exceptionCaught);
        }
    }

    @Test
    public void testOutputPaymentsAndDebtsOnlyCsvForOneParticipant() {
        ExportSettings exportSettings = createExportSettings(true, false, true, true, false, false, false);
        List<Participant> participants = new ArrayList<Participant>();
        participants.add(participant);
        List<File> result = exporter.exportReport(exportSettings, participants, tripToExport, resourceResolver,
                new ActivityResolver() {

                    public Object getActivity() {
                        return null;
                    }
                },
                amountFactory);

        Throwable exceptionCaught = null;
        try {
            Assert.assertEquals(2, result.size());
            Assert.assertTrue(match(result.get(0), patternCsvIndividual));
            Assert.assertTrue(match(result.get(1), patternCsvIndividual));
            for (File f : result) {
                Assert.assertTrue(nameContains(f, participant.getName()));
            }
            Assert.assertTrue(nameContains(result.get(0), resourceResolver.resolve(R.string.fileExportPostfix_Payments)));
            Assert.assertTrue(nameContains(result.get(1), resourceResolver.resolve(R.string.fileExportPostfix_Debts)));
        }
        catch (Exception e) {
            exceptionCaught = e;
        }
        finally {
            deleteCreatedFiles(result);
        }
        if (exceptionCaught != null) {
            throw new RuntimeException(exceptionCaught);
        }
    }

    @Test
    public void testOutputCsvForOneParticipant() {
        ExportSettings exportSettings = createExportSettings(true, true, true, true, false, false, false);
        List<Participant> participants = new ArrayList<Participant>();
        participants.add(participant);
        List<File> result = exporter.exportReport(exportSettings, participants, tripToExport, resourceResolver,
                new ActivityResolver() {

                    public Object getActivity() {
                        return null;
                    }
                },
                amountFactory);

        Throwable exceptionCaught = null;
        try {
            Assert.assertEquals(3, result.size());
            Assert.assertTrue(match(result.get(0), patternCsvIndividual));
            Assert.assertTrue(match(result.get(1), patternCsvIndividual));
            Assert.assertTrue(match(result.get(2), patternCsvIndividual));
            for (File f : result) {
                Assert.assertTrue(nameContains(f, participant.getName()));
            }
        }
        catch (Exception e) {
            exceptionCaught = e;
        }
        finally {
            deleteCreatedFiles(result);
        }
        if (exceptionCaught != null) {
            throw new RuntimeException(exceptionCaught);
        }
    }

    @Test
    public void testOutputHtmlForOneParticipant() {
        ExportSettings exportSettings = createExportSettings(true, true, true, false, true, false, false);
        List<Participant> participants = new ArrayList<Participant>();
        participants.add(participant);
        List<File> result = exporter.exportReport(exportSettings, participants, tripToExport, resourceResolver,
                new ActivityResolver() {

                    public Object getActivity() {
                        return null;
                    }
                },
                amountFactory);

        Throwable exceptionCaught = null;
        try {
            Assert.assertEquals(1, result.size());
            Assert.assertTrue(match(result.get(0), patternHtmlIndividual));
            Assert.assertTrue(nameContains(result.get(0), participant.getName()));
        }
        catch (Exception e) {
            exceptionCaught = e;
        }
        finally {
            deleteCreatedFiles(result);
        }
        if (exceptionCaught != null) {
            throw new RuntimeException(exceptionCaught);
        }
    }

    /* ---------------------- Support --------------------------- */

    private boolean match(File file, Pattern patternCsvIndividual2) {
        return patternCsvIndividual2.matcher(file.getName()).matches();
    }

    private boolean nameContains(File file, String fragment) {
        return file.getName().contains(fragment);
    }

    private void deleteCreatedFiles(List<File> result) {
        if (deleteCreatedFiles) {
            for (File f : result) {
                f.delete();
            }
        }
    }

    private ExportSettings createExportSettings(boolean exportDebts, boolean exportSpendings, boolean exportPayments,
            boolean formatCsv,
            boolean formatHtml, boolean separateFilesForIndividuals, boolean showGlobalSumsOnIndividualSpendingReport) {
        ExportSettings exportSettings = new ExportSettings();
        exportSettings.setExportDebts(exportDebts);
        exportSettings.setExportSpendings(exportSpendings);
        exportSettings.setExportPayments(exportPayments);
        exportSettings.setFormatCsv(formatCsv);
        exportSettings.setFormatHtml(formatHtml);
        exportSettings.setSeparateFilesForIndividuals(separateFilesForIndividuals);
        exportSettings.setShowGlobalSumsOnIndividualSpendingReport(showGlobalSumsOnIndividualSpendingReport);
        return exportSettings;
    }

}
