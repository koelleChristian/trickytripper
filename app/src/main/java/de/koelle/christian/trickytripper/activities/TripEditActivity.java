package de.koelle.christian.trickytripper.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.Currency;
import java.util.List;

import de.koelle.christian.common.abs.ActionBarSupport;
import de.koelle.christian.common.options.OptionConstraintsInflater;
import de.koelle.christian.common.text.BlankTextWatcher;
import de.koelle.christian.common.utils.CurrencyUtil;
import de.koelle.christian.common.utils.StringUtils;
import de.koelle.christian.trickytripper.R;
import de.koelle.christian.trickytripper.TrickyTripperApp;
import de.koelle.christian.trickytripper.activitysupport.CurrencyViewSupport;
import de.koelle.christian.trickytripper.activitysupport.SpinnerViewSupport;
import de.koelle.christian.trickytripper.constants.Rc;
import de.koelle.christian.trickytripper.constants.ViewMode;
import de.koelle.christian.trickytripper.model.TripSummary;
import de.koelle.christian.trickytripper.ui.model.RowObject;


public class TripEditActivity extends AppCompatActivity {

    private ViewMode viewMode;
    private TripSummary trip;
    private boolean hasTripPayments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trip_edit_view);

        readAndSetInput(getIntent());
        initWidgets();

        ActionBarSupport.addBackButton(this);
    }

    private void readAndSetInput(Intent intent) {
        viewMode = (ViewMode) getIntent().getExtras().get(Rc.ACTIVITY_PARAM_KEY_VIEW_MODE);
        if (ViewMode.EDIT == viewMode) {
            trip = (TripSummary) intent
                    .getSerializableExtra(Rc.ACTIVITY_PARAM_TRIP_EDIT_IN_TRIP_SUMMARY);
            hasTripPayments = getApp().getTripController().hasTripPayments(trip);
        } else {
            trip = new TripSummary();
            hasTripPayments = false;
            trip.setBaseCurrency(getApp().getMiscController().getDefaultBaseCurrency());
        }
    }

    private void initWidgets() {

        EditText editTextTripName = getEditText();
        Spinner spinner = getSpinner();

        int titleId;

        boolean isEdit = ViewMode.EDIT == viewMode;

        if (isEdit) {
            titleId = R.string.trip_edit_view_edit_heading;
        } else {
            titleId = R.string.trip_edit_view_create_heading;
        }

        setTitle(titleId);

        List<RowObject> spinnerObjects = CurrencyViewSupport
                .wrapCurrenciesInRowObject(CurrencyUtil
                        .getSupportedCurrencies(getResources()), getResources());

        ArrayAdapter<RowObject> spinnerAdapter = new ArrayAdapter<RowObject>(this,
                android.R.layout.simple_spinner_item,
                spinnerObjects);

        spinnerAdapter.setDropDownViewResource(R.layout.selection_list_medium);
        spinner.setPromptId(R.string.trip_edit_view_label_base_currency_spinner_prompt);
        spinner.setAdapter(spinnerAdapter);


        String name = trip.getName();
        Currency currency = trip.getBaseCurrency();

        spinner.setEnabled(!isEdit || !hasTripPayments);
        editTextTripName.setText(name);

        editTextTripName.addTextChangedListener(new BlankTextWatcher() {
            public void afterTextChanged(Editable s) {
                supportInvalidateOptionsMenu();
            }
        });

        SpinnerViewSupport.setSelection(spinner, currency, (ArrayAdapter) spinnerAdapter);

    }

    @SuppressWarnings("unchecked")
    private void doSave() {

        String inputName = getEditText().getText().toString().trim();
        Object o = getSpinner().getSelectedItem();
        Currency inputCurrency = ((RowObject<Currency>) o).getRowObject();

        trip.setName(inputName);
        trip.setBaseCurrency(inputCurrency);

        if (tryToSave()) {
            Toast.makeText(getApplicationContext(),
                    R.string.trip_edit_view_msg, Toast.LENGTH_SHORT)
                    .show();
        } else {
            prepareResultAndFinish();
        }
    }

    private boolean tryToSave() {
        return !getApp().getTripController().persist(trip);
    }

    private Spinner getSpinner() {
        return (Spinner) findViewById(R.id.edit_trip_view_spinner_base_currency);
    }

    private EditText getEditText() {
        return (EditText) findViewById(R.id.edit_trip_view_editText_tripName);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        int[] optionIds;
        if (ViewMode.CREATE.equals(viewMode)) {
            optionIds = new int[]{
                    R.id.option_save_create,
                    R.id.option_help
            };
        } else {
            optionIds = new int[]{
                    R.id.option_save_edit,
                    R.id.option_help
            };
        }
        return getApp()
                .getMiscController()
                .getOptionSupport()
                .populateOptionsMenu(
                        new OptionConstraintsInflater()
                                .activity(getMenuInflater()).menu(menu)
                                .options(optionIds));
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        //TODO(ckoelle) The button could be disabled in edit mode when nothing has changed.
        boolean inputNotBlank = !StringUtils.isBlank(getEditText().getEditableText().toString());
        MenuItem item = menu.findItem(R.id.option_save_create);
        if (item == null) {
            item = menu.findItem(R.id.option_save_edit);
        }
        item.setEnabled(inputNotBlank);
        item.getIcon().setAlpha((inputNotBlank) ? 255 : 64);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.option_save_create:
                doSave();
                return true;
            case R.id.option_save_edit:
                doSave();
                return true;
            case R.id.option_help:
                getApp().getViewController().openHelp(getSupportFragmentManager());
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private TrickyTripperApp getApp() {
        return (TrickyTripperApp) getApplication();
    }

    private void prepareResultAndFinish() {
        Intent resultIntent = new Intent();
        setResult(RESULT_OK, resultIntent);
        finish();
    }
}
