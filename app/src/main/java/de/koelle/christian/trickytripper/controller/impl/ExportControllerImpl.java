package de.koelle.christian.trickytripper.controller.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.util.Log;

import de.koelle.christian.common.io.impl.AppFileWriter;
import de.koelle.christian.trickytripper.R;
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

public class ExportControllerImpl implements ExportController {

    private final PrefsResolver prefsResolver;
    private final Context context;
    private final Exporter exporter;
    private final TripResolver tripResolver;

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
//        if (context.getResources().getBoolean(R.bool.v19AndAbove)) {
           // TODO Check for action view
            result.add(ExportOutputChannel.OPEN);
//        }
        Intent intent;
//        intent = new Intent(Rc.STREAM_SENDING_INTENT);

        intent = new Intent(Rc.STREAM_SENDING_INTENT);
        intent.setType(Rc.STREAM_SENDING_MIME);
        final PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> list = packageManager.queryIntentActivities(
                intent, PackageManager.MATCH_DEFAULT_ONLY);
        if (!list.isEmpty()) {
            result.add(ExportOutputChannel.STREAM_SENDING);
        }


        return result;
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
}
