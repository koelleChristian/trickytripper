package de.koelle.christian.trickytripper.activitysupport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import de.koelle.christian.trickytripper.R;
import de.koelle.christian.trickytripper.constants.Rc;
import de.koelle.christian.trickytripper.controller.TripExpensesFktnController;
import de.koelle.christian.trickytripper.dataaccess.PhoneContactResolver;
import de.koelle.christian.trickytripper.model.PhoneContact;

public class PopupFactory {

    private static final String HELP_ASSET_URL_BASE = "file:///android_asset/";
    private static final String HELP_FILE_NAME_ENDING = ".html";
    private static final String HELP_FILE_NAME_BASE = "help";
    private static final String HELP_FILE_URL_EN = HELP_ASSET_URL_BASE + HELP_FILE_NAME_BASE + HELP_FILE_NAME_ENDING;

    public static Dialog showDeleteConfirmationDialog(Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage("")
                .setCancelable(false)
                .setPositiveButton(R.string.common_button_yes, null)
                .setNegativeButton(R.string.common_button_no, null);
        AlertDialog alert = builder.create();
        return alert;
    }

    public static Dialog createHelpDialog(final Activity activity, TripExpensesFktnController fktnController,
            final int dialogId) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        String urlToHelp = determineHelpFileUrl(activity, fktnController);

        // final ProgressDialog progressDialog = ProgressDialog.show(activity,
        // activity.getResources().getString(R.string.fktn_glob_help_info),
        // activity.getResources().getString(R.string.progress_dialog_text_loading));

        LayoutInflater inflater = activity.getLayoutInflater();
        final View view = inflater.inflate(R.layout.help_view, null);
        final WebView webViewNested = (WebView) view.findViewById(R.id.help_view_web_view);
        // webViewNested.setWebViewClient(new WebViewClient() {
        // @Override
        // public boolean shouldOverrideUrlLoading(WebView view, String url) {
        // view.loadUrl(url);
        // return true;
        // }
        //
        // @Override
        // public void onPageFinished(WebView view, String url) {
        // if (progressDialog.isShowing()) {
        // progressDialog.dismiss();
        builder
                .setView(view)
                .setCancelable(false)
                .setPositiveButton(R.string.common_button_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        activity.dismissDialog(dialogId);

                    }
                });
        AlertDialog alert = builder.create();
        // alert.show();
        // }
        // }
        // });
        webViewNested.loadUrl(urlToHelp);
        return alert;
    }

    private static String determineHelpFileUrl(Activity activity, TripExpensesFktnController fktnController) {
        Locale locale = activity.getResources().getConfiguration().locale;
        locale.getLanguage();
        String localizedHelpFileName = HELP_FILE_NAME_BASE + "_" + locale.getLanguage() + HELP_FILE_NAME_ENDING;
        String urlToHelp = (fktnController.checkIfInAssets(localizedHelpFileName)) ? HELP_ASSET_URL_BASE
                + localizedHelpFileName : HELP_FILE_URL_EN;
        return urlToHelp;
    }

    public static Dialog createAndShowEditParticipantPopupCreateMode(final Activity activity,
            TripExpensesFktnController fktnController) {
        int titleId = R.string.edit_participant_view_heading_create;
        int positiveButtonLabelId = R.string.edit_participant_view_positive_button_create;
        return createAndShowEditParticipantPopup(titleId, positiveButtonLabelId, activity,
                fktnController);
    }

    public static Dialog createAndShowEditParticipantPopupEditMode(final Activity activity,
            TripExpensesFktnController fktnController) {
        int titleId = R.string.edit_participant_view_heading_edit;
        int positiveButtonLabelId = R.string.edit_participant_view_positive_button_edit;
        return createAndShowEditParticipantPopup(titleId, positiveButtonLabelId, activity,
                fktnController);
    }

    private static Dialog createAndShowEditParticipantPopup(int titleId, int positiveButtonLabelId,
            final Activity activity, final TripExpensesFktnController fktnController) {

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        LayoutInflater inflater = activity.getLayoutInflater();
        final View view = inflater.inflate(R.layout.create_participant_view, null);

        builder.setTitle(titleId)
                .setCancelable(true)
                .setView(view);

        final AlertDialog alert = builder.create();
        Button buttonPositive = (Button) view.findViewById(R.id.createParticipantView_button_positive);
        buttonPositive.setText(positiveButtonLabelId);

        final AutoCompleteTextView autoCompleteTextView =
                (AutoCompleteTextView) view.findViewById(R.id.createParticipantView_autocomplete_name);

        ButtonSupport.disableButtonOnBlankInput(autoCompleteTextView, buttonPositive);

        autoCompleteTextView.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable textInput) {
            }

            public void beforeTextChanged(CharSequence s, int start,
                    int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before,
                    int count) {
                if (s.length() == 2) {
                    NameLookupTask task = new NameLookupTask(activity, autoCompleteTextView);
                    task.execute(s.toString());
                }
            }
        });
        return alert;
    }

    private static class NameLookupTask extends AsyncTask<String, Void, ArrayList<PhoneContact>> {

        final Context context;
        final AutoCompleteTextView textView;

        private NameLookupTask(Context context, AutoCompleteTextView textView) {
            super();
            this.context = context;
            this.textView = textView;
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
            final HashMap<String, PhoneContact> contactMap = new HashMap<String, PhoneContact>();
            final List<String> contactList = new ArrayList<String>(result.size());
            for (int i = 0; i < result.size(); i++) {
                PhoneContact oc = result.get(i);
                if (oc == null || oc.displayName == null) {
                    continue;
                }
                String displayNameTrimmed = oc.displayName.trim();
                contactList.add(displayNameTrimmed);
                contactMap.put(displayNameTrimmed, oc);
            }
            ArrayAdapter<String> suggestionAdapter = new ArrayAdapter<String>(context,
                    R.layout.selection_list_medium, contactList);
            textView.setAdapter(suggestionAdapter);
            suggestionAdapter.notifyDataSetChanged();
            textView.setOnItemClickListener(new OnItemClickListener() {

                public void onItemClick(AdapterView<?> autoCompleteView,
                        View view, int position, long id) {
                    String displayName = (String) autoCompleteView
                            .getItemAtPosition(position);
                    PhoneContact contact = contactMap.get(displayName);
                    if (contact != null) {
                        if (Log.isLoggable(Rc.LT, Log.DEBUG)) {
                            Log.d("OnItemClick", contact.toString());
                        }
                    }
                }
            });
        }
    }
}
