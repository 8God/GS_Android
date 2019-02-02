package com.gaoshou.common.network;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import com.gaoshou.common.constant.APIKey;
import com.gaoshou.common.constant.CommonConstant;

import android.os.Bundle;

public class CommonRequest {

    public CommonRequest() {
        super();
        if (null == requestParamsSortedMap) {
            requestParamsSortedMap = new TreeMap<String, Object>();
//            requestParamsSortedMap.put(APIKey.COMMON_WHERE_STATUS, 1);//默认拿数据的status为1，不同接口有不同数据状态可在调接口的时候另外传
//            requestParamsSortedMap.put(APIKey.COMMON_SORT, "-" + APIKey.COMMON_CREATED_AT);//默认按时间倒序排
        }
    }

    public String getRequestID() {
        return requestID;
    }

    public void setRequestID(String requestID) {
        this.requestID = requestID;
    }

    public String getRequestApiName() {
        return requestApiName;
    }

    public String getRequestMethod() {
        return requestMethod;
    }

    public void setRequestMethod(String requestMethod) {
        this.requestMethod = requestMethod;
    }

    public void setRequestApiName(String requestApiName) {
        this.requestApiName = requestApiName;
    }

    public SortedMap<String, Object> getRequestParamsSortedMap() {
        return requestParamsSortedMap;
    }

    public Bundle getAdditionalArgsBundle() {
        return additionalArgsBundle;
    }

    public Map<String, Object> getAdditionalArgsMap() {
        return additionalArgsMap;
    }

    public void addRequestParam(String paramKey, Object paramValue) {
        if (null != paramKey) {
            if (null == requestParamsSortedMap) {
                requestParamsSortedMap = new TreeMap<String, Object>();
            }
            requestParamsSortedMap.put(paramKey, paramValue);
        } // if (null != paramKey)
    }

    public Object getRequestParamValue(String paramKey) {
        Object paramValue = null;

        if (null != paramKey && null != requestParamsSortedMap) {
            paramValue = requestParamsSortedMap.get(paramKey);
        }

        return paramValue;
    }

    public void addAdditionalArg(String argKey, Object argValue) {
        if (null != argKey) {
            if (null == additionalArgsMap) {
                additionalArgsMap = new HashMap<String, Object>();
            }
            additionalArgsMap.put(argKey, argValue);
        } // if (null != argKey)
    }

    public Object getAdditionalArgValue(String argKey) {
        Object argValue = null;

        if (null != argKey && null != additionalArgsMap) {
            argValue = additionalArgsMap.get(argKey);
        }

        return argValue;
    }

    public String getRequestURLBaseString() {

        if (null == this.requestURLBaseString) {
            this.requestURLBaseString = REQUEST_URL_BASE_STRING;
        }

        return this.requestURLBaseString;
    }

    public void setRequestURLBaseString(String requestURLBaseString) {
        this.requestURLBaseString = requestURLBaseString;
    }

    public String getRequestContentType() {
        return requestContentType;
    }

    public void setRequestContentType(String requestContentType) {
        this.requestContentType = requestContentType;
    }

    public static final String REQUEST_METHOD_POST = "POST";
    public static final String REQUEST_METHOD_GET = "GET";
    public static final String REQUEST_METHOD_DELETE = "DELETE";
    public static final String REQUEST_METHOD_PUT = "PUT";

    private static final String REQUEST_URL_BASE_STRING = CommonConstant.HTTP_HOST + "/gaoshou/api/web/?r=";

    public static final String REQUEST_IS_ONLINE_AVAILABLE = "REQUEST_IS_ONLINE_AVAILABLE";
    
    public static final String REQUEST_CONTENT_TYPE_FORM_DATA = "multipart/form-data";

    private String requestURLBaseString;

    private String requestID;

    private String requestApiName;

    private String requestMethod;
    
    private String requestContentType;

    private SortedMap<String, Object> requestParamsSortedMap;

    private Bundle additionalArgsBundle;

    private Map<String, Object> additionalArgsMap;
    
}
