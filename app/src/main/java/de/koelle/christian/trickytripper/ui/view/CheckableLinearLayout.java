package de.koelle.christian.trickytripper.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Checkable;
import android.widget.LinearLayout;

public class CheckableLinearLayout extends LinearLayout implements Checkable {
    private final CheckableSupport checkableSupport;

    public CheckableLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.checkableSupport = new CheckableSupport();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        checkableSupport.onFinishInflate(this);
    }

    public boolean isChecked() {
        return checkableSupport.isChecked();
    }

    public void setChecked(boolean checked) {
        checkableSupport.setChecked(checked);
    }

    public void toggle() {
        checkableSupport.toggle();
    }
}
