package de.koelle.christian.trickytripper.exchangerates.impl;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Currency;
import java.util.concurrent.TimeUnit;

import de.koelle.christian.common.json.AsyncJsonParser;
import de.koelle.christian.common.json.AsyncJsonParserResultCallback;
import de.koelle.christian.trickytripper.model.ImportOrigin;

public class AsyncExchangeRateJsonResolverExchangeratesapiIoImpl implements AsyncExchangeRateResolver {

    private static final String SOURCE_CURRENCY_CODE_PLACEHOLDER = "%%CURR_A%%";
    private static final String TARGET_CURRENCY_CODE_PLACEHOLDER = "%%CURR_B%%";
    public static final String EXCHANGE_RATE_SERVICE_URL = "https://api.exchangeratesapi.io/latest?base="+SOURCE_CURRENCY_CODE_PLACEHOLDER+"&symbols="+TARGET_CURRENCY_CODE_PLACEHOLDER;
    private final Context context;

    private AsyncJsonParser caller = new AsyncJsonParser();

    public AsyncExchangeRateJsonResolverExchangeratesapiIoImpl(Context context) {
        this.context = context;
    }

    public void cancelRunningRequests() {
        caller.cancelRunningRequests(context);
    }

    public void getExchangeRate(Currency from, Currency to, final AsyncExchangeRateResolverResultCallback callback) {
        caller.getJSONFromUrl(context, provideUrl(from, to), new AsyncJsonParserResultCallback() {

            public void deliverResult(JSONObject object) {
                String result = null;
                if (object != null) {
                    try {
                        result = new JSONObject(object.get(object.keys().next()).toString()).getString(to.getCurrencyCode());
                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                callback.deliverResult(result);
            }

        });
    }

    @SuppressWarnings("static-access")
    public long calculateResponseTime(Currency from, Currency to) {

        ResponseTimeDeterminationCallback callback = new ResponseTimeDeterminationCallback(System.nanoTime());
        caller.getJSONFromUrl(context, provideUrl(from, to), callback);
        while (!callback.hasResult()) {
            try {
                Thread.currentThread().sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return callback.getResultInMillis();
    }

    private String provideUrl(Currency from, Currency to) {
        return EXCHANGE_RATE_SERVICE_URL
                .replace(SOURCE_CURRENCY_CODE_PLACEHOLDER, from.getCurrencyCode())
                .replace(TARGET_CURRENCY_CODE_PLACEHOLDER, to.getCurrencyCode());
    }

    public ImportOrigin getOriginToBeUsed() {
        return ImportOrigin.GOOGLE;
    }

    private final class ResponseTimeDeterminationCallback implements AsyncJsonParserResultCallback {

        boolean hasResult = false;
        long result = 0;
        private final long startTime;

        public ResponseTimeDeterminationCallback(long startTime) {
            this.startTime = startTime;
        }

        public void deliverResult(JSONObject jsonObject) {
            if (jsonObject != null) {
                result = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
            }
            else {
                result = -1;
            }
            hasResult = true;
        }

        public boolean hasResult() {
            return hasResult;
        }

        public long getResultInMillis() {
            return result;
        }

    }

}
