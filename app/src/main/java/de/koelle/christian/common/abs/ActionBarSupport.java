package de.koelle.christian.common.abs;

import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;

import de.koelle.christian.trickytripper.R;
import de.koelle.christian.trickytripper.activities.PreferencesActivity;


public class ActionBarSupport {
    public static void addBackButton(PreferencesActivity activity) {
        //TODO(ckoelle) ABS
        //addBackButton( activity.getActionBar());
    }

    public static void addBackButton(ActionBarActivity activity) {
        addBackButton(activity.getSupportActionBar());
    }

    private static void addBackButton(ActionBar supportActionBar) {
        ActionBar actionBar = supportActionBar;

        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true); //optional
        /*
        In modern Android UIs developers should lean more on a visually distinct color scheme
        for toolbars than on their application icon. The use of application icon plus title as a
        standard layout is discouraged on API 21 devices and newer.
         */
        //actionBar.setDisplayUseLogoEnabled(true);
        //actionBar.setLogo(R.drawable.ic_launcher);

    }

}
