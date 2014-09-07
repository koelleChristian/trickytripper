package de.koelle.christian.trickytripper.dataaccess.impl.daos;

import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

public class ExchangeRatePrefTable {

    public static final String TABLE_NAME = "exchangeratepref";

    public static class ExchangeRatePrefColumns implements BaseColumns {
        public static final String EXCHANGE_RATE_ID = "exchange_rate_id";
        public static final String CURRENCY_FROM = "currency_from";
        public static final String CURRENCY_TO = "currency_to";
        public static final String DATE_LAST_USED = "date_last_used";
    }

    public static void onCreate(SQLiteDatabase db) {
        StringBuilder sb = new StringBuilder();

        sb.append("CREATE TABLE " + ExchangeRatePrefTable.TABLE_NAME + " (");

        sb.append(ExchangeRatePrefColumns.EXCHANGE_RATE_ID + " INTEGER NOT NULL, ");
        sb.append(ExchangeRatePrefColumns.CURRENCY_FROM + " TEXT NOT NULL, ");
        sb.append(ExchangeRatePrefColumns.CURRENCY_TO + " TEXT NOT NULL, ");
        sb.append(ExchangeRatePrefColumns.DATE_LAST_USED + " INTEGER NOT NULL, ");

        sb.append("FOREIGN KEY("
                + ExchangeRatePrefColumns.EXCHANGE_RATE_ID + ") REFERENCES "
                + ExchangeRateTable.TABLE_NAME + "("
                + BaseColumns._ID + "), ");

        sb.append("PRIMARY KEY ( "
                + ExchangeRatePrefColumns.CURRENCY_FROM + ", "
                + ExchangeRatePrefColumns.CURRENCY_TO + ", "
                + ExchangeRatePrefColumns.EXCHANGE_RATE_ID
                + ")");

        sb.append(");");

        db.execSQL(sb.toString());
    }

    public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion >= 4) {
            ExchangeRatePrefTable.onCreate(db);
        }
    }

}
