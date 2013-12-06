package com.sharethis.loopy.sdk;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import com.sharethis.loopy.sdk.util.AppUtils;
import com.sharethis.loopy.sdk.util.MD5Util;

import java.security.NoSuchAlgorithmException;

/**
 * @author Jason Polites
 */
public class Device {

	private String modelName;
	private String androidVersion;
	private String androidId;
    private String md5Id;

    private String carrier;
    private boolean initialized = false;

    private final WifiState wifiState = new WifiState();

    Device onCreate(Context context) {
		modelName = android.os.Build.MODEL;
		androidVersion = String.valueOf(Build.VERSION.SDK_INT);
		androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);

        try {
            md5Id = MD5Util.hash(androidId);
        } catch (NoSuchAlgorithmException e) {
            Logger.e(e);
            md5Id = androidId;
        }

        carrier = getCarrier(context);
        getWifiState(context);
        initialized = true;
        return this;
	}

	public String getAndroidVersion() {
		return androidVersion;
	}

	public String getAndroidId() {
		return androidId;
	}

	public String getModelName() {
		return modelName;
	}

    public boolean isInitialized() {
        return initialized;
    }

    public String getCarrier() {
        return carrier;
    }

    public WifiState getWifiState() {
        return wifiState;
    }

    public String getMd5Id() {
        return md5Id;
    }

    WifiState getWifiState(Context context) {
        if(AppUtils.getInstance().hasAndPermission(context, "android.permission.ACCESS_NETWORK_STATE")) {
            if(isOnWifi(context)) {
                wifiState.state = WifiState.ON;
            }
            else {
                wifiState.state = WifiState.OFF;
            }
        }
        return wifiState;
    }

    boolean isOnWifi(Context context) {
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return mWifi.isConnected();
    }

    String getCarrier(Context context) {
        TelephonyManager manager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        return manager.getNetworkOperatorName();
    }

    public static class WifiState {
        public static final String ON = "on";
        public static final String OFF = "off";
        public static final String UNKNOWN = "unknown";
        public String state = UNKNOWN;
    }
}
