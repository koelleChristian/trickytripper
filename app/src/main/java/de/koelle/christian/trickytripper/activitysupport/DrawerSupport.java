package de.koelle.christian.trickytripper.activitysupport;

import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;

import de.koelle.christian.trickytripper.R;

public class DrawerSupport {

    public static boolean drawerOpen(Fragment fragment) {
        DrawerLayout mDrawerLayout = (DrawerLayout) fragment.getActivity().findViewById(R.id.drawer_layout);
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(GravityCompat.START);
    }


}
