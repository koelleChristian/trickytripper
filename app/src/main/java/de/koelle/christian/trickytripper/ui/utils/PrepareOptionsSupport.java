package de.koelle.christian.trickytripper.ui.utils;

import android.view.Menu;

public class PrepareOptionsSupport {


    public static void reset(Menu menu) {
        for (int i = 0; i < menu.size(); i++) {
            if (menu.getItem(i) != null && menu.getItem(i).getIcon() != null) {
                menu.getItem(i).getIcon().setAlpha(255);
            }
        }
    }

}
