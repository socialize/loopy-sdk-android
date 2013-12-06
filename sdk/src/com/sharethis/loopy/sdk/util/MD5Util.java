package com.sharethis.loopy.sdk.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author Jason Polites
 */
public class MD5Util {

    public static String hash(String s) throws NoSuchAlgorithmException {
        MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
        digest.update(s.getBytes());
        byte messageDigest[] = digest.digest();
        StringBuilder hexString = new StringBuilder();
        for (byte aMessageDigest : messageDigest) {
            hexString.append(Integer.toHexString(0xFF & aMessageDigest));
        }
        return hexString.toString();
    }
}
