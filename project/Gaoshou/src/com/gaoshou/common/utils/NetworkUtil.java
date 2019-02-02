package com.gaoshou.common.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkUtil {

    public static final int TYPE_NULL = 0;
    public static final int TYPE_MOBILE = 1;
    public static final int TYPE_WIFI = 2;

    public static boolean isNetworkAvaliable(Context context) {
        boolean isNetworkAvaliable = false;
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            isNetworkAvaliable = true;
        } else {
            isNetworkAvaliable = false;
        }
        return isNetworkAvaliable;
    }

    public static int getNetworkState(Context context) {
        int networkState = 0;
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            switch (networkInfo.getType()) {
                case ConnectivityManager.TYPE_MOBILE:
                    networkState = TYPE_MOBILE;
                    break;
                    
                case ConnectivityManager.TYPE_WIFI:
                    networkState = TYPE_WIFI;
                    break;

                default:
                    networkState = TYPE_NULL;
                    break;
            }
        } else {
            networkState = TYPE_NULL;
        }
        return networkState;
    }
    
}
