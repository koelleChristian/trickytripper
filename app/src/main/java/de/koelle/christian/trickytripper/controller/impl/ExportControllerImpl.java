package de.koelle.christian.trickytripper.controller.impl;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.koelle.christian.common.io.impl.AppFileWriter;
import de.koelle.christian.common.utils.FileUtils;
import de.koelle.christian.trickytripper.apputils.PrefWriterReaderUtils;
import de.koelle.christian.trickytripper.constants.Rc;
import de.koelle.christian.trickytripper.controller.ExportController;
import de.koelle.christian.trickytripper.controller.TripResolver;
import de.koelle.christian.trickytripper.decoupling.PrefsResolver;
import de.koelle.christian.trickytripper.decoupling.impl.ActivityResolverImpl;
import de.koelle.christian.trickytripper.decoupling.impl.ResourceResolverImpl;
import de.koelle.christian.trickytripper.export.Exporter;
import de.koelle.christian.trickytripper.export.impl.ExporterImpl;
import de.koelle.christian.trickytripper.model.ExportSettings;
import de.koelle.christian.trickytripper.model.ExportSettings.ExportOutputChannel;
import de.koelle.christian.trickytripper.model.Participant;
import de.koelle.christian.trickytripper.provider.TrickyTripperFileProvider;

public class ExportControllerImpl implements ExportController {

    private final PrefsResolver prefsResolver;
    private final Context context;
    private final Exporter exporter;
    private final TripResolver tripResolver;
    private boolean osSupportsOpenCsv;
    private boolean osSupportsOpenTxt;
    private boolean osSupportsOpenHtml;

    public ExportControllerImpl(Context context, PrefsResolver prefsResolver, TripResolver tripResolver) {
        this.context = context;
        this.prefsResolver = prefsResolver;
        this.tripResolver = tripResolver;
        this.exporter = new ExporterImpl(new AppFileWriter(context));
    }

    public ExportSettings getDefaultExportSettings() {
        return PrefWriterReaderUtils.loadExportSettings(prefsResolver.getPrefs());
    }

    public List<ExportOutputChannel> getEnabledExportOutputChannel() {
        List<ExportOutputChannel> result = new ArrayList<>();

        Intent intent;
        intent = new Intent(Rc.INTENT_OPEN_FILE);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setDataAndType(getFakeFileAuthedUri("csv"), Rc.INTENT_OPEN_FILE_CSV_MIME);
        osSupportsOpenCsv = osSupportsIntent(intent);
        intent.setDataAndType(getFakeFileAuthedUri("html"), Rc.INTENT_OPEN_FILE_HTML_MIME);
        osSupportsOpenTxt = osSupportsIntent(intent);
        intent.setDataAndType(getFakeFileAuthedUri("txt"), Rc.INTENT_OPEN_FILE_TXT_MIME);
        osSupportsOpenHtml = osSupportsIntent(intent);
        if (osSupportsOpenCsv || osSupportsOpenHtml || osSupportsOpenTxt) {
            result.add(ExportOutputChannel.OPEN);
        }

        intent = new Intent(Rc.INTENT_SEND_STREAM);
        intent.setType(Rc.INTENT_SEND_STREAM_MIME);
        if (osSupportsIntent(intent)) {
            result.add(ExportOutputChannel.STREAM_SENDING);
        }


        return result;
    }

    private Uri getFakeFileAuthedUri(String extension) {
        return FileUtils.getContentUrisFromFile(new File("Whatever." + extension), TrickyTripperFileProvider.AUTHORITY);
    }

    private boolean osSupportsIntent(Intent intent) {
        return intent.resolveActivity(context.getPackageManager()) != null;

    }

    @Override
    public boolean hasEnabledOutputChannel() {
        return !getEnabledExportOutputChannel().isEmpty();
    }

    public List<File> exportReport(ExportSettings settings, Participant selectedParticipant, Activity activity) {
        if (Rc.debugOn) {
            Log.d(Rc.LT_INPUT, "exportReport() settings=" + settings + " selected=" + selectedParticipant);
        }
        List<Participant> participantsForReport = new ArrayList<>();
        if (selectedParticipant != null) {
            participantsForReport.add(selectedParticipant);
        } else {
            participantsForReport.addAll(tripResolver.getTripInEditing().getParticipant());
        }
        PrefWriterReaderUtils.saveExportSettings(prefsResolver.getEditingPrefsEditor(), settings);
        return exporter.exportReport(settings,
                participantsForReport,
                tripResolver.getTripInEditing(),
                new ResourceResolverImpl(context.getResources()), new ActivityResolverImpl(activity),
                tripResolver.getAmountFactory());

    }

    public boolean osSupportsOpenCsv() {
        return osSupportsOpenCsv;
    }

    public boolean osSupportsOpenTxt() {
        return osSupportsOpenTxt;
    }

    public boolean ossSupportsOpenHtml() {
        return osSupportsOpenHtml;
    }
}
