package com.sharethis.loopy.test;

import android.app.AlertDialog;
import android.app.MockAlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.test.mock.MockContext;
import android.view.LayoutInflater;
import android.widget.ListAdapter;
import android.widget.ListView;
import com.sharethis.loopy.sdk.ApiCallback;
import com.sharethis.loopy.sdk.ApiClient;
import com.sharethis.loopy.sdk.Config;
import com.sharethis.loopy.sdk.Item;
import com.sharethis.loopy.sdk.Loopy;
import com.sharethis.loopy.sdk.LoopyState;
import com.sharethis.loopy.sdk.MockApiClient;
import com.sharethis.loopy.sdk.MockLoopy;
import com.sharethis.loopy.sdk.MockShareCallback;
import com.sharethis.loopy.sdk.MockShareClickListener;
import com.sharethis.loopy.sdk.MockShareConfig;
import com.sharethis.loopy.sdk.MockShareDialogAdapter;
import com.sharethis.loopy.sdk.R;
import com.sharethis.loopy.sdk.Session;
import com.sharethis.loopy.sdk.ShareDialogAdapter;
import com.sharethis.loopy.sdk.ShareDialogListener;
import com.sharethis.loopy.sdk.ShareDialogRow;
import com.sharethis.loopy.sdk.util.AppUtils;
import com.sharethis.loopy.sdk.util.MockAppDataCache;
import com.sharethis.loopy.test.util.Holder;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * @author Jason Polites
 */
public class LoopyTest extends LoopyAndroidTestCase {

    final String apiKey = "foobar";
    final String apiSecret = "foobar_secret";

    public void testStartCallsOpen() throws Exception {

        MockApiClient apiClient = Mockito.mock(MockApiClient.class);
        final Session session = Mockito.mock(Session.class);
        final Config config = Mockito.mock(Config.class);
        final LoopyState state = Mockito.spy(new LoopyState());
        final String stdid = "foobar";

        Mockito.when(session.getConfig()).thenReturn(config);
        Mockito.when(session.waitForStart()).thenReturn(session);
        Mockito.when(session.getState()).thenReturn(state);
        Mockito.when(state.hasSTDID()).thenReturn(true); // We have already been opened
        Mockito.when(state.getSTDID()).thenReturn(stdid);
        Mockito.when(config.getSessionTimeoutSeconds()).thenReturn(60);

        MockLoopy loopy = new MockLoopy(apiClient) {
            @Override
            public Session getSession() {
                return session;
            }
        };

        MockLoopy.setInstance(loopy);
        MockLoopy.onCreate(getContext(), apiKey, apiSecret);

        loopy.start(getContext());

        // Wait for start to complete
        assertTrue(loopy.waitForStart(3000));

        Mockito.verify(apiClient).openDirect(apiKey, apiSecret, stdid, null);
    }

    public void testStartDoesNotCallMultipleOpens() throws Exception {

        MockApiClient apiClient = Mockito.mock(MockApiClient.class);
        final Session session = Mockito.mock(Session.class);
        final Config config = Mockito.mock(Config.class);
        final LoopyState state = Mockito.spy(new LoopyState());
        final String stdid = "foobar";

        Mockito.when(session.waitForStart()).thenReturn(session);
        Mockito.when(session.getConfig()).thenReturn(config);
        Mockito.when(session.getState()).thenReturn(state);
        Mockito.when(state.hasSTDID()).thenReturn(true); // We have already been opened
        Mockito.when(state.getSTDID()).thenReturn(stdid);
        Mockito.when(config.getSessionTimeoutSeconds()).thenReturn(60);

        MockLoopy loopy = new MockLoopy(apiClient) {
            @Override
            public Session getSession() {
                return session;
            }
        };

        MockLoopy.setInstance(loopy);
        MockLoopy.onCreate(getContext(), apiKey, apiSecret);

        loopy.start(getContext());

        // Wait for start to complete
        assertTrue(loopy.waitForStart(3000));

        loopy.start(getContext());

        // Wait for start to complete
        assertTrue(loopy.waitForStart(3000));

        Mockito.verify(apiClient, Mockito.times(1)).openDirect(apiKey, apiSecret, stdid, null);
    }

