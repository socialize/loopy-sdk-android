package com.sharethis.loopy.sdk;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

/**
 * Persistent state for Loopy.  Stored in a Preferences file.
 * @author Jason Polites
 */
public class LoopyState {

    static final String PREFS_NAME = "STLoopyPrefs";

    private String stdid;
    private String referrer;
    private boolean loaded;
    private long lastOpenTime = 0;

    public LoopyState load(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        setStdid(preferences.getString("stid", null));
        setReferrer(preferences.getString("referrer", null));
        loaded = true;
        return this;
    }

    public void save(Context context) throws Exception {
        if(!loaded) {
            load(context);
        }
        SharedPreferences preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("stid", getSTDID());
        editor.putString("referrer", getReferrer());
        editor.commit();
    }

    /**
     * Saves asynchronously
     * @param context The current context;
     */
    public void save(final Context context, final ApiCallback callback) {
        new AsyncTask<Void, Void, Void>() {
            Exception error;
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    save(context);
                    if(callback != null) {
                        callback.onProcess(null);
                    }
                }
                catch (Exception e) {
                    Logger.e(e);
                    error = e;
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                if(callback != null) {
                    if(error != null) {
                        callback.onError(error);
                    }
                    else {
                        callback.onSuccess(null);
                    }
                }
            }
        }.execute();
    }

    /**
     * Returns the "ShareThis Device ID" for this device.
     * This is only available AFTER a call to register has been made.
     * @return The ShareThis Device ID for this device.
     */
    public String getSTDID() {
        return stdid;
    }

    public boolean hasSTDID() {
        return stdid != null;
    }

    public void setStdid(String stdid) {
        this.stdid = stdid;
    }

    public String getReferrer() {
        return referrer;
    }

    public void setReferrer(String referrer) {
        this.referrer = referrer;
    }

    public long getLastOpenTime() {
        return lastOpenTime;
    }

    public void setLastOpenTime(long lastOpenTime) {
        this.lastOpenTime = lastOpenTime;
    }

    protected void clear(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();
    }
}
