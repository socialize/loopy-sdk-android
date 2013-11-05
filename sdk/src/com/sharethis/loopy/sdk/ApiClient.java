package com.sharethis.loopy.sdk;

import android.os.AsyncTask;
import com.sharethis.loopy.sdk.net.HttpClientFactory;
import com.sharethis.loopy.sdk.util.JSONUtils;
import com.sharethis.loopy.sdk.util.ShortlinkCache;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Jason Polites
 */
public class ApiClient {

    public static final String SHORTLINK = "shortlink";
    public static final String SHARE = "share";
    public static final String INSTALL = "install";
    public static final String REFERRER = "referrer";
    public static final String OPEN = "open";
    public static final String LOG = "log";
    public static final String STDID = "stdid";

    public static final String AUTH_HEADER = "X-LoopyAuth";

    private static final ShortlinkCache shortlinkCache = new ShortlinkCache();
    private boolean useShortlinkCache = true;

    private HttpClientFactory httpClientFactory;

    public void start(Session session) {
        httpClientFactory = new HttpClientFactory();
        httpClientFactory.start(session.getConfig().getApiTimeout());
    }

    public void stop() {
        httpClientFactory.stop();
    }

    /**
     * Correlates to the /stdid endpoint of the Loopy API.
     *
     * @param callback A callback to handle the result.
     */
    public void stdid(String apiKey, ApiCallback callback) {

        if (Logger.isDebugEnabled()) {
            Logger.d("stdid called");
        }

        try {
            LoopyState state = getState();

            if (state.hasSTDID()) {
                JSONObject payload = getSTDIDPayload(state);
                callAsync(getAuthHeader(apiKey), payload, STDID, true, callback);
            } else {
                LoopyException error = new LoopyException("Internal STDID not found.  Make sure you call \"install\" before calling stdid", LoopyException.PARAMETER_MISSING);
                if (callback != null) {
                    callback.onError(error);
                }
                Logger.e(error);
            }
        } catch (JSONException e) {
            Logger.e(e);
            if (callback != null) {
                callback.onError(e);
            }
        }
    }

    JSONObject stdidDirect(String apiKey) throws Exception {
        if (Logger.isDebugEnabled()) {
            Logger.d("stdid called");
        }

        LoopyState state = getState();
        if (state.hasSTDID()) {
            JSONObject payload = getSTDIDPayload(state);
            return call(getAuthHeader(apiKey), payload, STDID, true);
        } else {
            throw new LoopyException("Internal STDID not found.  Make sure you call \"install\" before calling stdid", LoopyException.PARAMETER_MISSING);
        }
    }

    JSONObject getSTDIDPayload(LoopyState state) throws JSONException {
        JSONObject payload = newJSONObject();
        payload.put("stdid", state.getSTDID());
        payload.put("timestamp", getCurrentTimestamp());
        addDevice(payload, true);
        addApp(payload);
        addClient(payload);
        return payload;
    }


    /**
     * Correlates to the /install endpoint of the Loopy API
     *
     * @param referrer The referrer that lead to the installation of the app.
     * @param callback A callback to handle the result.
     */
    public void install(String apiKey, String referrer, ApiCallback callback) {

        if (Logger.isDebugEnabled()) {
            Logger.d("install called for " + referrer);
        }

        try {
            JSONObject payload = getInstallPayload(referrer);
            callAsync(getAuthHeader(apiKey), payload, INSTALL, true, callback);
        } catch (JSONException e) {
            Logger.e(e);
            if (callback != null) {
                callback.onError(e);
            }
        }
    }

    JSONObject installDirect(String apiKey, String referrer) throws Exception {
        if (Logger.isDebugEnabled()) {
            Logger.d("install called for " + referrer);
        }
        JSONObject payload = getInstallPayload(referrer);
        return call(getAuthHeader(apiKey), payload, INSTALL, true);
    }

    JSONObject getInstallPayload(String referrer) throws JSONException {
        JSONObject payload = newJSONObject();
        payload.put("timestamp", getCurrentTimestamp());
        payload.put("referrer", referrer);
        addDevice(payload, true);
        addApp(payload);
        addClient(payload);
        return payload;
    }


