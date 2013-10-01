package com.sharethis.loopy.test.util;

import android.content.Context;
import com.sharethis.loopy.sdk.Logger;

import java.io.File;

/**
 * @author Jason Polites
 */
public class SystemUtils {

	static File cacheDir;

	public static File getCacheDir(Context context) {
		if(cacheDir == null) {
			File dataDir = new File("/data/data/" + context.getPackageName());
			cacheDir = new File(dataDir, "cache");
			if (!cacheDir.exists() && !cacheDir.mkdir()) {
				Logger.w("Failed to create cache dir in [" +
                        cacheDir +
                        "]");
                cacheDir = null;
			}
		}
		return cacheDir;
	}

	public static File getCacheDir() {
		return cacheDir;
	}
}
