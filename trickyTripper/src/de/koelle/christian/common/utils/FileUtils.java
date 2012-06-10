package de.koelle.christian.common.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.net.Uri;

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

}
