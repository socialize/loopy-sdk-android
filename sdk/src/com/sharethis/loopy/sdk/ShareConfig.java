package com.sharethis.loopy.sdk;

/**
 * @author Jason Polites
 */
public class ShareConfig {

    private String apiKey;
    private String apiSecret;

    ShareConfig() {}

    public String getApiKey() {
        return apiKey;
    }

    void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getApiSecret() {
        return apiSecret;
    }

    void setApiSecret(String apiSecret) {
        this.apiSecret = apiSecret;
    }
}
