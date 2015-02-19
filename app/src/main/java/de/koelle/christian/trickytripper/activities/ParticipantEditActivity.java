package de.koelle.christian.trickytripper.activities;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import de.koelle.christian.common.abs.ActionBarSupport;
import de.koelle.christian.common.options.OptionConstraintsInflater;
import de.koelle.christian.common.text.BlankTextWatcher;
import de.koelle.christian.common.utils.StringUtils;
import de.koelle.christian.common.utils.UiUtils;
import de.koelle.christian.trickytripper.R;
import de.koelle.christian.trickytripper.TrickyTripperApp;
import de.koelle.christian.trickytripper.constants.Rc;
import de.koelle.christian.trickytripper.constants.ViewMode;
import de.koelle.christian.trickytripper.dataaccess.PhoneContactResolver;
import de.koelle.christian.trickytripper.model.Participant;
import de.koelle.christian.trickytripper.model.PhoneContact;

public class ParticipantEditActivity extends ActionBarActivity {

    private ViewMode viewMode;
    private Participant participant;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_participant_view);

        readAndSetInput(getIntent());
        initWidgets();
        updateWidgets();

        ActionBarSupport.addBackButton(this);
    }

    private void readAndSetInput(Intent intent) {
        viewMode = (ViewMode) getIntent().getExtras().get(Rc.ACTIVITY_PARAM_KEY_VIEW_MODE);
        if (ViewMode.EDIT == viewMode) {
            participant = (Participant) intent.getSerializableExtra(Rc.ACTIVITY_PARAM_PARTICIPANT_EDIT_IN_PARTICIPANT);
        }
        else {
            participant = new Participant();
        }
    }

    private void updateWidgets() {
        String text = (participant != null && participant.getName() != null) ? participant.getName() : "";
        getAutoCompleteTextView().setText(text);
    }

    private void initWidgets() {
        int titleId;

        final AutoCompleteTextView autoCompleteTextView = getAutoCompleteTextView();

        if (ViewMode.EDIT == viewMode) {
            titleId = R.string.edit_participant_view_heading_edit;
        } else {
            titleId = R.string.edit_participant_view_heading_create;
        }

        setTitle(titleId);

        autoCompleteTextView.addTextChangedListener(new BlankTextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before,
                    int count) {
                if (s.length() == 2) {
                    NameLookupTask task = new NameLookupTask(ParticipantEditActivity.this, autoCompleteTextView);
                    task.execute(s.toString());
                } else if(s.length() == 0 || s.length() == 1){
                    supportInvalidateOptionsMenu();
                }
            }
        });
    }

    public void createAndCreateAnother() {
        if (save()) {
            updateAlreadyCreatedList();
            participant = new Participant();
            updateWidgets();
        }
    }

    private void updateAlreadyCreatedList() {
        if (adapter == null) {
            ListView listView = (ListView) findViewById(R.id.editParticipantViewListViewAlreadyCreated);
            adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, new ArrayList<String>());
            listView.setAdapter(adapter);
            UiUtils.setViewVisibility(listView, true);
            UiUtils.setViewVisibility(findViewById(R.id.editParticipantView_label_already_created), true);
        }
        adapter.insert(participant.getName(), 0);
    }

    public void saveAndClose() {
        if (save()) {
            /*To update the report.*/
            getApp().getTripController().reloadTrip();
            finish();
        }
    }

    private boolean save() {
        String input = getAutoCompleteTextView().getEditableText().toString();
        input = input.trim();
        participant.setName(input);
        boolean participantPersisted = getApp().getTripController().persistParticipant(participant);
        if (!participantPersisted) {
            Toast.makeText(
                    this.getApplicationContext(),
                    R.string.edit_participant_view_msg_denial,
                    Toast.LENGTH_SHORT)
                    .show();
        }

        return participantPersisted;
    }

    private TrickyTripperApp getApp() {
        return (TrickyTripperApp) getApplication();
    }

    private AutoCompleteTextView getAutoCompleteTextView() {
        return (AutoCompleteTextView) findViewById(R.id.editParticipantView_autocomplete_name);
    }

    private static class NameLookupTask extends AsyncTask<String, Void, ArrayList<PhoneContact>> {

        final Context context;
        private ArrayAdapter<String> adapter;

        private NameLookupTask(Context context, AutoCompleteTextView textView) {
            super();
            this.context = context;
            adapter = new ArrayAdapter<String>(context,
                    R.layout.selection_list_medium, new ArrayList<String>());
            textView.setAdapter(adapter);
        }

        @Override
        protected ArrayList<PhoneContact> doInBackground(String... args) {
            PhoneContactResolver mgr = new PhoneContactResolver(context.getContentResolver());
            if (args.length == 0) {
                return mgr.findContactByNameString2(null);
            }
            return mgr.findContactByNameString2(args[0]);
        }

        @Override
        protected void onPostExecute(ArrayList<PhoneContact> result) {
            // final HashMap<String, PhoneContact> contactMap = new
            // HashMap<String, PhoneContact>();
            final List<String> contactList = new ArrayList<String>(result.size());
            for (int i = 0; i < result.size(); i++) {
                PhoneContact oc = result.get(i);
                if (oc == null || oc.displayName == null) {
                    continue;
                }
                String displayNameTrimmed = oc.displayName.trim();
                contactList.add(displayNameTrimmed);
                // contactMap.put(displayNameTrimmed, oc);
            }
            adapter.clear();
            for (int i = 0; i < contactList.size(); i++) {
                adapter.add(contactList.get(i));
            }
            adapter.notifyDataSetChanged();

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        int[] optionIds;
        if(ViewMode.CREATE.equals(viewMode)){
            optionIds = new int[]{
                    R.id.option_accept,
                    R.id.option_help
            };
        } else{
            optionIds = new int[]{
                    R.id.option_save_edit,
                    R.id.option_help
            };
        }
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
        //TODO(ckoelle) The button could be disabled in edit mode when nothing has changed.
        boolean inputNotBlank = !StringUtils.isBlank(getAutoCompleteTextView().getEditableText().toString());
        MenuItem item = menu.findItem(R.id.option_accept);
        if(item == null){
            item = menu.findItem(R.id.option_save_edit);
        } else{
            item.setTitle(R.string.option_save_add);
        }
        item.setEnabled(inputNotBlank);
        item.getIcon().setAlpha((inputNotBlank) ? 255 : 64);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.option_accept:
                createAndCreateAnother();
                return true;
            case R.id.option_save_edit:
                saveAndClose();
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
