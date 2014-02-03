package com.sharethis.loopy.doc;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.sharethis.loopy.sdk.Loopy;

//begin-snippet-0
public class MyBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        // Just call loopy to track the install
        Loopy.onInstall(context, intent);

        // Your existing code goes here

    }
}
//end-snippet-0