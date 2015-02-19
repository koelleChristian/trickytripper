package de.koelle.christian.trickytripper.dataaccess.impl;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.Currency;

import de.koelle.christian.trickytripper.R;
import de.koelle.christian.trickytripper.apputils.PrefWriterReaderUtils;
import de.koelle.christian.trickytripper.constants.Rc;
import de.koelle.christian.trickytripper.dataaccess.impl.daos.ExchangeRatePrefTable;
import de.koelle.christian.trickytripper.dataaccess.impl.daos.ExchangeRateTable;
import de.koelle.christian.trickytripper.dataaccess.impl.daos.ParticipantTable;
import de.koelle.christian.trickytripper.dataaccess.impl.daos.PaymentTable;
import de.koelle.christian.trickytripper.dataaccess.impl.daos.RelPaymentParticipantTable;
import de.koelle.christian.trickytripper.dataaccess.impl.daos.TripDao;
import de.koelle.christian.trickytripper.dataaccess.impl.daos.TripTable;
import de.koelle.christian.trickytripper.factories.ModelFactory;
import de.koelle.christian.trickytripper.model.Trip;

public class OpenHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 4;

    private final Context context;

    public OpenHelper(final Context context) {
        super(context, DataConstants.DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onOpen(final SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            // versions of SQLite older than 3.6.19 don't support foreign keys
            // and neither do any version compiled with SQLITE_OMIT_FOREIGN_KEY
            // http://www.sqlite.org/foreignkeys.html#fk_enable
            //
            // make sure foreign key support is turned on if it's there (should
            // be already, just a double-checker)
            db.execSQL("PRAGMA foreign_keys=ON;");

            // then we check to make sure they're on
            // (if this returns no data they aren't even available, so we
            // shouldn't even TRY to use them)
            Cursor c = db.rawQuery("PRAGMA foreign_keys", null);
            if (c.moveToFirst()) {
                @SuppressWarnings("UnusedAssignment") int result = c.getInt(0);
                if (Rc.debugOn) {
                    Log.d(Rc.LT, "SQLite foreign key support (1 is on, 0 is off): " + result);
                }
            }
            else {
                // could use this approach in onCreate, and not rely on foreign
                // keys it not available, etc.
                if (Rc.debugOn) {
                    Log.d(Rc.LT, "SQLite foreign key support NOT AVAILABLE");
                }
                // if you had to here you could fall back to triggers
            }
            if (!c.isClosed()) {
                c.close();
            }
        }
    }

    @Override
    public void onCreate(final SQLiteDatabase db) {
        if (Rc.debugOn) {
            Log.d(Rc.LT, "DataHelper.OpenHelper onCreate creating database " + DataConstants.DATABASE_NAME);
        }
        TripTable.onCreate(db);
        ParticipantTable.onCreate(db);
        PaymentTable.onCreate(db);
        RelPaymentParticipantTable.onCreate(db);
        ExchangeRateTable.onCreate(db);
        ExchangeRatePrefTable.onCreate(db);

        /* ====== Initial records ====== */
        TripDao tripDao = new TripDao(db);
        Resources resources = context.getResources();
        SharedPreferences sharedPreferences = context.getSharedPreferences(Rc.PREFS_NAME_ID, Rc.PREFS_MODE);
        Currency baseCurrency = PrefWriterReaderUtils.loadDefaultCurrency(
                sharedPreferences, resources);
        Trip initialTrip = ModelFactory.createTrip(baseCurrency,
                resources.getString(R.string.initial_data_trip_name));
        tripDao.create(initialTrip);

    }

    @Override
    public void onUpgrade(final SQLiteDatabase db, final int oldVersion, final int newVersion) {
        Log
                .i(Rc.LT, "SQLiteOpenHelper onUpgrade - oldVersion:" + oldVersion + " newVersion:"
                        + newVersion);

        ExchangeRatePrefTable.onUpgrade(db, oldVersion, newVersion);
        ExchangeRateTable.onUpgrade(db, oldVersion, newVersion);

        RelPaymentParticipantTable.onUpgrade(db, oldVersion, newVersion);
        PaymentTable.onUpgrade(db, oldVersion, newVersion);
        ParticipantTable.onUpgrade(db, oldVersion, newVersion);
        TripTable.onUpgrade(db, oldVersion, newVersion);
    }
}
