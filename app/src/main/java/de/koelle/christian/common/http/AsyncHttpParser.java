package de.koelle.christian.common.http;

import android.content.Context;
import android.util.Log;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import de.koelle.christian.trickytripper.constants.Rc;
import org.json.JSONException;
import org.json.JSONObject;

public class AsyncHttpParser {
    private static final AsyncHttpClient httpClient = new AsyncHttpClient();

    public static void getHttpFromUrl(Context context, String url, final AsyncHttpParserResultCallback callback) {

        httpClient.post(context, url, null, new AsyncHttpResponseHandler() {

            @Override
            protected void sendSuccessMessage(String arg0) {
                super.sendSuccessMessage(arg0);
                callback.deliverResult(arg0);
            }

            @Override
            protected void sendFailureMessage(Throwable arg0, String arg1) {
                Log.e(Rc.LT_IO, "HttpRequest resulted in error (sendFailureMassage()): ", arg0);
                callback.deliverResult(null);
                super.sendFailureMessage(arg0, arg1);
            }

            @Override
            public void onFailure(Throwable arg0, String arg1) {
                Log.e(Rc.LT_IO, "HttpRequest resulted in error (onFailure()): ", arg0);
                callback.deliverResult(null);
                super.onFailure(arg0, arg1);
            }

        });
    }

    public static void cancelRunningRequests(Context context) {
        httpClient.cancelRequests(context, true);
    }
}
