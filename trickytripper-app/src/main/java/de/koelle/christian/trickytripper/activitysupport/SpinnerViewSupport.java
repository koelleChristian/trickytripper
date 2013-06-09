package de.koelle.christian.trickytripper.activitysupport;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import de.koelle.christian.trickytripper.R;
import de.koelle.christian.trickytripper.model.ExchangeRateSelection;
import de.koelle.christian.trickytripper.model.Participant;
import de.koelle.christian.trickytripper.model.ResourceLabelAwareEnumeration;
import de.koelle.christian.trickytripper.ui.model.RowObject;
import de.koelle.christian.trickytripper.ui.model.SpinnerObject;

public class SpinnerViewSupport {

    public static List<SpinnerObject> createSpinnerObjects(List<Participant> participants, Resources resources) {
        List<SpinnerObject> result = new ArrayList<SpinnerObject>();
        SpinnerObject spinnerObject;
        for (Participant participant : participants) {
            String name;
            long id;
            if (participant == null) {
                id = -1;
                name = resources.getString(R.string.report_view_entry_report_spinner_null_value);
            }
            else {
                name = participant.getName();
                id = participant.getId();
            }
            spinnerObject = new SpinnerObject();
            spinnerObject.setId(id);
            spinnerObject.setStringToDisplay(name);
            result.add(spinnerObject);
        }
        return result;
    }

    public static List<SpinnerObject> createSpinnerObjectsDeleteExchangeRateSelection(Resources resources) {
        List<SpinnerObject> result = new ArrayList<SpinnerObject>();
        SpinnerObject spinnerObject;
        for (ExchangeRateSelection entry : ExchangeRateSelection.values()) {
            spinnerObject = new SpinnerObject();
            spinnerObject.setId(entry.getResourceId());
            spinnerObject.setStringToDisplay(resources.getString(entry.getResourceId()));
            result.add(spinnerObject);
        }
        return result;
    }

    @SuppressWarnings("rawtypes")
    public static void setSelection(Spinner spinner, Object value, ArrayAdapter<RowObject> adapter) {
        int position = 0;
        if (value != null) {
            for (int i = 0; i < adapter.getCount(); i++) {
                if (value.equals(adapter.getItem(i).getRowObject())) {
                    position = i;
                    break;
                }
            }
        }
        spinner.setSelection(position, true);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static List<RowObject> createSpinnerObjects(ResourceLabelAwareEnumeration enumeration, boolean addNullValue,
            List<Object> valuesToBeFiltered, Resources resources, final Collator collator) {
        List<RowObject> result = new ArrayList<RowObject>();
        RowObject spinnerObject;
        if (addNullValue) {
            spinnerObject = new RowObject();
            spinnerObject.setRowObject(null);
            spinnerObject.setStringToDisplay(resources.getString(R.string.spinner_null_value_default));
            result.add(spinnerObject);
        }
        for (ResourceLabelAwareEnumeration o : enumeration.getAllValues()) {
            if (valuesToBeFiltered == null || !valuesToBeFiltered.contains(o)) {
                spinnerObject = new RowObject();
                spinnerObject.setRowObject(o);
                spinnerObject.setStringToDisplay(resources.getString(o.getResourceStringId()));
                result.add(spinnerObject);
            }
        }
        Collections.sort(result, new Comparator<RowObject>() {
            public int compare(RowObject object1, RowObject object2) {
                return collator.compare(object1.getStringToDisplay(), object2.getStringToDisplay());
            }

        });
        return result;
    }

    public static Spinner configureReportSelectionSpinner(View view, Context context, int spinnerViewId,
            List<Participant> participants) {

        Spinner spinner = (Spinner) view.findViewById(spinnerViewId);
         configureReportSelectionSpinner(context, participants, spinner);
         return spinner;
    }

    public static void configureReportSelectionSpinner(Context context, List<Participant> participants,
            Spinner spinner) {
        ArrayAdapter<SpinnerObject> adapter = new ArrayAdapter<SpinnerObject>(
                context,
                android.R.layout.simple_spinner_item,
                createSpinnerObjects(participants, context.getResources()
                ));
        adapter.setDropDownViewResource(R.layout.selection_list_medium);
        spinner.setAdapter(adapter);
    }

    public static Spinner configureDeleteExchangeRateSpinner(Activity activity, Context context, int spinnerViewId) {

        Spinner spinner = (Spinner) activity.findViewById(spinnerViewId);
        ArrayAdapter<SpinnerObject> adapter = new ArrayAdapter<SpinnerObject>(
                context,
                android.R.layout.simple_spinner_item,
                createSpinnerObjectsDeleteExchangeRateSelection(context.getResources()));
        adapter.setDropDownViewResource(R.layout.selection_list_medium);
        spinner.setAdapter(adapter);
        return spinner;
    }
}
