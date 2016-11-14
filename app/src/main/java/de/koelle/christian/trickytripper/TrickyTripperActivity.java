package de.koelle.christian.trickytripper;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import de.koelle.christian.common.changelog.ChangeLog;
import de.koelle.christian.common.options.OptionConstraints;
import de.koelle.christian.common.options.OptionConstraintsInflater;
import de.koelle.christian.common.utils.CurrencyUtil;
import de.koelle.christian.trickytripper.activitysupport.MainPagerAdapter;
import de.koelle.christian.trickytripper.constants.Rc;
import de.koelle.christian.trickytripper.controller.TripController;
import de.koelle.christian.trickytripper.dialogs.DeleteDialogFragment;
import de.koelle.christian.trickytripper.model.Trip;
import de.koelle.christian.trickytripper.model.TripSummary;
import de.koelle.christian.trickytripper.model.modelAdapter.TripSummarySymbolResolvingDelegator;

public class TrickyTripperActivity extends AppCompatActivity implements DeleteDialogFragment.DeleteConfirmationCallback {

    private static final String SELECTED_TAB_INDEX = "tabIndex";
    private static final String DIALOG_PARAM_TRIP_SUMMARY = "dialogParamTripSummary";

    private ViewPager mViewPager;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private ArrayAdapter<TripSummary> mDrawerListAdapter;
    private Comparator<TripSummary> mListComparator;
    private CharSequence mDrawerTitle;
    private long previouslySelectedTripId;

