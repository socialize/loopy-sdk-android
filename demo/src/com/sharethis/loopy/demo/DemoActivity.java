package com.sharethis.loopy.demo;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.sharethis.loopy.sdk.Item;
import com.sharethis.loopy.sdk.Loopy;
import com.sharethis.loopy.sdk.ShareDialogListener;

/**
 * Demonstration activity for creating trackable share dialogs and menus.
 */
public class DemoActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // Call onCreate and pass in your API key and secret
        Loopy.onCreate(this, "b8fef1da-88d6-4cbf-8af4-507215d671cd", "sharethis_loopy_demo");

        // The following code is just for this demo app
        ArrayAdapter<String> optionsAdapter = new ArrayAdapter<String>(this, R.layout.row, new String[]{
                "Share Dialog",
                "Share Options Menu"
        });
        ListView list = (ListView) findViewById(R.id.main);
        list.setAdapter(optionsAdapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:

                        // Show a share dialog for a single URL
                        // More detailed data can be provided by sharing an "Item"
                        showShareDialog("http://www.sharethis.com");
                        break;
                    case 1:
                        startActivity(new Intent(DemoActivity.this, ShareActionProviderActivity.class));
                        break;
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        Loopy.onDestroy(this);
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Loopy.onStart(this);
    }

    @Override
    protected void onStop() {
        Loopy.onStop(this);
        super.onStop();
    }

    void showShareDialog(final String urlToShare) {

        // Create an intent.  The URL to be shared will be available in the callback below
        final Intent dialogIntent = new Intent(Intent.ACTION_SEND);
        dialogIntent.setType("*/*");

        // Render the share dialog
        // The listener is used to set the trackable URL on to the share intent.
        Loopy.showShareDialog(this, "Share to...", urlToShare, dialogIntent, new ShareDialogListener() {

            @Override
            public void onLinkGenerated(Item item, Intent shareIntent, Throwable error) {
                if (error == null) {
                    // We got a tracking shortlink ok, set it in the body of the share (or wherever you like)
                    shareIntent.putExtra(Intent.EXTRA_TEXT, item.getShortlink());
                } else {
                    // We couldn't get the shortlink, just use the original URL
                    shareIntent.putExtra(Intent.EXTRA_TEXT, urlToShare);
                }
            }

            // Optional
            @Override
            public void onShow(DialogInterface dialog) {
                // Called when the dialog is shown.
                Log.i("Loopy", "Share dialog displayed");
            }

            // Optional
            @Override
            public boolean onClick(DialogInterface dialog, ResolveInfo app, Intent shareIntent) {
                // Called when the user makes a selection
                Log.i("Loopy", "Share dialog clicked for " + (app.activityInfo != null ? app.activityInfo.name : null));

                // Return false bey default, or true to handle the click event yourself.
                return false;
            }

            // Optional
            @Override
            public void onCancel(DialogInterface dialog) {
                // Called when the user cancels the share dialog
                Log.i("Loopy", "Share dialog cancelled");
            }
        });
    }
}
