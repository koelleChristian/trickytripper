package de.koelle.christian.trickytripper.activities;

import java.util.Currency;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import de.koelle.christian.common.abs.ActionBarSupport;
import de.koelle.christian.common.options.OptionContraintsAbs;
import de.koelle.christian.common.utils.CurrencyUtil;
import de.koelle.christian.trickytripper.R;
import de.koelle.christian.trickytripper.TrickyTripperApp;
import de.koelle.christian.trickytripper.activitysupport.CurrencyViewSupport;
import de.koelle.christian.trickytripper.activitysupport.PopupFactory;
import de.koelle.christian.trickytripper.constants.Rc;
import de.koelle.christian.trickytripper.constants.Rd;
import de.koelle.christian.trickytripper.exchangerates.impl.AsyncExchangeRateJsonResolverGoogleImpl;
import de.koelle.christian.trickytripper.exchangerates.impl.ExchangeRateImporterImpl;
import de.koelle.christian.trickytripper.exchangerates.impl.ExchangeRateImporterResultCallback;
import de.koelle.christian.trickytripper.exchangerates.impl.ExchangeRateImporterResultContainer;
import de.koelle.christian.trickytripper.exchangerates.impl.ExchangeRateResultExtractorGoogleImpl;
import de.koelle.christian.trickytripper.model.ExchangeRate;
import de.koelle.christian.trickytripper.model.ImportSettings;
import de.koelle.christian.trickytripper.ui.model.RowObject;
import de.koelle.christian.trickytripper.ui.utils.UiViewUtils;

public class ImportExchangeRatesActivity extends SherlockActivity {

    private ListView listView;
    @SuppressWarnings("rawtypes")
    private ArrayAdapter<RowObject> adapter;

    private int progressBarStatus;
    private ImportSettings importSettings;
    private ProgressDialog progressBar;
    private final Handler progressBarHandler = new Handler();

    @SuppressWarnings("rawtypes")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.import_exchange_rates_view);

        listView = (ListView) findViewById(R.id.importExchangeRatesViewListViewCurrenciesForImport);
        List<RowObject> spinnerObjects = CurrencyViewSupport.wrapCurrenciesInRowObject(
                CurrencyUtil.getAllCurrenciesAlive(), getResources());

        adapter = new ArrayAdapter<RowObject>(this, R.layout.general_checked_text_view, spinnerObjects);
        listView.setAdapter(adapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        this.importSettings = getApp().getExchangeRateController().getImportSettingsUsedLast();
        bindWidgets();
        
        ActionBarSupport.addBackButton(this);

    }

    private void bindWidgets() {
        CheckBox checkboxReplaceExisting = (CheckBox) findViewById(R.id.importExchangeRatesViewCheckboxReplaceExisting);
        checkboxReplaceExisting.setChecked(importSettings.isReplaceImportedRecordWhenAlreadyImported());
        checkboxReplaceExisting.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                importSettings.setReplaceImportedRecordWhenAlreadyImported(isChecked);
            }
        });
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
                                            getApp().getExchangeRateController().persitImportedExchangeRate(
                                                    parameterObject.exchangeRateResult,
                                                    importSettings.isReplaceImportedRecordWhenAlreadyImported());
                                        }
                                        catch (Throwable ex) {
                                            Log.e(Rc.LT_IO, "An imported record could not be persisted.", ex);
                                        }
                                    }
                                }
                                finally {
                                    progressBarStatus++;
                                }
                            }
                        });
                        importStarted = true;
                    }

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
                            getApp().getExchangeRateController().saveImportSettingsUsedLast(importSettings);
                            ImportExchangeRatesActivity.this.finishHere();
                        }
                    });
                }
            }
        }).start();

    }

    protected void finishHere() {
        setResult(Activity.RESULT_OK);
        finish();
    }

    private TrickyTripperApp getApp() {
        return ((TrickyTripperApp) getApplication());
    }
    /* ============== Options Shit [BGN] ============== */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return getApp().getMiscController().getOptionSupport().populateOptionsMenu(
                new OptionContraintsAbs().activity(getSupportMenuInflater()).menu(menu)
                        .options(new int[] {
                                R.id.option_help
                        }));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.option_help:
            showDialog(Rd.DIALOG_HELP);
            return true;
        case android.R.id.home:
            onBackPressed();
            return true;                  
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    /* ============== Options Shit [END] ============== */
    /* ============== Dialog Shit [BGN] ============== */

    @Override
    protected Dialog onCreateDialog(int id, Bundle args) {
        Dialog dialog;
        switch (id) {
        case Rd.DIALOG_HELP:
            dialog = PopupFactory.createHelpDialog(this, getApp().getMiscController(), Rd.DIALOG_HELP);
            break;
        default:
            dialog = null;
        }

        return dialog;
    }

    @Override
    protected void onPrepareDialog(int id, Dialog dialog, final Bundle args) {
        switch (id) {
        case Rd.DIALOG_HELP:
            // intentionally blank
            break;
        default:
            dialog = null;
        }
        super.onPrepareDialog(id, dialog, args);
    }

    /* ============== Dialog Shit [END] ============== */
}
