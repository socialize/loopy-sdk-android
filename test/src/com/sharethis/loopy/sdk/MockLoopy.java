package com.sharethis.loopy.sdk;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.view.LayoutInflater;
import com.sharethis.loopy.sdk.util.AppUtils;

import java.util.Collection;
import java.util.List;

/**
 * @author Jason Polites
 */
public class MockLoopy extends Loopy {

    public MockLoopy(ApiClient apiClient) {
        super(apiClient);
    }

    public MockLoopy() {
        super(null);
    }

    @Override
    public List<ShareDialogRow> getAppList(Collection<ResolveInfo> appsForContentType) {
        return super.getAppList(appsForContentType);
    }

    @Override
    public void shortlink(Item item, ApiCallback callback) {
        super.shortlink(item, callback);
    }

    @Override
    public void share(Item item, String channel, ApiCallback callback) {
        super.share(item, channel, callback);
    }

    @Override
    public void shareFromIntent(Context context, Item item, Intent intent) {
        super.shareFromIntent(context, item, intent);
    }

    @Override
    public ApiClient getApiClient() {
        return super.getApiClient();
    }

    @Override
    public void doShareDialog(Context context, String title, Item item, Intent shareIntent, ShareDialogListener dialogListener) {
        super.doShareDialog(context, title, item, shareIntent, dialogListener);
    }

    public static void setInstance(Loopy instance) {
        Loopy.instance = instance;
    }

    public static void _reportShareFromIntent(Context context, Item item, Intent intent) {
        Loopy.reportShareFromIntent(context, item, intent);
    }

    public void setConfig(ShareConfig config) {
        this.config = config;
    }

    public void setShareClickListener(ShareClickListener listener) {
        this.shareClickListener = listener;
    }

    @Override
    public AppUtils getAppUtils() {
        return super.getAppUtils();
    }

    @Override
    public ShareDialogAdapter newShareDialogAdapter(Context context, List<ShareDialogRow> appList) {
        return super.newShareDialogAdapter(context, appList);
    }

    @Override
    public AlertDialog.Builder newAlertDialogBuilder(Context context) {
        // Must have a theme
        return new AlertDialog.Builder(context, AlertDialog.THEME_TRADITIONAL);
    }

    @Override
    public LayoutInflater getInflater(Context context) {
        return super.getInflater(context);
    }

    @Override
    public App getApp() {
        return super.getApp();
    }

    @Override
    public Device getDevice() {
        return super.getDevice();
    }

    @Override
    public Geo getGeo() {
        return super.getGeo();
    }

    @Override
    public Session getSession() {
        return super.getSession();
    }

    @Override
    public void setApiClient(ApiClient apiClient) {
        super.setApiClient(apiClient);
    }

    @Override
    public void create(Context context, String apiKey, String apiSecret) {
        super.create(context, apiKey, apiSecret);
    }

    @Override
    public void start(Context context, StartCallback cb) {
        super.start(context, cb);
    }

    @Override
    public void start(Context context) {
        super.start(context);
    }

    @Override
    public void stop(Context context) {
        super.stop(context);
    }

    @Override
    public void destroy() {
        super.destroy();
    }

    @Override
    public void trackInstall(Context context, Intent intent) {
        super.trackInstall(context, intent);
    }

    @Override
    public boolean waitForStart(long timeout) {
        return super.waitForStart(timeout);
    }

    @Override
    public String generateUUID() {
        return super.generateUUID();
    }
}
