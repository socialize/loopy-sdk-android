package com.sharethis.loopy.test;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.test.mock.MockContext;
import com.sharethis.loopy.sdk.util.AppUtils;
import com.sharethis.loopy.sdk.util.MockAppUtils;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * @author Jason Polites
 */
public class AppUtilsTest extends LoopyAndroidTestCase {

    public void testHasAndPermission() {

        Context trueContext = Mockito.mock(MockContext.class);
        Context falseContext = Mockito.mock(MockContext.class);

        Mockito.when(trueContext.checkCallingOrSelfPermission(Mockito.anyString())).thenReturn(PackageManager.PERMISSION_GRANTED);
        Mockito.when(falseContext.checkCallingOrSelfPermission(Mockito.anyString())).thenReturn(PackageManager.PERMISSION_DENIED);

        assertTrue(AppUtils.getInstance().hasAndPermission(trueContext, "foo", "bar"));
        assertFalse(AppUtils.getInstance().hasAndPermission(falseContext, "foo", "bar"));
    }

    public void testHasOrPermission() {

        Context trueContext = Mockito.mock(MockContext.class);
        Context falseContext = Mockito.mock(MockContext.class);

        Mockito.when(trueContext.checkCallingOrSelfPermission("foo")).thenReturn(PackageManager.PERMISSION_GRANTED);
        Mockito.when(trueContext.checkCallingOrSelfPermission("bar")).thenReturn(PackageManager.PERMISSION_DENIED);

        Mockito.when(falseContext.checkCallingOrSelfPermission(Mockito.anyString())).thenReturn(PackageManager.PERMISSION_DENIED);

        assertTrue(AppUtils.getInstance().hasOrPermission(trueContext, "foo", "bar"));
        assertFalse(AppUtils.getInstance().hasOrPermission(falseContext, "foo", "bar"));

    }

    public void testListAppsForType() {

        final Intent intent = new Intent();

        PackageManager pm = Mockito.mock(PackageManager.class);
        Context context = Mockito.mock(MockContext.class);

        ResolveInfo foo = Mockito.mock(ResolveInfo.class);
        ResolveInfo bar = Mockito.mock(ResolveInfo.class);

        List<ResolveInfo> infos = Arrays.asList(foo, bar);

        Mockito.when(foo.loadLabel(pm)).thenReturn("foo");
        Mockito.when(bar.loadLabel(pm)).thenReturn("bar");
        Mockito.when(context.getPackageManager()).thenReturn(pm);
        Mockito.when(pm.queryIntentActivities(intent, 0)).thenReturn(infos);

        MockAppUtils appUtils = new MockAppUtils() {
            @Override
            public Intent getSendIntent() {
                return intent;
            }
        };

        List<ResolveInfo> apps = appUtils.listAppsForType(context, "foobar");

        assertNotNull(apps);
        assertEquals(infos.size(), apps.size());

        // Check sorted
        assertSame(bar, apps.get(0));
        assertSame(foo, apps.get(1));

        Mockito.verify(pm).queryIntentActivities(intent, 0);
    }


    public void testGetAppsForContentType() {

        ResolveInfo foo = Mockito.mock(ResolveInfo.class);
        ResolveInfo bar = Mockito.mock(ResolveInfo.class);

        ResolveInfo sna = Mockito.mock(ResolveInfo.class);
        ResolveInfo fu = Mockito.mock(ResolveInfo.class);

        final List<ResolveInfo> infos0 = Arrays.asList(foo, bar);
        final List<ResolveInfo> infos1 = Arrays.asList(sna, fu);

        final String type0 = "foo";
        final String type1 = "bar";

        MockAppUtils appUtils = new MockAppUtils() {
            @Override
            public List<ResolveInfo> listAppsForType(Context context, String type) {
                if(type.equals(type0)) {
                    return infos0;
                }
                else if(type.equals(type1)) {
                    return infos1;
                }
                return null;
            }
        };

        Collection<ResolveInfo> apps = appUtils.getAppsForContentType(getContext(), type0, type1);

        assertNotNull(apps);
        assertEquals(4, apps.size());
        assertTrue(apps.containsAll(infos0));
        assertTrue(apps.containsAll(infos1));
    }


}
