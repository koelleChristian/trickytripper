package de.koelle.christian.trickytripper.dataaccess.impl.daos;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.provider.BaseColumns;
import de.koelle.christian.common.utils.ConversionUtils;
import de.koelle.christian.trickytripper.dataaccess.impl.daos.ParticipantTable.ParticipantColumns;
import de.koelle.christian.trickytripper.dataaccess.impl.tecbeans.ParticipantReference;
import de.koelle.christian.trickytripper.model.Participant;

public class ParticipantDao {

    private static final String INSERT =
            "insert into " + ParticipantTable.TABLE_NAME + "("
                    + ParticipantColumns.TRIP_ID + ", "
                    + ParticipantColumns.NAME + ", "
                    + ParticipantColumns.ACTIVE
                    + ") values (?, ?, ? )";

    private static final String COUNT_NAME_IN_TRIP =
            "select count(*) from "
                    + ParticipantTable.TABLE_NAME
                    + " where "
                    + ParticipantColumns.NAME + " = ?"
                    + " and "
                    + ParticipantColumns.TRIP_ID + " = ?";

    private static final String COUNT_NAME_IN_TRIP_UNLESS =
            "select count(*) from "
                    + ParticipantTable.TABLE_NAME
                    + " where "
                    + ParticipantColumns.NAME + " = ?"
                    + " and "
                    + ParticipantColumns.TRIP_ID + " = ?"
                    + " and not "
                    + ParticipantColumns._ID + " = ?";

    private static final String DELETE =
            "delete from "
                    + ParticipantTable.TABLE_NAME
                    + " where "
                    + BaseColumns._ID + " = ?";

    private final SQLiteDatabase db;
    private final SQLiteStatement insertStatement;
    private final SQLiteStatement countNameInTripStatement;
    private final SQLiteStatement countNameInTripStatementUnless;
    private final SQLiteStatement deleteStatement;

    public ParticipantDao(SQLiteDatabase db) {
        this.db = db;
        insertStatement = db.compileStatement(ParticipantDao.INSERT);
        countNameInTripStatement = db.compileStatement(ParticipantDao.COUNT_NAME_IN_TRIP);
        countNameInTripStatementUnless = db.compileStatement(ParticipantDao.COUNT_NAME_IN_TRIP_UNLESS);
        deleteStatement = db.compileStatement(ParticipantDao.DELETE);
    }

    public long create(ParticipantReference type) {
        insertStatement.clearBindings();
        insertStatement.bindLong(1, type.getTrip_id());
        insertStatement.bindString(2, type.getName());
        insertStatement.bindLong(3, ConversionUtils.bool2Int(type.isActive()));
        return insertStatement.executeInsert();
    }

    public boolean doesParticipantAlreadyExist(String nameToCheck, long tripId, long participantId) {
        boolean result;

        if (1 > participantId) {
            countNameInTripStatement.clearBindings();
            countNameInTripStatement.bindString(1, ConversionUtils.nullSafe(nameToCheck));
            countNameInTripStatement.bindLong(2, tripId);
            result = ConversionUtils.int2bool((int) countNameInTripStatement.simpleQueryForLong());
        }
        else {
            countNameInTripStatementUnless.clearBindings();
            countNameInTripStatementUnless.bindString(1, ConversionUtils.nullSafe(nameToCheck));
            countNameInTripStatementUnless.bindLong(2, tripId);
            countNameInTripStatementUnless.bindLong(3, participantId);
            result = ConversionUtils.int2bool((int) countNameInTripStatementUnless.simpleQueryForLong());
        }
        return result;

    }

    public void update(ParticipantReference type) {
        final ContentValues values = new ContentValues();
        values.put(ParticipantColumns.NAME, ConversionUtils.nullSafe(type.getName()));
        values.put(ParticipantColumns.ACTIVE, ConversionUtils.bool2Int(type.isActive()));

        db.update(
                ParticipantTable.TABLE_NAME,
                values,
                BaseColumns._ID + " = ?",
                new String[] { String.valueOf(type.getId()) });
    }

    public void deleteAllInTrip(long tripId) {
        if (tripId > 0) {
            db.delete(ParticipantTable.TABLE_NAME, ParticipantColumns.TRIP_ID + " = ?", new String[] {
                    String.valueOf(tripId) });
        }
    }

    public List<Participant> getAllParticipantsInTrip(long tripId) {
        List<Participant> list = new ArrayList<Participant>();
        Cursor c =
                db.query(
                        ParticipantTable.TABLE_NAME,
                        new String[] {
                                BaseColumns._ID,
                                ParticipantColumns.NAME,
                                ParticipantColumns.ACTIVE },
                        ParticipantColumns.TRIP_ID + " = ?",
                        new String[] { String.valueOf(tripId) },
                        null,
                        null,
                        ParticipantColumns.NAME,
                        null);
        if (c.moveToFirst()) {
            do {
                Participant participant = new Participant();
                participant.setId(c.getLong(0));
                participant.setName(c.getString(1));
                participant.setActive(ConversionUtils.int2bool((int) c.getLong(2)));
                list.add(participant);
            }
            while (c.moveToNext());
        }
        if (!c.isClosed()) {
            c.close();
        }
        return list;
    }

    public void deleteParticipant(long participantId) {
        deleteStatement.clearBindings();
        deleteStatement.bindLong(1, participantId);
        deleteStatement.execute();
    }
}
