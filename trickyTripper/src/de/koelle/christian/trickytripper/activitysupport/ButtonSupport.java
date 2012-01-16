package de.koelle.christian.trickytripper.activitysupport;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import de.koelle.christian.common.utils.StringUtils;

public class ButtonSupport {

    public static void disableButtonOnBlankInput(final EditText textInput, final Button button) {

        if (StringUtils.isBlank(textInput.getEditableText().toString())) {
            button.setEnabled(false);
        }
        else {
            button.setEnabled(true);
        }

        textInput.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

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
