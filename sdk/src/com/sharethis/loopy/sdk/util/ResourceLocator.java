package com.sharethis.loopy.sdk.util;

import android.content.Context;
import com.sharethis.loopy.sdk.Logger;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author Jason Polites
 */
public class ResourceLocator {
    private ClassLoaderProvider classLoaderProvider = new ClassLoaderProvider();

    public InputStream locateInAssets(Context context, String name) throws IOException {
        InputStream in = null;

        try {
            if(Logger.isDebugEnabled()) {
                Logger.d("Looking for " +
                        name +
                        " in asset path...");
            }

            in = context.getAssets().open(name);

            if(Logger.isDebugEnabled()) {
                Logger.d("Found " +
                        name +
                        " in asset path");
            }
        }
        catch (IOException ignore) {
            // Ignore this, just means no override.
            if(Logger.isDebugEnabled()) {
                Logger.d("No file found in assets with name [" +
                        name +
                        "].");
            }
        }

        return in;
    }

    public InputStream locateInClassPath(String name) throws IOException {

        InputStream in = null;

        if(classLoaderProvider != null) {

            if(Logger.isDebugEnabled()) {
                Logger.d("Looking for " +
                        name +
                        " in classpath...");
            }

            in = classLoaderProvider.getClassLoader().getResourceAsStream(name);

            if(in != null) {
                if(Logger.isDebugEnabled()) {
                    Logger.d("Found " +
                            name +
                            " in classpath");
                }
            }
        }

        return in;
    }

    public InputStream locate(Context context, String name) throws IOException {

        InputStream in = locateInAssets(context, name);

        if(in == null) {
            in = locateInClassPath(name);
        }

        if(in == null) {
            Logger.w("Could not locate [" +
                    name +
                    "] in any location");
        }

        return in;
    }

    public ClassLoaderProvider getClassLoaderProvider() {
        return classLoaderProvider;
    }

    public void setClassLoaderProvider(ClassLoaderProvider classLoaderProvider) {
        this.classLoaderProvider = classLoaderProvider;
    }
}
