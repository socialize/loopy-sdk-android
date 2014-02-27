package com.sharethis.loopy.sdk;

import org.json.JSONObject;

import java.util.Map;

/**
 * Generic callback for async tasks.
 * @author Jason Polites
 */
public abstract class ApiCallback {

    /**
     * Called when the API request succeeded.
     * This method executes on the main UI thread.
     * @param result The result from the API call.
     */
	public abstract void onSuccess(JSONObject result);

    /**
     * Called when an unrecoverable error occurred during the API call.
     * This method executes on the main UI thread.
     * @param e The exception thrown within the async task.
     */
	public void onError(Throwable e) {}

    /**
     * Called within the async task.  Implement this if you want additional work in the async task thread.
     * This method is NOT called on the main UI thread.
     * @param result The result from the API call.
     */
    public void onProcess(JSONObject result) {}

    /**
     * For internal use only
     * @param payload The payload to be sent to the server
     */
    public void onBeforePost(Map<String, String> headers, JSONObject payload) {}
}
