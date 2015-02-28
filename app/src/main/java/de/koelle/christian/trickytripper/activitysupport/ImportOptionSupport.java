package de.koelle.christian.trickytripper.activitysupport;

import java.util.BitSet;
import java.util.Currency;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;
import de.koelle.christian.common.utils.CurrencyUtil;
import de.koelle.christian.trickytripper.R;
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

    public boolean onOptionsItemSelected(Activity caller, Currency[] currenciesToBeSelected) {
        if (!miscCtrl.isOnline()) {
            Toast.makeText(
                    context,
                    context.getResources().getString(
                            R.string.exchangeRateImportViewPreconToastRequiresOnlineConnection),
                    Toast.LENGTH_LONG).show();
            return true;
        }

        if (currenciesToBeSelected.length > 0) {
            BitSet aliveFlags = new BitSet();
            for (int i = 0; i < currenciesToBeSelected.length; i++) {
                aliveFlags.set(i, CurrencyUtil.isAlive(currenciesToBeSelected[i].getCurrencyCode()));
            }

            if (aliveFlags.cardinality() < 2) {
                StringBuilder builder = new StringBuilder()
                        .append(context.getResources().getString(R.string.exchangeRateImportViewPreconToastNotAlive1))
                        .append(" ");
                boolean oneAdded = false;
                for (int i = 0; i < currenciesToBeSelected.length; i++) {
                    if (!aliveFlags.get(i)) {
                        if (oneAdded) {
                            builder.append(", ");
                        }
                        builder.append(currenciesToBeSelected[i]);
                        oneAdded=true;
                    }
                    builder.append("\n");
                }
                builder.append(context.getResources().getString(R.string.exchangeRateImportViewPreconToastNotAlive2));
                Toast.makeText(context, builder.toString(), Toast.LENGTH_LONG).show();
                return true;
            }
        }

        viewCtrl.openImportExchangeRates(caller, currenciesToBeSelected);
        return true;
    }

}
