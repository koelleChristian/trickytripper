package de.koelle.christian.common.options;

import android.view.MenuInflater;
import de.koelle.christian.trickytripper.R;

public class OptionsSupport {

    private final int[] allOptions;

    // private static enum OptionSet {
    // DEFAULT(R.id.option_help),
    // DEFAULT_AND_IMPORT(R.id.option_help, R.id.option_import),
    // MANAGE_TRIPS(R.id.option_help, R.id.option_create),
    // MANAGE_EXCHANGE_RATES(R.id.option_help, R.id.option_delete),
    // TAB_PARTICIPANT(R.id.option_help, R.id.option_preferences,
    // R.id.option_export, R.id.option_create),
    // TAB_DEFAULT(R.id.option_help, R.id.option_preferences,
    // R.id.option_export),
    // /**/
    // ;
    //
    // private final int[] options;
    //
    // private OptionSet(int... options) {
    // this.options = options;
    // }
    //
    // public int[] getOptions() {
    // return options;
    // }
    //
    // }

    public OptionsSupport(int[] allOptionsAvailable) {
        this.allOptions = allOptionsAvailable;
    }

    // static {

    // }

    public boolean populateOptionsMenu(OptionContraints optionContraints) {
        MenuInflater inflater = optionContraints.getActivity().getMenuInflater();
        inflater.inflate(R.layout.options, optionContraints.getMenu());
        for (int i = 0; i < allOptions.length; i++) {
            boolean contained = false;
            for (int j = 0; j < optionContraints.getOptionIds().length; j++) {
                if (optionContraints.getOptionIds()[j] == allOptions[i]) {
                    contained = true;
                }
            }
            if (!contained) {
                optionContraints.getMenu().removeItem(allOptions[i]);
            }
        }
        return true;
    }
}
