package de.koelle.christian.trickytripper.activitysupport;

import java.util.Currency;

import android.app.Activity;
import android.widget.Toast;
import de.koelle.christian.trickytripper.TrickyTripperApp;

public class ImportOptionSupport {
    private final TrickyTripperApp app;

    public ImportOptionSupport(TrickyTripperApp app) {
        this.app = app;
    }

    public boolean onOptionsItemSelected(Activity caller) {
        if (!app.isOnline()) {
            Toast.makeText(
                    app.getApplicationContext(), "The import of exchange rates requires an online connection.",
                    Toast.LENGTH_LONG).show();
        }
        else {
            app.openImportExchangeRates(caller, new Currency[0]);
        }
        return true;
    }

}
