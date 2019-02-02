package com.gaoshou.android.activity;

import java.util.Map;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import com.gaoshou.android.R;
import com.gaoshou.android.entity.ConsultationEntity;
import com.gaoshou.android.fragment.ExpertAcceptOrderFragment;
import com.gaoshou.common.base.BaseActivity;
import com.gaoshou.common.constant.APIKey;
import com.gaoshou.common.constant.ServiceAPIConstant;
import com.gaoshou.common.network.CommonRequest;
import com.gaoshou.common.utils.EntityUtils;
import com.gaoshou.common.utils.Validator;

public class ExpertAcceptOrderActivity extends BaseActivity {

    public static final String ACTION_UPDATE_MY_CONSULTATION_LIST = "ACTION_UPDATE_MY_CONSULTATION_LIST";
    public static final String ACTION_DEAL_MY_CONSULTATION_LIST = "ACTION_DEAL_MY_CONSULTATION_LIST";
    public static final String ACTION_UPDATE_CONSULTATION_LIST = "ACTION_UPDATE_CONSULTATION_LIST";

    private static final int UPDATE_DELAY = 1000 * 60;

    private ExpertAcceptOrderFragment expertAcceptOrderFragment;

    private UpdateConsultationReceiver updateConsultationReceiver;
    private Handler updateConsultationListHandler;
    private Runnable updateCOnsultationListRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_expert_accept_order);

        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_UPDATE_MY_CONSULTATION_LIST);
        filter.addAction(ACTION_DEAL_MY_CONSULTATION_LIST);
        filter.addAction(ACTION_UPDATE_CONSULTATION_LIST);

        updateConsultationReceiver = new UpdateConsultationReceiver();

        registerReceiver(updateConsultationReceiver, filter);

        initUI();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (updateConsultationListHandler == null) {
            if (updateCOnsultationListRunnable == null) {
                updateCOnsultationListRunnable = new Runnable() {

                    @Override
                    public void run() {
                        if (null != expertAcceptOrderFragment) {
                            expertAcceptOrderFragment.updateConsultationList();

                            updateConsultationListHandler.postDelayed(this, UPDATE_DELAY);
                        }
                    }
                };
            }
            updateConsultationListHandler = new Handler();
            updateConsultationListHandler.postDelayed(updateCOnsultationListRunnable, UPDATE_DELAY);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unregisterReceiver(updateConsultationReceiver);
        if (null != updateConsultationListHandler) {
            updateConsultationListHandler.removeCallbacks(updateCOnsultationListRunnable);
            updateConsultationListHandler = null;
            updateCOnsultationListRunnable = null;
        }
    }

    private void initUI() {
        initToolbar("高手请接招");

        expertAcceptOrderFragment = new ExpertAcceptOrderFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        fragmentTransaction.replace(R.id.fragment_container, expertAcceptOrderFragment);
        fragmentTransaction.commit();
    }

    private void fetchConsultation(int consultationId) {
        CommonRequest fetchConsultationRequest = new CommonRequest();
        fetchConsultationRequest.setRequestApiName(ServiceAPIConstant.REQUEST_API_NAME_CONSULTATION_VIEW);
        fetchConsultationRequest.setRequestID(ServiceAPIConstant.REQUEST_ID_CONSULTATION_VIEW);
        fetchConsultationRequest.addRequestParam(APIKey.COMMON_ID, consultationId);
        //        fetchConsultationRequest.addRequestParam(APIKey.CONSULTATION_SEARCH_STATUS, APIKey.COMMON_STATUS_LEGAL);

        addRequestAsyncTask(fetchConsultationRequest);
    }

    @Override
    protected void onResponse(String requestID, boolean isSuccess, int statusCode, String message, Map<String, Object> data, Map<String, Object> additionalArgsmap) {
        super.onResponse(requestID, isSuccess, statusCode, message, data, additionalArgsmap);
        if (ServiceAPIConstant.REQUEST_ID_CONSULTATION_VIEW.equals(requestID)) {
            if (isSuccess && data != null) {
                ConsultationEntity consultationEntity = EntityUtils.getConsultationEntity(data);
                if (null != consultationEntity) {
                    if (null != expertAcceptOrderFragment) {
                        expertAcceptOrderFragment.updateMyConsulationList(consultationEntity);
                    }
                }
            }
        }
    }

    class UpdateConsultationReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.i("cth", "UpdateConsultationReceiver : onReceive = " + action);
            if (ACTION_UPDATE_MY_CONSULTATION_LIST.equals(action)) {
                int consultationId = intent.getIntExtra(APIKey.COMMON_ID, -1);
                Log.i("cth", "UpdateConsultationReceiver : consultationId = " + consultationId);
                if (consultationId != -1) {
                    fetchConsultation(consultationId);
                }
            } else if (ACTION_DEAL_MY_CONSULTATION_LIST.equals(action)) {
                int consultationId = intent.getIntExtra(APIKey.COMMON_ID, -1);
                Log.i("cth", "UpdateConsultationReceiver:ACTION_DEAL_MY_CONSULTATION_LIST:consultationId = " + consultationId);
                if (Validator.isIdValid(consultationId) && null != expertAcceptOrderFragment) {
                    Log.i("cth", "IdValid");
                    expertAcceptOrderFragment.dealMyConsultation(consultationId);
                }
            }
        }

    }
}
