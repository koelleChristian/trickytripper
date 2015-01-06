package de.koelle.christian.trickytripper.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;

import java.util.Locale;

import de.koelle.christian.trickytripper.R;
import de.koelle.christian.trickytripper.TrickyTripperApp;
import de.koelle.christian.trickytripper.controller.MiscController;

public class HelpDialogFragment extends DialogFragment {

    private static final String HELP_ASSET_URL_BASE = "file:///android_asset/";
    private static final String HELP_FILE_NAME_ENDING = ".html";
    private static final String HELP_FILE_NAME_BASE = "help";
    private static final String HELP_FILE_URL_EN = HELP_ASSET_URL_BASE + HELP_FILE_NAME_BASE + HELP_FILE_NAME_ENDING;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        FragmentActivity activity = getActivity();
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View view = inflater.inflate(R.layout.help_view, null);

        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        String urlToHelp = determineHelpFileUrl(activity,
                ((TrickyTripperApp) activity.getApplication()).getMiscController());

        final WebView webViewNested = (WebView) view.findViewById(R.id.help_view_web_view);
        builder
                .setView(view)
                .setCancelable(false)
                .setPositiveButton(R.string.common_button_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        HelpDialogFragment.this.dismiss();
                    }
                });
        AlertDialog dialog = builder.create();
        webViewNested.loadUrl(urlToHelp);
        return dialog;
    }

    private static String determineHelpFileUrl(Activity activity, MiscController miscController) {
        Locale locale = activity.getResources().getConfiguration().locale;
        locale.getLanguage();
        String localizedHelpFileName = HELP_FILE_NAME_BASE + "_" + locale.getLanguage() + HELP_FILE_NAME_ENDING;
        String urlToHelp = (miscController.checkIfInAssets(localizedHelpFileName)) ? HELP_ASSET_URL_BASE
                + localizedHelpFileName : HELP_FILE_URL_EN;
        return urlToHelp;
    }
}
