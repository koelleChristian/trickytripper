package de.koelle.christian.trickytripper.activitysupport;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import de.koelle.christian.trickytripper.R;
import de.koelle.christian.trickytripper.model.Participant;
import de.koelle.christian.trickytripper.ui.model.SpinnerObject;

public class ReportViewSupport {

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

    public static Spinner configureReportSelectionSpinner(Activity activity, Context context, int spinnerViewId,
            List<Participant> participants) {

        Spinner spinner = (Spinner) activity.findViewById(spinnerViewId);
        ArrayAdapter<SpinnerObject> adapter = new ArrayAdapter<SpinnerObject>(
                context,
                android.R.layout.simple_spinner_item,
                createSpinnerObjects(participants, context.getResources()
                ));
        adapter.setDropDownViewResource(R.layout.selection_list_medium);
        spinner.setAdapter(adapter);
        return spinner;
    }
}
