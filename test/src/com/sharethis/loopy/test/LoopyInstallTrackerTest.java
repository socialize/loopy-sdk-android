package com.sharethis.loopy.test;

import android.content.Context;
import android.content.Intent;
import com.sharethis.loopy.sdk.ApiCallback;
import com.sharethis.loopy.sdk.ApiClient;
import com.sharethis.loopy.sdk.Loopy;
import com.sharethis.loopy.sdk.LoopyAccess;
import com.sharethis.loopy.sdk.LoopyState;
import com.sharethis.loopy.sdk.MockLoopy;
import com.sharethis.loopy.sdk.Session;
import com.sharethis.loopy.test.util.Holder;
import org.mockito.Mockito;

/**
 * @author Jason Polites
 */
public class LoopyInstallTrackerTest extends LoopyAndroidTestCase {

    public void testTrackInstallSuccess() {

        String referrerString = "foobar";

        Intent intent = new Intent();
        intent.putExtra("referrer", referrerString);

        final Holder<String> referrer = new Holder<String>();

        ApiClient apiClient = new ApiClient() {
            @Override
            public void referrer(String apiKey, String apiSecret, String referrerString, ApiCallback callback) {
                // Do nothing, just make sure it's called
                referrer.set(referrerString);
            }
        };

        MockLoopy loopy = new MockLoopy(apiClient);

        loopy.trackInstall(getContext(), intent);

        assertEquals(referrerString, referrer.get());
    }

    public void testTrackInstallFail() {

        String referrerString = "foobar";

        Intent intent = new Intent();
        intent.putExtra("referrer", referrerString);


        final Session session = Mockito.mock(Session.class);
        final LoopyState state = Mockito.mock(LoopyState.class);

        Mockito.when(session.isStarted()).thenReturn(true);
        Mockito.when(session.getState()).thenReturn(state);
        Mockito.when(session.waitForStart()).thenReturn(session);

        ApiClient apiClient = new ApiClient() {
            @Override
            public void referrer(String apiKey, String apiSecret, String referrerString, ApiCallback callback) {
                // Simulate fail
                callback.onError(new Exception("Dummy Exception - Ignore Me"));
            }
        };

        MockLoopy loopy = new MockLoopy(apiClient) {
            @Override
            public Session getSession() {
                return session;
            }
        };

        loopy.trackInstall(getLocalContext(), intent);

        Mockito.verify(state).setReferrer(referrerString);
        Mockito.verify(state).save(Mockito.eq(getLocalContext()), (ApiCallback) Mockito.any());
    }

    /**
     * tests that the internal broadcast triggered on async install is correctly handled.
     */
    public void testInstallTrackerBroadcast() throws InterruptedException {

        String referrerString = "foobar";

        final Intent intent = new Intent();
        intent.putExtra("referrer", referrerString);

        final Holder<Intent> installIntentHolder = new Holder<Intent>();

        ApiClient apiClient = Mockito.mock(ApiClient.class);

        MockLoopy loopy = new MockLoopy(apiClient) {
            @Override
            public void trackInstall(Context context, Intent intent) {
                installIntentHolder.set(intent);
            }
        };

        LoopyAccess.setLoopy(loopy);

        final Context context = getLocalContext();

        // Setup the broadcast system
        Loopy.onStart(context);

        // Simualate a separate process to trigger the install
        new Thread() {
            @Override
            public void run() {
                Loopy.onInstall(context, intent);
            }
        }.start();

        // We need to wait for the process to be returned
        Thread.sleep(1000);

        Loopy.onStop(context);

        // now check the result
        Intent result = installIntentHolder.get();
        assertNotNull(result);
        assertEquals(referrerString, result.getStringExtra("referrer"));
    }
}
