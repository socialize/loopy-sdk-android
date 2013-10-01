package com.sharethis.loopy.sdk;

import android.util.Log;

/**
 * @author Jason Polites
 */
public class Logger {

    static boolean debugEnabled = true;
	static final String TAG = "Loopy";

	public static void e(Throwable e) {
		Log.e(TAG, e.getMessage(), e);
	}

    public static void w(Throwable e) {
        Log.w(TAG, e.getMessage(), e);
    }

	public static void w(String s) {
		Log.w(TAG, s);
	}

    public static void e(String s) {
        Log.e(TAG, s);
    }

    public static boolean isDebugEnabled() {
        return debugEnabled;
    }

    public static void d(String s) {
        Log.d(TAG, s);
    }

    public static void setDebugEnabled(boolean debugEnabled) {
        Logger.debugEnabled = debugEnabled;
    }
}
