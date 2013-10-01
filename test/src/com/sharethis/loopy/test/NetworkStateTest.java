/*
 *
 *  * Copyright (c) 2013 ShareThis Inc.
 *  *
 *  * Permission is hereby granted, free of charge, to any person obtaining a copy
 *  * of this software and associated documentation files (the "Software"), to deal
 *  * in the Software without restriction, including without limitation the rights
 *  * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  * copies of the Software, and to permit persons to whom the Software is
 *  * furnished to do so, subject to the following conditions:
 *  *
 *  * The above copyright notice and this permission notice shall be included in
 *  * all copies or substantial portions of the Software.
 *  *
 *  * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *  * THE SOFTWARE.
 *
 */

package com.sharethis.loopy.test;

import com.sharethis.loopy.sdk.*;
import com.sharethis.loopy.sdk.net.HttpClientFactory;
import com.sharethis.loopy.test.util.Holder;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.mockito.Mockito;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author Jason Polites
 */
public class NetworkStateTest extends LoopyAndroidTestCase {
    @Override
    public void setUp() throws Exception {
        super.setUp();
        Loopy.onStart(getContext());
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
        Loopy.onStop(getContext());
    }

    // Not a REAL test, but we can't test network because disabling network on device kills adb.
    public void testShortenReturnsWhenNoNetwork() throws InterruptedException, IOException {

        Session.getInstance().waitForStart();

        final String url = "foobar";
        final Holder<Item> result = new Holder<Item>();
        final Holder<Throwable> resultThrowable = new Holder<Throwable>();
        final CountDownLatch latch = new CountDownLatch(1);

        HttpClientFactory httpClientFactory = Mockito.mock(HttpClientFactory.class);
        HttpClient httpClient = Mockito.mock(HttpClient.class);
        final LoopyState state = Mockito.mock(LoopyState.class);

        ApiClient client = new ApiClient() {
            @Override
            public LoopyState getState() {
                return state;
            }
        };

        Mockito.when(state.hasSTDID()).thenReturn(true);
        Mockito.when(state.getSTDID()).thenReturn("foobar_stdid");

        Mockito.when(httpClientFactory.getClient()).thenReturn(httpClient);
        Mockito.when(httpClient.execute((HttpPost) Mockito.any())).thenThrow(new SocketTimeoutException());

        LoopyAccess.setApiClient(client);
        LoopyAccess.setHttpClientFactory(httpClientFactory);

        Loopy.shorten(url, new ShareCallback() {
            @Override
            public void onResult(Item item, Throwable error) {
                result.set(item);
                resultThrowable.set(error);
                latch.countDown();
            }
        });

        assertTrue(latch.await(2, TimeUnit.SECONDS));

        Item after = result.get();
        Throwable error = resultThrowable.get();

        assertNotNull(after);

        // We expect an error because the network is down
        assertNotNull(error);

        assertTrue(error instanceof LoopyException);

        LoopyException le = (LoopyException) error;

        assertEquals(LoopyException.CLIENT_TIMEOUT, le.getCode());

        assertEquals(url, after.getUrl());
        assertNull(after.getShortlink());

        Mockito.verify(httpClient).execute((HttpPost) Mockito.any());
    }
}
