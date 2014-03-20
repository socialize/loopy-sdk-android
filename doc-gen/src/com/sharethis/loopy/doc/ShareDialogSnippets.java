package com.sharethis.loopy.doc;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import com.sharethis.example.R;
import com.sharethis.loopy.sdk.Item;
import com.sharethis.loopy.sdk.Loopy;
import com.sharethis.loopy.sdk.ShareDialogListener;

/**
 * @author Jason Polites
 */
public class ShareDialogSnippets extends Activity {

    void showShareDialogBasic() {

//begin-snippet-0
final Activity context = this;

// This would be the url you are wanting to share...
final String urlToShare = "http://www.sharethis.com";   // <== TODO: Set this

// Set the content type for the intent (*/* means ALL)
final String contentType = "*/*";                       // <== TODO: Set this

// Set the title for the dialog
final String dialogTitle = "Share to...";               // <== TODO: Set this

// Create an intent.  The URL to be shared will be available in the callback below
final Intent shareIntent = new Intent(Intent.ACTION_SEND);

// Set the conent type for the intent (*/* means ALL)
shareIntent.setType(contentType);

// Grab a reference to your share button
Button shareButton = (Button) findViewById(R.id.btnShare);

// Trigger the share dialog on click
shareButton.setOnClickListener(new View.OnClickListener() {

    @Override
    public void onClick(View v) {

        // Call the "showShareDialog" endpoint to render the share dialog
        // The listener is used to set the trackable URL on to the share intent.
        Loopy.showShareDialog(context, "Share to...", urlToShare, shareIntent, new ShareDialogListener() {

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
        });
    }
});
//end-snippet-0
    }


    void showShareDialogItem() {

//begin-snippet-1
final Activity context = this;

// This would be the item you are wanting to share...
final Item item = new Item();

// The title corresponds to the og:title meta tag element
// One of title OR url are REQUIRED
item.setTitle("My article");

// The description corresponds to the og:description meta tag element
item.setDescription("Something to do with my item");

// Set the content type for the intent (*/* means ALL)
final String contentType = "*/*";                       // <== TODO: Set this

// Set the title for the dialog
final String dialogTitle = "Share to...";               // <== TODO: Set this

// Create an intent.  The URL to be shared will be available in the callback below
final Intent shareIntent = new Intent(Intent.ACTION_SEND);

// Set the conent type for the intent (*/* means ALL)
shareIntent.setType(contentType);

// Grab a reference to your share button
Button shareButton = (Button) findViewById(R.id.btnShare);

// Trigger the share dialog on click
shareButton.setOnClickListener(new View.OnClickListener() {

    @Override
    public void onClick(View v) {

        // Call the "showShareDialog" endpoint to render the share dialog
        // The listener is used to set the trackable URL on to the share intent.
        Loopy.showShareDialog(context, "Share to...", item, shareIntent, new ShareDialogListener() {
            @Override
            public void onLinkGenerated(Item item, Intent shareIntent, Throwable error) {
                if (error == null) {
                    // We got a tracking shortlink ok, set it in the body of the share (or wherever you like)
                    shareIntent.putExtra(Intent.EXTRA_TEXT, item.getShortlink());
                }
            }
        });
    }
});
//end-snippet-1
    }


    void shareSnippets() {
//begin-snippet-2
final String urlToShare = "http://www.sharethis.com";
//end-snippet-2

//begin-snippet-3
// This would be the item you are wanting to share...
final Item item = new Item();

// The title corresponds to the og:title meta tag element
// One of title OR url are REQUIRED
item.setTitle("My Article");

// The description corresponds to the og:description meta tag element
item.setDescription("The description of my article");

// Image, video and type are also supported
item.setImageUrl("...");
item.setVideoUrl("...");
item.setType("...");

// You can also add custom tags to the content
item.addTag("Entertainment");
item.addTag("Sports");

//end-snippet-3
    }

}
