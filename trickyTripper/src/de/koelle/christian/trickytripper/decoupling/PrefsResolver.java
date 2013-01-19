package de.koelle.christian.trickytripper.decoupling;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public interface PrefsResolver {

    Editor getEditingPrefsEditor();

    SharedPreferences getPrefs();

}
