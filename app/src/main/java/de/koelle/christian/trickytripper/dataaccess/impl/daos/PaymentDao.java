package de.koelle.christian.trickytripper.dataaccess.impl.daos;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.provider.BaseColumns;
import android.util.Log;

import java.util.ArrayList;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.koelle.christian.common.utils.ConversionUtils;
import de.koelle.christian.common.utils.NumberUtils;
import de.koelle.christian.trickytripper.constants.Rc;
import de.koelle.christian.trickytripper.dataaccess.impl.daos.PaymentTable.PaymentColumns;
import de.koelle.christian.trickytripper.dataaccess.impl.daos.RelPaymentParticipantTable.RelPaymentParticipantColumns;
import de.koelle.christian.trickytripper.dataaccess.impl.tecbeans.PaymentParticipantRelationKey;
import de.koelle.christian.trickytripper.dataaccess.impl.tecbeans.PaymentReference;
import de.koelle.christian.trickytripper.model.Amount;
import de.koelle.christian.trickytripper.model.PaymentCategory;

public class PaymentDao {

    private static final String INSERT =
            "insert into " + PaymentTable.TABLE_NAME + "("
                    + PaymentColumns.TRIP_ID + ", "
                    + PaymentColumns.DESCRIPTION + ", "
                    + PaymentColumns.CATEGORY + ", "
                    + PaymentColumns.DATE
                    + ") values (?, ?, ?, ?)";

    private static final String INSERT_RELATION =
            "insert into " + RelPaymentParticipantTable.TABLE_NAME + "("
                    + RelPaymentParticipantColumns.PAYMENT_ID + ", "
                    + RelPaymentParticipantColumns.PARTICIPANT_ID + ", "
                    + RelPaymentParticipantColumns.PAYER + ", "
                    + RelPaymentParticipantColumns.AMOUNT_CURRENCY_CODE + ", "
                    + RelPaymentParticipantColumns.AMOUNT_VALUE
                    + ") values (?, ?, ?, ? , ? )";

    private static final String DELETE_PAYMENT_REL =
            "delete from "
                    + RelPaymentParticipantTable.TABLE_NAME
                    + " where "
                    + RelPaymentParticipantColumns.PAYMENT_ID + " = ?";

    private static final String DELETE =
            "delete from "
                    + PaymentTable.TABLE_NAME
                    + " where "
                    + BaseColumns._ID + " = ?";

    private static final String COUNT_PAYMENTS_IN_TRIP =
            "select count(*) from "
                    + PaymentTable.TABLE_NAME
                    + " where "
                    + PaymentColumns.TRIP_ID + " = ?";

    private static final String PAYMENT_TABLE_SHORTY = "p";
    private static final String REL_TABLE_SHORTY = "rel";
    private static final String TRIP_TABLE_SHORTY = "t";

    private static final String PAYMENT_QUERY =
            "select "
                    + PAYMENT_TABLE_SHORTY + "." + BaseColumns._ID + ", "
                    + PAYMENT_TABLE_SHORTY + "." + PaymentColumns.DESCRIPTION + ", "
                    + PAYMENT_TABLE_SHORTY + "." + PaymentColumns.CATEGORY + ", "
                    + PAYMENT_TABLE_SHORTY + "." + PaymentColumns.DATE + ", "
                    + REL_TABLE_SHORTY + "." + RelPaymentParticipantColumns.PARTICIPANT_ID + ", "
                    + REL_TABLE_SHORTY + "." + RelPaymentParticipantColumns.PAYER + ", "
                    + REL_TABLE_SHORTY + "." + RelPaymentParticipantColumns.AMOUNT_CURRENCY_CODE + ", "
                    + REL_TABLE_SHORTY + "." + RelPaymentParticipantColumns.AMOUNT_VALUE
                    + " from " +
                    PaymentTable.TABLE_NAME + " " + PAYMENT_TABLE_SHORTY + ", " +
                    TripTable.TABLE_NAME + " " + TRIP_TABLE_SHORTY + ", " +
                    RelPaymentParticipantTable.TABLE_NAME + " " + REL_TABLE_SHORTY
                    + " where "
                    + PAYMENT_TABLE_SHORTY + "." + BaseColumns._ID + " = "
                    + REL_TABLE_SHORTY + "." + RelPaymentParticipantColumns.PAYMENT_ID
                    + " and "
                    + TRIP_TABLE_SHORTY + "." + BaseColumns._ID + " = "
                    + PAYMENT_TABLE_SHORTY + "." + PaymentColumns.TRIP_ID
                    + " and "
                    + TRIP_TABLE_SHORTY + "." + BaseColumns._ID + " = ?";

    private static final String PAYMENT_DESCRIPTION_QUERY =
            "select distinct "
                    + PAYMENT_TABLE_SHORTY + "." + PaymentColumns.DESCRIPTION
                    + " from " +
                    PaymentTable.TABLE_NAME + " " + PAYMENT_TABLE_SHORTY + ", " +
                    TripTable.TABLE_NAME + " " + TRIP_TABLE_SHORTY
                    + " where "
                    + TRIP_TABLE_SHORTY + "." + BaseColumns._ID + " = "
                    + PAYMENT_TABLE_SHORTY + "." + PaymentColumns.TRIP_ID
                    + " and "
                    + TRIP_TABLE_SHORTY + "." + BaseColumns._ID + " = ?";

    private final SQLiteDatabase db;
    private final SQLiteStatement stmtInsert;
    private final SQLiteStatement stmtDelete;
    private final SQLiteStatement stmtInsertRelation;
    private final SQLiteStatement stmtDeleteByPaymentId;
    private final SQLiteStatement stmtCountPaymentsInTrip;

