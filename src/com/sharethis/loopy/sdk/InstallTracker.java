package com.sharethis.loopy.sdk;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * @author Jason Polites
 */
public class InstallTracker extends BroadcastReceiver {
	@Override
	public void onReceive(final Context context, Intent intent) {
        Loopy.onInstall(context, intent);
	}
}
