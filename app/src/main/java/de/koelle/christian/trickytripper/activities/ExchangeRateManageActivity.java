package de.koelle.christian.trickytripper.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import de.koelle.christian.common.abs.ActionBarSupport;
import de.koelle.christian.common.options.OptionConstraintsInflater;
import de.koelle.christian.common.support.DimensionSupport;
import de.koelle.christian.common.utils.Assert;
import de.koelle.christian.trickytripper.R;
import de.koelle.christian.trickytripper.TrickyTripperApp;
import de.koelle.christian.trickytripper.activitysupport.ImportOptionSupport;
import de.koelle.christian.trickytripper.dialogs.DeleteDialogFragment.DeleteConfirmationCallback;
import de.koelle.christian.trickytripper.model.ExchangeRate;
import de.koelle.christian.trickytripper.model.modelAdapter.ExchangeRateRowListAdapter;
import de.koelle.christian.trickytripper.model.modelAdapter.ExchangeRateRowListAdapter.DisplayMode;
import de.koelle.christian.trickytripper.modelutils.AmountViewUtils;
import de.koelle.christian.trickytripper.ui.utils.PrepareOptionsSupport;

public class ExchangeRateManageActivity extends AppCompatActivity implements DeleteConfirmationCallback {

    private static final String DIALOG_PARAM_EXCHANGE_RATE = "dialogParamExchangeRate";
    private final List<ExchangeRate> exchangeRateList = new ArrayList<ExchangeRate>();
    private ArrayAdapter<ExchangeRate> listAdapter;
    private Comparator<ExchangeRate> comparator;
    private ImportOptionSupport importOptionSupport;
    private ListView listView;

