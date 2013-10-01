package com.sharethis.loopy.test;

import android.content.Context;
import android.test.InstrumentationTestCase;
import com.sharethis.loopy.sdk.Logger;
import com.sharethis.loopy.sdk.Loopy;
import com.sharethis.loopy.sdk.LoopyAccess;
import com.sharethis.loopy.test.util.SystemUtils;

import java.io.File;

/**
 * @author Jason Polites
 */
public abstract class LoopyAndroidTestCase extends InstrumentationTestCase {

    Loopy realLoopy;

    @Override
	public void setUp() throws Exception {
		super.setUp();

        Logger.setDebugEnabled(true);

        // Fix for bug https://code.google.com/p/dexmaker/issues/detail?id=2
        File cacheDir = SystemUtils.getCacheDir(getContext());
		assertNotNull(cacheDir);
		System.setProperty("dexmaker.dexcache", cacheDir.toString());

        // Fix for http://stackoverflow.com/questions/13048985/verifyerror-using-mockito-1-9-5-and-dexmaker-mockito-1-0
        Thread.currentThread().setContextClassLoader(getClass().getClassLoader());

        realLoopy = LoopyAccess.getLoopy();
    }

    @Override
    public void tearDown() throws Exception {
        LoopyAccess.setLoopy(realLoopy);
        super.tearDown();
    }

    protected Context getContext() {
        return getInstrumentation().getTargetContext();
    }

    protected Context getLocalContext() {
        return getInstrumentation().getContext();
    }

    protected void sleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException ignore) {}
    }
}
