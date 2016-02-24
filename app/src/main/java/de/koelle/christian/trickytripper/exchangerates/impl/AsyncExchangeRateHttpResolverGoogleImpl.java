package de.koelle.christian.trickytripper.exchangerates.impl;

import android.content.Context;
import de.koelle.christian.common.http.AsyncHttpParser;
import de.koelle.christian.common.http.AsyncHttpParserResultCallback;
import de.koelle.christian.common.json.AsyncJsonParserResultCallback;
import de.koelle.christian.trickytripper.model.ImportOrigin;
import org.json.JSONObject;

import java.util.Currency;
import java.util.concurrent.TimeUnit;

public class AsyncExchangeRateHttpResolverGoogleImpl implements AsyncExchangeRateResolver {

    private static final String SOURCE_CURRENCY_CODE_PLACEHOLDER = "%%CURR_A%%";
    private static final String TARGET_CURRENCY_CODE_PLACEHOLDER = "%%CURR_B%%";
    public static final String EXCHANGE_RATE_SERVICE_URL = "https://www.google.com/finance/converter?a=1&from="
            + SOURCE_CURRENCY_CODE_PLACEHOLDER + "&to=" + TARGET_CURRENCY_CODE_PLACEHOLDER;
    private final Context context;

    private AsyncHttpParser caller = new AsyncHttpParser();


    public AsyncExchangeRateHttpResolverGoogleImpl(Context context) {
        this.context = context;
    }

    public void cancelRunningRequests() {
        caller.cancelRunningRequests(context);
    }

    public void getExchangeRate(Currency from, Currency to, final AsyncExchangeRateResolverResultCallback callback) {
        String url = provideUrl(from, to);
        caller.getHttpFromUrl(context, url, new AsyncHttpParserResultCallback() {

            public void deliverResult(String httpResponseString) {
//                System.out.println(httpResponseString);//
                callback.deliverResult(httpResponseString);
            }

        });
    }

    @SuppressWarnings("static-access")
    public long calculateResponseTime(Currency from, Currency to) {

        ResponseTimeDeterminationCallback callback = new ResponseTimeDeterminationCallback(System.nanoTime());
        caller.getHttpFromUrl(context, provideUrl(from, to), callback);
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

    private final class ResponseTimeDeterminationCallback implements AsyncJsonParserResultCallback, AsyncHttpParserResultCallback {

        boolean hasResult = false;
        long result = 0;
        private final long startTime;

        public ResponseTimeDeterminationCallback(long startTime) {
            this.startTime = startTime;
        }

        public void deliverResult(JSONObject jsonObject) {
            deliverResult((Object) jsonObject);
        }

        @Override
        public void deliverResult(String arg0) {
            deliverResult((Object) arg0);
        }

        public boolean hasResult() {
            return hasResult;
        }

        public long getResultInMillis() {
            return result;
        }


        public void deliverResult(Object arg0) {
            if (arg0 != null) {
                result = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
            } else {
                result = -1;
            }
            hasResult = true;
        }


    }

}
