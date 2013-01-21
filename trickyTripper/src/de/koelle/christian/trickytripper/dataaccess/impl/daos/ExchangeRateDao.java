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

    private static final String ER_DELETE =
            "delete from "
                    + ExchangeRateTable.TABLE_NAME
                    + " where "
                    + BaseColumns._ID + " = ?";

    private static final String ERP_DELETE =
            "delete from "
                    + ExchangeRatePrefTable.TABLE_NAME
                    + " where "
                    + ExchangeRatePrefColumns.EXCHANGE_RATE_ID + " = ?";

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

    private final SQLiteDatabase db;
    private final SQLiteStatement insertStatementEr;
    private final SQLiteStatement deleteStatementEr;
    private final SQLiteStatement insertStatementErp;
    private final SQLiteStatement deleteStatementErp;
    private final SQLiteStatement countDescInEr;
    private final SQLiteStatement countDescInErUnless;

    public ExchangeRateDao(SQLiteDatabase db) {
        this.db = db;
        insertStatementEr = db.compileStatement(ExchangeRateDao.ER_INSERT);
        deleteStatementEr = db.compileStatement(ExchangeRateDao.ER_DELETE);
        insertStatementErp = db.compileStatement(ExchangeRateDao.ERP_INSERT);
        deleteStatementErp = db.compileStatement(ExchangeRateDao.ERP_DELETE);
        countDescInEr = db.compileStatement(ExchangeRateDao.ER_COUNT_DESCRIPTION);
        countDescInErUnless = db.compileStatement(ExchangeRateDao.ER_COUNT_DESCRIPTION_UNLESS);
    }

    public long create(ExchangeRate type) {
        insertStatementEr.clearBindings();
        insertStatementEr.bindString(1, type.getCurrencyFrom().getCurrencyCode());
        insertStatementEr.bindString(2, type.getCurrencyTo().getCurrencyCode());
        insertStatementEr.bindDouble(3, type.getExchangeRate());
        insertStatementEr.bindString(4, type.getDescription());
        insertStatementEr.bindLong(5, type.getCreationDate().getTime());
        insertStatementEr.bindLong(6, type.getUpdateDate().getTime());
        insertStatementEr.bindLong(7, type.getImportOrigin().ordinal());
        return insertStatementEr.executeInsert();
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

    public void delete(long exchangeRateId) {
        deletePrefs(exchangeRateId);
        deleteStatementEr.clearBindings();
        deleteStatementEr.bindLong(1, exchangeRateId);
        deleteStatementEr.execute();
    }

    public void deletePrefs(long exchangeRateId) {
        deleteStatementErp.clearBindings();
        deleteStatementEr.bindLong(1, exchangeRateId);
        deleteStatementEr.execute();
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
        ExchangeRate rateUsedLastTime = findRateUseLastTime(currencyFrom, currencyTo);
        return new ExchangeRateResult(resultList, rateUsedLastTime);
    }

    private ExchangeRate findRateUseLastTime(Currency currencyFrom, Currency currencyTo) {
        // TODO Auto-generated method stub
        return null;
    }

    private List<ExchangeRate> findMatchingExchangeRates(Currency currencyFrom, Currency currencyTo) {
        String selectionCriteria = new StringBuilder()
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
        String[] selectionArgs = new String[] {
                currencyFrom.getCurrencyCode(),
                currencyTo.getCurrencyCode(),
                currencyFrom.getCurrencyCode(),
                currencyTo.getCurrencyCode()
        };

        return queryExchangeRates(selectionCriteria, selectionArgs);
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

}
