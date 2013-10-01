package com.sharethis.loopy.sdk;

import android.content.Context;
import android.os.AsyncTask;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author Jason Polites
 */
public class Session {

    Config config;
    LoopyState state;
    boolean started = false;

    final CountDownLatch startLatch = new CountDownLatch(1);

    static final Session instance = new Session();

    public static Session getInstance() {
        return instance;
    }

    Session() {}

    void start(final Context context) {
        if(!started) {
            config = newConfig();
            state = newLoopyState();
            started = true;
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    try {
                        config.load(context);

                        Logger.setDebugEnabled(config.isDebug());

                        state.load(context);
                    }
                    catch (Exception e) {
                        Logger.e(e);
                    }

                    return null;
                }
                @Override
                protected void onPostExecute(Void aVoid) {
                    startLatch.countDown();
                }
            }.execute();
        }
    }

    // Mockable
    LoopyState newLoopyState() {
        return new LoopyState();
    }

    // Mockable
    Config newConfig() {
        return new Config();
    }

    void stop(Context context) {
        // TODO: Anything to do here?
    }

    public Config getConfig() {
        if(config == null) {
            Logger.w("Session was not started.  Make sure you call onStart in the onStart method of your activity");
        }
        return config;
    }

    public LoopyState getState() {
        if(state == null) {
            Logger.w("Session was not started.  Make sure you call onStart in the onStart method of your activity");
        }
        return state;
    }

    public boolean isStarted() {
        return started;
    }

    public Session waitForStart() {
        if(started) {
            try {
                if(!startLatch.await(Config.SESSION_LOAD_TIMEOUT, TimeUnit.MILLISECONDS)) {
                    Logger.w("Timeout waiting for session start");
                }
            } catch (InterruptedException ignore) {}
        }
        else {
            Logger.w("Session was not started.  Make sure you call onStart in the onStart method of your activity");
        }
        return this;
    }
}
