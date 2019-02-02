package com.gaoshou.common.network;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.gaoshou.common.base.GsApplication;
import com.gaoshou.common.constant.APIKey;
import com.gaoshou.common.utils.FileUtil;
import com.gaoshou.common.utils.Validator;

//import java.net.URLEncoder;

public class CommonAsyncConnector extends AsyncTask<CommonRequest, Integer, Map<String, Object>> {

    private static final String SLASH_SPLITTING_APPENDER = "/";
    private static final String UNDERSCORE_SPLITTING_APPENDER = "_";
    private static final String QUESTION_SPLITTING_APPENDER = "?";
    private static final String AND_SPLITTING_APPENDER = "&";
    private static final String EQUAL_SPLITTING_APPENDER = "=";
    private static final String NULL_STR = "null";
    private static final String REQUEST_METHOD = "POST";
    private static final String QUESTION_MARK_SPLITTER = "\\?";
    private static final String PARAMS_SPLITTER = "\\?|\\&|=";
    private static final String REQUEST_URL_DUMMAY_BASE_STRING = "http://" + GsApplication.class.getPackage() + "/dummy";
    //private static final String REQUEST_URL_BASE_STRING 				= CommonConstant.HTTP_HOST + "/kswiki/api";
    private static final String CHARSET_UTF8 = "UTF-8";

    private static final int READ_TIMEOUT_MILLIS = 30000;
    private static final int CONNECTION_TIMEOUT_MILLIS = 15000;

    private static final boolean IS_AUTH_MODE = false;
    private static final boolean IS_DEBUG_PRINTING = true;
    private static final boolean IS_LOCAL_DATABANK_SUPPORTED = true;
    private static final boolean IS_HTTP_CLIENT_SUPPORTED = true;

    private IConnectorToRenderListener toRenderListener;
    private Context notApplicationContext;
    private boolean isOnlineAvailable = true;

    //    private static List<String> noCachedParamNameList;
    //    static {
    //        noCachedParamNameList = new ArrayList<String>();
    //    }

    private static List<String> secretParamNameList;
    static {
        secretParamNameList = new ArrayList<String>();
        secretParamNameList.add("password");
        secretParamNameList.add("oldPassword");
        //        secretParamNameList.add("cardPassword");
    }

    private static List<String> fileTypeParamNameList;
    static {
        fileTypeParamNameList = new ArrayList<String>();
    }

    private static List<String> noLocalDatabankKeyList;
    static {
        noLocalDatabankKeyList = new ArrayList<String>();
        if (null != fileTypeParamNameList) {
            noLocalDatabankKeyList.addAll(fileTypeParamNameList);
        }

    }

    private static List<String> noSignedParamNameList;
    static {
        noSignedParamNameList = new ArrayList<String>();
        if (null != fileTypeParamNameList) {
            noSignedParamNameList.addAll(fileTypeParamNameList);
        }
    }

    public CommonAsyncConnector(Context notApplicationContext) {
        this.notApplicationContext = notApplicationContext;
    }

    public CommonAsyncConnector(Context notApplicationContext, IConnectorToRenderListener toRenderListener) {
        this.notApplicationContext = notApplicationContext;
        this.toRenderListener = toRenderListener;
    }

    public void setOnlineAvailable(boolean isOnlineAvailable) {
        this.isOnlineAvailable = isOnlineAvailable;
    }

    public void setToRenderListener(IConnectorToRenderListener toRenderListener) {
        this.toRenderListener = toRenderListener;
    }

