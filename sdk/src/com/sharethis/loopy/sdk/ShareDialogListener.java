package com.sharethis.loopy.sdk;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;

/**
 * Provides callbacks for dialog events.
 * @author Jason Polites
 */
public abstract class ShareDialogListener implements DialogInterface.OnCancelListener, DialogInterface.OnShowListener {

    public abstract void onLinkGenerated(Item item, Intent shareIntent, Throwable error);

    public void onShow(DialogInterface dialog) {}

    /**
     * Called when the user makes a selection in the dialog.
     * @param dialog The dialog.
     * @param app The app the user selected.
     * @param shareIntent The original intent being shared.
     * @return Return true to indicate the click is being handled.  Default returns false.
     */
    public boolean onClick(DialogInterface dialog, ResolveInfo app, Intent shareIntent) {
        return false;
    }

    public void onCancel(DialogInterface dialog) {}
}
