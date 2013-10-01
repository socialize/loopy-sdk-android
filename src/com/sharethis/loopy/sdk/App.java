package com.sharethis.loopy.sdk;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

/**
 * @author Jason Polites
 */
public class App {
    private String id;
    private String name;
    private String version;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    App onCreate(Context context) {
        id = context.getPackageName();
        name = context.getApplicationInfo().name;

        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(id, 0);
            version = pInfo.versionName;
        }
        catch (PackageManager.NameNotFoundException e) {
            Logger.e(e);
        }

        return this;
    }
}
