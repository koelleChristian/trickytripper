package de.koelle.christian.trickytripper.activitysupport;

import java.util.Currency;
import java.util.Set;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Handler;
import android.os.Message;
import de.koelle.christian.trickytripper.R;
import de.koelle.christian.trickytripper.controller.ExchangeRateController;
import de.koelle.christian.trickytripper.exchangerates.ExchangeRateImporter;

public class ExchangeRateImportExecutor {

    private final ExchangeRateImporter exchangeRateImporter;
    private final ExchangeRateController exchangeRateService;
    private ProgressDialog progressDialog;
    private static Handler progressResultHandler;

    private ExchangeRateImportExecutor(ExchangeRateImporter exchangeRateImporter,
            ExchangeRateController exchangeRateService) {
        super();
        this.exchangeRateImporter = exchangeRateImporter;
        this.exchangeRateService = exchangeRateService;
    }

    public void doImport(final Activity caller, final Set<Currency> currencies) {
        if (progressResultHandler == null) {
            progressResultHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    if (isProgressDialogShowing()) {
                        progressDialog.dismiss();
                    }
                }

            };
        }

        progressDialog = ProgressDialog.show(caller, caller.getResources()
                .getString(R.string.save2SdReceiverProgressHeading), "Bla bla Bla", true, false);

        Runnable runnable = new Runnable() {
            public void run() {
                // List<ExchangeRate> importedExchangeRates =
                // exchangeRateImporter.importExchangeRates(currencies);
                // exchangeRateService.persistExchangeRates(importedExchangeRates);
                // progressResultHandler.sendEmptyMessage(0);
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();

    }

    public void shutdown() {
        if (isProgressDialogShowing()) {
            progressDialog.dismiss();
        }
    }

    private boolean isProgressDialogShowing() {
        return progressDialog != null && progressDialog.isShowing();
    }

}
