package com.sharethis.loopy.sdk;

import com.sharethis.loopy.sdk.net.HttpClientFactory;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * @author Jason Polites
 */
public class LoopyAccess {
    public static Loopy getLoopy() {
        return Loopy.getInstance();
    }

    public static ApiClient getApiClient() {
        return Loopy.getInstance().getApiClient();
    }

    public static void setApiClient(ApiClient client) {
        Loopy.getInstance().setApiClient(client);
    }

    public static void setHttpClientFactory(HttpClientFactory factory) {
        Loopy.getInstance().getApiClient().setHttpClientFactory(factory);
    }

    public static void setLoopy(Loopy loopyPrivate) {
        Loopy.setInstance(loopyPrivate);
    }

    public static ApiCallback wrapDelay(final ApiCallback callback, final int delay) {

        return new ApiCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                callback.onSuccess(result);
            }

            @Override
            public void onError(Throwable e) {
                callback.onError(e);
            }

            @Override
            public void onProcess(JSONObject result) {
                callback.onProcess(result);
            }

            @Override
            public void onBeforePost(Map<String, String> headers, JSONObject payload) {

                if(delay > 0) {
                    try {
                        JSONObject mock = new JSONObject();
                        mock.put("hang", delay);
                        payload.put("mock", mock);
                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                callback.onBeforePost(headers, payload);
            }
        };
    }
}
