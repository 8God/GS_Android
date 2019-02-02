package com.gaoshou.android.activity;

import java.util.Map;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.baidu.mapapi.map.Text;
import com.gaoshou.android.R;
import com.gaoshou.android.entity.DoctorEntity;
import com.gaoshou.android.entity.UserEntity;
import com.gaoshou.common.base.BaseActivity;
import com.gaoshou.common.base.GsApplication;
import com.gaoshou.common.constant.APIKey;
import com.gaoshou.common.constant.ServiceAPIConstant;
import com.gaoshou.common.network.CommonRequest;
import com.gaoshou.common.utils.EntityUtils;
import com.gaoshou.common.utils.MD5UUtil;
import com.gaoshou.common.utils.NetworkUtil;
import com.gaoshou.common.utils.Validator;

public class UpdatePasswordActivity extends BaseActivity implements TextWatcher {
    private MenuItem update;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_pwd);

        initToolbar(getString(R.string.actionbar_title_update_pwd));

        initView();

    }

    private void initView() {
        ((EditText) findView(R.id.edt_old_pwd)).addTextChangedListener(this);
        ((EditText) findView(R.id.edt_new_pwd)).addTextChangedListener(this);
        ((EditText) findView(R.id.edt_reconfirm)).addTextChangedListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_update_pwd, menu);

        update = menu.findItem(R.id.item_update_pwd);
        update.setVisible(false);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case R.id.item_update_pwd:
                updatePwd();
                break;
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updatePwd() {
        String oldPwd = getEditTextInput(R.id.edt_old_pwd, null);
        String newPwd = getEditTextInput(R.id.edt_new_pwd, null);
        String reconfirmPwd = getEditTextInput(R.id.edt_reconfirm, null);
        boolean isOldPassword = isOldPassowrd(oldPwd);
        if (isOldPassword) {
            if (newPwd.equals(reconfirmPwd)) {
                if (NetworkUtil.isNetworkAvaliable(getContext())) {
                    showProgressDialog(getString(R.string.update_pwding));
                    CommonRequest updatePwdRequest = new CommonRequest();
                    updatePwdRequest.setRequestApiName(ServiceAPIConstant.REQUEST_API_NAME_USER_UPDATE + "&" + APIKey.COMMON_EXPAND + "=" + APIKey.COMMON_DOCTOR);
                    updatePwdRequest.setRequestID(ServiceAPIConstant.REQUEST_ID_USER_UPDATE);
                    updatePwdRequest.setRequestMethod(CommonRequest.REQUEST_METHOD_PUT);
                    updatePwdRequest.addRequestParam(APIKey.COMMON_ID, GsApplication.getInstance(getContext()).getUserId());
                    //添加新密码
                    updatePwdRequest.addRequestParam(APIKey.USER_PASSWORD, newPwd);

                    addRequestAsyncTask(updatePwdRequest);
                } else {
                    showToast(getString(R.string.network_error));
                }
            } else {
                showToast(getString(R.string.difference_pwd));
                return;
            }
        } else {
            showToast(getString(R.string.old_pwd_wrong));
        }

    }

    private boolean isOldPassowrd(String oldPwd) {
        String localPswMD5 = ""; //本地存放的密码MD5值
        DoctorEntity doctorEntity = GsApplication.getInstance(getContext()).getMyself();
        if (null != doctorEntity) {
            localPswMD5 = doctorEntity.getPswMD5();
        }
        String oldPwdMD5 = ""; //用户输入的旧密码MD5值
        if (!TextUtils.isEmpty(oldPwd)) {
            oldPwdMD5 = MD5UUtil.generate(oldPwd);
        }
        boolean isSame = false;
        if (!TextUtils.isEmpty(oldPwdMD5) && !TextUtils.isEmpty(localPswMD5) && localPswMD5.equals(oldPwdMD5)) {
            isSame = true;
        }
        return isSame;
    }

    private boolean isPasswordAvailable(int id) {
        String pwd = getEditTextInput(id, "");
        if (Validator.isPassword(pwd)) {
            if (pwd.length() < 6) {
                ((EditText) findView(id)).setError(getString(R.string.pwd_too_short));
            } else {
                return true;
            }
        }
        return false;
    }

    @Override
    public void afterTextChanged(Editable s) {
        boolean isOldPwdAvailable = isPasswordAvailable(R.id.edt_old_pwd);
        ;
        boolean isNewPwdAvailable = isPasswordAvailable(R.id.edt_new_pwd);
        boolean isReconfirmAvailable = isPasswordAvailable(R.id.edt_reconfirm);
        if (isOldPwdAvailable && isNewPwdAvailable && isReconfirmAvailable) {
            if (update != null)
                update.setVisible(true);
        } else {
            if (update != null)
                update.setVisible(false);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    protected void onResponse(String requestID, boolean isSuccess, int statusCode, String message, Map<String, Object> data, Map<String, Object> additionalArgsmap) {
        if (requestID == ServiceAPIConstant.REQUEST_ID_USER_UPDATE) {
            if (isSuccess && statusCode == 200) {
                if (data != null) {
                    UserEntity user = EntityUtils.getUserEntity(data);
                    if (user != null) {
                        GsApplication.getInstance(getContext()).setUser(user);
                        dimissProgressDialog();
                        showToast(getString(R.string.update_pwd_successed));
                    }
                }
            } else {
                showToast(message);
            }
        }
    }
}
