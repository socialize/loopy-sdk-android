package com.sharethis.loopy.sdk;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.widget.ListView;
import com.sharethis.loopy.sdk.util.AppDataCache;
import com.sharethis.loopy.sdk.util.AppUtils;
import com.sharethis.loopy.sdk.util.StringUtils;
import org.json.JSONObject;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;


/**
 * @author Jason Polites
 */
public class Loopy {

    public static String VERSION = "1.0";

    static final String INSTALL_ACTION = "STInstallAction";

    static Loopy instance = new Loopy(new ApiClient());

    // TODO: Should be part of loopy instance.
    private static BroadcastReceiver installReceiver;

    private ApiClient apiClient;
    private Device device;
    private App app;
    private Geo geo;
    private int instances = 0;
    private boolean receiverRegistered = false;
    private CountDownLatch startLatch;

    ShareClickListener shareClickListener = new ShareClickListener();
    ShareConfig config = new ShareConfig();

    Loopy(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    static Loopy getInstance() {
        return instance;
    }

    /**
     * Should be called within the Activity lifecycle onCreate() method.
     *
     * @param context The current context.
     */
    public static void onCreate(Context context, String apiKey, String apiSecret) {

        if(Logger.isDebugEnabled()) {
            Logger.d("onCreate called for " + apiKey);
        }

        AppDataCache.getInstance().onCreate(context);
        instance.create(context, apiKey, apiSecret);
    }

    /**
     * Should be called within the Activity lifecycle onDestroy() method.
     *
     * @param context The current context.
     */
    public static void onDestroy(Context context) {
        if(Logger.isDebugEnabled()) {
            Logger.d("onDestroy called");
        }

        AppDataCache.getInstance().onDestroy(context);
        instance.destroy();
    }

    /**
     * Should be called within the Activity lifecycle onStart() method.
     *
     * @param context The current context.
     */
    public static void onStart(Context context) {
        if(Logger.isDebugEnabled()) {
            Logger.d("onStart called");
        }
        instance.start(context);
    }

    /**
     * Optional variation of onStart that takes a callback
     * @param context The current context.
     * @param callback A callback that will be notified when the start process has completed.
     */
    public static void onStart(Context context, StartCallback callback) {
        if(Logger.isDebugEnabled()) {
            Logger.d("onStart called");
        }
        instance.start(context, callback);
    }

    /**
     * Should be called within the Activity lifecycle onStop() method.
     *
     * @param context The current context.
     */
    public static void onStop(Context context) {
        if(Logger.isDebugEnabled()) {
            Logger.d("onStop called");
        }
        instance.stop(context);
    }

    /**
     * Called by the BroadcastReciever when a referred installation occurs.
     * This will be called from a separate process.
     *
     * @param context The current context.
     * @param intent  The receiver intent.
     */
    public static void onInstall(final Context context, Intent intent) {
        // Send an in-app broadcast to trigger the install on the main process thread.
        Intent installIntent = new Intent();
        installIntent.setAction(INSTALL_ACTION);
        installIntent.putExtras(intent);
        context.sendBroadcast(installIntent);
    }

    /**
     * Displays the default share dialog and presents the apps that are able to consume the content type of the share intent provided.
     *
     * @param context     The current context.
     * @param dialogTitle The title of the dialog.
     * @param url         The url to be shared.
     * @param shareIntent The original share intent
     */
    public static void showShareDialog(
            Context context,
            String dialogTitle,
            String url,
            Intent shareIntent,
            ShareDialogListener listener) {
        Item item = new Item();
        item.setUrl(url);
        showShareDialog(context, dialogTitle, item, shareIntent, listener);
    }

    /**
     * Displays the default share dialog and presents the apps that are able to consume the content type of the share intent provided.
     *
     * @param context     The current context.
     * @param dialogTitle The title of the dialog.
     * @param item        The item to be shared.
     * @param shareIntent The original share intent
     */
    public static void showShareDialog(final Context context,
                                       final String dialogTitle,
                                       final Item item,
                                       final Intent shareIntent,
                                       final ShareDialogListener listener) {
        Loopy.shorten(item, new ShareCallback() {
            @Override
            public void onResult(Item item, Throwable error) {
                if (listener != null) {
                    listener.onLinkGenerated(item, shareIntent, error);
                }

                if (error == null || !StringUtils.isEmpty(item.getUrl())) {
                    showDialog(context, dialogTitle, shareIntent, listener);
                }
            }
        });
    }

    /**
     * Displays the default share dialog and presents the apps that are able to consume the content type
     * of the share intent provided.
     * <br/>
     * NOTE: This method assumes the shortlink property of the share Item has been set.
     * @param context     The current context.
     * @param title       The title of the dialog.
     * @param item        The Item to be shared.
     * @param shareIntent The original share intent
     */
    static void showDialog(
            Context context,
            String title,
            Item item,
            Intent shareIntent,
            ShareDialogListener listener) {
        instance.doShareDialog(context, title, item, shareIntent, listener);
    }

    /**
     * Adds tracking to the given url.
     *
     * @param url      The URL to shorten.
     * @param callback A callback to handle the result.
     */
    @SuppressWarnings("unused")
    public static void shorten(String url, final ShareCallback callback) {
        final Item item = new Item();
        item.setUrl(url);
        shorten(item, callback);
    }

    /**
     * Reports a share AND generates a shortlink
     * @param item The item to be shared.
     * @param channel The channel through which the share occurred.
     * @param callback A callback to handle the result.
     */
    public static void shareAndLink(Item item, String channel, final ShareCallback callback) {
        if (callback != null) {
            callback.setItem(item);
        }
        instance.sharelink(item, channel, callback);
    }

    /**
     * Adds tracking to the given item.
     *
     * @param item     The item to track.
     * @param callback A callback to handle the result.
     */
    public static void shorten(final Item item, final ShareCallback callback) {
        if (callback != null) {
            callback.setItem(item);
        }
        instance.shortlink(item, callback);
    }

    static void reportShareFromIntent(Context context, Item item, Intent intent) {
        instance.shareFromIntent(context, item, intent);
    }

    @SuppressWarnings("unused")
    public static void reportShare(Item item, String channel) {
        instance.share(item, channel, null);
    }

    @SuppressWarnings("unused")
    public static void reportShare(Item item, String channel, ApiCallback callback) {
        instance.share(item, channel, callback);
    }

    /**
     * Sets the api key/secret combination
     *
     * @param apiKey    Your api key.
     * @param apiSecret Your api secret.
     */
    @SuppressWarnings("unused")
    public static void setApiKey(String apiKey, String apiSecret) {
        instance.config.setApiKey(apiKey);
        instance.config.setApiSecret(apiSecret);
    }

    void sharelink(Item item, String channel, ApiCallback callback) {
        getApiClient().shareLink(
                config.getApiKey(),
                config.getApiSecret(),
                item,
                channel,
                callback
        );
    }

    // Mockable
    void shortlink(Item item, ApiCallback callback) {
        getApiClient().shortlink(
                config.getApiKey(),
                config.getApiSecret(),
                item, callback);
    }

    // Mockable
    void share(Item item, String channel, ApiCallback callback) {
        if (item.getShortlink() != null) {
            getApiClient().share(
                    config.getApiKey(),
                    config.getApiSecret(),
                    item.getShortlink(),
                    channel,
                    callback);
        }
    }

    void shareFromIntent(Context context, Item item, Intent intent) {
        String app = AppDataCache.getInstance().getAppLabel(intent.getComponent().getPackageName(), intent.getComponent().getClassName());

        if (app == null) {
            PackageManager pm = context.getPackageManager();
            try {
                if(pm != null) {
                    ActivityInfo activityInfo = pm.getActivityInfo(intent.getComponent(), 0);
                    if(activityInfo != null) {
                        CharSequence label = activityInfo.loadLabel(pm);
                        if(label != null) {
                            app = label.toString();
                        }
                    }
                }
            } catch (Exception e) {
                Logger.e(e);
            }
        }

        if (app != null) {
            share(item, app, null);
        }
    }

    void doShareDialog(
            final Context context,
            final String title,
            final Item item,
            final Intent shareIntent,
            final ShareDialogListener dialogListener) {

        final LayoutInflater inflater = getInflater(context);

        new AsyncTask<Void, Void, Void>() {

            ListView view;
            ShareDialogAdapter adapter;

            @Override
            protected Void doInBackground(Void... params) {
                String type = shareIntent.getType();

                if (type == null) {
                    type = "text/plain";
                }

                Collection<ResolveInfo> appsForContentType = getAppUtils().getAppsForContentType(context, type);

                adapter = newShareDialogAdapter(context, getAppList(appsForContentType));

                adapter.setOnShareClickListener(shareClickListener);
                adapter.setShareItem(item);
                adapter.setShareIntent(shareIntent);
                adapter.setConfig(config);

                view = (ListView) inflater.inflate(R.layout.st_share_dialog_list, null);

                if(view != null) {
                    view.setAdapter(adapter);
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                if (view != null) {
                    AlertDialog.Builder builder = newAlertDialogBuilder(new ContextThemeWrapper(context, R.style.STDialogTheme));
                    builder.setTitle(title);
                    builder.setView(view);
                    AlertDialog alertDialog = builder.create();

                    if (dialogListener != null) {
                        alertDialog.setOnCancelListener(dialogListener);
                        alertDialog.setOnShowListener(dialogListener);
                        adapter.setShareDialogListener(dialogListener);
                    }

                    adapter.setDialog(alertDialog);

                    alertDialog.show();
                }
            }
        }.execute();
    }

    List<ShareDialogRow> getAppList(Collection<ResolveInfo> appsForContentType) {

        List<ShareDialogRow> appdata = new LinkedList<ShareDialogRow>();

        int i = 0;
        ShareDialogRow row = null;

        if (appsForContentType != null) {
            for (ResolveInfo resolveInfo : appsForContentType) {
                if (i % 2 == 0) { // even
                    row = new ShareDialogRow();
                    appdata.add(row);
                    row.left = resolveInfo;
                } else if (row != null) {
                    row.right = resolveInfo;
                }
                i++;
            }
        }

        return appdata;
    }

    @SuppressWarnings("unused")
    public static final class Channel {
        public static final String FACEBOOK = "facebook";
        public static final String TWITTER = "twitter";
        public static final String GOOGLEPLUS = "googleplus";
        public static final String PINTEREST = "pinterest";
        public static final String EMAIL = "email";
        public static final String SMS = "sms";
    }

    // mockable
    AppUtils getAppUtils() { return AppUtils.getInstance(); }

    // mockable
    ShareDialogAdapter newShareDialogAdapter(Context context, List<ShareDialogRow> appList) {
        return new ShareDialogAdapter(context, appList);
    }

    // mockable
    AlertDialog.Builder newAlertDialogBuilder(Context context) { return new AlertDialog.Builder(context); }

    // mockable
    LayoutInflater getInflater(Context context) { return LayoutInflater.from(context); }

    // Internal use only
    static void setInstance(Loopy loopy) {
        instance = loopy;
    }

    protected App getApp() {
        return app;
    }

    protected Device getDevice() {
        return device;
    }

    protected Geo getGeo() {
        return geo;
    }

    // Mockable
    protected Session getSession() {
        return Session.getInstance();
    }

    public ApiClient getApiClient() {
        return apiClient;
    }

    @SuppressWarnings("unused")
    protected void setApiClient(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    protected void create(Context context, String apiKey, String apiSecret) {
        if (instances == 0) {
            this.device = new Device().onCreate(context);
            this.app = new App().onCreate(context);
            this.config.setApiKey(apiKey);
            this.config.setApiSecret(apiSecret);
            if (Geo.hasPermission(context)) {
                this.geo = new Geo();
            }
        }
        instances++;
    }

    protected void start(Context context) {
        start(context, null);
    }

    protected void start(final Context context, final StartCallback cb) {

        startLatch = new CountDownLatch(1);

        if (geo != null) {
            geo.onStart(context);
        }

        final Session session = getSession();

        session.start(context);

        unregisterReceiver(context);
        registerReceiver(context);

        // Check for install or open
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {

                try {
                    session.waitForStart();

                    apiClient.start(session);

                    if (session.getState() != null) {

                        if (session.getState().hasSTDID()) {

                            long currentTime = System.currentTimeMillis();
                            long sessionTimeoutMS = session.getConfig().getSessionTimeoutSeconds() * 1000;
                            long lastOpenTime = session.getState().getLastOpenTime();

                            if (currentTime - lastOpenTime >= sessionTimeoutMS) {
                                try {
                                    apiClient.openDirect(
                                            config.getApiKey(),
                                            config.getApiSecret(),
                                            session.getState().getSTDID(),
                                            null);

                                    session.getState().setLastOpenTime(currentTime);
                                    session.getState().save(context);
                                } catch (Exception e) {
                                    Logger.e(e);
                                }
                            }
                        } else {
                            try {
                                final String stdid = generateUUID();

                                apiClient.installDirect(
                                        config.getApiKey(),
                                        config.getApiSecret(),
                                        stdid,
                                        null);

                                session.getState().setStdid(stdid);
                                session.getState().save(context);
                            } catch (Exception e) {
                                Logger.e(e);
                            }
                        }
                    } else {
                        Logger.w("No session state during start.  This should not happen");
                    }
                } finally {
                    startLatch.countDown();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                if(cb != null) {
                    cb.onComplete();
                }
            }
        }.execute();
    }

    // Mockable
    String generateUUID() {
        return UUID.randomUUID().toString();
    }

    protected void registerReceiver(Context context) {
        if (!receiverRegistered) {

            if(installReceiver == null) {
                installReceiver = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        instance.trackInstall(context, intent);
                    }
                };
            }

            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(INSTALL_ACTION);
            context.registerReceiver(installReceiver, intentFilter);
            receiverRegistered = true;
        }
    }

    protected void unregisterReceiver(Context context) {
        if(installReceiver != null) {
            try {
                context.unregisterReceiver(installReceiver);
            } catch (Exception ignore) {}
            receiverRegistered = false;
        }
    }

    protected void stop(Context context) {
        if (geo != null) {
            geo.onStop(context);
        }

        unregisterReceiver(context);

        apiClient.stop();

        getSession().stop(context);
    }

    protected void destroy() {
        instances--;
        if (instances <= 0) {
            instances = 0;
        }
    }

    protected void trackInstall(final Context context, Intent intent) {

        Bundle extras = intent.getExtras();

        if(extras != null) {
            final String referrerString = extras.getString("referrer");

            if(!StringUtils.isEmpty(referrerString)) {
                if (Logger.isDebugEnabled()) {
                    Logger.d("Received install referrer [" +
                            referrerString +
                            "]");
                }

                apiClient.referrer(
                        config.getApiKey(),
                        config.getApiSecret(), referrerString, new ApiCallback() {
                    @Override
                    public void onSuccess(JSONObject result) {
                        // Just log
                        if (Logger.isDebugEnabled()) {
                            Logger.d("Install referrer recorded");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        // We couldn't save the referrer, queue for later
                        Logger.e(e);

                        if (getSession().isStarted()) {
                            Session session = getSession().waitForStart();

                            // TODO: Queue referrer for sending later
                            session.getState().setReferrer(referrerString);
                            session.getState().save(context, null);
                        }
                    }
                });
            }
        }
    }

    protected boolean waitForStart(long timeout) {
        if (startLatch != null) {
            try {
                return startLatch.await(timeout, TimeUnit.MILLISECONDS);
            } catch (InterruptedException ignore) {}
        }
        return false;
    }
}
