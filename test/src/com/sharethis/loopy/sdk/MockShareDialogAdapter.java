package com.sharethis.loopy.sdk;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.TextView;
import com.sharethis.loopy.sdk.util.AppDataCache;

import java.util.List;

/**
 * @author Jason Polites
 */
public class MockShareDialogAdapter extends ShareDialogAdapter {
    public MockShareDialogAdapter(Context context, List<ShareDialogRow> items) {
        super(context, items);
    }

    @Override
    public void setOnShareClickListener(ShareClickListener onShareClickListener) {
        super.setOnShareClickListener(onShareClickListener);
    }

    @Override
    public void setDialog(Dialog dialog) {
        super.setDialog(dialog);
    }

    @Override
    public void setShareItem(Item shareItem) {
        super.setShareItem(shareItem);
    }

    @Override
    public void setShareIntent(Intent shareIntent) {
        super.setShareIntent(shareIntent);
    }

    @Override
    public void setConfig(ShareConfig config) {
        super.setConfig(config);
    }

    @Override
    public void setShareDialogListener(ShareDialogListener shareDialogListener) {
        super.setShareDialogListener(shareDialogListener);
    }

    @Override
    public void setText(Context context, TextView textView, ResolveInfo item) {
        super.setText(context, textView, item);
    }

    @Override
    public void setImage(Context context, ImageView imageView, ResolveInfo item) {
        super.setImage(context, imageView, item);
    }

    @Override
    public LayoutInflater getInflater(Context context) {
        return super.getInflater(context);
    }

    @Override
    public AppDataCache getAppDataCache() {
        return super.getAppDataCache();
    }
}
