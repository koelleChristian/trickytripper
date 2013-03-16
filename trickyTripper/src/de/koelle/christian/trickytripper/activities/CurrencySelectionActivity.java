package de.koelle.christian.trickytripper.activities;

import java.util.Currency;

import android.app.ExpandableListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import de.koelle.christian.trickytripper.TrickyTripperApp;
import de.koelle.christian.trickytripper.model.HierarchicalCurrencyList;
import de.koelle.christian.trickytripper.model.modelAdapter.CurrencyExpandableListAdapter;

public class CurrencySelectionActivity extends ExpandableListActivity {

    private Currency targetCurrency;
    private Currency currencySelected;

    private ExpandableListAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        HierarchicalCurrencyList lis = ((TrickyTripperApp) getApplication()).getMiscController().getAllCurrencies();

        mAdapter = new CurrencyExpandableListAdapter(this, lis);
        setListAdapter(mAdapter);

        getExpandableListView().setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                System.out.println("group=" + groupPosition + " child=" + childPosition);
                return true;
            }
        });
    }
}
