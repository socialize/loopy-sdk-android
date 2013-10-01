package com.sharethis.loopy.sdk.util;

/**
 * @author Jason Polites
 */
public class StringUtils {
    public static boolean isEmpty(String str) {
        return str == null || str.trim().length() == 0;
    }
}
