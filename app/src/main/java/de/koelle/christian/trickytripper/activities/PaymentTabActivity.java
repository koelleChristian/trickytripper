package de.koelle.christian.trickytripper.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;

import de.koelle.christian.common.options.OptionConstraintsInflater;
import de.koelle.christian.common.support.DimensionSupport;
import de.koelle.christian.trickytripper.R;
import de.koelle.christian.trickytripper.TrickyTripperActivity;
import de.koelle.christian.trickytripper.TrickyTripperApp;
import de.koelle.christian.trickytripper.activitysupport.TabDialogSupport;
import de.koelle.christian.trickytripper.dialogs.DeleteDialogFragment.DeleteConfirmationCallback;
import de.koelle.christian.trickytripper.model.Amount;
import de.koelle.christian.trickytripper.model.Participant;
import de.koelle.christian.trickytripper.model.Payment;
import de.koelle.christian.trickytripper.model.PaymentCategory;
import de.koelle.christian.trickytripper.model.modelAdapter.PaymentRowListAdapter;
import de.koelle.christian.trickytripper.model.utils.PaymentComparator;
import de.koelle.christian.trickytripper.modelutils.AmountViewUtils;

public class PaymentTabActivity extends ListFragment implements DeleteConfirmationCallback {

    private static final String DELIMITER = ": ";

    private final List<Payment> paymentRows = new ArrayList<>();
    private ArrayAdapter<Payment> adapter;
    private ListView listView;
    private final Comparator<Payment> comparator = new PaymentComparator();

    private MyActionModeCallback mActionModeCallback = new MyActionModeCallback();

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (getRunningActionMode() != null) {
                    return;
                }
                Payment row = (Payment) getListView().getItemAtPosition(position);
                if (isNotMoneyTransfer(row)) {
                    startEditPaymentActivity(row);
                }
            }
        });
        listView.setLongClickable(true);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (getRunningActionMode() != null) {
                    return false;
                }

                // Start the CAB using the ActionMode.Callback defined above
                Payment selection = adapter.getItem(position);
                mActionModeCallback.setSelectedPayment(selection);
                AppCompatActivity activity = ((AppCompatActivity) PaymentTabActivity.this.getActivity());

                ActionMode actionMode = activity.startSupportActionMode(mActionModeCallback);
                actionMode.setTitle(selection.getDescription());
                setRunningActionMode(actionMode);
                view.setSelected(true);
                return true;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        sortAndUpdateView();
        getActivity().supportInvalidateOptionsMenu();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.list_view, container, false);
        TextView textView = (TextView) view.findViewById(android.R.id.empty);
        listView = (ListView) view.findViewById(android.R.id.list);

        adapter = new PaymentRowListAdapter(getActivity(), R.layout.payment_tab_row_view, paymentRows, getApp()
                .getTripController()
                .getAmountFactory(), getApp().getMiscController().getDefaultStringCollator());

        setListAdapter(adapter);

        sortAndUpdateView();

        textView.setText(getResources().getString(R.string.payment_view_blank_list_notification));
        DimensionSupport dimensionSupport = getApp().getMiscController().getDimensionSupport();
        int px16 = dimensionSupport.dp2Px(16);
        int px08 = dimensionSupport.dp2Px(8);
        textView.setPadding(px16, px08, px08, px16);

        return view;
    }

    public void sortAndUpdateView() {
        paymentRows.clear();
        paymentRows.addAll(getApp().getTripController().getTripLoaded().getPayments());
        adapter.sort(comparator);
        adapter.notifyDataSetChanged();
    }

    private TrickyTripperApp getApp() {
        return ((TrickyTripperApp) getActivity().getApplication());
    }

    private boolean isNotMoneyTransfer(Payment row) {
        return !PaymentCategory.MONEY_TRANSFER.equals(row.getCategory());
    }

    private void startEditPaymentActivity(final Payment row) {
        getApp().getViewController().openEditPayment(row);
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
        getActivity().supportInvalidateOptionsMenu();
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

    private class MyActionModeCallback implements ActionMode.Callback {

        private Payment payment;

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            int[] optionIds;
            if (isNotMoneyTransfer(payment)) {
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
                    getApp().getViewController().openDeleteConfirmationOnFragment(getFragmentManager(),
                            TabDialogSupport.createBundleWithPaymentSelected(payment), PaymentTabActivity.this);
                    mode.finish();
                    return true;
                case R.id.option_edit:
                    startEditPaymentActivity(payment);
                    mode.finish();
                    return true;
                default:
                    return false;
            }
        }


        @Override
        public void onDestroyActionMode(ActionMode mode) {
            setRunningActionMode(null);
        }

        public void setSelectedPayment(Payment payment) {
            this.payment = payment;
        }
    }

    public ActionMode getRunningActionMode() {
        return ((TrickyTripperActivity) getActivity()).getRunningActionMode();
    }

    public void setRunningActionMode(ActionMode actionMode) {
        ((TrickyTripperActivity) getActivity()).setRunningActionMode(actionMode);
    }


}
