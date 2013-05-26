package de.koelle.christian.trickytripper;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Button;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockFragmentActivity;

import de.koelle.christian.common.changelog.ChangeLog;
import de.koelle.christian.common.utils.CurrencyUtil;
import de.koelle.christian.trickytripper.activities.ParticipantTabActivity;
import de.koelle.christian.trickytripper.activities.PaymentTabActivity;
import de.koelle.christian.trickytripper.activities.ReportTabActivity;
import de.koelle.christian.trickytripper.activitysupport.PopupFactory;
import de.koelle.christian.trickytripper.constants.Rd;
import de.koelle.christian.trickytripper.model.Trip;

public class TrickyTripperActivity extends SherlockFragmentActivity {

    

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

//    private PopupCallback createPopupCallPaymentDelete(final Payment payment)
//    {
//        LocalActivityManager manager = getLocalActivityManager();
//        final PaymentTabActivity paymentTabActivity = (PaymentTabActivity)
//                manager
//                        .getActivity(Rt.PAYMENT.getId());
//        return new PopupCallBackAdapter() {
//            @Override
//            public void done() {
//                getApp().getTripController().deletePayment(payment);
//                paymentTabActivity.sortAndUpdateView();
//            }
//        };
//    }



    public void openManageTrips(View view) {
        if (view.getId() == R.id.mainView_trip_button) {
            getApp().getViewController().openManageTrips();
        }
    }

    private void updateButtonText() {
        Button button = (Button) findViewById(R.id.mainView_trip_button);
        Trip trip = getApp().getTripController().getTripLoaded();
        button.setText(trip.getName() + " "
                + CurrencyUtil.getSymbolToCurrency(getResources(), trip.getBaseCurrency(), true));
    }

    private TrickyTripperApp getApp() {
        return ((TrickyTripperApp) getApplication());
    }



    class MyTabListener implements ActionBar.TabListener {
        private Fragment fragment;

        public MyTabListener(Fragment fragment) {
            this.fragment = fragment;
        }

        public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
            ft.replace(R.id.mainView_fragment_content, fragment, null);
        }

        public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
            ft.remove(fragment);
        }

        public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
            System.out.println("Reselect");
        }
    }
}