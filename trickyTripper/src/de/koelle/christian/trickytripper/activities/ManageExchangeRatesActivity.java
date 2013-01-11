package de.koelle.christian.trickytripper.activities;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Currency;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import de.koelle.christian.trickytripper.R;
import de.koelle.christian.trickytripper.TrickyTripperApp;
import de.koelle.christian.trickytripper.activitysupport.ImportOptionSupport;
import de.koelle.christian.trickytripper.activitysupport.PopupFactory;
import de.koelle.christian.trickytripper.constants.Rc;
import de.koelle.christian.trickytripper.model.ExchangeRate;
import de.koelle.christian.trickytripper.model.modelAdapter.ExchangeRateRowListAdapter;

public class ManageExchangeRatesActivity extends Activity {

    private ArrayAdapter<ExchangeRate> listAdapter;
    private final List<ExchangeRate> exchangeRateList = new ArrayList<ExchangeRate>();
    private ListView listView;
    private Comparator<ExchangeRate> comparator;
    private ImportOptionSupport importOptionSupport;

    /* ============== Menu Shit [BGN] ============== */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.layout.general_options_plus_import, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.general_options_help:
            showDialog(Rc.DIALOG_SHOW_HELP);
            return true;
        case R.id.option_import_exchange_rates:
            return importOptionSupport.onOptionsItemSelected();
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected Dialog onCreateDialog(int id, Bundle args) {
        Dialog dialog;
        switch (id) {
        case Rc.DIALOG_SHOW_HELP:
            dialog = PopupFactory.createHelpDialog(this, getApp(), Rc.DIALOG_SHOW_HELP);
            break;
        default:
            dialog = null;
        }

        return dialog;
    }

    @Override
    protected void onPrepareDialog(int id, Dialog dialog, Bundle args) {
        switch (id) {
        case Rc.DIALOG_SHOW_HELP:
            // intentionally blank
            break;
        default:
            dialog = null;
        }
        super.onPrepareDialog(id, dialog, args);
    }

    /* ============== Menu Shit [END] ============== */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manage_exchange_rates_view);

        TrickyTripperApp app = getApp();

        this.importOptionSupport = new ImportOptionSupport(app);

        final Collator collator = app.getDefaultStringCollator();
        comparator = new Comparator<ExchangeRate>() {
            public int compare(ExchangeRate object1, ExchangeRate object2) {
                return collator.compare(object1.getSortString(), object2.getSortString());
            }

        };
        listView = (ListView) findViewById(R.id.manageExchangeRatesViewListViewRates);

        initListView(listView, app);
        addClickListener(listView, app);

        registerForContextMenu(listView);

    }

    private void initListView(ListView listView2, TrickyTripperApp app) {
        listAdapter = new ExchangeRateRowListAdapter(this, android.R.layout.simple_list_item_1,
                exchangeRateList);

        listView2.setAdapter(listAdapter);
        listView2.setChoiceMode(ListView.CHOICE_MODE_NONE);

        updateList();
        listAdapter.sort(comparator);
    }

    void updateList() {
        List<ExchangeRate> currentList = getApp().getExchangeRateController().getAllExchangeRatesWithoutInversion();
        listAdapter.clear();
        for (ExchangeRate rate : currentList) {
            listAdapter.add(rate);
            listAdapter.add(rate.cloneToInversion());
        }
        listAdapter.sort(comparator);
        listView.invalidateViews();
    }

    /* ========= Context menu [BGN] =========== */
    private static final int CTX_MENU_GROUP_ID_EDIT = 1;
    private static int CTX_MENU_GROUP_ID_DELETE = 2;
    private static final int CTX_MENU_ID_EDIT = 20;
    private static final int CTX_MENU_ID_DELETE = 21;

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        ExchangeRate row = getParticipantByInfo(info);
        menu.setHeaderTitle(getStringOfExchangeRate(row));

        menu.add(CTX_MENU_GROUP_ID_DELETE, CTX_MENU_ID_DELETE, Menu.NONE,
                getResources().getString(R.string.common_button_delete));

        menu.add(CTX_MENU_GROUP_ID_EDIT, CTX_MENU_ID_EDIT, Menu.NONE,
                getResources().getString(R.string.common_button_edit));

        menu.setGroupEnabled(CTX_MENU_GROUP_ID_EDIT, !row.isImported());

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        TrickyTripperApp app = getApp();
        ExchangeRate row = getParticipantByInfo(info);
        switch (item.getItemId()) {
        case R.string.common_button_delete: {
            if (!app.getExchangeRateController().deleteExchangeRate(row)) {
                Toast.makeText(getApplicationContext(),
                        getResources().getString(R.string.msg_delete_not_possible_inbalance),
                        Toast.LENGTH_SHORT)
                        .show();
            }
            else {
                updateList();
            }
            return true;
        }
        case R.string.common_button_edit: {
            app.getViewController().openEditExchangeRate(row);
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
                .append(row.getCurrencyFrom().getCurrencyCode())
                .append(" > ")
                .append(row.getCurrencyTo().getCurrencyCode())
                .append(" = ")
                .append(row.getExchangeRate())
                .append("\n")
                .append(inversion.getCurrencyFrom().getCurrencyCode())
                .append(" > ")
                .append(inversion.getCurrencyTo().getCurrencyCode())
                .append(" = ")
                .append(inversion.getExchangeRate())
        /**/;
    }

    /* ========= Context menu [END] =========== */

    private ExchangeRate getParticipantByInfo(AdapterView.AdapterContextMenuInfo info) {
        ExchangeRate rate = listAdapter.getItem(info.position);
        return rate;
    }

    private void addClickListener(ListView listView2, TrickyTripperApp app) {
        // TODO Auto-generated method stub

    }

    private TrickyTripperApp getApp() {
        return ((TrickyTripperApp) getApplication());
    }

    public void importExchangeRates() {
        getApp().getViewController().openImportExchangeRates(new Currency[0]);
    }

}
