package com.sharethis.loopy.sdk.net;

import com.sharethis.loopy.sdk.Logger;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import java.security.KeyStore;

/**
 * @author Jason Polites
 */
public class HttpClientFactory {

    public static final int DEFAULT_CONNECTION_TIMEOUT = 1000;
    public static final int DEFAULT_READ_TIMEOUT = 1000;

    private HttpParams params;
    private ClientConnectionManager connectionManager;
    private DefaultHttpClient client; // This should be thread safe
    private IdleConnectionMonitorThread monitor;
    private boolean initialized = false;

    private int connectionTimeout = DEFAULT_CONNECTION_TIMEOUT;
    private int readTimeout = DEFAULT_READ_TIMEOUT;

    public void start(int timeout) {
        setConnectionTimeout(timeout);
        setReadTimeout(timeout);
        init();
    }

    public void stop() {
        destroy();
    }

    void init()  {
        if(!initialized) {

            params = new BasicHttpParams();

            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
            HttpConnectionParams.setConnectionTimeout(params, connectionTimeout);
            HttpConnectionParams.setSoTimeout(params, readTimeout);

            SchemeRegistry registry = new SchemeRegistry();
            registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));

            try {
                KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
                trustStore.load(null, null);
                SSLSocketFactory sf = new NaiveSSLSocketFactory(trustStore);
                sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
                registry.register(new Scheme("https", sf, 443));
            }
            catch (Exception e) {
                Logger.e(e);
            }

            connectionManager = new ThreadSafeClientConnManager(params, registry);

            monitor = new IdleConnectionMonitorThread(connectionManager);
            monitor.setDaemon(true);
            monitor.start();
        }
    }

    void destroy() {
        if(initialized) {
            initialized = false;
            if(monitor != null) {
                monitor.shutdown();
            }
            if(connectionManager != null) {
                connectionManager.shutdown();
            }
        }
    }

    public synchronized HttpClient getClient() {
        if(client == null) {
            client = new DefaultHttpClient(connectionManager, params);
        }
        else {
            monitor.trigger();
        }

        return client;
    }

    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public int getReadTimeout() {
        return readTimeout;
    }

    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
    }
}
