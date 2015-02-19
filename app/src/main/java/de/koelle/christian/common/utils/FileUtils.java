package de.koelle.christian.common.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.app.Application;
import android.net.Uri;
import android.util.Log;
import de.koelle.christian.trickytripper.constants.Rc;

public class FileUtils {

    private static final String CONTENT_PREFIX = "content://";

    public static List<Uri> getUrisFromFiles(List<File> files) {
        List<Uri> result = new ArrayList<Uri>();
        for (File f : files) {
            result.add(Uri.parse(Uri.fromFile(f).toString()));
        }
        return result;
    }

    public static List<Uri> getContentUrisFromFiles(List<File> files, String authority) {
        List<Uri> result = new ArrayList<Uri>();
        for (File f : files) {
            result.add(Uri.parse(CONTENT_PREFIX + authority + File.separator + f.getName()));
        }
        return result;
    }

    public static void deleteAllFiles(Activity callingActivity) {
        deleteFiles(Arrays.asList(callingActivity.getFilesDir().listFiles()));
        deleteFiles(Arrays.asList(callingActivity.getCacheDir().listFiles()));
    }

    public static void deleteAllFiles(Application callingApplication) {
        deleteFiles(Arrays.asList(callingApplication.getFilesDir().listFiles()));
        deleteFiles(Arrays.asList(callingApplication.getCacheDir().listFiles()));
    }

    private static void deleteFiles(List<File> fileList) {
        if (fileList != null) {
            for (File f : fileList) {
                if (Rc.debugOn) {
                    Log.d(Rc.LT_IO, "Delete file f=" + f.getAbsolutePath());
                }
                /*
                 * Notes: deleteFile(String name) on this activity only works
                 * with short names of files existing in the application's data
                 * directory. It does not work with absolute paths.
                 */
                f.delete(); // TODO(ckoelle) result ignored
            }
        }
    }

}
