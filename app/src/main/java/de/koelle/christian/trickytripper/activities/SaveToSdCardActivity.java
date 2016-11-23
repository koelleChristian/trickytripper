package de.koelle.christian.trickytripper.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import de.koelle.christian.common.io.impl.AppFileWriter;
import de.koelle.christian.common.utils.FileUtils;
import de.koelle.christian.trickytripper.R;
import de.koelle.christian.trickytripper.constants.Rc;

public class SaveToSdCardActivity extends AppCompatActivity {

    private static final String MSG_SPACE = " ";
    private List<Uri> fileUris;
    private ProgressDialog progressDialog;
    private Handler pickerResultHandler;
    private Handler progressResultHandler;
    private String directoryPickedPath;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    //TODO(ckoelle) ABS
//    @Override
 //   public Object onRetainNonConfigurationInstance() {
  //      return directoryPickedPath;
   // };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        String action = intent.getAction();
        if (Intent.ACTION_SEND_MULTIPLE.equals(action)) {

            fileUris = handleSendMultipleImages(intent);
            Object o = getLastNonConfigurationInstance();
            if (o != null) {
                directoryPickedPath = (String) o;
            }
            progressResultHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    progressDialog.dismiss();
                    finishHere(true);
                }
            };
            pickerResultHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    writeFiles();
                }
            };

            if (directoryPickedPath == null || directoryPickedPath.length() == 0) {
                Intent directoryPickerIntent = new Intent().setClass(this, DirectoryPickerActivity.class);
                directoryPickerIntent.putExtra(DirectoryPickerActivity.EXTRA_TITLE,
                        getResources().getString(R.string.save2SdReceiverHeading));
                directoryPickerIntent.putExtra(DirectoryPickerActivity.EXTRA_CHOOSE_TXT_PREFIX,
                        getResources().getString(R.string.save2SdReceiverSelect));
                startActivityForResult(directoryPickerIntent, DirectoryPickerActivity.REQUEST_CODE);
            }
            else {
                writeFiles();
            }
        }
        else {
            finish();
        }

    }

    private void writeFiles() {
        progressDialog = ProgressDialog.show(this, getResources()
                .getString(R.string.save2SdReceiverProgressHeading), directoryPickedPath, true, false);
        Runnable runnable = new Runnable() {
            public void run() {
                AppFileWriter.writeContentsToDisc(directoryPickedPath, getContentResolver(), fileUris);
                progressResultHandler.sendEmptyMessage(0);
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == DirectoryPickerActivity.REQUEST_CODE && resultCode == RESULT_OK) {

            Bundle extras = data.getExtras();
            /* e.g. /mnt/sdcard/tmp */
            directoryPickedPath = (String) extras.get(DirectoryPickerActivity.EXTRA_RESULT_CHOSEN_DIRECTORY);
            if (Rc.debugOn) {
                Log.d(Rc.LT_IO, "File to be written to disc=" + directoryPickedPath);
            }
            Runnable runnable = new Runnable() {
                public void run() {
                    pickerResultHandler.sendEmptyMessage(0);
                }
            };
            Thread thread = new Thread(runnable);
            thread.start();
        }
        else {
            finishHere(false);
        }
    }

    private void finishHere(boolean successful) {
        StringBuilder toastMsg = new StringBuilder();
        toastMsg.append((successful) ? getSuccessfulMsg(getResources(), fileUris.size()) : getResources()
                .getString(R.string.save2SdReceiverResultNothingSaved));
        Toast.makeText(getApplicationContext(), toastMsg.toString(), Toast.LENGTH_LONG).show();
        FileUtils.deleteAllFiles(this);
        finish();
    }

    private StringBuilder getSuccessfulMsg(Resources resources, int size) {
        return new StringBuilder()
                .append(resources.getString(R.string.save2SdReceiverResultSavedPrefix))
                .append(MSG_SPACE)
                .append(size)
                .append(MSG_SPACE)
                .append((size > 1) ? resources.getString(R.string.save2SdReceiverResultSavedFiles) : resources
                        .getString(R.string.save2SdReceiverResultSavedFile))
                .append(MSG_SPACE)
                .append(resources.getString(R.string.save2SdReceiverResultSavedPostfix));
    }

    private List<Uri> handleSendMultipleImages(Intent intent) {
        List<Uri> result = new ArrayList<Uri>();
        ArrayList<Uri> fileUris = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
        if (fileUris != null) {
            if (Rc.debugOn) {
                for (Uri uri : fileUris) {
                    Log.d(Rc.LT_IO, "File to be written to disc=" + uri);
                }
            }
            result.addAll(fileUris);
        }
        return result;
    }
}
