package com.sharethis.loopy.sdk;

import android.content.Context;
import com.sharethis.loopy.sdk.net.HttpClientFactory;
import com.sharethis.loopy.sdk.util.ResourceLocator;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author Jason Polites
 */
public class Config {

    public static final int SESSION_LOAD_TIMEOUT = 2000;
    public static final int USER_SESSION_TIMEOUT_SECONDS = 60;
    public static final String DEFAULT_API_URL = "http://loopy.getsocialize.com/v1/";
    public static final String DEFAULT_SECURE_URL = "https://loopy.getsocialize.com/v1/";

    public static final String LOOPY_PROPERTIES = "loopy.properties";

    private String apiUrl = DEFAULT_API_URL;
    private String secureApiUrl = DEFAULT_SECURE_URL;
    private int apiTimeout = HttpClientFactory.DEFAULT_READ_TIMEOUT;
    private boolean debug = false;

    // Time between successive open calls in seconds
    private int sessionTimeoutSeconds = USER_SESSION_TIMEOUT_SECONDS;

    boolean initialized = false;

    public Config load(Context context) {

        if(!initialized) {
            ResourceLocator locator = newResourceLocator();
            InputStream in = null;
            InputStream override = null;
            try {
                in = locator.locateInClassPath(LOOPY_PROPERTIES);

                if(in != null) {
                    Properties props = newProperties();
                    props.load(in);

                    override = locator.locateInAssets(context, LOOPY_PROPERTIES);

                    if(override != null) {

                        if(Logger.isDebugEnabled()) {
                            Logger.d("Found override properties in assets");
                        }

                        Properties op = newProperties();
                        op.load(override);
                        props.putAll(op);
                    }

                    this.apiUrl = getUrlProperty(props, "api.host");
                    this.secureApiUrl = getUrlProperty(props, "secure.api.host");
                    this.apiTimeout = getIntProperty(props, "api.timeout", 1000);
                    this.debug = getBooleanProperty(props, "debug", false);
                    this.sessionTimeoutSeconds = getIntProperty(props, "session.timeout", 60);
                }
                else {
                    throw new IOException("Failed to locate default " + LOOPY_PROPERTIES);
                }
            }
            catch (IOException e) {
                Logger.e(e);
            }
            finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException ignore) {}
                }

                if (override != null) {
                    try {
                        override.close();
                    } catch (IOException ignore) {}
                }
            }

            initialized = true;
        }

        return this;
    }

    String getUrlProperty(Properties props, String key) {
        String property = props.getProperty(key);

        if(property != null) {
            return clean(property);
        }
        return null;
    }

    int getIntProperty(Properties props, String key, int defaultValue) {
        String val = props.getProperty(key);
        if(val != null) {
            return Integer.parseInt(val);
        }
        return defaultValue;
    }

    boolean getBooleanProperty(Properties props, String key, boolean defaultValue) {
        String val = props.getProperty(key);
        if(val != null) {
            return Boolean.parseBoolean(val);
        }
        return defaultValue;
    }

    public String getAPIUrl() {
        return apiUrl;
    }

    public String getSecureAPIUrl() {
        return secureApiUrl;
    }

    public int getApiTimeout() {
        return apiTimeout;
    }

    public boolean isDebug() {
        return debug;
    }

    public int getSessionTimeoutSeconds() {
        return sessionTimeoutSeconds;
    }

    String clean(String url) {
        url = url.trim();
        if(!url.endsWith("/")) {
            return url += "/";
        }
        return url;
    }

    // mockable
    Properties newProperties() {
        return new Properties();
    }

    ResourceLocator newResourceLocator() {
        return new ResourceLocator();
    }
}
