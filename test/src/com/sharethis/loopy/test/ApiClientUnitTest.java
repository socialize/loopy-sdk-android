package com.sharethis.loopy.test;

import com.sharethis.loopy.sdk.ApiCallback;
import com.sharethis.loopy.sdk.ApiClient;
import com.sharethis.loopy.sdk.App;
import com.sharethis.loopy.sdk.Device;
import com.sharethis.loopy.sdk.Event;
import com.sharethis.loopy.sdk.Geo;
import com.sharethis.loopy.sdk.Item;
import com.sharethis.loopy.sdk.Loopy;
import com.sharethis.loopy.sdk.LoopyException;
import com.sharethis.loopy.sdk.LoopyState;
import com.sharethis.loopy.sdk.MockApiClient;
import com.sharethis.loopy.test.util.Holder;
import com.sharethis.loopy.test.util.JsonAssert;
import com.sharethis.loopy.test.util.TestUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author Jason Polites
 */
public class ApiClientUnitTest extends LoopyAndroidTestCase {

    Device device;
    App app;
    Geo geo;
    ApiClient client;
    LoopyState state;
    Device.WifiState wifiState;
    String apiKey = "foobar_api_key";
    String apiSecret = "foobar_api_secret";

    final Holder<JSONObject> result = new Holder<JSONObject>();
    final Holder<String> endpointResult = new Holder<String>();

    @Override
    public void setUp() throws Exception {
        super.setUp();

        // Setup default mocks
        device = Mockito.mock(Device.class);
        app = Mockito.mock(App.class);
        geo = Mockito.mock(Geo.class);
        state = Mockito.mock(LoopyState.class);

        wifiState = new Device.WifiState();
        wifiState.state = Device.WifiState.OFF;

        Mockito.when(device.getCarrier()).thenReturn("foobar_carrier");
        Mockito.when(device.getAndroidVersion()).thenReturn("123");
        Mockito.when(device.getAndroidId()).thenReturn("foobar_id");
        Mockito.when(device.getModelName()).thenReturn("foobar_model");
        Mockito.when(device.getWifiState()).thenReturn(wifiState);
        Mockito.when(geo.getLat()).thenReturn(123.456d);
        Mockito.when(geo.getLon()).thenReturn(789.000d);
        Mockito.when(app.getId()).thenReturn("com.socialize.foobar");
        Mockito.when(app.getName()).thenReturn("FooBar");
        Mockito.when(app.getVersion()).thenReturn("123.4");
        Mockito.when(state.hasSTDID()).thenReturn(true);
        Mockito.when(state.getSTDID()).thenReturn("foobar_sharethis_id");

        client = new ApiClient() {

            @Override
            protected void callAsync(Map<String, String> headers, JSONObject payload, String endpoint, boolean secure, ApiCallback cb) {
                result.set(payload);
                endpointResult.set(endpoint);
                // Call the callback
                cb.onSuccess(null);
            }

            @Override
            protected Device getDevice() {
                return device;
            }

            @Override
            protected Geo getGeo() {
                return geo;
            }

            @Override
            protected App getApp() {
                return app;
            }

            @Override
            public LoopyState getState() {
                return state;
            }

            @Override
            protected long getCurrentTimestamp() {
                return 1234567890;
            }
        };

        // Disable cache for these tests.
        client.setUseShortlinkCache(false);
    }

    @Override
    public void tearDown() throws Exception {

        result.set(null);
        endpointResult.set(null);

        super.tearDown();
    }

