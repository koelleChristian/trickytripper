package de.koelle.christian.common.io.impl;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import de.koelle.christian.common.io.FileWriter;
import de.koelle.christian.trickytripper.constants.Rc;

public class AppFileWriter implements FileWriter {

    private Context context;
    private boolean isCacheDirNotFileDir = Rc.USE_CACHE_DIR_NOT_FILE_DIR_FOR_REPORTS;
    private int mode = Context.MODE_PRIVATE;

    public AppFileWriter(Context context) {
        this.context = context;
    }

    public static void writeContentsToDisc(String targetDirPath, ContentResolver contentResolver, List<Uri> uris) {
        InputStream in = null;
        ByteArrayOutputStream out = null;

        try {
            for (Uri uri : uris) {
                in = contentResolver.openInputStream(uri);

                out = new ByteArrayOutputStream();
                byte[] buffer = new byte[4096];
                int n = in.read(buffer);
                while (n >= 0) {
                    out.write(buffer, 0, n);
                    n = in.read(buffer);
                }
                in.close();
                in = null;
                AppFileWriter.save(out, targetDirPath, uri.getLastPathSegment());
                out = null;
            }
        }
        catch (FileNotFoundException e) {
            Log.e(Rc.LT_IO, "Content could not be resolved: ", e);
        }
        catch (IOException e) {
            Log.e(Rc.LT_IO, "I/O exception during byte shuffling: ", e);
        }
        finally {
            if (in != null) {
                try {
                    in.close();
                }
                catch (IOException e) {
                    Log.e(Rc.LT_IO, "Could not finally close in: ", e);
                }
            }
            if (out != null) {
                try {
                    out.close();
                }
                catch (IOException e) {
                    Log.e(Rc.LT_IO, "Could not finally close out: ", e);
                }
            }
        }
    }

    public static void save(ByteArrayOutputStream os, String path, String fileName) {
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(new File(path, fileName));
            os.writeTo(fos);
            os.flush();
            fos.flush();
            os.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public File write(String fileName, StringBuilder contents) {

        FileOutputStream fos = null;
        File newFile = null;
        try {
            if (isCacheDirNotFileDir) {
                File directory =
                        (isCacheDirNotFileDir) ?
                                context.getCacheDir() :
                                context.getFilesDir();
                newFile = new File(directory, fileName);
                fos = new FileOutputStream(newFile);
            }
            else {
                fos = context.openFileOutput(fileName, mode);
            }
            fos.write(contents.toString().getBytes());
        }
        catch (FileNotFoundException e) {
            Log.e(Rc.LT_IO, "File not found", e);
        }
        catch (IOException e) {
            Log.e(Rc.LT_IO, "IO problem", e);
        }
        finally {
            try {
                if (fos != null) {
                    fos.close();
                    fos.getFD().sync();
                }
            }
            catch (IOException e) {
                // ignored intentionally
            }
        }
        return (newFile == null) ? new File(context.getFilesDir(), fileName) : newFile;
    }

    /* -------------- below only setters -------------- */

    public void setContext(Context context) {
        this.context = context;
    }

    public void setCacheDirNotFileDir(boolean isCacheDirNotFileDir) {
        this.isCacheDirNotFileDir = isCacheDirNotFileDir;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

}
