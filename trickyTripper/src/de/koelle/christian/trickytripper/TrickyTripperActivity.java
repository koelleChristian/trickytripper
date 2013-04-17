package de.koelle.christian.trickytripper;

import java.util.Locale;
import java.util.Map.Entry;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockFragmentActivity;

import de.koelle.christian.common.changelog.ChangeLog;
import de.koelle.christian.common.utils.CurrencyUtil;
import de.koelle.christian.trickytripper.activities.ParticipantTabActivity;
import de.koelle.christian.trickytripper.activities.PaymentTabActivity;
import de.koelle.christian.trickytripper.activities.ReportTabActivity;
import de.koelle.christian.trickytripper.activitysupport.PopupCallback;
import de.koelle.christian.trickytripper.activitysupport.PopupFactory;
import de.koelle.christian.trickytripper.activitysupport.TabDialogSupport;
import de.koelle.christian.trickytripper.constants.Rd;
import de.koelle.christian.trickytripper.constants.Rt;
import de.koelle.christian.trickytripper.model.Amount;
import de.koelle.christian.trickytripper.model.Participant;
import de.koelle.christian.trickytripper.model.Payment;
import de.koelle.christian.trickytripper.model.Trip;
import de.koelle.christian.trickytripper.modelutils.AmountViewUtils;

public class TrickyTripperActivity extends SherlockFragmentActivity {

    private static final String DELIMITER = ": ";

    @Override
    protected void onResume() {
        super.onResume();
        updateButtonText();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tricky_tripper_main_view);

        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setDisplayShowTitleEnabled(false);               
        
        actionBar.selectTab(addTab(actionBar, new ParticipantTabActivity(), R.string.activity_label_participants));
        addTab(actionBar, new PaymentTabActivity(), R.string.activity_label_payments);
        addTab(actionBar, new ReportTabActivity(), R.string.activity_label_report);

        updateButtonText();

