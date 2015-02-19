package de.koelle.christian.trickytripper.ui.utils;

import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;

import android.util.SparseBooleanArray;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import de.koelle.christian.trickytripper.model.Amount;
import de.koelle.christian.trickytripper.modelutils.AmountViewUtils;
import de.koelle.christian.trickytripper.ui.model.RowObject;

public class UiViewUtils {
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static <T> Set<T> getListSelection(ListView listView2, ArrayAdapter<RowObject> adapter2) {

        final Set<T> selectionResult = new LinkedHashSet<T>();

        SparseBooleanArray selection = listView2.getCheckedItemPositions();
        for (int i = 0; i < listView2.getCount(); i++) {
            if (selection.get(i)) {
                T selectedParticipant = ((T) adapter2.getItem(i).getRowObject());
                selectionResult.add(selectedParticipant);
            }
        }
        return selectionResult;
    }

    public static void writeAmountToEditText(Amount amount, EditText editText, Locale locale) {
        editText.setText(AmountViewUtils.getAmountString(locale, amount, true, true, true, false, true));
    }
}
