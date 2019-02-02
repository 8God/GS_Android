package com.gaoshou.common.utils;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import android.util.Log;

public class MD5UUtil {

    private static final String HASH_ALGORITHM = "MD5";
    private static final int RADIX = 10 + 26; // 10 digits + 26 letters

    public static String generate(String imageUri) {

        String suffix = "";
        if (null != imageUri) {
            int index = imageUri.lastIndexOf(".");
            if (-1 != index) {
                suffix = imageUri.substring(index);
            }
        }

        byte[] md5 = getMD5(imageUri.getBytes());
        BigInteger bi = new BigInteger(md5).abs();
        return "_" + bi.toString(RADIX) + suffix;
    }

    public static String generateSign(byte[] data) {

        String sign = null;
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] digestData = md5.digest(data);

            sign = byteArrayToHex(digestData);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return sign;
    }

    private static byte[] getMD5(byte[] data) {
        byte[] hash = null;
        try {
            MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);
            digest.update(data);
            hash = digest.digest();
        } catch (NoSuchAlgorithmException e) {

        }
        return hash;
    }

    public static String byteArrayToHex(byte[] byteArray) {

        // 首先初始化一个字符数组，用来存放每个16进制字符

        char[] hexDigits = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

        // new一个字符数组，这个就是用来组成结果字符串的（解释一下：一个byte是八位二进制，也就是2位十六进制字符（2的8次方等于16的2次方））

        char[] resultCharArray = new char[byteArray.length * 2];

        // 遍历字节数组，通过位运算（位运算效率高），转换成字符放到字符数组中去

        int index = 0;

        for (byte b : byteArray) {

            resultCharArray[index++] = hexDigits[b >>> 4 & 0xf];

            resultCharArray[index++] = hexDigits[b & 0xf];

        }

        // 字符数组组合成字符串返回

        return new String(resultCharArray);

    }

}