    public void testInstall() throws Exception {

        final Holder<Throwable> error = new Holder<Throwable>();
        final String referrer = "foobar_referrer";

        final CountDownLatch latch = new CountDownLatch(1);

        client.install(apiKey, apiSecret, referrer, new ApiCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                latch.countDown();
            }

            @Override
            public void onError(Throwable e) {
                error.set(e);
                latch.countDown();
            }
        });

        assertTrue(latch.await(10, TimeUnit.SECONDS));

        assertNull(error.get());

        assertEquals("install", endpointResult.get());

        // Make sure the request is as we expect
        JSONObject actual = result.get();

        // Load the test file
        JSONObject expected = new JSONObject(TestUtils.getAssetFile(getLocalContext(), "install.json"));

        // Set the version on expected
        expected.getJSONObject("client").put("version", String.valueOf(Loopy.VERSION));

        JsonAssert.assertJsonEquals(expected, actual);
    }

    public void testReferrer() throws Exception {

        final Holder<Throwable> error = new Holder<Throwable>();
        final String referrer = "foobar_referrer";

        final CountDownLatch latch = new CountDownLatch(1);

        client.referrer(apiKey, apiSecret, referrer, new ApiCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                latch.countDown();
            }

            @Override
            public void onError(Throwable e) {
                error.set(e);
                latch.countDown();
            }
        });

        assertTrue(latch.await(10, TimeUnit.SECONDS));

        assertNull(error.get());

        assertEquals("referrer", endpointResult.get());

        // Make sure the request is as we expect
        JSONObject actual = result.get();

        // Load the test file
        JSONObject expected = new JSONObject(TestUtils.getAssetFile(getLocalContext(), "referrer.json"));

        // Set the version on expected
        expected.getJSONObject("client").put("version", String.valueOf(Loopy.VERSION));

        JsonAssert.assertJsonEquals(expected, actual);
    }

    public void testOpen() throws Exception {

        final Holder<Throwable> error = new Holder<Throwable>();
        final String referrer = "foobar_referrer";

        final CountDownLatch latch = new CountDownLatch(1);

        client.open(apiKey, apiSecret, referrer, new ApiCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                latch.countDown();
            }

            @Override
            public void onError(Throwable e) {
                error.set(e);
                latch.countDown();
            }
        });

        assertTrue(latch.await(10, TimeUnit.SECONDS));

        assertNull(error.get());

        assertEquals("open", endpointResult.get());

        // Make sure the request is as we expect
        JSONObject actual = result.get();

        // Load the test file
        JSONObject expected = new JSONObject(TestUtils.getAssetFile(getLocalContext(), "open.json"));

        // Set the version on expected
        expected.getJSONObject("client").put("version", String.valueOf(Loopy.VERSION));

        JsonAssert.assertJsonEquals(expected, actual);
    }

    public void testSTDID() throws Exception {

        final Holder<Throwable> error = new Holder<Throwable>();

        final CountDownLatch latch = new CountDownLatch(1);

        client.stdid(apiKey, apiSecret, new ApiCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                latch.countDown();
            }

            @Override
            public void onError(Throwable e) {
                error.set(e);
                latch.countDown();
            }
        });

        assertTrue(latch.await(10, TimeUnit.SECONDS));

        assertNull(error.get());

        assertEquals("stdid", endpointResult.get());

        // Make sure the request is as we expect
        JSONObject actual = result.get();

        // Load the test file
        JSONObject expected = new JSONObject(TestUtils.getAssetFile(getLocalContext(), "stdid.json"));

        // Set the version on expected
        expected.getJSONObject("client").put("version", String.valueOf(Loopy.VERSION));

        JsonAssert.assertJsonEquals(expected, actual);
    }

    public void testShortlink() throws Exception {

        final Holder<Throwable> error = new Holder<Throwable>();

        final CountDownLatch latch = new CountDownLatch(1);

        Set<String> tags = new HashSet<String>();
        tags.addAll(Arrays.asList("sports", "entertainment"));

        Item item = new Item();
        item.setUrl("foobar_url");
        item.setTitle("foobar_title");
        item.setDescription("foobar_description");
        item.setImageUrl("foobar_image");
        item.setType("foobar_type");
        item.setVideoUrl("foobar_video");
        item.setTags(tags);

        client.shortlink(apiKey, apiSecret, item, new ApiCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                latch.countDown();
            }

            @Override
            public void onError(Throwable e) {
                error.set(e);
                latch.countDown();
            }
        });

        assertTrue(latch.await(10, TimeUnit.SECONDS));

        assertNull(error.get());

        assertEquals("shortlink", endpointResult.get());

        // Make sure the request is as we expect
        JSONObject actual = result.get();

        // Load the test file
        JSONObject expected = new JSONObject(TestUtils.getAssetFile(getLocalContext(), "shortlink.json"));

        JsonAssert.assertJsonEquals(expected, actual);
    }

    public void testShare() throws Exception {

        final Holder<Throwable> error = new Holder<Throwable>();

        final CountDownLatch latch = new CountDownLatch(1);

        client.share(apiKey, apiSecret, "foobar_shortlink", "facebook", new ApiCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                latch.countDown();
            }

            @Override
            public void onError(Throwable e) {
                error.set(e);
                latch.countDown();
            }
        });

        assertTrue(latch.await(10, TimeUnit.SECONDS));

        assertNull(error.get());

        assertEquals("share", endpointResult.get());

        // Make sure the request is as we expect
        JSONObject actual = result.get();

        // Load the test file
        JSONObject expected = new JSONObject(TestUtils.getAssetFile(getLocalContext(), "share.json"));

        // Set the version on expected
        expected.getJSONObject("client").put("version", String.valueOf(Loopy.VERSION));

        JsonAssert.assertJsonEquals(expected, actual);
    }

    public void testLog() throws Exception {

        final Holder<Throwable> error = new Holder<Throwable>();

        final CountDownLatch latch = new CountDownLatch(1);

        Event event = new Event();
        event.setType("share");
        event.addMeta("foo", "bar");
        event.addMeta("foo2", "bar2");

        client.log(apiKey, apiSecret, event, new ApiCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                latch.countDown();
            }

            @Override
            public void onError(Throwable e) {
                error.set(e);
                latch.countDown();
            }
        });

        assertTrue(latch.await(10, TimeUnit.SECONDS));

        assertNull(error.get());

        assertEquals("log", endpointResult.get());

        // Make sure the request is as we expect
        JSONObject actual = result.get();

        // Load the test file
        JSONObject expected = new JSONObject(TestUtils.getAssetFile(getLocalContext(), "log.json"));

        // Set the version on expected
        expected.getJSONObject("client").put("version", String.valueOf(Loopy.VERSION));

        JsonAssert.assertJsonEquals(expected, actual);
    }

    public void testStidNoState() throws JSONException {
        new NoStateApiRunner() {
            @Override
            void doCall(ApiClient client, ApiCallback cb) {
                client.stdid(apiKey, apiSecret, cb);
            }
        }.runTest();
    }

    public void testReferrerNoState() throws JSONException {
        new NoStateApiRunner() {
            @Override
            void doCall(ApiClient client, ApiCallback cb) {
                client.referrer(apiKey, apiSecret, "foobar", cb);
            }
        }.runTest();
    }

    public void testOpenNoState() throws JSONException {
        new NoStateApiRunner() {
            @Override
            void doCall(ApiClient client, ApiCallback cb) {
                client.open(apiKey, apiSecret, "foobar", cb);
            }
        }.runTest();
    }

    public void testShareNoState() throws JSONException {
        new NoStateApiRunner() {
            @Override
            void doCall(ApiClient client, ApiCallback cb) {
                client.share(apiKey, apiSecret, null, null, cb);
            }
        }.runTest();
    }

    public void testLogNoState() throws JSONException {
        new NoStateApiRunner() {
            @Override
            void doCall(ApiClient client, ApiCallback cb) {
                client.log(apiKey, apiSecret, null, cb);
            }
        }.runTest();
    }

    public void testStidError() throws JSONException {
        new ErrorApiRunner() {
            @Override
            void doCall(ApiClient client, ApiCallback cb) {
                client.stdid(apiKey, apiSecret, cb);
            }
        }.runTest();
    }

    public void testInstallError() throws JSONException {
        new ErrorApiRunner() {
            @Override
            void doCall(ApiClient client, ApiCallback cb) {
                client.install(apiKey, apiSecret, "foobar", cb);
            }
        }.runTest();
    }

    public void testReferrerError() throws JSONException {
        new ErrorApiRunner() {
            @Override
            void doCall(ApiClient client, ApiCallback cb) {
                client.referrer(apiKey, apiSecret, "foobar", cb);
            }
        }.runTest();
    }

    public void testOpenError() throws JSONException {
        new ErrorApiRunner() {
            @Override
            void doCall(ApiClient client, ApiCallback cb) {
                client.open(apiKey, apiSecret, "foobar", cb);
            }
        }.runTest();
    }

    public void testShareError() throws JSONException {
        new ErrorApiRunner() {
            @Override
            void doCall(ApiClient client, ApiCallback cb) {
                client.share(apiKey, apiSecret, null, null, cb);
            }
        }.runTest();
    }

    public void testLogError() throws JSONException {
        new ErrorApiRunner() {
            @Override
            void doCall(ApiClient client, ApiCallback cb) {
                client.log(apiKey, apiSecret, null, cb);
            }
        }.runTest();
    }

    abstract class ErrorApiRunner {

        public void runTest() throws JSONException {
            final JSONObject json = Mockito.mock(JSONObject.class);
            final LoopyState state = Mockito.mock(LoopyState.class);

            JSONException jsonException = new JSONException("Test exception - ignore");

            Mockito.when(json.put(Mockito.anyString(), Mockito.anyObject())).thenThrow(jsonException);
            Mockito.when(state.hasSTDID()).thenReturn(true);

            final Holder<Throwable> error = new Holder<Throwable>();

            MockApiClient client = new MockApiClient() {
                @Override
                public JSONObject newJSONObject() {
                    return json;
                }

                @Override
                public LoopyState getState() {
                    return state;
                }
            };

            ApiCallback callback = new ApiCallback() {
                @Override
                public void onSuccess(JSONObject result) {}

                @Override
                public void onError(Throwable e) {
                    error.set(e);
                }
            };

            doCall(client, callback);

            assertNotNull(error.get());
            assertSame(jsonException, error.get());
        }

        abstract void doCall(ApiClient client, ApiCallback cb);

    }

    abstract class NoStateApiRunner {

        public void runTest() throws JSONException {
            final JSONObject json = Mockito.mock(JSONObject.class);
            final LoopyState state = Mockito.mock(LoopyState.class);

            Mockito.when(state.hasSTDID()).thenReturn(false);

            final Holder<LoopyException> error = new Holder<LoopyException>();

            MockApiClient client = new MockApiClient() {
                @Override
                public JSONObject newJSONObject() {
                    return json;
                }

                @Override
                public LoopyState getState() {
                    return state;
                }
            };

            ApiCallback callback = new ApiCallback() {
                @Override
                public void onSuccess(JSONObject result) {}

                @Override
                public void onError(Throwable e) {
                    error.set((LoopyException) e);
                }
            };

            doCall(client, callback);

            assertNotNull(error.get());
            assertEquals(LoopyException.PARAMETER_MISSING, error.get().getCode());
        }

        abstract void doCall(ApiClient client, ApiCallback cb);

    }

}
