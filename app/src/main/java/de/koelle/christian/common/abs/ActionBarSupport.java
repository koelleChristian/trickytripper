package de.koelle.christian.common.abs;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.app.SherlockListActivity;
import com.actionbarsherlock.app.SherlockPreferenceActivity;

public class ActionBarSupport {

    public static void addBackButton(SherlockFragmentActivity activity) {
        addBackButton( activity.getSupportActionBar());
    }
	public static void addBackButton(SherlockPreferenceActivity activity) {
		addBackButton( activity.getSupportActionBar());
	}
	public static void addBackButton(SherlockActivity activity) {
		addBackButton( activity.getSupportActionBar());
	}
	public static void addBackButton(SherlockListActivity activity) {
		addBackButton( activity.getSupportActionBar());
	}
	private static void addBackButton(ActionBar supportActionBar) {
		ActionBar actionBar = supportActionBar;
		actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);
	}

}