    private MyActionModeCallback mActionModeCallback = new MyActionModeCallback();
    private ActionMode mActionMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_view);

        TrickyTripperApp app = getApp();

        this.importOptionSupport = new ImportOptionSupport(getApp()
                .getViewController(), getApp().getMiscController(), this);

        final Collator collator = app.getMiscController()
                .getDefaultStringCollator();
        comparator = new Comparator<ExchangeRate>() {
            public int compare(ExchangeRate object1, ExchangeRate object2) {
                return collator.compare(object1.getSortString(),
                        object2.getSortString());
            }
        };

        listView = (ListView) findViewById(android.R.id.list);
        initListView(listView);
        registerForContextMenu(listView);

        TextView textView = (TextView) findViewById(android.R.id.empty);
        textView.setText(getResources().getString(
                R.string.exchangeRateManageViewBlankListNotification));
        
        DimensionSupport dimensionSupport = getApp().getMiscController().getDimensionSupport();
        int px16 = dimensionSupport.dp2Px(16);
        int px08 = dimensionSupport.dp2Px(8);
        textView.setPadding(px16, px08, px08, px16);



        ActionBarSupport.addBackButton(this);
    }

    private void initListView(final ListView listView2) {

        listView2.setEmptyView(findViewById(android.R.id.empty));

        listAdapter = new ExchangeRateRowListAdapter(this,
                android.R.layout.simple_list_item_1, exchangeRateList,
                DisplayMode.SINGLE);

        listView2.setAdapter(listAdapter);
        listView2.setChoiceMode(ListView.CHOICE_MODE_NONE);

        listView2.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mActionMode != null) {
                    return;
                }
                ExchangeRate row = (ExchangeRate) listView2.getItemAtPosition(position);
                if (!row.isImported()) {
                    getApp().getViewController().openEditExchangeRate(ExchangeRateManageActivity.this, row);
                }
            }
        });
        listView2.setLongClickable(true);
        listView2.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (mActionMode != null) {
                    return false;
                }
                ExchangeRate selectionNonInverted = getNonInverted(listAdapter.getItem(position));
                mActionModeCallback.setSelection(selectionNonInverted);
                mActionMode = ExchangeRateManageActivity.this.startSupportActionMode(mActionModeCallback);
                StringBuilder builder = new StringBuilder()
                        .append(selectionNonInverted.getCurrencyFrom().getCurrencyCode())
                        .append(" > ")
                        .append(selectionNonInverted.getCurrencyTo().getCurrencyCode())
                        .append(" | ")
                        .append(selectionNonInverted.getCurrencyTo().getCurrencyCode())
                        .append(" > ")
                        .append(selectionNonInverted.getCurrencyFrom().getCurrencyCode());
                mActionMode.setTitle(builder);
                view.setSelected(true);
                return true;
            }
        });


        updateList();
    }

    void updateList() {
        List<ExchangeRate> currentList = getApp().getExchangeRateController()
                .getAllExchangeRatesWithoutInversion();
        listAdapter.clear();
        for (ExchangeRate rate : currentList) {
            listAdapter.add(rate);
            listAdapter.add(rate.cloneToInversion());
        }
        listAdapter.sort(comparator);
        listView.invalidateViews();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateList();
        supportInvalidateOptionsMenu();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        return getApp()
                .getMiscController()
                .getOptionSupport()
                .populateOptionsMenu(
                        new OptionConstraintsInflater()
                                .activity(getMenuInflater()).menu(menu)
                                .options(new int[]{
                                        R.id.option_help,
                                        R.id.option_import,
                                        R.id.option_delete,
                                        R.id.option_create_exchange_rate}));
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.option_delete).setVisible(!exchangeRateList.isEmpty());

        PrepareOptionsSupport.reset(menu);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.option_delete:
                openDeleteActivity();
                return true;
            case R.id.option_import:
                return importOptionSupport.onOptionsItemSelected(this);
            case R.id.option_help:
                getApp().getViewController().openHelp(getSupportFragmentManager());
                return true;
            case R.id.option_create_exchange_rate:
                openCreateActivity();
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public String getDeleteConfirmationMsg(Bundle bundle) {
        final ExchangeRate row = getRowFromBundle(bundle);
        return new StringBuilder()
                .append(getResources().getString(R.string.exchangeRateManageViewDeleteConfirmation))
                .append("\n")
                .append(getStringOfExchangeRate(row))
                .toString();
    }

    public void doDelete(Bundle bundle) {
        final ExchangeRate row = getRowFromBundle(bundle);
        deleteRowAndUpdateList(row);
    }

    private ExchangeRate getNonInverted(ExchangeRate rate) {
        if (!rate.isInversion()) {
            return rate;
        }

        ExchangeRate result = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            ExchangeRate item = listAdapter.getItem(i);
            if (rate.getId() == item.getId() && !item.isInversion()) {
                result = item;
                break;
            }
        }
        Assert.notNull(result);
        return result;
    }


    private StringBuilder getStringOfExchangeRate(ExchangeRate row) {
        ExchangeRate inversion = row.cloneToInversion();
        return new StringBuilder()
                .append(row.getCurrencyFrom().getCurrencyCode()).append(" > ")
                .append(row.getCurrencyTo().getCurrencyCode()).append(" = ")
                .append(getRateString(row.getExchangeRate())).append("\n")
                .append(inversion.getCurrencyFrom().getCurrencyCode())
                .append(" > ")
                .append(inversion.getCurrencyTo().getCurrencyCode())
                .append(" = ").append(getRateString(inversion.getExchangeRate()))
        /**/;
    }

    public String getRateString(Double input) {
        return AmountViewUtils.getDoubleString(getResources().getConfiguration().locale, input);
    }

    public Bundle wrapRowInBundle(ExchangeRate row) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(DIALOG_PARAM_EXCHANGE_RATE, row);
        return bundle;
    }

    public ExchangeRate getRowFromBundle(Bundle args) {
        if (args != null) {
            return (ExchangeRate) args.get(DIALOG_PARAM_EXCHANGE_RATE);
        }
        return null;
    }

    private void deleteRowAndUpdateList(ExchangeRate row) {
        ArrayList<ExchangeRate> rates = new ArrayList<ExchangeRate>();
        rates.add(row);

        getApp().getExchangeRateController().deleteExchangeRates(rates);
        updateList();
    }

    private void openDeleteActivity() {
        getApp().getViewController().openDeleteExchangeRates(this);
    }

    private void openCreateActivity() {
        getApp().getViewController().openCreateExchangeRate(this);
    }

    private ExchangeRate getRateByInfo(AdapterView.AdapterContextMenuInfo info) {
        ExchangeRate rate = listAdapter.getItem(info.position);
        return rate;
    }

    private TrickyTripperApp getApp() {
        return ((TrickyTripperApp) getApplication());
    }

    private class MyActionModeCallback implements ActionMode.Callback {

        private ExchangeRate selectionUninverted;

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            boolean canEdit = !selectionUninverted.isImported();
            int[] optionIds;
            if (canEdit) {
                optionIds = new int[]{
                        R.id.option_delete,
                        R.id.option_edit
                };
            } else {
                optionIds = new int[]{
                        R.id.option_delete
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
                            wrapRowInBundle(selectionUninverted));
                    mode.finish();
                    return true;
                case R.id.option_edit:
                    getApp().getViewController().openEditExchangeRate(ExchangeRateManageActivity.this, selectionUninverted);
                    mode.finish();
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mActionMode = null;
        }

        public void setSelection(ExchangeRate selection) {
            this.selectionUninverted = selection;
        }
    }
}