    /**
     * Correlates to the /referrer endpoint of the Loopy API
     *
     * @param referrer The referrer that lead to the installation of the app.
     * @param callback A callback to handle the result.
     */
    public void referrer(String apiKey, String referrer, ApiCallback callback) {

        if (Logger.isDebugEnabled()) {
            Logger.d("referrer called for " + referrer);
        }

        try {
            JSONObject payload = newJSONObject();

            LoopyState state = getState();

            if (state.hasSTDID()) {

                payload.put("stdid", state.getSTDID());
                payload.put("timestamp", getCurrentTimestamp());
                payload.put("referrer", referrer);

                addDevice(payload);
                addApp(payload);
                addClient(payload);

                callAsync(getAuthHeader(apiKey), payload, REFERRER, false, callback);
            } else {
                LoopyException error = new LoopyException("Internal STDID not found.  Make sure you call \"install\" before calling referrer", LoopyException.PARAMETER_MISSING);
                if (callback != null) {
                    callback.onError(error);
                }
                Logger.e(error);
            }
        } catch (JSONException e) {
            Logger.e(e);
            if (callback != null) {
                callback.onError(e);
            }
        }
    }


    /**
     * Correlates to the /open endpoint of the Loopy API
     *
     * @param referrer The referrer that lead to the open of the app.
     * @param callback A callback to handle the result.
     */
    public void open(String apiKey, String referrer, ApiCallback callback) {

        if (Logger.isDebugEnabled()) {
            Logger.d("open called");
        }

        try {
            JSONObject payload = newJSONObject();

            LoopyState state = getState();

            if (state.hasSTDID()) {

                payload.put("stdid", state.getSTDID());
                payload.put("timestamp", getCurrentTimestamp());
                payload.put("referrer", referrer);

                addDevice(payload);
                addApp(payload);
                addClient(payload);

                callAsync(getAuthHeader(apiKey), payload, OPEN, false, callback);
            } else {
                LoopyException error = new LoopyException("Internal STDID not found.  Make sure you call \"install\" before calling open", LoopyException.PARAMETER_MISSING);
                if (callback != null) {
                    callback.onError(error);
                }
                Logger.e(error);
            }
        } catch (JSONException e) {
            Logger.e(e);
            if (callback != null) {
                callback.onError(e);
            }
        }
    }

