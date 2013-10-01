package com.sharethis.loopy.sdk.util;

import android.content.Context;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Jason Polites
 */
public class AppDataCache {

    private final Map<String, Drawable> iconCache = new HashMap<String, Drawable>();
    private final Map<String, String> labelCache = new HashMap<String, String>();

    static AppDataCache instance = new AppDataCache();

    AppDataCache() {}

    public static AppDataCache getInstance() {
        return instance;
    }

    public void onCreate(final Context context) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                Collection<ResolveInfo> appsForContentType = getAppUtils().getAppsForContentType(context, "*/*");
                for (ResolveInfo resolveInfo : appsForContentType) {
                    getAppIcon(context, resolveInfo);
                    getAppLabel(context, resolveInfo);
                }
                return null;
            }
        }.execute();
    }

    public void onDestroy(Context context) {
        iconCache.clear();
        labelCache.clear();
    }

    public synchronized Drawable getAppIcon(Context context, ResolveInfo item) {
        String key = getKey(item);
        Drawable icon = iconCache.get(key);
        if(icon == null) {
            icon = item.activityInfo.loadIcon(context.getPackageManager());
            iconCache.put(key, icon);
        }
        return icon;
    }

    /**
     * May return null!
     * @param packageName The package name.
     * @param className The activity class name
     * @return The app (activity) label, or null
     */
    public String getAppLabel(String packageName, String className) {
        return labelCache.get(packageName + "." + className);
    }

    public synchronized String getAppLabel(Context context, ResolveInfo item) {
        String key = getKey(item);
        String label = labelCache.get(key);
        if(label == null) {
            label = item.activityInfo.loadLabel(context.getPackageManager()).toString();
            labelCache.put(key, label);
        }
        return label;
    }

    String getKey(ResolveInfo item) {
        return item.activityInfo.packageName + "." + item.activityInfo.name;
    }

    // mockable
    AppUtils getAppUtils() {
        return AppUtils.getInstance();
    }

}
