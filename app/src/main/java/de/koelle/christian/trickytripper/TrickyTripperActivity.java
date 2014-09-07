package de.koelle.christian.trickytripper;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Button;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockFragmentActivity;

import de.koelle.christian.common.changelog.ChangeLog;
import de.koelle.christian.common.utils.CurrencyUtil;
import de.koelle.christian.common.widget.tab.GenericTabListener;
import de.koelle.christian.trickytripper.activities.ParticipantTabActivity;
import de.koelle.christian.trickytripper.activities.PaymentTabActivity;
import de.koelle.christian.trickytripper.activities.ReportTabActivity;
import de.koelle.christian.trickytripper.model.Trip;

public class TrickyTripperActivity extends SherlockFragmentActivity {

    private static final String SELETED_TAB_INDEX = "tabIndex";

    @Override
    protected void onResume() {
        super.onResume();
        updateButtonText();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tricky_tripper_main_view);

        ChangeLog changeLog = new ChangeLog(this);
        if (changeLog.firstRun()) {
            changeLog.getLogDialog().show();
        }

        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setDisplayShowTitleEnabled(false);

        Tab firstTab =
                addTab(actionBar, ParticipantTabActivity.class, R.string.activity_label_participants, "participants");
        addTab(actionBar, PaymentTabActivity.class, R.string.activity_label_payments, "payments");
        addTab(actionBar, ReportTabActivity.class, R.string.activity_label_report, "report");

        updateButtonText();

        if (savedInstanceState != null) {
            int index = savedInstanceState.getInt(SELETED_TAB_INDEX);
            actionBar.setSelectedNavigationItem(index);
        } else {
            actionBar.selectTab(firstTab);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SELETED_TAB_INDEX, getSupportActionBar().getSelectedTab().getPosition());
    }

    private Tab addTab(final ActionBar actionBar, Class<? extends Fragment> fragment, int label, String tag) {
        Tab tab = actionBar.newTab().setText(label).setTabListener(new GenericTabListener(this, tag, fragment, R.id.mainView_fragment_content));
        actionBar.addTab(tab);
        return tab;
    }

    public void openManageTrips(View view) {
        if (view.getId() == R.id.mainView_trip_button) {
            getApp().getViewController().openManageTrips();
        }
    }

    private void updateButtonText() {
        Button button = (Button) findViewById(R.id.mainView_trip_button);
        Trip trip = getApp().getTripController().getTripLoaded();
        button.setText(trip.getName() + " "
                + CurrencyUtil.getSymbolToCurrency(getResources(), trip.getBaseCurrency(), true));
    }

    private TrickyTripperApp getApp() {
        return ((TrickyTripperApp) getApplication());
    }
}