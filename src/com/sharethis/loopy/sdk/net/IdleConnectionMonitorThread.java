package com.sharethis.loopy.sdk.net;

import org.apache.http.conn.ClientConnectionManager;

import java.util.concurrent.TimeUnit;

/**
 * @author Jason Polites
 */
public class IdleConnectionMonitorThread extends Thread {
    private final ClientConnectionManager connMgr;
    private volatile boolean shutdown;
    public IdleConnectionMonitorThread(ClientConnectionManager connMgr) {
        super("IdleConnectionMonitorThread");
        this.connMgr = connMgr;
    }
    @Override
    public void run() {
        try {
            while (!shutdown) {
                synchronized (this) {
                    long timeout = 30000;
                    wait(timeout);
                    // Close expired connections
                    connMgr.closeExpiredConnections();
                    // Optionally, close connections
                    // that have been idle longer than [timeout] milliseconds
                    connMgr.closeIdleConnections(timeout, TimeUnit.MILLISECONDS);
                }
            }
        }
        catch (InterruptedException ignore) {}
    }

    public void trigger() {
        synchronized (this) {
            notifyAll();
        }
    }

    public void shutdown() {
        shutdown = true;
        synchronized (this) {
            notifyAll();
        }
    }
}
