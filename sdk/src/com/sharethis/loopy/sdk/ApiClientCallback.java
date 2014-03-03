package com.sharethis.loopy.sdk;

import org.json.JSONObject;

/**
 * Internal callback class used by the ApiClient
 * @author Jason Polites
 */
interface ApiClientCallback {
    void onSuccess(JSONObject result);
}
