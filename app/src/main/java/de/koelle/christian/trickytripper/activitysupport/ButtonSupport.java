package de.koelle.christian.trickytripper.activitysupport;

import android.text.Editable;
import android.widget.Button;
import android.widget.EditText;
import de.koelle.christian.common.text.BlankTextWatcher;
import de.koelle.christian.common.utils.StringUtils;

public class ButtonSupport {

    // TODO(ckoelle) ABS Is this still required?

    public static void disableButtonOnBlankInput(final EditText textInput, final Button button) {

        if (StringUtils.isBlank(textInput.getEditableText().toString())) {
            button.setEnabled(false);
        }
        else {
            button.setEnabled(true);
        }

        textInput.addTextChangedListener(new BlankTextWatcher() {
            public void afterTextChanged(Editable s) {
                String textInputString = textInput.getEditableText().toString();
                if (StringUtils.isBlank(textInputString)) {
                    button.setEnabled(false);
                }
                else {
                    button.setEnabled(true);
                }
            }
        });

    }

}
