package com.sharethis.loopy.sdk.util;

/**
 * Abstracts the provision of classloaded instances.
 * Used to decouple classloader dependencies for test cases.
 *
 * @author Jason Polites
 */
public class ClassLoaderProvider {
    public ClassLoader getClassLoader() {
        return ClassLoaderProvider.class.getClassLoader();
    }
}
