package de.koelle.christian.trickytripper.dataaccess.impl.daos;

import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

public final class TripTable {

    public static final String TABLE_NAME = "trip";

    public static class TripColumns implements BaseColumns {
        public static final String NAME = "name";
        public static final String BASE_CURRENCY_CODE = "base_currency_code";
    }

    public static void onCreate(SQLiteDatabase db) {
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE " + TripTable.TABLE_NAME + " (");

        sb.append(BaseColumns._ID + " INTEGER PRIMARY KEY, ");
        sb.append(TripColumns.NAME + " TEXT UNIQUE NOT NULL, ");
        sb.append(TripColumns.BASE_CURRENCY_CODE + " TEXT NOT NULL");

        sb.append(");");
        db.execSQL(sb.toString());
    }

    public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // db.execSQL("DROP TABLE IF EXISTS " + TripTable.TABLE_NAME);
        // TripTable.onCreate(db);
    }
}
