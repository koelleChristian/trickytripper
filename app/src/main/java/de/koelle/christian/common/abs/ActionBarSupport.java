package de.koelle.christian.common.abs;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;


public class ActionBarSupport {

    public static void addBackButton(AppCompatActivity activity) {
        addBackButton(activity.getSupportActionBar());
    }

    private static void addBackButton(ActionBar actionBar) {

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
