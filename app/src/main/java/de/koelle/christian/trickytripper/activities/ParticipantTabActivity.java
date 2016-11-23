package de.koelle.christian.trickytripper.activities;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import de.koelle.christian.common.support.DimensionSupport;
import de.koelle.christian.trickytripper.R;
import de.koelle.christian.trickytripper.TrickyTripperActivity;
import de.koelle.christian.trickytripper.TrickyTripperApp;
import de.koelle.christian.trickytripper.constants.Rc;
import de.koelle.christian.trickytripper.controller.TripController;
import de.koelle.christian.trickytripper.model.Participant;
import de.koelle.christian.trickytripper.model.modelAdapter.ParticipantRowListAdapter;
import de.koelle.christian.trickytripper.strategies.SumReport;
import de.koelle.christian.trickytripper.ui.model.ParticipantRow;

public class ParticipantTabActivity extends ListFragment {

    final List<ParticipantRow> participantRows = new ArrayList<ParticipantRow>();

    private ParticipantRowListAdapter adapter;
    private ListView listView;

    private MyActionModeCallback mActionModeCallback = new MyActionModeCallback();

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        listView.setLongClickable(true);

        listView.setOnItemClickListener(new ShortTabListener());
        listView.setOnItemLongClickListener(new LongTabListener());
    }

    @Override
    public void onResume() {
        super.onResume();
        updateRows();
        getActivity().supportInvalidateOptionsMenu();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.list_view, container, false);
        view.setTag(Rc.TAB_ID_PARTICIPANTS);
        TextView textView = (TextView) view.findViewById(android.R.id.empty);
        listView = (ListView) view.findViewById(android.R.id.list);

        adapter = new ParticipantRowListAdapter(getActivity(), R.layout.participant_tab_row_view, participantRows);
        setListAdapter(adapter);

        updateRows();
        textView.setText(getResources().getString(R.string.participant_tab_blank_list_notification));
        DimensionSupport dimensionSupport = getApp().getMiscController().getDimensionSupport();
        int px16 = dimensionSupport.dp2Px(16);
        int px08 = dimensionSupport.dp2Px(8);
        textView.setPadding(px16, px08, px08, px16);
        return view;
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

    private void setActiveAndPersist(Participant participant, boolean isActive) {
        participant.setActive(isActive);
        getApp().getTripController().persistParticipant(participant);
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


    private class MyActionModeCallback implements ActionMode.Callback {

        private Participant selectedParticipant;

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.options_participant, menu);

            menu.setGroupEnabled(R.id.menu_group_participant_active_required, selectedParticipant.isActive());
            menu.setGroupEnabled(R.id.menu_group_participant_deletable_required,
                    getApp().getTripController().isParticipantDeletable(selectedParticipant));
            menu.setGroupEnabled(R.id.menu_group_participant_at_least_another, getApp().getTripController().getAllParticipants(false).size() > 1);

            if (selectedParticipant.isActive()) {
                menu.removeItem(R.id.option_participant_activate_for_costs);
            } else {
                menu.removeItem(R.id.option_participant_deactivate_for_costs);
            }
            return true;
        }


        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.option_participant_create_payment:
                    getApp().getViewController().openCreatePayment(selectedParticipant);
                    mode.finish();
                    return true;
                case R.id.option_participant_create_money_transfer:
                    getApp().getViewController().openTransferMoney(selectedParticipant);
                    mode.finish();
                    return true;
                case R.id.option_participant_show_report:
                    getApp().getTripController().getDialogState().setParticipantReporting(selectedParticipant);
                    ViewPager pager = (ViewPager) getActivity().findViewById(R.id.drawer_content_pager);
                    pager.setCurrentItem(Rc.TAB_ID_REPORT);
                    mode.finish();
                    return true;
                case R.id.option_participant_deactivate_for_costs:
                    boolean isActive = false;
                    setActiveAndPersist(selectedParticipant, isActive);
                    adapter.notifyDataSetChanged();
                    mode.finish();
                    return true;
                case R.id.option_participant_activate_for_costs:
                    isActive = true;
                    setActiveAndPersist(selectedParticipant, isActive);
                    adapter.notifyDataSetChanged();
                    mode.finish();
                    return true;
                case R.id.option_participant_edit:
                    getApp().getViewController().openEditParticipant(selectedParticipant);
                    mode.finish();
                    return true;
                case R.id.option_participant_delete:
                    if (!getApp().getTripController().deleteParticipant(selectedParticipant)) {
                        Toast.makeText(getActivity(),
                                getResources().getString(R.string.msg_delete_not_possible_inbalance),
                                Toast.LENGTH_SHORT)
                                .show();
                    } else {
                        mode.finish();
                        updateRows();
                    }
                    return true;
                default:
                    return false;
            }
        }


        @Override
        public void onDestroyActionMode(ActionMode mode) {
            setRunningActionMode(null);
        }

        public void setSelectedParticipant(Participant selectedParticipant) {
            this.selectedParticipant = selectedParticipant;
        }
    }

    private class ShortTabListener implements OnItemClickListener {
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (getRunningActionMode() != null) {
                return;
            }
            TrickyTripperApp app = getApp();
            ParticipantRow row = (ParticipantRow) getListView().getItemAtPosition(position);
            if (row.getParticipant().isActive()) {
                Participant p = row.getParticipant();
                app.getViewController().openCreatePayment(p);
            }
        }
    }

    public ActionMode getRunningActionMode() {
        return ((TrickyTripperActivity) getActivity()).getRunningActionMode();
    }

    public void setRunningActionMode(ActionMode actionMode) {
        ((TrickyTripperActivity) getActivity()).setRunningActionMode(actionMode);
    }


    private class LongTabListener implements AdapterView.OnItemLongClickListener {

        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            if (getRunningActionMode() != null) {
                return false;
            }
            Participant selection = adapter.getItem(position).getParticipant();
            mActionModeCallback.setSelectedParticipant(selection);
            AppCompatActivity activity = ((AppCompatActivity) ParticipantTabActivity.this.getActivity());


            ActionMode actionMode = activity.startSupportActionMode(mActionModeCallback);
            actionMode.setTitle(selection.getName());
            setRunningActionMode(actionMode);
            view.setSelected(true);
            return true;
        }
    }

}
