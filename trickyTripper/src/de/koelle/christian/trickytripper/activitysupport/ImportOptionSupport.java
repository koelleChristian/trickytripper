package de.koelle.christian.trickytripper.activitysupport;

import java.util.Currency;

import android.widget.Toast;
import de.koelle.christian.trickytripper.TrickyTripperApp;

public class ImportOptionSupport {
    private final TrickyTripperApp app;

    public ImportOptionSupport(TrickyTripperApp app) {
        this.app = app;
    }

    public boolean onOptionsItemSelected() {
        if (!app.isOnline()) {
            Toast.makeText(
                    app.getApplicationContext(), "The import of exchange rates requires an online connection.",
                    Toast.LENGTH_SHORT).show();
        }
        else {
            app.openImportExchangeRates(new Currency[0]);
        }
        return true;
    }

}
