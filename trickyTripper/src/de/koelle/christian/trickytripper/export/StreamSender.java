package de.koelle.christian.trickytripper.export;

import java.util.List;

import android.app.Activity;
import android.net.Uri;

public interface StreamSender {

    void sendStream(Activity activity, String subject, String content, List<Uri> attachments);

}