    public void testStartCallsInstall() throws Exception {

        MockApiClient apiClient = Mockito.mock(MockApiClient.class);
        final Session session = Mockito.mock(Session.class);
        final LoopyState state = Mockito.mock(LoopyState.class);

        final String uuid = "foobar_uuid";

        Mockito.when(session.waitForStart()).thenReturn(session);
        Mockito.when(session.getState()).thenReturn(state);
        Mockito.when(state.hasSTDID()).thenReturn(false); // We have NOT been opened

        MockLoopy loopy = new MockLoopy(apiClient) {
            @Override
            public Session getSession() {
                return session;
            }

            @Override
            public String generateUUID() {
                return uuid;
            }
        };

        MockLoopy.setInstance(loopy);
        MockLoopy.onCreate(getContext(), apiKey, apiSecret);

        loopy.start(getContext());

        // Wait for start to complete
        assertTrue(loopy.waitForStart(3000));

        Mockito.verify(apiClient).installDirect(apiKey, apiSecret, uuid, null);
        Mockito.verify(state).setStdid(uuid);
        Mockito.verify(state).save(getContext());
    }


    public void testLifeCycle() {

        MockLoopy lp = Mockito.mock(MockLoopy.class);
        MockAppDataCache cache = Mockito.mock(MockAppDataCache.class);

        MockAppDataCache.setInstance(cache);
        MockLoopy.setInstance(lp);

        final Context context = getLocalContext();

        Loopy.onCreate(context, apiKey, apiSecret);
        Loopy.onStart(context);
        Loopy.onStop(context);
        Loopy.onDestroy(context);

        Mockito.verify(lp).create(context, apiKey, apiSecret);
        Mockito.verify(lp).start(context);
        Mockito.verify(lp).stop(context);
        Mockito.verify(lp).destroy();
        Mockito.verify(cache).onCreate(context);
    }

    public void testStaticShortenWithUrl() {
        final String url = "foobar_url";
        final MockShareCallback callback = Mockito.mock(MockShareCallback.class);

        MockLoopy lp = Mockito.mock(MockLoopy.class);

        MockLoopy.setInstance(lp);

        Loopy.shorten(url, callback);

        Mockito.verify(lp).shortlink(
                (Item) Mockito.any(),
                Mockito.eq(callback));

        Mockito.verify(callback).setItem((Item) Mockito.any());
    }

    public void testStaticShortenWithItem() {
        final Item item = Mockito.mock(Item.class);
        final MockShareCallback callback = Mockito.mock(MockShareCallback.class);

        MockLoopy lp = Mockito.mock(MockLoopy.class);

        MockLoopy.setInstance(lp);

        Loopy.shorten(item, callback);

        Mockito.verify(lp).shortlink(
                item,
                callback);

        Mockito.verify(callback).setItem(item);
    }

    public void testStaticReportShareFromIntent() {
        final Context context = getLocalContext();
        final Intent intent = new Intent();
        final Item item = Mockito.mock(Item.class);

        MockLoopy lp = Mockito.mock(MockLoopy.class);
        MockLoopy.setInstance(lp);
        MockLoopy._reportShareFromIntent(context, item, intent);

        Mockito.verify(lp).shareFromIntent(context, item, intent);
    }

    public void testStaticReportShare() {
        final Item item = Mockito.mock(Item.class);
        final String channel = "foobar";
        final ApiCallback callback = Mockito.mock(ApiCallback.class);

        MockLoopy lp = Mockito.mock(MockLoopy.class);
        MockLoopy.setInstance(lp);

        Loopy.reportShare(item, channel, callback);
        Mockito.verify(lp).share(item, channel, callback);
    }

