package de.koelle.christian.common.options;


import android.view.Menu;
import android.view.MenuInflater;

public class OptionContraintsInflater {
    private MenuInflater menuInflater;
    private Menu menu;
    private int[] optionIds;

    public OptionContraintsInflater menu(Menu menu) {
        this.menu = menu;
        return this;
    }

    public OptionContraintsInflater activity(MenuInflater menuInflater) {
        this.menuInflater = menuInflater;
        return this;
    }

    public OptionContraintsInflater options(int... optionIds) {
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