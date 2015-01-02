package de.koelle.christian.trickytripper.activities;

import java.util.Currency;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import de.koelle.christian.common.abs.ActionBarSupport;
import de.koelle.christian.common.options.OptionContraintsAbs;
import de.koelle.christian.common.utils.CurrencyUtil;
import de.koelle.christian.trickytripper.R;
import de.koelle.christian.trickytripper.TrickyTripperApp;
import de.koelle.christian.trickytripper.activitysupport.ButtonSupport;
import de.koelle.christian.trickytripper.activitysupport.CurrencyViewSupport;
import de.koelle.christian.trickytripper.activitysupport.SpinnerViewSupport;
import de.koelle.christian.trickytripper.constants.Rc;
import de.koelle.christian.trickytripper.constants.ViewMode;
import de.koelle.christian.trickytripper.model.TripSummary;
import de.koelle.christian.trickytripper.ui.model.RowObject;

public class TripEditActivity extends ActionBarActivity {

    private ViewMode viewMode;
    private TripSummary trip;
    private boolean hasTripPayments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_trip_view);

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
        }
        else {
            trip = new TripSummary();
            hasTripPayments = false;
            trip.setBaseCurrency(getApp().getMiscController().getDefaultBaseCurrency());
        }
    }

    private void initWidgets() {

        Button buttonPositive = (Button) findViewById(R.id.edit_trip_view_button_positive);
        Button buttonPositiveAndLoad = (Button) findViewById(R.id.edit_trip_view_button_positive_and_load);
        EditText editTextTripName = getEditText();
        Spinner spinner = getSpinner();

        int titleId;
        int positiveButtonLabelId;
        int positiveAndLoadButtonLabelId;

        boolean isEdit = ViewMode.EDIT == viewMode;

        if (isEdit) {
            titleId = R.string.edit_trip_view_edit_heading;
            positiveButtonLabelId = R.string.edit_trip_view_edit_positive_button;
            positiveAndLoadButtonLabelId = R.string.edit_trip_view_edit_positive_button_and_load;
        } else {
            titleId = R.string.edit_trip_view_create_heading;
            positiveButtonLabelId = R.string.edit_trip_view_create_positive_button;
            positiveAndLoadButtonLabelId = R.string.edit_trip_view_create_positive_button_and_load;
        }

        setTitle(titleId);

        List<RowObject> spinnerObjects = CurrencyViewSupport
                .wrapCurrenciesInRowObject(CurrencyUtil
                        .getSupportedCurrencies(getResources()), getResources());

        ArrayAdapter<RowObject> spinnerAdapter = new ArrayAdapter<RowObject>(this,
                android.R.layout.simple_spinner_item,
                spinnerObjects);

        spinnerAdapter.setDropDownViewResource(R.layout.selection_list_medium);
        spinner.setPromptId(R.string.edit_trip_view_label_base_currency_spinner_prompt);
        spinner.setAdapter(spinnerAdapter);

        ButtonSupport.disableButtonOnBlankInput(editTextTripName, buttonPositive);
        ButtonSupport.disableButtonOnBlankInput(editTextTripName, buttonPositiveAndLoad);

        buttonPositive.setText(positiveButtonLabelId);
        buttonPositiveAndLoad.setText(positiveAndLoadButtonLabelId);

        String name = trip.getName();
        Currency currency = trip.getBaseCurrency();

        spinner.setEnabled(!isEdit || !hasTripPayments);
        editTextTripName.setText(name);

        SpinnerViewSupport.setSelection(spinner, currency, (ArrayAdapter) spinnerAdapter);

        buttonPositive.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                processButtonClick(false);
            }
        });

        buttonPositiveAndLoad.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                processButtonClick(true);
            }
        });
    }

    @SuppressWarnings("unchecked")
    private void processButtonClick(boolean isSaveAndLoad) {

        String inputName = getEditText().getText().toString().trim();
        Object o = getSpinner().getSelectedItem();
        Currency inputCurrency = ((RowObject<Currency>) o).getRowObject();

        trip.setName(inputName);
        trip.setBaseCurrency(inputCurrency);

        if (tryToSave(isSaveAndLoad)) {
            Toast.makeText(getApplicationContext(),
                    R.string.edit_trip_view_msg, Toast.LENGTH_SHORT)
                    .show();
        }
        else {
            prepareResultAndFinish(isSaveAndLoad);
        }
    }

    private boolean tryToSave(boolean isSaveAndLoad) {
        return (isSaveAndLoad) ?
                !(getApp().getTripController().persistAndLoadTrip(trip)) :
                !(getApp().getTripController().persist(trip));
    }

    private Spinner getSpinner() {
        return (Spinner) findViewById(R.id.edit_trip_view_spinner_base_currency);
    }

    private EditText getEditText() {
        return (EditText) findViewById(R.id.edit_trip_view_editText_tripName);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return getApp()
                .getMiscController()
                .getOptionSupport()
                .populateOptionsMenu(
                        new OptionContraintsAbs()
                                .activity(getMenuInflater()).menu(menu)
                                .options(new int[] {
                                        R.id.option_help
                                }));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
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

    private void prepareResultAndFinish(boolean isSaveAndLoad) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra(Rc.ACTIVITY_PARAM_TRIP_EDIT_OUT_SAVE_AND_LOAD, isSaveAndLoad);
        setResult(RESULT_OK, resultIntent);
        finish();
    }
}
