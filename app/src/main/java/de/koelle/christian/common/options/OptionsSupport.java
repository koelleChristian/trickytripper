package de.koelle.christian.common.options;

import android.view.MenuInflater;
import de.koelle.christian.trickytripper.R;

public class OptionsSupport {

    private final int[] allOptions;

    public OptionsSupport(int[] allOptionsAvailable) {
        this.allOptions = allOptionsAvailable;
    }

    public boolean populateOptionsMenu(OptionConstraints optionConstraints) {
        MenuInflater inflater = optionConstraints.getActivity().getMenuInflater();
        inflater.inflate(R.menu.options, optionConstraints.getMenu());
        for (int allOption : allOptions) {
            boolean contained = false;
            for (int j = 0; j < optionConstraints.getOptionIds().length; j++) {
                if (optionConstraints.getOptionIds()[j] == allOption) {
                    contained = true;
                }
            }
            if (!contained) {
                optionConstraints.getMenu().removeItem(allOption);
            }
        }
        return true;
    }
    public boolean populateOptionsMenu(OptionConstraintsInflater optionConstraints) {
        MenuInflater inflater = optionConstraints.getMenuInflater();
        inflater.inflate(R.menu.options, optionConstraints.getMenu());
        for (int allOption : allOptions) {
            boolean contained = false;
            for (int j = 0; j < optionConstraints.getOptionIds().length; j++) {
                if (optionConstraints.getOptionIds()[j] == allOption) {
                    contained = true;
                }
            }
            if (!contained) {
                optionConstraints.getMenu().removeItem(allOption);
            }
        }
        return true;
    }
}
