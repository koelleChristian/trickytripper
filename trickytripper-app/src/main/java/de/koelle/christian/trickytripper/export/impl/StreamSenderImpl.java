package de.koelle.christian.trickytripper.export.impl;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import de.koelle.christian.trickytripper.R;
import de.koelle.christian.trickytripper.constants.Rc;
import de.koelle.christian.trickytripper.export.StreamSender;
import de.koelle.christian.trickytripper.model.ExportSettings.ExportOutputChannel;

public class StreamSenderImpl implements StreamSender {

    public void sendStream(Activity activity, String subject, String content, List<Uri> attachmentUris,
            ExportOutputChannel channelSelection) {
        if (activity != null) {
            Intent sendIntent = new Intent(Rc.STREAM_SENDING_INTENT);
            sendIntent.setPackage(channelSelection.getPackageName());

            if (subject != null) {
                sendIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
            }
            if (content != null) {
                if (ExportOutputChannel.EVERNOTE.equals(channelSelection)) {
                    /*
                     * Unfortunately Evernote appears to have issues with the
                     * file name.
                     */
                    StringBuilder evernoteAddOn = new StringBuilder(content)
                            .append(Rc.LINE_FEED)
                            .append(Rc.LINE_FEED);
                    for (Uri uri : attachmentUris) {
                        evernoteAddOn.append(uri.getLastPathSegment()).append(Rc.LINE_FEED);
                    }
                    content = evernoteAddOn.toString();
                }
                /*
                 * Unfortunately you cannot transfer a Html-table into the
                 * ordinary interpreter of the intent like into the email.
                 */
                sendIntent.putExtra(Intent.EXTRA_TEXT, content);

            }
            if (attachmentUris != null & !attachmentUris.isEmpty()) {
                ArrayList<Uri> uris = new ArrayList<Uri>();
                for (Uri uri : attachmentUris) {
                    uris.add(uri);
                }
                sendIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM,
                        uris);
            }

            sendIntent.setType(Rc.STREAM_SENDING_MIME).addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            activity.startActivityForResult(Intent.createChooser(sendIntent,
                    activity.getResources().getText(R.string.exportViewIntentChooserHeader)), 1);
        }

    }
}
