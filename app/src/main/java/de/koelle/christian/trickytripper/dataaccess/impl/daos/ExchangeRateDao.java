package de.koelle.christian.trickytripper.dataaccess.impl.daos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Currency;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.provider.BaseColumns;
import android.util.Log;
import de.koelle.christian.common.utils.ConversionUtils;
import de.koelle.christian.trickytripper.constants.Rc;
import de.koelle.christian.trickytripper.dataaccess.impl.daos.ExchangeRatePrefTable.ExchangeRatePrefColumns;
import de.koelle.christian.trickytripper.dataaccess.impl.daos.ExchangeRateTable.ExchangeRateColumns;
import de.koelle.christian.trickytripper.model.ExchangeRate;
import de.koelle.christian.trickytripper.model.ImportOrigin;

public class ExchangeRateDao {

    private static final String ERP_INSERT =
            "insert into " + ExchangeRatePrefTable.TABLE_NAME + "("
                    + ExchangeRatePrefColumns.CURRENCY_FROM + ", "
                    + ExchangeRatePrefColumns.CURRENCY_TO + ", "
                    + ExchangeRatePrefColumns.EXCHANGE_RATE_ID + ", "
                    + ExchangeRatePrefColumns.DATE_LAST_USED
                    + ") values (?, ?, ?, ? )";

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

    private static final String ERP_SELECTION_ARGS_FIND_EXISTING = new StringBuilder()
            .append(ExchangeRatePrefColumns.CURRENCY_FROM)
            .append(" = ?")
            .append(" AND ")
            .append(ExchangeRatePrefColumns.CURRENCY_TO)
            .append(" = ?")
            .append(" AND ")
            .append(ExchangeRatePrefColumns.EXCHANGE_RATE_ID)
            .append(" = ?")
            .toString();

