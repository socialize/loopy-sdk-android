package com.sharethis.loopy.sdk;

import org.json.JSONObject;

/**
 * @author Jason Polites
 */
public class MockApiClient extends ApiClient {

    @Override
    public JSONObject newJSONObject() {
        return super.newJSONObject();
    }

    @Override
    public void installDirect(String apiKey, String apiSecret, String stdid, String referrer) throws Exception {
        super.installDirect(apiKey, apiSecret, stdid, referrer);
    }
}
