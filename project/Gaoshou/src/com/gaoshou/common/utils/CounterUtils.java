package com.gaoshou.common.utils;

public class CounterUtils {

    public static String format(int count) {
        String formatCount = "0";

        int baseCount = count / 10000;
        if (baseCount > 0) { //大于1万
            
            if(baseCount / 10000 > 0) {  //大于1亿
                formatCount = baseCount / 10000.0f + "亿";
            } else {
                formatCount = count / 10000.0f + "万";
            }
            
        } else {
            formatCount = count + "";
        }

        return formatCount;
    }
}