    private static final String ERP_SELECTION_ARGS_FIND_ALL_MATCHING = new StringBuilder()
            .append("(")
            .append(ExchangeRatePrefColumns.CURRENCY_FROM)
            .append(" = ?")
            .append(" AND ")
            .append(ExchangeRatePrefColumns.CURRENCY_TO)
            .append(" = ?")
            .append(")")
            .append(" OR ")
            .append("(")
            .append(ExchangeRatePrefColumns.CURRENCY_TO)
            .append(" = ?")
            .append(" AND ")
            .append(ExchangeRatePrefColumns.CURRENCY_FROM)
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
        insertStatementErp.bindLong(4, new Date().getTime());
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
        values.put(ExchangeRatePrefColumns.DATE_LAST_USED, new Date().getTime());
        return db.update(
                ExchangeRatePrefTable.TABLE_NAME,
                values,
                ERP_SELECTION_ARGS_FIND_EXISTING,
                new String[] {
                        currencyFrom.getCurrencyCode(),
                        currencyTo.getCurrencyCode(),
                        String.valueOf(id)
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
        return queryExchangeRates(null, null, null);
    }

    public List<ExchangeRate> findSuitableRates(Currency currencyFrom, Currency currencyTo) {
        return findMatchingExchangeRates(currencyFrom, currencyTo);
    }

    public List<ExchangeRate> findExistingImportedRecords(ExchangeRate rate) {
        String[] selectionArgs = new String[] {
                rate.getCurrencyFrom().getCurrencyCode(),
                rate.getCurrencyTo().getCurrencyCode(),
                rate.getCurrencyFrom().getCurrencyCode(),
                rate.getCurrencyTo().getCurrencyCode(),
                rate.getImportOrigin().ordinal() + ""
        };
        return queryExchangeRates(ER_SELECTION_ARGS_FIND_IMPORTED, selectionArgs, null);
    }

    private List<ExchangeRate> findMatchingExchangeRates(Currency currencyFrom, Currency currencyTo) {
        final List<Long> sortedIdListUsedLast = queryExchangeRatePrefs(currencyFrom, currencyTo);

        String[] selectionArgs = new String[] {
                currencyFrom.getCurrencyCode(),
                currencyTo.getCurrencyCode(),
                currencyFrom.getCurrencyCode(),
                currencyTo.getCurrencyCode()
        };
        String orderArguments = ExchangeRateColumns.DATE_UPDATE + " DESC";

        List<ExchangeRate> result = queryExchangeRates(ER_SELECTION_ARGS_FIND_ALL_MATCHING, selectionArgs,
                orderArguments);
        Collections.sort(result, new ExchangeRateUsedComparator(sortedIdListUsedLast));

        return result;
    }

    private List<Long> queryExchangeRatePrefs(Currency currencyFrom, Currency currencyTo) {
        List<Long> resultList = new ArrayList<>();
        Cursor c =
                db.query(
                        ExchangeRatePrefTable.TABLE_NAME,
                        new String[] { ExchangeRatePrefColumns.EXCHANGE_RATE_ID },
                        ERP_SELECTION_ARGS_FIND_ALL_MATCHING,
                        new String[] {
                                currencyFrom.getCurrencyCode(),
                                currencyTo.getCurrencyCode(),
                                currencyFrom.getCurrencyCode(),
                                currencyTo.getCurrencyCode()
                        },
                        null,
                        null,
                        ExchangeRatePrefColumns.DATE_LAST_USED + " DESC",
                        null);
        if (c.moveToFirst()) {
            do {
                resultList.add(c.getLong(0));
            }
            while (c.moveToNext());
        }
        if (!c.isClosed()) {
            c.close();
        }
        if (Rc.debugOn) {
            Log.d(Rc.LT_DB, "Ordered exchange rates last used: " + resultList);
        }
        return resultList;
    }

    public ExchangeRate getExchangeRateById(Long technicalId) {
        String[] selectionArgs = new String[] {
                String.valueOf(technicalId)
        };
        // TODO(ckoelle) Could be done more beautifully,i.e. without List
        List<ExchangeRate> resultList = queryExchangeRates(BaseColumns._ID + " = ?", selectionArgs, null);
        return (resultList.size() > 0) ? resultList.get(0) : null;
    }

    //

    private List<ExchangeRate> queryExchangeRates(String selectionCriteria, String[] selectionArgs,
            String orderArguments) {
        List<ExchangeRate> resultList = new ArrayList<>();
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
                        orderArguments,
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
                        ERP_RAW_QUERY_FIND,
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

    public Map<List<Currency>, List<Currency>> findUsedCurrencies(Currency targetCurrency) {
        List<Currency> currenciesMatchingInOrderOfUsage = new ArrayList<>();
        List<Currency> currenciesUsedByDate = new ArrayList<>();
        Cursor c =
                db.query(
                        ExchangeRatePrefTable.TABLE_NAME,
                        new String[] {
                                ExchangeRatePrefColumns.CURRENCY_FROM,
                                ExchangeRatePrefColumns.CURRENCY_TO
                        },
                        null,
                        null,
                        null,
                        null,
                        ExchangeRatePrefColumns.DATE_LAST_USED + " DESC",
                        null);
        if (c.moveToFirst()) {
            do {
                Currency cA = Currency.getInstance(c.getString(0));
                Currency cB = Currency.getInstance(c.getString(1));

                /* Match list */
                if (targetCurrency != null && targetCurrency.equals(cA)) {
                    addIfNotYetContained(cB, currenciesMatchingInOrderOfUsage, targetCurrency);
                }
                else if (targetCurrency != null && targetCurrency.equals(cB)) {
                    addIfNotYetContained(cA, currenciesMatchingInOrderOfUsage, targetCurrency);
                }
                /* General list */
                if (cA.getCurrencyCode().compareTo(cB.getCurrencyCode()) < 0) {
                    addIfNotYetContained(cA, currenciesUsedByDate, targetCurrency);
                    addIfNotYetContained(cB, currenciesUsedByDate, targetCurrency);
                }
                else {
                    addIfNotYetContained(cB, currenciesUsedByDate, targetCurrency);
                    addIfNotYetContained(cA, currenciesUsedByDate, targetCurrency);
                }
            }
            while (c.moveToNext());
        }
        if (!c.isClosed()) {
            c.close();
        }
        Map<List<Currency>, List<Currency>> result = new LinkedHashMap<>();
        result.put(currenciesMatchingInOrderOfUsage, currenciesUsedByDate);
        return result;
    }

    public Map<List<Currency>, List<Currency>> findCurrenciesInExchangeRates(Currency targetCurrency) {
        List<Currency> currenciesMatchingByDateOfUpdate = new ArrayList<>();
        List<Currency> currenciesElseByDateOfUpdate = new ArrayList<>();

        Cursor c =
                db.query(
                        ExchangeRateTable.TABLE_NAME,
                        new String[] {
                                ExchangeRateColumns.CURRENCY_FROM,
                                ExchangeRateColumns.CURRENCY_TO
                        },
                        null,
                        null,
                        null,
                        null,
                        ExchangeRateColumns.DATE_UPDATE + " DESC",
                        null);
        if (c.moveToFirst()) {
            do {
                Currency cA = Currency.getInstance(c.getString(0));
                Currency cB = Currency.getInstance(c.getString(1));

                /* Match list */
                if (targetCurrency != null && targetCurrency.equals(cA)) {
                    addIfNotYetContained(cB, currenciesMatchingByDateOfUpdate, targetCurrency);
                }
                else if (targetCurrency != null && targetCurrency.equals(cB)) {
                    addIfNotYetContained(cA, currenciesMatchingByDateOfUpdate, targetCurrency);
                }

                /* General list */
                if (cA.getCurrencyCode().compareTo(cB.getCurrencyCode()) < 0) {
                    addIfNotYetContained(cA, currenciesElseByDateOfUpdate, targetCurrency);
                    addIfNotYetContained(cB, currenciesElseByDateOfUpdate, targetCurrency);
                }
                else {
                    addIfNotYetContained(cB, currenciesElseByDateOfUpdate, targetCurrency);
                    addIfNotYetContained(cA, currenciesElseByDateOfUpdate, targetCurrency);
                }
            }
            while (c.moveToNext());
        }
        if (!c.isClosed()) {
            c.close();
        }
        Map<List<Currency>, List<Currency>> result = new LinkedHashMap<>();
        result.put(currenciesMatchingByDateOfUpdate, currenciesElseByDateOfUpdate);
        return result;
    }

    private void addIfNotYetContained(Currency currencyToBeAdded, List<Currency> list, Currency targetCurrency) {
        if (targetCurrency != null && currencyToBeAdded.equals(targetCurrency) || list.contains(currencyToBeAdded)) {
            return;
        }
        list.add(currencyToBeAdded);
    }
}
