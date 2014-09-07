package de.koelle.christian.trickytripper.dataaccess.impl.daos;

import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

public class RelPaymentParticipantTable {
    public static final String TABLE_NAME = "rel_payment_participant";

    public static class RelPaymentParticipantColumns {
        public static final String PAYMENT_ID = "payment_id";
        public static final String PARTICIPANT_ID = "participant_id";
        public static final String PAYER = "payer";
        public static final String AMOUNT_CURRENCY_CODE = "amount_currency_code";
        public static final String AMOUNT_VALUE = "amount_value";
    }

    public static void onCreate(SQLiteDatabase db) {
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE " + RelPaymentParticipantTable.TABLE_NAME + " (");

        sb.append(RelPaymentParticipantColumns.PAYMENT_ID + " INTEGER NOT NULL, ");
        sb.append(RelPaymentParticipantColumns.PARTICIPANT_ID + " INTEGER NOT NULL, ");
        sb.append(RelPaymentParticipantColumns.PAYER + " BOOLEAN NOT NULL, ");
        sb.append(RelPaymentParticipantColumns.AMOUNT_CURRENCY_CODE + " TEXT NOT NULL, ");
        sb.append(RelPaymentParticipantColumns.AMOUNT_VALUE + " DECIMAL(10,2) NOT NULL, ");

        sb.append("FOREIGN KEY("
                + RelPaymentParticipantColumns.PAYMENT_ID + ") REFERENCES "
                + PaymentTable.TABLE_NAME + "("
                + BaseColumns._ID + "), ");
        sb.append("FOREIGN KEY("
                + RelPaymentParticipantColumns.PARTICIPANT_ID + ") REFERENCES "
                + ParticipantTable.TABLE_NAME + "("
                + BaseColumns._ID + "), ");

        sb.append("PRIMARY KEY ( "
                + RelPaymentParticipantColumns.PAYMENT_ID + ", "
                + RelPaymentParticipantColumns.PARTICIPANT_ID + ", "
                + RelPaymentParticipantColumns.PAYER
                + ")");
        sb.append(");");
        db.execSQL(sb.toString());
    }

    public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Currently nothing to do here.
    }

}
