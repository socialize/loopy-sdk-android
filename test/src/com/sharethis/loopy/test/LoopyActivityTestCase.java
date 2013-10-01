package com.sharethis.loopy.test;

import android.content.Context;
import com.sharethis.loopy.sdk.Logger;
import com.sharethis.loopy.sdk._EmptyActivity;
import com.sharethis.loopy.test.util.SystemUtils;

import java.io.File;

/**
 * @author Jason Polites
 */
public abstract class LoopyActivityTestCase extends LoopyInstrumentationTestCase<_EmptyActivity> {

    protected LoopyActivityTestCase() {
        super(_EmptyActivity.class);
    }

    @Override
	public void setUp() throws Exception {
		super.setUp();

        Logger.setDebugEnabled(true);

		// Fix for bug https://code.google.com/p/dexmaker/issues/detail?id=2
        File cacheDir = SystemUtils.getCacheDir(getInstrumentation().getTargetContext());
        assertNotNull(cacheDir);
		System.setProperty("dexmaker.dexcache", cacheDir.toString());
	}

	public Context getContext() {
        return getActivity();
//		return getInstrumentation().getTargetContext().getApplicationContext();
	}

    public Context getLocalContext() {
        return getInstrumentation().getContext();
    }

    protected void sleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException ignore) {}
    }
}
