package com.sharethis.loopy.sdk;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import com.sharethis.loopy.sdk.util.AppDataCache;
import org.json.JSONObject;

/**
 * @author Jason Polites
 */
class ShareClickListener {

    public void onClick(
            Dialog dlg,
            final ResolveInfo app,
            final ShareConfig config,
            final Item shareItem,
            final Intent shareIntent) {

        if (dlg != null) {

            dlg.dismiss();

            final Context context = dlg.getContext();
            final String appLabel = getAppDataCache().getAppLabel(context, app);

            try {
                String shortlink = shareItem.getShortlink();

                if (shortlink == null) {
                    shortlink = shareItem.getUrl();
                }

                getApiClient().share(config.getApiKey(), config.getApiSecret(), shortlink, appLabel, new ApiCallback() {
                    @Override
                    public void onSuccess(JSONObject result) {
                        // Launch the app with the data from the share
                        launchShareIntent(context, app, shareIntent);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Logger.e(e);
                        // Launch the app with the data from the share, even if there's an error
                        launchShareIntent(context, app, shareIntent);
                    }
                });
            } catch (Exception e) {
                Logger.e(e);

                // Launch the app with the data from the share, even if there's an error
                launchShareIntent(context, app, shareIntent);
            }
        }
    }

    void launchShareIntent(Context context, ResolveInfo app, Intent shareIntent) {
        if (app.activityInfo != null) {
            shareIntent.setClassName(app.activityInfo.packageName, app.activityInfo.name);
            context.startActivity(shareIntent);
        }
    }

    // Mockable
    ApiClient getApiClient() {
        return Loopy.getInstance().getApiClient();
    }

    // Mockable
    AppDataCache getAppDataCache() {
        return AppDataCache.getInstance();
    }
}
