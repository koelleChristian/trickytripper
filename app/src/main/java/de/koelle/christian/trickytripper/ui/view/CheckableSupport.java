package de.koelle.christian.trickytripper.ui.view;

import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

public class CheckableSupport {
    private CheckBox checkbox;

    protected void onFinishInflate(ViewGroup viewGroup) {
        checkbox = findCheckbox(viewGroup);
    }

    private CheckBox findCheckbox(ViewGroup viewGroup) {
        int childCount = viewGroup.getChildCount();
        for (int i = 0; i < childCount; ++i) {
            View view = viewGroup.getChildAt(i);
            if (view instanceof CheckBox) {
                return (CheckBox) view;
            }
            else if (view instanceof ViewGroup) {
                CheckBox interimResult = findCheckbox(((ViewGroup) view));
                if (interimResult != null) {
                    return interimResult;
                }
            }

        }
        return null;
    }

    public boolean isChecked() {
        return checkbox != null && checkbox.isChecked();
    }

    public void setChecked(boolean checked) {
        if (checkbox != null) {
            checkbox.setChecked(checked);
        }
    }

    public void toggle() {
        if (checkbox != null) {
            checkbox.toggle();
        }
    }
}
