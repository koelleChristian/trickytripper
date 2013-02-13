package de.koelle.christian.trickytripper.dataaccess.impl.daos;

import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.provider.BaseColumns;
import de.koelle.christian.common.utils.ConversionUtils;
import de.koelle.christian.trickytripper.dataaccess.impl.daos.ExchangeRatePrefTable.ExchangeRatePrefColumns;
import de.koelle.christian.trickytripper.dataaccess.impl.daos.ExchangeRateTable.ExchangeRateColumns;
import de.koelle.christian.trickytripper.model.ExchangeRate;
import de.koelle.christian.trickytripper.model.ExchangeRateResult;
import de.koelle.christian.trickytripper.model.ImportOrigin;

public class ExchangeRateDao {

    private static final String ERP_INSERT =
            "insert into " + ExchangeRatePrefTable.TABLE_NAME + "("
                    + ExchangeRatePrefColumns.CURRENCY_FROM + ", "
                    + ExchangeRatePrefColumns.CURRENCY_TO + ", "
                    + ExchangeRatePrefColumns.EXCHANGE_RATE_ID
                    + ") values (?, ?, ? )";

    private static final String ER_INSERT =
            "insert into " + ExchangeRateTable.TABLE_NAME + "("
                    + ExchangeRateColumns.CURRENCY_FROM + ", "
                    + ExchangeRateColumns.CURRENCY_TO + ", "
                    + ExchangeRateColumns.RATE + ", "
                    + ExchangeRateColumns.DESCRIPTION + ", "
                    + ExchangeRateColumns.DATE_CREATE + ", "
                    + ExchangeRateColumns.DATE_UPDATE + ", "
                    + ExchangeRateColumns.IMPORT_ORIGIN
                    + ") values (?, ?, ?, ?, ?, ?, ? )";

    private static final String TEMP_TABLE_EXCHANGE_RATE_DELETE = "exchangeRateDelete";
    private static final String TEMP_TABLE_EXCHANGE_RATE_PREF_DELETE = "exchangeRatePrefsDelete";

    private static final String ER_DELETE_01 = "CREATE TEMP TABLE " + TEMP_TABLE_EXCHANGE_RATE_DELETE + "(x);";
    private static final String ER_DELETE_02 = "INSERT INTO " + TEMP_TABLE_EXCHANGE_RATE_DELETE + " VALUES(?);";
    private static final String ER_DELETE_03 = "DELETE FROM " + ExchangeRateTable.TABLE_NAME
            + " WHERE " + ExchangeRateColumns._ID + " IN (SELECT * FROM " + TEMP_TABLE_EXCHANGE_RATE_DELETE + ");";
    private static final String ER_DELETE_04 = "DROP TABLE " + TEMP_TABLE_EXCHANGE_RATE_DELETE + ";";

    private static final String ERP_DELETE_01 = "CREATE TEMP TABLE " + TEMP_TABLE_EXCHANGE_RATE_PREF_DELETE + "(x);";
    private static final String ERP_DELETE_02 = "INSERT INTO " + TEMP_TABLE_EXCHANGE_RATE_PREF_DELETE + " VALUES(?);";
    private static final String ERP_DELETE_03 = "DELETE FROM " + ExchangeRatePrefTable.TABLE_NAME
            + " WHERE " + ExchangeRatePrefColumns.EXCHANGE_RATE_ID + " IN (SELECT * FROM "
            + TEMP_TABLE_EXCHANGE_RATE_PREF_DELETE + ");";
    private static final String ERP_DELETE_04 = "DROP TABLE " + TEMP_TABLE_EXCHANGE_RATE_PREF_DELETE + ";";

    private static final String ER_COUNT_DESCRIPTION =
            "select count(*) from "
                    + ExchangeRateTable.TABLE_NAME
                    + " where "
                    + ExchangeRateColumns.DESCRIPTION + " = ?";

    private static final String ER_COUNT_DESCRIPTION_UNLESS =
            "select count(*) from "
                    + ExchangeRateTable.TABLE_NAME
                    + " where "
                    + ExchangeRateColumns.DESCRIPTION + " = ?"
                    + " and not "
                    + ExchangeRateColumns._ID + " = ?";

