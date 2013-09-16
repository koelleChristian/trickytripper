package de.koelle.christian.trickytripper.dataaccess.impl.daos;

import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

public class PaymentTable {
    public static final String TABLE_NAME = "payment";

    public static class PaymentColumns implements BaseColumns {
        public static final String TRIP_ID = "trip_id";
        public static final String DESCRIPTION = "description";
        public static final String CATEGORY = "category";
        public static final String DATE = "date";
    }

    public static void onCreate(SQLiteDatabase db) {
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE " + PaymentTable.TABLE_NAME + " (");

        sb.append(BaseColumns._ID + " INTEGER PRIMARY KEY, ");
        sb.append(PaymentColumns.TRIP_ID + " INTEGER NOT NULL, ");
        sb.append(PaymentColumns.DESCRIPTION + " TEXT, ");
        sb.append(PaymentColumns.CATEGORY + " INTEGER NOT NULL, ");
        sb.append(PaymentColumns.DATE + " INTEGER NOT NULL, ");


        sb.append("FOREIGN KEY("
                + PaymentColumns.TRIP_ID + ") REFERENCES "
                + TripTable.TABLE_NAME + "("
                + BaseColumns._ID + ")");

        sb.append(");");
        db.execSQL(sb.toString());
    }

    public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
   /*     if (oldVersion == newVersion) {
            return;
        } else if (oldVersion == 4) {
            addOrderIdColumn(db);
        } else {
            onUpgrade(db, oldVersion + 1, newVersion);
        }
    }

    private static void addOrderIdColumn(SQLiteDatabase db) {
        StringBuffer sb = new StringBuffer();
        sb.append("ALTER TABLE ");
        sb.append(PaymentTable.TABLE_NAME);
        sb.append(" ADD ");
        sb.append(PaymentColumns.ORDER_ID + " INTEGER NOT NULL;");
        db.execSQL(sb.toString());
    }
    */
    }
}
