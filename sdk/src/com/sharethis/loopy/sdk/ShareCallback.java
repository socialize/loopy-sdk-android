package com.sharethis.loopy.sdk;

import android.content.Context;
import android.content.Intent;
import com.sharethis.loopy.sdk.util.JSONUtils;
import org.json.JSONObject;

/**
 * Callback used when creating a tracking url.
 * @author Jason Polites
 */
public abstract class ShareCallback extends ApiCallback {

    Item item;

    @Override
    public final void onSuccess(JSONObject result) {
        if(item != null) {
            item.setShortlink(JSONUtils.getString(result, "shortlink"));
        }
        onResult(item, null);
    }

    @Override
    public final void onError(Throwable e) {
        onResult(item, e);
    }

    /**
     * Not used in this implementation.
     * @param result The result from the API call.
     */
    @Override
    public final void onProcess(JSONObject result) {}

    /**
     * Called when the asynchronous call to the API has returned.
     * @param item The original item being shared.  If there is no error, a shortlink will be available.
     * @param error The error object if there was a failure.  This is null for successful results.
     */
    public abstract void onResult(Item item, Throwable error);

    // Internal use only
    void setItem(Item item) {
        this.item = item;
    }

    /**
     * Convenience method for showing the share dialog from within the callback.
     * @param context The current context.
     * @param title The title to show on the dialog.
     * @param shareIntent The original share intent.
     */
    public final void showDialog(Context context, String title, Intent shareIntent) {
        Loopy.showDialog(context, title, item, shareIntent, null);
    }

    /**
     * Convenience method for showing the share dialog from within the callback.
     * @param context The current context.
     * @param title The title to show on the dialog.
     * @param shareIntent The original share intent.
     * @param listener A listener to handle/intercept dialog events.
     */
    public final void showDialog(Context context, String title, Intent shareIntent, ShareDialogListener listener) {
        Loopy.showDialog(context, title, item, shareIntent, listener);
    }

    /**
     * Convenience method for reporting a share when using a ShareActionProvider.
     * @param context The current context.
     * @param intent The intent that was used for the share.
     */
    @SuppressWarnings("unused")
    public final void reportShare(Context context, Intent intent) {
        if(item != null) {
            Loopy.reportShareFromIntent(context, item, intent);
        }
    }
}
