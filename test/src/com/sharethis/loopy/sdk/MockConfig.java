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

package com.sharethis.loopy.sdk;

import com.sharethis.loopy.sdk.util.ResourceLocator;

import java.util.Properties;

/**
 * @author Jason Polites
 */
public class MockConfig extends Config {

    @Override
    public Properties newProperties() {
        return super.newProperties();
    }

    @Override
    public String clean(String url) {
        return super.clean(url);
    }

    @Override
    public ResourceLocator newResourceLocator() {
        return super.newResourceLocator();
    }

    @Override
    public boolean getBooleanProperty(Properties props, String key, boolean defaultValue) {
        return super.getBooleanProperty(props, key, defaultValue);
    }

    @Override
    public int getIntProperty(Properties props, String key, int defaultValue) {
        return super.getIntProperty(props, key, defaultValue);
    }

    @Override
    public String getUrlProperty(Properties props, String key) {
        return super.getUrlProperty(props, key);
    }
}
