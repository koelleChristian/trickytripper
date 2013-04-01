package de.koelle.christian.trickytripper.activitysupport;

import java.util.Currency;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;
import de.koelle.christian.trickytripper.controller.MiscController;
import de.koelle.christian.trickytripper.controller.ViewController;

public class ImportOptionSupport {
    private final ViewController viewCtrl;
    private final MiscController miscCtrl;
    private final Context context;

    public ImportOptionSupport(ViewController viewCtrl, MiscController miscCtrl, Context context) {
        this.viewCtrl = viewCtrl;
        this.miscCtrl = miscCtrl;
        this.context = context;
    }

    public boolean onOptionsItemSelected(Activity caller) {
        return onOptionsItemSelected(caller, new Currency[0]);
    }

    public boolean onOptionsItemSelected(Activity caller, Currency [] currenciesToBeSelected) {
        if (!miscCtrl.isOnline()) {
            Toast.makeText(
                    context, "The import of exchange rates requires an online connection.",
                    Toast.LENGTH_LONG).show();
        }
        else {
            viewCtrl.openImportExchangeRates(caller, currenciesToBeSelected);
        }
        return true;
    }

}
