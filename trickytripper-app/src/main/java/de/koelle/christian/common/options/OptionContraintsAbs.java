package de.koelle.christian.common.options;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;

public class OptionContraintsAbs {
    private MenuInflater menuInflater;
    private Menu menu;
    private int[] optionIds;

    public OptionContraintsAbs menu(Menu menu) {
        this.menu = menu;
        return this;
    }

    public OptionContraintsAbs activity(MenuInflater menuInflater) {
        this.menuInflater = menuInflater;
        return this;
    }

    public OptionContraintsAbs options(int... optionIds) {
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