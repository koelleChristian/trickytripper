package de.koelle.christian.trickytripper.activities;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;

import android.app.Activity;
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
import de.koelle.christian.trickytripper.activitysupport.TabDialogSupport;
import de.koelle.christian.trickytripper.dialogs.DeleteDialogFragement.DeleteConfirmationCallback;
import de.koelle.christian.trickytripper.model.Amount;
import de.koelle.christian.trickytripper.model.Participant;
import de.koelle.christian.trickytripper.model.Payment;
import de.koelle.christian.trickytripper.model.PaymentCategory;
import de.koelle.christian.trickytripper.model.modelAdapter.PaymentRowListAdapter;
import de.koelle.christian.trickytripper.modelutils.AmountViewUtils;
import de.koelle.christian.trickytripper.ui.utils.PrepareOptionsSupport;

public class PaymentTabActivity extends SherlockListFragment implements DeleteConfirmationCallback {
    
    private static final String DELIMITER = ": ";

    private final List<Payment> paymentRows = new ArrayList<Payment>();
    private ArrayAdapter<Payment> adapter;
    private ListView listView;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        registerForContextMenu(listView);
        listView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Payment row = (Payment) getListView().getItemAtPosition(position);
                if (!isMoneyTransfer(row)) {
                    startEditPaymentActivity(row);
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        sortAndUpdateView();
        getSherlockActivity().invalidateOptionsMenu();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.list_view, container, false);
        TextView textView = (TextView) view.findViewById(android.R.id.empty);
        listView = (ListView) view.findViewById(android.R.id.list);

        setHasOptionsMenu(true);

        adapter = new PaymentRowListAdapter(getActivity(), R.layout.payment_tab_row_view, paymentRows, getApp()
                .getTripController()
                .getAmountFactory(), getApp().getMiscController().getDefaultStringCollator());

        setListAdapter(adapter);

        sortAndUpdateView();

        textView.setText(getResources().getString(R.string.payment_view_blank_list_notification));

        return view;
    }

    @Override
    public void onPrepareOptionsMenu(com.actionbarsherlock.view.Menu menu) {
        PrepareOptionsSupport.prepareMajorTabOptions(menu, getApp(), false);
    }

    @Override
    public void onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu, MenuInflater inflater) {
        getApp().getMiscController().getOptionSupport().populateOptionsMenu(
                new OptionContraintsAbs().activity(inflater).menu(menu)
                        .options(new int[] {
                                R.id.option_create_participant,
                                R.id.option_help,
                                R.id.option_preferences,
                                R.id.option_export
                        }));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.option_help:
            getApp().getViewController().openHelp(getFragmentManager());
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

    @Override
    public boolean onContextItemSelected(android.view.MenuItem item) {
        AdapterView.AdapterContextMenuInfo info =
                (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        final Payment row = adapter.getItem(info.position);
        switch (item.getItemId()) {
        case R.string.fktn_payment_list_edit_payment: {
            startEditPaymentActivity(row);
            return true;
        }
        case R.string.fktn_payment_list_delete_payment: {
            getApp().getViewController().openDeleteConfirmation(getFragmentManager(),
                    TabDialogSupport.createBundleWithPaymentSelected(row), this);
            return true;
        }
        case R.string.fktn_payment_list_edit_transfer: {
            startEditPaymentActivity(row);
            return true;
        }
        case R.string.fktn_payment_list_delete_transfer: {
            getApp().getViewController().openDeleteConfirmation(getFragmentManager(),
                    TabDialogSupport.createBundleWithPaymentSelected(row), this);
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

    public String getDeleteConfirmationMsg(Bundle bundle) {
        Payment payment = TabDialogSupport.getPaymentFromBundle(bundle);
        int idDeleteConfirmation = (payment.isMoneyTransfer()) ?
                R.string.payment_view_delete_confirmation_transfer :
                R.string.payment_view_delete_confirmation_payment;
        String deleteConfirmationPrefix = (payment.isMoneyTransfer()) ?
                getPrefixTextForTransferDeletion(payment) :
                getPrefixTextForPaymentDeletion(payment, getActivity());
        return deleteConfirmationPrefix + getResources().getString(idDeleteConfirmation);
    }

    public void doDelete(Bundle bundle) {
        Payment payment = TabDialogSupport.getPaymentFromBundle(bundle);
        ((TrickyTripperApp) getActivity().getApplication()).getTripController().deletePayment(payment);
        sortAndUpdateView();
    }

    private String getPrefixTextForTransferDeletion(Payment row) {
        Entry<Participant, Amount> transfererEntry = row.getParticipantToSpending().entrySet().iterator()
                .next();

        Locale locale = getResources().getConfiguration().locale;

        StringBuilder builder = new StringBuilder();
        builder.append(getResources().getString(
                row.getCategory().getResourceStringId()));
        builder.append(" (");
        builder.append(AmountViewUtils.getAmountString(locale, transfererEntry.getValue(), true, true, true));
        builder.append(")\n");
        builder.append(transfererEntry.getKey().getName());
        builder.append(" >> ");
        builder.append(row.getParticipantToPayment().entrySet().iterator().next().getKey().getName());
        builder.append(DELIMITER);
        builder.append("\n");

        return builder.toString();
    }

    private String getPrefixTextForPaymentDeletion(Payment payment, Activity activity) {
        Locale locale = getResources().getConfiguration().locale;

        Amount totalAmount = ((TrickyTripperApp) activity.getApplication()).getTripController().getAmountFactory()
                .createAmount();
        payment.getTotalAmount(totalAmount);

        StringBuilder builder = new StringBuilder();
        builder.append((payment.getDescription() != null && payment.getDescription().length() > 0) ? payment
                .getDescription()
                + " " : "");
        builder.append("(");
        builder.append(AmountViewUtils.getAmountString(locale, totalAmount, true, true, true));
        builder.append(") ");
        builder.append(DELIMITER);
        builder.append("\n");

        return builder.toString();

    }



}
