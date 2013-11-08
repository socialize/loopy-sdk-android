package com.sharethis.loopy.doc;
//begin-snippet-0

import android.app.Activity;
import android.os.Bundle;
import com.sharethis.loopy.sdk.Loopy;

public class MyActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Call onCreate and pass in your API key and secret
        Loopy.onCreate(this, "<YOUR-API-KEY>", "<YOUR-API-SECRET>");    // <== TODO: Set this
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
    protected void onDestroy() {
        Loopy.onDestroy(this);
        super.onDestroy();
    }
}
//end-snippet-0