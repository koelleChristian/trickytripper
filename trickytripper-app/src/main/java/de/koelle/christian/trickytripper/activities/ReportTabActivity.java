package de.koelle.christian.trickytripper.activities;

import java.util.*;
import java.util.Map.Entry;

import android.os.Bundle;
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

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import de.koelle.christian.common.options.OptionContraintsAbs;
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
import de.koelle.christian.trickytripper.ui.utils.PrepareOptionsSupport;

public class ReportTabActivity extends SherlockFragment {

    private final static int PADDING = 3;

    private final List<View> dynamicSpendingRows = new ArrayList<View>();
    private final List<View> dynamicOwingDebtsRows = new ArrayList<View>();

    private List<Participant> participantsInSpinner;

    private View view;

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        PrepareOptionsSupport.prepareMajorTabOptions(menu, getApp(), false);
    }

    @Override
    public void onResume() {
        super.onResume();
        createPanel(view);
        getSherlockActivity().invalidateOptionsMenu();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getApp().getMiscController().getOptionSupport().populateOptionsMenu(
                new OptionContraintsAbs().activity(inflater).menu(menu)
                        .options(new int[]{
                                R.id.option_create_participant,
                                R.id.option_help,
                                R.id.option_preferences,
                                R.id.option_export
                        }));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.option_help:
                getApp().getViewController().openHelp(getFragmentManager());
                return true;
            case R.id.option_export:
                getApp().getViewController().openExport();
                return true;
            case R.id.option_preferences:
                getApp().getViewController().openSettings();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        setHasOptionsMenu(true);

        view = inflater.inflate(R.layout.report_tab_view, container, false);
        //createPanel(view);
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
            if (pInList == null) {
                continue;
            } else if (pInList.equals(p)) {
                return i;
            }
        }
        return 0;
    }

    private TrickyTripperApp getApp() {
        final TrickyTripperApp app = ((TrickyTripperApp) getActivity().getApplication());
        return app;
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
                    .getTotalSpendings(), true, true, true);
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
            textView.setPadding(PADDING, PADDING, 0, PADDING);

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
                    textView.setPadding(PADDING, PADDING, 0, PADDING);

                    this.dynamicSpendingRows.add(newRow);
                    newRows.put(categoryDisplayName, newRow);
                }

            }
            for (Entry<String, View> entry : newRows.entrySet()) {
                tableLayout.addView(entry.getValue());

            }
        }

    }

    private void addDynamicDebtRows(TrickyTripperApp app, Participant participantSelected, Locale locale, TableLayout tableLayoutDebts) {

        TableRow headingDebtsSubheading = (TableRow) view.findViewById(R.id.reportViewTableLayoutDebtsHeading2);
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

        int tenDpi = UiUtils.dpi2px(getResources(), 10);
        int fifteenDpi = UiUtils.dpi2px(getResources(), 15);

        for (Iterator<Entry<Participant, Debts>> it = getTripLoaded(app).getDebts().entrySet().iterator();it.hasNext();) {
            Entry<Participant, Debts> entry = it.next();
            Debts debts = entry.getValue();
            if (debts != null && debts.getLoanerToDepts() != null) {
                TableRow newRow;
                TextView textView;
                String value;
                int column;


                for (Iterator<Entry<Participant, Amount>> itInternal = debts.getLoanerToDepts().entrySet().iterator(); itInternal.hasNext();) {
                    Entry<Participant, Amount> debt = itInternal.next();

                    int bottomPadding = (it.hasNext() || itInternal.hasNext())? fifteenDpi : PADDING;
                    if (isInScope(involvedParticipants, entry, debt)) {
                        areThereDebtsToBeDisplayed = true;

                        newRow = new TableRow(getActivity());

                        value = entry.getKey().getName();
                        column = 0;
                        textView = addNewTextViewToRow(newRow, value, column, 0.35f);
                        textView.setPadding(tenDpi, PADDING, PADDING, bottomPadding);
                        textView.setWidth(0);


                        value = debt.getKey().getName();
                        column = 1;
                        textView = addNewTextViewToRow(newRow, value, column, 0.35f);
                        textView.setPadding(PADDING, PADDING, PADDING, bottomPadding);
                        textView.setWidth(0);


                        value = AmountViewUtils.getAmountString(locale, debt.getValue(), true, true, true);
                        column = 2;
                        textView = addNewTextViewToRow(newRow, value, column, 0.30f);
                        textView.setGravity(Gravity.RIGHT);

                        textView.setPadding(0, 0, 0, bottomPadding);
                        textView.setWidth(0);


                        this.dynamicOwingDebtsRows.add(newRow);
                        newRows.put(entry.getKey().getName() + debt.getKey().getName(), newRow);
                    }
                }
            }
        }
        for (Entry<String, View> entry : newRows.entrySet()) {
            tableLayoutDebts.addView(entry.getValue());
        }
        UiUtils.setViewVisibility(headingDebtsSubheading, areThereDebtsToBeDisplayed);
        UiUtils.setViewVisibility(headingNoDebts, !areThereDebtsToBeDisplayed);
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
        columnRowParams.weight=widthPercentage;
        return addNewTextViewToRow(target, valueForDisplay, column, columnRowParams);
    }
    private TextView addNewTextViewToRow(TableRow target, String valueForDisplay, int column,
                                         TableRow.LayoutParams columnRowParams) {
        TableRow.LayoutParams columnRowParamsHere = columnRowParams;
        TextView textView;
        textView = new TextView(getActivity());
        textView.setText(valueForDisplay);
        textView.setPadding(UiUtils.dpi2px(getResources(), 10), PADDING, PADDING, PADDING);
        columnRowParamsHere.column = column;

        target.addView(textView, columnRowParams);
        return textView;
    }

    private void removeDynamicRows(TableLayout tableLayoutDebts, TableLayout tableLayoutSpending) {
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
