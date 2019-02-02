package com.gaoshou.common.network;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.gaoshou.common.base.GsApplication;
import com.gaoshou.common.constant.APIKey;
import com.gaoshou.common.utils.FileUtil;
import com.gaoshou.common.utils.TypeUtil;
import com.gaoshou.common.utils.Validator;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class RestAPIRequester {

    private static final String TAG = "RestAPIRequester";

    private static final int CONNECTION_TIMEOUT_MILLIS = 15000;

    private static final String REQUEST_METHOD_POST = "POST";
    private static final String REQUEST_METHOD_GET = "GET";
    private static final String REQUEST_METHOD_DELETE = "DELETE";
    private static final String REQUEST_METHOD_PUT = "PUT";

    private static final String SLASH_SPLITTING_APPENDER = "/";
    private static final String UNDERSCORE_SPLITTING_APPENDER = "_";
    private static final String QUESTION_SPLITTING_APPENDER = "?";
    private static final String AND_SPLITTING_APPENDER = "&";
    private static final String EQUAL_SPLITTING_APPENDER = "=";
    private static final String NULL_STR = "null";
    private static final String QUESTION_MARK_SPLITTER = "\\?";
    private static final String PARAMS_SPLITTER = "\\?|\\&|=";
    private static final String REQUEST_URL_DUMMAY_BASE_STRING = "http://" + GsApplication.class.getPackage() + "/dummy";
    private static final String CHARSET_UTF8 = "UTF-8";

    private static final boolean IS_DEBUG_PRINTING = true;
    private static final boolean IS_LOCAL_DATABANK_SUPPORTED = true;

    private Context context;
    private String requestMethod;
    private String requestContentType;

    private OnResponseCallback onResponseCallback;

    private List<File> tmpFileList = null;

    public RestAPIRequester(Context context) {
        super();
        this.context = context;
    }

    public String getRequestContentType() {
        return requestContentType;
    }

    public void setRequestContentType(String requestContentType) {
        this.requestContentType = requestContentType;
    }

    public String getRequestType() {
        return requestMethod;
    }

    public void setRequestType(String requestMethod) {
        this.requestMethod = requestMethod;
    }

    public OnResponseCallback getOnResponseCallback() {
        return onResponseCallback;
    }

    public void setOnResponseCallback(OnResponseCallback onResponseCallback) {
        this.onResponseCallback = onResponseCallback;
    }

    public void sendRequest(CommonRequest request) {
        request(request);
    }

    private void request(CommonRequest request) {
        try {
            if (null != context) {
                ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                GsApplication gsApplication = GsApplication.getInstance(context);

                if (networkInfo != null && networkInfo.isConnected()) {
                    gsApplication.resetNetworkSettingsMessageEnabled();
                    sendByNetwork(request);
                } else if (IS_LOCAL_DATABANK_SUPPORTED) {

                    if (null != request) {

                        final String requestApiName = request.getRequestApiName();
                        final SortedMap<String, Object> requestParamsSortedMap = request.getRequestParamsSortedMap();

                        StringBuilder requestURLSB = new StringBuilder();
                        requestURLSB.append(request.getRequestURLBaseString());
                        requestURLSB.append(requestApiName);

                        printRequestInfo(requestURLSB, requestParamsSortedMap);

                        getDataFromLocalBank(request);

                    }
                }
            } else {
                sendByNetwork(request);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendByNetwork(CommonRequest request) throws FileNotFoundException {
        Map<String, Object> result = null;

        requestMethod = request.getRequestMethod();

        requestContentType = request.getRequestContentType();

        String baseUrl = request.getRequestURLBaseString();
        String apiName = request.getRequestApiName();
        Map<String, Object> paramsMap = request.getRequestParamsSortedMap();

        //如果没传请求方法，则根据apiName的规则来判断是什么请求方法
        if (requestMethod == null && !TextUtils.isEmpty(apiName)) {
            if (apiName.contains("create")) {
                requestMethod = REQUEST_METHOD_POST;
            } else if (apiName.contains("update")) {
                requestMethod = REQUEST_METHOD_PUT;
            }
            if (apiName.contains("delete")) {
                requestMethod = REQUEST_METHOD_DELETE;
            }
            if (apiName.contains("index") || apiName.contains("view")) {
                requestMethod = REQUEST_METHOD_GET;
            }
        }
        //如果没传请求方法，且上述情况都不符合，则默认为GET方法
        if (TextUtils.isEmpty(requestMethod)) {
            requestMethod = REQUEST_METHOD_GET;
        }

        //请求的Url
        StringBuilder urlStrBdr = new StringBuilder();
        urlStrBdr.append(baseUrl);
        urlStrBdr.append(apiName);

        RequestParams requestParams = null;
        if (null != paramsMap) {
            Object idObj = paramsMap.get("id");
            if (Validator.isIdValid(idObj)) {
                urlStrBdr.append(AND_SPLITTING_APPENDER);
                urlStrBdr.append("id");
                urlStrBdr.append(EQUAL_SPLITTING_APPENDER);
                urlStrBdr.append(idObj.toString());
            }

            requestParams = getRequestParams(paramsMap);
        }

        Log.i(TAG, "Request Methond: " + requestMethod);
        Log.d(TAG, "URL: " + urlStrBdr.toString());

        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(CONNECTION_TIMEOUT_MILLIS);
        client.setConnectTimeout(CONNECTION_TIMEOUT_MILLIS);
        client.setURLEncodingEnabled(true);
        //        client.addHeader("Content-Type", "application/json");

        MyJsonHttpResponseHandler responseHandler = getJsonHttpResponseHandler(request);

        if (REQUEST_METHOD_POST.equals(requestMethod)) {
            //            if (requestContentType == null) {
            //                client.addHeader("Content-Type", "application/x-www-form-urlencoded");
            //            } else {
            //                client.addHeader("Content-Type", requestContentType);
            //                client.addHeader(client.HEADER_CONTENT_TYPE, requestContentType);
            //            }

            client.post(urlStrBdr.toString(), requestParams, responseHandler);

        } else if (REQUEST_METHOD_PUT.equals(requestMethod)) {
            //            if (requestContentType == null) {
            //                client.addHeader("Content-Type", "application/x-www-form-urlencoded");
            //            } else {
            //                client.addHeader("Content-Type", requestContentType);
            //            }

            client.put(urlStrBdr.toString(), requestParams, responseHandler);

        } else if (REQUEST_METHOD_DELETE.equals(requestMethod)) {

            client.delete(urlStrBdr.toString(), requestParams, responseHandler);

        } else if (REQUEST_METHOD_GET.equals(requestMethod)) {

            client.get(urlStrBdr.toString(), requestParams, responseHandler);

        }

    }

    private MyJsonHttpResponseHandler getJsonHttpResponseHandler(final CommonRequest request) {
        MyJsonHttpResponseHandler responseHandler = new MyJsonHttpResponseHandler(request) {

            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                String requestID = request.getRequestID();
                Log.d(TAG, "RequestID = " + requestID + ", responseString = " + responseString);
                dealResponse(getCommonRequest(), responseString);
            };

            public void onSuccess(int statusCode, Header[] headers, org.json.JSONArray response) {
                String requestID = request.getRequestID();
                Log.d(TAG, "RequestID = " + requestID + ", response = " + response);
            };

            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                String requestID = request.getRequestID();
                Log.d(TAG, "RequestID = " + requestID + ", response = " + response);
                dealResponse(getCommonRequest(), response);
            };

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                String requestID = request.getRequestID();
                Log.e(TAG, "RequestID = " + requestID + ", errorResponse = " + errorResponse);
                dealResponse(getCommonRequest(), errorResponse);
            }

            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                String requestID = request.getRequestID();
                Log.e(TAG, "RequestID = " + requestID + ", responseString = " + responseString);
                dealResponse(getCommonRequest(), responseString);

            };

            public void onFailure(int statusCode, Header[] headers, Throwable throwable, org.json.JSONArray errorResponse) {
                String requestID = request.getRequestID();
                Log.e(TAG, "RequestID = " + requestID + ", errorResponse = " + errorResponse);
            };
        };
        return responseHandler;
    }

    private void dealResponse(CommonRequest request, String responseString) {
        JSONObject responseObject;
        try {
            responseObject = new JSONObject(responseString);
            dealResponse(request, responseObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void dealResponse(CommonRequest request, JSONObject responseObject) {
        try {
            saveDataToLocalBank(request, responseObject.toString());
            Map<String, Object> responseMap = new CommonJSONParser().parseJSONObject(responseObject);

            boolean isSuccess = TypeUtil.getBoolean(responseMap.get(APIKey.COMMON_SUCCESS), false);
            int statusCode = TypeUtil.getInteger(responseMap.get(APIKey.COMMON_STATUS_CODE), 0);
            String message = TypeUtil.getString(responseMap.get(APIKey.COMMON_MESSAGE), "");
            Map<String, Object> data = TypeUtil.getMap(responseMap.get(APIKey.COMMON_DATA));

            if (IS_DEBUG_PRINTING && data != null) {
                String requestID = request.getRequestID();
                Log.i(TAG, "RequestID = " + requestID + ",dataMap = " + data);
            }

            if (null != onResponseCallback) {
                if (isSuccess) {
                    onResponseCallback.onSuccess(isSuccess, statusCode, message, data);
                } else {
                    onResponseCallback.onFailure(isSuccess, statusCode, message, data);
                }
            }

            //清楚缓存文件
            if (null != tmpFileList && tmpFileList.size() > 0) {
                for (int i = 0; i < tmpFileList.size(); i++) {
                    File tmpFile = tmpFileList.get(i);
                    if (null != tmpFile && tmpFile.isFile() && tmpFile.exists()) {
                        tmpFile.delete();
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private String fileParamsKey = "";

    private RequestParams getRequestParams(Map<String, Object> paramsMap) throws FileNotFoundException {
        RequestParams requestParams = new RequestParams();

        if (IS_DEBUG_PRINTING) {
            Log.e(TAG, "RequestParams: =============== RequestStart =================");
        }
        for (Entry<String, Object> entry : paramsMap.entrySet()) {
            String key = entry.getKey();
            Object values = entry.getValue();
            if (null != key && !key.equals("id") && values != null) {
                if (values instanceof List<?>) {
                    for (Object object : (List<Object>) values) {
                        if (object instanceof File) {
                            File sourceFile = (File) object;
                            sourceFile = new File(sourceFile.getAbsolutePath());
                            File tmpFile = new File(FileUtil.getCacheBaseDir(context), Uri.encode(sourceFile.getName()));
                            FileUtil.copyFile(sourceFile, tmpFile);
                            Log.i("cth","sourceFile="+sourceFile.length());
                            Log.i("cth","tmpFile="+tmpFile.length());

                            if (null == tmpFileList) {
                                tmpFileList = new ArrayList<File>();
                            }
                            tmpFileList.add(tmpFile);

                            fileParamsKey = key;

                            if (Validator.isLocalFileValid(tmpFile)) {
                                String fileUrl = "file://" + tmpFile.getAbsolutePath();
                                String mineType = FileUtil.getMimeType(fileUrl);
                                //     requestParams.put(key + "[]", tmpFile, mineType);
                                if (IS_DEBUG_PRINTING) {
                                    Log.d(TAG, key + "[]" + ": " + tmpFile.toURI() + ",contentType:" + mineType + ",file.length = " + tmpFile.length());
                                }
                            }
                        } else {
                            requestParams.add(key + "[]", object.toString());
                            if (IS_DEBUG_PRINTING) {
                                Log.d(TAG, key + "[]" + ": " + object);
                            }
                        }
                    }

                } else if (values instanceof Object) {
                    if (values instanceof File) {
                        File sourceFile = (File) values;
                        sourceFile = new File(sourceFile.getAbsolutePath());
                        File tmpFile = new File(FileUtil.getCacheBaseDir(context), Uri.encode(sourceFile.getName()));
                        FileUtil.copyFile(sourceFile, tmpFile);

                        if (null == tmpFileList) {
                            tmpFileList = new ArrayList<File>();
                        }
                        tmpFileList.add(tmpFile);

                        if (Validator.isLocalFileValid(tmpFile)) {
                            String fileUrl = "file://" + tmpFile.getAbsolutePath();
                            String mineType = FileUtil.getMimeType(fileUrl);
                            requestParams.put(key, tmpFile, mineType);
                            if (IS_DEBUG_PRINTING) {
                                Log.d(TAG, key + ": " + tmpFile.toURI() + ",contentType:" + mineType + ",file.length = " + tmpFile.length());
                            }
                        }
                    } else {
                        requestParams.put(key, values);
                        if (IS_DEBUG_PRINTING) {
                            Log.d(TAG, key + ": " + values);
                        }
                    }
                }
            }
        }

        //是否包含文件参数
        if (null != tmpFileList && tmpFileList.size() > 0 && !TextUtils.isEmpty(fileParamsKey)) {
            File[] files = new File[tmpFileList.size()];
            for (int i = 0; i < tmpFileList.size(); i++) {
                files[i] = tmpFileList.get(i);
            }
            if (IS_DEBUG_PRINTING) {
                Log.d(TAG, fileParamsKey + "[]" + ": " + files);
            }
            requestParams.put(fileParamsKey + "[]", files);
        }

        return requestParams;
    }

    /**
     * 从本地数据库获取信息
     * 
     * @param request
     * @return
     */
    private Map<String, Object> getDataFromLocalBank(CommonRequest request) {
        Map<String, Object> result = null;
        if (IS_LOCAL_DATABANK_SUPPORTED) {
            ConnectorDatabank connectorDatabank = new ConnectorDatabank(context);
            String responseStr = connectorDatabank.getData(REQUEST_URL_DUMMAY_BASE_STRING + getRequestStr(request));
            connectorDatabank.closeDatabank();

            if (IS_DEBUG_PRINTING) {
                Log.d(TAG, "Response from local databank: " + responseStr);
            }

            CommonJSONParser commonJSONParser = new CommonJSONParser();
            result = commonJSONParser.parse(responseStr);
        }

        return result;
    }

    /**
     * 保存数据到本地
     * 
     * @param request
     * @param ins
     * @return
     * @throws IOException
     */
    private String saveDataToLocalBank(CommonRequest request, String responseStr) throws IOException {
        try {
            if (IS_LOCAL_DATABANK_SUPPORTED) {
                ConnectorDatabank connectorDatabank = new ConnectorDatabank(context);
                connectorDatabank.saveData(request.getRequestURLBaseString() + getRequestStr(request), responseStr);
                connectorDatabank.closeDatabank();
            } // if (IS_LOCAL_DATABANK_SUPPORTED)
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return responseStr;
    }

    /**
     * 打印出请求信息
     * 
     * @param requestURLSB
     *            请求的url
     * @param requestParamsSortedMap
     *            请求参数Map
     */
    private void printRequestInfo(StringBuilder requestURLSB, Map<String, Object> requestParamsMap) {
        if (IS_DEBUG_PRINTING) {
            Log.d(TAG, "Request URL: " + requestURLSB.toString());
            if (null != requestParamsMap) {
                for (Entry<String, Object> entry : requestParamsMap.entrySet()) {
                    Log.d(TAG, entry.getKey() + " : " + entry.getValue());
                }
            }
        }
    }

    /**
     * 对象转成字符串
     * 
     * @param object
     * @return
     */
    private static String convert2String(final Object object) {
        String str = null;

        if (null != object) {
            if (object instanceof String) {
                str = (String) object;
            } else if (object instanceof Boolean) {
                str = Boolean.toString((Boolean) object);
            } else if (object instanceof Long) {
                str = Long.toString((Long) object);
            } else if (object instanceof Integer) {
                str = Integer.toString((Integer) object);
            } else if (object instanceof Float) {
                str = Float.toString((Float) object);
            } else if (object instanceof Double) {
                str = Double.toString((Double) object);
            } else {
                str = object.toString();
            }
        }

        return str;
    }

    private static String getRequestStr(CommonRequest request) {
        StringBuilder requestSB = new StringBuilder();
        if (null != request) {
            final String requestApiName = request.getRequestApiName();
            final SortedMap<String, Object> requestParamsSortedMap = request.getRequestParamsSortedMap();
            requestSB.append(requestApiName.replaceAll(SLASH_SPLITTING_APPENDER, UNDERSCORE_SPLITTING_APPENDER));

            if (null != requestParamsSortedMap) {
                for (Entry<String, Object> entry : requestParamsSortedMap.entrySet()) {
                    final String paramKey = entry.getKey();
                    Object paramValue = entry.getValue();
                    if (null != paramValue) {
                        if (paramValue instanceof List<?>) {
                            for (Object paramValueObject : (List<Object>) paramValue) {
                                if (!(paramValueObject instanceof File)) {
                                    requestSB.append(UNDERSCORE_SPLITTING_APPENDER);
                                    requestSB.append(paramKey);
                                    requestSB.append(UNDERSCORE_SPLITTING_APPENDER);
                                    requestSB.append(convert2String(paramValueObject));
                                }
                            }
                        } else if (paramValue instanceof Object) {
                            if (!(paramValue instanceof File)) {
                                requestSB.append(UNDERSCORE_SPLITTING_APPENDER);
                                requestSB.append(paramKey);
                                requestSB.append(UNDERSCORE_SPLITTING_APPENDER);
                                requestSB.append(convert2String(paramValue));
                            }
                        }
                    }
                }
            }
        }

        return requestSB.toString();
    }

    public interface OnResponseCallback {
        void onSuccess(boolean isSuccess, int statusCode, String message, Map<String, Object> data);

        void onFailure(boolean isSuccess, int statusCode, String message, Map<String, Object> data);
    }

    class MyJsonHttpResponseHandler extends JsonHttpResponseHandler {
        private CommonRequest commonRequest;

        public MyJsonHttpResponseHandler(CommonRequest commonRequest) {
            super();
            this.commonRequest = commonRequest;
        }

        public CommonRequest getCommonRequest() {
            return commonRequest;
        }

        public void setCommonRequest(CommonRequest commonRequest) {
            this.commonRequest = commonRequest;
        }

    }
}
