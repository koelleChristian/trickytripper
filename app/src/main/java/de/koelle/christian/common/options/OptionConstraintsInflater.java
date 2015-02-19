package de.koelle.christian.common.options;


import android.view.Menu;
import android.view.MenuInflater;

public class OptionConstraintsInflater {
    private MenuInflater menuInflater;
    private Menu menu;
    private int[] optionIds;

    public OptionConstraintsInflater menu(Menu menu) {
        this.menu = menu;
        return this;
    }

    public OptionConstraintsInflater activity(MenuInflater menuInflater) {
        this.menuInflater = menuInflater;
        return this;
    }

    public OptionConstraintsInflater options(int... optionIds) {
        this.optionIds = optionIds;
        return this;
    }


    public MenuInflater getMenuInflater() {
		return menuInflater;
	}

	public Menu getMenu() {
        return menu;
    }

    public int[] getOptionIds() {
        return optionIds;
    }
}