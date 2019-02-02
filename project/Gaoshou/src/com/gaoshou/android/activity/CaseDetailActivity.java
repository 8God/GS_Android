package com.gaoshou.android.activity;

import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RatingBar;
import android.widget.ScrollView;

import com.gaoshou.android.R;
import com.gaoshou.android.entity.AnamnesisEntity;
import com.gaoshou.android.entity.ConsultationEntity;
import com.gaoshou.android.entity.ConsultationFileEntity;
import com.gaoshou.android.entity.DoctorEntity;
import com.gaoshou.android.entity.DoctorFileEntity;
import com.gaoshou.android.entity.EvaluateEntity;
import com.gaoshou.android.entity.OrderEntity;
import com.gaoshou.android.entity.RepineEntity;
import com.gaoshou.android.entity.SymptomEntity;
import com.gaoshou.common.base.BaseActivity;
import com.gaoshou.common.base.GsApplication;
import com.gaoshou.common.constant.APIKey;
import com.gaoshou.common.constant.ServiceAPIConstant;
import com.gaoshou.common.network.CommonRequest;
import com.gaoshou.common.utils.EntityUtils;
import com.gaoshou.common.utils.NetworkUtil;
import com.gaoshou.common.utils.OpenFileUtil;
import com.gaoshou.common.utils.Validator;
import com.gaoshou.common.utils.WindowUtils;
import com.gaoshou.common.widget.ImagePickerView;

public class CaseDetailActivity extends BaseActivity implements OnClickListener {
    private final int UNFINISHED_DOCTOR = 0;
    private final int UNFINISHED_EXPERT = 1;
    private final int FINISHED_DOCTOR = 2;
    private final int FINISHED_EXPERT = 3;
    private final int[][] STATUS = { { UNFINISHED_DOCTOR, FINISHED_DOCTOR }, { UNFINISHED_EXPERT, FINISHED_EXPERT } };

    private int currentStatus = -1;
    private int location = -1;
    private int orderId = -1;
    private OrderEntity order;

    private String signPath;
    private String assayPath;
    private String imagePath;

