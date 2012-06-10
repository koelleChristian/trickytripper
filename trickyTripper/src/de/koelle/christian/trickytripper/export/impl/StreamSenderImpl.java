package de.koelle.christian.trickytripper.export.impl;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import de.koelle.christian.trickytripper.R;
import de.koelle.christian.trickytripper.export.StreamSender;

public class StreamSenderImpl implements StreamSender {

    public void sendStream(Activity activity, String subject, String content, List<Uri> attachmentUris) {
        if (activity != null) {
            Intent sendIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);

            if (subject != null) {
                sendIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
            }
            if (content != null) {
                sendIntent.putExtra(Intent.EXTRA_TEXT, android.text.Html.fromHtml(content));
            }
            if (attachmentUris != null & !attachmentUris.isEmpty()) {
                ArrayList<Uri> uris = new ArrayList<Uri>();
                for (Uri uri : attachmentUris) {
                    uris.add(uri);
                }
                sendIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM,
                        uris);
            }

            sendIntent.setType("text/html");

            activity.startActivityForResult(Intent.createChooser(sendIntent,
                    activity.getResources().getText(R.string.exportViewIntentChooserHeader)), 1);
        }

    }
}
