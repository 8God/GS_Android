package com.gaoshou.common.constant;

import java.text.SimpleDateFormat;

public class CommonConstant {

    //    public static final String HTTP_HOST = "http://121.40.148.142";
    public static final String HTTP_HOST = "http://115.28.85.76";

    /**
     * 首页网站地址
     */
    public static final String HOME_PAGE = "http://www.tpq.com/";

    public static final int MSG_PAGE_SIZE = 20;

    public static final boolean IS_DEBUG_MODE = true;

    public static final SimpleDateFormat serverTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//    public static final SimpleDateFormat serverTimeFormat = new SimpleDateFormat("yyyyMMddHHmmss");

    public static int CURRENT_VERSION_CODE = 0;

    public static final String KEY_TO_BE_CONTINUED = "KEY_TO_BE_CONTINUED";
    public static final String KEY_DATA_LIST = "KEY_DATA_LIST";

    //登录类型
    public static final int LOGON_TYPE_DOCTOR = 0;
    public static final int LOGON_TYPE_EXPERT = 1;

    //咨询类型
    public static final int CONSULTATION_TYPE_CONSULTATION = 0;
    public static final int CONSULTATION_TYPE_OPERATION = 1;

}
