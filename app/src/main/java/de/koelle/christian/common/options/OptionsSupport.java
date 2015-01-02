package de.koelle.christian.common.options;

import android.view.MenuInflater;
import de.koelle.christian.trickytripper.R;

public class OptionsSupport {

    private final int[] allOptions;

    public OptionsSupport(int[] allOptionsAvailable) {
        this.allOptions = allOptionsAvailable;
    }

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
    public boolean populateOptionsMenu(OptionContraintsAbs optionContraints) {
        MenuInflater inflater = optionContraints.getMenuInflater();
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
