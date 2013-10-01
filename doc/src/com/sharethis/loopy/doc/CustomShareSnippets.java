package com.sharethis.loopy.doc;

import android.app.Activity;
import android.view.View;
import android.widget.Button;
import com.sharethis.loopy.sdk.Item;
import com.sharethis.loopy.sdk.Loopy;
import com.sharethis.loopy.sdk.ShareCallback;

/**
 * @author Jason Polites
 */
public class CustomShareSnippets extends Activity {


public void customShare() {

    final Button shareButton = new Button(this);

//begin-snippet-0
final String originalUrl = "http://www.sharethis.com"; // <== The original URL you are sharing.

// Hide/Disable your UI until we create a shortlink
shareButton.setEnabled(false);

// Generate a trackable shortlink
Loopy.shorten(originalUrl, new ShareCallback() {

    @Override
    public void onResult(final Item item, final Throwable error) {

        // You can now use the "shortlink" version of your original URL

        // Set your onclick event and report the share
        shareButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // The "channel" corresponds to the medium the user chose to share through.
                String channel = Loopy.Channel.FACEBOOK; // TODO: <== Set this

                // Don't forget to report the share!
                Loopy.reportShare(item, channel);

                // This will be the URL that is ultimately shared
                String urlToShare;

                if (error != null) {
                    // Now execute the share as you normally would
                    urlToShare = item.getShortlink();
                } else {
                    // We couldn't get a shorlink, so revert to the original URL
                    urlToShare = originalUrl;
                }

                // YOUR CODE HERE <== TODO: Implement this
            }
        });

        // Finally ensure the UI is enabled/visible
        shareButton.setEnabled(true);
    }
});

//end-snippet-0
}

}
