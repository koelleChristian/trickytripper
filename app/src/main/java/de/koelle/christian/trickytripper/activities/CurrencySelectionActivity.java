package de.koelle.christian.trickytripper.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseArray;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.TableLayout;
import android.widget.TextView;

import java.util.Currency;

import de.koelle.christian.common.utils.UiUtils;
import de.koelle.christian.trickytripper.R;
import de.koelle.christian.trickytripper.TrickyTripperApp;
import de.koelle.christian.trickytripper.constants.Rc;
import de.koelle.christian.trickytripper.model.HierarchicalCurrencyList;
import de.koelle.christian.trickytripper.model.modelAdapter.CurrencyExpandableListAdapter;
import de.koelle.christian.trickytripper.model.modelAdapter.CurrencyGroupNamingCallback;

public class CurrencySelectionActivity extends AppCompatActivity {

    private Currency currencyProvided;
    private int resultViewId;
    private Currency currencySelected;
    private CurrencySelectionMode mode;

    public enum CurrencySelectionMode {
        SELECT_FOR_EXCHANGE_CALCULATION,
        SELECT_EXCHANGE_RATE_LEFT,
        SELECT_EXCHANGE_RATE_RIGHT,
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.currency_selection_view);

        readAndSetInput(getIntent());
        updateInstructionLabels();

        initList();
    }

    private void initList() {

        ExpandableListView list = (ExpandableListView) findViewById(R.id.currencySelectionViewList);

        HierarchicalCurrencyList lis;

        if (CurrencySelectionMode.SELECT_FOR_EXCHANGE_CALCULATION.equals(mode)) {
            lis = ((TrickyTripperApp) getApplication()).getMiscController().getAllCurrenciesForTarget(currencyProvided);
        }
        else {
            lis = ((TrickyTripperApp) getApplication()).getMiscController().getAllCurrencies();
        }

        final SparseArray<String> groupLabels = localizeGroupLabels();

        final CurrencyExpandableListAdapter adapter = new CurrencyExpandableListAdapter(this,
                lis.createListWithAllLists(), new CurrencyGroupNamingCallback() {

                    public String getGroupDescription(int groupPosition) {
                        return groupLabels.get(groupPosition);
                    }
                });

        list.setAdapter(adapter);
        list.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                currencySelected = adapter.getRecordByVisualId(groupPosition, childPosition).getCurrency();
                prepareResultAndFinish();
                return true;
            }
        });
        
        list.expandGroup(0);
    }

    private SparseArray<String> localizeGroupLabels() {
        final SparseArray<String> groupLabels = new SparseArray<>();

        groupLabels.put(HierarchicalCurrencyList.GROUP_POS_ID_USED_MATCHING,
                getResources().getString(R.string.currency_selection_view_instruction_group_label_used_and_matching));

        groupLabels.put(
                HierarchicalCurrencyList.GROUP_POS_ID_IN_RATES_MATCHING,
                getResources().getString(
                        R.string.currency_selection_view_instruction_group_label_used_in_rates_matching));

        groupLabels.put(HierarchicalCurrencyList.GROUP_POS_ID_USED_UNMATCHED,
                getResources()
                        .getString(R.string.currency_selection_view_instruction_group_label_used_unmatched));

        groupLabels.put(
                HierarchicalCurrencyList.GROUP_POS_ID_IN_RATES_UNMATCHED,
                getResources().getString(
                        R.string.currency_selection_view_instruction_group_label_used_in_rates_unmatched));

        groupLabels.put(HierarchicalCurrencyList.GROUP_POS_ID_TRIPS,
                getResources().getString(R.string.currency_selection_view_instruction_group_label_in_trips));

        groupLabels.put(HierarchicalCurrencyList.GROUP_POS_ID_ELSE,
                getResources().getString(R.string.currency_selection_view_instruction_group_label_else));
        return groupLabels;
    }

    private void updateInstructionLabels() {
        TextView instructionsMoneyExchangeCalculation = (TextView) findViewById(R.id.currencySelectionViewLabelInstrExchangeRateCalculation);
        TextView instructionsNewExchangeRate = (TextView) findViewById(R.id.currencySelectionViewLabelInstrNewExchangeRate);
        TableLayout instructionsNewExchangeRate2 = (TableLayout) findViewById(R.id.currencySelectionViewLabelInstrNewExchangeRateTableLayout);

        String currencyCodeProvided = currencyProvided.getCurrencyCode();

        if (CurrencySelectionMode.SELECT_FOR_EXCHANGE_CALCULATION.equals(mode)) {
            UiUtils.setViewVisibility(instructionsMoneyExchangeCalculation, true);
            UiUtils.setViewVisibility(instructionsNewExchangeRate, false);
            UiUtils.setViewVisibility(instructionsNewExchangeRate2, false);

            StringBuilder instructions = new StringBuilder()
                    .append(getResources().getString(R.string.currency_selection_view_instruction_exchange_rate_calc))
                    .append(" ")
                    .append(currencyCodeProvided)
                    .append(".");
            instructionsMoneyExchangeCalculation.setText(instructions.toString());
        }
        else {
            UiUtils.setViewVisibility(instructionsMoneyExchangeCalculation, false);
            UiUtils.setViewVisibility(instructionsNewExchangeRate, true);
            UiUtils.setViewVisibility(instructionsNewExchangeRate2, true);
            TextView currencyLeftHand = (TextView) findViewById(R.id.currencySelectionViewLabelInstrNewExchangeRateValueLeft);
            TextView currencyRightHand = (TextView) findViewById(R.id.currencySelectionViewLabelInstrNewExchangeRateValueRight);

            CharSequence toBeSelectedTxt = getResources().getText(
                    R.string.currency_selection_view_instruction_new_exchange_rate_label_to_be_selected);

            if (CurrencySelectionMode.SELECT_EXCHANGE_RATE_LEFT.equals(mode)) {
                currencyLeftHand.setText(toBeSelectedTxt);
                currencyRightHand.setText(currencyCodeProvided);
            }
            else {
                currencyLeftHand.setText(currencyCodeProvided);
                currencyRightHand.setText(toBeSelectedTxt);
            }
        }

    }

    private void prepareResultAndFinish() {
        Intent resultIntent = new Intent();
        resultIntent.putExtra(Rc.ACTIVITY_PARAM_CURRENCY_SELECTION_OUT_CURRENCY, currencySelected);
        resultIntent.putExtra(Rc.ACTIVITY_PARAM_CURRENCY_SELECTION_OUT_VIEW_ID, resultViewId);
        if (!CurrencySelectionMode.SELECT_FOR_EXCHANGE_CALCULATION.equals(mode)) {
            resultIntent.putExtra(Rc.ACTIVITY_PARAM_CURRENCY_SELECTION_OUT_WAS_LEFT_NOT_RIGHT,
                    CurrencySelectionMode.SELECT_EXCHANGE_RATE_LEFT.equals(mode));
        }
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    private void readAndSetInput(Intent intent) {
        currencyProvided = (Currency) intent.getSerializableExtra(Rc.ACTIVITY_PARAM_CURRENCY_SELECTION_IN_CURRENCY);
        resultViewId = intent.getIntExtra(Rc.ACTIVITY_PARAM_CURRENCY_SELECTION_IN_VIEW_ID, -1);
        mode = (CurrencySelectionMode) intent.getSerializableExtra(Rc.ACTIVITY_PARAM_CURRENCY_SELECTION_IN_MODE);
    }
}
