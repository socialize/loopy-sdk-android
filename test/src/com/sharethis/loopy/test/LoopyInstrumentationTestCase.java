package com.sharethis.loopy.test;

import android.app.Activity;
import android.content.Context;
import android.test.ActivityInstrumentationTestCase2;
import com.sharethis.loopy.sdk.Logger;
import com.sharethis.loopy.test.util.SystemUtils;

import java.io.File;

/**
 * @author Jason Polites
 */
public abstract class LoopyInstrumentationTestCase<T extends Activity> extends ActivityInstrumentationTestCase2<T> {
	public LoopyInstrumentationTestCase(Class<T> activityClass) {
		super(activityClass);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();

        Logger.setDebugEnabled(true);

		// Fix for bug https://code.google.com/p/dexmaker/issues/detail?id=2
        File cacheDir = SystemUtils.getCacheDir(getContext());
		assertNotNull(cacheDir);
        System.setProperty("dexmaker.dexcache", cacheDir.toString());
	}

	protected Context getContext() {
		return getInstrumentation().getTargetContext();
	}
}
