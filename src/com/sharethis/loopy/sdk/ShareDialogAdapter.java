
package com.sharethis.loopy.sdk;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.sharethis.loopy.sdk.util.AppDataCache;

import java.util.List;

/**
 * @author Jason Polites
 */
public class ShareDialogAdapter extends BaseAdapter {

    final List<ShareDialogRow> items;
    final Context context;
    final LayoutInflater inflater;

    Dialog dialog;
    ShareClickListener onShareClickListener;
    ShareDialogListener shareDialogListener;
    Item shareItem;
    Intent shareIntent;
    ShareConfig config;

    public ShareDialogAdapter(Context context, List<ShareDialogRow> items) {
        this.items = items;
        this.context = context;
        this.inflater = getInflater(context);
    }

    @Override
    public int getCount() {
        return (items == null) ? 0 : items.size();
    }

    @Override
    public Object getItem(int position) {
        return (items == null) ? null : items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.st_share_dialog_row, null);

            holder = new ViewHolder();
            holder.leftLayout = convertView.findViewById(R.id.left_item);
            holder.rightLayout = convertView.findViewById(R.id.right_item);
            holder.leftIcon = (ImageView) convertView.findViewById(R.id.left_item_icon);
            holder.leftText = (TextView) convertView.findViewById(R.id.left_item_text);
            holder.rightIcon = (ImageView) convertView.findViewById(R.id.right_item_icon);
            holder.rightText = (TextView) convertView.findViewById(R.id.right_item_text);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        final ShareDialogRow item = items.get(position);

        if(item.left != null) {
            setText(context, holder.leftText, item.left);
            setImage(context, holder.leftIcon, item.left);
        }

        if(item.right != null) {
            setText(context, holder.rightText, item.right);
            setImage(context, holder.rightIcon, item.right);
        }

        if(onShareClickListener != null) {
            holder.leftLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(shareDialogListener == null || !shareDialogListener.onClick(dialog, item.left, shareIntent)) {
                        onShareClickListener.onClick(dialog, item.left, config, shareItem, shareIntent);
                    }
                }
            });

            holder.rightLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(shareDialogListener == null || !shareDialogListener.onClick(dialog, item.right, shareIntent)) {
                        onShareClickListener.onClick(dialog, item.right, config, shareItem, shareIntent);
                    }
                }
            });
        }

        return convertView;
    }

    void setOnShareClickListener(ShareClickListener onShareClickListener) {
        this.onShareClickListener = onShareClickListener;
    }

    void setDialog(Dialog dialog) {
        this.dialog = dialog;
    }

    void setShareItem(Item shareItem) {
        this.shareItem = shareItem;
    }

    void setShareIntent(Intent shareIntent) {
        this.shareIntent = shareIntent;
    }

    void setConfig(ShareConfig config) {
        this.config = config;
    }

    void setShareDialogListener(ShareDialogListener shareDialogListener) {
        this.shareDialogListener = shareDialogListener;
    }

    void setText(Context context, TextView textView, ResolveInfo item) {
        textView.setText( getAppDataCache().getAppLabel(context, item) );
    }

    void setImage(Context context, ImageView imageView, ResolveInfo item) {
        imageView.setImageDrawable( getAppDataCache().getAppIcon(context, item) );
    }

    // mockable
    LayoutInflater getInflater(Context context) {
        return LayoutInflater.from(context);
    }

    AppDataCache getAppDataCache() {
        return AppDataCache.getInstance();
    }

    public static class ViewHolder {
        public TextView leftText;
        public TextView rightText;
        public ImageView leftIcon;
        public ImageView rightIcon;
        public View leftLayout;
        public View rightLayout;
    }
}
