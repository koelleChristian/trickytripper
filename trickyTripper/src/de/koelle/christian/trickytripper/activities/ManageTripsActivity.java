package de.koelle.christian.trickytripper.activities;

import java.text.Collator;
import java.util.ArrayList;
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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import de.koelle.christian.common.utils.CurrencyUtil;
import de.koelle.christian.trickytripper.R;
import de.koelle.christian.trickytripper.TrickyTripperApp;
import de.koelle.christian.trickytripper.activitysupport.ButtonSupport;
import de.koelle.christian.trickytripper.activitysupport.PopupFactory;
import de.koelle.christian.trickytripper.activitysupport.SpinnerViewSupport;
import de.koelle.christian.trickytripper.constants.Rc;
import de.koelle.christian.trickytripper.controller.TripExpensesFktnController;
import de.koelle.christian.trickytripper.model.TripSummary;
import de.koelle.christian.trickytripper.model.modelAdapter.TripSummarySymbolResolvingDelegator;
import de.koelle.christian.trickytripper.ui.model.RowObject;
import de.koelle.christian.trickytripper.ui.model.RowObjectCallback;

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
    private static final String DIALOG_PARAM_IS_NEW = "dialogParamIsNew";
    private static final String DIALOG_PARAM_HAS_TRIP_PAYMENTS = "dialogParamHasTripPayments";

    private ArrayAdapter<TripSummary> arrayAdapterTripSummary;
    private ListView listView;
    private Comparator<TripSummary> comparator;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manage_trips_view);

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
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    private void initListView(ListView listView, TrickyTripperApp app) {

        arrayAdapterTripSummary = new ArrayAdapter<TripSummary>(ManageTripsActivity.this,
                android.R.layout.simple_list_item_1, new ArrayList<TripSummary>());

        listView.setAdapter(arrayAdapterTripSummary);
        listView.setChoiceMode(ListView.CHOICE_MODE_NONE);

        updateList(app.getFktnController().getAllTrips());
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
        boolean hasTripPayments = getApp().getFktnController().hasTripPayments(selectedTripSummary);
        boolean isTripNew = false;

        switch (item.getItemId()) {
        case R.string.manage_trips_view_fktn_edit: {
            showDialog(DIALOG_EDIT,
                    createBundleWithTripSummaryForPopup(selectedTripSummary, isTripNew, hasTripPayments));
            return true;
        }
        case R.string.manage_trips_view_fktn_delete: {
            showDialog(DIALOG_DELETE,
                    createBundleWithTripSummaryForPopup(selectedTripSummary, isTripNew, hasTripPayments));
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
            TripSummary newTripSummary = new TripSummary();
            newTripSummary.setBaseCurrency(getApp().getFktnController().getDefaultBaseCurrency());
            showDialog(DIALOG_CREATE, createBundleWithTripSummaryForPopup(newTripSummary, true, false));
        }
    }

    void updateList(List<TripSummary> currentList) {
        arrayAdapterTripSummary.clear();
        for (TripSummary summary : currentList) {
            arrayAdapterTripSummary.add(new TripSummarySymbolResolvingDelegator(summary, getResources()));
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

    private Bundle createBundleWithTripSummaryForPopup(TripSummary selectedTripSummary, boolean isNew,
            boolean hasTripPayments) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(DIALOG_PARAM_TRIP_SUMMARY, selectedTripSummary);
        bundle.putBoolean(DIALOG_PARAM_IS_NEW, isNew);
        bundle.putBoolean(DIALOG_PARAM_HAS_TRIP_PAYMENTS, hasTripPayments);
        return bundle;
    }

    private TripSummary getTripSummaryFromBundle(Bundle args) {
        TripSummary selectedTripSummary = (TripSummary) args.get(DIALOG_PARAM_TRIP_SUMMARY);
        return selectedTripSummary;
    }

    private boolean isCreateTripNotEdit(Bundle args) {
        return args.getBoolean(DIALOG_PARAM_IS_NEW);
    }

    private boolean hasTripPayments(Bundle args) {
        return args.getBoolean(DIALOG_PARAM_HAS_TRIP_PAYMENTS);
    }

    /* ================== Popup job ================ */

    private Dialog createCreatePopup() {
        int titleId = R.string.edit_trip_view_create_heading;
        int positiveButtonLabelId = R.string.edit_trip_view_create_positive_button;
        int positiveAndLoadButtonLabelId = R.string.edit_trip_view_create_positive_button_and_load;
        int layoutId = R.layout.edit_trip_view;

        return createTripEditPopup(titleId, positiveButtonLabelId, positiveAndLoadButtonLabelId,
                layoutId);
    }

    private Dialog createEditPopup() {
        int titleId = R.string.edit_trip_view_edit_heading;
        int positiveButtonLabelId =
                R.string.edit_trip_view_edit_positive_button;
        int positiveAndLoadButtonLabelId =
                R.string.edit_trip_view_edit_positive_button_and_load;
        int layoutId = R.layout.edit_trip_view;

        return createTripEditPopup(titleId, positiveButtonLabelId,
                positiveAndLoadButtonLabelId,
                layoutId);
    }

    @SuppressWarnings("rawtypes")
    private Dialog createTripEditPopup(int titleId,
            int positiveButtonLabelId, int positiveAndLoadButtonLabelId, int layoutId) {
        final View viewInf = inflate(layoutId);

        Button buttonPositive = (Button) viewInf.findViewById(R.id.edit_trip_view_button_positive);
        Button buttonPositiveAndLoad = (Button) viewInf.findViewById(R.id.edit_trip_view_button_positive_and_load);
        EditText editTextTripName = (EditText) viewInf.findViewById(R.id.edit_trip_view_editText_tripName);
        Spinner spinner = (Spinner) viewInf.findViewById(R.id.edit_trip_view_spinner_base_currency);

        List<RowObject> spinnerObjects = wrapCurrenciesInRowObject(CurrencyUtil.getSuportedCurrencies(getResources()));

        ArrayAdapter<RowObject> adapter = new ArrayAdapter<RowObject>(this, android.R.layout.simple_spinner_item,
                spinnerObjects);

        adapter.setDropDownViewResource(R.layout.selection_list_medium);
        spinner.setPromptId(R.string.payment_view_spinner_prompt);
        spinner.setAdapter(adapter);

        ButtonSupport.disableButtonOnBlankInput(editTextTripName, buttonPositive);
        ButtonSupport.disableButtonOnBlankInput(editTextTripName, buttonPositiveAndLoad);

        buttonPositive.setText(positiveButtonLabelId);
        buttonPositiveAndLoad.setText(positiveAndLoadButtonLabelId);

        final AlertDialog dialog = createDialog(titleId,
                viewInf,
                ManageTripsActivity.this);

        return dialog;
    }

    @SuppressWarnings("rawtypes")
    private List<RowObject> wrapCurrenciesInRowObject(List<Currency> supportedCurrencies) {
        List<RowObject> result = new ArrayList<RowObject>();

        for (final Currency c : supportedCurrencies) {
            result.add(new RowObject<Currency>(new RowObjectCallback<Currency>() {
                public String getStringToDisplay(Currency c) {
                    return CurrencyUtil.getFullNameToCurrency(getResources(), c);
                }
            }, c));
        }
        return result;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private void updateCreateOrEditDialog(final Dialog dialog, Bundle args) {
        final EditText editTextTripName = (EditText) dialog.findViewById(R.id.edit_trip_view_editText_tripName);
        final Spinner spinnerCurrency = (Spinner) dialog.findViewById(R.id.edit_trip_view_spinner_base_currency);
        final Button buttonPositive = (Button) dialog.findViewById(R.id.edit_trip_view_button_positive);
        final Button buttonPositiveAndLoad = (Button) dialog.findViewById(R.id.edit_trip_view_button_positive_and_load);
        final Button buttonCancel = (Button) dialog.findViewById(R.id.edit_trip_view_button_cancel);

        final TripSummary selectedTripSummary = getTripSummaryFromBundle(args);
        final boolean isNew = isCreateTripNotEdit(args);
        final boolean hasTripPayments = hasTripPayments(args);

        String name = selectedTripSummary.getName();
        Currency currency = selectedTripSummary.getBaseCurrency();

        spinnerCurrency.setEnabled(isNew || !hasTripPayments);
        editTextTripName.setText(name);
        SpinnerViewSupport.setSelection(spinnerCurrency, currency, (ArrayAdapter) spinnerCurrency.getAdapter());

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

    @SuppressWarnings("unchecked")
    private void processButtonClick(final TrickyTripperApp app, final Dialog dialog,
            final ButtonClickMode mode, TripSummary recordInEdit) {

        final EditText editTextName = (EditText) dialog.findViewById(R.id.edit_trip_view_editText_tripName);
        final Spinner spinnerCurrency = (Spinner) dialog.findViewById(R.id.edit_trip_view_spinner_base_currency);

        String inputName = editTextName.getText().toString().trim();
        Object o = spinnerCurrency.getSelectedItem();
        Currency inputCurrency = ((RowObject<Currency>) o).getRowObject();

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

    private AlertDialog createDialog(int titleId, final View viewInf, Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder
                .setTitle(titleId)
                .setCancelable(true)
                .setView(viewInf);

        AlertDialog alert = builder.create();
        return alert;
    }

}
