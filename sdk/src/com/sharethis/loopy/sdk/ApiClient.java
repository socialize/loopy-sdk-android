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

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Jason Polites
 */
public class ApiClient {

    public static final String SHORTLINK = "shortlink";
    public static final String SHARE = "share";
    public static final String SHARELINK = "sharelink";
    public static final String INSTALL = "install";
    public static final String REFERRER = "referrer";
    public static final String OPEN = "open";
    public static final String LOG = "log";

    public static final String AUTH_HEADER_KEY = "X-LoopyAppID";
    public static final String AUTH_HEADER_SECRET = "X-LoopyKey";

    private static final ShortlinkCache shortlinkCache = new ShortlinkCache();
    private boolean useShortlinkCache = true;

    private HttpClientFactory httpClientFactory;

    public void start(Session session) {
        httpClientFactory = new HttpClientFactory();
        httpClientFactory.start(session.getConfig().getApiTimeout());
    }

    public void stop() {
        if(httpClientFactory != null) {
            httpClientFactory.stop();
        }
    }

    /**
     * Correlates to the /install endpoint of the Loopy API
     *
     * @param referrer The referrer that lead to the installation of the app.
     * @param callback A callback to handle the result.
     */
    @SuppressWarnings("unused")
    public void install(String apiKey, String apiSecret, String stdid, String referrer, ApiCallback callback) {

        if (Logger.isDebugEnabled()) {
            Logger.d("install called for " + referrer);
        }

        try {
            JSONObject payload = getInstallOpenPayload(stdid, referrer, true);
            callAsync(getAuthHeader(apiKey, apiSecret), payload, INSTALL, true, callback);
        } catch (Exception e) {
            Logger.e(e);
            if (callback != null) {
                callback.onError(e);
            }
        }
    }

    void installDirect(String apiKey, String apiSecret, String stdid, String referrer) throws Exception {
        if (Logger.isDebugEnabled()) {
            Logger.d("install called for " + referrer);
        }
        JSONObject payload = getInstallOpenPayload(stdid, referrer, true);
        call(getAuthHeader(apiKey, apiSecret), payload, INSTALL, true);
    }


    /**
     * Correlates to the /open endpoint of the Loopy API
     *
     * @param referrer The referrer that lead to the open of the app.
     * @param callback A callback to handle the result.
     */
    @SuppressWarnings("unused")
    public void open(String apiKey, String apiSecret, String referrer, ApiCallback callback) {

        if (Logger.isDebugEnabled()) {
            Logger.d("open called for " + referrer);
        }

        try {
            JSONObject payload = getInstallOpenPayload(null, referrer, false);
            doAsyncCall(apiKey, apiSecret, payload, OPEN, false, callback, null);
        } catch (Exception e) {
            Logger.e(e);
            if (callback != null) {
                callback.onError(e);
            }
        }
    }

    void openDirect(String apiKey, String apiSecret, String stdid, String referrer) throws Exception {

        if (Logger.isDebugEnabled()) {
            Logger.d("open called for " + referrer);
        }
        JSONObject payload = getInstallOpenPayload(stdid, referrer, false);
        call(getAuthHeader(apiKey, apiSecret), payload, OPEN, true);
    }

