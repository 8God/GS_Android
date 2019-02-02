package com.gaoshou.android.activity;

import java.util.List;
import java.util.Map;

import android.app.ProgressDialog;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;

import com.gaoshou.android.R;
import com.gaoshou.android.entity.VersionEntity;
import com.gaoshou.common.base.BaseActivity;
import com.gaoshou.common.component.BasicDialog;
import com.gaoshou.common.constant.APIKey;
import com.gaoshou.common.constant.ServiceAPIConstant;
import com.gaoshou.common.network.CommonRequest;
import com.gaoshou.common.update.AppUpdater;
import com.gaoshou.common.utils.EntityUtils;
import com.gaoshou.common.utils.NetworkUtil;

public class AboutActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_about);

        initToolbar(getString(R.string.actionbar_title_about));

        String versionName = getVersionName();
        setTextView(R.id.tv_app_version, String.format(getString(R.string.about_app_version), versionName));

        findView(R.id.btn_check_update).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgressDialog(getString(R.string.about_checking_update));
                checkUpdate();
            }
        });

    }

    protected void checkUpdate() {
        if (NetworkUtil.isNetworkAvaliable(getContext())) {
            CommonRequest checkUpdateRequest = new CommonRequest();
            checkUpdateRequest.setRequestApiName(ServiceAPIConstant.REQUEST_API_NAME_VERSION_INDEX);
            checkUpdateRequest.setRequestID(ServiceAPIConstant.REQUEST_ID_VERSION_INDEX);
            checkUpdateRequest.addRequestParam(APIKey.VERSION_PACKAGE_NAME, getPackageName());
            checkUpdateRequest.addRequestParam(APIKey.COMMON_SORT, "-" + APIKey.COMMON_CREATED_AT);
            checkUpdateRequest.addRequestParam(APIKey.COMMON_PAGE_SIZE, 1);

            addRequestAsyncTask(checkUpdateRequest);
        } else {
            showToast(getString(R.string.network_error));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResponse(String requestID, boolean isSuccess, int statusCode, String message, List<Map<String, Object>> items, Map<String, Object> links, Map<String, Object> meta, Map<String, Object> additionalArgsmap) {
        if (requestID == ServiceAPIConstant.REQUEST_ID_VERSION_INDEX) {
            if (isSuccess && statusCode == 200) {
                if (items != null && items.size() > 0) {
                    List<VersionEntity> versionList = EntityUtils.getVersionEntityList(items);
                    if (versionList != null && versionList.size() > 0) {
                        VersionEntity newestVersion = versionList.get(0);
                        if (newestVersion != null) {
                            AppUpdater appUpdater = new AppUpdater(getContext());
                            int versionCode = newestVersion.getVersion_code();
                            String versionName = newestVersion.getVersion_name();
                            String versionDesc = newestVersion.getVersion_desc();
                            String packageName = newestVersion.getPackage_name();
                            String downloadUri = newestVersion.getUri();
                            String appName = newestVersion.getVersion_name();
                            if (appName == null || "".equals(appName))
                                appName = getString(R.string.app_name);
                            boolean isHasNewVersion = false;
                            try {
                                if (versionCode > 0 && !TextUtils.isEmpty(packageName) && !TextUtils.isEmpty(downloadUri)) {
                                    isHasNewVersion = appUpdater.update(getPackageManager().getPackageInfo(packageName, 0), appName, String.valueOf(versionCode), versionName, versionDesc, downloadUri);
                                }
                            } catch (NameNotFoundException e) {
                                e.printStackTrace();
                            }
                            if (!isHasNewVersion) {
                                showToast(getString(R.string.about_current_version_is_newest));
                            }
                        }
                    }
                } else {
                    showToast(getString(R.string.about_current_version_is_newest));
                }
            } else {
                showToast(message);
            }
            dimissProgressDialog();
        }
    }

    private String getVersionName() {
        String currentVersion = null;
        try {
            currentVersion = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return currentVersion;
    }
}
