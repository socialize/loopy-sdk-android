package com.sharethis.loopy.demo;

//begin-snippet-0

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.ShareActionProvider;
import android.view.Menu;
import android.view.MenuItem;
import com.sharethis.loopy.sdk.Item;
import com.sharethis.loopy.sdk.Loopy;
import com.sharethis.loopy.sdk.ShareCallback;

/**
 * Demonstrates using trackable share urls in conjuction
 * with a ShareActionProvider in a standard Android Action Bar
 */
public class ShareActionProviderActivity extends ActionBarActivity {

    private ShareActionProvider mShareActionProvider;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Replace the reference to share_menu_activity
        // with your activity's layout
        setContentView(R.layout.share_menu_activity);       // <== TODO: Set this

        // Call onCreate and pass in your API key and secret
        Loopy.onCreate(this, "e3xjjqq3pcuwsqmzh7swewv6", "dsfgrtwhi73937ehodlsoi0");             // <== TODO: Set this
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

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {

        // This would be the url you are wanting to share...
        final String urlToShare = "http://www.sharethis.com";   // <== TODO: Set this

        // Set the content type for the intent (*/* means ALL)
        final String contentType = "*/*";                       // <== TODO: Set this

        // The next 3 lines are standard menu inflation commands
        getMenuInflater().inflate(R.menu.share_menu, menu);

        final MenuItem shareItem = menu.findItem(R.id.action_share);

        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(shareItem);

        final Context context = mShareActionProvider.getContext();

        // Ensure we hide the menu while we generate the trackable link
        shareItem.setVisible(false);

        // Create an intent to share.  The URL to be shared will be available in the callback below
        final Intent shareIntent = new Intent(Intent.ACTION_SEND);

        // Set the conent type for the intent (*/* means ALL)
        shareIntent.setType(contentType);

        // Create a trackable URL
        Loopy.shorten(urlToShare, new ShareCallback() {

            @Override
            public void onResult(final Item item, Throwable error) {

                if (error == null) {

                    // We got a tracking shortlink ok, set it in the body of the share (or wherever you like)
                    shareIntent.putExtra(Intent.EXTRA_TEXT, item.getShortlink());

                    // Add a listener to record the click
                    mShareActionProvider.setOnShareTargetSelectedListener(

                            new ShareActionProvider.OnShareTargetSelectedListener() {

                                @Override
                                public boolean onShareTargetSelected(ShareActionProvider shareActionProvider, Intent intent) {
                                    // Report the share event
                                    reportShare(context, intent);

                                    // Return value is ignored, so just return false.
                                    return false;
                                }
                            });
                } else {
                    // We couldn't get the shortlink, just use the original URL
                    shareIntent.putExtra(Intent.EXTRA_TEXT, urlToShare);
                }

                // Set the intent on the share action provider
                mShareActionProvider.setShareIntent(shareIntent);

                // Finally show (unhide) the menu item
                shareItem.setVisible(true);
            }
        });

        return super.onCreateOptionsMenu(menu);
    }
}
//end-snippet-0
