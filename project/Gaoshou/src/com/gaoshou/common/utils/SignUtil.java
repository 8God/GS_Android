package com.gaoshou.common.utils;

import java.io.ByteArrayInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.util.Log;

public class SignUtil {
    
    public static String sign(String data, String algorithm) {
        String signature = null;
        if (null != data) {
            signature = SignUtil.sign(data.getBytes(), algorithm);
        } // if (null != data)
        
        return signature;
    }
    
    public static String sign(byte[] data, String algorithm) {
        String sign = null;
        if (null != data) {
            if (null == algorithm) {
                algorithm = "SHA1";
            } // if (null == algorithm)

            try {
                MessageDigest messageDigest = MessageDigest.getInstance(algorithm);
                byte[] digestData = messageDigest.digest(data);

                sign = byteArrayToHex(digestData);

            } catch (NoSuchAlgorithmException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }

        return sign;
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

    public static String getFingerprintHexString(Context context, String packageName, String algorithm) {

        StringBuilder fingerprintHexSB = new StringBuilder();

        if (null != context) {
            if (null == packageName) {
                packageName = context.getPackageName();
            } // if (null == packageName)
            
            if (null == algorithm) {
                algorithm = "SHA1";
            } // if (null == algorithm)

            try {
                PackageInfo packageInfo = context.getPackageManager().getPackageInfo(packageName, PackageManager.GET_SIGNATURES);
                Signature[] signatures = packageInfo.signatures;
                CertificateFactory certFactory = CertificateFactory.getInstance("X.509");

                //获取证书
                X509Certificate cert = (X509Certificate) certFactory.generateCertificate(new ByteArrayInputStream(signatures[0].toByteArray()));
                MessageDigest messageDigest = MessageDigest.getInstance(algorithm);
                byte[] publicKey = messageDigest.digest(cert.getEncoded());
                String sign = MD5UUtil.generateSign(publicKey);
                Log.i("JNSTesting", "sign = " + sign);

                final String COLON = ":";
                for (int i = 0; i < publicKey.length; i++) {
                    if (0 != i) {
                        fingerprintHexSB.append(COLON);
                    } // if (0 != i)

                    String hexStr = Integer.toHexString(0xFF & publicKey[i]);
                    if (hexStr.length() < 2) {
                        fingerprintHexSB.append(0);
                    } // if (hexStr.length() < 2)

                    fingerprintHexSB.append(hexStr.toUpperCase());
                } // for (int i = 0; i < publicKey.length; i++)
            } catch (NameNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (CertificateException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        } // if (null != packageName)

        return fingerprintHexSB.toString();
    }

}
