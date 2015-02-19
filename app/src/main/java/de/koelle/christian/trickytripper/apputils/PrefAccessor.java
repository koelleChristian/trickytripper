package de.koelle.christian.trickytripper.apputils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import de.koelle.christian.trickytripper.constants.Rc;
import de.koelle.christian.trickytripper.decoupling.PrefsResolver;

public class PrefAccessor implements PrefsResolver {

    private final Context context;

    public PrefAccessor(Context context) {
        this.context = context;
    }

    public SharedPreferences getPrefs() {
        return context.getSharedPreferences(Rc.PREFS_NAME_ID, Rc.PREFS_MODE);
    }

    @SuppressLint("CommitPrefEdits")
    public Editor getEditingPrefsEditor() {
        SharedPreferences prefs = getPrefs();
        return  prefs.edit();
    }

}
