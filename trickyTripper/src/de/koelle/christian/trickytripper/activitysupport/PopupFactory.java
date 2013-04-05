package de.koelle.christian.trickytripper.activitysupport;

import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import de.koelle.christian.trickytripper.R;
import de.koelle.christian.trickytripper.controller.MiscController;

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

    public static Dialog createHelpDialog(final Activity activity, MiscController miscController,
            final int dialogId) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        String urlToHelp = determineHelpFileUrl(activity, miscController);

        LayoutInflater inflater = activity.getLayoutInflater();
        final View view = inflater.inflate(R.layout.help_view, null);
        final WebView webViewNested = (WebView) view.findViewById(R.id.help_view_web_view);
        builder
                .setView(view)
                .setCancelable(false)
                .setPositiveButton(R.string.common_button_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        activity.dismissDialog(dialogId);

                    }
                });
        AlertDialog alert = builder.create();
        webViewNested.loadUrl(urlToHelp);
        return alert;
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
