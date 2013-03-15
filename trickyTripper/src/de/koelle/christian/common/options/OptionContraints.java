package de.koelle.christian.common.options;

import android.app.Activity;
import android.view.Menu;

public class OptionContraints {
    private Activity activity;
    private Menu menu;
    private int[] optionIds;

    public OptionContraints menu(Menu menu) {
        this.menu = menu;
        return this;
    }

    public OptionContraints activity(Activity activity) {
        this.activity = activity;
        return this;
    }

    public OptionContraints options(int... optionIds) {
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