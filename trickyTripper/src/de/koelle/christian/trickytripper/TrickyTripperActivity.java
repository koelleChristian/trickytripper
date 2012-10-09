package de.koelle.christian.trickytripper;

import java.util.Locale;
import java.util.Map.Entry;

import android.app.Dialog;
import android.app.LocalActivityManager;
import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;
import de.koelle.christian.common.changelog.ChangeLog;
import de.koelle.christian.common.utils.CurrencyUtil;
import de.koelle.christian.trickytripper.activities.ParticipantTabActivity;
import de.koelle.christian.trickytripper.activities.PaymentTabActivity;
import de.koelle.christian.trickytripper.activities.ReportTabActivity;
import de.koelle.christian.trickytripper.activitysupport.PopupCallBackAdapter;
import de.koelle.christian.trickytripper.activitysupport.PopupCallback;
import de.koelle.christian.trickytripper.activitysupport.PopupFactory;
import de.koelle.christian.trickytripper.activitysupport.TabDialogSupport;
import de.koelle.christian.trickytripper.constants.Rt;
import de.koelle.christian.trickytripper.constants.TrickyTripperTabConstants;
import de.koelle.christian.trickytripper.model.Amount;
import de.koelle.christian.trickytripper.model.Participant;
import de.koelle.christian.trickytripper.model.Payment;
import de.koelle.christian.trickytripper.model.Trip;
import de.koelle.christian.trickytripper.modelutils.AmountViewUtils;

public class TrickyTripperActivity extends TabActivity {

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

        Resources res = getResources();
        TabHost tabHost = getTabHost();

        createTabs(res, tabHost);

        tabHost.setCurrentTab(0);

        updateButtonText();

        ChangeLog changeLog = new ChangeLog(this);
        if (changeLog.firstRun()) {
            changeLog.getLogDialog().show();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.layout.general_options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.general_options_help:
            showDialog(TrickyTripperTabConstants.DIALOG_SHOW_HELP);
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected Dialog onCreateDialog(int id, Bundle args) {
        Dialog dialog;
        switch (id) {
        case TrickyTripperTabConstants.DIALOG_CREATE_PARTICIPANT:
            dialog = PopupFactory.createAndShowEditParticipantPopupCreateMode(this);
            break;
        case TrickyTripperTabConstants.DIALOG_EDIT_PARTICIPANT:
            dialog = PopupFactory.createAndShowEditParticipantPopupEditMode(this);
            break;
        case TrickyTripperTabConstants.DIALOG_SHOW_HELP:
            dialog = PopupFactory.createHelpDialog(this, getApp().getFktnController(),
                    TrickyTripperTabConstants.DIALOG_SHOW_HELP);
            break;
        case TrickyTripperTabConstants.DIALOG_DELETE_PAYMENT:
            dialog = PopupFactory.showDeleteConfirmationDialog(this);
            break;
        case TrickyTripperTabConstants.DIALOG_DELETE_TRANSFER:
            dialog = PopupFactory.showDeleteConfirmationDialog(this);
            break;
        default:
            dialog = null;
        }
        return dialog;
    }

    @Override
    protected void onPrepareDialog(int id, Dialog dialog, Bundle args) {
        switch (id) {
        case TrickyTripperTabConstants.DIALOG_CREATE_PARTICIPANT:
            PopupCallback callBack1 = createPopupCallBackParticipantUpdate();
            updateCreateOrEditParticipantDialog(dialog, args, callBack1);
            break;

        case TrickyTripperTabConstants.DIALOG_EDIT_PARTICIPANT:
            PopupCallback callBack2 = createPopupCallBackParticipantUpdate();
            updateCreateOrEditParticipantDialog(dialog, args, callBack2);
            break;

        case TrickyTripperTabConstants.DIALOG_SHOW_HELP:
            // intentionally do nothing
            break;

        case TrickyTripperTabConstants.DIALOG_DELETE_PAYMENT:
            PopupCallback callbackDeletePayment = createPopupCallPaymentDelete(TabDialogSupport
                    .getPaymentFromBundle(args));
            updateCreateOrEditPaymentDeleteDialog(dialog, args, false, callbackDeletePayment);
            break;

        case TrickyTripperTabConstants.DIALOG_DELETE_TRANSFER:
            PopupCallback callbackDeleteTransfer = createPopupCallPaymentDelete(TabDialogSupport
                    .getPaymentFromBundle(args));
            updateCreateOrEditPaymentDeleteDialog(dialog, args, true, callbackDeleteTransfer);
            break;

        default:
            dialog = null;
        }
        super.onPrepareDialog(id, dialog, args);
    }

    private PopupCallback createPopupCallBackParticipantUpdate() {
        LocalActivityManager manager = getLocalActivityManager();
        final ParticipantTabActivity participantActivity = (ParticipantTabActivity) manager
                .getActivity(Rt.PARTICIPANTS.getId());
        return new PopupCallBackAdapter() {
            @Override
            public void done() {
                participantActivity.updateRows();
            }
        };
    }

    private PopupCallback createPopupCallPaymentDelete(final Payment payment) {
        LocalActivityManager manager = getLocalActivityManager();
        final PaymentTabActivity paymentTabActivity = (PaymentTabActivity) manager
                .getActivity(Rt.PAYMENT.getId());
        return new PopupCallBackAdapter() {
            @Override
            public void done() {
                getApp().getFktnController().deletePayment(payment);
                paymentTabActivity.sortAndUpdateView();
            }
        };
    }

    private void updateCreateOrEditParticipantDialog(final Dialog dialog, Bundle args, final PopupCallback popupCallback) {
        final AutoCompleteTextView autoCompleteTextView =
                (AutoCompleteTextView) dialog.findViewById(R.id.createParticipantView_autocomplete_name);
        final Participant participant = TabDialogSupport.getParticipantFromBundle(args);
        String text = (participant != null && participant.getName() != null) ? participant.getName() : "";
        autoCompleteTextView.setText(text);

        Button buttonPositive = (Button) dialog.findViewById(R.id.createParticipantView_button_positive);
        Button buttonCancel = (Button) dialog.findViewById(R.id.createParticipantView_button_cancel);

        buttonPositive.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                String input = autoCompleteTextView.getEditableText().toString();
                input = input.trim();
                participant.setName(input);
                if (getApp().getFktnController().persistParticipant(participant)) {
                    dialog.dismiss();
                    if (popupCallback != null) {
                        popupCallback.done();
                    }
                }
                else {
                    Toast.makeText(TrickyTripperActivity.this.getApplicationContext(),
                            R.string.edit_participant_view_msg_denial,
                            Toast.LENGTH_SHORT).show();
                }

            }
        });
        buttonCancel.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                dialog.cancel();
                if (popupCallback != null) {
                    popupCallback.canceled();
                }

            }
        });
    }

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

    public void switchTab(Rt tabId) {
        getTabHost().setCurrentTab(tabId.getPosition());
    }

    private void updateButtonText() {
        Button button = (Button) findViewById(R.id.mainView_trip_button);
        Trip trip = getApp().getFktnController().getTripLoaded();
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

        Amount totalAmount = getApp().getAmountFactory().createAmount();
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
                showDialog(TrickyTripperTabConstants.DIALOG_CREATE_PARTICIPANT,
                        TabDialogSupport.createBundleWithParticipantSelected(new Participant()));
                return true;
            }
        });

    }
}