    private static final String ERP_RAW_QUERY_FIND = new StringBuilder()
            .append("select ")
            .append(ExchangeRateTable.TABLE_NAME).append(".").append(BaseColumns._ID)
            .append(" ")
            .append(ExchangeRateTable.TABLE_NAME).append(".").append(ExchangeRateColumns.CURRENCY_FROM)
            .append(" ")
            .append(ExchangeRateTable.TABLE_NAME).append(".").append(ExchangeRateColumns.CURRENCY_TO)
            .append(" ")
            .append(ExchangeRateTable.TABLE_NAME).append(".").append(ExchangeRateColumns.RATE)
            .append(" ")
            .append(ExchangeRateTable.TABLE_NAME).append(".").append(ExchangeRateColumns.DESCRIPTION)
            .append(" ")
            .append(ExchangeRateTable.TABLE_NAME).append(".").append(ExchangeRateColumns.DATE_CREATE)
            .append(" ")
            .append(ExchangeRateTable.TABLE_NAME).append(".").append(ExchangeRateColumns.DATE_UPDATE)
            .append(" ")
            .append(ExchangeRateTable.TABLE_NAME).append(".").append(ExchangeRateColumns.IMPORT_ORIGIN)
            /**/
            .append(" inner join ")
            .append(ExchangeRatePrefTable.TABLE_NAME)
            .append(" on ")
            .append(ExchangeRateTable.TABLE_NAME).append(".").append(BaseColumns._ID)
            .append(" = ")
            .append(ExchangeRatePrefTable.TABLE_NAME).append(".").append(ExchangeRatePrefColumns.EXCHANGE_RATE_ID)
            /**/
            .append(" where ")
            .append("(")
            .append(ExchangeRateTable.TABLE_NAME).append(".").append(ExchangeRateColumns.CURRENCY_FROM)
            .append(" = ? ")
            .append(" AND ")
            .append(ExchangeRateTable.TABLE_NAME).append(".").append(ExchangeRateColumns.CURRENCY_TO)
            .append(" = ? ")
            .append(")")
            .append(" OR ")
            .append("(")
            .append(ExchangeRateTable.TABLE_NAME).append(".").append(ExchangeRateColumns.CURRENCY_TO)
            .append(" = ? ")
            .append(" AND ")
            .append(ExchangeRateTable.TABLE_NAME).append(".").append(ExchangeRateColumns.CURRENCY_FROM)
            .append(" = ? ")
            .append(");")
            .toString();

    private static final String ER_SELECTION_ARGS_FIND_IMPORTED = new StringBuilder()
            .append("(")
            .append("(")
            .append(ExchangeRateColumns.CURRENCY_FROM)
            .append(" = ?")
            .append(" AND ")
            .append(ExchangeRateColumns.CURRENCY_TO)
            .append(" = ?")
            .append(")")
            .append(" OR ")
            .append("(")
            .append(ExchangeRateColumns.CURRENCY_TO)
            .append(" = ?")
            .append(" AND ")
            .append(ExchangeRateColumns.CURRENCY_FROM)
            .append(" = ?")
            .append(")")
            .append(")")
            .append(" AND ")
            .append(ExchangeRateColumns.IMPORT_ORIGIN)
            .append(" = ?")
            .toString();

    private static final String ER_SELECTION_ARGS_FIND_ALL_MATCHING = new StringBuilder()
            .append("(")
            .append(ExchangeRateColumns.CURRENCY_FROM)
            .append(" = ?")
            .append(" AND ")
            .append(ExchangeRateColumns.CURRENCY_TO)
            .append(" = ?")
            .append(")")
            .append(" OR ")
            .append("(")
            .append(ExchangeRateColumns.CURRENCY_TO)
            .append(" = ?")
            .append(" AND ")
            .append(ExchangeRateColumns.CURRENCY_FROM)
            .append(" = ?")
            .append(")")
            .toString();

    private final SQLiteDatabase db;
    private final SQLiteStatement insertStatementEr;
    private final SQLiteStatement insertStatementErp;
    private final SQLiteStatement countDescInEr;
    private final SQLiteStatement countDescInErUnless;

    public ExchangeRateDao(SQLiteDatabase db) {
        this.db = db;
        insertStatementEr = db.compileStatement(ExchangeRateDao.ER_INSERT);
        insertStatementErp = db.compileStatement(ExchangeRateDao.ERP_INSERT);
        countDescInEr = db.compileStatement(ExchangeRateDao.ER_COUNT_DESCRIPTION);
        countDescInErUnless = db.compileStatement(ExchangeRateDao.ER_COUNT_DESCRIPTION_UNLESS);
    }

    public long create(ExchangeRate type) {
        insertStatementEr.clearBindings();
        insertStatementEr.bindString(1, type.getCurrencyFrom().getCurrencyCode());
        insertStatementEr.bindString(2, type.getCurrencyTo().getCurrencyCode());
        insertStatementEr.bindDouble(3, type.getExchangeRate());
        insertStatementEr.bindString(4, ConversionUtils.nullSafe(type.getDescription()));
        insertStatementEr.bindLong(5, type.getCreationDate().getTime());
        insertStatementEr.bindLong(6, type.getUpdateDate().getTime());
        insertStatementEr.bindLong(7, type.getImportOrigin().ordinal());
        return insertStatementEr.executeInsert();
    }

