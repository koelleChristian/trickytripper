package de.koelle.christian.trickytripper.activities;

import java.text.Collator;
import java.util.Comparator;
import java.util.Currency;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import de.koelle.christian.trickytripper.R;
import de.koelle.christian.trickytripper.TrickyTripperApp;
import de.koelle.christian.trickytripper.activitysupport.ButtonSupport;
import de.koelle.christian.trickytripper.activitysupport.PopupFactory;
import de.koelle.christian.trickytripper.constants.Rc;
import de.koelle.christian.trickytripper.controller.TripExpensesFktnController;
import de.koelle.christian.trickytripper.model.TripSummary;

public class ManageTripsActivity extends Activity {

    private enum ButtonClickMode {
        SAVE,
        SAVE_AND_LOAD;
    }

    private static final int DIALOG_EDIT = 1;
    private static final int DIALOG_CREATE = 2;
    private static final int DIALOG_DELETE = 3;

    private static final int MENU_GROUP_STD = 1;
    private static final int MENU_GROUP_DELETE = 2;

    private static final String DIALOG_PARAM_TRIP_SUMMARY = "dialogParamTripSummary";

    private ArrayAdapter<TripSummary> arrayAdapterTripSummary;
    private ListView listView;
    private Comparator<TripSummary> comparator;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manage_trips);

        TrickyTripperApp app = getApp();

        final Collator c = app.getFktnController().getDefaultStringCollator();
        comparator = new Comparator<TripSummary>() {
            public int compare(TripSummary object1, TripSummary object2) {
                return c.compare(object1.getName(), object2.getName());
            }
        };

        listView = (ListView) findViewById(R.id.manageTripsView_list_view_trips);

        initListView(listView, app);
        addClickListener(listView, app);

        registerForContextMenu(listView);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.layout.general_options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.general_options_help:
            showDialog(Rc.DIALOG_SHOW_HELP);
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    private void initListView(ListView listView, TrickyTripperApp app) {

        arrayAdapterTripSummary = new ArrayAdapter<TripSummary>(ManageTripsActivity.this,
                android.R.layout.simple_list_item_1, app.getFktnController().getAllTrips());

        listView.setAdapter(arrayAdapterTripSummary);
        listView.setChoiceMode(ListView.CHOICE_MODE_NONE);
        arrayAdapterTripSummary.sort(comparator);
    }

    @Override
    protected Dialog onCreateDialog(int id, Bundle args) {
        Dialog dialog;
        switch (id) {
        case DIALOG_CREATE:
            dialog = createCreatePopup();
            break;
        case DIALOG_EDIT:
            dialog = createEditPopup();
            break;
        case DIALOG_DELETE:
            dialog = createDeletePopup();
            break;
        case Rc.DIALOG_SHOW_HELP:
            dialog = PopupFactory.createHelpDialog(this, getApp().getFktnController(), Rc.DIALOG_SHOW_HELP);
            break;
        default:
            dialog = null;
        }
        return dialog;
    }

    @Override
    protected void onPrepareDialog(int id, Dialog dialog, Bundle args) {
        switch (id) {
        case DIALOG_CREATE:
            updateCreateOrEditDialog(dialog, args);
            break;
        case DIALOG_EDIT:
            updateCreateOrEditDialog(dialog, args);
            break;
        case DIALOG_DELETE:
            updateDeleteDialog(dialog, args);
            break;
        case Rc.DIALOG_SHOW_HELP:
            // intentionally blank
            break;
        default:
            dialog = null;
        }
        super.onPrepareDialog(id, dialog, args);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        TripSummary p = arrayAdapterTripSummary.getItem(info.position);
        menu.setHeaderTitle(p.getName());

        menu.add(MENU_GROUP_STD, R.string.manage_trips_view_fktn_edit, Menu.NONE,
                getResources().getString(R.string.manage_trips_view_fktn_edit));

        menu.add(MENU_GROUP_DELETE, R.string.manage_trips_view_fktn_delete, Menu.NONE,
                getResources().getString(R.string.manage_trips_view_fktn_delete));

        menu.setGroupEnabled(MENU_GROUP_DELETE, !getApp().getFktnController().oneOrLessTripsLeft());
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        TripSummary selectedTripSummary = arrayAdapterTripSummary.getItem(info.position);
        switch (item.getItemId()) {
        case R.string.manage_trips_view_fktn_edit: {
            showDialog(DIALOG_EDIT, createBundleWithTripSummarySelected(selectedTripSummary));
            return true;
        }
        case R.string.manage_trips_view_fktn_delete: {
            showDialog(DIALOG_DELETE, createBundleWithTripSummarySelected(selectedTripSummary));
            return true;
        }
        }
        return false;
    }

    private void deleteTrip(TripSummary tripSummary) {
        getApp().getFktnController().deleteTrip(tripSummary);
    }

    private void addClickListener(final ListView listView, final TrickyTripperApp app) {
        listView.setOnItemClickListener(new OnItemClickListener() {

            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {
                TripSummary selectedTripSummary = arrayAdapterTripSummary.getItem(position);
                app.getFktnController().loadTrip(selectedTripSummary);
                finish();

            }
        });
    }

    public void createNewTrip(View view) {
        if (R.id.manageTripsView_button_create_new_trip == view.getId()) {
            showDialog(DIALOG_CREATE, createBundleWithTripSummarySelected(new TripSummary()));
        }
    }

    void updateList(List<TripSummary> currentList) {
        arrayAdapterTripSummary.clear();
        for (TripSummary summary : currentList) {
            arrayAdapterTripSummary.add(summary);
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
        TripExpensesFktnController ctrl = getApp().getFktnController();
        if (ctrl.getTripLoaded() == null) {
            ctrl.loadTrip(ctrl.getAllTrips().get(0));
        }
    }

    private TrickyTripperApp getApp() {
        return ((TrickyTripperApp) getApplication());
    }

    /* ================== Bundle job ================ */

    private Bundle createBundleWithTripSummarySelected(TripSummary selectedTripSummary) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(DIALOG_PARAM_TRIP_SUMMARY, selectedTripSummary);
        return bundle;
    }

    private TripSummary getTripSummaryFromBundle(Bundle args) {
        if (args == null) {
            return null; // Create
        }
        // Edit
        TripSummary selectedTripSummary = (TripSummary) args.get(DIALOG_PARAM_TRIP_SUMMARY);
        return selectedTripSummary;
    }

    /* ================== Popup job ================ */

    private Dialog createCreatePopup() {
        int titleId = R.string.edit_trip_view_create_heading;
        int positiveButtonLabelId = R.string.edit_trip_view_create_positive_button;
        int positiveAndLoadButtonLabelId = R.string.edit_trip_view_create_positive_button_and_load;
        int negativeButtonLabelId = R.string.common_button_cancel;
        int layoutId = R.layout.edit_trip_view;

        return createTripEditPopup(titleId, positiveButtonLabelId, positiveAndLoadButtonLabelId,
                negativeButtonLabelId, layoutId);
    }

    private Dialog createEditPopup() {
        int titleId = R.string.edit_trip_view_edit_heading;
        int positiveButtonLabelId =
                R.string.edit_trip_view_edit_positive_button;
        int positiveAndLoadButtonLabelId =
                R.string.edit_trip_view_edit_positive_button_and_load;
        int negativeButtonLabelId = R.string.common_button_cancel;
        int layoutId = R.layout.edit_trip_view;

        return createTripEditPopup(titleId, positiveButtonLabelId,
                positiveAndLoadButtonLabelId,
                negativeButtonLabelId, layoutId);
    }

    private Dialog createTripEditPopup(int titleId,
            int positiveButtonLabelId, int positiveAndLoadButtonLabelId, int negativeButtonLabelId, int layoutId) {
        final View viewInf = inflate(layoutId);

        Button buttonPositive = (Button) viewInf.findViewById(R.id.edit_trip_view_button_positive);
        Button buttonPositiveAndLoad = (Button) viewInf.findViewById(R.id.edit_trip_view_button_positive_and_load);
        EditText editTextTripName = (EditText) viewInf.findViewById(R.id.edit_trip_view_editText_tripName);

        ButtonSupport.disableButtonOnBlankInput(editTextTripName, buttonPositive);
        ButtonSupport.disableButtonOnBlankInput(editTextTripName, buttonPositiveAndLoad);

        buttonPositive.setText(positiveButtonLabelId);
        buttonPositiveAndLoad.setText(positiveAndLoadButtonLabelId);

        final AlertDialog dialog = createDialog(titleId, positiveButtonLabelId,
                negativeButtonLabelId,
                viewInf,
                ManageTripsActivity.this);

        return dialog;
    }

    private void updateCreateOrEditDialog(final Dialog dialog, Bundle args) {
        EditText editTextTripName = (EditText) dialog.findViewById(R.id.edit_trip_view_editText_tripName);
        EditText editTextBaseCurrency = (EditText) dialog.findViewById(R.id.edit_trip_view_editText_base_currency);
        final TripSummary selectedTripSummary = getTripSummaryFromBundle(args);
        String name;
        Currency currency;
        if (selectedTripSummary == null) {
            name = "";
            currency = getApp().getFktnController().getDefaultBaseCurrency();
        }
        else {
            name = selectedTripSummary.getName();
            currency = selectedTripSummary.getBaseCurrency();
        }

        editTextTripName.setText(name);
        editTextBaseCurrency.setText(currency.getSymbol());

        Button buttonPositive = (Button) dialog.findViewById(R.id.edit_trip_view_button_positive);
        Button buttonPositiveAndLoad = (Button) dialog.findViewById(R.id.edit_trip_view_button_positive_and_load);
        Button buttonCancel = (Button) dialog.findViewById(R.id.edit_trip_view_button_cancel);

        buttonPositive.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                processButtonClick(getApp(), dialog, ButtonClickMode.SAVE, selectedTripSummary);
            }
        });

        buttonPositiveAndLoad.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                processButtonClick(getApp(), dialog, ButtonClickMode.SAVE_AND_LOAD, selectedTripSummary);
            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog.cancel();
            }
        });
    }

    private void processButtonClick(final TrickyTripperApp app, final Dialog dialog,
            final ButtonClickMode mode, TripSummary recordInEdit) {

        EditText editTextName = (EditText) dialog.findViewById(R.id.edit_trip_view_editText_tripName);
        String inputName = editTextName.getText().toString().trim();

        Currency inputCurrency = app.getFktnController().getDefaultBaseCurrency();
        TripSummary tripSummary = new TripSummary();
        tripSummary.setId(recordInEdit.getId());
        tripSummary.setName(inputName);
        tripSummary.setBaseCurrency(inputCurrency);

        if (ButtonClickMode.SAVE.equals(mode)) {

            if (!(app.getFktnController().persist(tripSummary))) {
                Toast.makeText(getApplicationContext(), R.string.edit_trip_view_msg, Toast.LENGTH_SHORT)
                        .show();
            }
            else {
                dialog.dismiss();
                updateList(app.getAllTrips());
            }
        }
        else if (ButtonClickMode.SAVE_AND_LOAD.equals(mode)) {

            if (!(app.getFktnController().persistAndLoadTrip(tripSummary))) {
                Toast.makeText(getApplicationContext(), R.string.edit_trip_view_msg, Toast.LENGTH_SHORT)
                        .show();
            }
            else {
                dialog.dismiss();
                finish();
            }
        }
    }

    private Dialog createDeletePopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("blank") // Will be updated later
                .setCancelable(false)
                .setPositiveButton(R.string.common_button_yes, null)
                .setNegativeButton(R.string.common_button_no, null);
        AlertDialog result = builder.create();
        return result;
    }

    private void updateDeleteDialog(final Dialog dialog, Bundle args) {
        final TripSummary tripSummary = getTripSummaryFromBundle(args);
        String msg = tripSummary.getName() + ": "
                + getResources().getString(R.string.manage_trips_view_delete_confirmation);
        ((TextView) dialog.findViewById(android.R.id.message)).setText(msg);

        Button positiveButton = (Button) dialog.findViewById(android.R.id.button1);
        Button negativeButton = (Button) dialog.findViewById(android.R.id.button3);

        positiveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                dialog.dismiss();
                ManageTripsActivity.this.deleteTrip(tripSummary);
                updateList(getApp().getAllTrips());
            }
        });
        negativeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                dialog.cancel();
            }
        });
    }

    private View inflate(int layoutId) {
        LayoutInflater inflater = getLayoutInflater();
        final View viewInf = inflater.inflate(layoutId, null);
        return viewInf;
    }

    private AlertDialog createDialog(int titleId, int positiveButtonLabelId,
            int negativeButtonLabelId, final View viewInf, Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder
                .setTitle(titleId)
                .setCancelable(true)
                .setView(viewInf);

        AlertDialog alert = builder.create();
        return alert;
    }

}
