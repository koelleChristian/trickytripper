package de.koelle.christian.trickytripper.dataaccess.impl.daos;

import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

public class ExchangeRateTable {

    public static final String TABLE_NAME = "exchangerate";

    public static class ExchangeRateColumns implements BaseColumns {
        public static final String CURRENCY_FROM = "currency_from";
        public static final String CURRENCY_TO = "currency_to";
        public static final String RATE = "rate";
        public static final String DESCRIPTION = "description";
        public static final String DATE_CREATE = "date_update";
        public static final String DATE_UPDATE = "date_create";
        public static final String IMPORT_ORIGIN = "import_origin";
    }

    public static void onCreate(SQLiteDatabase db) {
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE " + ExchangeRateTable.TABLE_NAME + " (");

        sb.append(BaseColumns._ID + " INTEGER PRIMARY KEY, ");
        sb.append(ExchangeRateColumns.CURRENCY_FROM + " TEXT NOT NULL, ");
        sb.append(ExchangeRateColumns.CURRENCY_TO + " TEXT NOT NULL, ");
        sb.append(ExchangeRateColumns.RATE + " DECIMAL(10,10) NOT NULL, ");
        sb.append(ExchangeRateColumns.DESCRIPTION + " TEXT, ");
        sb.append(ExchangeRateColumns.DATE_CREATE + " INTEGER NOT NULL, ");
        sb.append(ExchangeRateColumns.DATE_UPDATE + " INTEGER NOT NULL, ");
        sb.append(ExchangeRateColumns.IMPORT_ORIGIN + "  INTEGER NOT NULL"); // enum
        sb.append(");");
        db.execSQL(sb.toString());
    }

    public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion >= 4) {
            ExchangeRateTable.onCreate(db);
        }
    }

}
