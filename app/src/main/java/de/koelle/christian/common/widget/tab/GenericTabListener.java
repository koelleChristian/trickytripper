package de.koelle.christian.common.widget.tab;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;


public class GenericTabListener implements ActionBar.TabListener {
    private Fragment fragment;
    private final AppCompatActivity host;
    private final Class<? extends Fragment> type;
    private final int targetContainerViewId;
    private String tag;

    public GenericTabListener(AppCompatActivity parent, String tag, Class<? extends Fragment> type,
            int targetContainerViewId) {
        this.host = parent;
        this.tag = tag;
        this.type = type;
        this.targetContainerViewId = targetContainerViewId;
    }

    public GenericTabListener(AppCompatActivity parent, String tag, Class<? extends Fragment> type) {
        this(parent, tag, type, android.R.id.content);
    }

    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction transaction) {
        /*
         * The fragment which has been added to this listener may have been
         * replaced (can be the case for lists when drilling down), but if the
         * tag has been retained, we should find the actual fragment that was
         * showing in this tab before the user switched to another.
         */
        Fragment currentlyShowing = host.getSupportFragmentManager().findFragmentByTag(tag);
        if (currentlyShowing == null) {
            fragment = Fragment.instantiate(host, type.getName());
            transaction.add(targetContainerViewId, fragment, tag);
        } else {
            transaction.attach(currentlyShowing);
        }
    }

    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        /*
         * The fragment which has been added to this listener may have been
         * replaced (can be the case for lists when drilling down), but if the
         * tag has been retained, we should find the actual fragment that's
         * currently active.
         */
        Fragment currentlyShowing = host.getSupportFragmentManager().findFragmentByTag(tag);
        if (currentlyShowing != null) {
            fragmentTransaction.detach(currentlyShowing);
        } else if (this.fragment != null) {
            fragmentTransaction.detach(fragment);
        }
    }

    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // Intentionally blank.
    }
}
