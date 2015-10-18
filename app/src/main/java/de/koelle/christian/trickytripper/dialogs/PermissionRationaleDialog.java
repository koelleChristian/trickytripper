package de.koelle.christian.trickytripper.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import de.koelle.christian.trickytripper.R;

public class PermissionRationaleDialog  extends DialogFragment {

    public interface PermissionRationaleDialogCallback {
        public void permissionCustomNotificationDone();
        public int getPermissionNotificationTextId();
    }

    PermissionRationaleDialogCallback callback;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            callback = (PermissionRationaleDialogCallback) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement PermissionRationaleDialogCallback");
        }
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(callback.getPermissionNotificationTextId())
                .setPositiveButton(R.string.common_button_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dismiss();
                        callback.permissionCustomNotificationDone();

                    }
                });
        return builder.create();
    }
}