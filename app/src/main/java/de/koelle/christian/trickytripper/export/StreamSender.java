package de.koelle.christian.trickytripper.export;

import android.app.Activity;
import android.net.Uri;

import java.util.List;

import de.koelle.christian.trickytripper.model.ExportSettings.ExportOutputChannel;

public interface StreamSender {

    void sendStream(Activity activity, String subject, String content, List<Uri> attachments,
            ExportOutputChannel channelSelection);

}
