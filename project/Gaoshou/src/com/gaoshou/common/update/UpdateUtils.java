package com.gaoshou.common.update;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.text.TextUtils;

import com.gaoshou.android.R;
import com.gaoshou.common.component.BaseProgressDialog;
import com.gaoshou.common.constant.APIKey;
import com.gaoshou.common.constant.ServiceAPIConstant;
import com.gaoshou.common.network.CommonAsyncConnector;
import com.gaoshou.common.network.CommonRequest;
import com.gaoshou.common.network.IConnectorToRenderListener;
import com.gaoshou.common.utils.TypeUtil;

public class UpdateUtils {

    public static void checkVersion(final Context context) {
        checkVersion(context, false);
    }

    public static void checkVersion(final Context context, final boolean isShowProgressDialog) {

        final BaseProgressDialog baseProgressDialog = new BaseProgressDialog(context);
        if (isShowProgressDialog) {
            if (null != baseProgressDialog && !baseProgressDialog.isShowing()) {
                baseProgressDialog.setMessage("检查新版本");
                baseProgressDialog.show();
            }
        }

        CommonRequest checkVersionRequest = new CommonRequest();
        checkVersionRequest.setRequestApiName(ServiceAPIConstant.REQUEST_API_NAME_VERSION_INDEX);
        checkVersionRequest.setRequestID(ServiceAPIConstant.REQUEST_ID_VERSION_INDEX);
        checkVersionRequest.addRequestParam(APIKey.PACKAGE_NAME, context.getPackageName());
        checkVersionRequest.addRequestParam(APIKey.COMMON_OFFSET, 0);
        checkVersionRequest.addRequestParam(APIKey.COMMON_PAGE_SIZE, 1);
        checkVersionRequest.addRequestParam(APIKey.COMMON_SORT_TYPES, "desc");
        checkVersionRequest.addRequestParam(APIKey.COMMON_SORT_FIELDS, APIKey.VERSION_CODE);
        // addRequestAsyncTask(checkVersionRequest, false);
        CommonAsyncConnector commonAsyncConnector = new CommonAsyncConnector(context);
        commonAsyncConnector.setToRenderListener(new IConnectorToRenderListener() {

            @Override
            public void toRender(Map<String, Object> result) {
                if (null != result) {
                    int status = TypeUtil.getInteger(result.get(APIKey.COMMON_STATUS), -1);
                    Map<String, Object> resultMap = TypeUtil.getMap(result.get(APIKey.COMMON_RESULT));
                    Map<String, Object> versionMap = null;
                    if (null != resultMap) {
                        List<Map<String, Object>> versionList = TypeUtil.getList(resultMap.get(APIKey.VERSIONS));
                        if (null != versionList && versionList.size() > 0) {
                            versionMap = versionList.get(0);
                        }
                    }
                    if (APIKey.STATUS_SUCCESSFUL == status && versionMap != null) {
                        AppUpdater appUpdater = new AppUpdater(context);
                        final int versionCode = TypeUtil.getInteger(versionMap.get(APIKey.VERSION_CODE), 0);
                        final String versionName = TypeUtil.getString(versionMap.get(APIKey.VERSION_NAME));
                        final String packageName = TypeUtil.getString(versionMap.get(APIKey.PACKAGE_NAME));
                        final String downloadUrl = TypeUtil.getString(versionMap.get(APIKey.DOWNLOAD_URL));
                        final String appName = TypeUtil.getString(versionMap.get(APIKey.VERSION_NAME), context.getString(R.string.app_name));
                        final String versionDesc = TypeUtil.getString(versionMap.get(APIKey.VERSION_DES));
                        try {
                            if (versionCode > 0 && !TextUtils.isEmpty(packageName) && !TextUtils.isEmpty(downloadUrl)) {
                                appUpdater.update(context.getPackageManager().getPackageInfo(packageName, 0), appName, versionCode + "", versionName, versionDesc, downloadUrl);
                            }
                        } catch (NameNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
                if (isShowProgressDialog && baseProgressDialog != null && baseProgressDialog.isShowing()) {
                    baseProgressDialog.dismiss();
                }
            }
        });
        commonAsyncConnector.execute(checkVersionRequest);
    }
}
