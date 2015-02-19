package de.koelle.christian.common.options;

import android.app.Activity;
import android.view.Menu;

public class OptionConstraints {
    private Activity activity;
    private Menu menu;
    private int[] optionIds;

    public OptionConstraints menu(Menu menu) {
        this.menu = menu;
        return this;
    }

    public OptionConstraints activity(Activity activity) {
        this.activity = activity;
        return this;
    }

    public OptionConstraints options(int... optionIds) {
        this.optionIds = optionIds;
        return this;
    }

    public Activity getActivity() {
        return activity;
    }

    public Menu getMenu() {
        return menu;
    }

    public int[] getOptionIds() {
        return optionIds;
    }
}