package android.app;

import android.content.Context;

/**
 * @author Jason Polites
 */
public class MockAlertDialog extends AlertDialog {
    public MockAlertDialog(Context context) {
        super(context, AlertDialog.THEME_TRADITIONAL);
    }

    @Override
    public void show() {
        // Nothing
    }
}
