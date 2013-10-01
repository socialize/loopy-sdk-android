package com.sharethis.loopy.sdk.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import java.util.*;

/**
 * @author Jason Polites
 */
public class AppUtils {

    static final AppUtils instance = new AppUtils();

    AppUtils() {}

    public static AppUtils getInstance() {
        return instance;
    }

    /**
     * Determines inclusive permissions.
     * @param context The current context.
     * @param permissions The permissions sought.
     * @return Returns true iff the app has ALL the given permissions.
     */
    public boolean hasAndPermission(Context context, String...permissions) {
        for (String permission : permissions) {
            int res = context.checkCallingOrSelfPermission(permission);
            if(res != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    /**
     * Determines exclusive permissions.
     * @param context The current context.
     * @param permissions The permissions sought.
     * @return Returns true iff the app has ANY the given permissions.
     */
    public boolean hasOrPermission(Context context, String...permissions) {
        for (String permission : permissions) {
            int res = context.checkCallingOrSelfPermission(permission);
            if(res == PackageManager.PERMISSION_GRANTED) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the app info for the apps which support the ACTION_SEND intent with the given types.
     * @param context The current context.
     * @param contentTypes The content types supported.
     * @return A Collection (set) of ResolveInfo objects corresponding to the apps installed on the device that
     *          support the content type(s).
     */
    public Collection<ResolveInfo> getAppsForContentType(Context context, String...contentTypes) {
        Set<ResolveInfo> apps = new LinkedHashSet<ResolveInfo>();
        for (String type : contentTypes) {
            List<ResolveInfo> appsByType = listAppsForType(context, type);
            if(appsByType != null && appsByType.size() > 0) {
                apps.addAll(appsByType);
            }
        }
        return apps;
    }

    List<ResolveInfo> listAppsForType(Context context, String type) {
        Intent intent = getSendIntent();
        intent.setType(type);

        PackageManager p = context.getPackageManager();

        List<ResolveInfo> appList = p.queryIntentActivities(intent, 0);

        // Sort alphabetically.
        Collections.sort(appList, new android.content.pm.ResolveInfo.DisplayNameComparator(p));

        return appList;
    }

    // mockable
    Intent getSendIntent() {
        return new Intent(Intent.ACTION_SEND);
    }

}