    public void testStaticSetApiKey() {
        final String key = "foo";
        final String secret = "bar";

        MockShareConfig config = Mockito.mock(MockShareConfig.class);

        MockLoopy lp = new MockLoopy();
        lp.setConfig(config);

        MockLoopy.setInstance(lp);

        Loopy.setApiKey(key, secret);

        Mockito.verify(config).setApiKey(key);
        Mockito.verify(config).setApiSecret(secret);

    }

    public void testShortlink() {

        final ApiClient client = Mockito.mock(ApiClient.class);
        final MockShareConfig config = Mockito.mock(MockShareConfig.class);
        final Item item = Mockito.mock(Item.class);
        final ApiCallback callback = Mockito.mock(ApiCallback.class);

        MockLoopy loopy = new MockLoopy() {
            @Override
            public ApiClient getApiClient() {
                return client;
            }
        };

        MockLoopy.setInstance(loopy);

        loopy.setConfig(config);

        Mockito.when(config.getApiKey()).thenReturn(apiKey);
        Mockito.when(config.getApiSecret()).thenReturn(apiSecret);

        loopy.shortlink(item, callback);

        Mockito.verify(client).shortlink(apiKey, apiSecret, item, callback);
    }

    public void testShare() {
        final String channel = "foobar_channel";
        final String shortlink = "foobar_shortlink";
        final ApiClient client = Mockito.mock(ApiClient.class);
        final MockShareConfig config = Mockito.mock(MockShareConfig.class);
        final Item item = Mockito.mock(Item.class);
        final ApiCallback callback = Mockito.mock(ApiCallback.class);

        MockLoopy loopy = new MockLoopy() {
            @Override
            public ApiClient getApiClient() {
                return client;
            }
        };

        MockLoopy.setInstance(loopy);
        loopy.setConfig(config);

        Mockito.when(item.getShortlink()).thenReturn(shortlink);
        Mockito.when(config.getApiKey()).thenReturn(apiKey);
        Mockito.when(config.getApiSecret()).thenReturn(apiSecret);

        loopy.share(item, channel, callback);

        Mockito.verify(client).share(apiKey, apiSecret, shortlink, channel, callback);
    }

    public void testShareFromIntent() throws PackageManager.NameNotFoundException {

        final String appName = "foobar_app";
        final Item item = Mockito.mock(Item.class);
        final MockAppDataCache appDataCache = Mockito.mock(MockAppDataCache.class);
        final PackageManager packageManager = Mockito.mock(PackageManager.class);
        final ActivityInfo activityInfo = Mockito.mock(ActivityInfo.class);
        final Context context = Mockito.mock(MockContext.class);
        final ComponentName cn = new ComponentName("foo", "bar");
        final Intent intent = new Intent();
        final MockLoopy loopy = Mockito.mock(MockLoopy.class);
        intent.setComponent(cn);

        MockAppDataCache.setInstance(appDataCache);

        Mockito.when(appDataCache.getAppLabel("foo", "bar")).thenReturn(null);
        Mockito.when(context.getPackageManager()).thenReturn(packageManager);
        Mockito.when(packageManager.getActivityInfo(cn, 0)).thenReturn(activityInfo);
        Mockito.when(activityInfo.loadLabel(packageManager)).thenReturn(appName);

        Mockito.doCallRealMethod().when(loopy).shareFromIntent(context, item, intent);

        loopy.shareFromIntent(context, item, intent);

        Mockito.verify(loopy).share(item, appName, null);

    }

    public void testGetAppsList() {

        ResolveInfo info0 = new ResolveInfo() {
            @Override
            public String toString() { return "0"; }
        };
        ResolveInfo info1 = new ResolveInfo() {
            @Override
            public String toString() { return "1"; }
        };
        ResolveInfo info2 = new ResolveInfo() {
            @Override
            public String toString() { return "2"; }
        };

        List<ResolveInfo> infos = Arrays.asList(info0, info1, info2);

        MockLoopy loopy = new MockLoopy();

        List<ShareDialogRow> appList = loopy.getAppList(infos);

        assertNotNull(appList);
        assertEquals(2, appList.size());

        ShareDialogRow row0 = appList.get(0);

        assertNotNull(row0);
        assertNotNull(row0.left);
        assertNotNull(row0.right);

        assertSame(info0, row0.left);
        assertSame(info1, row0.right);

        ShareDialogRow row1 = appList.get(1);

        assertNotNull(row1);
        assertNotNull(row1.left);
        assertNull(row1.right);
        assertSame(info2, row1.left);
    }

