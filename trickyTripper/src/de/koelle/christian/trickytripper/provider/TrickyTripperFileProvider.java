package de.koelle.christian.trickytripper.provider;

import java.io.File;
import java.io.FileNotFoundException;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import de.koelle.christian.trickytripper.constants.Rc;

public class TrickyTripperFileProvider extends ContentProvider {

    public static final String AUTHORITY = "de.koelle.christian.trickytripperfileprovider";
    private UriMatcher uriMatcher;
    private final boolean isCacheDirNotFileDir = Rc.USE_CACHE_DIR_NOT_FILE_DIR_FOR_REPORTS;

    @Override
    public boolean onCreate() {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, "*", 1);
        return true;
    }

    @Override
    public ParcelFileDescriptor openFile(Uri uri, String mode) throws FileNotFoundException {

        if (Rc.debugOn) {
            Log.d(Rc.LT_PROV, "Called with uri: '" + uri + "'." + uri.getLastPathSegment());
        }

        switch (uriMatcher.match(uri)) {

        case 1: /* match */

            /*
             * e.g.
             * 'content://de.koelle.christian.trickytripperfileprovider/Test.txt'
             */

            StringBuilder fileLocation = new StringBuilder()
                    .append((isCacheDirNotFileDir) ? getContext().getCacheDir() : getContext().getFilesDir())
                    .append(File.separator)
                    .append(uri.getLastPathSegment());

            /* Note:they're only getting read only */
            ParcelFileDescriptor pfd = ParcelFileDescriptor.open(new File(
                    fileLocation.toString()), ParcelFileDescriptor.MODE_READ_ONLY);
            return pfd;

        default:
            throw new FileNotFoundException("Unsupported uri: "
                    + uri.toString());
        }
    }

    /*------------------------- not supported or not implemented---------------------------------*/

    @Override
    public int update(Uri uri, ContentValues contentvalues, String s,
            String[] as) {
        return 0;
    }

    @Override
    public int delete(Uri uri, String s, String[] as) {
        return 0;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentvalues) {
        return null;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String s, String[] as1,
            String s1) {
        return null;
    }
}
