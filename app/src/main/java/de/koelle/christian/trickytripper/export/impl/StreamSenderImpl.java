package de.koelle.christian.trickytripper.export.impl;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Parcelable;
import android.webkit.MimeTypeMap;

import de.koelle.christian.trickytripper.R;
import de.koelle.christian.trickytripper.constants.Rc;
import de.koelle.christian.trickytripper.export.StreamSender;
import de.koelle.christian.trickytripper.model.ExportSettings;
import de.koelle.christian.trickytripper.model.ExportSettings.ExportOutputChannel;

public class StreamSenderImpl implements StreamSender {

    public static final int ACTIVITY_RETURN_CODE = 54323;

    public void sendStream(Activity activity, String subject, String content, List<Uri> externalAttachmentUris,
                           ExportOutputChannel channelSelection) {
        if (activity != null && externalAttachmentUris != null & !externalAttachmentUris.isEmpty() && channelSelection != null) {

            Intent intent = null;
            int flags = Intent.FLAG_GRANT_READ_URI_PERMISSION;

            if (channelSelection == ExportSettings.ExportOutputChannel.STREAM_SENDING) {
                Intent sendIntent = new Intent(Rc.INTENT_SEND_STREAM);
                if (subject != null) {
                    sendIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
                }
                sendIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, new ArrayList<Parcelable>(externalAttachmentUris));
                sendIntent.setType(Rc.INTENT_SEND_STREAM_MIME);
                intent = Intent.createChooser(sendIntent, activity.getResources().getText(R.string.exportViewIntentChooserHeader));
            }
            /**/
            else if (channelSelection == ExportOutputChannel.OPEN) {
                flags = flags | Intent.FLAG_ACTIVITY_NO_HISTORY;
                for (Uri contentUri : externalAttachmentUris) {
                    String fileName = contentUri.toString();
                    final String extension = fileName.substring(fileName.lastIndexOf('.') + 1);
                    final String mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
                    intent = new Intent(Rc.INTENT_OPEN_FILE).setDataAndType(contentUri, mime);
                }
            }
            /**/
            else {
                throw new IllegalArgumentException("Not supported: " + channelSelection + ".");
            }
            intent.addFlags(flags);
            activity.startActivityForResult(intent, ACTIVITY_RETURN_CODE);
        }
    }
}
