package de.koelle.christian.trickytripper.dataaccess.impl.daos;

import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

public class ParticipantTable {
    public static final String TABLE_NAME = "participant";

    public static class ParticipantColumns implements BaseColumns {
        public static final String TRIP_ID = "trip_id";
        public static final String NAME = "name";
        public static final String ACTIVE = "active";
    }

    public static void onCreate(SQLiteDatabase db) {
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE " + ParticipantTable.TABLE_NAME + " (");

        sb.append(BaseColumns._ID + " INTEGER PRIMARY KEY, ");
        sb.append(ParticipantColumns.TRIP_ID + " INTEGER NOT NULL, ");
        sb.append(ParticipantColumns.NAME + " TEXT NOT NULL, ");
        sb.append(ParticipantColumns.ACTIVE + " INTEGER NOT NULL, ");

        sb.append("FOREIGN KEY("
                + ParticipantColumns.TRIP_ID + ") REFERENCES "
                + TripTable.TABLE_NAME + "("
                + BaseColumns._ID + ")");
        sb.append(");");
        db.execSQL(sb.toString());
    }

    public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Currently nothing to do here.
    }
}