    /**
     * Correlates to the /shortlink endpoint of the Loopy API
     *
     * @param item     The item being shared.
     * @param callback A callback to handle the response.
     */
    public void shortlink(String apiKey, final Item item, final ApiCallback callback) {

        if (Logger.isDebugEnabled()) {
            Logger.d("shortlink called for " + item);
        }

        try {
            if (useShortlinkCache) {
                String shortlink = shortlinkCache.getShortlink(item);
                if (shortlink != null && callback != null) {
                    JSONObject result = newJSONObject();
                    result.put("shortlink", shortlink);
                    callback.onSuccess(result);
                    return;
                }
            }

            JSONObject payload = newJSONObject();

            LoopyState state = getState();

            if (state.hasSTDID()) {

                payload.put("stdid", state.getSTDID());
                payload.put("timestamp", getCurrentTimestamp());

                JSONObject itemJSON = new JSONObject();

                JSONUtils.put(itemJSON, "type", item.getType());
                JSONUtils.put(itemJSON, "url", item.getUrl());
                JSONUtils.put(itemJSON, "title", item.getTitle());
                JSONUtils.put(itemJSON, "image", item.getImageUrl());
                JSONUtils.put(itemJSON, "description", item.getDescription());
                JSONUtils.put(itemJSON, "video", item.getVideoUrl());

                payload.put("item", itemJSON);

                if (item.tags != null && item.tags.size() > 0) {
                    JSONUtils.put(payload, "tags", item.tags);
                }

                callAsync(getAuthHeader(apiKey), payload, SHORTLINK, false, new ApiCallback() {
                    @Override
                    public void onSuccess(JSONObject result) {

                        if (useShortlinkCache) {
                            String shortlink = JSONUtils.getString(result, "shortlink");
                            if (shortlink != null) {
                                shortlinkCache.add(shortlink, item);
                            }
                        }

                        if (callback != null) {
                            callback.onSuccess(result);
                        }
                    }

                    @Override
                    public void onBeforePost(Map<String, String> headers, JSONObject payload) {
                        if (callback != null) {
                            callback.onBeforePost(headers, payload);
                        }
                    }

                    @Override
                    public void onProcess(JSONObject result) {
                        if (callback != null) {
                            callback.onProcess(result);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (callback != null) {
                            callback.onError(e);
                        }
                    }
                });
            } else {
                LoopyException error = new LoopyException("Internal STDID not found.  Make sure you call \"install\" before calling shortlink", LoopyException.PARAMETER_MISSING);
                if (callback != null) {
                    callback.onError(error);
                }
                Logger.e(error);
            }
        } catch (JSONException e) {
            Logger.e(e);
            if (callback != null) {
                callback.onError(e);
            }
        }
    }

    /**
     * Correlates to the /share endpoint of the Loopy API
     *
     * @param shortlink The original shortlink generated from the /shortlink endpoint.
     * @param channel   The channel through which the share is occurring (e.g. facebook, email etc)
     * @param callback  A callback to handle the response.
     */
    public void share(String apiKey, final String shortlink, String channel, final ApiCallback callback) {

        if (Logger.isDebugEnabled()) {
            Logger.d("share called for " + shortlink + " via channel " + channel);
        }

        try {
            JSONObject payload = newJSONObject();

            LoopyState state = getState();

            if (state.hasSTDID()) {

                payload.put("stdid", state.getSTDID());
                payload.put("timestamp", getCurrentTimestamp());
                payload.put("shortlink", shortlink);

                JSONUtils.put(payload, "channel", channel);

                addDevice(payload);
                addApp(payload);
                addClient(payload);

                callAsync(getAuthHeader(apiKey), payload, SHARE, false, new ApiCallback() {
                    @Override
                    public void onSuccess(JSONObject result) {

                        if (useShortlinkCache) {
                            shortlinkCache.remove(shortlink);
                        }

                        if (callback != null) {
                            callback.onSuccess(result);
                        }
                    }

                    @Override
                    public void onBeforePost(Map<String, String> headers, JSONObject payload) {
                        if (callback != null) {
                            callback.onBeforePost(headers, payload);
                        }
                    }

                    @Override
                    public void onProcess(JSONObject result) {
                        if (callback != null) {
                            callback.onProcess(result);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (callback != null) {
                            callback.onError(e);
                        }
                    }
                });
            } else {
                LoopyException error = new LoopyException("Internal STDID not found.  Make sure you call \"install\" before calling share", LoopyException.PARAMETER_MISSING);
                if (callback != null) {
                    callback.onError(error);
                }
                Logger.e(error);
            }
        } catch (JSONException e) {
            Logger.e(e);
            if (callback != null) {
                callback.onError(e);
            }
        }
    }

    /**
     * Correlates to the /log endpoint of the Loopy API
     *
     * @param event    The event being logged.
     * @param callback A callback to handle the result.
     */
    public void log(String apiKey, Event event, ApiCallback callback) {

        if (Logger.isDebugEnabled()) {
            Logger.d("log called for " + event);
        }

        try {
            JSONObject payload = newJSONObject();

            LoopyState state = getState();

            if (state.hasSTDID()) {

                payload.put("stdid", state.getSTDID());
                payload.put("timestamp", getCurrentTimestamp());

                JSONObject eventJSON = new JSONObject();

                JSONUtils.put(eventJSON, "type", event.getType());
                JSONUtils.put(eventJSON, "meta", event.getMeta());

                payload.put("event", eventJSON);

                addDevice(payload);
                addApp(payload);
                addClient(payload);

                callAsync(getAuthHeader(apiKey), payload, LOG, false, callback);
            } else {
                LoopyException error = new LoopyException("Internal STDID not found.  Make sure you call \"install\" before calling log", LoopyException.PARAMETER_MISSING);
                if (callback != null) {
                    callback.onError(error);
                }
                Logger.e(error);
            }
        } catch (JSONException e) {
            Logger.e(e);
            if (callback != null) {
                callback.onError(e);
            }
        }
    }

    protected void callAsync(final Map<String, String> headers, final JSONObject payload, final String endpoint, final boolean secure, final ApiCallback cb) {

        if (cb != null) {
            cb.onBeforePost(headers, payload);
        }

        new AsyncTask<JSONObject, Void, JSONObject>() {

            Exception error;

            @Override
            protected JSONObject doInBackground(JSONObject... params) {
                try {
                    JSONObject result = call(headers, payload, endpoint, secure);

                    if (cb != null) {
                        cb.onProcess(result);
                    }

                    return result;
                } catch (Exception e) {
                    Logger.e(e);
                    error = e;
                    return null;
                }
            }

            @Override
            protected void onPostExecute(JSONObject result) {
                if (cb != null) {
                    if (error != null) {
                        if (error instanceof SocketTimeoutException) {
                            cb.onError(LoopyException.wrap(error, LoopyException.CLIENT_TIMEOUT));
                        } else {
                            cb.onError(error);
                        }
                    } else {
                        // Check for api error
                        if (!JSONUtils.isNull(result, "error")) {
                            try {
                                JSONObject error = result.getJSONObject("error");
                                StringBuilder builder = new StringBuilder();

                                if (!JSONUtils.isNull(error, "message")) {
                                    JSONArray messages = error.getJSONArray("message");
                                    for (int i = 0; i < messages.length(); i++) {
                                        if (i > 0) {
                                            builder.append(",");
                                        }
                                        builder.append(messages.getString(i));
                                    }
                                }

                                cb.onError(new LoopyException(builder.toString(), error.getInt("code")));
                            } catch (JSONException e) {
                                Logger.e(e);
                                cb.onError(new LoopyException("An error was returned but the error data could not be parsed", e, LoopyException.PARSE_ERROR));
                            }
                        } else {
                            cb.onSuccess(result);
                        }
                    }
                }
            }
        }.execute();
    }


    protected JSONObject call(Map<String, String> headers, JSONObject payload, String endpoint, boolean secure) throws Exception {

        HttpEntity entity = null;

        try {

            Session session = Session.getInstance().waitForStart();
            String url;

            if (secure) {
                url = session.getConfig().getSecureAPIUrl();
            } else {
                url = session.getConfig().getAPIUrl();
            }

            url += endpoint;

            HttpClient client = httpClientFactory.getClient();
            HttpPost post = new HttpPost(url);

            post.setHeader("Content-Type", "application/json");

            if (headers != null) {
                for (Map.Entry<String, String> header : headers.entrySet()) {
                    post.addHeader(header.getKey(), header.getValue());
                }
            }

            post.setEntity(new StringEntity(payload.toString(), HTTP.UTF_8));

            if (Logger.isDebugEnabled()) {
                Logger.d("calling endpoint url " + url + " with POST data [" +
                        payload +
                        "]");
            }

            HttpResponse response = client.execute(post);

            if (response != null) {
                int statusCode = response.getStatusLine().getStatusCode();
                entity = response.getEntity();
                String sResponseText = EntityUtils.toString(entity, HTTP.UTF_8);

                if (Logger.isDebugEnabled()) {
                    Logger.d("got result [" +
                            statusCode +
                            "] with data [" +
                            sResponseText +
                            "]");
                }

                if (statusCode != 200) {
                    throw new LoopyException(response.getStatusLine().getReasonPhrase(), statusCode);
                } else {
                    return new JSONObject(sResponseText);
                }
            } else {
                throw new LoopyException("Empty Response", 204);  // 204 is "no content"
            }
        } finally {
            if (entity != null) {
                entity.consumeContent();
            }
        }
    }

    // Mockable
    protected App getApp() {
        return Loopy.getInstance().getApp();
    }

    // Mockable
    protected Device getDevice() {
        return Loopy.getInstance().getDevice();
    }

    // Mockable
    protected Geo getGeo() {
        return Loopy.getInstance().getGeo();
    }

    // Mockable
    protected long getCurrentTimestamp() {
        return System.currentTimeMillis();
    }

    public LoopyState getState() {
        Session instance = Session.getInstance();
        if (instance.isStarted()) {
            instance.waitForStart();
            return instance.getState();
        } else {
            throw new LoopyException("Session not started.  Make sure you have called Loopy.onStart in the onStart method of your Activity", LoopyException.LIFECYCLE_ERROR);
        }
    }

    protected void addDevice(JSONObject payload) throws JSONException {
        addDevice(payload, false);
    }

    protected void addDevice(JSONObject payload, boolean id) throws JSONException {
        Device device = getDevice();
        if (device != null) {
            JSONObject d = newJSONObject();

            if (id) {
                d.put("id", device.getAndroidId());
            }

            JSONUtils.put(d, "model", device.getModelName());
            JSONUtils.put(d, "os", "android");
            JSONUtils.put(d, "osv", device.getAndroidVersion());
            JSONUtils.put(d, "carrier", device.getCarrier());
            JSONUtils.put(d, "wifi", device.getWifiState().state);

            Geo geo = getGeo();

            if (geo != null) {
                JSONObject g = new JSONObject();
                g.put("lat", geo.getLat());
                g.put("lon", geo.getLon());

                d.put("geo", g);
            }

            payload.put("device", d);
        }
    }

    protected void addApp(JSONObject payload) throws JSONException {
        App app = getApp();
        if (app != null) {
            JSONObject a = new JSONObject();
            JSONUtils.put(a, "id", app.getId());
            JSONUtils.put(a, "name", app.getName());
            JSONUtils.put(a, "version", app.getVersion());
            payload.put("app", a);
        }
    }

    protected void addClient(JSONObject payload) throws JSONException {
        JSONObject c = new JSONObject();
        JSONUtils.put(c, "lang", "java");
        JSONUtils.put(c, "version", Loopy.VERSION);
        payload.put("client", c);
    }

    // Mockable
    JSONObject newJSONObject() {
        return new JSONObject();
    }

    final void setHttpClientFactory(HttpClientFactory httpClientFactory) {
        this.httpClientFactory = httpClientFactory;
    }

    Map<String, String> getAuthHeader(String apiKey) {
        Map<String, String> headers = new HashMap<String, String>();
        headers.put(AUTH_HEADER, apiKey);
        return headers;
    }

    public void setUseShortlinkCache(boolean useShortlinkCache) {
        this.useShortlinkCache = useShortlinkCache;
    }
}
