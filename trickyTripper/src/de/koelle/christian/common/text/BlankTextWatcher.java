package de.koelle.christian.common.text;

import android.text.Editable;
import android.text.TextWatcher;

/**
 * Blank adapter, allowing you only to override those functions you actually
 * require.
 */
public class BlankTextWatcher implements TextWatcher {

    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        // Intentionally blank, can be overridden on demand.
    }

    public void onTextChanged(CharSequence s, int start, int before, int count) {
        // Intentionally blank, can be overridden on demand.
    }

    public void afterTextChanged(Editable s) {
        // Intentionally blank, can be overridden on demand.
    }

}
