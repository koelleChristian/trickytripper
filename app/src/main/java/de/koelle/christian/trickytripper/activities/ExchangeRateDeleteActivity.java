package de.koelle.christian.trickytripper.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Spinner;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import de.koelle.christian.common.abs.ActionBarSupport;
import de.koelle.christian.common.options.OptionConstraintsInflater;
import de.koelle.christian.trickytripper.R;
import de.koelle.christian.trickytripper.TrickyTripperApp;
import de.koelle.christian.trickytripper.activitysupport.SpinnerViewSupport;
import de.koelle.christian.trickytripper.dialogs.DeleteDialogFragment.DeleteConfirmationCallback;
import de.koelle.christian.trickytripper.model.ExchangeRate;
import de.koelle.christian.trickytripper.model.ExchangeRateSelection;
import de.koelle.christian.trickytripper.model.modelAdapter.ExchangeRateRowListAdapter;
import de.koelle.christian.trickytripper.model.modelAdapter.ExchangeRateRowListAdapter.DisplayMode;
import de.koelle.christian.trickytripper.ui.model.SpinnerObject;

public class ExchangeRateDeleteActivity extends AppCompatActivity implements DeleteConfirmationCallback {

    private static final String DIALOG_PARAM_EXCHANGE_RATES = "dialogParamExchangeRates";

    private ListView listView;
    private ExchangeRateRowListAdapter adapter;
    private Comparator<ExchangeRate> comparator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.exchange_rate_delete_view);

        initList();
        updateList();
        initInvisibleSpinner();
        supportInvalidateOptionsMenu();

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
                supportInvalidateOptionsMenu();
            }
        });

        final Collator collator = getApp().getMiscController().getDefaultStringCollator();
        comparator = new Comparator<ExchangeRate>() {
            public int compare(ExchangeRate object1, ExchangeRate object2) {
                return collator.compare(object1.getSortString(), object2.getSortString());
            }
        };
    }

    public void deleteSelectedExchangeRates() {
        getApp().getViewController().openDeleteConfirmationOnActivity(
                getSupportFragmentManager(),
                wrapSelectionInBundle(getSelection()));
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

    public void select(View view) {
        findViewById(R.id.deleteExchangeRatesViewSpinner).performClick();
    }

    void updateList() {
        List<ExchangeRate> allRates = getApp().getExchangeRateController().getAllExchangeRatesWithoutInversion();
        if (allRates.isEmpty()) {
            finish();
        } else {
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
        supportInvalidateOptionsMenu();
    }

    private boolean isSomethingSelected() {
        for (int i = 0; i < adapter.getCount(); i++) {
            ExchangeRate rate = adapter.getItem(i);
            if (rate.isSelected()) {
                return true;
            }
        }
        return false;
    }

    public String getDeleteConfirmationMsg(Bundle bundle) {
        return getResources()
                .getString(R.string.exchangeRateDeleteViewDeleteConfirmation)
                .replace("@@1@@", getSelectionFromBundle(bundle).size() + "");
    }

    public void doDelete(Bundle bundle) {
        getApp().getExchangeRateController().deleteExchangeRates(getSelectionFromBundle(bundle));
        updateList();
        supportInvalidateOptionsMenu();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return getApp().getMiscController().getOptionSupport().populateOptionsMenu(
                new OptionConstraintsInflater().activity(getMenuInflater()).menu(menu)
                        .options(new int[]{
                                R.id.option_accept,
                                R.id.option_help
                        }));
    }

    public boolean onPrepareOptionsMenu(Menu menu) {
        boolean canDelete = isSomethingSelected();
        MenuItem item = menu.findItem(R.id.option_accept);
        item.setTitle(R.string.option_accept_exchange_rate_delete);
        item.setEnabled(canDelete);
        item.getIcon().setAlpha((canDelete) ? 255 : 64);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.option_accept:
                deleteSelectedExchangeRates();
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
}
