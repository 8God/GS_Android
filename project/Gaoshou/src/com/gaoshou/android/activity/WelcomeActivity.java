package com.gaoshou.android.activity;

import java.util.Map;

import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import com.gaoshou.android.R;
import com.gaoshou.android.entity.DoctorEntity;
import com.gaoshou.android.entity.UserEntity;
import com.gaoshou.common.base.BaseActivity;
import com.gaoshou.common.base.GsApplication;
import com.gaoshou.common.constant.APIKey;
import com.gaoshou.common.network.CommonJSONParser;
import com.gaoshou.common.utils.EntityUtils;
import com.gaoshou.common.utils.MD5UUtil;
import com.gaoshou.common.utils.TypeUtil;

public class WelcomeActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        isFullScreen = true;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        init();
    }

    private void init() {
        preLoadData();

        //TODO 加入引导页的时候去注释该部分代码
        //        int currentVersionCode = 0;
        //        try {
        //            currentVersionCode = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
        //        } catch (NameNotFoundException e) {
        //            e.printStackTrace();
        //        }
        //
        //        int lastVersionCode = GsApplication.getInstance(getContext()).getCurrentVersionCode();
        //        if (currentVersionCode == 0 || lastVersionCode < currentVersionCode) {
        //            GsApplication.getInstance(getContext()).setCurrentVersionCode(currentVersionCode);
        //
        //            startActivity(new Intent(getContext(),Guide));
        //        } else {
        checkLogon();
        //        }

    }

    private void checkLogon() {
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                boolean isLogon = GsApplication.getInstance(getContext()).isLogon();
                if (isLogon) {
                    startHome();
                } else {
                    startLogon();
                }

                finish();
            }
        }, 1000);
    }

    private void startHome() {
        Intent startHome = new Intent(getContext(), HomeActivity.class);
        startActivity(startHome);
    }

    private void startLogon() {
        Intent startHome = new Intent(getContext(), LogonActivity.class);
        startActivity(startHome);
    }

    private void preLoadData() {

    }

    //TODO 伪装登录，拿到数据后
    private void fakeLogon() {
        String logonJson = getString(R.string.logonJson);
        CommonJSONParser parser = new CommonJSONParser();
        Map<String, Object> logonMap = parser.parse(logonJson);
        if (null != logonMap) {
            UserEntity user = EntityUtils.getUserEntity(logonMap);

            Map<String, Object> doctorMap = TypeUtil.getMap(logonMap.get(APIKey.COMMON_DOCTOR));
            if (null != doctorMap) {
                DoctorEntity doctorEntity = EntityUtils.getDoctorEntity(doctorMap);
                if (null != doctorEntity) {
                    String psw = "123456";
                    if (!TextUtils.isEmpty(psw)) {
                        String pswMD5 = MD5UUtil.generate(psw);
                        doctorEntity.setPswMD5(pswMD5);
                    }
                    doctorEntity.setUser(user);
                    GsApplication.getInstance(getContext()).setMyself(doctorEntity);
                }
            }
        }
    }

}