    public long createPref(ExchangeRate type) {
        insertStatementErp.clearBindings();
        insertStatementErp.bindString(1, type.getCurrencyFrom().getCurrencyCode());
        insertStatementErp.bindString(2, type.getCurrencyTo().getCurrencyCode());
        insertStatementErp.bindLong(3, type.getId());
        return insertStatementErp.executeInsert();
    }

    public void update(ExchangeRate type) {
        final ContentValues values = new ContentValues();
        values.put(ExchangeRateColumns.CURRENCY_FROM, type.getCurrencyFrom().getCurrencyCode());
        values.put(ExchangeRateColumns.CURRENCY_TO, type.getCurrencyTo().getCurrencyCode());
        values.put(ExchangeRateColumns.RATE, type.getExchangeRate());
        values.put(ExchangeRateColumns.DESCRIPTION, type.getDescription());
        values.put(ExchangeRateColumns.DATE_UPDATE, type.getUpdateDate().getTime());
        values.put(ExchangeRateColumns.IMPORT_ORIGIN, type.getImportOrigin().ordinal());
        db.update(
                ExchangeRateTable.TABLE_NAME,
                values,
                BaseColumns._ID + " = ?",
                new String[] { String.valueOf(type.getId()) });
    }

    public int updatePrefs(Currency currencyFrom, Currency currencyTo, long id) {
        final ContentValues values = new ContentValues();
        values.put(ExchangeRatePrefColumns.EXCHANGE_RATE_ID, id);
        return db.update(
                ExchangeRatePrefTable.TABLE_NAME,
                values,
                ER_SELECTION_ARGS_FIND_ALL_MATCHING,
                new String[] {
                        currencyFrom.getCurrencyCode(),
                        currencyTo.getCurrencyCode(),
                        currencyFrom.getCurrencyCode(),
                        currencyTo.getCurrencyCode()
                });
    }

    public void delete(List<Long> idsToBeDeleted) {
        deletePrefs(idsToBeDeleted);
        db.execSQL(ER_DELETE_01);
        for (Long id : idsToBeDeleted) {
            db.execSQL(ER_DELETE_02, new Object[] { id });
        }
        db.execSQL(ER_DELETE_03);
        db.execSQL(ER_DELETE_04);
    }

    public void deletePrefs(List<Long> idsToBeDeleted) {
        db.execSQL(ERP_DELETE_01);
        for (Long id : idsToBeDeleted) {
            db.execSQL(ERP_DELETE_02, new Object[] { id });
        }
        db.execSQL(ERP_DELETE_03);
        db.execSQL(ERP_DELETE_04);
    }

    public boolean doesExchangeRateAlreadyExist(ExchangeRate exchangeRate) {
        long queryResult;

        String descriptionToCheckNullSafe = ConversionUtils.nullSafe(exchangeRate.getDescription());
        if (exchangeRate.isNew()) {
            countDescInEr.clearBindings();
            countDescInEr.bindString(1, descriptionToCheckNullSafe);
            queryResult = countDescInEr.simpleQueryForLong();
        }
        else {
            countDescInErUnless.clearBindings();
            countDescInErUnless.bindString(1, descriptionToCheckNullSafe);
            countDescInErUnless.bindLong(2, exchangeRate.getId());
            queryResult = countDescInErUnless.simpleQueryForLong();
        }
        return ConversionUtils.int2bool((int) queryResult);

    }

    public List<ExchangeRate> getAllExchangeRatesWithoutInversion() {
        return queryExchangeRates(null, null);
    }

    public ExchangeRateResult findSuitableRates(Currency currencyFrom, Currency currencyTo) {
        List<ExchangeRate> resultList = findMatchingExchangeRates(currencyFrom, currencyTo);
        // ExchangeRate rateUsedLastTime = findRateUseLastTime(currencyFrom,
        // currencyTo);
        ExchangeRate rateUsedLastTime = null;
        return new ExchangeRateResult(resultList, rateUsedLastTime);
    }

    public List<ExchangeRate> findExistingImportedRecords(ExchangeRate rate) {
        String[] selectionArgs = new String[] {
                rate.getCurrencyFrom().getCurrencyCode(),
                rate.getCurrencyTo().getCurrencyCode(),
                rate.getCurrencyFrom().getCurrencyCode(),
                rate.getCurrencyTo().getCurrencyCode(),
                rate.getImportOrigin().ordinal() + ""
        };
        return queryExchangeRates(ER_SELECTION_ARGS_FIND_IMPORTED, selectionArgs);
    }

