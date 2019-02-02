package com.gaoshou.common.component;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.BaseAdapter;
import android.widget.Toast;

import com.gaoshou.common.constant.APIKey;
import com.gaoshou.common.network.CommonRequest;
import com.gaoshou.common.network.RestAPIRequester;
import com.gaoshou.common.network.RestAPIRequester.OnResponseCallback;
import com.gaoshou.common.utils.TypeUtil;

/**
 * 优化的公共适配器
 * 
 * @author CTH
 *
 */
public abstract class CommonListAdapter<T> extends BaseAdapter {
    protected Context context;
    protected LayoutInflater mInfalter;
    protected List<T> dataList;

    public CommonListAdapter(Context context, List<T> dataList) {
        this.context = context;
        this.dataList = dataList;
        this.mInfalter = LayoutInflater.from(context);
        Log.i("testYJ","success");
    }

    @Override
    public int getCount() {
        int count = 0;
        if (null != dataList) {
            count = dataList.size();
        }
        return count;
    }

    @Override
    public T getItem(int position) {
        T item = null;
        if (null != dataList) {
            item = dataList.get(position);
        }
        return item;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    protected void showToast(final CharSequence text) {
        if (null != text && 0 != text.toString().trim().length()) {
            Toast toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
            //            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
    }

    protected void showLongToast(final CharSequence text) {
        if (null != text && 0 != text.toString().trim().length()) {
            Toast toast = Toast.makeText(context, text, Toast.LENGTH_LONG);
            //            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
    }

    //    protected void addRequestAsyncTask(final CommonRequest dataLoadingRequest) {
    //
    //        final String requestID = dataLoadingRequest.getRequestID();
    //        boolean isOnlineAvailable = TypeUtil.getBoolean(dataLoadingRequest.getAdditionalArgValue(CommonRequest.REQUEST_IS_ONLINE_AVAILABLE), true);
    //
    //        CommonAsyncConnector commonAsyncConnector = new CommonAsyncConnector(context);
    //        commonAsyncConnector.setOnlineAvailable(isOnlineAvailable);
    //        commonAsyncConnector.setToRenderListener(new IConnectorToRenderListener() {
    //
    //            @Override
    //            public void toRender(Map<String, Object> result) {
    //                int status = TypeUtil.getInteger(result.get(APIKey.COMMON_STATUS), -1);
    //                String message = TypeUtil.getString(result.get(APIKey.COMMON_MESSAGE), "");
    //                Map<String, Object> resultMap = TypeUtil.getMap(result.get(APIKey.COMMON_RESULT));
    //                int toBeContinued = 0;
    //                if (null != resultMap) {
    //                    toBeContinued = TypeUtil.getInteger(resultMap.get(APIKey.COMMON_TO_BE_CONTINUED), 0);
    //                }
    //                onResponseAsyncTaskRender(status, message, toBeContinued, resultMap, requestID, dataLoadingRequest.getAdditionalArgsMap());
    //                onResponseAsyncTaskRender(result, requestID, dataLoadingRequest.getAdditionalArgsBundle());
    //                onResponseAsyncTaskRender(result, requestID, dataLoadingRequest.getAdditionalArgsMap());
    //                onResponseAsyncTaskRender(result, requestID);
    //
    //            }
    //        });
    //        commonAsyncConnector.execute(dataLoadingRequest);
    //    }

    protected void addRequestAsyncTask(final CommonRequest dataLoadingRequest) {

        final String requestID = dataLoadingRequest.getRequestID();
        final Map<String, Object> additionalArgsmap = dataLoadingRequest.getAdditionalArgsMap();

        RestAPIRequester restAPIRequester = new RestAPIRequester(context);
        restAPIRequester.setOnResponseCallback(new OnResponseCallback() {

            @Override
            public void onSuccess(boolean isSuccess, int statusCode, String message, Map<String, Object> data) {
                List<Map<String, Object>> items = null;
                Map<String, Object> links = null;
                Map<String, Object> meta = null;
                if (null != data) {
                    items = TypeUtil.getList(data.get(APIKey.COMMON_IMTES));
                    links = TypeUtil.getMap(data.get(APIKey.COMMON_LINKS));
                    meta = TypeUtil.getMap(data.get(APIKey.COMMON_META));
                }

                onResponse(requestID, isSuccess, statusCode, message, data, additionalArgsmap);
                onResponse(requestID, isSuccess, statusCode, message, items, links, meta, additionalArgsmap);
            }

            @Override
            public void onFailure(boolean isSuccess, int statusCode, String message, Map<String, Object> data) {
                List<Map<String, Object>> items = null;
                Map<String, Object> links = null;
                Map<String, Object> meta = null;
                if (null != data) {
                    items = TypeUtil.getList(data.get(APIKey.COMMON_IMTES));
                    links = TypeUtil.getMap(data.get(APIKey.COMMON_LINKS));
                    meta = TypeUtil.getMap(data.get(APIKey.COMMON_META));
                }

                onResponse(requestID, isSuccess, statusCode, message, data, additionalArgsmap);
                onResponse(requestID, isSuccess, statusCode, message, items, links, meta, additionalArgsmap);
            }
        });

        restAPIRequester.sendRequest(dataLoadingRequest);
    }

    protected void onResponse(final String requestID, final boolean isSuccess, final int statusCode, final String message, final Map<String, Object> data, final Map<String, Object> additionalArgsmap) {

    }

    protected void onResponse(final String requestID, final boolean isSuccess, final int statusCode, final String message, final List<Map<String, Object>> items, final Map<String, Object> links, final Map<String, Object> meta, final Map<String, Object> additionalArgsmap) {

    }

    protected void onResponseAsyncTaskRender(final Map<String, Object> result, final String requestID) {

    }

    protected void onResponseAsyncTaskRender(final Map<String, Object> result, final String requestID, final Bundle additionalArgsBundle) {

    }

    protected void onResponseAsyncTaskRender(final Map<String, Object> result, final String requestID, final Map<String, Object> additionalArgsMap) {

    }

    protected void onResponseAsyncTaskRender(final int status, final String message, final int toBeContinued, final Map<String, Object> resultMap, final String requestID, final Map<String, Object> additionalArgsMap) {

    }

}
