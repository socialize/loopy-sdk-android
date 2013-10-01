package com.sharethis.loopy.test;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import com.sharethis.loopy.sdk.*;
import com.sharethis.loopy.sdk.util.AppDataCache;
import com.sharethis.loopy.test.util.Holder;
import org.mockito.Mockito;

/**
 * @author Jason Polites
 */
@SuppressWarnings("ThrowableInstanceNeverThrown")
public class ShareClickListenerTest extends LoopyActivityTestCase {

    public void testOnClick() {

        final Context context = Mockito.spy(getContext());

        final Dialog dlg = Mockito.spy(new Dialog(context));

        final AppDataCache appDataCache = Mockito.mock(AppDataCache.class);
        final MockShareConfig shareConfig = Mockito.mock(MockShareConfig.class);
        final MockItem shareItem = Mockito.mock(MockItem.class);

        final String shortlink = "foobar_shortlink";
        final String appLabel = "foobar_appLabel";
        final String packageName = "foobar_packageName";
        final String className = "foobar_className";
        final String apiKey = "foobar_apiKey";

        final Holder<String> apiKeyHolder = new Holder<String>();
        final Holder<String> shortlinkHolder = new Holder<String>();
        final Holder<String> channelHolder = new Holder<String>();

        final Throwable error = new Exception("DUMMY - IGNORE");

        final Intent shareIntent = Mockito.mock(Intent.class);

        ResolveInfo app = new ResolveInfo();
        ActivityInfo activityInfo = new ActivityInfo();
        activityInfo.packageName = packageName;
        activityInfo.name = className;

        app.activityInfo = activityInfo;

        final ApiClient apiClient = new ApiClient() {
            @Override
            public void share(String apiKey, String shortlink, String channel, ApiCallback callback) {
                apiKeyHolder.set(apiKey);
                shortlinkHolder.set(shortlink);
                channelHolder.set(channel);

                // Call both methods for the test
                callback.onSuccess(null);
                callback.onError(error);
            }
        };

        Mockito.when(appDataCache.getAppLabel((Context) Mockito.any(), Mockito.eq(app))).thenReturn(appLabel);
        Mockito.when(shareItem.getShortlink()).thenReturn(null);
        Mockito.when(shareItem.getUrl()).thenReturn(shortlink);
        Mockito.when(shareConfig.getApiKey()).thenReturn(apiKey);
        Mockito.doNothing().when(dlg).dismiss();
        Mockito.doNothing().when(context).startActivity(shareIntent);

        MockShareClickListener listener = new MockShareClickListener() {
            @Override
            public ApiClient getApiClient() {
                return apiClient;
            }

            @Override
            public AppDataCache getAppDataCache() {
                return appDataCache;
            }
        };


        listener.onClick(dlg, app, shareConfig, shareItem, shareIntent);

        Mockito.verify(shareIntent, Mockito.times(2)).setClassName(packageName, className);
        Mockito.verify(context, Mockito.times(2)).startActivity(shareIntent);
        Mockito.verify(dlg).dismiss();

        assertNotNull(apiKeyHolder.get());
        assertNotNull(shortlinkHolder.get());
        assertNotNull(channelHolder.get());

        assertEquals(apiKey, apiKeyHolder.get());
        assertEquals(shortlink, shortlinkHolder.get());
        assertEquals(appLabel, channelHolder.get());
    }
}
