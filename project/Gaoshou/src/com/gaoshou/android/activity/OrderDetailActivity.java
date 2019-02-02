package com.gaoshou.android.activity;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.gaoshou.android.R;
import com.gaoshou.android.entity.ConsultationEntity;
import com.gaoshou.android.entity.ConsultationFileEntity;
import com.gaoshou.common.base.BaseActivity;
import com.gaoshou.common.base.GsApplication;
import com.gaoshou.common.constant.APIKey;
import com.gaoshou.common.constant.CommonConstant;
import com.gaoshou.common.constant.OrderStatus;
import com.gaoshou.common.constant.ServiceAPIConstant;
import com.gaoshou.common.network.CommonRequest;
import com.gaoshou.common.utils.OpenFileUtil;
import com.gaoshou.common.utils.Validator;

/**
 * 专家端的接单详情界面
 * 
 * @author CTH
 *
 */
public class OrderDetailActivity extends BaseActivity implements OnClickListener {

    public static final String KEY_IS_MY_CONSULTATION = "KEY_IS_MY_CONSULTATION";

    private boolean isMyConsultation = false;

    private ConsultationEntity consultationEntity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_order_detail);

        init();
    }

    private void init() {
        initData();
        initToolbar("会诊资料");
        if (null != consultationEntity) {
            initUI();
        }
    }

    private void initData() {
        consultationEntity = GsApplication.getInstance(getContext()).getExpertAcceptConsultation();
        isMyConsultation = getIntent().getBooleanExtra(KEY_IS_MY_CONSULTATION, false);
    }

    private void initUI() {
        Button refuseBtn = findView(R.id.btn_refuse_order);
        if (isMyConsultation) {
            refuseBtn.setVisibility(View.VISIBLE);
        } else {
            refuseBtn.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(consultationEntity.getPatient_dept())) {
            setTextView(R.id.tv_dept, consultationEntity.getPatient_dept());
        } else {
            findView(R.id.ll_dept).setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(consultationEntity.getPatient_hospital())) {
            setTextView(R.id.tv_hospital, consultationEntity.getPatient_hospital());
        } else {
            findView(R.id.ll_hospital).setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(consultationEntity.getPatient_city())) {
            setTextView(R.id.tv_city, consultationEntity.getPatient_city());
        } else {
            findView(R.id.ll_city).setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(consultationEntity.getPatient_address())) {
            setTextView(R.id.tv_address, consultationEntity.getPatient_address());
        } else {
            findView(R.id.tv_address).setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(consultationEntity.getPatient_name())) {
            setTextView(R.id.tv_patient_name, consultationEntity.getPatient_name());
        } else {
            findView(R.id.ll_patient_name).setVisibility(View.GONE);
        }

        switch (consultationEntity.getPatient_sex()) {
            case APIKey.SEX_FEMALE:
                setTextView(R.id.tv_sex, "女");
                break;
            case APIKey.SEX_MALE:
                setTextView(R.id.tv_sex, "男");
                break;
            default:
                setTextView(R.id.tv_sex, "未知");
                break;
        }

        if (consultationEntity.getPatient_age() > 0) {
            setTextView(R.id.tv_age, consultationEntity.getPatient_age() + "");
        } else {
            findView(R.id.ll_age).setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(consultationEntity.getPatient_illness())) {
            setTextView(R.id.tv_illness, consultationEntity.getPatient_illness());
        } else {
            findView(R.id.ll_illness).setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(consultationEntity.getAnamnesis())) {
            setTextView(R.id.tv_anamnesis, consultationEntity.getAnamnesis());
        } else {
            findView(R.id.ll_anamnesis).setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(consultationEntity.getSymptom())) {
            setTextView(R.id.tv_symptom, consultationEntity.getSymptom());
        } else {
            findView(R.id.ll_symptom).setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(consultationEntity.getRemark())) {
            setTextView(R.id.tv_remark, consultationEntity.getRemark());
        } else {
            findView(R.id.ll_remark).setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(consultationEntity.getPurpose())) {
            setTextView(R.id.tv_purpose, consultationEntity.getPurpose());
        } else {
            findView(R.id.ll_purpose).setVisibility(View.GONE);
        }

        String orderAt = consultationEntity.getOrder_at();
        if (!TextUtils.isEmpty(orderAt)) {
            try {
                Date orderDate = CommonConstant.serverTimeFormat.parse(orderAt);
                orderAt = CommonConstant.serverTimeFormat.format(orderDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            setTextView(R.id.tv_order_at, orderAt);
        } else {
            findView(R.id.ll_order_at).setVisibility(View.GONE);
        }

        showConsultationImages();

        setViewClickListener(R.id.btn_accept_order, this);
        setViewClickListener(R.id.btn_refuse_order, this);
    }

    /**
     * 显示会诊资料图片
     */
    private void showConsultationImages() {
        if (null != consultationEntity) {
            List<ConsultationFileEntity> consultationFileEntities = consultationEntity.getConsultationFiles();
            Log.i("cth", "showConsultationImages : consultationFileEntities = " + consultationFileEntities);
            if (null != consultationFileEntities && consultationFileEntities.size() > 0) {
                for (int i = 0; i < consultationFileEntities.size(); i++) {
                    final ConsultationFileEntity consultationFileEntity = consultationFileEntities.get(i);
                    if (null != consultationFileEntity) {
                        int type = consultationFileEntity.getType();
                        switch (type) {
                            case APIKey.CONSULTATION_FILE_TYPE_BODY_SIGN:
                                setImageView(R.id.imv_body_sign, consultationFileEntity.getPath(), R.drawable.photo);
                                setViewClickListener(R.id.imv_body_sign, new OnClickListener() {

                                    @Override
                                    public void onClick(View v) {
                                        OpenFileUtil.openFile(getContext(), consultationFileEntity.getPath());
                                    }
                                });
                                break;
                            case APIKey.CONSULTATION_FILE_TYPE_ASSAY:
                                setImageView(R.id.imv_assay, consultationFileEntity.getPath(), R.drawable.photo);
                                setViewClickListener(R.id.imv_assay, new OnClickListener() {

                                    @Override
                                    public void onClick(View v) {
                                        OpenFileUtil.openFile(getContext(), consultationFileEntity.getPath());
                                    }
                                });
                                break;
                            case APIKey.CONSULTATION_FILE_TYPE_SCREENAGE:
                                setImageView(R.id.imv_screenage, consultationFileEntity.getPath(), R.drawable.photo);
                                setViewClickListener(R.id.imv_screenage, new OnClickListener() {

                                    @Override
                                    public void onClick(View v) {
                                        OpenFileUtil.openFile(getContext(), consultationFileEntity.getPath());
                                    }
                                });

                                break;

                            default:
                                break;
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_accept_order:
                if (null != consultationEntity) {
                    int consultationId = consultationEntity.getId();
                    boolean isHasReceive = GsApplication.getInstance().isConsultationHasReceive(consultationId);
                    if (isHasReceive) {
                        showToast("该单您已接过");
                    } else {
                        createOrder();
                    }
                }
                break;
            case R.id.btn_refuse_order:
                if (null != consultationEntity) {
                    int consultationId = consultationEntity.getId();
                    boolean isHasReceive = GsApplication.getInstance().isConsultationHasReceive(consultationId);
                    if (isHasReceive) {
                        showToast("该单您已接过");
                    } else {
                        refuseOrder();
                    }
                }
                break;
            default:
                break;
        }
    }

    private void updateConsultationStatus(int consultationId) {
        CommonRequest updateConsultationStatus = new CommonRequest();
        updateConsultationStatus.setRequestApiName(ServiceAPIConstant.REQUEST_API_NAME_CONSULTATION_UPDATE);
        updateConsultationStatus.setRequestID(ServiceAPIConstant.REQUEST_ID_CONSULTATION_UPDATE);
        updateConsultationStatus.addRequestParam(APIKey.COMMON_ID, consultationId);
        updateConsultationStatus.addRequestParam(APIKey.COMMON_STATUS, 2);

        addRequestAsyncTask(updateConsultationStatus);
    }

    private void createOrder() {
        if (null != consultationEntity) {
            int userId = GsApplication.getInstance().getUserId();
            if (Validator.isIdValid(userId)) {
                CommonRequest createOrderRequest = new CommonRequest();
                createOrderRequest.setRequestApiName(ServiceAPIConstant.REQUEST_API_NAME_ORDER_CREATE);
                createOrderRequest.setRequestID(ServiceAPIConstant.REQUEST_ID_ORDER_CREATE);
                createOrderRequest.addRequestParam(APIKey.COMMON_DOCTOR_ID, consultationEntity.getDoctor_id());
                createOrderRequest.addRequestParam(APIKey.ORDER_ORDER_DOCTOR_ID, userId);
                createOrderRequest.addRequestParam(APIKey.COMMON_CONSULTATION_ID, consultationEntity.getId());
                createOrderRequest.addRequestParam(APIKey.COMMON_TYPE, consultationEntity.getType());
                createOrderRequest.addRequestParam(APIKey.COMMON_STATUS, 1);

                addRequestAsyncTask(createOrderRequest);

                showProgressDialog("订单创建中");
            }
        }
    }

    private void refuseOrder() {
        if (null != consultationEntity) {

            CommonRequest refuseOrderRequest = new CommonRequest();
            refuseOrderRequest.setRequestApiName(ServiceAPIConstant.REQUEST_API_NAME_CONSULTATION_UPDATE);
            refuseOrderRequest.setRequestID(ServiceAPIConstant.REQUEST_ID_CONSULTATION_UPDATE);
            refuseOrderRequest.addRequestParam(APIKey.COMMON_ID, consultationEntity.getId());
            switch (consultationEntity.getTimely()) {
                case APIKey.CONSULTATION_TIMELY_NO: //非实时直接关闭咨询
                    refuseOrderRequest.addRequestParam(APIKey.COMMON_STATUS, OrderStatus.CONSULTATION_STATUS_FINISH);
                    break;
                case APIKey.CONSULTATION_TIMELY_YES://实时则取消指定专家与咨询的关联
                    refuseOrderRequest.addRequestParam(APIKey.COMMON_EXPERT_ID, -1);
                    break;

                default:
                    break;
            }

            addRequestAsyncTask(refuseOrderRequest);

            showProgressDialog("请稍等");
        }
    }

    @Override
    protected void onResponse(String requestID, boolean isSuccess, int statusCode, String message, Map<String, Object> data, Map<String, Object> additionalArgsmap) {
        super.onResponse(requestID, isSuccess, statusCode, message, data, additionalArgsmap);
        if (ServiceAPIConstant.REQUEST_ID_ORDER_CREATE.equals(requestID)) {
            if (isSuccess) {
                showToast("接单成功");

                if (null != consultationEntity) {
                    Intent dealConsultation = new Intent();
                    dealConsultation.setAction(ExpertAcceptOrderActivity.ACTION_DEAL_MY_CONSULTATION_LIST);
                    Log.i("cth", "onResponse: consultationEntity.getId() = " + consultationEntity.getId());
                    dealConsultation.putExtra(APIKey.COMMON_ID, consultationEntity.getId());

                    sendBroadcast(dealConsultation);

                    GsApplication.getInstance().setConsultationHasReceive(consultationEntity.getId(), true);

                    //                    updateConsultationStatus(consultationEntity.getId());
                }

                dimissProgressDialog();

                finish();
            } else {
                dimissProgressDialog();
                showToast(message);
            }
        } else if (ServiceAPIConstant.REQUEST_ID_CONSULTATION_UPDATE.equals(requestID)) {
            if (isSuccess) {
                showToast("成功拒绝");

                if (null != consultationEntity) {
                    Intent dealConsultation = new Intent();
                    dealConsultation.setAction(ExpertAcceptOrderActivity.ACTION_DEAL_MY_CONSULTATION_LIST);
                    Log.i("cth", "onResponse: consultationEntity.getId() = " + consultationEntity.getId());
                    dealConsultation.putExtra(APIKey.COMMON_ID, consultationEntity.getId());

                    sendBroadcast(dealConsultation);
                    
                    GsApplication.getInstance().setConsultationHasReceive(consultationEntity.getId(), false);

                    //                    updateConsultationStatus(consultationEntity.getId());
                }

                dimissProgressDialog();

                finish();
            } else {
                dimissProgressDialog();
                showToast(message);
            }
        }
    }
}
