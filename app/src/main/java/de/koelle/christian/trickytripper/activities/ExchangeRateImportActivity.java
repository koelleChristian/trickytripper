package de.koelle.christian.trickytripper.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Currency;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import de.koelle.christian.common.abs.ActionBarSupport;
import de.koelle.christian.common.options.OptionConstraintsInflater;
import de.koelle.christian.common.utils.CurrencyUtil;
import de.koelle.christian.trickytripper.R;
import de.koelle.christian.trickytripper.TrickyTripperApp;
import de.koelle.christian.trickytripper.activitysupport.CurrencyViewSupport;
import de.koelle.christian.trickytripper.constants.Rc;
import de.koelle.christian.trickytripper.exchangerates.impl.AsyncExchangeRateHttpResolverGoogleImpl;
import de.koelle.christian.trickytripper.exchangerates.impl.ExchangeRateImporterImpl;
import de.koelle.christian.trickytripper.exchangerates.impl.ExchangeRateImporterResultCallback;
import de.koelle.christian.trickytripper.exchangerates.impl.ExchangeRateImporterResultContainer;
import de.koelle.christian.trickytripper.exchangerates.impl.ExchangeRateResultExtractorHttpGoogleImpl;
import de.koelle.christian.trickytripper.model.ImportSettings;
import de.koelle.christian.trickytripper.ui.model.RowObject;
import de.koelle.christian.trickytripper.ui.utils.UiViewUtils;

public class ExchangeRateImportActivity extends AppCompatActivity {

    private final Handler progressBarHandler = new Handler();
    private ListView listView;
    @SuppressWarnings("rawtypes")
    private ArrayAdapter<RowObject> adapter;
    private int progressBarStatus;
    private ImportSettings importSettings;
    private ProgressDialog progressBar;

