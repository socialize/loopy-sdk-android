package com.sharethis.loopy.test.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.test.ActivityTestCase;
import android.view.View;
import com.sharethis.loopy.sdk.util.IOUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static junit.framework.Assert.fail;

/**
 * @author Jason Polites
 */
public class TestUtils {

	public static InputStream getAssetStream(Context context, String name) throws IOException {
		return context.getAssets().open(name);
	}

	public static String getAssetFile(Context context, String name) {
		try {
			InputStream in = context.getAssets().open(name);
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			IOUtils.pipe(in, bout, 2048);
			in.close();
			return new String(bout.toByteArray());
		}
		catch (IOException e) {
			e.printStackTrace();

			fail("Failed to open asset file [" +
					name +
					"]");
		}
		return null;
	}

	public static void assertBitmapsEqual(Bitmap expected, Bitmap actual) {

		int eWidth = expected.getWidth();
		int eHeight = expected.getHeight();

		int aWidth = actual.getWidth();
		int aHeight = actual.getHeight();

		if(eWidth == aWidth && eHeight == aHeight) {

			for (int y = 0; y < eHeight; y++) {
				for (int x = 0; x < eWidth; x++) {

					if(expected.getPixel(x, y) != actual.getPixel(x, y)) {
						fail("Pixel at [" +
								x +
								"," +
								y +
								"] does not match");

                        return;
					}
				}
			}
		}
		else {
			fail("Expected bitmap has dimensions [" +
					eWidth +
					"," +
					eHeight +
					"] whereas actual has dimensions [" +
					aWidth +
					"," +
					aHeight +
					"]");
		}

	}

	public static void runOnUIThread(Context context, Runnable runnable) {
		new Handler(context.getMainLooper()).post(runnable);
	}

    @SuppressWarnings("unchecked")
    public static void setMobileDataEnabled(Context context, boolean enabled) throws Exception {
        final EmulatorControl control = new EmulatorControl();
        control.connect("localhost", 5554);
        if(enabled) {
            control.sendCommand("network delay none");
        }
        else {
            control.sendCommand("network delay 10000:10000");
        }
        control.disconnect();
    }

    public static boolean isMobileDataEnabled(Context context) {
        ConnectivityManager conMgr =  (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return conMgr.getNetworkInfo(0).getState() == NetworkInfo.State.CONNECTED
                || conMgr.getNetworkInfo(1).getState() == NetworkInfo.State.CONNECTING;
    }

    public static void disableNetworkAndWait(Context context, long timeout) throws Exception {
        setMobileDataEnabled(context, false);
        long sleep = timeout / 100;
        long slept = 0;
        while(isMobileDataEnabled(context)) {
            Thread.sleep(sleep);
            slept += sleep;
            if(slept >= timeout) {
                throw new Exception("Timeout waiting for network to disconnect");
            }
        }
    }

    public static void enableNetworkAndWait(Context context, long timeout) throws Exception {
        setMobileDataEnabled(context, true);
        long sleep = timeout / 100;
        long slept = 0;
        while(!isMobileDataEnabled(context)) {
            Thread.sleep(sleep);
            slept += sleep;
            if(slept >= timeout) {
                throw new Exception("Timeout waiting for network to reconnect");
            }
        }
    }

    public static Boolean click(ActivityTestCase test, final View view) throws Throwable {

		final CountDownLatch latch = new CountDownLatch(1);
		final Holder<Boolean> result = new Holder<Boolean>();
		test.runTestOnUiThread(new Runnable() {
			@Override
			public void run() {
				result.set(view.performClick());
				latch.countDown();
			}
		});

		latch.await(10, TimeUnit.SECONDS);

		return result.get();
	}
}
