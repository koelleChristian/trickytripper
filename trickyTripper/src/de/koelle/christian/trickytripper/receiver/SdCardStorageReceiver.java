package de.koelle.christian.trickytripper.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class SdCardStorageReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        System.out.println("onReceive: intent=" + intent);

        // Intent stockService =
        // new Intent(context, .class);
        // context.startService(stockService);
    }
}
