package com.gaoshou.common.base;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gaoshou.android.R;
import com.gaoshou.common.component.BaseProgressDialog;
import com.gaoshou.common.component.DefaultImageLoadingListener;
import com.gaoshou.common.component.DefaultImageLoadingListener.IImageOnLoadingCompleteListener;
import com.gaoshou.common.constant.APIKey;
import com.gaoshou.common.network.CommonAsyncConnector;
import com.gaoshou.common.network.CommonRequest;
import com.gaoshou.common.network.IConnectorToRenderListener;
import com.gaoshou.common.network.RestAPIRequester;
import com.gaoshou.common.network.RestAPIRequester.OnResponseCallback;
import com.gaoshou.common.utils.TypeUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

public class BasePager extends LinearLayout {

    private int defaultImage = 0;

    private DisplayImageOptions.Builder displayImageOptionsBuilder = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.loading_bg).showImageForEmptyUri(R.drawable.loading_bg).showImageOnFail(R.drawable.loading_bg).cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).imageScaleType(ImageScaleType.EXACTLY).bitmapConfig(Bitmap.Config.RGB_565);
    private boolean canAsyncRender = true;

    private BaseProgressDialog baseProgressDialog;

    public BasePager(Context context) {
        super(context);
    }

    public BasePager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    protected void showToast(final CharSequence text) {
        if (null != text && 0 != text.toString().trim().length()) {
            Toast toast = Toast.makeText(getContext(), text, Toast.LENGTH_SHORT);
            //            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
    }

    protected void setDefaultImage(int defaultImage) {
        this.defaultImage = defaultImage;
    }

    public void getDataList() {

    }

    protected boolean isCanAsyncRender() {
        return canAsyncRender;
    }

    protected void setCanAsyncRender(boolean canAsyncRender) {
        this.canAsyncRender = canAsyncRender;
    }

    //    protected void addRequestAsyncTask(final CommonRequest dataLoadingRequest) {
    //
    //        final String requestID = dataLoadingRequest.getRequestID();
    //        boolean isOnlineAvailable = TypeUtil.getBoolean(dataLoadingRequest.getAdditionalArgValue(CommonRequest.REQUEST_IS_ONLINE_AVAILABLE), true);
    //
    //        CommonAsyncConnector commonAsyncConnector = new CommonAsyncConnector(getContext());
    //        commonAsyncConnector.setOnlineAvailable(isOnlineAvailable);
    //        commonAsyncConnector.setToRenderListener(new IConnectorToRenderListener() {
    //
    //            @Override
    //            public void toRender(Map<String, Object> result) {
    //                if (canAsyncRender) {
    //                    int status = TypeUtil.getInteger(result.get(APIKey.COMMON_STATUS), -1);
    //                    String message = TypeUtil.getString(result.get(APIKey.COMMON_MESSAGE), "");
    //                    Map<String, Object> resultMap = TypeUtil.getMap(result.get(APIKey.COMMON_RESULT));
    //                    int toBeContinued = 0;
    //                    if (null != resultMap) {
    //                        toBeContinued = TypeUtil.getInteger(resultMap.get(APIKey.COMMON_TO_BE_CONTINUED), 0);
    //                    }
    //                    onResponseAsyncTaskRender(status, message, toBeContinued, resultMap, requestID, dataLoadingRequest.getAdditionalArgsMap());
    //                    onResponseAsyncTaskRender(result, requestID, dataLoadingRequest.getAdditionalArgsBundle());
    //                    onResponseAsyncTaskRender(result, requestID, dataLoadingRequest.getAdditionalArgsMap());
    //                    onResponseAsyncTaskRender(result, requestID);
    //                } else {
    //
    //                }
    //
    //            }
    //        });
    //        commonAsyncConnector.execute(dataLoadingRequest);
    //    }

    private void addRequestAsyncTask(final CommonRequest dataLoadingRequest) {
        final String requestID = dataLoadingRequest.getRequestID();
        final Map<String, Object> additionalArgsmap = dataLoadingRequest.getAdditionalArgsMap();

        RestAPIRequester restAPIRequester = new RestAPIRequester(getContext());
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

    protected void showProgressDialog() {
        showProgressDialog(null);
    }

    protected void showProgressDialog(String msg) {
        if (null == baseProgressDialog) {
            baseProgressDialog = new BaseProgressDialog(getContext());
        }

        if (null != baseProgressDialog && !baseProgressDialog.isShowing()) {
            if (!TextUtils.isEmpty(msg)) {
                baseProgressDialog.setMessage(msg);
            }
            baseProgressDialog.show();
        }
    }

    protected void dimissProgressDialog() {
        if (null != baseProgressDialog && baseProgressDialog.isShowing()) {
            baseProgressDialog.dismiss();
        }
    }

    protected <T extends View> T findView(View layoutView, int viewId) {
        return (T) layoutView.findViewById(viewId);
    }

    /******************************* 设置TextView *******************************/
    public void setTextView(View layout, int viewId, String text) {
        setTextView(layout, viewId, text, false);
    }

    public void setTextView(View layout, int viewId, String text, boolean isEmptyGone) {
        TextView tv = findView(layout, viewId);
        if (null != tv) {
            if (!TextUtils.isEmpty(text)) {
                tv.setText(text);
            } else {
                if (isEmptyGone) {
                    tv.setVisibility(View.GONE);
                } else {
                    tv.setText(null);
                }
            }
        }
    }

    /******************************* 设置ImageView *******************************/
    protected void setImageView(View layout, int imageViewId, String imageUri) {
        setImageView(layout, imageViewId, imageUri, 0, false, null);
    }

    protected void setImageView(View layout, int imageViewId, String imageUri, boolean isOpenLoadAnimation) {
        setImageView(layout, imageViewId, imageUri, 0, isOpenLoadAnimation, null);
    }

    protected void setImageView(View layout, int imageViewId, String imageUri, IImageOnLoadingCompleteListener iImageOnLoadingCompleteListener) {
        setImageView(layout, imageViewId, imageUri, 0, false, iImageOnLoadingCompleteListener);
    }

    protected void setImageView(View layout, int imageViewId, String imageUri, int defaultImage) {
        setImageView(layout, imageViewId, imageUri, defaultImage, false, null);
    }

    protected void setImageView(View layout, int imageViewId, String imageUri, int defaultImage, boolean isOpenLoadAnimation) {
        setImageView(layout, imageViewId, imageUri, defaultImage, isOpenLoadAnimation, null);
    }

    protected void setImageView(View layout, int imageViewId, String imageUri, int defaultImage, IImageOnLoadingCompleteListener iImageOnLoadingCompleteListener) {
        setImageView(layout, imageViewId, imageUri, defaultImage, false, iImageOnLoadingCompleteListener);
    }

    private void setImageView(View layout, int imageViewId, String imageUri, int defaultImage, boolean isOpenLoadAnimation, IImageOnLoadingCompleteListener iImageOnLoadingCompleteListener) {
        ImageView imageView = findView(layout, imageViewId);
        if (null != imageView && getContext() != null) {
            if (defaultImage != 0) {
                displayImageOptionsBuilder.showImageOnFail(defaultImage).showImageForEmptyUri(defaultImage);

                Drawable defaultDrawable = getContext().getResources().getDrawable(defaultImage);
                if (null != defaultDrawable) {
                    imageView.setImageDrawable(defaultDrawable);
                }
            } else {
                displayImageOptionsBuilder.showImageOnFail(this.defaultImage).showImageForEmptyUri(this.defaultImage);
            }

            DefaultImageLoadingListener defaultImageLoadingListener = null;
            if (null == iImageOnLoadingCompleteListener) {
                defaultImageLoadingListener = new DefaultImageLoadingListener(getContext(), imageView, isOpenLoadAnimation);
            } else {
                defaultImageLoadingListener = new DefaultImageLoadingListener(getContext(), imageView, isOpenLoadAnimation, iImageOnLoadingCompleteListener);
            }
            ImageLoader.getInstance().displayImage(imageUri, imageView, displayImageOptionsBuilder.build(), defaultImageLoadingListener);
        }
    }

    /******************************* 设置View点击事件 *******************************/
    protected void setViewClickListener(View layout, int viewID, OnClickListener onClickListener) {
        View view = findView(layout, viewID);
        if (null != view) {
            if (null != onClickListener) {
                view.setOnClickListener(onClickListener);
            }
        }
    }

    /******************************* 获取EditText的输入内容 *******************************/
    protected String getEditTextInput(View layout, int edtID, String defaultValues) {
        String input = null;

        EditText edt = findView(layout, edtID);
        if (null != edt) {
            input = edt.getText() != null ? edt.getText().toString() : "";
        }

        return input;
    }

    /******************************* 设置EditText的text *******************************/
    protected void setEditText(View layout, int edtID, String text) {

        EditText edt = findView(layout, edtID);
        if (null != edt) {
            edt.setText(text);
        }

    }

    public boolean canListScrollVertically(int direction) {
        return false;
    }

}
