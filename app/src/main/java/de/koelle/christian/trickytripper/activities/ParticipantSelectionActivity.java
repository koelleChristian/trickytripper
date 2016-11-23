package de.koelle.christian.trickytripper.activities;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.koelle.christian.common.abs.ActionBarSupport;
import de.koelle.christian.common.options.OptionConstraintsInflater;
import de.koelle.christian.trickytripper.R;
import de.koelle.christian.trickytripper.TrickyTripperApp;
import de.koelle.christian.trickytripper.constants.Rc;
import de.koelle.christian.trickytripper.model.Amount;
import de.koelle.christian.trickytripper.model.Participant;
import de.koelle.christian.trickytripper.modelutils.AmountViewUtils;
import de.koelle.christian.trickytripper.ui.model.RowObject;
import de.koelle.christian.trickytripper.ui.model.RowObjectCallback;


public class ParticipantSelectionActivity extends AppCompatActivity {

    private ArrayList<Participant> participantsInUse;
    private List<Participant> allRelevantParticipants;
    private Amount currentTotalAmount;
    private boolean isPayerNotChargedSelection;

    private boolean divideAmountResult;
    private ArrayAdapter<RowObject<Participant>> adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.participant_selection_view);

        Bundle extras = getIntent().getExtras();
        participantsInUse = new ArrayList<>((List<Participant>) extras.getSerializable(
                Rc.ACTIVITY_PARAM_PARTICIPANT_SEL_IN_PARTICIPANTS_IN_USE));
        currentTotalAmount = (Amount) extras.getSerializable(Rc.ACTIVITY_PARAM_PARTICIPANT_SEL_IN_TOTAL_PAYMENT_AMOUNT);
        isPayerNotChargedSelection = extras.getBoolean(Rc.ACTIVITY_PARAM_PARTICIPANT_SEL_IN_IS_PAYMENT);
        allRelevantParticipants = (List<Participant>) extras.getSerializable(
                Rc.ACTIVITY_PARAM_PARTICIPANT_SEL_IN_ALL_RELEVANT_PARTICIPANTS);
        if (allRelevantParticipants == null || allRelevantParticipants.isEmpty()) {
            allRelevantParticipants = getApp().getTripController().getAllParticipants(true);
        }



        initView();
        ActionBarSupport.addBackButton(this);

    }

    private void initView() {
        final ListView listView = (ListView) findViewById(R.id.participantSelectionViewParticipantList);
        TextView textViewInstruction = (TextView) findViewById(R.id.participantSelectionViewLabelInstructions);
        CheckBox checkBox = (CheckBox) findViewById(R.id.participantSelectionViewCheckBox);


        Resources res = getResources();

        int idParticipantSelectorMessage = (isPayerNotChargedSelection) ?
                R.string.participant_selection_payer_selection_msg :
                R.string.participant_selection_traveler_to_debit_msg;
        textViewInstruction.setText(res.getString(idParticipantSelectorMessage));


        RowObjectCallback<Participant> callback = new RowObjectCallback<Participant>() {
            public String getStringToDisplay(Participant t) {
                return t.getName()
                        + ((t.isActive()) ? "" : " " + getResources().getString(R.string.common_label_inactive_addon));
            }
        };

        List<RowObject<Participant>> participantsWrapped = new ArrayList<RowObject<Participant>>();
        for (Participant p : allRelevantParticipants) {
            participantsWrapped.add(new RowObject<Participant>(callback, p));
        }

        adapter = new ArrayAdapter<RowObject<Participant>>(
                ParticipantSelectionActivity.this,
                R.layout.general_checked_text_view,
                participantsWrapped);

        listView.setAdapter(adapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listView.setId(R.id.payment_edit_selection_dialog_list_view);


        if (isAmountBiggerZero(currentTotalAmount)) {
            checkBox.setVisibility(View.VISIBLE);
            checkBox.setText(getDivisionCheckboxOnParticipantSelectionText(currentTotalAmount));
            checkBox.setChecked(isPayerNotChargedSelection);
            divideAmountResult = isPayerNotChargedSelection;
        } else {
            checkBox.setVisibility(View.GONE);
        }

        /*Selects the ones in use from the relevant list*/
        SparseBooleanArray selection = listView.getCheckedItemPositions();

        for (int i = 0; i < allRelevantParticipants.size(); i++) {
            boolean selected = participantsInUse.contains(allRelevantParticipants.get(i));
            selection.put(i, selected);
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                participantsInUse.clear();
                SparseBooleanArray selection = listView.getCheckedItemPositions();
                for (int i = 0; i < listView.getCount(); i++)
                    if (selection.get(i)) {
                        Participant selectedParticipant = adapter.getItem(i)
                                .getRowObject();
                        participantsInUse.add(selectedParticipant);
                    }
                supportInvalidateOptionsMenu();
            }
        });

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                divideAmountResult = isChecked;
            }
        });
    }

    private boolean isAmountBiggerZero(Amount amount) {
        return amount != null && amount.getValue() > 0;
    }

    private String getDivisionCheckboxOnParticipantSelectionText(Amount amount) {
        String checkboxText;
        checkboxText = getResources().getString(R.string.participant_selection_traveler_divide_amount)
                + " "
                + AmountViewUtils.getAmountString(getLocale(), amount, false, false, false, true, true);
        return checkboxText;
    }

    private Locale getLocale() {
        return getResources().getConfiguration().locale;
    }

    private TrickyTripperApp getApp() {
        return ((TrickyTripperApp) getApplication());
    }

    private void acceptResultAndFinish() {

        Intent resultIntent = new Intent();
        resultIntent.putExtra(Rc.ACTIVITY_PARAM_PARTICIPANT_SEL_OUT_SELECTED_PARTICIPANTS,
                participantsInUse);
        resultIntent.putExtra(Rc.ACTIVITY_PARAM_PARTICIPANT_SEL_OUT_DIVIDE_AMOUNT,
                divideAmountResult);
        resultIntent.putExtra(Rc.ACTIVITY_PARAM_PARTICIPANT_SEL_OUT_IS_PAYMENT,
                isPayerNotChargedSelection);
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        int[] optionIds = new int[]{
                R.id.option_accept,
                R.id.option_help};

        return getApp()
                .getMiscController()
                .getOptionSupport()
                .populateOptionsMenu(
                        new OptionConstraintsInflater()
                                .activity(getMenuInflater()).menu(menu)
                                .options(optionIds));
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        boolean acceptable = !participantsInUse.isEmpty();
        MenuItem item = menu.findItem(R.id.option_accept);
        item.setEnabled(acceptable);
        item.getIcon().setAlpha((acceptable) ? 255 : 64);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.option_accept:
                acceptResultAndFinish();
                return true;
            case R.id.option_help:
                getApp().getViewController().openHelp(getSupportFragmentManager());
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
