package com.sharethis.loopy.sdk;

/**
 * @author Jason Polites
 */
public class MockShareConfig extends ShareConfig {

    @Override
    public void setApiKey(String apiKey) {
        super.setApiKey(apiKey);
    }

    @Override
    public String getApiKey() {
        return super.getApiKey();
    }

    @Override
    public String getApiSecret() {
        return super.getApiSecret();
    }

    @Override
    public void setApiSecret(String apiSecret) {
        super.setApiSecret(apiSecret);
    }
}
