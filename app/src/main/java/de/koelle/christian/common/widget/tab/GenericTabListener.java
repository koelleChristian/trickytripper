package de.koelle.christian.common.widget.tab;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.ActionBar.TabListener;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;

public class GenericTabListener implements TabListener {
    private Fragment fragment;
    private final SherlockFragmentActivity host;
    private final Class<? extends Fragment> type;
    private final int targetContainerViewId;
    private String tag;

    public GenericTabListener(SherlockFragmentActivity parent, String tag, Class<? extends Fragment> type,
            int targetContainerViewId) {
        this.host = parent;
        this.tag = tag;
        this.type = type;
        this.targetContainerViewId = targetContainerViewId;
    }

    public GenericTabListener(SherlockFragmentActivity parent, String tag, Class<? extends Fragment> type) {
        this(parent, tag, type, android.R.id.content);
    }

    public void onTabSelected(Tab tab, FragmentTransaction transaction) {
        /*
         * The fragment which has been added to this listener may have been
         * replaced (can be the case for lists when drilling down), but if the
         * tag has been retained, we should find the actual fragment that was
         * showing in this tab before the user switched to another.
         */
        Fragment currentlyShowing = host.getSupportFragmentManager().findFragmentByTag(tag);
        if (currentlyShowing == null) {
            fragment = SherlockFragment.instantiate(host, type.getName());
            transaction.add(targetContainerViewId, fragment, tag);
        } else {
            transaction.attach(currentlyShowing);
        }
    }

    public void onTabUnselected(Tab tab, FragmentTransaction fragmentTransaction) {
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

    public void onTabReselected(Tab tab, FragmentTransaction fragmentTransaction) {
        // Intentionally blank.
    }
}
