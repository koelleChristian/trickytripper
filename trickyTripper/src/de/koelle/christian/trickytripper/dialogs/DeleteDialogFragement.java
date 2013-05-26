package de.koelle.christian.trickytripper.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.actionbarsherlock.app.SherlockDialogFragment;

import de.koelle.christian.trickytripper.R;

public class DeleteDialogFragement extends SherlockDialogFragment {

    public interface DeleteConfirmationCallback {
        public String getDeleteConfirmationMsg(Bundle bundle);

        public void doDelete(Bundle bundle);
    }
    
    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        FragmentActivity activity = getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        final Bundle arguments =  getArguments();
        builder.setMessage(getMessage(arguments))
                .setCancelable(false)
                .setPositiveButton(R.string.common_button_yes, new OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        doDelete(arguments);
                        DeleteDialogFragement.this.dismiss();
                    }
                })
                .setNegativeButton(R.string.common_button_no, new OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        DeleteDialogFragement.this.dismiss();
                    }
                });

        return builder.create();
    }

    private String getMessage(Bundle bundle) {
        return getCallBack().getDeleteConfirmationMsg(bundle);
    }

    private void doDelete(Bundle bundle) {        
        getCallBack().doDelete(bundle);
    }

    private DeleteConfirmationCallback getCallBack() {
        try {
            return (DeleteConfirmationCallback) getTargetFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException(getTargetFragment() + " must implement OnArticleSelectedListener");
        }
    }
}
