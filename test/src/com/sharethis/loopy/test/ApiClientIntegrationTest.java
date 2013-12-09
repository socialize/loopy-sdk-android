package com.sharethis.loopy.test;

import android.content.Context;
import com.sharethis.loopy.sdk.ApiCallback;
import com.sharethis.loopy.sdk.ApiClient;
import com.sharethis.loopy.sdk.Event;
import com.sharethis.loopy.sdk.Item;
import com.sharethis.loopy.sdk.Loopy;
import com.sharethis.loopy.sdk.LoopyAccess;
import com.sharethis.loopy.sdk.LoopyException;
import com.sharethis.loopy.test.util.Holder;
import com.sharethis.loopy.test.util.JsonAssert;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * tests against the mock API
 *
 * @author Jason Polites
 */
public class ApiClientIntegrationTest extends LoopyAndroidTestCase {

    ApiClient apiClient;
    String apiKey = "foobar_api_key";
    String apiSecret = "foobar_api_secret";
    String stdid = "foobar_stdid";

    @Override
    public void setUp() throws Exception {
        super.setUp();
        Context context = getLocalContext();
        Loopy.onCreate(context, apiKey, apiSecret);
        Loopy.onStart(context);

        apiClient = LoopyAccess.getApiClient();

        // Create a dummy STID
        apiClient.getState().setStdid("foobar_stdid");
    }

    @Override
    public void tearDown() throws Exception {
        Context context = getLocalContext();
        Loopy.onStop(context);
        Loopy.onDestroy(context);
        super.tearDown();
    }

    public void testInstall() throws Exception {
        new ApiTestRunner() {
            @Override
            void doApiCall(ApiClient client, ApiCallback callback) {
                client.install(apiKey, apiSecret, stdid, "test_referrer", callback);
            }

            @Override
            void assertResult(JSONObject response) {
                assertNotNull(response);
            }
        }.doTest();
    }

    public void testReferrer() throws Exception {
        new ApiTestRunner() {
            @Override
            void doApiCall(ApiClient client, ApiCallback callback) {
                client.referrer(apiKey, apiSecret, "test_referrer", callback);
            }

            @Override
            void assertResult(JSONObject response) {
                assertNotNull(response);
            }
        }.doTest();
    }

    public void testOpen() throws Exception {
        new ApiTestRunner() {
            @Override
            void doApiCall(ApiClient client, ApiCallback callback) {
                client.open(apiKey, apiSecret, "test_referrer", callback);
            }

            @Override
            void assertResult(JSONObject response) {
                assertNotNull(response);
            }
        }.doTest();
    }

    public void testShare() throws Exception {
        new ApiTestRunner() {
            @Override
            void doApiCall(ApiClient client, ApiCallback callback) {
                client.share(apiKey, apiSecret, "foobar", Loopy.Channel.FACEBOOK, callback);
            }

            @Override
            void assertResult(JSONObject response) {
                assertNotNull(response);
            }
        }.doTest();
    }

    public void testShortlink() throws Exception {
        new ApiTestRunner() {
            @Override
            void doApiCall(ApiClient client, ApiCallback callback) {

                Set<String> tags = new HashSet<String>();
                tags.addAll(Arrays.asList("technology", "android"));

                Item item = new Item();
                item.setUrl("url");
                item.setTags(tags);

                client.shortlink(apiKey, apiSecret, item, callback);
            }

            @Override
            void assertResult(JSONObject response) {
                assertNotNull(response);
                JsonAssert.assertHasValueAtLocation(response, "shortlink");
            }
        }.doTest();
    }

    public void testLog() throws Exception {
        new ApiTestRunner() {
            @Override
            void doApiCall(ApiClient client, ApiCallback callback) {

                Event event = new Event();
                event.setType("share");
                event.addMeta("foo", "bar");

                client.log(apiKey, apiSecret, event, callback);
            }

            @Override
            void assertResult(JSONObject response) {
                assertNotNull(response);
            }
        }.doTest();
    }

    public void testClientTimeout() throws Exception {
        final Holder<JSONObject> success = new Holder<JSONObject>();
        final Holder<Throwable> fail = new Holder<Throwable>();

        // We can use any old endpoint
        new ApiTestRunner() {
            @Override
            void doApiCall(ApiClient client, ApiCallback callback) {
                // Test app has 4 second timeout
                client.open(apiKey, apiSecret, null, LoopyAccess.wrapDelay(callback, 5000));
            }

            @Override
            void assertResult(JSONObject response) {
                success.set(response);
            }

            @Override
            void assertError(Throwable e) {
                fail.set(e);
            }
        }.doTestWithError();

        assertNull(success.get());

        Throwable error = fail.get();
        assertNotNull(error);

        assertTrue(error instanceof LoopyException);

        LoopyException loopyException = (LoopyException) error;

        assertEquals(LoopyException.CLIENT_TIMEOUT, loopyException.getCode());
    }

    abstract class ApiTestRunner {

        void doTestWithError() throws Exception {
            doTest(true);
        }

        void doTest() throws Exception {
            doTest(false);
        }

        void doTest(boolean allowError) throws Exception {
            final Holder<JSONObject> success = new Holder<JSONObject>();
            final Holder<Throwable> fail = new Holder<Throwable>();

            final CountDownLatch latch = new CountDownLatch(1);

            doApiCall(apiClient, new ApiCallback() {
                @Override
                public void onSuccess(JSONObject result) {
                    success.set(result);
                    latch.countDown();
                }

                @Override
                public void onError(Throwable e) {
                    fail.set(e);
                    e.printStackTrace();
                    latch.countDown();
                }
            });

            boolean returned = latch.await(10, TimeUnit.SECONDS);

            assertTrue("Timeout waiting for api to return", returned);

            if (fail.get() != null) {
                if (allowError) {
                    assertError(fail.get());
                } else {
                    assertNull(fail.get().getMessage(), fail.get());
                }
            }

            JSONObject response = success.get();

            assertResult(response);
        }

        abstract void doApiCall(ApiClient client, ApiCallback callback);

        void assertError(Throwable e) {}

        abstract void assertResult(JSONObject response);
    }
}
