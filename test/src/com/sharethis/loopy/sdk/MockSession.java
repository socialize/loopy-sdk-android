package com.sharethis.loopy.sdk;

import android.content.Context;

/**
 * @author Jason Polites
 */
public class MockSession extends Session {
    public MockSession() {}

    @Override
    public LoopyState newLoopyState() {
        return super.newLoopyState();
    }

    @Override
    public Config newConfig() {
        return super.newConfig();
    }

    @Override
    public void start(Context context) {
        super.start(context);
    }
}
