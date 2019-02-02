package com.gaoshou.common.component;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.util.Log;

import com.nostra13.universalimageloader.cache.disc.naming.FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;

public class ImageLoaderFileNameGenerator implements FileNameGenerator {

    private static final String HASH_ALGORITHM = "MD5";
    private static final int RADIX = 10 + 26; // 10 digits + 26 letters

    @Override
    public String generate(String imageUri) {

        String suffix = "";
        if (null != imageUri) {

            String[] uriParts = imageUri.split("\\?e=");
            if (null != uriParts && uriParts.length > 1) {
                imageUri = uriParts[0];
            }

            int index = imageUri.lastIndexOf(".");
            if (-1 != index) {
                suffix = imageUri.substring(index);
            }
        }

        byte[] md5 = getMD5(imageUri.getBytes());
        BigInteger bi = new BigInteger(md5).abs();
        return "_" + bi.toString(RADIX) + suffix;
    }

    private byte[] getMD5(byte[] data) {
        byte[] hash = null;
        try {
            MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);
            digest.update(data);
            hash = digest.digest();
        } catch (NoSuchAlgorithmException e) {
            Log.e(ImageLoader.TAG, e.getMessage(), e);
        }
        return hash;
    }

}