    @SuppressWarnings("rawtypes")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.exchange_rate_import_view);

        listView = (ListView) findViewById(R.id.importExchangeRatesViewListViewCurrenciesForImport);
        List<Currency> allCurrenciesAlive = CurrencyUtil.getAllCurrenciesAlive();

        List<RowObject> spinnerObjects = CurrencyViewSupport.wrapCurrenciesInRowObject(
                allCurrenciesAlive, getResources());

        adapter = new ArrayAdapter<RowObject>(this, R.layout.general_checked_text_view, spinnerObjects);
        listView.setAdapter(adapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> list, View lv, int position,
                                    long id) {
                updateViewState();
            }
        });

        setIncomingSelection(allCurrenciesAlive);

        this.importSettings = getApp().getExchangeRateController().getImportSettingsUsedLast();

        bindWidgets();

        updateViewState();

        ActionBarSupport.addBackButton(this);
    }

    private void updateViewState() {
        final Set<Currency> currentSelection = getSelection();
        updateCurrentSelectionDisplay(currentSelection);
        supportInvalidateOptionsMenu();
    }

    private boolean atLeastTwoSelected() {
        return getSelection().size() >= 2;
    }

    private void setIncomingSelection(List<Currency> allCurrenciesAlive) {
        @SuppressWarnings("unchecked")
        ArrayList<Currency> incomingCurrencies = (ArrayList<Currency>) getIntent().getSerializableExtra(
                Rc.ACTIVITY_PARAM_IMPORT_EXCHANGE_RATES_IN_CURRENCY_LIST);
        if (incomingCurrencies != null && !incomingCurrencies.isEmpty()) {
            SparseBooleanArray selection = listView.getCheckedItemPositions();
            for (Currency c : incomingCurrencies) {
                int index = allCurrenciesAlive.indexOf(c);
                if (index >= 0) {
                    selection.put(index, true);
                }
            }
        }
    }

    private void bindWidgets() {
        CheckBox checkboxReplaceExisting = (CheckBox) findViewById(R.id.importExchangeRatesViewCheckboxReplaceExisting);
        checkboxReplaceExisting.setChecked(importSettings.isCreateNewRateOnValueChange());
        checkboxReplaceExisting.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                importSettings.setCreateNewRateOnValueChange(isChecked);
            }
        });

    }

    private void updateCurrentSelectionDisplay(Set<Currency> currencySelection) {

        final TextView textView = (TextView) findViewById(R.id.importExchangeRatesListViewLabelSelection);
        final StringBuilder builder = new StringBuilder()
                .append(getResources().getString(R.string.exchangeRateImportViewSelectionPrefix))
                .append(" ");

        if (currencySelection.size() == 0) {
            builder.append(0);
        } else if (currencySelection.size() > 3) {
            builder.append(currencySelection.size());
        } else {
            for (Iterator<Currency> it = currencySelection.iterator(); it.hasNext(); ) {
                builder.append(it.next().getCurrencyCode());
                if (it.hasNext()) {
                    builder.append(", ");
                }
            }
        }
        textView.setText(builder.toString());

    }

    public void importSelectedExchangeRates() {

        final Set<Currency> selectionResult = getSelection();

        int amountOfCurrenciesSelected = selectionResult.size();
        if (amountOfCurrenciesSelected <= 0) {
            return;
        }

        final int progressCeiling = CurrencyUtil.calcExpectedAmountOfExchangeRates(amountOfCurrenciesSelected);

        final ExchangeRateImporterImpl importer = new ExchangeRateImporterImpl();
        importer.setAsyncExchangeRateResolver(new AsyncExchangeRateHttpResolverGoogleImpl(this));
        importer.setExchangeRateResultExtractor(new ExchangeRateResultExtractorHttpGoogleImpl());

        progressBar = new ProgressDialog(this);
        progressBar.setMessage(getResources().getString(R.string.exchangeRateImportViewProgressBarMessage));
        progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressBar.setProgress(0);
        progressBar.setMax(progressCeiling);
        progressBar.setCancelable(false);
        progressBar.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                importer.cancelRunningRequests();
                dialog.dismiss();
                Toast.makeText(getApplicationContext(),
                        getResources().getString(R.string.exchangeRateImportViewCancelImportToast), Toast.LENGTH_SHORT)
                        .show();
            }
        });
        progressBar.show();

        progressBarStatus = 0;

        final Set<Currency> currenciesToBeLoaded = new LinkedHashSet<Currency>(selectionResult);

        new Thread(new Runnable() {
            public void run() {
                boolean importStarted = false;
                while (progressBarStatus < progressCeiling) {
                    if (!importStarted) {
                        importer.importExchangeRates(currenciesToBeLoaded, new ExchangeRateImporterResultCallback() {

                            public void deliverResult(ExchangeRateImporterResultContainer parameterObject) {
                                try {
                                    if (parameterObject.requestWasSuccess()) {
                                        try {
                                            getApp().getExchangeRateController().persistImportedExchangeRate(
                                                    parameterObject.exchangeRateResult,
                                                    !importSettings.isCreateNewRateOnValueChange());
                                        } catch (Throwable ex) {
                                            Log.e(Rc.LT_IO, "An imported record could not be persisted.", ex);
                                        }
                                    }
                                } finally {
                                    progressBarStatus++;

                                    progressBarHandler.post(new Runnable() {
                                        public void run() {
                                            progressBar.setProgress(progressBarStatus);
                                        }
                                    });
                                }
                            }
                        });
                        importStarted = true;
                    }

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                if (progressBarStatus >= progressCeiling) {

                    /* sleep, so that you can see the 100% */
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    progressBar.dismiss();
                    ExchangeRateImportActivity.this.runOnUiThread(new Runnable() {
                        public void run() {
                            getApp().getExchangeRateController().saveImportSettingsUsedLast(importSettings);
                            ExchangeRateImportActivity.this.finishHere();
                        }
                    });
                }
            }
        })
        .start();

    }

    private Set<Currency> getSelection() {
        final Set<Currency> selectionResult = UiViewUtils.getListSelection(listView, adapter
        );
        return selectionResult;
    }

    protected void finishHere() {
        setResult(Activity.RESULT_OK);
        finish();
    }

    private TrickyTripperApp getApp() {
        return ((TrickyTripperApp) getApplication());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return getApp().getMiscController().getOptionSupport().populateOptionsMenu(
                new OptionConstraintsInflater().activity(getMenuInflater()).menu(menu)
                        .options(new int[]{
                                R.id.option_accept,
                                R.id.option_help
                        }));
    }
    public boolean onPrepareOptionsMenu(Menu menu) {
        boolean canImport = atLeastTwoSelected();
        MenuItem item = menu.findItem(R.id.option_accept);
        item.setTitle(R.string.option_accept_execute_import);
        item.setEnabled(canImport);
        item.getIcon().setAlpha((canImport) ? 255 : 64);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.option_accept:
                importSelectedExchangeRates();
                return true;
            case R.id.option_help:
                getApp().getViewController().openHelp(getSupportFragmentManager());
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
