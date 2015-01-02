package de.koelle.christian.trickytripper.activities;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import de.koelle.christian.common.abs.ActionBarSupport;
import de.koelle.christian.common.options.OptionContraintsAbs;
import de.koelle.christian.trickytripper.R;
import de.koelle.christian.trickytripper.TrickyTripperApp;
import de.koelle.christian.trickytripper.constants.Rc;
import de.koelle.christian.trickytripper.controller.TripController;
import de.koelle.christian.trickytripper.dialogs.DeleteDialogFragment.DeleteConfirmationCallback;
import de.koelle.christian.trickytripper.model.TripSummary;
import de.koelle.christian.trickytripper.model.modelAdapter.TripSummarySymbolResolvingDelegator;
import de.koelle.christian.trickytripper.ui.utils.PrepareOptionsSupport;

public class ManageTripsActivity extends ActionBarActivity implements DeleteConfirmationCallback {

    private static final int MENU_GROUP_STD = 1;
    private static final int MENU_GROUP_DELETE = 2;

    private static final String DIALOG_PARAM_TRIP_SUMMARY = "dialogParamTripSummary";

    private ArrayAdapter<TripSummary> arrayAdapterTripSummary;
    private ListView listView;
    private Comparator<TripSummary> comparator;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manage_trips_view);

        TrickyTripperApp app = getApp();
        final Collator c = app.getMiscController().getDefaultStringCollator();
        comparator = new Comparator<TripSummary>() {
            public int compare(TripSummary object1, TripSummary object2) {
                return c.compare(object1.getName(), object2.getName());
            }
        };

        listView = (ListView) findViewById(R.id.manageTripsView_list_view_trips);

        initListView(listView, app);
        addClickListener(listView, app);

        registerForContextMenu(listView);

        ActionBarSupport.addBackButton(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        updateList(getApp().getTripController().getAllTrips());
        supportInvalidateOptionsMenu();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return getApp()
                .getMiscController()
                .getOptionSupport()
                .populateOptionsMenu(
                        new OptionContraintsAbs()
                                .activity(getMenuInflater()).menu(menu)
                                .options(new int[]{
                                        R.id.option_help,
                                        R.id.option_create_trip
                                }));
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
         PrepareOptionsSupport.reset(menu);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        if (requestCode == Rc.ACTIVITY_PARAM_EDIT_TRIP_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            boolean wasSaveAndLoadRequested =
                    resultData.getBooleanExtra(Rc.ACTIVITY_PARAM_TRIP_EDIT_OUT_SAVE_AND_LOAD, false);
            if (wasSaveAndLoadRequested) {
                finish();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.option_help:
                getApp().getViewController().openHelp(getSupportFragmentManager());
                return true;
            case R.id.option_create_trip:
                getApp().getViewController().openEditTrip(this, null);
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void initListView(ListView listView, TrickyTripperApp app) {
        arrayAdapterTripSummary = new ArrayAdapter<TripSummary>(
                ManageTripsActivity.this,
                android.R.layout.simple_list_item_1,
                new ArrayList<TripSummary>());

        listView.setAdapter(arrayAdapterTripSummary);
        listView.setChoiceMode(ListView.CHOICE_MODE_NONE);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenuInfo menuInfo) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        TripSummary p = arrayAdapterTripSummary.getItem(info.position);
        menu.setHeaderTitle(p.getName());

        menu.add(MENU_GROUP_STD, R.string.common_button_edit, Menu.NONE,
                getResources().getString(R.string.common_button_edit));

        menu.add(MENU_GROUP_DELETE, R.string.common_button_delete, Menu.NONE,
                getResources().getString(R.string.common_button_delete));

        menu.setGroupEnabled(MENU_GROUP_DELETE, !getApp().getTripController()
                .oneOrLessTripsLeft());
    }

    @Override
    public boolean onContextItemSelected(android.view.MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
                .getMenuInfo();
        TripSummary selectedTripSummary = unwrap(arrayAdapterTripSummary.getItem(info.position));

        switch (item.getItemId()) {
            case R.string.common_button_edit:
                getApp().getViewController().openEditTrip(this, selectedTripSummary);
                return true;

            case R.string.common_button_delete:
                getApp().getViewController().openDeleteConfirmationOnActivity(
                        getSupportFragmentManager(),
                        createBundleWithTripSummaryForPopup(selectedTripSummary));
                return true;

        }
        return false;
    }

    private TripSummary unwrap(TripSummary item) {
        if (item instanceof TripSummarySymbolResolvingDelegator) {
            return ((TripSummarySymbolResolvingDelegator) item).getNested();
        }
        return item;
    }

    private void addClickListener(final ListView listView,
                                  final TrickyTripperApp app) {
        listView.setOnItemClickListener(new OnItemClickListener() {

            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int position, long id) {
                TripSummary selectedTripSummary = arrayAdapterTripSummary
                        .getItem(position);
                app.getTripController().loadTrip(selectedTripSummary);
                finish();

            }
        });
    }

    void updateList(List<TripSummary> currentList) {
        arrayAdapterTripSummary.clear();
        for (TripSummary summary : currentList) {
            arrayAdapterTripSummary
                    .add(new TripSummarySymbolResolvingDelegator(summary,
                            getResources()));
        }
        arrayAdapterTripSummary.sort(comparator);
        listView.invalidateViews();
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

    private TrickyTripperApp getApp() {
        return ((TrickyTripperApp) getApplication());
    }

    private Bundle createBundleWithTripSummaryForPopup(TripSummary selectedTripSummary) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(DIALOG_PARAM_TRIP_SUMMARY, selectedTripSummary);
        return bundle;
    }

    private TripSummary getTripSummaryFromBundle(Bundle args) {
        TripSummary selectedTripSummary = (TripSummary) args.get(DIALOG_PARAM_TRIP_SUMMARY);
        return selectedTripSummary;
    }

    public String getDeleteConfirmationMsg(Bundle bundle) {
        TripSummary tripSummary = getTripSummaryFromBundle(bundle);
        return new StringBuilder()
                .append(tripSummary.getName())
                .append(": ")
                .append(getResources().getString(
                        R.string.manage_trips_view_delete_confirmation))
                .toString();
    }

    public void doDelete(Bundle bundle) {
        TripSummary tripSummary = getTripSummaryFromBundle(bundle);
        getApp().getTripController().deleteTrip(tripSummary);
        updateList(getApp().getTripController().getAllTrips());
    }

}
