package de.koelle.christian.trickytripper.activities;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Currency;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import de.koelle.christian.trickytripper.R;
import de.koelle.christian.trickytripper.TrickyTripperApp;
import de.koelle.christian.trickytripper.model.ExchangeRate;
import de.koelle.christian.trickytripper.model.modelAdapter.ExchangeRateRowListAdapter;

public class ManageExchangeRatesActivity extends Activity {

    private ArrayAdapter<ExchangeRate> listAdapter;
    private final List<ExchangeRate> exchangeRateList = new ArrayList<ExchangeRate>();
    private ListView listView;
    private Comparator<ExchangeRate> comparator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manage_exchange_rates_view);

        TrickyTripperApp app = getApp();

        final Collator collator = app.getFktnController().getDefaultStringCollator();
        comparator = new Comparator<ExchangeRate>() {
            public int compare(ExchangeRate object1, ExchangeRate object2) {
                return collator.compare(object1.getSortString(), object2.getSortString());
            }

        };
        listView = (ListView) findViewById(R.id.manageExchangeRatesViewListViewRates);

        initListView(listView, app);
        addClickListener(listView, app);

        registerForContextMenu(listView);

    }

    private void initListView(ListView listView2, TrickyTripperApp app) {
        listAdapter = new ExchangeRateRowListAdapter(this, android.R.layout.simple_list_item_1,
                exchangeRateList);

        listView2.setAdapter(listAdapter);
        listView2.setChoiceMode(ListView.CHOICE_MODE_NONE);

        updateList(app.getExchangeRateService().getAllExchangeRates());
        listAdapter.sort(comparator);
    }

    void updateList(List<ExchangeRate> currentList) {
        listAdapter.clear();
        for (ExchangeRate rate : currentList) {
            listAdapter.add(rate);
            listAdapter.add(rate.cloneToInversion());
        }
        listAdapter.sort(comparator);
        listView.invalidateViews();
    }

    private void addClickListener(ListView listView2, TrickyTripperApp app) {
        // TODO Auto-generated method stub

    }

    private TrickyTripperApp getApp() {
        return ((TrickyTripperApp) getApplication());
    }

    public void importExchangeRates(View view) {
        getApp().getViewController().openImportExchangeRates(new Currency[0]);
    }

}
