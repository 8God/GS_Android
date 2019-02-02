package com.gaoshou.android.activity;

import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.gaoshou.android.R;
import com.gaoshou.common.base.BaseActivity;
import com.gaoshou.common.constant.APIKey;
import com.gaoshou.common.constant.ServiceAPIConstant;
import com.gaoshou.common.network.CommonRequest;
import com.gaoshou.common.utils.DialogUtils;
import com.gaoshou.common.utils.TypeUtil;
import com.gaoshou.common.utils.Validator;
import com.gaoshou.common.widget.ClearTextEditText;
import com.gaoshou.common.widget.CountDownButton;

public class RegisterationActivity extends BaseActivity implements OnClickListener {

    private String verificationCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_registeraion);

        init();
    }

    private void init() {
        initToolbar("注册");
        initUI();
    }

    private void initUI() {
        CountDownButton requestIdentifyingBtn = findView(R.id.btn_get_verification_code);
        requestIdentifyingBtn.setOnClickListener(this);

        Button registerNextBtn = findView(R.id.btn_register_next);
        registerNextBtn.setOnClickListener(this);
    }

    /**
     * 先检查手机号是否被注册过，注册过则无法获取验证码
     */
    private void checkMobileIsExisting() {
        String mobile = getEditTextInput(R.id.ctedt_mobile, "");
        CommonRequest checkMobileRequest = new CommonRequest();
        checkMobileRequest.setRequestApiName(ServiceAPIConstant.REQUEST_API_NAME_SITE_VIEW_BY_USERNAME);
        checkMobileRequest.setRequestID(ServiceAPIConstant.REQUEST_ID_SITE_VIEW_BY_USERNAME);
        checkMobileRequest.addRequestParam(APIKey.USER_USERNAME, mobile);

        addRequestAsyncTask(checkMobileRequest);

        showProgressDialog("获取验证码中");
    }

    private void requestIdentifyingCode(String mobile) {
        CommonRequest requestIdentifying = new CommonRequest();
        requestIdentifying.setRequestApiName(ServiceAPIConstant.REQUEST_API_NAME_SITE_VERIFY_CODE);
        requestIdentifying.setRequestID(ServiceAPIConstant.REQUEST_ID_SITE_VERIFY_CODE);
        requestIdentifying.addRequestParam(APIKey.COMMON_MOBILE, mobile);

        addRequestAsyncTask(requestIdentifying);
    }

    private void registerMobile() {
        ClearTextEditText mobileXEdt = findView(R.id.ctedt_mobile);
        ClearTextEditText verificationCodeXEdt = findView(R.id.ctedt_verification_code);
        ClearTextEditText pswXEdt = findView(R.id.ctedt_user_psw);

        String mobile = mobileXEdt.getText().toString();
        String verificationCode = verificationCodeXEdt.getText().toString();
        String psw = pswXEdt.getText().toString();

        boolean islegalInput = true;

        islegalInput = isMobileInputLegal(mobile);
        islegalInput = isVerificationCodeInputLegal(verificationCode);
        islegalInput = isPswInputLegal(psw);

        if (islegalInput) {
            startNextRegisteration(mobile, psw);
        }
    }

    private boolean isMobileInputLegal(String mobile) {
        boolean islegalInput = true;

        if (!Validator.isPhoneNumber(mobile)) {
            islegalInput = false;
            ClearTextEditText mobileXEdt = findView(R.id.ctedt_mobile);
            mobileXEdt.setError("请输入正确的手机号码");
        }

        return islegalInput;
    }

    private boolean isVerificationCodeInputLegal(String verificationCode) {
        boolean islegalInput = true;
        ClearTextEditText verificationCodeXEdt = findView(R.id.ctedt_verification_code);

        if (TextUtils.isEmpty(verificationCode)) {
            islegalInput = false;
            verificationCodeXEdt.setError("验证码不能为空");
        } else if (!this.verificationCode.equals(verificationCode)) {
            islegalInput = false;
            verificationCodeXEdt.setError("验证码错误");
        }

        return islegalInput;
    }

    private boolean isPswInputLegal(String psw) {
        boolean islegalInput = true;

        if (!Validator.isPassword(psw)) {
            islegalInput = false;
            ClearTextEditText pswXEdt = findView(R.id.ctedt_user_psw);
            pswXEdt.setError("密码长度为6-16位");
        }

        return islegalInput;
    }

    private void startNextRegisteration(String mobile, String psw) {
        Intent startNextStep = new Intent(getContext(), RegisterationFillInfoActivity.class);
        startNextStep.putExtra(APIKey.COMMON_MOBILE, mobile);
        startNextStep.putExtra(APIKey.USER_PASSWORD, psw);
        startActivity(startNextStep);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_get_verification_code:
                ClearTextEditText mobileXEdt = findView(R.id.ctedt_mobile);
                String mobile = mobileXEdt.getText() != null ? mobileXEdt.getText().toString() : "";
                if (!TextUtils.isEmpty(mobile)) {
                    checkMobileIsExisting();
                } else {
                    showToast("请填写手机号");
                }
                break;
            case R.id.btn_register_next:
                registerMobile();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onResponse(String requestID, boolean isSuccess, int statusCode, String message, Map<String, Object> data, Map<String, Object> additionalArgsmap) {
        super.onResponse(requestID, isSuccess, statusCode, message, data, additionalArgsmap);
        if (ServiceAPIConstant.REQUEST_ID_SITE_VIEW_BY_USERNAME.equals(requestID)) {
            if (!isSuccess && data == null) {
                String mobile = getEditTextInput(R.id.ctedt_mobile, "");
                requestIdentifyingCode(mobile);
            } else {
                dimissProgressDialog();
                showToast("该手机号已被注册");
            }

        } else if (ServiceAPIConstant.REQUEST_ID_SITE_VERIFY_CODE.equals(requestID)) {
            if (isSuccess && data != null) {
                verificationCode = TypeUtil.getString(data.get(APIKey.VERIFY_CODE), "");
                CountDownButton requestIdentifyingBtn = findView(R.id.btn_get_verification_code);
                requestIdentifyingBtn.startCountDown();
            }
            dimissProgressDialog();
            showToast(message);
        }
    }
}
