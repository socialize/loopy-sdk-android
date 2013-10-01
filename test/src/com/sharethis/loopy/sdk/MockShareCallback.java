package com.sharethis.loopy.sdk;

import org.json.JSONObject;

import java.util.Map;

/**
 * @author Jason Polites
 */
public class MockShareCallback extends ShareCallback {
    @Override
    public void setItem(Item item) {
        super.setItem(item);
    }

    @Override
    public void onBeforePost(Map<String, String> headers, JSONObject payload) {
        super.onBeforePost(headers, payload);
    }

    @Override
    public void onResult(Item item, Throwable error) {

    }
}
