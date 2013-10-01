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

import android.content.Context;
import com.sharethis.loopy.sdk.MockConfig;
import com.sharethis.loopy.sdk.util.ResourceLocator;
import org.mockito.Mockito;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author Jason Polites
 */
public class ConfigTest extends LoopyActivityTestCase {

    public void testConfigLoadWithOverride() throws IOException {

        final Context context = getContext();
        final String apiUrl = "foobar_url/";
        final String secureApiUrl =  "foobar_secure_url/";
        final String apiTimeout = "69";
        final String sessionTimeout = "96";
        final String debug = "true";


        final ResourceLocator resourceLocator = Mockito.mock(ResourceLocator.class);
        final Properties props = Mockito.mock(Properties.class);
        final Properties overrideProps = Mockito.mock(Properties.class);
        final InputStream in = Mockito.mock(InputStream.class);
        final InputStream override = Mockito.mock(InputStream.class);


        Mockito.when(resourceLocator.locateInClassPath("loopy.properties")).thenReturn(in);
        Mockito.when(resourceLocator.locateInAssets(context, "loopy.properties")).thenReturn(override);
        Mockito.when(props.getProperty("api.host")).thenReturn(apiUrl);
        Mockito.when(props.getProperty("secure.api.host")).thenReturn(secureApiUrl);
        Mockito.when(props.getProperty("api.timeout")).thenReturn(apiTimeout);
        Mockito.when(props.getProperty("session.timeout")).thenReturn(sessionTimeout);
        Mockito.when(props.getProperty("debug")).thenReturn(debug);

        MockConfig config = Mockito.spy(new MockConfig());
        Mockito.when(config.newProperties()).thenReturn(props).thenReturn(overrideProps);
        Mockito.when(config.newResourceLocator()).thenReturn(resourceLocator);
        Mockito.when(config.load(getContext())).thenCallRealMethod();

        config.load(getContext());

        Mockito.verify(props).load(in);
        Mockito.verify(overrideProps).load(override);
        Mockito.verify(props).putAll(overrideProps);

        assertEquals(69, config.getApiTimeout());
        assertEquals(96, config.getSessionTimeoutSeconds());
        assertEquals(apiUrl, config.getAPIUrl());
        assertEquals(secureApiUrl, config.getSecureAPIUrl());
        assertTrue(config.isDebug());
    }

    public void testConfigLoadWithoutOverride() throws IOException {

        final Context context = getContext();
        final String apiUrl = "foobar_url/";
        final String secureApiUrl =  "foobar_secure_url/";
        final String apiTimeout = "69";
        final String sessionTimeout = "96";
        final String debug = "true";

        final ResourceLocator resourceLocator = Mockito.mock(ResourceLocator.class);
        final Properties props = Mockito.mock(Properties.class);
        final InputStream in = Mockito.mock(InputStream.class);

        Mockito.when(resourceLocator.locateInClassPath("loopy.properties")).thenReturn(in);
        Mockito.when(resourceLocator.locateInAssets(context, "loopy.properties")).thenReturn(null);
        Mockito.when(props.getProperty("api.host")).thenReturn(apiUrl);
        Mockito.when(props.getProperty("secure.api.host")).thenReturn(secureApiUrl);
        Mockito.when(props.getProperty("api.timeout")).thenReturn(apiTimeout);
        Mockito.when(props.getProperty("session.timeout")).thenReturn(sessionTimeout);
        Mockito.when(props.getProperty("debug")).thenReturn(debug);

        MockConfig config = Mockito.spy(new MockConfig());
        Mockito.when(config.newProperties()).thenReturn(props);
        Mockito.when(config.newResourceLocator()).thenReturn(resourceLocator);
        Mockito.when(config.load(getContext())).thenCallRealMethod();

        config.load(getContext());

        Mockito.verify(props, Mockito.times(1)).load(in);

        assertEquals(69, config.getApiTimeout());
        assertEquals(96, config.getSessionTimeoutSeconds());
        assertEquals(apiUrl, config.getAPIUrl());
        assertEquals(secureApiUrl, config.getSecureAPIUrl());
        assertTrue(config.isDebug());
    }
}
