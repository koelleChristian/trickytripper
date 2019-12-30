package de.koelle.christian.trickytripper.activitysupport;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import de.koelle.christian.trickytripper.R;

public class DrawerSupport {

    public static boolean drawerOpen(Fragment fragment) {
        DrawerLayout mDrawerLayout = fragment.getActivity().findViewById(R.id.drawer_layout);
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(GravityCompat.START);
    }


}
