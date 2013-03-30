package de.koelle.christian.trickytripper.activities;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import de.koelle.christian.common.abs.ActionBarSupport;
import de.koelle.christian.common.options.OptionContraints;
import de.koelle.christian.common.options.OptionContraintsAbs;
import de.koelle.christian.trickytripper.R;
import de.koelle.christian.trickytripper.TrickyTripperApp;
import de.koelle.christian.trickytripper.activitysupport.PopupFactory;
import de.koelle.christian.trickytripper.activitysupport.SpinnerViewSupport;
import de.koelle.christian.trickytripper.constants.Rd;
import de.koelle.christian.trickytripper.model.ExchangeRate;
import de.koelle.christian.trickytripper.model.ExchangeRateSelection;
import de.koelle.christian.trickytripper.model.modelAdapter.ExchangeRateRowListAdapter;
import de.koelle.christian.trickytripper.model.modelAdapter.ExchangeRateRowListAdapter.DisplayMode;
import de.koelle.christian.trickytripper.ui.model.SpinnerObject;

public class DeleteExchangeRatesActivity extends SherlockActivity {

    private static final String DIALOG_PARAM_EXCHANGE_RATES = "dialogParamExchangeRates";

    private ListView listView;
    private ExchangeRateRowListAdapter adapter;
    private Comparator<ExchangeRate> comparator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.delete_exchange_rates_view);

        initList();
        updateList();
        initInvisibleSpinner();
        updateButtonState();
        
        ActionBarSupport.addBackButton(this);

    }

    private void initList() {
        listView = (ListView) findViewById(R.id.deleteExchangeRatesViewListViewToBeDeleted);

        adapter = new ExchangeRateRowListAdapter(this, android.R.layout.simple_list_item_1,
                new ArrayList<ExchangeRate>(),
                DisplayMode.DOUBLE_WITH_SELECTION);
        listView.setAdapter(adapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> list, View lv, int position,
                    long id) {
                ExchangeRate rate = adapter.getItem(position);
                rate.setSelected(!rate.isSelected());
                updateButtonState();
            }
        });

        final Collator collator = getApp().getMiscController().getDefaultStringCollator();
        comparator = new Comparator<ExchangeRate>() {
            public int compare(ExchangeRate object1, ExchangeRate object2) {
                return collator.compare(object1.getSortString(), object2.getSortString());
            }
        };
    }

    @SuppressWarnings("unused")
    public void deleteExchangeRates(View view) {
        showDialog(Rd.DIALOG_DELETE, wrapSelectionInBundle(getSelection()));
    }

    private Bundle wrapSelectionInBundle(ArrayList<ExchangeRate> selection) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(DIALOG_PARAM_EXCHANGE_RATES, selection);
        return bundle;
    }

    @SuppressWarnings("unchecked")
    public List<ExchangeRate> getSelectionFromBundle(Bundle args) {
        if (args != null) {
            return (ArrayList<ExchangeRate>) args.get(DIALOG_PARAM_EXCHANGE_RATES);
        }
        return null;
    }

    @SuppressWarnings("unused")
    public void select(View view) {
        ((Spinner) findViewById(R.id.deleteExchangeRatesViewSpinner)).performClick();
    }

    void updateList() {
        List<ExchangeRate> allRates = getApp().getExchangeRateController().getAllExchangeRatesWithoutInversion();
        if (allRates.isEmpty()) {
            finish();
        }
        else {
            adapter.clear();
            for (ExchangeRate rate : allRates) {
                adapter.add(rate);
            }
            adapter.sort(comparator);

            /**
             * This selection shit is required, as the widget still has its
             * selection state when coming from deletion. The state does not
             * reset by calling adapter.notifyDataSetChanged(), as said on the
             * net.
             */
            for (int i = 0; i < adapter.getCount(); i++) {
                listView.setItemChecked(i, false);
            }

            listView.invalidateViews();
        }

    }

    private ArrayList<ExchangeRate> getSelection() {
        ArrayList<ExchangeRate> ratesSelected = new ArrayList<ExchangeRate>();

        for (int i = 0; i < adapter.getCount(); i++) {
            ExchangeRate rate = adapter.getItem(i);
            if (rate.isSelected()) {
                ratesSelected.add(rate);
            }
        }
        return ratesSelected;
    }

    private TrickyTripperApp getApp() {
        return ((TrickyTripperApp) getApplication());
    }

    private void initInvisibleSpinner() {
        final Spinner spinner = SpinnerViewSupport.configureDeleteExchangeRateSpinner(this, this,
                R.id.deleteExchangeRatesViewSpinner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                SpinnerObject spinnerObject = (SpinnerObject) spinner.getSelectedItem();
                ExchangeRateSelection instruction = ExchangeRateSelection.getByResourceId((int) spinnerObject
                        .getId());
                modifySelection(instruction);
                return;
            }

            public void onNothingSelected(AdapterView<?> parent) {
                // intentionally blank
            }

        });
    }

    protected void modifySelection(ExchangeRateSelection instructionsSelected) {
        for (int i = 0; i < adapter.getCount(); i++) {
            ExchangeRate rate = adapter.getItem(i);
            boolean selectionToBe = false;
            if (ExchangeRateSelection.ALL.equals(instructionsSelected)
                    || !rate.isImported() && ExchangeRateSelection.ALL_CUSTOM.equals(instructionsSelected)
                    || rate.isImported() && ExchangeRateSelection.ALL_IMPORTED.equals(instructionsSelected)) {
                selectionToBe = true;
            }
            rate.setSelected(selectionToBe);
            listView.setItemChecked(i, selectionToBe);
        }
        updateButtonState();
    }

    private void updateButtonState() {

        ((Button) findViewById(R.id.deleteExchangeRatesViewButtonDeleteSelection)).setEnabled(isButtonEnabled());
    }

    private boolean isButtonEnabled() {
        for (int i = 0; i < adapter.getCount(); i++) {
            ExchangeRate rate = adapter.getItem(i);
            if (rate.isSelected()) {
                return true;
            }
        }
        return false;
    }

    /* ============== Options Shit [BGN] ============== */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return getApp().getMiscController().getOptionSupport().populateOptionsMenu(
                new OptionContraintsAbs().activity(getSupportMenuInflater()).menu(menu)
                        .options(new int[] {
                                R.id.option_help
                        }));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.option_help:
            showDialog(Rd.DIALOG_HELP);
            return true;
        case android.R.id.home:
            onBackPressed();
            return true;                  
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    /* ============== Options Shit [END] ============== */

    /* ============== Dialog Shit [END] ============== */

    @Override
    protected Dialog onCreateDialog(int id, Bundle args) {
        Dialog dialog;
        switch (id) {
        case Rd.DIALOG_HELP:
            dialog = PopupFactory.createHelpDialog(this, getApp().getMiscController(), Rd.DIALOG_HELP);
            break;
        case Rd.DIALOG_DELETE:
            dialog = PopupFactory.showDeleteConfirmationDialog(this);
            break;
        default:
            dialog = null;
        }

        return dialog;
    }

    @Override
    protected void onPrepareDialog(int id, Dialog dialog, final Bundle args) {
        switch (id) {
        case Rd.DIALOG_HELP:
            // intentionally blank
            break;
        case Rd.DIALOG_DELETE:
            final Dialog dialogFinal = dialog;
            final List<ExchangeRate> selection = getSelectionFromBundle(args);
            ((TextView) dialog.findViewById(android.R.id.message)).setText(getResources().getString(
                    R.string.deleteExchangeRatesViewDeleteConfirmation).replace("@@1@@", selection.size() + ""));
            ((Button) dialog.findViewById(android.R.id.button1)).setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    dialogFinal.dismiss();
                    getApp().getExchangeRateController().deleteExchangeRates(selection);
                    updateList();
                }
            });
            break;
        default:
            dialog = null;
        }
        super.onPrepareDialog(id, dialog, args);
    }

    /* ============== Dialog Shit [END] ============== */
}
