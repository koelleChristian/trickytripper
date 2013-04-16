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



    public static Dialog showDeleteConfirmationDialog(Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage("")
                .setCancelable(false)
                .setPositiveButton(R.string.common_button_yes, null)
                .setNegativeButton(R.string.common_button_no, null);
        AlertDialog alert = builder.create();
        return alert;
    }


}
