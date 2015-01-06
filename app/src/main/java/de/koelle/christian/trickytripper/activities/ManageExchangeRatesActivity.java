package de.koelle.christian.trickytripper.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
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
import java.util.Arrays;
import java.util.Comparator;
import java.util.Currency;
import java.util.List;

import de.koelle.christian.common.abs.ActionBarSupport;
import de.koelle.christian.common.options.OptionContraintsInflater;
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

public class ManageExchangeRatesActivity extends ActionBarActivity implements DeleteConfirmationCallback {

    private static final String DIALOG_PARAM_EXCHANGE_RATE = "dialogParamExchangeRate";

    private ArrayAdapter<ExchangeRate> listAdapter;
    private final List<ExchangeRate> exchangeRateList = new ArrayList<ExchangeRate>();
    private Comparator<ExchangeRate> comparator;
    private ImportOptionSupport importOptionSupport;

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
                R.string.manageExchangeRatesViewBlankListNotification));

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
                TrickyTripperApp app = getApp();
                ExchangeRate row =  (ExchangeRate) listView2.getItemAtPosition(position);
                if (!row.isImported()) {
                    app.getViewController().openEditExchangeRate(ManageExchangeRatesActivity.this, row);
                }
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

    /* ============== Options Shit [BGN] ============== */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        return getApp()
                .getMiscController()
                .getOptionSupport()
                .populateOptionsMenu(
                        new OptionContraintsInflater()
                                .activity(getMenuInflater()).menu(menu)
                                .options(new int[] {
                                        R.id.option_help,
                                        R.id.option_import,
                                        R.id.option_delete,
                                        R.id.option_create_exchange_rate }));
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
        final ExchangeRate row = makeUninverted(getRowFromBundle(bundle));
        return new StringBuilder()
                .append(getResources().getString(R.string.manageExchangeRatesViewDeleteConfirmation))
                .append("\n")
                .append(getStringOfExchangeRate(row))
                .toString();
    }

    public void doDelete(Bundle bundle) {
        final ExchangeRate row = makeUninverted(getRowFromBundle(bundle));
        deleteRowAndUpdateList(row);
    }

    /* ========= Context menu [BGN] =========== */
    private static final int CTX_MENU_GROUP_ID_EDIT = 1;
    private static int CTX_MENU_GROUP_ID_DELETE = 2;
    private static final int CTX_MENU_ID_EDIT = 20;
    private static final int CTX_MENU_ID_DELETE = 21;

    private ListView listView;

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
            ContextMenuInfo menuInfo) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        ExchangeRate row = makeUninverted(getRateByInfo(info));

        menu.setHeaderTitle(getStringOfExchangeRate(row));

        menu.add(CTX_MENU_GROUP_ID_EDIT, CTX_MENU_ID_EDIT, Menu.NONE,
                getResources().getString(R.string.common_button_edit));

        menu.add(CTX_MENU_GROUP_ID_DELETE, CTX_MENU_ID_DELETE, Menu.NONE,
                getResources().getString(R.string.common_button_delete));

        menu.setGroupEnabled(CTX_MENU_GROUP_ID_EDIT, !row.isImported());

    }

    private ExchangeRate makeUninverted(ExchangeRate rate) {
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

    @Override
    public boolean onContextItemSelected(android.view.MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
                .getMenuInfo();
        TrickyTripperApp app = getApp();
        ExchangeRate row = getRateByInfo(info);
        switch (item.getItemId()) {

        case CTX_MENU_ID_DELETE: {
            getApp().getViewController().openDeleteConfirmationOnActivity(
                    getSupportFragmentManager(),
                    wrapRowInBundle(row));
            return true;
        }
        case CTX_MENU_ID_EDIT: {
            app.getViewController().openEditExchangeRate(this, row);
            return true;
        }
        default:
            break;
        }
        return false;
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

    /* ========= Context menu [END] =========== */
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
        getApp().getExchangeRateController().deleteExchangeRates(
                Arrays.asList(new ExchangeRate[] { row }));
        updateList();
    }

    private void openDeleteActivity() {
        getApp().getViewController().openDeleteExchangeRates(this,
                new Currency[0]);
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

}