    @SuppressWarnings ("unchecked")
    public void testDoShareDialog() throws Throwable {

        final String contentType = "*/*";
        final String title = "foobar";
        final Context context = getContext();


        final Item item = Mockito.mock(Item.class);
        final AppUtils appUtils = Mockito.mock(AppUtils.class);

        final List<ShareDialogRow> dialogRows = (List<ShareDialogRow>) Mockito.mock(List.class);
        final AlertDialog.Builder builder = Mockito.mock(AlertDialog.Builder.class);
        final MockShareDialogAdapter adapter = Mockito.spy(new MockShareDialogAdapter(context, null));
        final LayoutInflater layoutInflater = Mockito.mock(LayoutInflater.class);

        final MockShareConfig config = Mockito.mock(MockShareConfig.class);
        final MockShareClickListener shareClickListener = Mockito.mock(MockShareClickListener.class);
        final ShareDialogListener dialogListener = Mockito.mock(ShareDialogListener.class);
        final Intent shareIntent = new Intent();

        final Collection<ResolveInfo> appsForContentType = Arrays.asList(new ResolveInfo());

        // Mocking ListView fails due to: https://code.google.com/p/android/issues/detail?id=46766
        final Holder<ListAdapter> adapterHolder = new Holder<ListAdapter>();
        final ListView view = new ListView(context) {
            @Override
            public void setAdapter(ListAdapter adapter) {
                adapterHolder.set(adapter);
            }
        };

        final AlertDialog dialog = Mockito.spy(new MockAlertDialog(context));

        final MockLoopy loopy = new MockLoopy() {
            @Override
            public AppUtils getAppUtils() {
                return appUtils;
            }

            @Override
            public ShareDialogAdapter newShareDialogAdapter(Context context, List<ShareDialogRow> appList) {
                return adapter;
            }

            @Override
            public AlertDialog.Builder newAlertDialogBuilder(Context context) {
                return builder;
            }

            @Override
            public List<ShareDialogRow> getAppList(Collection<ResolveInfo> appsForContentType) {
                return dialogRows;
            }

            @Override
            public LayoutInflater getInflater(Context context) {
                return layoutInflater;
            }
        };

        Mockito.when(layoutInflater.inflate(R.layout.st_share_dialog_list, null)).thenReturn(view);
        Mockito.when(appUtils.getAppsForContentType(context, contentType)).thenReturn(appsForContentType);
        Mockito.when(builder.create()).thenReturn(dialog);

        loopy.setConfig(config);
        loopy.setShareClickListener(shareClickListener);

        runTestOnUiThread(new Runnable() {
            @Override
            public void run() {
                loopy.doShareDialog(context, title, item, shareIntent, dialogListener);
            }
        });

        // Wait for async task to complete
        sleep(1500);

        Mockito.verify(adapter).setOnShareClickListener(shareClickListener);
        Mockito.verify(adapter).setShareItem(item);
        Mockito.verify(adapter).setShareIntent(shareIntent);
        Mockito.verify(adapter).setConfig(config);
        Mockito.verify(adapter).setDialog(dialog);
        Mockito.verify(builder).setTitle(title);
        Mockito.verify(builder).setView(view);
        Mockito.verify(dialog).show();
        Mockito.verify(dialog).setOnCancelListener(dialogListener);
        Mockito.verify(dialog).setOnShowListener(dialogListener);
        Mockito.verify(adapter).setShareDialogListener(dialogListener);

        assertNotNull(adapterHolder.get());
        assertSame(adapter, adapterHolder.get());
    }

}
