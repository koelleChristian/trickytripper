package de.koelle.christian.trickytripper.activities;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Currency;
import java.util.List;

import android.app.Dialog;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockListActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import de.koelle.christian.common.abs.ActionBarSupport;
import de.koelle.christian.common.options.OptionContraints;
import de.koelle.christian.common.options.OptionContraintsAbs;
import de.koelle.christian.common.utils.Assert;
import de.koelle.christian.trickytripper.R;
import de.koelle.christian.trickytripper.TrickyTripperApp;
import de.koelle.christian.trickytripper.activitysupport.ImportOptionSupport;
import de.koelle.christian.trickytripper.activitysupport.PopupFactory;
import de.koelle.christian.trickytripper.constants.Rd;
import de.koelle.christian.trickytripper.model.ExchangeRate;
import de.koelle.christian.trickytripper.model.modelAdapter.ExchangeRateRowListAdapter;
import de.koelle.christian.trickytripper.model.modelAdapter.ExchangeRateRowListAdapter.DisplayMode;
import de.koelle.christian.trickytripper.modelutils.AmountViewUtils;

public class ManageExchangeRatesActivity extends SherlockListActivity {

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

        initListView(getListView());

        TextView textView = (TextView) findViewById(android.R.id.empty);
        textView.setText(getResources().getString(
                R.string.manageExchangeRatesViewBlankListNotification));

        registerForContextMenu(getListView());

        ActionBarSupport.addBackButton(this);
    }

    private void initListView(ListView listView2) {
        listAdapter = new ExchangeRateRowListAdapter(this,
                android.R.layout.simple_list_item_1, exchangeRateList,
                DisplayMode.SINGLE);

        listView2.setAdapter(listAdapter);
        listView2.setChoiceMode(ListView.CHOICE_MODE_NONE);

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
        getListView().invalidateViews();
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
                        new OptionContraintsAbs()
                                .activity(getSupportMenuInflater()).menu(menu)
                                .options(new int[] {
                                        R.id.option_help, R.id.option_import,
                                        R.id.option_delete,
                                        R.id.option_create_exchange_rate }));
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.option_delete).setVisible(!exchangeRateList.isEmpty());
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
            // TODO(ckoelle) JunkFuck super shit.
//            getApp().getViewController().openHelp(getSupportFragmentManager());
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

    /* ============== Options Shit [END] ============== */
    /* ============== Dialog Shit [END] ============== */

    @Override
    protected Dialog onCreateDialog(int id, Bundle args) {
        Dialog dialog;
        switch (id) {
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
        case Rd.DIALOG_DELETE:
            final Dialog dialogFinal = dialog;
            final ExchangeRate row = makeUninverted(getRowFromBundle(args));
            StringBuilder builder = new StringBuilder()
                    .append(getResources().getString(
                            R.string.manageExchangeRatesViewDeleteConfirmation))
                    .append("\n").append(getStringOfExchangeRate(row));
            ((TextView) dialog.findViewById(android.R.id.message))
                    .setText(builder.toString());
            ((Button) dialog.findViewById(android.R.id.button1))
                    .setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            dialogFinal.dismiss();
                            ManageExchangeRatesActivity.this
                                    .deleteRowAndUpdateList(row);
                        }
                    });
            break;
        default:
            dialog = null;
        }
        super.onPrepareDialog(id, dialog, args);
    }

    /* ============== Dialog Shit [END] ============== */
    /* ========= Context menu [BGN] =========== */
    private static final int CTX_MENU_GROUP_ID_EDIT = 1;
    private static int CTX_MENU_GROUP_ID_DELETE = 2;
    private static final int CTX_MENU_ID_EDIT = 20;
    private static final int CTX_MENU_ID_DELETE = 21;

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
            showDialog(Rd.DIALOG_DELETE, wrapRowInBundle(row));
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
