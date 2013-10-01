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
    public JSONObject stdidDirect(String apiKey) throws Exception {
        return super.stdidDirect(apiKey);
    }

    @Override
    public JSONObject installDirect(String apiKey, String referrer) throws Exception {
        return super.installDirect(apiKey, referrer);
    }
}
