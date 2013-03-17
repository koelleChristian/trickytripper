package de.koelle.christian.trickytripper.activities;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import de.koelle.christian.common.options.OptionContraints;
import de.koelle.christian.trickytripper.R;
import de.koelle.christian.trickytripper.TrickyTripperApp;
import de.koelle.christian.trickytripper.activitysupport.TabDialogSupport;
import de.koelle.christian.trickytripper.constants.Rd;
import de.koelle.christian.trickytripper.model.Payment;
import de.koelle.christian.trickytripper.model.PaymentCategory;
import de.koelle.christian.trickytripper.model.modelAdapter.PaymentRowListAdapter;

public class PaymentTabActivity extends ListActivity {

    private final List<Payment> paymentRows = new ArrayList<Payment>();
    private ArrayAdapter<Payment> adapter;

    @Override
    protected void onResume() {
        super.onResume();
        sortAndUpdateView();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.option_export).setEnabled(getApp().getTripController().hasLoadedTripPayments());
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return getApp().getMiscController().getOptionSupport().populateOptionsMenu(
                new OptionContraints().activity(this).menu(menu)
                        .options(new int[] {
                                R.id.option_help,
                                R.id.option_preferences,
                                R.id.option_export
                        }));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.option_help:
            getParent().showDialog(Rd.DIALOG_HELP);
            return true;
        case R.id.option_export:
            getApp().getViewController().openExport();
            return true;
        case R.id.option_preferences:
            getApp().getViewController().openSettings();
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_view);
        adapter = new PaymentRowListAdapter(this, R.layout.payment_tab_row_view, paymentRows, getApp()
                .getTripController()
                .getAmountFactory(), getApp().getMiscController().getDefaultStringCollator());

        setListAdapter(adapter);

        ListView lv = getListView();
        registerForContextMenu(lv);
        sortAndUpdateView();

        TextView textView = (TextView) findViewById(android.R.id.empty);
        textView.setText(getResources().getString(R.string.payment_view_blank_list_notification));

    }

    public void sortAndUpdateView() {
        paymentRows.clear();
        paymentRows.addAll(getApp().getTripController().getTripLoaded().getPayments());
        adapter.sort(new Comparator<Payment>() {
            public int compare(Payment lhs, Payment rhs) {
                return lhs.getPaymentDateTime().compareTo(rhs.getPaymentDateTime()) * -1;
            }
        });
        // Alternatively getListView().invalidateViews();
        adapter.notifyDataSetChanged();
    }

    private TrickyTripperApp getApp() {
        return ((TrickyTripperApp) getApplication());
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;

        Payment row = adapter.getItem(info.position);
        String heading;

        if (PaymentCategory.MONEY_TRANSFER.equals(row.getCategory())) {
            heading = getCategoryText(row);
            menu.add(Menu.NONE, R.string.fktn_payment_list_delete_transfer, 1,
                    getResources().getString(R.string.fktn_payment_list_delete_transfer));
        }
        else {
            heading = row.getDescription();

            menu.add(Menu.NONE, R.string.fktn_payment_list_edit_payment, 1,
                    getResources().getString(R.string.fktn_payment_list_edit_payment));
            menu.add(Menu.NONE, R.string.fktn_payment_list_delete_payment, 1,
                    getResources().getString(R.string.fktn_payment_list_delete_payment));
        }
        menu.setHeaderTitle(heading);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        final Payment row = adapter.getItem(info.position);
        switch (item.getItemId()) {
        case R.string.fktn_payment_list_edit_payment: {
            startEditPaymentActivity(row);
            return true;
        }
        case R.string.fktn_payment_list_delete_payment: {
            getParent().showDialog(Rd.DIALOG_DELETE_PAYMENT,
                    TabDialogSupport.createBundleWithPaymentSelected(row));
            return true;
        }
        case R.string.fktn_payment_list_edit_transfer: {
            startEditPaymentActivity(row);
            return true;
        }
        case R.string.fktn_payment_list_delete_transfer: {
            getParent().showDialog(Rd.DIALOG_DELETE_TRANSFER,
                    TabDialogSupport.createBundleWithPaymentSelected(row));
            return true;
        }
        default:
            break;
        }
        return false;
    }

    private void startEditPaymentActivity(final Payment row) {
        getApp().getViewController().openEditPayment(row);
    }

    protected void deletePayment(Payment row) {
        getApp().getTripController().deletePayment(row);
    }

    private String getCategoryText(Payment row) {
        return (row.getCategory() != null) ? getResources().getString(
                row.getCategory().getResourceStringId()) : "";
    }

}
