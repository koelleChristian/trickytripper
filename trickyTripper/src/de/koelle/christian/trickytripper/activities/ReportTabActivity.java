package de.koelle.christian.trickytripper.activities;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import de.koelle.christian.common.utils.UiUtils;
import de.koelle.christian.trickytripper.R;
import de.koelle.christian.trickytripper.TrickyTripperApp;
import de.koelle.christian.trickytripper.constants.TrickyTripperTabConstants;
import de.koelle.christian.trickytripper.model.Amount;
import de.koelle.christian.trickytripper.model.Debts;
import de.koelle.christian.trickytripper.model.Participant;
import de.koelle.christian.trickytripper.model.PaymentCategory;
import de.koelle.christian.trickytripper.modelutils.AmountViewUtils;
import de.koelle.christian.trickytripper.strategies.SumReport;
import de.koelle.christian.trickytripper.ui.model.SpinnerObject;

public class ReportTabActivity extends Activity {

    private final static int PADDING = 3;

    private final List<View> dynamicSpendingRows = new ArrayList<View>();
    private final List<View> dynamicOwingDebtsRows = new ArrayList<View>();

    private List<Participant> participantsInSpinner;

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.layout.general_options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.general_options_help:
            getParent().showDialog(TrickyTripperTabConstants.DIALOG_SHOW_HELP);
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        createPanel();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.report_tab_view);
        createPanel();

    }

    private void createPanel() {

        final TrickyTripperApp app = getApp();

        participantsInSpinner = new ArrayList<Participant>();
        participantsInSpinner.add(null);
        participantsInSpinner.addAll(getAllRelevantParticipantsInOrder());

        Spinner spinner = (Spinner) findViewById(R.id.reportViewBaseSpinner);
        ArrayAdapter<SpinnerObject> adapter = new ArrayAdapter<SpinnerObject>(this,
                android.R.layout.simple_spinner_item, createSpinnerObjects(participantsInSpinner));
        adapter.setDropDownViewResource(R.layout.selection_list_medium);
        spinner.setAdapter(adapter);

        Participant p = app.getDialogState().getParticipantReporting(); // can
                                                                        // be
                                                                        // null
        int spinnerPositionToBe = getPositionInSpinner(p, participantsInSpinner);
        spinner.setSelection(spinnerPositionToBe, false);

        final Locale locale = getResources().getConfiguration().locale;
        updateDynamicRows(app, p, locale);
        updateStaticRows(app, p, locale);

        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                Participant participantSelected = participantsInSpinner.get(position); // can
                                                                                       // be
                                                                                       // null
                app.getDialogState().setParticipantReporting(participantSelected);
                ReportTabActivity.this.updateDynamicRows(app, participantSelected, locale);
                ReportTabActivity.this.updateStaticRows(app, participantSelected, locale);
            }

            public void onNothingSelected(AdapterView<?> parentView) {
                // intentionally blank
            }

        });

    }

    private int getPositionInSpinner(Participant p, List<Participant> participantsInSpinner2) {
        if (p == null) {
            return 0;
        }

        for (int i = 0; i < participantsInSpinner2.size(); i++) {
            Participant pInList = participantsInSpinner2.get(i);
            if (pInList == null) {
                continue;
            }
            else if (pInList.equals(p)) {
                return i;
            }
        }
        return 0;
    }

    private List<Participant> getAllRelevantParticipantsInOrder() {
        List<Participant> participantsInvolved = new ArrayList<Participant>(getApp().getFktnController()
                .getAllParticipants(false));
        final Collator collator = getApp().getFktnController().getDefaultStringCollator();
        Collections.sort(participantsInvolved, new Comparator<Participant>() {
            public int compare(Participant object1, Participant object2) {
                return collator.compare(object1.getName(), object2.getName());
            }
        });
        return participantsInvolved;
    }

    private TrickyTripperApp getApp() {
        final TrickyTripperApp app = ((TrickyTripperApp) getApplication());
        return app;
    }

    private void updateStaticRows(TrickyTripperApp app, Participant participantSelected, Locale locale) {

        int viewId;
        Object valueCount;
        Object valueTotalSpent;

        if (participantSelected != null) {
            valueTotalSpent = AmountViewUtils.getAmountString(locale, app.getTripToBeEdited().getSumReport()
                    .getSpendingByUser().get(participantSelected), true, true, true);
            valueCount = app.getTripToBeEdited().getSumReport().getSpendingByUserCount().get(participantSelected);
        }
        else {
            valueTotalSpent = AmountViewUtils.getAmountString(locale, app.getTripToBeEdited().getSumReport()
                    .getTotalSpendings(), true, true, true);
            valueCount = app.getTripToBeEdited().getSumReport().getTotalSpendingCount();
        }
        viewId = R.id.reportViewOutputTotalSpent;
        UiUtils.setLabelAndValueOnTextView(this, viewId, null, valueTotalSpent);
        viewId = R.id.reportViewOutputPaymentCount;
        UiUtils.setLabelAndValueOnTextView(this, viewId, null, valueCount);
    }

    private void updateDynamicRows(TrickyTripperApp app, Participant participantSelected, Locale locale) {

        TableLayout tableLayout = (TableLayout) findViewById(R.id.reportViewTableLayout);
        TextView heading = (TextView) tableLayout.findViewById(R.id.reportViewOutputHeadingOwingDebts);
        View delimiterLine = tableLayout.findViewById(R.id.reportViewOutputDelimiterLine);

        removeDynamicRows(tableLayout);

        addDynamicCategoryRows(app, participantSelected, locale, tableLayout, delimiterLine);
        addDynamicDebtRows(app, participantSelected, locale, tableLayout, heading, delimiterLine);

    }

    private void addDynamicCategoryRows(TrickyTripperApp app, Participant participantSelected, Locale locale,
            TableLayout tableLayout, View delimiterLine) {

        SumReport sumReport = app.getTripToBeEdited().getSumReport();
        Map<PaymentCategory, Amount> categorySpending;

        if (participantSelected != null) {
            categorySpending = sumReport.getSpendingByUserByCategory().get(participantSelected);
        }
        else {
            categorySpending = sumReport.getTotalSpendingByCategory();
        }
        TableRow newRow;
        String value;
        int column;
        if (categorySpending == null || categorySpending.isEmpty()) {
            newRow = new TableRow(this);

            value = getResources().getString(R.string.report_view_label_no_spendings);
            column = 4;

            TextView textView = addNewTextViewToRow(newRow, value, column);
            textView.setGravity(Gravity.RIGHT);
            textView.setPadding(PADDING, PADDING, 0, PADDING);

            this.dynamicOwingDebtsRows.add(newRow);
            tableLayout.addView(newRow, calculatePositionToInsert(tableLayout, delimiterLine));
        }
        else {
            TreeMap<String, View> newRows = new TreeMap<String, View>(getApp().getFktnController()
                    .getDefaultStringCollator());

            for (Entry<PaymentCategory, Amount> catAmountMapEntry : categorySpending.entrySet()) {

                if (!catAmountMapEntry.getKey().isInternal()) {
                    newRow = new TableRow(this);
                    String categoryDisplayName = getResources().getString(
                            catAmountMapEntry.getKey().getResourceStringId());

                    value = categoryDisplayName + ":";
                    column = 1;
                    addNewTextViewToRow(newRow, value, column);

                    value = AmountViewUtils.getAmountString(locale, catAmountMapEntry.getValue(), true, true, true);
                    column = 4;
                    TextView textView = addNewTextViewToRow(newRow, value, column);
                    textView.setGravity(Gravity.RIGHT);
                    textView.setPadding(PADDING, PADDING, 0, PADDING);

                    this.dynamicSpendingRows.add(newRow);
                    newRows.put(categoryDisplayName, newRow);
                }

            }
            int positionToInsert = calculatePositionToInsert(tableLayout, delimiterLine);
            for (Entry<String, View> entry : newRows.entrySet()) {
                tableLayout.addView(entry.getValue(), positionToInsert);
                positionToInsert++;
            }
        }

    }

    private int calculatePositionToInsert(TableLayout tableLayout, View delimiterLine) {
        for (int i = 0; i < tableLayout.getChildCount(); i++) {
            View view = tableLayout.getChildAt(i);
            if (delimiterLine.getId() == view.getId()) {
                return i;
            }
        }
        return -1;
    }

    private void addDynamicDebtRows(TrickyTripperApp app, Participant participantSelected, Locale locale,
            TableLayout tableLayout, TextView heading, View delimiterLine) {
        if (participantSelected != null) {
            heading.setVisibility(ViewGroup.VISIBLE);
            delimiterLine.setVisibility(ViewGroup.VISIBLE);

            TableRow newRow;
            String value;
            int column;

            Debts debts = app.getTripToBeEdited().getDebts().get(participantSelected);
            if (debts.getLoanerToDepts().entrySet().isEmpty()) {
                newRow = new TableRow(this);

                value = AmountViewUtils.getAmountString(locale, app.getAmountFactory().createAmount(), true, true,
                        true);
                column = 2;
                TableRow.LayoutParams params = new TableRow.LayoutParams();
                params.span = 3;
                TextView textView = addNewTextViewToRow(newRow, value, column, params);
                textView.setGravity(Gravity.RIGHT);
                textView.setPadding(PADDING, PADDING, 0, PADDING);

                this.dynamicOwingDebtsRows.add(newRow);
                tableLayout.addView(newRow, tableLayout.getChildCount() - 1);
            }
            else {
                TreeMap<String, View> newRows = new TreeMap<String, View>(getApp().getFktnController()
                        .getDefaultStringCollator());

                for (Entry<Participant, Amount> debt : debts.getLoanerToDepts().entrySet()) {

                    newRow = new TableRow(this);
                    String displayName = debt.getKey().getName();

                    value = displayName;
                    column = 1;
                    addNewTextViewToRow(newRow, value, column);

                    value = AmountViewUtils.getAmountString(locale, debt.getValue(), true, true, true);
                    column = 4;
                    TextView textView = addNewTextViewToRow(newRow, value, column);
                    textView.setGravity(Gravity.RIGHT);
                    textView.setPadding(PADDING, PADDING, 0, PADDING);

                    this.dynamicOwingDebtsRows.add(newRow);
                    newRows.put(displayName, newRow);
                }
                for (Entry<String, View> entry : newRows.entrySet()) {
                    tableLayout.addView(entry.getValue(), tableLayout.getChildCount() - 1);
                }
            }

        }
        else {
            heading.setVisibility(ViewGroup.GONE);
            delimiterLine.setVisibility(ViewGroup.GONE);
        }
    }

    private TextView addNewTextViewToRow(TableRow target, String valueForDisplay, int column) {
        return addNewTextViewToRow(target, valueForDisplay, column, new TableRow.LayoutParams());
    }

    private TextView addNewTextViewToRow(TableRow target, String valueForDisplay, int column,
            TableRow.LayoutParams columnRowParams) {
        TableRow.LayoutParams columnRowParamsHere = columnRowParams;
        TextView textView;
        textView = new TextView(this);
        textView.setText(valueForDisplay);
        textView.setPadding(PADDING, PADDING, PADDING, PADDING);
        columnRowParamsHere.column = column;

        target.addView(textView, columnRowParams);
        return textView;
    }

    private void removeDynamicRows(TableLayout tableLayout) {
        if (!dynamicOwingDebtsRows.isEmpty()) {
            for (View dynamicRow : dynamicOwingDebtsRows) {
                tableLayout.removeView(dynamicRow);
            }
        }
        if (!dynamicSpendingRows.isEmpty()) {
            for (View dynamicRow : dynamicSpendingRows) {
                tableLayout.removeView(dynamicRow);
            }
        }
    }

    private List<SpinnerObject> createSpinnerObjects(List<Participant> participants) {
        List<SpinnerObject> result = new ArrayList<SpinnerObject>();
        SpinnerObject spinnerObject;
        for (Participant participant : participants) {
            String name;
            long id;
            if (participant == null) {
                id = -1;
                name = getResources().getString(R.string.report_view_entry_report_spinner_null_value);
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

}
