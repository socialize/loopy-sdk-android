package com.sharethis.loopy.test;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import com.sharethis.loopy.sdk.util.AppUtils;
import com.sharethis.loopy.sdk.util.MockAppDataCache;
import com.sharethis.loopy.test.util.Holder;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;

/**
 * @author Jason Polites
 */
public class AppDataCacheTest extends LoopyAndroidTestCase {

    public void testGetAppLabel() {

        // Can't mock the ResolveInfo because activityInfo has members not methods.
        ResolveInfo mockInfo = new ResolveInfo();
        ActivityInfo activityInfo = Mockito.mock(ActivityInfo.class);

        activityInfo.packageName = "foo";
        activityInfo.name = "bar";

        Mockito.when(activityInfo.loadLabel((PackageManager)Mockito.any())).thenReturn("foobar");

        mockInfo.activityInfo = activityInfo;

        MockAppDataCache cache = new MockAppDataCache() ;

        String appLabel = cache.getAppLabel(getContext(), mockInfo);

        assertNotNull(appLabel);
        assertEquals("foobar", appLabel);

        appLabel = cache.getAppLabel(getContext(), mockInfo);

        assertNotNull(appLabel);
        assertEquals("foobar", appLabel);

        appLabel = cache.getAppLabel("foo", "bar");

        assertNotNull(appLabel);
        assertEquals("foobar", appLabel);

        Mockito.verify(activityInfo, Mockito.times(1)).loadLabel((PackageManager) Mockito.any());
    }

    public void testGetAppIcon() {

        // Can't mock the ResolveInfo because activityInfo has members not methods.
        ResolveInfo mockInfo = new ResolveInfo();
        ActivityInfo activityInfo = Mockito.mock(ActivityInfo.class);
        Drawable drawable = Mockito.mock(Drawable.class);

        activityInfo.packageName = "foo";
        activityInfo.name = "bar";

        Mockito.when(activityInfo.loadIcon((PackageManager) Mockito.any())).thenReturn(drawable);

        mockInfo.activityInfo = activityInfo;

        MockAppDataCache cache = new MockAppDataCache() ;

        Drawable cached = cache.getAppIcon(getContext(), mockInfo);

        assertNotNull(cached);
        assertSame(drawable, cached);

        cached = cache.getAppIcon(getContext(), mockInfo);

        assertNotNull(cached);
        assertSame(drawable, cached);

        Mockito.verify(activityInfo, Mockito.times(1)).loadIcon((PackageManager) Mockito.any());
    }

    public void testOnCreate() {

        ResolveInfo info0 = new ResolveInfo();
        ResolveInfo info1 = new ResolveInfo();

        final List<ResolveInfo> infos = Arrays.asList(info0, info1);

        final Holder<ResolveInfo> iconHolder = new Holder<ResolveInfo>();
        final Holder<ResolveInfo> labelHolder = new Holder<ResolveInfo>();

        final Drawable drawable = Mockito.mock(Drawable.class);
        final AppUtils appUtils = Mockito.mock(AppUtils.class);

        Mockito.when(appUtils.getAppsForContentType(getContext(), "*/*")).thenReturn(infos);

        MockAppDataCache cache = new MockAppDataCache() {

            @Override
            public synchronized Drawable getAppIcon(Context context, ResolveInfo item) {
                iconHolder.add(item);
                return drawable;
            }

            @Override
            public synchronized String getAppLabel(Context context, ResolveInfo item) {
                labelHolder.add(item);
                return "foobar";
            }

            @Override
            public AppUtils getAppUtils() {
                return appUtils;
            }
        };

        cache.onCreate(getContext());

        sleep(2000);

        List<ResolveInfo> labels = labelHolder.getObjects();
        List<ResolveInfo> icons = iconHolder.getObjects();

        assertNotNull(labels);
        assertNotNull(icons);

        assertEquals(2, labels.size());
        assertEquals(2, icons.size());

        assertTrue(labels.containsAll(infos));
        assertTrue(icons.containsAll(infos));
    }
}
