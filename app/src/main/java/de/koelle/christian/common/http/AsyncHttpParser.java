package de.koelle.christian.common.http;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.koelle.christian.trickytripper.constants.Rc;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AsyncHttpParser {

    private OkHttpClient client = new OkHttpClient();
    private List<Call> calls = Collections.synchronizedList(new ArrayList<Call>());

    public void getHttpFromUrl(Context context, String url, final AsyncHttpParserResultCallback callback) {

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
                callback.deliverResult(getStringFromResponse(response));
            }
        });
    }


    protected String getStringFromResponse(Response response) throws IOException {
        return response.body().string();
    }

    public void cancelRunningRequests(Context context) {
        for (Call call : calls) {
            call.cancel();
        }
        calls.clear();
    }
}
