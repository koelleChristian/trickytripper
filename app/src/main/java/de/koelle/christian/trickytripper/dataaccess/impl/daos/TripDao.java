package de.koelle.christian.trickytripper.dataaccess.impl.daos;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.provider.BaseColumns;

import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

import de.koelle.christian.common.utils.ConversionUtils;
import de.koelle.christian.trickytripper.dataaccess.impl.Dao;
import de.koelle.christian.trickytripper.dataaccess.impl.daos.TripTable.TripColumns;
import de.koelle.christian.trickytripper.model.Trip;
import de.koelle.christian.trickytripper.model.TripSummary;

public class TripDao implements Dao<Trip> {

    private static final String INSERT =
            "insert into " + TripTable.TABLE_NAME + "("
                    + TripColumns.NAME + ", "
                    + TripColumns.BASE_CURRENCY_CODE
                    + ") values (?, ?)";

    private static final String COUNT_NAME_IN_TRIP =
            "select count(*) from "
                    + TripTable.TABLE_NAME
                    + " where "
                    + TripColumns.NAME + " = ?";

    private static final String COUNT_NAME_IN_TRIP_UNLESS =
            "select count(*) from "
                    + TripTable.TABLE_NAME
                    + " where "
                    + TripColumns.NAME + " = ?"
                    + " and not "
                    + TripColumns._ID + " = ?";

    private static final String COUNT_TRIPS =
            "select count(*) from "
                    + TripTable.TABLE_NAME;

    private final SQLiteDatabase db;
    private final SQLiteStatement insertStatement;
    private final SQLiteStatement countNameInTripStatement;
    private final SQLiteStatement countNameInTripStatementUnless;
    private final SQLiteStatement countTripStatement;

    public TripDao(SQLiteDatabase db) {
        this.db = db;
        insertStatement = db.compileStatement(TripDao.INSERT);
        countNameInTripStatement = db.compileStatement(TripDao.COUNT_NAME_IN_TRIP);
        countNameInTripStatementUnless = db.compileStatement(TripDao.COUNT_NAME_IN_TRIP_UNLESS);
        countTripStatement = db.compileStatement(TripDao.COUNT_TRIPS);
    }

    public long create(Trip type) {
        insertStatement.clearBindings();
        insertStatement.bindString(1, type.getName());
        insertStatement.bindString(2, type.getBaseCurrency().getCurrencyCode());
        return insertStatement.executeInsert();
    }

    public boolean doesTripAlreadyExist(String nameToCheck, long tripId) {
        boolean result;

        if (1 > tripId) {
            countNameInTripStatement.clearBindings();
            countNameInTripStatement.bindString(1, ConversionUtils.nullSafe(nameToCheck));
            result = ConversionUtils.int2bool((int) countNameInTripStatement.simpleQueryForLong());
        }
        else {
            countNameInTripStatementUnless.clearBindings();
            countNameInTripStatementUnless.bindString(1, ConversionUtils.nullSafe(nameToCheck));
            countNameInTripStatementUnless.bindLong(2, tripId);
            result = ConversionUtils.int2bool((int) countNameInTripStatementUnless.simpleQueryForLong());
        }

        return result;
    }

    public boolean onlyOneTripLeft() {
        return (countTripStatement.simpleQueryForLong() <= 1);
    }

    public void update(Trip type) {
        final ContentValues values = new ContentValues();
        values.put(TripColumns.NAME, ConversionUtils.nullSafe(type.getName()));
        values.put(TripColumns.BASE_CURRENCY_CODE, type.getBaseCurrency().getCurrencyCode());

        db.update(
                TripTable.TABLE_NAME,
                values,
                BaseColumns._ID + " = ?",
                new String[] { String.valueOf(type.getId()) });

    }

    public void deleteWithRelations(Trip type) {
        if (type.getId() > 0) {
            db.delete(TripTable.TABLE_NAME, BaseColumns._ID + " = ?", new String[] {
                    String.valueOf(type.getId()) });
        }
    }

    public void delete(long tripId) {
        if (tripId > 0) {
            db.delete(TripTable.TABLE_NAME, BaseColumns._ID + " = ?", new String[] {
                    String.valueOf(tripId) });
        }
    }

    public Trip get(long id) {
        Trip trip = null;
        Cursor c =
                db.query(
                        TripTable.TABLE_NAME, // Table
                        new String[] { // columns
                        BaseColumns._ID,
                                TripColumns.NAME,
                                TripColumns.BASE_CURRENCY_CODE },
                        BaseColumns._ID + " = ?", // selection
                        new String[] { String.valueOf(id) }, // selection args
                        null, // group by
                        null, // having
                        null, // order by
                        "1"); // limit
        if (c.moveToFirst()) {
            trip = new Trip();
            trip.setId(c.getLong(0));
            trip.setName(c.getString(1));
            trip.setBaseCurrency(Currency.getInstance(c.getString(2)));
        }
        if (!c.isClosed()) {
            c.close();
        }
        return trip;
    }

    public List<TripSummary> getAllTripSummaries() {
        List<TripSummary> result = new ArrayList<TripSummary>();
        List<Trip> interimResult = getAll();
        for (Trip t : interimResult) {
            TripSummary tripSummary = new TripSummary();
            tripSummary.setId(t.getId());
            tripSummary.setName(t.getName());
            tripSummary.setBaseCurrency(t.getBaseCurrency());
            result.add(tripSummary);
        }
        return result;
    }

    public List<Trip> getAll() {
        List<Trip> list = new ArrayList<Trip>();
        Cursor c =
                db.query(
                        TripTable.TABLE_NAME,
                        new String[] {
                                BaseColumns._ID,
                                TripColumns.NAME,
                                TripColumns.BASE_CURRENCY_CODE },
                        null,
                        null,
                        null,
                        null,
                        TripColumns.NAME,
                        null);
        if (c.moveToFirst()) {
            do {
                Trip trip = new Trip();
                trip.setId(c.getLong(0));
                trip.setName(c.getString(1));
                trip.setBaseCurrency(Currency.getInstance(c.getString(2)));
                list.add(trip);
            }
            while (c.moveToNext());
        }
        if (!c.isClosed()) {
            c.close();
        }
        return list;
    }

    public List<Currency> findAllCurrenciesUsedInTrips() {
        List<Currency> result = new ArrayList<Currency>();
        Cursor c =
                db.query(
                        TripTable.TABLE_NAME,
                        new String[] {
                        TripColumns.BASE_CURRENCY_CODE
                        },
                        null,
                        null,
                        null,
                        null,
                        TripColumns.BASE_CURRENCY_CODE + " ASC",
                        null);
        if (c.moveToFirst()) {
            do {
                Currency currencyLoaded = Currency.getInstance(c.getString(0));
                if(!result.contains(currencyLoaded)){                    
                    result.add(currencyLoaded);
                }
            }
            while (c.moveToNext());
        }
        if (!c.isClosed()) {
            c.close();
        }
        return result;
    }
}
