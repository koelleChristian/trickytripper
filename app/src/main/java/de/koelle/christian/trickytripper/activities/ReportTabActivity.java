package de.koelle.christian.trickytripper.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import de.koelle.christian.common.utils.UiUtils;
import de.koelle.christian.trickytripper.R;
import de.koelle.christian.trickytripper.TrickyTripperApp;
import de.koelle.christian.trickytripper.activitysupport.SpinnerViewSupport;
import de.koelle.christian.trickytripper.model.Amount;
import de.koelle.christian.trickytripper.model.Debts;
import de.koelle.christian.trickytripper.model.Participant;
import de.koelle.christian.trickytripper.model.PaymentCategory;
import de.koelle.christian.trickytripper.model.Trip;
import de.koelle.christian.trickytripper.modelutils.AmountViewUtils;
import de.koelle.christian.trickytripper.strategies.SumReport;

public class ReportTabActivity extends Fragment {

    private final List<View> dynamicSpendingRows = new ArrayList<View>();
    private final List<View> dynamicOwingDebtsRows = new ArrayList<View>();
    private final List<View> dynamicDividerRowsSpending = new ArrayList<View>();
    private final List<View> dynamicDividerRowsOwingDebtsRows = new ArrayList<View>();

    private List<Participant> participantsInSpinner;
    private View view;

    private int padding8;
    private int onePixel;

