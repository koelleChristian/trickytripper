package de.koelle.christian.trickytripper.controller.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Environment;
import android.util.Log;

import de.koelle.christian.common.io.impl.AppFileWriter;
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
        boolean testExport = false;
        if (testExport) {
            result.add(ExportOutputChannel.SD_CARD);
        } else {
            Intent tweetIntent = new Intent(Rc.STREAM_SENDING_INTENT);
            tweetIntent.setType(Rc.STREAM_SENDING_MIME);
            final PackageManager packageManager = context.getPackageManager();
            List<ResolveInfo> list = packageManager.queryIntentActivities(
                    tweetIntent, PackageManager.MATCH_DEFAULT_ONLY);
            for (ExportOutputChannel channel : ExportOutputChannel.values()) {
                for (ResolveInfo info : list) {
                    if (channel.getPackageName().startsWith(info.activityInfo.packageName)) {
                        result.add(channel);
                    }
                }
            }
        }
        File externalStorageDirectory = Environment.getExternalStorageDirectory();
        // Permissions will be checked in ExportActivity
        if (result.contains(ExportOutputChannel.SD_CARD) && (externalStorageDirectory == null) && !externalStorageDirectory.exists()) {
            result.remove(ExportOutputChannel.SD_CARD);
        }
        return result;
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
