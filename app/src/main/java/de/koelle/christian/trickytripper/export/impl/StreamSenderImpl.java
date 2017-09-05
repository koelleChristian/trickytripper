package de.koelle.christian.trickytripper.export.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Parcelable;
import android.webkit.MimeTypeMap;

import de.koelle.christian.trickytripper.R;
import de.koelle.christian.trickytripper.constants.Rc;
import de.koelle.christian.trickytripper.export.StreamSender;
import de.koelle.christian.trickytripper.model.ExportSettings.ExportOutputChannel;

public class StreamSenderImpl implements StreamSender {

    public void sendStream(Activity activity, String subject, String content, List<Uri> attachmentUris,
                           ExportOutputChannel channelSelection) {
        if (activity != null) {
            Intent sendIntent = new Intent(Intent.ACTION_SEND_MULTIPLE); // Rc.STREAM_SENDING_INTENT TODO Check what is more appropriate
            if (subject != null) {
                sendIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
            }
            if (attachmentUris != null & !attachmentUris.isEmpty()) {
                ArrayList<Uri> uris = new ArrayList<Uri>();
                for (Uri uri : attachmentUris) {
                    uris.add(uri);
                }
                sendIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
            }

            sendIntent.setType(Rc.STREAM_SENDING_MIME).addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            activity.startActivityForResult(Intent.createChooser(sendIntent, activity.getResources().getText(R.string.exportViewIntentChooserHeader)), 1);
        }
    }
}