    @Override
    public void onResume() {
        super.onResume();
        if (padding8 == 0) {
            padding8 = UiUtils.dpi2px(getResources(), 8);
        }
        if (onePixel == 0) {
            onePixel = UiUtils.dpi2px(getResources(), 1);
        }
        createPanel(view);
        getActivity().supportInvalidateOptionsMenu();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.report_tab_view, container, false);
        return view;
    }

    private void createPanel(final View view) {

        final TrickyTripperApp app = getApp();

        participantsInSpinner = new ArrayList<Participant>();
        participantsInSpinner.add(null);
        participantsInSpinner.addAll(getAllParticipants(app));

        Spinner spinner = SpinnerViewSupport.configureReportSelectionSpinner(
                view,
                getActivity(),
                R.id.reportViewBaseSpinner,
                participantsInSpinner);

        Participant p = app.getTripController().getDialogState().getParticipantReporting(); // can
        // be
        // null
        int spinnerPositionToBe = getPositionInSpinner(p, participantsInSpinner);
        spinner.setSelection(spinnerPositionToBe, false);

        final Locale locale = getResources().getConfiguration().locale;
        updateDynamicRows(app, p, locale, view);
        updateStaticRows(app, p, locale, view);

        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                Participant participantSelected = participantsInSpinner.get(position); // can
                // be
                // null
                app.getTripController().getDialogState().setParticipantReporting(participantSelected);
                ReportTabActivity.this.updateDynamicRows(app, participantSelected, locale, view);
                ReportTabActivity.this.updateStaticRows(app, participantSelected, locale, view);
            }

            public void onNothingSelected(AdapterView<?> parentView) {
                // intentionally blank
            }

        });

    }

    private List<Participant> getAllParticipants(TrickyTripperApp app) {
        return app.getTripController().getAllParticipants(false, true);
    }

    private int getPositionInSpinner(Participant p, List<Participant> participantsInSpinner2) {
        if (p == null) {
            return 0;
        }

        for (int i = 0; i < participantsInSpinner2.size(); i++) {
            Participant pInList = participantsInSpinner2.get(i);
            if (pInList != null && pInList.equals(p)) {
                return i;
            }
        }
        return 0;
    }

    private TrickyTripperApp getApp() {
        return ((TrickyTripperApp) getActivity().getApplication());
    }

    private void updateStaticRows(TrickyTripperApp app, Participant participantSelected, Locale locale, View view) {

        int viewId;
        Object valueCount;
        Object valueTotalSpent;

        if (participantSelected != null) {
            valueTotalSpent = AmountViewUtils.getAmountString(locale, getTripLoaded(app)
                    .getSumReport()
                    .getSpendingByUser().get(participantSelected), true, true, true);
            valueCount = getTripLoaded(app).getSumReport().getSpendingByUserCount()
                    .get(participantSelected);
        } else {
            valueTotalSpent = AmountViewUtils.getAmountString(locale, getTripLoaded(app)
                    .getSumReport()
                    .getTotalSpending(), true, true, true);
            valueCount = getTripLoaded(app).getSumReport().getTotalSpendingCount();
        }
        viewId = R.id.reportViewOutputTotalSpent;
        UiUtils.setLabelAndValueOnTextView(view, viewId, null, valueTotalSpent);
        viewId = R.id.reportViewOutputPaymentCount;
        UiUtils.setLabelAndValueOnTextView(view, viewId, null, valueCount);
    }

    private void updateDynamicRows(TrickyTripperApp app, Participant participantSelected, Locale locale, View view) {

        TableLayout tableLayoutUpper = (TableLayout) view.findViewById(R.id.reportViewTableLayout);
        TableLayout tableLayoutLower = (TableLayout) view.findViewById(R.id.reportViewTableLayoutDebts);

        removeDynamicRows(tableLayoutLower, tableLayoutUpper);

        addDynamicCategoryRows(app, participantSelected, locale, tableLayoutUpper);
        addDynamicDebtRows(app, participantSelected, locale, tableLayoutLower);

    }

    private void addDynamicCategoryRows(TrickyTripperApp app, Participant participantSelected, Locale locale,
                                        TableLayout tableLayout) {

        SumReport sumReport = getTripLoaded(app).getSumReport();
        Map<PaymentCategory, Amount> categorySpending;

        if (participantSelected != null) {
            categorySpending = sumReport.getSpendingByUserByCategory().get(participantSelected);
        } else {
            categorySpending = sumReport.getTotalSpendingByCategory();
        }
        TableRow newRow;
        String value;
        int column;
        if (categorySpending == null || categorySpending.isEmpty()) {
            newRow = new TableRow(getActivity());

            value = getResources().getString(R.string.report_view_label_no_spendings);
            column = 3;

            TextView textView = addNewTextViewToRow(newRow, value, column);
            textView.setGravity(Gravity.RIGHT);

            this.dynamicSpendingRows.add(newRow);
            tableLayout.addView(newRow);
        } else {
            TreeMap<String, View> newRows = new TreeMap<String, View>(getApp().getMiscController()
                    .getDefaultStringCollator());

            for (Entry<PaymentCategory, Amount> catAmountMapEntry : categorySpending.entrySet()) {

                if (!catAmountMapEntry.getKey().isInternal()) {
                    newRow = new TableRow(getActivity());
                    String categoryDisplayName = getResources().getString(
                            catAmountMapEntry.getKey().getResourceStringId());

                    value = categoryDisplayName + ":";
                    column = 0;
                    addNewTextViewToRow(newRow, value, column);

                    value = AmountViewUtils.getAmountString(locale, catAmountMapEntry.getValue(), true, true, true);
                    column = 3;
                    TextView textView = addNewTextViewToRow(newRow, value, column);
                    textView.setGravity(Gravity.RIGHT);

                    this.dynamicSpendingRows.add(newRow);
                    newRows.put(categoryDisplayName, newRow);
                }
            }
            addOrderedDynamicRowsToView(newRows, tableLayout, 4, dynamicDividerRowsSpending, false);
        }

    }

    private void addDynamicDebtRows(TrickyTripperApp app, Participant participantSelected, Locale locale, TableLayout tableLayoutDebts) {

        TableRow headingDebtsSubheading = (TableRow) view.findViewById(R.id.reportViewTableLayoutDebtsHeading2);
        TableRow subheadingDividerTop =  (TableRow) view.findViewById(R.id.reportViewSpacerDebtsTop);
        TableRow headingNoDebts = (TableRow) view.findViewById(R.id.reportViewTableLayoutDebtsHeadingNoDebts);

        Collection<Participant> involvedParticipants = new ArrayList<Participant>();
        if (participantSelected != null) {
            involvedParticipants.add(participantSelected);
        } else {
            involvedParticipants.addAll(getAllParticipants(app));
        }
        boolean areThereDebtsToBeDisplayed = false;
        TreeMap<String, View> newRows = new TreeMap<String, View>(getApp().getMiscController()
                .getDefaultStringCollator());


        for (Entry<Participant, Debts> entry : getTripLoaded(app).getDebts().entrySet()) {

            Debts debts = entry.getValue();
            if (debts != null && debts.getLoanerToDebts() != null) {
                TableRow newRow;
                TextView textView;
                String value;
                int column;

                for (Iterator<Entry<Participant, Amount>> itInternal = debts.getLoanerToDebts().entrySet().iterator(); itInternal.hasNext(); ) {
                    Entry<Participant, Amount> debt = itInternal.next();

                    if (isInScope(involvedParticipants, entry, debt)) {

                        areThereDebtsToBeDisplayed = true;

                        newRow = new TableRow(getActivity());

                        value = entry.getKey().getName();
                        column = 0;
                        textView = addNewTextViewToRow(newRow, value, column, 0.35f);
                        textView.setWidth(0);

                        value = debt.getKey().getName();
                        column = 1;
                        textView = addNewTextViewToRow(newRow, value, column, 0.35f);
                        textView.setWidth(0);

                        value = AmountViewUtils.getAmountString(locale, debt.getValue(), true, true, true);
                        column = 2;
                        textView = addNewTextViewToRow(newRow, value, column, 0.30f);
                        textView.setGravity(Gravity.RIGHT);
                        textView.setWidth(0);

                        this.dynamicOwingDebtsRows.add(newRow);
                        newRows.put(entry.getKey().getName() + debt.getKey().getName(), newRow);
                    }
                }
            }
        }
        if(areThereDebtsToBeDisplayed){
            addOrderedDynamicRowsToView(newRows, tableLayoutDebts, 3, dynamicDividerRowsOwingDebtsRows, true);
        }

        UiUtils.setViewVisibility(subheadingDividerTop, areThereDebtsToBeDisplayed);
        UiUtils.setViewVisibility(headingDebtsSubheading, areThereDebtsToBeDisplayed);
        UiUtils.setViewVisibility(headingNoDebts, !areThereDebtsToBeDisplayed);
    }

    private void addOrderedDynamicRowsToView(TreeMap<String, View> newRows, TableLayout tableLayout, int dividerLineRowSpan, List<View> dynamicDividerRowHolder, boolean isDebts) {

        tableLayout.addView(createAndSaveNewDividerRow(dividerLineRowSpan, dynamicDividerRowHolder, isDebts));

        for (Entry<String, View> entry : newRows.entrySet()) {
            View row = entry.getValue();
            row.setPadding(0, padding8, 0, padding8);
            tableLayout.addView(row);
            tableLayout.addView(createAndSaveNewDividerRow(dividerLineRowSpan, dynamicDividerRowHolder, isDebts));
        }
    }

    private TableRow createAndSaveNewDividerRow(int dividerLineRowSpan, List<View> dynamicDividerRowHolder, boolean isDebts) {
        TableRow dividerLineRow = new TableRow(getActivity());
        int column = 0;
        TextView textView = addNewTextViewToRwo(dividerLineRow, null, column, new TableRow.LayoutParams(), dividerLineRowSpan, isDebts);
        textView.setHeight(onePixel);
        textView.setBackgroundColor(getResources().getColor(R.color.listDividerGrey));
        dynamicDividerRowHolder.add(dividerLineRow);
        return dividerLineRow;
    }

    private boolean isInScope(Collection<Participant> participants, Entry<Participant, Debts> entry,
                              Entry<Participant, Amount> debt) {
        return participants.contains(entry.getKey()) || participants.contains(debt.getKey());
    }

    private Trip getTripLoaded(TrickyTripperApp app) {
        return app.getTripController().getTripLoaded();
    }

    private TextView addNewTextViewToRow(TableRow target, String valueForDisplay, int column) {
        return addNewTextViewToRow(target, valueForDisplay, column, new TableRow.LayoutParams());
    }

    private TextView addNewTextViewToRow(TableRow target, String valueForDisplay, int column, float widthPercentage) {
        TableRow.LayoutParams columnRowParams = new TableRow.LayoutParams();
        columnRowParams.weight = widthPercentage;
        return addNewTextViewToRow(target, valueForDisplay, column, columnRowParams);
    }

    private TextView addNewTextViewToRow(TableRow target, String valueForDisplay, int column,
                                         TableRow.LayoutParams columnRowParams) {
        int span = 1;
        return addNewTextViewToRwo(target, valueForDisplay, column, columnRowParams, span, false);
    }

    private TextView addNewTextViewToRwo(TableRow target, String valueForDisplay, int column, TableRow.LayoutParams columnRowParams, int span, boolean isDebts) {
        TableRow.LayoutParams columnRowParamsHere = columnRowParams;
        TextView textView;
        textView = new TextView(getActivity());
        textView.setText(valueForDisplay);
        columnRowParamsHere.column = column;
        columnRowParamsHere.span = span;
        if(isDebts){
            columnRowParamsHere.weight = 1;
        }
        target.addView(textView, columnRowParams);
        return textView;
    }

    private void removeDynamicRows(TableLayout tableLayoutDebts, TableLayout tableLayoutSpending) {
        if (!dynamicDividerRowsOwingDebtsRows.isEmpty()) {
            for (View dynamicRow : dynamicDividerRowsOwingDebtsRows) {
                tableLayoutDebts.removeView(dynamicRow);
            }
        }
        if (!dynamicDividerRowsSpending.isEmpty()) {
            for (View dynamicRow : dynamicDividerRowsSpending) {
                tableLayoutSpending.removeView(dynamicRow);
            }
        }
        if (!dynamicOwingDebtsRows.isEmpty()) {
            for (View dynamicRow : dynamicOwingDebtsRows) {
                tableLayoutDebts.removeView(dynamicRow);
            }
        }
        if (!dynamicSpendingRows.isEmpty()) {
            for (View dynamicRow : dynamicSpendingRows) {
                tableLayoutSpending.removeView(dynamicRow);
            }
        }
    }

}
