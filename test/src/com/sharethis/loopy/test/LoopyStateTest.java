package com.sharethis.loopy.test;

import android.content.Context;
import com.sharethis.loopy.sdk.ApiCallback;
import com.sharethis.loopy.sdk.MockLoopyState;
import org.json.JSONObject;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author Jason Polites
 */
public class LoopyStateTest extends LoopyAndroidTestCase {

    MockLoopyState state = null;

    @Override
    public void setUp() throws Exception {
        state = new MockLoopyState();
        super.setUp();
    }

    @Override
    public void tearDown() throws Exception {
        state.clear(getLocalContext());
    }

    // Integration test
    public void testSaveAndLoad() throws InterruptedException {

        String referrer = "foobar_referrer";
        String stdid = "foobar_stdid";

        Context context = getLocalContext();

        state.clear(context);
        state.load(context);

        assertNull(state.getReferrer());
        assertNull(state.getSTDID());

        state.setReferrer(referrer);
        state.setStdid(stdid);

        final CountDownLatch latch = new CountDownLatch(1);

        state.save(context, new ApiCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                latch.countDown();
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                latch.countDown();
            }
        });

        assertTrue(latch.await(10, TimeUnit.SECONDS));

        assertNotNull(state.getReferrer());
        assertNotNull(state.getSTDID());

        assertEquals(referrer, state.getReferrer());
        assertEquals(stdid, state.getSTDID());

        state.setReferrer(null);
        state.setStdid(null);

        state.load(context);

        assertEquals(referrer, state.getReferrer());
        assertEquals(stdid, state.getSTDID());
    }

}
