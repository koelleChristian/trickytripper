package de.koelle.christian.trickytripper.activities;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.MenuInflater;

import de.koelle.christian.common.options.OptionContraintsAbs;
import de.koelle.christian.trickytripper.R;
import de.koelle.christian.trickytripper.TrickyTripperApp;
import de.koelle.christian.trickytripper.constants.Rd;
import de.koelle.christian.trickytripper.constants.Rt;
import de.koelle.christian.trickytripper.controller.TripController;
import de.koelle.christian.trickytripper.model.Participant;
import de.koelle.christian.trickytripper.model.modelAdapter.ParticipantRowListAdapter;
import de.koelle.christian.trickytripper.strategies.SumReport;
import de.koelle.christian.trickytripper.ui.model.ParticipantRow;

public class ParticipantTabActivity extends SherlockListFragment {

    private static final int MENU_GROUP_P_STD = 1;
    private static final int MENU_GROUP_P_ACTIVE_REQ = 2;
    private static final int MENU_GROUP_P_DELETE_ABLE_REQ = 3;
    private static final int MENU_GROUP_P_AT_LEAST_ONE = 4;

    final List<ParticipantRow> participantRows = new ArrayList<ParticipantRow>();

    private ParticipantRowListAdapter adapter;

    // @Override
    // protected void onResume() {
    // super.onResume();
    // updateRows();
    // }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.list_view, container, false);
        TextView textView = (TextView) view.findViewById(android.R.id.empty);
        ListView listView = (ListView) view.findViewById(android.R.id.list);

        setHasOptionsMenu(true);

        adapter = new ParticipantRowListAdapter(getActivity(), R.layout.participant_tab_row_view, participantRows);
        setListAdapter(adapter);

        registerForContextMenu(view);
        listView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TrickyTripperApp app = getApp();
                ParticipantRow row = (ParticipantRow) getListView().getItemAtPosition(position);
                if (row.getParticipant().isActive()) {
                    Participant p = row.getParticipant();
                    app.getViewController().openCreatePayment(p);
                }
            }
        });
        updateRows();
        textView.setText(getResources().getString(R.string.participant_tab_blank_list_notification));
        return view;
    }

    @Override
    public void onPrepareOptionsMenu(com.actionbarsherlock.view.Menu menu) {
        menu.findItem(R.id.option_export).setEnabled(getApp().getTripController().hasLoadedTripPayments());
    }

    @Override
    public void onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu, MenuInflater inflater) {
        getApp().getMiscController().getOptionSupport().populateOptionsMenu(
                new OptionContraintsAbs().activity(inflater).menu(menu)
                        .options(new int[] {
                                R.id.option_create_participant,
                                R.id.option_help,
                                R.id.option_export,
                                R.id.option_preferences
                        }));
    }

    @Override
    public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem item) {
        switch (item.getItemId()) {
        case R.id.option_create_participant:
            getApp().getViewController().openCreateParticipant();
            return true;
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

    public void updateRows() {
        participantRows.clear();
        refillListFromModel(participantRows, getApp().getTripController());
        final Collator collator = getApp().getMiscController().getDefaultStringCollator();
        adapter.sort(new Comparator<ParticipantRow>() {
            public int compare(ParticipantRow lhs, ParticipantRow rhs) {
                return collator.compare(lhs.getParticipant().getName(), rhs.getParticipant().getName());
            }
        });
        adapter.notifyDataSetChanged();

    }

    private TrickyTripperApp getApp() {
        return (TrickyTripperApp) getActivity().getApplication();
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
        menu.add(MENU_GROUP_P_STD, R.string.common_button_edit, Menu.NONE, getResources()
                .getString(R.string.common_button_edit));

        menu.add(MENU_GROUP_P_DELETE_ABLE_REQ, R.string.common_button_delete,
                Menu.NONE,
                getResources().getString(R.string.common_button_delete));

        menu.setGroupEnabled(MENU_GROUP_P_ACTIVE_REQ, p.isActive());
        menu.setGroupEnabled(MENU_GROUP_P_DELETE_ABLE_REQ, getApp().getTripController().isParticipantDeleteable(p));
        menu.setGroupEnabled(MENU_GROUP_P_AT_LEAST_ONE,
                getApp().getTripController().getAllParticipants(false).size() > 1);

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
            app.getTripController().getDialogState().setParticipantReporting(participant);
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
        case R.string.common_button_delete: {
            if (!app.getTripController().deleteParticipant(participant)) {
                Toast.makeText(getActivity(),
                        getResources().getString(R.string.msg_delete_not_possible_inbalance),
                        Toast.LENGTH_SHORT)
                        .show();
            }
            else {
                updateRows();
            }
            return true;
        }
        case R.string.common_button_edit: {
            getApp().getViewController().openEditParticipant(participant);
            return true;
        }
        default:
            break;
        }
        return false;
    }

    private void setActiveAndPersist(Participant participant, boolean isActive) {
        participant.setActive(isActive);
        getApp().getTripController().persistParticipant(participant);
    }

    private Participant getParticipantByInfo(AdapterView.AdapterContextMenuInfo info) {
        Participant participant = adapter.getItem(info.position).getParticipant();
        return participant;
    }

    public void switchTabInActivity(Rt tabId) {
        // TrickyTripperActivity parent = (TrickyTripperActivity)
        // this.getParent();
        // parent.switchTab(tabId);
    }

    private void refillListFromModel(List<ParticipantRow> participantRows, TripController fktnController) {
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