    private List<ExchangeRate> findMatchingExchangeRates(Currency currencyFrom, Currency currencyTo) {
        String[] selectionArgs = new String[] {
                currencyFrom.getCurrencyCode(),
                currencyTo.getCurrencyCode(),
                currencyFrom.getCurrencyCode(),
                currencyTo.getCurrencyCode()
        };
        return queryExchangeRates(ER_SELECTION_ARGS_FIND_ALL_MATCHING, selectionArgs);
    }

    private List<ExchangeRate> queryExchangeRates(String selectionCriteria, String[] selectionArgs) {
        List<ExchangeRate> resultList = new ArrayList<ExchangeRate>();
        Cursor c =
                db.query(
                        ExchangeRateTable.TABLE_NAME,
                        new String[] {
                                BaseColumns._ID,
                                ExchangeRateColumns.CURRENCY_FROM,
                                ExchangeRateColumns.CURRENCY_TO,
                                ExchangeRateColumns.RATE,
                                ExchangeRateColumns.DESCRIPTION,
                                ExchangeRateColumns.DATE_CREATE,
                                ExchangeRateColumns.DATE_UPDATE,
                                ExchangeRateColumns.IMPORT_ORIGIN },
                        selectionCriteria,
                        selectionArgs,
                        null,
                        null,
                        null,
                        null);
        if (c.moveToFirst()) {
            do {
                ExchangeRate rate = assembleExchangeRate(c);
                resultList.add(rate);
            }
            while (c.moveToNext());
        }
        if (!c.isClosed()) {
            c.close();
        }
        return resultList;
    }

    public ExchangeRate findRateUseLastTime(Currency currencyFrom, Currency currencyTo) {
        ExchangeRate result = null;

        Cursor c =
                db.rawQuery(
                        ERP_RAW_QUERY_FIND.toString(),
                        new String[] {
                                currencyFrom.getCurrencyCode(),
                                currencyTo.getCurrencyCode(),
                                currencyFrom.getCurrencyCode(),
                                currencyTo.getCurrencyCode()
                        });
        if (c.moveToFirst()) {
            do {
                result = assembleExchangeRate(c);
            }
            while (c.moveToNext());
        }
        if (!c.isClosed()) {
            c.close();
        }
        return result;
    }

    private ExchangeRate assembleExchangeRate(Cursor c) {
        ExchangeRate rate = new ExchangeRate();
        rate.setId(c.getLong(0));
        rate.setCurrencyFrom(Currency.getInstance(c.getString(1)));
        rate.setCurrencyTo(Currency.getInstance(c.getString(2)));
        rate.setExchangeRate(c.getDouble(3));
        rate.setDescription(c.getString(4));
        rate.setCreationDate(ConversionUtils.getDateByLong(c.getLong(5)));
        rate.setUpdateDate(ConversionUtils.getDateByLong(c.getLong(6)));
        rate.setImportOrigin(ImportOrigin.getValueByOrdinal((int) c.getLong(7)));
        return rate;
    }

    public void persistExchangeRateUsedLast(ExchangeRate exchangeRateUsedLast) {
        int rowsAffected = updatePrefs(exchangeRateUsedLast.getCurrencyFrom(), exchangeRateUsedLast.getCurrencyTo(),
                exchangeRateUsedLast.getId());
        if (rowsAffected == 0) {
            createPref(exchangeRateUsedLast);
        }

    }

    private List<ExchangeRate> queryExchangeRatePref(String selectionCriteria, String[] selectionArgs) {
        List<ExchangeRate> resultList = new ArrayList<ExchangeRate>();
        Cursor c =
                db.query(
                        ExchangeRateTable.TABLE_NAME,
                        new String[] {
                                BaseColumns._ID,
                                ExchangeRateColumns.CURRENCY_FROM,
                                ExchangeRateColumns.CURRENCY_TO,
                                ExchangeRateColumns.RATE,
                                ExchangeRateColumns.DESCRIPTION,
                                ExchangeRateColumns.DATE_CREATE,
                                ExchangeRateColumns.DATE_UPDATE,
                                ExchangeRateColumns.IMPORT_ORIGIN },
                        selectionCriteria,
                        selectionArgs,
                        null,
                        null,
                        null,
                        null);
        if (c.moveToFirst()) {
            do {
                ExchangeRate rate = assembleExchangeRate(c);
                resultList.add(rate);
            }
            while (c.moveToNext());
        }
        if (!c.isClosed()) {
            c.close();
        }
        return resultList;
    }

}