    /**
     * Correlates to the /referrer endpoint of the Loopy API
     *
     * @param referrer The referrer that lead to the installation of the app.
     * @param callback A callback to handle the result.
     */
    public void referrer(String apiKey, String apiSecret, String referrer, ApiCallback callback) {

        if (Logger.isDebugEnabled()) {
            Logger.d("referrer called for " + referrer);
        }

        try {
            JSONObject payload = newJSONObject();

            payload.put("referrer", referrer);

            addDevice(payload);
            addApp(payload);
            addClient(payload);

            doAsyncCall(apiKey, apiSecret, payload, REFERRER, false, callback, null);
        } catch (Exception e) {
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
    public void shortlink(String apiKey, String apiSecret, final Item item, final ApiCallback callback) {

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
            Device device = getDevice();

            if (device != null) {
                payload.put("md5id", device.getMd5Id());
            }

            payload.put("item", getItemPayload(item));

            if (item.tags != null && item.tags.size() > 0) {
                JSONUtils.put(payload, "tags", item.tags);
            }

            doAsyncCall(apiKey, apiSecret, payload, SHORTLINK, false, callback, new ApiClientCallback() {
                @Override
                public void onSuccess(JSONObject result) {
                    if (useShortlinkCache) {
                        String shortlink = JSONUtils.getString(result, "shortlink");
                        if (shortlink != null) {
                            shortlinkCache.add(shortlink, item);
                        }
                    }
                }
            });
        } catch (Exception e) {
            Logger.e(e);
            if (callback != null) {
                callback.onError(e);
            }
        }
    }

    /**
     * Correlates to the /sharelink endpoint of the Loopy API.  This creates a shortlink and generates a share event
     * in a single call.
     * @param item      The item being shared.
     * @param channel   The channel through which the share is occurring (e.g. facebook, email etc)
     * @param callback  A callback to handle the response.
     */
    @SuppressWarnings("unused")
    public void shareLink(String apiKey, String apiSecret, final Item item, String channel, final ApiCallback callback) {
        if (Logger.isDebugEnabled()) {
            Logger.d("shareLink called for " + item + " via channel " + channel);
        }

        try {
            JSONObject payload = newJSONObject();
            payload.put("item", getItemPayload(item));
            payload.put("channel", channel);

            addDevice(payload);
            addApp(payload);
            addClient(payload);

            doAsyncCall(apiKey, apiSecret, payload, SHARELINK, false, callback, new ApiClientCallback() {
                @Override
                public void onSuccess(JSONObject result) {
                    if (useShortlinkCache) {
                        String shortlink = JSONUtils.getString(result, "shortlink");
                        if (shortlink != null) {
                            shortlinkCache.remove(shortlink);
                        }
                    }
                }
            });
        } catch (Exception e) {
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
    public void share(String apiKey, String apiSecret, final String shortlink, String channel, final ApiCallback callback) {

        if (Logger.isDebugEnabled()) {
            Logger.d("share called for " + shortlink + " via channel " + channel);
        }

        try {
            JSONObject payload = newJSONObject();
            payload.put("shortlink", shortlink);
            payload.put("channel", channel);

            addDevice(payload);
            addApp(payload);
            addClient(payload);

            doAsyncCall(apiKey, apiSecret, payload, SHARE, false, callback, new ApiClientCallback() {
                @Override
                public void onSuccess(JSONObject result) {
                    if (useShortlinkCache) {
                        shortlinkCache.remove(shortlink);
                    }
                }
            });
        } catch (Exception e) {
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
    @SuppressWarnings("unused")
    public void log(String apiKey, String apiSecret, Event event, ApiCallback callback) {

        if (Logger.isDebugEnabled()) {
            Logger.d("log called for " + event);
        }

        try {
            JSONObject eventJSON = new JSONObject();

            if(event != null) {
                JSONUtils.put(eventJSON, "type", event.getType());
                JSONUtils.put(eventJSON, "meta", event.getMeta());
            }

            JSONObject payload = newJSONObject();

            payload.put("event", eventJSON);

            addDevice(payload);
            addApp(payload);
            addClient(payload);

            doAsyncCall(apiKey, apiSecret, payload, LOG, false, callback, null);
        } catch (Exception e) {
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

                try {
                    if (cb != null) {
                        if (error != null) {
                            if (error instanceof SocketTimeoutException) {
                                cb.onError(LoopyException.wrap(error, LoopyException.CLIENT_TIMEOUT));
                            } else {
                                // Hack for Android <= 2.3
                                if(error instanceof IOException && error.getMessage().equals("Request aborted")) {
                                    cb.onError(LoopyException.wrap(error, LoopyException.CLIENT_TIMEOUT));
                                } else {
                                    cb.onError(error);
                                }
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
                                } catch (Exception e) {
                                    Logger.e(e);
                                    cb.onError(new LoopyException("An error was returned but the error data could not be parsed", e, LoopyException.PARSE_ERROR));
                                }
                            } else {
                                cb.onSuccess(result);
                            }
                        }
                    }
                } catch (Throwable e) {
                    // We NEVER want to bubble any exception up to the calling/ui thread.
                    Logger.e(e);
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
                    throw new LoopyException(response.getStatusLine().getReasonPhrase() + ":\n" + sResponseText, statusCode);
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

    void doAsyncCall(String apiKey, String apiSecret, JSONObject payload, final String endpoint, boolean secure, final ApiCallback callback, final ApiClientCallback apiCallback) {

        try {

            LoopyState state = getState();

            if (state.hasSTDID()) {

                payload.put("stdid", state.getSTDID());
                payload.put("timestamp", getCurrentTimestamp());

                callAsync(getAuthHeader(apiKey, apiSecret), payload, endpoint, secure, new ApiCallback() {
                    @Override
                    public void onSuccess(JSONObject result) {

                        if (apiCallback != null) {
                            apiCallback.onSuccess(result);
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
        } catch (Exception e) {
            Logger.e(e);
            if (callback != null) {
                callback.onError(e);
            }
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
            payload.put("md5id", device.getMd5Id());

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

    JSONObject getInstallOpenPayload(String stdid, String referrer, boolean deviceId) throws JSONException {
        JSONObject payload = newJSONObject();
        if(stdid != null) {
            payload.put("stdid", stdid);
        }
        payload.put("timestamp", getCurrentTimestamp());
        payload.put("referrer", referrer);
        addDevice(payload, deviceId);
        addApp(payload);
        addClient(payload);
        return payload;
    }

    JSONObject getItemPayload(Item item) throws JSONException {
        JSONObject itemJSON = new JSONObject();
        JSONUtils.put(itemJSON, "type", item.getType());
        JSONUtils.put(itemJSON, "url", item.getUrl());
        JSONUtils.put(itemJSON, "title", item.getTitle());
        JSONUtils.put(itemJSON, "image", item.getImageUrl());
        JSONUtils.put(itemJSON, "description", item.getDescription());
        JSONUtils.put(itemJSON, "video", item.getVideoUrl());
        return itemJSON;
    }

    // Mockable
    JSONObject newJSONObject() {
        return new JSONObject();
    }

    Map<String, String> getAuthHeader(String apiKey, String apiSecret) {
        Map<String, String> headers = new HashMap<String, String>();
        headers.put(AUTH_HEADER_KEY, apiKey);
        headers.put(AUTH_HEADER_SECRET, apiSecret);
        return headers;
    }

    @SuppressWarnings("unused")
    final void setHttpClientFactory(HttpClientFactory httpClientFactory) {
        this.httpClientFactory = httpClientFactory;
    }

    @SuppressWarnings("unused")
    public void setUseShortlinkCache(boolean useShortlinkCache) {
        this.useShortlinkCache = useShortlinkCache;
    }
}
