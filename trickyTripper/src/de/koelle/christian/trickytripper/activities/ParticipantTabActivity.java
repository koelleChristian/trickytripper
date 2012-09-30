package de.koelle.christian.trickytripper.activities;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import de.koelle.christian.trickytripper.R;
import de.koelle.christian.trickytripper.TrickyTripperActivity;
import de.koelle.christian.trickytripper.TrickyTripperApp;
import de.koelle.christian.trickytripper.activitysupport.TabDialogSupport;
import de.koelle.christian.trickytripper.constants.Rt;
import de.koelle.christian.trickytripper.constants.TrickyTripperTabConstants;
import de.koelle.christian.trickytripper.controller.TripExpensesFktnController;
import de.koelle.christian.trickytripper.model.Participant;
import de.koelle.christian.trickytripper.model.modelAdapter.ParticipantRowListAdapter;
import de.koelle.christian.trickytripper.strategies.SumReport;
import de.koelle.christian.trickytripper.ui.model.ParticipantRow;

public class ParticipantTabActivity extends ListActivity {

    private static final int MENU_GROUP_P_STD = 1;
    private static final int MENU_GROUP_P_ACTIVE_REQ = 2;
    private static final int MENU_GROUP_P_DELETE_ABLE_REQ = 3;
    private static final int MENU_GROUP_P_AT_LEAST_ONE = 4;

    final List<ParticipantRow> participantRows = new ArrayList<ParticipantRow>();

    private ParticipantRowListAdapter adapter;

    @Override
    protected void onResume() {
        super.onResume();
        updateRows();
        showToastIfNoParticipants();
    }

