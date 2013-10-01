package com.sharethis.loopy.sdk;

import com.sharethis.loopy.sdk.util.AppDataCache;

/**
 * @author Jason Polites
 */
public class MockShareClickListener extends ShareClickListener {

    @Override
    public ApiClient getApiClient() {
        return super.getApiClient();
    }

    @Override
    public AppDataCache getAppDataCache() {
        return super.getAppDataCache();
    }
}