        ChangeLog changeLog = new ChangeLog(this);
        if (changeLog.firstRun()) {
            changeLog.getLogDialog().show();
        }

    }

    private Tab addTab(final ActionBar actionBar, Fragment fragment1, int label) {
        Tab tab = actionBar.newTab().setText(label).setTabListener(new MyTabListener(fragment1));
        actionBar.addTab(tab);
        return tab;
    }

    @Override
    protected Dialog onCreateDialog(int id, Bundle args) {
        Dialog dialog;
        switch (id) {
        case Rd.DIALOG_DELETE_PAYMENT:
            dialog = PopupFactory.showDeleteConfirmationDialog(this);
            break;
        case Rd.DIALOG_DELETE_TRANSFER:
            dialog = PopupFactory.showDeleteConfirmationDialog(this);
            break;
        default:
            dialog = null;
        }
        return dialog;
    }

    @Override
    protected void onPrepareDialog(int id, Dialog dialog, Bundle args) {
        // switch (id) {
        //
        // case Rd.DIALOG_HELP:
        // // intentionally do nothing
        // break;
        //
        // case Rd.DIALOG_DELETE_PAYMENT:
        // PopupCallback callbackDeletePayment =
        // createPopupCallPaymentDelete(TabDialogSupport
        // .getPaymentFromBundle(args));
        // updateCreateOrEditPaymentDeleteDialog(dialog, args, false,
        // callbackDeletePayment);
        // break;
        //
        // case Rd.DIALOG_DELETE_TRANSFER:
        // PopupCallback callbackDeleteTransfer =
        // createPopupCallPaymentDelete(TabDialogSupport
        // .getPaymentFromBundle(args));
        // updateCreateOrEditPaymentDeleteDialog(dialog, args, true,
        // callbackDeleteTransfer);
        // break;
        //
        // default:
        // dialog = null;
        // }
        super.onPrepareDialog(id, dialog, args);
    }

    // private PopupCallback createPopupCallBackParticipantUpdate() {
    // LocalActivityManager manager = getLocalActivityManager();
    // final ParticipantTabActivity participantActivity =
    // (ParticipantTabActivity) manager
    // .getActivity(Rt.PARTICIPANTS.getId());
    // return new PopupCallBackAdapter() {
    // @Override
    // public void done() {
    // participantActivity.updateRows();
    // }
    // };
    // }

    // private PopupCallback createPopupCallPaymentDelete(final Payment payment)
    // {
    // LocalActivityManager manager = getLocalActivityManager();
    // final PaymentTabActivity paymentTabActivity = (PaymentTabActivity)
    // manager
    // .getActivity(Rt.PAYMENT.getId());
    // return new PopupCallBackAdapter() {
    // @Override
    // public void done() {
    // getApp().getTripController().deletePayment(payment);
    // paymentTabActivity.sortAndUpdateView();
    // }
    // };
    // }

    private void updateCreateOrEditPaymentDeleteDialog(final Dialog dialog, Bundle args, boolean isTransfer,
            final PopupCallback callback) {
        Payment payment = TabDialogSupport.getPaymentFromBundle(args);
        int idDeleteConfirmation = (isTransfer) ?
                R.string.payment_view_delete_confirmation_transfer :
                R.string.payment_view_delete_confirmation_payment;
        String deleteConfirmationPrefix = (isTransfer) ?
                getPrefixTextForTransferDeletion(payment) :
                getPrefixTextForPaymentDeletion(payment);
        String msg = deleteConfirmationPrefix + getResources().getString(idDeleteConfirmation);
        ((TextView) dialog.findViewById(android.R.id.message)).setText(msg);

        Button buttonOk = (Button) dialog.findViewById(android.R.id.button1);
        Button buttonCancel = (Button) dialog.findViewById(android.R.id.button3);

        buttonOk.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog.dismiss();
                callback.done();
            }
        });
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                callback.canceled();
            }
        });
    }

    public void openManageTrips(View view) {
        if (view.getId() == R.id.mainView_trip_button) {
            getApp().getViewController().openManageTrips();
        }
    }

    // public void switchTab(Rt tabId) {
    // getTabHost().setCurrentTab(tabId.getPosition());
    // }

    private void updateButtonText() {
        Button button = (Button) findViewById(R.id.mainView_trip_button);
        Trip trip = getApp().getTripController().getTripLoaded();
        button.setText(trip.getName() + " "
                + CurrencyUtil.getSymbolToCurrency(getResources(), trip.getBaseCurrency(), true));
    }

    private TrickyTripperApp getApp() {
        return ((TrickyTripperApp) getApplication());
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

    private String getPrefixTextForPaymentDeletion(Payment payment) {
        Locale locale = getResources().getConfiguration().locale;

        Amount totalAmount = getApp().getTripController().getAmountFactory().createAmount();
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

    private void createTabs(Resources res, TabHost tabHost) {
        TabHost.TabSpec spec;
        Intent intent;

        intent = new Intent().setClass(this, ParticipantTabActivity.class); //

        spec = tabHost
                .newTabSpec(Rt.PARTICIPANTS.getId())
                .setIndicator(res.getString(R.string.activity_label_participants),
                        res.getDrawable(R.drawable.ic_tab_participants)).setContent(intent);
        tabHost.addTab(spec);

        intent = new Intent().setClass(this, PaymentTabActivity.class);
        spec = tabHost
                .newTabSpec(Rt.PAYMENT.getId())
                .setIndicator(res.getString(R.string.activity_label_payments),
                        res.getDrawable(R.drawable.ic_tabl_payment)).setContent(intent);
        tabHost.addTab(spec);

        intent = new Intent().setClass(this, ReportTabActivity.class);
        spec = tabHost.newTabSpec(Rt.REPORT.getId())
                .setIndicator(res.getString(R.string.activity_label_report), res.getDrawable(R.drawable.ic_tab_report))
                .setContent(intent);
        tabHost.addTab(spec);

        TabWidget tabWidget = (TabWidget) findViewById(android.R.id.tabs);
        tabWidget.getChildAt(0).setOnLongClickListener(new OnLongClickListener() {

            public boolean onLongClick(View v) {
                getApp().getViewController().openCreateParticipant();
                return true;
            }
        });

    }

    class MyTabListener implements ActionBar.TabListener {
        private Fragment fragment;

        public MyTabListener(Fragment fragment) {
            this.fragment = fragment;
        }

        public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
            // ft.replace(R.id.mainView_fragment_content, fragment, null);
            ft.add(R.id.mainView_fragment_content, fragment, null);
        }

        public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
            ft.remove(fragment);
        }

        public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
            System.out.println("Reselect");
        }
    }
}