    private RatingBar score;
    private RadioGroup appraise;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_case_detail);

        initToolbar(getString(R.string.actionbar_title_case_detail));

        init();
    }

    private void init() {
        initIntent();
        order = GsApplication.getInstance(getContext()).getOrder();
        if (order != null) {
            orderId = order.getId();
            fetchConsultation();
        }
    }

    private void initIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            location = intent.getIntExtra(getString(R.string.intent_case_detail_location), -1);
        }
    }

    private void initView() {
        if (order != null) {
            ConsultationEntity consultation = order.getConsultation();
            setTextView(R.id.tv_order_number, String.valueOf(order.getId()));
            if (consultation != null) {
                setTextView(R.id.tv_order_dept, consultation.getPatient_dept());
                setTextView(R.id.tv_order_hospital, consultation.getPatient_hospital());
                setTextView(R.id.tv_order_city, consultation.getPatient_city());
                setTextView(R.id.tv_order_address, consultation.getPatient_address());
                setTextView(R.id.tv_order_name, consultation.getPatient_name());
                setTextView(R.id.tv_order_sex, (consultation.getPatient_sex() == APIKey.SEX_MALE ? "男" : "女"));
                setTextView(R.id.tv_order_age, String.valueOf(consultation.getPatient_age()));
                setTextView(R.id.tv_order_illness, String.valueOf(consultation.getPatient_illness()));

                //                AnamnesisEntity anamnesis = consultation.getAnamnesis();
                //                SymptomEntity symptom = consultation.getSymptom();
                //                if (anamnesis != null)
                //                    setTextView(R.id.tv_order_anamnesis, anamnesis.getName());
                //                if (symptom != null)
                //                    setTextView(R.id.tv_order_symptom, symptom.getName());
                setTextView(R.id.tv_order_anamnesis, consultation.getAnamnesis());
                setTextView(R.id.tv_order_symptom, consultation.getSymptom());

                String reservation_date = consultation.getOrder_at();
                if (reservation_date != null)
                    setTextView(R.id.tv_date, reservation_date);
                else {
                    setTextView(R.id.tv_date, consultation.getCreated_at());
                }

                setTextView(R.id.tv_remark, consultation.getRemark());
                setTextView(R.id.tv_purpose, consultation.getPurpose());

                setConsultationPhoto(consultation);

                score = findView(R.id.rtb_score);
                
                appraise = findView(R.id.rdg_expert_appraise);
                appraise.setOnCheckedChangeListener(new OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        switch (checkedId) {
                            case R.id.rdb_perfect:
                                if(score!=null){
                                    score.setRating(5);
                                }
                                break;
                            case R.id.rdb_satify:
                                if(score!=null){
                                    score.setRating(4);
                                }
                                break;
                            case R.id.rdb_normarl:
                                if(score!=null){
                                    score.setRating(2);
                                }
                                break;
                            default:
                                if(score!=null){
                                    score.setRating(5);
                                }
                                break;
                        }
                    }
                });
                
                //分状态显示UI
                int userType = GsApplication.getInstance(getContext()).getOrderDetailUserType();
                if (userType == -1) {
                    userType = GsApplication.getInstance(getContext()).getLogonType();
                }
                int orderType = 0;

                switch (order.getStatus()) {
                    case OrderEntity.STATUS_CANCEL:
                    case OrderEntity.STATUS_PRE_PAYMENT_CANCEL:
                    case OrderEntity.STATUS_COMPLETE:
                        orderType = 1;
                        break;
                    default:
                        orderType = 0;
                        break;
                }

                Log.i("testYJ", "userType=" + userType);
                Log.i("testYJ", "orderType=" + orderType);
                currentStatus = STATUS[userType][orderType];

                switch (currentStatus) {
                    case UNFINISHED_DOCTOR:
                        findView(R.id.ll_finished_container).setVisibility(View.GONE);
                        findView(R.id.ll_unfinished_ordered_container).setVisibility(View.GONE);
                        showUI();
                        break;
                    case UNFINISHED_EXPERT:
                        findView(R.id.ll_finished_container).setVisibility(View.GONE);
                        showUI();
                        break;
                    case FINISHED_DOCTOR:
                        findView(R.id.ll_unfinished_container).setVisibility(View.GONE);
                        findView(R.id.ll_finished_ordered_container).setVisibility(View.GONE);
                        findView(R.id.ll_finished_order_remark_container).setVisibility(View.VISIBLE);
                        ;
                        setTextView(R.id.tv_finished_order_expert_remark, order.getRemark());
                        fethEvaluate();
                        findView(R.id.btn_send).setOnClickListener(this);
                        findView(R.id.btn_repine).setOnClickListener(this);
                        break;
                    case FINISHED_EXPERT:
                        findView(R.id.ll_unfinished_container).setVisibility(View.GONE);
                        findView(R.id.ll_finished_order_container).setVisibility(View.GONE);
                        setTextView(R.id.tv_finished_ordered_expert_remark, order.getRemark());
                        fethEvaluate();
                        break;
                }
            }

        }

    }

    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
    }

    //设置咨询图片
    private void setConsultationPhoto(ConsultationEntity consultation) {
        if (consultation != null) {
            List<ConsultationFileEntity> consultationFiles = consultation.getConsultationFiles();
            if (consultationFiles != null && consultationFiles.size() > 0) {
                //显示图片
                for (ConsultationFileEntity temp : consultationFiles) {
                    if (temp != null) {
                        int type = temp.getType();
                        switch (type) {
                            case ConsultationFileEntity.SIGN:
                                signPath = temp.getPath();
                                setImageView(R.id.iv_sign, signPath);
                                findView(R.id.iv_sign).setOnClickListener(this);
                                break;
                            case ConsultationFileEntity.ASSAY:
                                assayPath = temp.getPath();
                                setImageView(R.id.iv_assay, assayPath);
                                findView(R.id.iv_assay).setOnClickListener(this);
                                break;
                            case ConsultationFileEntity.IAMGE:
                                imagePath = temp.getPath();
                                setImageView(R.id.iv_image, imagePath);
                                findView(R.id.iv_image).setOnClickListener(this);
                                break;
                        }
                    }
                }
            }
        }
    }

    private void fetchConsultation() {
        if (NetworkUtil.isNetworkAvaliable(getContext())) {
            CommonRequest fetchConsultationRequest = new CommonRequest();
            fetchConsultationRequest.setRequestApiName(ServiceAPIConstant.REQUEST_API_NAME_CONSULTATION_INDEX + "&" + APIKey.COMMON_EXPAND + "=" + APIKey.COMMON_CONSULTATION_FILES);
            fetchConsultationRequest.setRequestID(ServiceAPIConstant.REQUEST_ID_CONSULTATION_INDEX);
            if (orderId != -1) {
                fetchConsultationRequest.addRequestParam(APIKey.ORDER_WHERE_ID, orderId);
            }

            fetchConsultationRequest.addRequestParam(APIKey.COMMON_JOIN + "[]", APIKey.COMMON_ORDERS);

            addRequestAsyncTask(fetchConsultationRequest);
        } else {
            showToast(getString(R.string.network_error));
            findView(R.id.rl_loading_container).setVisibility(View.GONE);
            findView(R.id.tv_no_record).setVisibility(View.VISIBLE);
        }

    }

    private void fethEvaluate() {
        if (NetworkUtil.isNetworkAvaliable(getContext())) {
            CommonRequest fetchEvaluateRequest = new CommonRequest();
            fetchEvaluateRequest.setRequestApiName(ServiceAPIConstant.REQUEST_API_NAME_EVALUATE_INDEX);
            fetchEvaluateRequest.setRequestID(ServiceAPIConstant.REQUEST_ID_EVALUATE_INDEX);
            if (orderId != -1)
                fetchEvaluateRequest.addRequestParam(APIKey.COMMON_WHERE_ORDER_ID, orderId);

            addRequestAsyncTask(fetchEvaluateRequest);
        } else {
            showToast(getString(R.string.network_error));
            findView(R.id.ll_doctor_repine_container).setVisibility(View.GONE);
        }
    }

    private void fetchRepine() {
        if (NetworkUtil.isNetworkAvaliable(getContext())) {
            CommonRequest fetchRepineRequest = new CommonRequest();
            fetchRepineRequest.setRequestApiName(ServiceAPIConstant.REQUEST_API_NAME_REPINE_INDEX);
            fetchRepineRequest.setRequestID(ServiceAPIConstant.REQUEST_ID_REPINE_INDEX);
            if (orderId != -1)
                fetchRepineRequest.addRequestParam(APIKey.COMMON_WHERE_ORDER_ID, orderId);

            addRequestAsyncTask(fetchRepineRequest);
        } else {
            showToast(getString(R.string.network_error));
            findView(R.id.ll_doctor_repine_container).setVisibility(View.GONE);
        }
    }

    private void createRepine() {
        if (NetworkUtil.isNetworkAvaliable(getContext())) {
            CommonRequest createRepineRequest = new CommonRequest();
            createRepineRequest.setRequestApiName(ServiceAPIConstant.REQUEST_API_NAME_REPINE_CREATE);
            createRepineRequest.setRequestID(ServiceAPIConstant.REQUEST_ID_REPINE_CREATE);
            createRepineRequest.setRequestMethod(CommonRequest.REQUEST_METHOD_POST);
            createRepineRequest.addRequestParam(APIKey.COMMON_ORDER_ID, order.getId());
            createRepineRequest.addRequestParam(APIKey.COMMON_DOCTOR_ID, order.getDoctor_id());
            createRepineRequest.addRequestParam(APIKey.REPINE_REPINED_DOCTOR_ID, order.getOrder_doctor_id());
            createRepineRequest.addRequestParam(APIKey.COMMON_CONTENT, getEditTextInput(R.id.edt_repine, ""));

            addRequestAsyncTask(createRepineRequest);
        } else {
            showToast(getString(R.string.network_error));
        }
    }

    private void createEvaluate() {
        if (NetworkUtil.isNetworkAvaliable(getContext())) {
            CommonRequest createEvaluateRequest = new CommonRequest();
            createEvaluateRequest.setRequestApiName(ServiceAPIConstant.REQUEST_API_NAME_EVALUATE_CREATE);
            createEvaluateRequest.setRequestID(ServiceAPIConstant.REQUEST_ID_EVALUATE_CREATE);
            createEvaluateRequest.setRequestMethod(CommonRequest.REQUEST_METHOD_POST);
            createEvaluateRequest.addRequestParam(APIKey.COMMON_ORDER_ID, order.getId());
            createEvaluateRequest.addRequestParam(APIKey.COMMON_DOCTOR_ID, order.getDoctor_id());
            createEvaluateRequest.addRequestParam(APIKey.EVALUATE_EVALUATED_DOCTOR_ID, order.getOrder_doctor_id());
            createEvaluateRequest.addRequestParam(APIKey.EVALUATE_SCORE, (int) score.getRating());
            createEvaluateRequest.addRequestParam(APIKey.COMMON_CONTENT, getEditTextInput(R.id.edt_appraise, ""));

            addRequestAsyncTask(createEvaluateRequest);
        } else {
            showToast(getString(R.string.network_error));
        }
    }

    @Override
    protected void onResponse(String requestID, boolean isSuccess, int statusCode, String message, List<Map<String, Object>> items, Map<String, Object> links, Map<String, Object> meta, Map<String, Object> additionalArgsmap) {
        if (requestID == ServiceAPIConstant.REQUEST_ID_CONSULTATION_INDEX) {
            if (isSuccess) {
                Log.i("testYJ", "isSuccess");
                List<ConsultationEntity> consultations = EntityUtils.getConsultationEntityList(items);
                if (consultations != null && consultations.size() > 0) {
                    ConsultationEntity consultation = consultations.get(0);
                    Log.i("testYJ", "consultation=" + consultation);
                    if (order != null && consultation != null) {
                        order.setConsultation(consultations.get(0));
                        initView();
                    }
                } else {
                    findView(R.id.rl_loading_container).setVisibility(View.GONE);
                    findView(R.id.tv_no_record).setVisibility(View.VISIBLE);
                }
            }
        } else if (requestID == ServiceAPIConstant.REQUEST_ID_EVALUATE_INDEX) {
            if (isSuccess && statusCode == 200) {
                List<EvaluateEntity> evaluates = EntityUtils.getEvaluateEntityList(items);
                Log.i("testYJ", "evaluate=" + evaluates);
                if (evaluates != null && evaluates.size() > 0) {
                    EvaluateEntity evaluate = evaluates.get(0);
                    if (evaluate != null) {
                        Log.i("testYJ", "evaluate.getContent=" + evaluate.getContent());
                        setTextView(R.id.tv_doctor_evaluate, evaluate.getContent());

                        int score_value = evaluate.getScore();
                        score = findView(R.id.rtb_score);
                        score.setRating(score_value);
                        score.setIsIndicator(true);
                        
                        if(appraise!=null){
                            appraise.setOnCheckedChangeListener(null);
                        }
                        RadioButton perfect = findView(R.id.rdb_perfect);
                        RadioButton satify = findView(R.id.rdb_satify);
                        RadioButton normal = findView(R.id.rdb_normarl);
                        switch (score_value) {
                            case 5:
                                perfect.setChecked(true);
                                break;
                            case 4:
                            case 3:
                                satify.setChecked(true);
                                break;
                            case 2:
                            case 1:
                                normal.setChecked(true);
                                break;
                        }
                        perfect.setClickable(false);
                        satify.setClickable(false);
                        normal.setClickable(false);

                        ((RadioGroup) findView(R.id.rdg_expert_appraise)).setClickable(false);

                        setEditText(R.id.edt_appraise, evaluate.getContent());
                        findView(R.id.edt_appraise).setFocusable(false);

                        findView(R.id.btn_send).setVisibility(View.GONE);
                    }
                }
            } else {
                showToast(message);
                findView(R.id.ll_doctor_repine_container).setVisibility(View.GONE);
            }

            fetchRepine();
        } else if (requestID == ServiceAPIConstant.REQUEST_ID_REPINE_INDEX) {
            if (isSuccess && statusCode == 200) {
                List<RepineEntity> repines = EntityUtils.getRepineEntityList(items);
                if (repines != null && repines.size() > 0) {
                    RepineEntity repine = repines.get(0);
                    if (repine != null) {
                        setTextView(R.id.tv_doctor_repine, repine.getContent());

                        setEditText(R.id.edt_repine, repine.getContent());
                        findView(R.id.edt_repine).setFocusable(false);

                        findView(R.id.btn_repine).setVisibility(View.GONE);
                    }
                }
            } else {
                showToast(message);
                findView(R.id.ll_doctor_repine_container).setVisibility(View.GONE);
            }

            showUI();
        }
    }

    @Override
    protected void onResponse(String requestID, boolean isSuccess, int statusCode, String message, Map<String, Object> data, Map<String, Object> additionalArgsmap) {
        if (requestID == ServiceAPIConstant.REQUEST_ID_EVALUATE_CREATE) {
            if (isSuccess && statusCode == 201) {
                EvaluateEntity evaluate = EntityUtils.getEvaluateEntity(data);
                if (evaluate != null && evaluate.getStatus() == 1) {
                    ((RadioGroup) findView(R.id.rdg_expert_appraise)).setClickable(false);
                    findView(R.id.edt_appraise).setFocusable(false);
                    findView(R.id.btn_send).setVisibility(View.GONE);
                    showToast(getString(R.string.success_to_create_evaluate));
                } else {
                    showToast(getString(R.string.faild_to_create_evaluate));
                }
            } else {
                showToast(message);
            }
        } else if (requestID == ServiceAPIConstant.REQUEST_ID_REPINE_CREATE) {
            if (isSuccess && statusCode == 201) {
                RepineEntity repine = EntityUtils.getRepineEntity(data);
                if (repine != null && repine.getStatus() == 1) {
                    findView(R.id.edt_repine).setFocusable(false);
                    findView(R.id.btn_repine).setVisibility(View.GONE);
                    showToast(getString(R.string.success_to_create_repine));
                } else {
                    showToast(getString(R.string.faild_to_create_repine));
                }
            } else {
                showToast(message);
            }
        }
    }

    private void slideToBottom() {
        LinearLayout first_part_container = findView(R.id.ll_first_part_container);
        final ScrollView scrollView = findView(R.id.sv_container);
        final int height = WindowUtils.getMeasureHeightOfView(first_part_container);
        Log.i("testYJ", "height---->" + height);
        Log.i("testYJ", "location---->" + location);
        switch (location) {
            case MyCasesActivity.LOCATION_CANCEL:
            case MyCasesActivity.LOCATION_EVALUATE:
            case MyCasesActivity.LOCATION_REPINED:
                if (scrollView != null) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Log.i("testYJ", "scroll");
                            scrollView.scrollTo(0, height + WindowUtils.getActionBarHeight(getContext()));
                        }
                    }, 100);
                }
                break;
            case -1:
                break;
        }
    }

    private void showUI() {
        findView(R.id.rl_loading_container).setVisibility(View.GONE);
        findView(R.id.sv_container).setVisibility(View.VISIBLE);
        slideToBottom();
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        switch (viewId) {
            case R.id.btn_send:
                if (!Validator.isEmpty(getEditTextInput(R.id.edt_appraise, "")))
                    createEvaluate();
                else {
                    showToast(getString(R.string.appraise_can_not_be_empty));
                }
                break;
            case R.id.btn_repine:
                if (!Validator.isEmpty(getEditTextInput(R.id.edt_repine, "")))
                    createRepine();
                else {
                    showToast(getString(R.string.repine_can_not_be_empty));
                }
                break;
            case R.id.iv_sign:
                if (signPath != null)
                    OpenFileUtil.openFile(getContext(), signPath);
                break;
            case R.id.iv_assay:
                if (assayPath != null)
                    OpenFileUtil.openFile(getContext(), assayPath);
                break;
            case R.id.iv_image:
                if (imagePath != null)
                    OpenFileUtil.openFile(getContext(), imagePath);
                break;
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
    protected void onDestroy() {
        GsApplication.getInstance(getContext()).setOrder(null);
        GsApplication.getInstance(getContext()).setOrderDetailUserType(-1);
        super.onDestroy();
    }

}