    private void showToastIfNoParticipants() {
        if (participantRows.isEmpty()) {
            Toast.makeText(getApplicationContext(),
                    getResources().getString(R.string.participant_tab_msg_no_participants_in_trip), Toast.LENGTH_LONG)
                    .show();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new ParticipantRowListAdapter(this, R.layout.participant_tab_row_view, participantRows);
        setListAdapter(adapter);
        ListView lv = getListView();
        registerForContextMenu(lv);
        updateRows();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.general_options_export).setEnabled(getApp().getFktnController().hasLoadedTripPayments());
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.layout.participant_tab_options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.traveller_options_new:
            getParent().showDialog(TrickyTripperTabConstants.DIALOG_CREATE_PARTICIPANT,
                    TabDialogSupport.createBundleWithParticipantSelected(new Participant()));
            return true;
        case R.id.general_options_help:
            getParent().showDialog(TrickyTripperTabConstants.DIALOG_SHOW_HELP);
            return true;
        case R.id.general_options_export:
            getApp().getViewController().openExport();
            return true;
        case R.id.general_options_preferences:
            getApp().getViewController().openSettings();
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    public void updateRows() {
        participantRows.clear();
        refillListFromModel(participantRows, getApp().getFktnController());
        final Collator collator = getApp().getFktnController().getDefaultStringCollator();
        adapter.sort(new Comparator<ParticipantRow>() {
            public int compare(ParticipantRow lhs, ParticipantRow rhs) {
                return collator.compare(lhs.getParticipant().getName(), rhs.getParticipant().getName());
            }
        });
        adapter.notifyDataSetChanged();

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        Participant p = getParticipantByInfo(info);
        menu.setHeaderTitle(p.getName());

        menu.add(MENU_GROUP_P_ACTIVE_REQ, R.string.fktn_participant_create_payment, Menu.NONE,
                getResources().getString(R.string.fktn_participant_create_payment));

        menu.add(MENU_GROUP_P_AT_LEAST_ONE, R.string.fktn_participant_transfer_money, Menu.NONE,
                getResources().getString(R.string.fktn_participant_transfer_money));

        menu.add(MENU_GROUP_P_STD, R.string.fktn_participant_show_report, Menu.NONE,
                getResources().getString(R.string.fktn_participant_show_report));

        if (p.isActive()) {
            menu.add(MENU_GROUP_P_STD, R.string.fktn_participant_deactivate, Menu.NONE,
                    getResources().getString(R.string.fktn_participant_deactivate));
        }
        else {
            menu.add(MENU_GROUP_P_STD, R.string.fktn_participant_activate, Menu.NONE,
                    getResources().getString(R.string.fktn_participant_activate));
        }

        menu.add(MENU_GROUP_P_DELETE_ABLE_REQ, R.string.fktn_participant_delete,
                Menu.NONE,
                getResources().getString(R.string.fktn_participant_delete));

        menu.add(MENU_GROUP_P_STD, R.string.fktn_participant_edit, Menu.NONE, getResources()
                .getString(R.string.fktn_participant_edit));

        menu.setGroupEnabled(MENU_GROUP_P_ACTIVE_REQ, p.isActive());
        menu.setGroupEnabled(MENU_GROUP_P_DELETE_ABLE_REQ, getApp().getFktnController().isParticipantDeleteable(p));
        menu.setGroupEnabled(MENU_GROUP_P_AT_LEAST_ONE,
                getApp().getFktnController().getAllParticipants(false).size() > 1);

    }

    private TrickyTripperApp getApp() {
        return (TrickyTripperApp) getApplication();
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        TrickyTripperApp app = getApp();
        Participant participant = getParticipantByInfo(info);
        switch (item.getItemId()) {
        case R.string.fktn_participant_create_payment: {
            app.getViewController().openCreatePayment(participant);
            return true;
        }
        case R.string.fktn_participant_transfer_money: {
            app.getViewController().openTransferMoney(participant);
            return true;
        }
        case R.string.fktn_participant_show_report: {
            app.getDialogState().setParticipantReporting(participant);
            switchTabInActivity(Rt.REPORT);
            return true;
        }
        case R.string.fktn_participant_deactivate: {
            boolean isActive = false;
            setActiveAndPersist(participant, isActive);
            adapter.notifyDataSetChanged();
            return true;
        }
        case R.string.fktn_participant_activate: {
            boolean isActive = true;
            setActiveAndPersist(participant, isActive);
            adapter.notifyDataSetChanged();
            return true;
        }
        case R.string.fktn_participant_delete: {
            if (!app.getFktnController().deleteParticipant(participant)) {
                Toast.makeText(getApplicationContext(),
                        getResources().getString(R.string.msg_delete_not_possible_inbalance),
                        Toast.LENGTH_SHORT)
                        .show();
            }
            else {
                updateRows();
            }
            return true;
        }
        case R.string.fktn_participant_edit: {
            getParent().showDialog(TrickyTripperTabConstants.DIALOG_EDIT_PARTICIPANT,
                    TabDialogSupport.createBundleWithParticipantSelected(participant));

            return true;
        }
        default:
            break;
        }
        return false;
    }

    private void setActiveAndPersist(Participant participant, boolean isActive) {
        participant.setActive(isActive);
        getApp().getFktnController().persistParticipant(participant);
    }

    private Participant getParticipantByInfo(AdapterView.AdapterContextMenuInfo info) {
        Participant participant = adapter.getItem(info.position).getParticipant();
        return participant;
    }

    public void switchTabInActivity(Rt tabId) {
        TrickyTripperActivity parent = (TrickyTripperActivity) this.getParent();
        parent.switchTab(tabId);
    }

    private void refillListFromModel(List<ParticipantRow> participantRows, TripExpensesFktnController fktnController) {
        List<Participant> allParticipants = fktnController.getAllParticipants(false);
        SumReport sumReport = fktnController.getSumReport();

        for (Participant participant : allParticipants) {
            ParticipantRow row = new ParticipantRow();
            row.setSumPaid(sumReport.getPaymentByUser().get(participant));
            row.setSumSpent(sumReport.getSpendingByUser().get(participant));
            row.setBalance(sumReport.getBalanceByUser().get(participant));
            row.setParticipant(participant);
            row.setAmountOfPaymentLines(sumReport.getPaymentByUserCount().get(participant));

            participantRows.add(row);
        }
    }
}
