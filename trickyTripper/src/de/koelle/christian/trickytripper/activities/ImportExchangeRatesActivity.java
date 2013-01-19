package de.koelle.christian.trickytripper.activities;

import java.util.ArrayList;
import java.util.Currency;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import de.koelle.christian.common.utils.CurrencyUtil;
import de.koelle.christian.trickytripper.R;
import de.koelle.christian.trickytripper.TrickyTripperApp;
import de.koelle.christian.trickytripper.activitysupport.CurrencyViewSupport;
import de.koelle.christian.trickytripper.exchangerates.impl.AsyncExchangeRateJsonResolverGoogleImpl;
import de.koelle.christian.trickytripper.exchangerates.impl.ExchangeRateImporterImpl;
import de.koelle.christian.trickytripper.exchangerates.impl.ExchangeRateImporterResultCallback;
import de.koelle.christian.trickytripper.exchangerates.impl.ExchangeRateImporterResultContainer;
import de.koelle.christian.trickytripper.exchangerates.impl.ExchangeRateResultExtractorGoogleImpl;
import de.koelle.christian.trickytripper.ui.model.RowObject;
import de.koelle.christian.trickytripper.ui.utils.UiViewUtils;

public class ImportExchangeRatesActivity extends Activity {

    private final List<Currency> currencies = new ArrayList<Currency>();
    private ListView listView;
    private ArrayAdapter<RowObject> adapter;

    private int progressBarStatus;
    private boolean replaceImportedRecordWhenAlreadyImported;
    private ProgressDialog progressBar;
    private final Handler progressBarHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.import_exchange_rates_view);

        listView = (ListView) findViewById(R.id.importExchangeRatesViewListViewCurrenciesForImport);

        List<RowObject> spinnerObjects = CurrencyViewSupport.wrapCurrenciesInRowObject(CurrencyUtil
                .getSuportedCurrencies(getResources()), getResources());

        adapter = new ArrayAdapter<RowObject>(this, R.layout.general_checked_text_view, spinnerObjects);
        listView.setAdapter(adapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

    }

    public void importExchangeRates(View view) {

        final Set<Currency> selectionResult = UiViewUtils.getListSelection(listView, adapter,
                Currency.getInstance("EUR"));

        int amoutOfCurrenciesSelected = selectionResult.size();
        if (amoutOfCurrenciesSelected <= 0) {
            return;
        }

        final int progressCeiling = CurrencyUtil.calcExpectedAmountOfExchangeRates(amoutOfCurrenciesSelected);

        final ExchangeRateImporterImpl importer = new ExchangeRateImporterImpl();
        importer.setAsyncExchangeRateJsonResolver(new AsyncExchangeRateJsonResolverGoogleImpl());
        importer.setExchangeRateResultExtractor(new ExchangeRateResultExtractorGoogleImpl());

        progressBar = new ProgressDialog(view.getContext());
        progressBar.setMessage("Importing exchange rates ...");
        progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressBar.setProgress(0);
        progressBar.setMax(progressCeiling);
        progressBar.setCancelable(false);
        progressBar.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Toast.makeText(getApplicationContext(), "Canceled TODO", Toast.LENGTH_SHORT).show();
            }
        });
        progressBar.show();

        progressBarStatus = 0;

        List<Currency> currencies = CurrencyUtil.getAllCurrenciesAlive();
        final Set<Currency> currenciesToBeLoaded = new LinkedHashSet<Currency>();
        for (int i = 0; i < amoutOfCurrenciesSelected; i++) {
            currenciesToBeLoaded.add(currencies.get(i));
        }

        new Thread(new Runnable() {
            public void run() {
                while (progressBarStatus < progressCeiling) {

                    importer.importExchangeRates(currenciesToBeLoaded, new ExchangeRateImporterResultCallback() {

                        public void deliverResult(ExchangeRateImporterResultContainer parameterObject) {
                            getApp().getExchangeRateController().persitImportedExchangeRate(
                                    parameterObject.exchangeRateResult, replaceImportedRecordWhenAlreadyImported);
                            progressBarStatus++;
                        }
                    });
                    try {
                        Thread.sleep(1000);
                    }
                    catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    progressBarHandler.post(new Runnable() {
                        public void run() {
                            progressBar.setProgress(progressBarStatus);
                        }
                    });
                }

                if (progressBarStatus >= progressCeiling) {

                    /* sleep 2 seconds, so that you can see the 100% */
                    try {
                        Thread.sleep(2000);
                    }
                    catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    progressBar.dismiss();
                    ImportExchangeRatesActivity.this.runOnUiThread(new Runnable() {
                        public void run() {
                            ImportExchangeRatesActivity.this.finish();
                        }
                    });
                }
            }
        }).start();

    }

    private TrickyTripperApp getApp() {
        return ((TrickyTripperApp) getApplication());
    }
}
