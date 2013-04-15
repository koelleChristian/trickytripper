package de.koelle.christian.trickytripper.activities;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import de.koelle.christian.common.options.OptionContraintsAbs;
import de.koelle.christian.trickytripper.R;
import de.koelle.christian.trickytripper.TrickyTripperApp;
import de.koelle.christian.trickytripper.constants.Rd;
import de.koelle.christian.trickytripper.model.Payment;
import de.koelle.christian.trickytripper.model.PaymentCategory;
import de.koelle.christian.trickytripper.model.modelAdapter.PaymentRowListAdapter;

public class PaymentTabActivity extends SherlockListFragment {

    private final List<Payment> paymentRows = new ArrayList<Payment>();
    private ArrayAdapter<Payment> adapter;

    // @Override
    // protected void onResume() {
    // super.onResume();
    // sortAndUpdateView();
    // }

    @Override
    public void onPrepareOptionsMenu(com.actionbarsherlock.view.Menu menu) {
        menu.findItem(R.id.option_export).setEnabled(getApp().getTripController().hasLoadedTripPayments());
    }

    @Override
    public void onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu, MenuInflater inflater) {
        getApp().getMiscController().getOptionSupport().populateOptionsMenu(
                new OptionContraintsAbs().activity(inflater).menu(menu)
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
            getActivity().showDialog(Rd.DIALOG_HELP);
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.list_view, container, false);
        TextView textView = (TextView) view.findViewById(android.R.id.empty);
        ListView listView = (ListView) view.findViewById(android.R.id.list);

        setHasOptionsMenu(true);

        adapter = new PaymentRowListAdapter(getActivity(), R.layout.payment_tab_row_view, paymentRows, getApp()
                .getTripController()
                .getAmountFactory(), getApp().getMiscController().getDefaultStringCollator());

        setListAdapter(adapter);

        listView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Payment row = (Payment) getListView().getItemAtPosition(position);
                if (!isMoneyTransfer(row)) {
                    startEditPaymentActivity(row);
                }
            }
        });

        registerForContextMenu(listView);
        sortAndUpdateView();

        textView.setText(getResources().getString(R.string.payment_view_blank_list_notification));

        return view;
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
        return ((TrickyTripperApp) getActivity().getApplication());
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;

        Payment row = adapter.getItem(info.position);
        String heading;

        if (isMoneyTransfer(row)) {
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

    private boolean isMoneyTransfer(Payment row) {
        return PaymentCategory.MONEY_TRANSFER.equals(row.getCategory());
    }

    // @Override
    // public boolean onContextItemSelected(MenuItem item) {
    // AdapterView.AdapterContextMenuInfo info =
    // (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
    // final Payment row = adapter.getItem(info.position);
    // switch (item.getItemId()) {
    // case R.string.fktn_payment_list_edit_payment: {
    // startEditPaymentActivity(row);
    // return true;
    // }
    // case R.string.fktn_payment_list_delete_payment: {
    // getParent().showDialog(Rd.DIALOG_DELETE_PAYMENT,
    // TabDialogSupport.createBundleWithPaymentSelected(row));
    // return true;
    // }
    // case R.string.fktn_payment_list_edit_transfer: {
    // startEditPaymentActivity(row);
    // return true;
    // }
    // case R.string.fktn_payment_list_delete_transfer: {
    // getParent().showDialog(Rd.DIALOG_DELETE_TRANSFER,
    // TabDialogSupport.createBundleWithPaymentSelected(row));
    // return true;
    // }
    // default:
    // break;
    // }
    // return false;
    // }

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