    private MyActionModeCallback mActionModeCallback = new MyActionModeCallback();
    /*This action mode might also be set by the nested fragments.*/
    private ActionMode mActionMode;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tricky_tripper_main_view);

        ChangeLog changeLog = new ChangeLog(this);
        if (changeLog.firstRun()) {
            changeLog.getLogDialog().show();
        }

        mDrawerTitle = getResources().getString(R.string.trip_manage_view_heading);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.drawer_drawer_list_view_trips);
        mViewPager = (ViewPager) findViewById(R.id.drawer_content_pager);

        updatePagerAdapter();

        mDrawerToggle = new MyActionBarDrawerToggle();
        mDrawerLayout.setDrawerListener(mDrawerToggle);


        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        // This line has to be prior to the next one.
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        setTripNameToHeader();

        int index = 0; // first one by default
        if (savedInstanceState != null) {
            index = savedInstanceState.getInt(SELECTED_TAB_INDEX);
        }
        mViewPager.setCurrentItem(index);


        TrickyTripperApp app = getApp();
        final Collator c = app.getMiscController().getDefaultStringCollator();
        mListComparator = new Comparator<TripSummary>() {
            public int compare(TripSummary object1, TripSummary object2) {
                return c.compare(object1.getName(), object2.getName());
            }
        };

        iniDrawerListView(mDrawerList);
        addDrawerListClickListener(mDrawerList, app);
    }


    private void iniDrawerListView(ListView listView) {

        mDrawerListAdapter = new ArrayAdapter<TripSummary>(
                this,
                android.R.layout.simple_list_item_1,
                new ArrayList<TripSummary>()) {


            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                /* Display currency code only when not in list view. */
                TextView result = (TextView) super.getView(position,
                        convertView, parent);
                TripSummary row = this.getItem(position);
                int color;
                int typefaceStyle;
                if (row.getId() == getApp().getTripController().getTripLoaded().getId()) {
                    color = getResources().getColor(R.color.main);
                    typefaceStyle = Typeface.BOLD;
                } else {
                    color = ContextCompat.getColor(getContext(), android.R.color.tertiary_text_light);
                    typefaceStyle = Typeface.NORMAL;
                }
                result.setText(row.toString());
                result.setTextColor(color);
                result.setTypeface(null, typefaceStyle);
                return result;
            }
        };

        listView.setAdapter(mDrawerListAdapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
    }

    private void updatePagerAdapter() {
        FragmentStatePagerAdapter mPagerAdapter = new MainPagerAdapter(getSupportFragmentManager(), this);
        mViewPager.setAdapter(mPagerAdapter);
    }

    private void addDrawerListClickListener(final ListView listView,
                                            final TrickyTripperApp app) {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int position, long id) {
                if (mActionMode != null) {
                    return;
                }
                mDrawerList.setItemChecked(position, true);
                TripSummary selectedTripSummary = mDrawerListAdapter.getItem(position);
                app.getTripController().loadTrip(selectedTripSummary);
                mDrawerLayout.closeDrawer(GravityCompat.START);
                setTripNameToHeader();
                if (selectedTripSummary.getId() != previouslySelectedTripId) {
                    updatePagerAdapter();
                }
            }
        });


        listView.setLongClickable(true);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (mActionMode != null) {
                    return false;
                }
                TripSummary selectedTrip = mDrawerListAdapter.getItem(position);
                mActionModeCallback.setSelectedTrip(selectedTrip);
                mActionMode = TrickyTripperActivity.this.startSupportActionMode(mActionModeCallback);
                mActionMode.setTitle(selectedTrip.toString());
                view.setSelected(true);
                return true;
            }
        });
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        int[] optionIds;
        if (mDrawerLayout.isDrawerOpen(mDrawerList)) {
            optionIds = new int[]{
                    R.id.option_help,
                    R.id.option_create_trip
            };
        } else {
            optionIds = new int[]{
                    R.id.option_add_participant,
                    R.id.option_help,
                    R.id.option_export,
                    R.id.option_preferences
            };

        }
        return getApp()
                .getMiscController()
                .getOptionSupport()
                .populateOptionsMenu(
                        new OptionConstraints()
                                .activity(this)
                                .menu(menu)
                                .options(optionIds));
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.option_export);
        if (item != null) {
            boolean exportEnabled = getApp().getTripController().hasLoadedTripPayments();
            item.setEnabled(exportEnabled);
            item.getIcon().setAlpha((exportEnabled) ? 255 : 64);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle action buttons
        switch (item.getItemId()) {
            case R.id.option_add_participant:
                getApp().getViewController().openCreateParticipant();
                return true;
            case R.id.option_export:
                getApp().getViewController().openExport();
                return true;
            case R.id.option_preferences:
                getApp().getViewController().openSettings();
                return true;
            case R.id.option_help:
                getApp().getViewController().openHelp(getSupportFragmentManager());
                return true;
            case R.id.option_create_trip:
                getApp().getViewController().openEditTrip(this, null);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SELECTED_TAB_INDEX, mViewPager.getCurrentItem());
    }


    private void setTripNameToHeader() {
        Trip trip = getApp().getTripController().getTripLoaded();
        String tripText = trip.getName() + " "
                + CurrencyUtil.getSymbolToCurrency(getResources(), trip.getBaseCurrency(), true);
        getSupportActionBar().setTitle(tripText);
    }

    private TrickyTripperApp getApp() {
        return ((TrickyTripperApp) getApplication());
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        if (requestCode == Rc.ACTIVITY_REQ_CODE_EDIT_TRIP && resultCode == Activity.RESULT_OK) {
            updateList(getApp().getTripController().getAllTrips());
        }
    }

    void updateList(List<TripSummary> currentList) {
        mDrawerListAdapter.clear();
        for (TripSummary summary : currentList) {
            mDrawerListAdapter
                    .add(new TripSummarySymbolResolvingDelegator(summary,
                            getResources()));
        }
        mDrawerListAdapter.sort(mListComparator);
    }

    @Override
    public void finish() {
        loadNextTripIfPreviousDeleted();
        super.finish();
    }

    private void loadNextTripIfPreviousDeleted() {
        TripController ctrl = getApp().getTripController();
        if (ctrl.getTripLoaded() == null) {
            ctrl.loadTrip(ctrl.getAllTrips().get(0));
        }
    }

    private Bundle createBundleWithTripSummaryForPopup(TripSummary selectedTripSummary) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(DIALOG_PARAM_TRIP_SUMMARY, selectedTripSummary);
        return bundle;
    }

    private TripSummary getTripSummaryFromBundle(Bundle args) {
        return  (TripSummary) args.get(DIALOG_PARAM_TRIP_SUMMARY);
    }

    public String getDeleteConfirmationMsg(Bundle bundle) {
        TripSummary tripSummary = getTripSummaryFromBundle(bundle);
        return new StringBuilder()
                .append(tripSummary.getName())
                .append(": ")
                .append(getResources().getString(
                        R.string.trip_manage_view_delete_confirmation))
                .toString();
    }

    public void doDelete(Bundle bundle) {
        TripSummary tripSummary = getTripSummaryFromBundle(bundle);
        getApp().getTripController().deleteTrip(tripSummary);
        updateList(getApp().getTripController().getAllTrips());
        invalidateOptionsMenu();
        setTripNameToHeader();
        updatePagerAdapter();
    }

    //TODO(ckoelle) ABS The delegating TripSummary could be kicked out, as the Adapter is overridden.

    private class MyActionModeCallback implements ActionMode.Callback {

        private TripSummary selectedTrip;

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            boolean canDelete = !getApp().getTripController().oneOrLessTripsLeft();

            int[] optionIds;
            if (canDelete) {
                optionIds = new int[]{
                        R.id.option_delete,
                        R.id.option_edit
                };
            } else {
                optionIds = new int[]{
                        R.id.option_edit
                };
            }

            return getApp().getMiscController().getOptionSupport().populateOptionsMenu(
                    new OptionConstraintsInflater()
                            .activity(mode.getMenuInflater())
                            .menu(menu)
                            .options(optionIds));
        }


        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.option_delete:
                    getApp().getViewController().openDeleteConfirmationOnActivity(
                            getSupportFragmentManager(),
                            createBundleWithTripSummaryForPopup(getTransferableSelection()));
                    mode.finish(); // Closes CAB
                    return true;
                case R.id.option_edit:
                    getApp().getViewController().openEditTrip(TrickyTripperActivity.this, getTransferableSelection());
                    mode.finish(); // Closes CAB
                    return true;
                default:
                    return false;
            }
        }

        private TripSummary getTransferableSelection() {
            TripSummary result = new TripSummary(selectedTrip.getName(), selectedTrip.getBaseCurrency());
            result.setId(selectedTrip.getId());
            return result;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mActionMode = null;
        }

        public void setSelectedTrip(TripSummary selectedTrip) {
            this.selectedTrip = selectedTrip;
        }
    }


    private class MyActionBarDrawerToggle extends ActionBarDrawerToggle {

        public MyActionBarDrawerToggle() {
            super(TrickyTripperActivity.this, TrickyTripperActivity.this.mDrawerLayout, R.string.drawer_open, R.string.drawer_close);
        }

        public void onDrawerClosed(View view) {
            super.onDrawerClosed(view);
            setTripNameToHeader();
            supportInvalidateOptionsMenu();
        }

        public void onDrawerOpened(View drawerView) {
            previouslySelectedTripId = getApp().getTripController().getTripLoaded().getId();
            super.onDrawerOpened(drawerView);
            updateList(getApp().getTripController().getAllTrips());
            getSupportActionBar().setTitle(mDrawerTitle);
            supportInvalidateOptionsMenu();
        }
    }

    public ActionMode getRunningActionMode() {
        return mActionMode;
    }

    public void setRunningActionMode(ActionMode actionMode) {
        this.mActionMode = actionMode;
    }
}