package com.sharethis.loopy.sdk.util;

import android.content.pm.ResolveInfo;

/**
 * @author Jason Polites
 */
public class MockAppDataCache extends AppDataCache{

    public MockAppDataCache() {}

    @Override
    public String getKey(ResolveInfo item) {
        return super.getKey(item);
    }

    @Override
    public AppUtils getAppUtils() {
        return super.getAppUtils();
    }

    public static void setInstance(AppDataCache instance) {
        AppDataCache.instance = instance;
    }
}
