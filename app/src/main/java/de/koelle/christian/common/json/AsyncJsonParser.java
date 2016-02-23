package de.koelle.christian.common.json;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.koelle.christian.common.http.AsyncHttpParserResultCallback;
import de.koelle.christian.trickytripper.constants.Rc;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AsyncJsonParser {
    private OkHttpClient client = new OkHttpClient();
    private List<Call> calls = Collections.synchronizedList(new ArrayList<Call>());

    public void getJSONFromUrl(Context context, String url, final AsyncJsonParserResultCallback callback) {

        Request request = new Request.Builder()
                .url(url)
                .build();

        Call call = client.newCall(request);
        calls.add(call);
        call.enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(Rc.LT_IO, "HttpRequest resulted in error (onFailure()): ", e);
                callback.deliverResult(null);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                JSONObject result = null;
                try {
                    result = new JSONObject(response.body().string());
                } catch (JSONException e) {
                    Log.e(Rc.LT_IO, "Error on creating a JSON-Object: " + e.toString());
                }
                callback.deliverResult(result);
            }
        });
    }

    public void cancelRunningRequests(Context context) {
        for (Call call : calls) {
            call.cancel();
        }
        calls.clear();
    }

}
