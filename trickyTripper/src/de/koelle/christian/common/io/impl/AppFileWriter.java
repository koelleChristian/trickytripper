package de.koelle.christian.common.io.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.util.Log;
import de.koelle.christian.common.io.FileWriter;
import de.koelle.christian.trickytripper.constants.Rc;

public class AppFileWriter implements FileWriter {

    private Context context;
    private boolean isCacheDirNotFileDir = Rc.USE_CACHE_DIR_NOT_FILE_DIR_FOR_REPORTS;
    private int mode = Context.MODE_WORLD_WRITEABLE;

    public AppFileWriter(Context context) {
        this.context = context;
    }

    public File write(String filenName, StringBuilder contents) {

        FileOutputStream fos = null;
        File newFile = null;
        try {
            if (isCacheDirNotFileDir) {
                newFile = new File(context.getCacheDir(), filenName);
                fos = new FileOutputStream(newFile);
            }
            else {
                fos = context.openFileOutput(filenName, mode);
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
                fos.close();
                fos.getFD().sync();
            }
            catch (IOException e) {
                // ignored intentionally
            }
        }
        return (newFile == null) ? new File(context.getFilesDir(), filenName) : newFile;
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