    public PaymentDao(SQLiteDatabase db) {
        this.db = db;
        stmtInsert = db.compileStatement(PaymentDao.INSERT);
        stmtDelete = db.compileStatement(PaymentDao.DELETE);
        stmtInsertRelation = db.compileStatement(INSERT_RELATION);
        stmtDeleteByPaymentId = db.compileStatement(DELETE_PAYMENT_REL);
        stmtCountPaymentsInTrip = db.compileStatement(COUNT_PAYMENTS_IN_TRIP);
    }

    public long create(PaymentReference type) {
        long result;

        stmtInsert.clearBindings();
        stmtInsert.bindLong(1, type.getTrip_id());
        stmtInsert.bindString(2, ConversionUtils.nullSafe(type.getDescription()));
        stmtInsert.bindLong(3, type.getCategory().ordinal());
        stmtInsert.bindLong(4, type.getPaymentDateTime().getTime());
        result = stmtInsert.executeInsert();

        create(result, type.getPaymentRelationKeys());
        return result;
    }

    private void create(long paymentId, List<PaymentParticipantRelationKey> paymentRelationKeys) {
        for (PaymentParticipantRelationKey relation : paymentRelationKeys) {
            relation.setPaymentId(paymentId);
            create(relation);
        }
    }

    public void deletePayment(long paymentId) {
        deleteWithRelations(paymentId);

    }

    public void deleteAllInTrip(long tripId) {
        List<PaymentReference> paymentsInTrip = getAllPaymentsInTrip(tripId);
        for (PaymentReference payment : paymentsInTrip) {
            deleteWithRelations(payment.getId());
        }
    }

    public long create(PaymentParticipantRelationKey type) {
        stmtInsertRelation.clearBindings();
        stmtInsertRelation.bindLong(1, type.getPaymentId());
        stmtInsertRelation.bindLong(2, type.getParticipantId());
        stmtInsertRelation.bindLong(3, ConversionUtils.bool2Int(type.isPayer()));
        stmtInsertRelation.bindString(4, type.getAmount().getUnit().getCurrencyCode());
        stmtInsertRelation.bindDouble(5, type.getAmount().getValue());
        return stmtInsertRelation.executeInsert();
    }

    public void update(PaymentReference type) {
        final ContentValues values = new ContentValues();
        values.put(PaymentColumns.DESCRIPTION, ConversionUtils.nullSafe(type.getDescription()));
        values.put(PaymentColumns.CATEGORY, type.getCategory().ordinal());
        values.put(PaymentColumns.DATE, type.getPaymentDateTime().getTime() );

        db.update(
                PaymentTable.TABLE_NAME,
                values,
                BaseColumns._ID + " = ?",
                new String[] { String.valueOf(type.getId()) });

        deleteAllRelatedRecords(type.getId());
        create(type.getId(), type.getPaymentRelationKeys());
    }

    public void deleteWithRelations(long paymentId) {
        deleteAllRelatedRecords(paymentId);
        stmtDelete.clearBindings();
        stmtDelete.bindLong(1, paymentId);
        stmtDelete.execute();
    }

    private void deleteAllRelatedRecords(long paymentId) {
        stmtDeleteByPaymentId.clearBindings();
        stmtDeleteByPaymentId.bindLong(1, paymentId);
        stmtDeleteByPaymentId.execute();
    }

    public int countPaymentsInTrip(long tripId) {
        stmtCountPaymentsInTrip.clearBindings();
        stmtCountPaymentsInTrip.bindLong(1, tripId);
        return (int) stmtCountPaymentsInTrip.simpleQueryForLong();
    }

    public List<PaymentReference> getAllPaymentsInTrip(long tripId) {
        List<PaymentReference> list = new ArrayList<PaymentReference>();
        // TODO(ckoelle) Use LongSparseArray
        Map<Long, PaymentReference> resultMap = new HashMap<Long, PaymentReference>();
        Cursor c =
                db.rawQuery(PAYMENT_QUERY,
                        new String[] { String.valueOf(tripId) });
        if (c.moveToFirst()) {
            do {
                long paymentId = c.getLong(0);
                PaymentReference ref = resultMap.get(paymentId);
                if (ref == null) {
                    ref = new PaymentReference();
                    ref.setId(paymentId);
                    ref.setDescription(c.getString(1));
                    ref.setCategory(PaymentCategory.getValueByOrdinal((int) c.getLong(2)));
                    ref.setPaymentDateTime(ConversionUtils.getDateByLong(c.getLong(3)));
                }
                long participantId = c.getLong(4);
                boolean isPayer = ConversionUtils.int2bool((int) c.getLong(5));
                Amount amount = new Amount();
                amount.setUnit(Currency.getInstance(c.getString(6)));
                amount.setValue(NumberUtils.round(Double.valueOf(c.getDouble(7))));
                if (Rc.debugOn) {
                    Log.d(Rc.LT, amount + "");
                }
                PaymentParticipantRelationKey position = new PaymentParticipantRelationKey(paymentId, participantId,
                        isPayer, amount);
                ref.getPaymentRelationKeys().add(position);
                resultMap.put(paymentId, ref);
            }
            while (c.moveToNext());
        }
        if (!c.isClosed()) {
            c.close();
        }
        list.addAll(resultMap.values());
        return list;
    }

    public ArrayList<String> getAllPaymentDescriptionsInTrip(long tripId) {
        ArrayList<String> list = new ArrayList<>();
        Cursor c = db.rawQuery(PAYMENT_DESCRIPTION_QUERY, new String[] { String.valueOf(tripId) });
        if (c.moveToFirst()) {
            do {
                String paymentDescription = c.getString(0);
                list.add(paymentDescription);
            } while (c.moveToNext());
        }
        if (!c.isClosed()) {
            c.close();
        }
        return list;
    }

}
