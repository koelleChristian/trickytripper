package de.koelle.christian.trickytripper.activities;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

public class SaveToSdCardActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        String action = intent.getAction();

        if (Intent.ACTION_SEND_MULTIPLE.equals(action)) {
            handleSendMultipleImages(intent);
        }
    }

    private List<Uri> handleSendMultipleImages(Intent intent) {
        List<Uri> result = new ArrayList<Uri>();
        ArrayList<Uri> fileUris = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
        if (fileUris != null) {
            result.addAll(fileUris);
        }
        return result;
    }
}