    @Override
    protected Map<String, Object> doInBackground(CommonRequest... requests) {
        Log.i("JNSTesting", "doInBackground");

        Map<String, Object> result = null;
        try {
            if (null != notApplicationContext) {
                ConnectivityManager connectivityManager = (ConnectivityManager) notApplicationContext.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                GsApplication gsApplication = GsApplication.getInstance(notApplicationContext);

                if (isOnlineAvailable && networkInfo != null && networkInfo.isConnected()) {
                    gsApplication.resetNetworkSettingsMessageEnabled();
                    result = sendByNetwork(requests[0]);
                } else if (IS_LOCAL_DATABANK_SUPPORTED) {

                    //                        GsApplication.showNetworkSettingsMessage();

                    CommonRequest request = requests[0];

                    if (null != request) {
                        request = encodeRequestParams(request);
                        if (IS_AUTH_MODE) {
                            //                            request = authRequestParams(request);
                        }

                        final String requestApiName = request.getRequestApiName();
                        final SortedMap<String, Object> requestParamsSortedMap = request.getRequestParamsSortedMap();

                        StringBuilder requestURLSB = new StringBuilder();
                        requestURLSB.append(request.getRequestURLBaseString());
                        //                            requestURLSB.append(REQUEST_URL_BASE_STRING);
                        requestURLSB.append(requestApiName);

                        if (IS_DEBUG_PRINTING) {
                            Log.d("CommonAsyncConnector", "Request URL: " + requestURLSB.toString());
                            if (null != requestParamsSortedMap) {
                                Log.d("CommonAsyncConnector", "Request parameters : " + requestURLSB.toString());
                                for (Entry<String, Object> entry : requestParamsSortedMap.entrySet()) {
                                    Log.d("CommonAsyncConnector", entry.getKey() + " : " + entry.getValue());
                                } // for (Entry<String, Object> entry: requestParamsSortedMap.entrySet())
                            } // if (null != requestParamsSortedMap)
                        } // if (IS_DEBUG_PRINTING)

                        ConnectorDatabank connectorDatabank = new ConnectorDatabank(notApplicationContext);
                        String responseStr = connectorDatabank.getData(request.getRequestURLBaseString() + getRequestStr(request));
                        //                            String responseStr = connectorDatabank.getData(REQUEST_URL_BASE_STRING + getRequestStr(request));
                        connectorDatabank.closeDatabank();

                        if (IS_AUTH_MODE) {
                            responseStr = parseAuthResponseStr(responseStr);
                        }

                        if (IS_DEBUG_PRINTING) {
                            Log.d("CommonAsyncConnector", "Response from local databank: " + responseStr);
                        }

                        CommonJSONParser commonJSONParser = new CommonJSONParser();
                        result = commonJSONParser.parse(responseStr);
                    } // if (null != request)
                } // else if (IS_LOCAL_DATABANK_SUPPORTED)
            } else {
                result = sendByNetwork(requests[0]);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private Map<String, Object> sendByNetwork(CommonRequest request) throws IOException {
        Log.i("JNSTesting", "run sendByNetwork()");

        Map<String, Object> result = null;

        if (IS_HTTP_CLIENT_SUPPORTED) {
            result = sendByNetworkWithHttpClient(request);
        } else {
            result = sendByNetworkWithHttpURLConnection(request);
        }

        return result;
    }

    private Map<String, Object> sendByNetworkWithHttpURLConnection(CommonRequest request) throws IOException {

        Map<String, Object> result = null;

        if (null != request) {

            request = encodeRequestParams(request);
            if (IS_AUTH_MODE) {
                //                request = authRequestParams(request);
            }

            final String requestApiName = request.getRequestApiName();
            final SortedMap<String, Object> requestParamsSortedMap = request.getRequestParamsSortedMap();

            StringBuilder requestURLSB = new StringBuilder();
            requestURLSB.append(request.getRequestURLBaseString());
            //            requestURLSB.append(REQUEST_URL_BASE_STRING);
            requestURLSB.append(requestApiName);

            if (IS_DEBUG_PRINTING) {
                Log.d("CommonAsyncConnector", "Request URL: " + requestURLSB.toString());
                if (null != requestParamsSortedMap) {
                    Log.d("CommonAsyncConnector", "Request parameters : " + requestURLSB.toString());
                    for (Entry<String, Object> entry : requestParamsSortedMap.entrySet()) {
                        Log.d("CommonAsyncConnector", entry.getKey() + " : " + entry.getValue());
                    } // for (Entry<String, Object> entry: requestParamsSortedMap.entrySet())
                } // if (null != requestParamsSortedMap)
            } // if (IS_DEBUG_PRINTING)

            if (null != requestApiName) {
                InputStream is = null;

                try {
                    String connUrl = request.getRequestURLBaseString() + requestApiName;
                    //                    String connUrl = REQUEST_URL_BASE_STRING + requestApiName;
                    URL url = new URL(connUrl.trim());
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setReadTimeout(READ_TIMEOUT_MILLIS);
                    conn.setConnectTimeout(CONNECTION_TIMEOUT_MILLIS);
                    // 设置以POST方式
                    conn.setRequestMethod(REQUEST_METHOD);
                    // 因为这个是POST请求，设置为true
                    conn.setDoOutput(true);
                    conn.setDoInput(true);

                    // POST请求不能使用缓存
                    conn.setUseCaches(false);
                    conn.setInstanceFollowRedirects(true);

                    // 配置本次连接的Content-type，配置为application/x-www-form-urlencoded的
                    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                    conn.connect();

                    StringBuilder paramsSB = new StringBuilder();
                    if (null != requestParamsSortedMap) {

                        for (Entry<String, Object> entry : requestParamsSortedMap.entrySet()) {
                            final String paramKey = entry.getKey();
                            Object paramValue = entry.getValue();
                            if (null != paramValue) {
                                if (paramValue instanceof List<?>) {
                                    for (Object paramValueObject : (List<Object>) paramValue) {
                                        if (0 != paramsSB.length()) {
                                            paramsSB.append(AND_SPLITTING_APPENDER);
                                        }
                                        paramsSB.append(paramKey);
                                        paramsSB.append(EQUAL_SPLITTING_APPENDER);
                                        paramsSB.append(Uri.encode(convert2String(paramValueObject)));
                                    } // for (Object paramValueObject: (List<Object>) paramValue)

                                } else if (paramValue instanceof Object) {
                                    if (0 != paramsSB.length()) {
                                        paramsSB.append(AND_SPLITTING_APPENDER);
                                    }
                                    paramsSB.append(paramKey);
                                    paramsSB.append(EQUAL_SPLITTING_APPENDER);
                                    paramsSB.append(Uri.encode(convert2String(paramValue)));
                                }
                            } // if (null != paramValue)

                        } // for (Entry<String, Object> entry: requestParamsSortedMap.entrySet())
                    } // if (null != requestParamsSortedMap)

                    if (0 != paramsSB.length()) {
                        DataOutputStream out = new DataOutputStream(conn.getOutputStream());
                        out.write(paramsSB.toString().getBytes(CHARSET_UTF8));
                    }

                    if (HttpURLConnection.HTTP_OK == conn.getResponseCode()) {
                        is = conn.getInputStream();

                        String responseStr = myRead(is);

                        if (IS_LOCAL_DATABANK_SUPPORTED) {
                            ConnectorDatabank connectorDatabank = new ConnectorDatabank(notApplicationContext);
                            connectorDatabank.saveData(request.getRequestURLBaseString() + getRequestStr(request), responseStr);
                            connectorDatabank.closeDatabank();
                        } // if (IS_LOCAL_DATABANK_SUPPORTED)

                        if (IS_AUTH_MODE) {
                            responseStr = parseAuthResponseStr(responseStr);
                        }

                        if (IS_DEBUG_PRINTING) {
                            Log.d("CommonAsyncConnector", "Response from remote server: " + responseStr);
                        }

                        CommonJSONParser commonJSONParser = new CommonJSONParser();
                        result = commonJSONParser.parse(responseStr);
                    } else {
                        throw new Exception();
                    }

                } catch (Exception e) {

                    if (IS_LOCAL_DATABANK_SUPPORTED) {
                        ConnectorDatabank connectorDatabank = new ConnectorDatabank(notApplicationContext);
                        String responseStr = connectorDatabank.getData(REQUEST_URL_DUMMAY_BASE_STRING + getRequestStr(request));
                        connectorDatabank.closeDatabank();

                        if (IS_AUTH_MODE) {
                            responseStr = parseAuthResponseStr(responseStr);
                        }

                        if (IS_DEBUG_PRINTING) {
                            Log.d("CommonAsyncConnector", "Response from local databank: " + responseStr);
                        }

                        CommonJSONParser commonJSONParser = new CommonJSONParser();
                        result = commonJSONParser.parse(responseStr);
                    } // if (IS_LOCAL_DATABANK_SUPPORTED)
                } finally {
                    if (is != null) {
                        is.close();
                    }
                }

            } // if (null != requestApiName)

        } // if (null != request)

        return result;

    }

    @SuppressWarnings("deprecation")
    private Map<String, Object> sendByNetworkWithHttpClient(CommonRequest request) throws IOException {

        Map<String, Object> result = null;

        if (null != request) {

            request = encodeRequestParams(request);
            if (IS_AUTH_MODE) {
                //                request = authRequestParams(request);
            }

            final String requestApiName = request.getRequestApiName();
            final SortedMap<String, Object> requestParamsSortedMap = request.getRequestParamsSortedMap();

            StringBuilder requestURLSB = new StringBuilder();
            requestURLSB.append(request.getRequestURLBaseString());
            //            requestURLSB.append(REQUEST_URL_BASE_STRING);
            requestURLSB.append(requestApiName);

            if (IS_DEBUG_PRINTING) {
                Log.d("CommonAsyncConnector", "Request URL: " + requestURLSB.toString());
                if (null != requestParamsSortedMap) {
                    Log.d("CommonAsyncConnector", "Request parameters : " + requestURLSB.toString());
                    for (Entry<String, Object> entry : requestParamsSortedMap.entrySet()) {
                        Log.d("CommonAsyncConnector", entry.getKey() + " : " + entry.getValue());
                    } // for (Entry<String, Object> entry: requestParamsSortedMap.entrySet())
                } // if (null != requestParamsSortedMap)
            } // if (IS_DEBUG_PRINTING)

            if (null != requestApiName) {

                List<File> tmpFileList = null;
                String postUrl = request.getRequestURLBaseString() + requestApiName;
                //                String postUrl = REQUEST_URL_BASE_STRING + requestApiName;
                HttpPost httpPost = new HttpPost(postUrl.trim());
                MultipartEntity multipartEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
                if (null != requestParamsSortedMap) {
                    for (Entry<String, Object> entry : requestParamsSortedMap.entrySet()) {
                        final String paramKey = entry.getKey();
                        final Object paramValue = entry.getValue();
                        if (null != paramValue) {
                            if (paramValue instanceof List<?>) {
                                for (Object paramValueObject : (List<Object>) paramValue) {
                                    final String paramValueStr = convert2String(paramValueObject);
                                    if (null != fileTypeParamNameList && fileTypeParamNameList.contains(paramKey)) {
                                        final File sourceFile = new File(paramValueStr);
                                        if (null != sourceFile) {
                                            File targetFile = sourceFile;
                                            final String sourceFileName = sourceFile.getName();
                                            if (!TextUtils.isEmpty(sourceFileName)) {
                                                String targetFileName = null;
                                                //                                                try {
                                                //                                                    targetFileName = URLEncoder.encode(sourceFileName, CHARSET_UTF8);
                                                //                                                } catch (UnsupportedEncodingException e) {
                                                //                                                    targetFileName = sourceFileName;
                                                //                                                }
                                                targetFileName = Uri.encode(sourceFileName);
                                                if (!sourceFileName.equals(targetFileName)) {
                                                    targetFile = new File(sourceFile.getParent(), targetFileName);
                                                    if (null != targetFile && !targetFile.exists()) {
                                                        FileUtil.copyFile(sourceFile, targetFile);
                                                        if (null == tmpFileList) {
                                                            tmpFileList = new ArrayList<File>();
                                                        }
                                                        tmpFileList.add(targetFile);
                                                    } // if (null != targetFile && !targetFile.exists())                                       

                                                } // if (!sourceFileName.equals(targetFileName))

                                            } // if (!TextUtils.isEmpty(sourceFileName))

                                            if (Validator.isLocalFileValid(targetFile)) {
                                                String fileUrl = "file://" + targetFile.getAbsolutePath();
                                                String mineType = FileUtil.getMimeType(fileUrl);
                                                multipartEntity.addPart(paramKey + "[]", new FileBody(targetFile, mineType));
                                            } // if (Validator.isLocalFileValid(targetFile))
                                        } // if (null != sourceFile)
                                    } else {
                                        //                                        multipartEntity.addPart(paramKey + "[]", new StringBody(Uri.encode(paramValueStr)));
                                        multipartEntity.addPart(paramKey + "[]", new StringBody(paramValueStr));
                                    }
                                }
                            } else if (paramValue instanceof Object) {
                                final String paramValueStr = convert2String(paramValue);
                                if (null != fileTypeParamNameList && fileTypeParamNameList.contains(paramKey)) {
                                    final File sourceFile = new File(paramValueStr);
                                    if (null != sourceFile) {
                                        File targetFile = sourceFile;
                                        final String sourceFileName = sourceFile.getName();
                                        if (!TextUtils.isEmpty(sourceFileName)) {
                                            String targetFileName = null;
                                            //                                            try {
                                            //                                                targetFileName = URLEncoder.encode(sourceFileName, CHARSET_UTF8);
                                            //                                            } catch (UnsupportedEncodingException e) {
                                            //                                                targetFileName = sourceFileName;
                                            //                                            }
                                            targetFileName = Uri.encode(sourceFileName);
                                            if (!sourceFileName.equals(targetFileName)) {
                                                targetFile = new File(sourceFile.getParent(), targetFileName);
                                                if (null != targetFile && !targetFile.exists()) {
                                                    FileUtil.copyFile(sourceFile, targetFile);
                                                    if (null == tmpFileList) {
                                                        tmpFileList = new ArrayList<File>();
                                                    }
                                                    tmpFileList.add(targetFile);
                                                } // if (null != targetFile && !targetFile.exists())                                       

                                            } // if (!sourceFileName.equals(targetFileName))

                                        } // if (!TextUtils.isEmpty(sourceFileName))

                                        if (Validator.isLocalFileValid(targetFile)) {
                                            String fileUrl = "file://" + targetFile.getAbsolutePath();
                                            String mineType = FileUtil.getMimeType(fileUrl);
                                            multipartEntity.addPart(paramKey, new FileBody(targetFile, mineType));
                                        } // if (Validator.isLocalFileValid(targetFile))
                                    } // if (null != sourceFile)
                                } else {
                                    //                                    multipartEntity.addPart(paramKey, new StringBody(Uri.encode(paramValueStr)));
                                    multipartEntity.addPart(paramKey, new StringBody(paramValueStr));
                                }
                            } // else if (paramValue instanceof Object)

                        } // if (null != paramValue)
                    } // for (Entry<String, Object> entry: requestParamsSortedMap.entrySet())                    
                } // if (null != requestParamsSortedMap)
                httpPost.setEntity(multipartEntity);

                HttpClient httpClient = new DefaultHttpClient();
                httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, CONNECTION_TIMEOUT_MILLIS);
                httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, READ_TIMEOUT_MILLIS);
                HttpResponse httpResponse = httpClient.execute(httpPost);
                if (null != tmpFileList) {
                    for (File tmpFile : tmpFileList) {
                        if (null != tmpFile && tmpFile.exists()) {
                            tmpFile.delete();
                        } // if (null != tmpFile && tmpFile.exists())
                    } // for (File tmpFile : tmpFileList)
                } // if (null != tmpFileList)

                String responseStr = EntityUtils.toString(httpResponse.getEntity(), CHARSET_UTF8);

                if (IS_LOCAL_DATABANK_SUPPORTED) {
                    ConnectorDatabank connectorDatabank = new ConnectorDatabank(notApplicationContext);
                    connectorDatabank.saveData(request.getRequestURLBaseString() + getRequestStr(request), responseStr);
                    connectorDatabank.closeDatabank();
                } // if (IS_LOCAL_DATABANK_SUPPORTED)

                if (IS_AUTH_MODE) {
                    if (IS_DEBUG_PRINTING) {
                        Log.d("CommonAsyncConnector", "Raw response from remote server: " + responseStr);
                    }
                    responseStr = parseAuthResponseStr(responseStr);
                }

                if (IS_DEBUG_PRINTING) {
                    Log.d("CommonAsyncConnector", "Response from remote server: " + responseStr);
                }

                CommonJSONParser commonJSONParser = new CommonJSONParser();
                result = commonJSONParser.parse(responseStr);

            } // if (null != requestApiName)
        } // if (null != request)

        return result;

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
                    if (null != noLocalDatabankKeyList && !noLocalDatabankKeyList.contains(paramKey)) {
                        if (null != paramValue) {
                            if (paramValue instanceof List<?>) {
                                for (Object paramValueObject : (List<Object>) paramValue) {
                                    requestSB.append(UNDERSCORE_SPLITTING_APPENDER);
                                    requestSB.append(paramKey);
                                    requestSB.append(UNDERSCORE_SPLITTING_APPENDER);
                                    requestSB.append(convert2String(paramValueObject));
                                } // for (Object paramValueObject: (List<Object>) paramValue)

                            } else if (paramValue instanceof Object) {
                                requestSB.append(UNDERSCORE_SPLITTING_APPENDER);
                                requestSB.append(paramKey);
                                requestSB.append(UNDERSCORE_SPLITTING_APPENDER);
                                requestSB.append(convert2String(paramValue));
                            }
                        } // if (null != paramValue)
                    }

                } // for (Entry<String, Object> entry: requestParamsSortedMap.entrySet())
            } // if (null != requestParamsSortedMap)
        } // if (null != request)

        return requestSB.toString();
    }

    @Override
    protected void onPostExecute(Map<String, Object> result) {
        super.onPostExecute(result);
        //添加isOnlineAvailable条件，如果是网络请求为空才显示提示，如果是从离线数据库拿到空数据则不提示网络连接问题
        if (null == result && isOnlineAvailable && null != notApplicationContext) {
            GsApplication.getInstance(notApplicationContext).showNetworkSettingsMessage();
        } // if (null == result && null != notApplicationContext)

        if (null != toRenderListener) {
            try {
                toRenderListener.toRender(result);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } // if (null != toRenderListener)
    }

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

    private String parseAuthResponseStr(final String authResponseStr) {
        //        String responseStr = null;
        //        try {
        //            responseStr = Des3Util.decrypt(authResponseStr, GsApplication.getInstance(notApplicationContext).getUserPassword());
        //        } catch (Exception e) {
        //            responseStr = authResponseStr;
        //        }
        //        
        //        return responseStr;
        return authResponseStr;
    }

    private static String encode(String str) {
        String encodedStr = str;

        if (null != str) {
            //            try {
            //                encodedStr = URLEncoder.encode(str, CHARSET_UTF8);
            //            } catch (UnsupportedEncodingException e) {
            //                encodedStr = str;
            //            }
            encodedStr = Uri.encode(str);
        } // if (null != str)

        return encodedStr;
    }

    private static List<String> encode(List<Object> objectList) {
        List<String> encodedStrList = null;

        if (null != objectList) {
            encodedStrList = new ArrayList<String>();
            for (Object object : objectList) {
                String encodedStr = encode(convert2String(object));
                if (null != encodedStr) {
                    encodedStrList.add(encodedStr);
                } // if (null != encodedStr)
            } // for (Object object : objectList)
        } // if (null != objectList)

        return encodedStrList;
    }

    private static CommonRequest encodeRequestParams(CommonRequest request) {
        if (null != request) {
            final SortedMap<String, Object> requestParamsSortedMap = request.getRequestParamsSortedMap();
            if (null != requestParamsSortedMap) {
                for (Entry<String, Object> entry : requestParamsSortedMap.entrySet()) {
                    final String paramKey = entry.getKey();
                    Object paramValue = entry.getValue();
                    if (!fileTypeParamNameList.contains(entry.getKey())) {
                        if (null != paramValue) {
                            if (paramValue instanceof List<?>) {
                                request.addRequestParam(paramKey, encode((List<Object>) paramValue));
                            } else if (paramValue instanceof Object) {
                                request.addRequestParam(paramKey, encode(convert2String(paramValue)));
                            }
                        } // if (null != paramValue)
                    } // if (!fileTypeParamNameList.contains(entry.getKey()))

                } // for (Entry<String, Object> entry: requestParamsSortedMap.entrySet())
            } // if (null != requestParamsSortedMap)
        } // if (null != request)

        return request;
    }

    //    private CommonRequest authRequestParams(CommonRequest request) {
    //
    //        if (null != request) {
    //            StringBuilder unsignedDataSB = new StringBuilder();
    //            unsignedDataSB.append(request.getRequestURLBaseString());
    //            unsignedDataSB.append(request.getRequestApiName());
    //            final SortedMap<String, Object> requestParamsSortedMap = request.getRequestParamsSortedMap();
    //            if (null != requestParamsSortedMap) {
    //                StringBuilder cachedParamsSB = new StringBuilder();
    //                for (Entry<String, Object> entry : requestParamsSortedMap.entrySet()) {
    //                    if (null != entry) {
    //                        final String paramKey = entry.getKey();
    //                        Object paramValue = entry.getValue();
    //
    //                        if (null != noCachedParamNameList && !noCachedParamNameList.contains(paramKey)) {
    //                            paramValue = request.getRequestParamValue(paramKey);
    //                            if (null != paramValue) {
    //                                if (paramValue instanceof List<?>) {
    //                                    for (Object paramValueObject : (List<Object>) paramValue) {
    //                                        if (0 != cachedParamsSB.length()) {
    //                                            cachedParamsSB.append(AND_SPLITTING_APPENDER);
    //                                        }
    //                                        cachedParamsSB.append(paramKey);
    //                                        cachedParamsSB.append(EQUAL_SPLITTING_APPENDER);
    //                                        cachedParamsSB.append(convert2String(paramValueObject));
    //                                    } // for (Object paramValueObject: (List<Object>) paramValue)
    //
    //                                } else if (paramValue instanceof Object) {
    //                                    if (0 != cachedParamsSB.length()) {
    //                                        cachedParamsSB.append(AND_SPLITTING_APPENDER);
    //                                    }
    //                                    cachedParamsSB.append(paramKey);
    //                                    cachedParamsSB.append(EQUAL_SPLITTING_APPENDER);
    //                                    cachedParamsSB.append(convert2String(paramValue));
    //                                }
    //                            } // if (null != paramValue)
    //                        } // if (null != noSignedParamNameList && !noSignedParamNameList.contains(paramKey))
    //
    //                    } // if (null != entry)                    
    //                } // for (Entry<String, Object> entry : requestParamsSortedMap.entrySet())
    //
    //                String cacheKey = SignUtil.sign(request.getRequestURLBaseString() + request.getRequestApiName() + cachedParamsSB, DEFAULT_SIGN_ALGORITHM);
    //                if (null != cacheKey) {
    //                    request.addRequestParam(APIKey.COMMON_CACHE_KEY, cacheKey);
    //                } // if (null != cacheKey)
    //
    //                if (IS_DEBUG_PRINTING) {
    //                    Log.d("CommonAsyncConnector", "cachedParamsSB.toString() = " + cachedParamsSB.toString());
    //                    Log.d("CommonAsyncConnector", "cacheKey = " + cacheKey);
    //                }
    //
    //                StringBuilder unsignedParamsSB = new StringBuilder();
    //                for (Entry<String, Object> entry : requestParamsSortedMap.entrySet()) {
    //
    //                    if (null != entry) {
    //                        final String paramKey = entry.getKey();
    //                        Object paramValue = entry.getValue();
    //
    //                        if (null != noSignedParamNameList && !noSignedParamNameList.contains(paramKey)) {
    //                            paramValue = request.getRequestParamValue(paramKey);
    //                            if (null != paramValue) {
    //                                if (paramValue instanceof List<?>) {
    //                                    for (Object paramValueObject : (List<Object>) paramValue) {
    //                                        if (0 != unsignedParamsSB.length()) {
    //                                            unsignedParamsSB.append(AND_SPLITTING_APPENDER);
    //                                        }
    //                                        unsignedParamsSB.append(paramKey);
    //                                        unsignedParamsSB.append(EQUAL_SPLITTING_APPENDER);
    //                                        unsignedParamsSB.append(convert2String(paramValueObject));
    //                                    } // for (Object paramValueObject: (List<Object>) paramValue)
    //
    //                                } else if (paramValue instanceof Object) {
    //                                    if (0 != unsignedParamsSB.length()) {
    //                                        unsignedParamsSB.append(AND_SPLITTING_APPENDER);
    //                                    }
    //                                    unsignedParamsSB.append(paramKey);
    //                                    unsignedParamsSB.append(EQUAL_SPLITTING_APPENDER);
    //                                    unsignedParamsSB.append(convert2String(paramValue));
    //                                }
    //                            } // if (null != paramValue)
    //                        } // if (null != noSignedParamNameList && !noSignedParamNameList.contains(paramKey))
    //
    //                    } // if (null != entry)
    //
    //                } // for (Entry<String, Object> entry: requestParamsSortedMap.entrySet()) 
    //
    //                unsignedDataSB.append(unsignedParamsSB);
    //
    //            } // if (null != requestParamsSortedMap)      
    //
    //            unsignedDataSB.append(SignUtil.getFingerprintHexString(notApplicationContext, notApplicationContext.getPackageName(), DEFAULT_SIGN_ALGORITHM));
    //
    //            String sign = SignUtil.sign(unsignedDataSB.toString(), DEFAULT_SIGN_ALGORITHM);
    //
    //            if (IS_DEBUG_PRINTING) {
    //                Log.d("CommonAsyncConnector", "unsignedDataSB.toString() = " + unsignedDataSB.toString());
    //                Log.d("CommonAsyncConnector", "sign = " + sign);
    //            }
    //
    //        } // if (null != request)
    //
    //        return request;
    //    }

    private static String myRead(InputStream stream) throws IOException {

        StringBuffer sb = new StringBuffer();

        if (null != stream) {
            BufferedReader br = new BufferedReader(new InputStreamReader(stream, CHARSET_UTF8));
            String data = "";
            while ((data = br.readLine()) != null) {
                sb.append(data);
            }
        }

        return sb.toString();
    }

}
