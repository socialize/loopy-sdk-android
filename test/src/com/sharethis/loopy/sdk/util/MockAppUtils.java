package com.sharethis.loopy.sdk.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;

import java.util.List;

/**
 * @author Jason Polites
 */
public class MockAppUtils extends AppUtils {
    public MockAppUtils() {
        super();
    }

    @Override
    public List<ResolveInfo> listAppsForType(Context context, String type) {
        return super.listAppsForType(context, type);
    }

    @Override
    public Intent getSendIntent() {
        return super.getSendIntent();
    }
}
