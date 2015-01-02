package de.koelle.christian.trickytripper;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;


import de.koelle.christian.common.changelog.ChangeLog;
import de.koelle.christian.common.utils.CurrencyUtil;
import de.koelle.christian.common.widget.tab.GenericTabListener;
import de.koelle.christian.trickytripper.activities.ParticipantTabActivity;
import de.koelle.christian.trickytripper.activities.PaymentTabActivity;
import de.koelle.christian.trickytripper.activities.ReportTabActivity;
import de.koelle.christian.trickytripper.activitysupport.MainPagerAdapter;
import de.koelle.christian.trickytripper.model.Trip;

public class TrickyTripperActivity extends ActionBarActivity {

    private static final String SELETED_TAB_INDEX = "tabIndex";

    private ViewPager mViewPager;
    private FragmentStatePagerAdapter mPagerAdapter;

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

        mPagerAdapter =  new MainPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mPagerAdapter);



        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setLogo(R.drawable.ic_launcher);

        updateButtonText();

        int index = 0; // first one by default
        if (savedInstanceState != null) {
            index = savedInstanceState.getInt(SELETED_TAB_INDEX);
        }
        mViewPager.setCurrentItem(index);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SELETED_TAB_INDEX,mViewPager.getCurrentItem());
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

    private class MyTabListener implements ActionBar.TabListener{
        @Override
        public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        }

        @Override
        public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
            mViewPager.setCurrentItem(tab.getPosition());
        }

        @Override
        public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        }
    }
}