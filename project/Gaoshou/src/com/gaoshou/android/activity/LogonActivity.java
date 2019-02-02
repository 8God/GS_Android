package com.gaoshou.android.activity;

import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RadioGroup;

import com.gaoshou.android.R;
import com.gaoshou.android.entity.DoctorEntity;
import com.gaoshou.android.entity.UserEntity;
import com.gaoshou.common.base.BaseActivity;
import com.gaoshou.common.base.GsApplication;
import com.gaoshou.common.constant.APIKey;
import com.gaoshou.common.constant.CommonConstant;
import com.gaoshou.common.constant.ServiceAPIConstant;
import com.gaoshou.common.network.CommonRequest;
import com.gaoshou.common.utils.EntityUtils;
import com.gaoshou.common.utils.MD5UUtil;
import com.gaoshou.common.utils.TypeUtil;
import com.gaoshou.common.utils.Validator;

public class LogonActivity extends BaseActivity implements OnClickListener {

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        isFullScreen = true;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logon);

        setStatusBarColor(android.R.color.white);

        init();
    }

    private void init() {
        initUI();
    }

    private void initUI() {
        setViewClickListener(R.id.btn_logon, this);
        setViewClickListener(R.id.tv_open_registeration, this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_logon:
                logon();
                break;
            case R.id.tv_open_registeration:
                startActivity(new Intent(getContext(), RegisterationActivity.class));
                break;

            default:
                break;
        }
    }

    private void logon() {
        String mobile = getEditTextInput(R.id.ctedt_mobile, "");
        String psw = getEditTextInput(R.id.ctedt_psw, "");

        boolean isLegalMobile = checkMobile(mobile);
        boolean isLegalPsw = checkPsw(psw);

        if (isLegalMobile && isLegalPsw) {

            sendLogonRequest(mobile, psw);
        }
    }

    private boolean checkMobile(String mobile) {
        boolean isLegalMobile = true;

        if (!Validator.isPhoneNumber(mobile)) {
            isLegalMobile = false;

            showToast("请输入正确手机号");
        }

        return isLegalMobile;
    }

    private boolean checkPsw(String psw) {
        boolean isLegalPsw = true;

        if (TextUtils.isEmpty(psw)) {
            isLegalPsw = false;

            showToast("请输入密码");
        } else if (!Validator.isPassword(psw)) {
            isLegalPsw = false;

            showToast("密码长度为6-16位");
        }
        return isLegalPsw;
    }

    private void sendLogonRequest(String userName, String psw) {
        RadioGroup statusRadioGroup = findView(R.id.rdogp_type);
        int type = CommonConstant.LOGON_TYPE_DOCTOR;
        int checkBtnId = statusRadioGroup.getCheckedRadioButtonId();
        switch (checkBtnId) {
            case R.id.rdobtn_doctor:
                type = CommonConstant.LOGON_TYPE_DOCTOR;
                break;
            case R.id.rdobtn_expert:
                type = CommonConstant.LOGON_TYPE_EXPERT;
                break;
            default:
                break;
        }
        CommonRequest sendLogonRequest = new CommonRequest();
        sendLogonRequest.setRequestApiName(ServiceAPIConstant.REQUEST_API_NAME_USER_LOGON);
        sendLogonRequest.setRequestID(ServiceAPIConstant.REQUEST_ID_USER_LOGON);
        sendLogonRequest.addRequestParam(APIKey.USER_USERNAME, userName);
        sendLogonRequest.addRequestParam(APIKey.USER_PASSWORD, psw);
        sendLogonRequest.addRequestParam(APIKey.COMMON_TYPE, type);
        sendLogonRequest.addAdditionalArg(APIKey.USER_PASSWORD, psw);
        sendLogonRequest.setRequestMethod(CommonRequest.REQUEST_METHOD_POST);

        addRequestAsyncTask(sendLogonRequest);

        showProgressDialog("登陆中");
    }

    @Override
    protected void onResponse(String requestID, boolean isSuccess, int statusCode, String message, Map<String, Object> data, Map<String, Object> additionalArgsmap) {
        super.onResponse(requestID, isSuccess, statusCode, message, data, additionalArgsmap);
        if (ServiceAPIConstant.REQUEST_ID_USER_LOGON.equals(requestID)) {
            if (isSuccess) {
                if (null != data) {
                    UserEntity user = EntityUtils.getUserEntity(data);

                    Map<String, Object> doctorMap = TypeUtil.getMap(data.get(APIKey.COMMON_DOCTOR));
                    if (null != doctorMap) {
                        DoctorEntity doctorEntity = EntityUtils.getDoctorEntity(doctorMap);
                        if (null != doctorEntity) {
                            if (null != additionalArgsmap) {
                                String psw = TypeUtil.getString(additionalArgsmap.get(APIKey.USER_PASSWORD), "");
                                if (!TextUtils.isEmpty(psw)) {
                                    String pswMD5 = MD5UUtil.generate(psw);
                                    doctorEntity.setPswMD5(pswMD5);
                                }
                            }
                            doctorEntity.setUser(user);
                            GsApplication.getInstance(getContext()).setMyself(doctorEntity);

                            Intent startHome = new Intent(getContext(), HomeActivity.class);
                            startActivity(startHome);

                            showToast("登陆成功");

                            finish();
                        }
                    }
                }
            } else {
                showToast(message);
            }
            dimissProgressDialog();
        }
    }
}
