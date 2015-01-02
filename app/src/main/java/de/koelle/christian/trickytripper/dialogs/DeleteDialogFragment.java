package de.koelle.christian.trickytripper.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;

import de.koelle.christian.common.utils.Assert;
import de.koelle.christian.trickytripper.R;

public class DeleteDialogFragment extends DialogFragment {

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
                        DeleteDialogFragment.this.dismiss();
                    }
                })
                .setNegativeButton(R.string.common_button_no, new OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        DeleteDialogFragment.this.dismiss();
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
        DeleteConfirmationCallback result;        
        if(getTargetFragment() != null){            
            try {            
                result = (DeleteConfirmationCallback) getTargetFragment();
            } catch (ClassCastException e) {
                throw new ClassCastException("The targetFragment had been set but did not implement DeleteConfirmationCallback. Was: "  +getTargetFragment().getClass());
            }
        } else{
            try {            
                result = (DeleteConfirmationCallback) getActivity();
            } catch (ClassCastException e) {
                throw new ClassCastException("At least "+ getActivity() + " must implement DeleteConfirmationCallback");
            }
        }
        Assert.notNull(result);
        return result;
    }
}
