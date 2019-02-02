package com.gaoshou.android.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RatingBar;

import com.gaoshou.android.R;
import com.gaoshou.android.entity.ConsultationEntity;
import com.gaoshou.android.entity.ConsultationFileEntity;
import com.gaoshou.android.entity.DoctorEntity;
import com.gaoshou.common.base.BaseActivity;
import com.gaoshou.common.base.GsApplication;
import com.gaoshou.common.constant.APIKey;
import com.gaoshou.common.constant.ServiceAPIConstant;
import com.gaoshou.common.network.CommonRequest;
import com.gaoshou.common.utils.EntityUtils;
import com.gaoshou.common.utils.NetworkUtil;
import com.gaoshou.common.widget.ImagePickerView;
import com.gaoshou.common.widget.ImagePickerView.OnImageChangedListener;

/**
 * 年龄和是否及时这两个参数无法正确获取
 */
public class ConsultationDetailActivity extends BaseActivity implements OnClickListener, OnCheckedChangeListener, OnImageChangedListener {
    public static final int CONSULTATION_DETAIL_FRAGMENT = 0;
    public static final int SELECT_ANAMNESIS_ACTIVITY = 11;
    public static final int SELECT_SYMPTOM_ACTIVITY = 12;

    public static final int ORDER_TYPE_CONSULTATION = 0;
    public static final int ORDER_TYPE_CONSULTATION_ORPERATION = 1;

    private DoctorEntity doctor;
    private ConsultationEntity consultation;
    private ConsultationEntity consultationNew;
    private List<ConsultationFileEntity> consultationFileList;

    private RadioGroup sexGroup;
    private RadioGroup timelyGroup;
    private ImagePickerView sign;
    private ImagePickerView assay;
    private ImagePickerView image;
    private CheckBox other_order;

    private DatePickerDialog datePickerDialog;

    private int consultationType;
    private int selectImagePickerViewID = -1;
    private int anamnesisIndex = -1;
    private int symptomIndex = -1;

    private String signPath;
    private String assayPath;
    private String imagePath;
    private int consultationId;

    private String anamnesis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consultation_detail);
        init();
    }

    private void init() {
        initToolbar(getString(R.string.consultation_detail_title), true);

        initView();

        initDoctorDetail();

        getConsultationHistory();
    }

    private void initDoctorDetail() {
        doctor = GsApplication.getInstance(getContext()).getCurrentExpert();
        consultationType = GsApplication.getInstance(getContext()).getConsultationType();

        if (doctor != null) {
            //还差头像
            setImageView(R.id.iv_expert_icon, doctor.getHeadPicPath(), R.drawable.ic_launcher);
            setTextView(R.id.tv_name, doctor.getName());
            setTextView(R.id.tv_position, doctor.getTitle());

            ((RatingBar) findView(R.id.rtb_score)).setRating(Float.valueOf((doctor.getAvgScore())));

            if (consultationType == ConsultationDetailActivity.ORDER_TYPE_CONSULTATION) {
                findView(R.id.tv_operation).setVisibility(View.GONE);
            } else if (consultationType == ConsultationDetailActivity.ORDER_TYPE_CONSULTATION_ORPERATION) {
                findView(R.id.tv_consultation).setVisibility(View.GONE);
            }
        }
    }

    private void getConsultationHistory() {
        consultation = GsApplication.getInstance(getContext()).getConsultation();

        if (consultation != null) {
            consultationFileList = consultation.getConsultationFiles();
            Log.i("testYJ", "consultation=" + consultationFileList);
            //读取已修改的订单数据
            setTextView(R.id.edt_patient_dept, consultation.getPatient_dept());
            setTextView(R.id.edt_patient_hospital, consultation.getPatient_hospital());
            setTextView(R.id.edt_patient_city, consultation.getPatient_city());
            setTextView(R.id.edt_patient_address, consultation.getPatient_address());
            setEditText(R.id.edt_patient_name, consultation.getPatient_name());

            switch (consultation.getPatient_sex()) {
                case APIKey.SEX_MALE:
                    ((RadioButton) findView(R.id.rdb_male)).setChecked(true);
                    ((RadioButton) findView(R.id.rdb_female)).setChecked(false);
                    break;
                case APIKey.SEX_FEMALE:
                    ((RadioButton) findView(R.id.rdb_male)).setChecked(false);
                    ((RadioButton) findView(R.id.rdb_female)).setChecked(true);
                    break;
            }

            setEditText(R.id.edt_patient_age, String.valueOf(consultation.getPatient_age()));
            setEditText(R.id.edt_patient_mobile, consultation.getPatient_mobile());
            setEditText(R.id.edt_patient_illness, consultation.getPatient_illness());
            //            if (consultation.getAnamnesis() != null)
            //                setEditText(R.id.edt_patient_anamnesis, consultation.getAnamnesis().getName());
            //            if (consultation.getSymptom() != null)
            //                setEditText(R.id.edt_patient_symptom, consultation.getSymptom().getName());
            anamnesis = consultation.getAnamnesis();
            setEditText(R.id.edt_patient_anamnesis, anamnesis);
            setEditText(R.id.edt_patient_symptom, consultation.getSymptom());
            setEditText(R.id.edt_patient_remark, consultation.getRemark());

            //            if (consultationFileList != null && consultationFileList.size() > 0) {
            //                for (ConsultationFileEntity consultationFile : consultationFileList) {
            //                    if (consultationFile != null) {
            //                        switch (consultationFile.getType()) {
            //                            case ConsultationFileEntity.SIGN:
            //                                sign.setImageByPath(consultationFile.getPath());
            //                                break;
            //                            case ConsultationFileEntity.ASSAY:
            //                                assay.setImageByPath(consultationFile.getPath());
            //                                break;
            //                            case ConsultationFileEntity.IAMGE:
            //                                image.setImageByPath(consultationFile.getPath());
            //                                break;
            //                        }
            //                    }
            //                }
            //            }

            setEditText(R.id.edt_consultation_purpose, consultation.getPurpose());

            Log.i("testYJ", "timeLy=" + consultation.getTimely());
            switch (consultation.getTimely()) {
                case APIKey.CONSULTATION_TIMELY_YES:
                    ((RadioButton) findView(R.id.rdb_yes)).setChecked(true);
                    ((RadioButton) findView(R.id.rdb_no)).setChecked(false);
                    ((CheckBox) findView(R.id.cb_others_order_or_not)).setChecked(true);
                    ((LinearLayout) findView(R.id.ll_patient_reservation_date_container)).setVisibility(View.GONE);
                    ((LinearLayout) findView(R.id.ll_others_order_or_not_container)).setVisibility(View.VISIBLE);

                    break;
                case APIKey.CONSULTATION_TIMELY_NO:
                    ((RadioButton) findView(R.id.rdb_yes)).setChecked(false);
                    ((RadioButton) findView(R.id.rdb_no)).setChecked(true);
                    ((CheckBox) findView(R.id.cb_others_order_or_not)).setChecked(false);
                    ((LinearLayout) findView(R.id.ll_patient_reservation_date_container)).setVisibility(View.VISIBLE);
                    ((LinearLayout) findView(R.id.ll_others_order_or_not_container)).setVisibility(View.GONE);
                    break;
            }

            boolean isCheck = true;
            Log.i("testYJ", "other_order=" + consultation.getOther_order());
            switch (consultation.getOther_order()) {
                case APIKey.CONSULTATION_TIMELY_NO:
                    isCheck = false;
                    break;
                case APIKey.CONSULTATION_TIMELY_YES:
                    isCheck = true;
                    break;
            }
            other_order.setChecked(isCheck);

            setEditText(R.id.edt_patient_reservation_date, getOrderDateFromService(consultation.getOrder_at()));

            anamnesisIndex = consultation.getAnamnesis_id();
            symptomIndex = consultation.getSymptom_id();
        } else {
            consultation = new ConsultationEntity();

            DoctorEntity myself = GsApplication.getInstance(getContext()).getMyself();
            if (myself != null) {
                setEditText(R.id.edt_patient_dept, myself.getDept());
                setEditText(R.id.edt_patient_hospital, myself.getHospital());
                if (myself.getCity() != null) {
                    Log.i("testYJ", "city=" + myself.getCity().getName());
                    setEditText(R.id.edt_patient_city, myself.getCity().getName());
                }
                setEditText(R.id.edt_patient_address, myself.getAddress());
            }
            setEditText(R.id.edt_patient_reservation_date, getOrderDateFromService("2015-08-30"));
        }

    }

    private String getOrderDateFromLocate(String time) {
        StringBuffer orderTime = null;
        Log.i("testYJ", "time=" + time);
        if (time != null && !"".equals(time)) {
            orderTime = new StringBuffer();
            if (!time.contains("年")) {
                int firstLine = time.indexOf("-");
                int secondLine = time.indexOf("-", firstLine + 1);
                Log.i("testYJ", "year=" + time.substring(0, firstLine) + "年");
                Log.i("testYJ", "month=" + time.substring(firstLine + 1, secondLine) + "月");
                Log.i("testYJ", "day=" + time.substring(secondLine + 1) + "日" + "  ");
                orderTime.append(time.substring(0, firstLine) + "年").append(time.substring(firstLine + 1, secondLine) + "月").append(time.substring(secondLine + 1) + "日" + "  ");
            } else {
                orderTime.append(time.substring(0, time.indexOf("年"))).append("-").append(time.substring(time.indexOf("年") + 1, time.indexOf("月"))).append("-").append(time.substring(time.indexOf("月") + 1, time.indexOf("日")));
            }
            return orderTime.toString();
        }
        return null;
    }
    
    private String getOrderDateFromService(String time){
        StringBuffer orderTime = null;
        Log.i("testYJ", "time=" + time);
        if (time != null && !"".equals(time)) {
            orderTime = new StringBuffer();
            if (!time.contains("年")) {
                int firstLine = time.indexOf("-");
                int secondLine = time.indexOf("-", firstLine + 1);
                Log.i("testYJ", "year=" + time.substring(0, firstLine) + "年");
                Log.i("testYJ", "month=" + time.substring(firstLine + 1, secondLine) + "月");
                Log.i("testYJ", "day=" + time.substring(secondLine + 1) + "日" + "  ");
                if("0".equals(time.indexOf(firstLine+1))){
                    orderTime.append(time.substring(0, firstLine) + "年").append(time.substring(firstLine + 2, secondLine) + "月").append(time.substring(secondLine + 1) + "日" + "  ");
                }else{
                    orderTime.append(time.substring(0, firstLine) + "年").append(time.substring(firstLine + 1, secondLine) + "月").append(time.substring(secondLine + 1) + "日" + "  ");
                }
            } else {
                orderTime.append(time.substring(0, time.indexOf("年"))).append("-").append(time.substring(time.indexOf("年") + 1, time.indexOf("月"))).append("-").append(time.substring(time.indexOf("月") + 1, time.indexOf("日")));
            }
            return orderTime.toString();
        }
        return null;
    }

    private void initView() {
        sexGroup = findView(R.id.rdg_gender);
        timelyGroup = findView(R.id.rdg_immediate_or_not);
        sign = findView(R.id.ipv_sign);
        assay = findView(R.id.ipv_assay);
        image = findView(R.id.ipv_image);
        other_order = findView(R.id.cb_others_order_or_not);

        findView(R.id.edt_patient_reservation_date).setOnClickListener(this);
        findView(R.id.btn_upload_data).setOnClickListener(this);
        findView(R.id.edt_patient_anamnesis).setOnClickListener(this);
        //        findView(R.id.edt_patient_symptom).setOnClickListener(this);

        timelyGroup.setOnCheckedChangeListener(this);

        sign.setOnImageChangedListener(this);
        assay.setOnImageChangedListener(this);
        image.setOnImageChangedListener(this);

        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        datePickerDialog = new DatePickerDialog(getContext(), new OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                setEditText(R.id.edt_patient_reservation_date, year + "年" + (monthOfYear + 1) + "月" + dayOfMonth + "日");
            }
        }, year, month, day);

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
    public void onClick(View v) {
        int id = v.getId();
        Intent intent = null;
        switch (id) {
            case R.id.edt_patient_reservation_date:
                if (null != datePickerDialog) {
                    datePickerDialog.show();
                }
                break;
            case R.id.btn_upload_data:
                submit();
                break;
            case R.id.edt_patient_anamnesis:
                intent = new Intent(getContext(), SelectAnamnesisActivity.class);
                startActivityForResult(intent, SELECT_ANAMNESIS_ACTIVITY);
                break;
        //            case R.id.edt_patient_symptom:
        //                intent = new Intent(getContext(), SelectSymptomActivity.class);
        //                startActivityForResult(intent, SELECT_SYMPTOM_ACTIVITY);
        //                break;
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.rdb_yes:
                findView(R.id.ll_others_order_or_not_container).setVisibility(View.VISIBLE);
                ((CheckBox) findView(R.id.cb_others_order_or_not)).setChecked(true);
                findView(R.id.ll_patient_reservation_date_container).setVisibility(View.GONE);
                break;
            case R.id.rdb_no:
                findView(R.id.ll_others_order_or_not_container).setVisibility(View.GONE);
                ((CheckBox) findView(R.id.cb_others_order_or_not)).setChecked(false);
                findView(R.id.ll_patient_reservation_date_container).setVisibility(View.VISIBLE);
                break;
        }
    }

    private void submit() {
        //发送请求
        if (NetworkUtil.isNetworkAvaliable(getContext()) && doctor != null) {
            showProgressDialog(getString(R.string.consultation_creating));
            int doctorId = doctor.getId();
            int userId = GsApplication.getInstance().getUserId();
            String patient_dept = getEditTextInput(R.id.edt_patient_dept, "");
            String patient_hospital = getEditTextInput(R.id.edt_patient_hospital, "");
            String patient_city = getEditTextInput(R.id.edt_patient_city, "");
            String patient_address = getEditTextInput(R.id.edt_patient_address, "");
            String patient_name = getEditTextInput(R.id.edt_patient_name, "");
            Integer patient_sex = getSex();
            Integer patient_age = Integer.valueOf(getEditTextInput(R.id.edt_patient_age, "-1"));
            Log.i("testYJ", "age instanseof int?-->" + (patient_age instanceof Integer));
            String patient_mobile = getEditTextInput(R.id.edt_patient_mobile, "");
            String patient_illness = getEditTextInput(R.id.edt_patient_illness, "");
            String patient_remark = getEditTextInput(R.id.edt_patient_remark, "");
            String purpose = getEditTextInput(R.id.edt_consultation_purpose, "");
            Integer timely = getTimely();
            Integer other_order = getOtherOrder();
            String order_at = getOrderAt();

            CommonRequest consultationDetailRequest = new CommonRequest();
            consultationDetailRequest.setRequestApiName(ServiceAPIConstant.REQUEST_API_NAME_CONSULTATION_CREATE + "&" + APIKey.COMMON_EXPAND + "=" + APIKey.COMMON_SYMPTOM + "," + APIKey.COMMON_ANAMNESIS);
            //            consultationDetailRequest.setRequestApiName(ServiceAPIConstant.REQUEST_API_NAME_CONSULTATION_CREATE);
            consultationDetailRequest.setRequestID(ServiceAPIConstant.REQUEST_ID_CONSULTATION_CREATE);
            consultationDetailRequest.setRequestMethod(CommonRequest.REQUEST_METHOD_POST);
            //            consultationDetailRequest.addRequestParam(APIKey.COMMON_DOCTOR_ID, GsApplication.getInstance(getContext()).getUserId());
            consultationDetailRequest.addRequestParam(APIKey.COMMON_DOCTOR_ID, userId);
            consultationDetailRequest.addRequestParam(APIKey.COMMON_EXPERT_ID, doctorId);
            consultationDetailRequest.addRequestParam(APIKey.COMMON_TYPE, consultationType);
            consultationDetailRequest.addRequestParam(APIKey.CONSULTATION_PATIENT_DEPT, patient_dept);
            consultationDetailRequest.addRequestParam(APIKey.CONSULTATION_PATIENT_HOSPITAL, patient_hospital);
            consultationDetailRequest.addRequestParam(APIKey.CONSULTATION_PATIENT_CITY, patient_city);
            consultationDetailRequest.addRequestParam(APIKey.CONSULTATION_PATIENT_ADDRESS, patient_address);
            consultationDetailRequest.addRequestParam(APIKey.CONSULTATION_PATIENT_NAME, patient_name);
            consultationDetailRequest.addRequestParam(APIKey.CONSULTATION_PATIENT_SEX, patient_sex);
            consultationDetailRequest.addRequestParam(APIKey.CONSULTATION_PATIENT_AGE, patient_age);
            consultationDetailRequest.addRequestParam(APIKey.CONSULTATION_PATIENT_MOBILE, patient_mobile);
            consultationDetailRequest.addRequestParam(APIKey.CONSULTATION_PATIENT_ILLNESS, patient_illness);
            consultationDetailRequest.addRequestParam(APIKey.CONSULTATION_PATIENT_REMARK, patient_remark);
            consultationDetailRequest.addRequestParam(APIKey.CONSULTATION_PURPOSE, purpose);
            consultationDetailRequest.addRequestParam(APIKey.CONSULTATION_TIMELY, timely);
            consultationDetailRequest.addRequestParam(APIKey.CONSULTATION_OTHER_ORDER, other_order);
            consultationDetailRequest.addRequestParam(APIKey.CONSULTATION_ORDER_AT, order_at);
            if (anamnesis != null)
                consultationDetailRequest.addRequestParam(APIKey.COMMON_ANAMNESIS, anamnesis);

            String symptom = getEditTextInput(R.id.edt_patient_symptom, null);
            if (symptom != null) {
                consultationDetailRequest.addRequestParam(APIKey.COMMON_SYMPTOM, symptom);
            }
            //            if (anamnesisIndex != -1)
            //                consultationDetailRequest.addRequestParam(APIKey.CONSULTATION_ANAMNESIS_ID, anamnesisIndex);
            //            if (symptomIndex != -1)
            //                consultationDetailRequest.addRequestParam(APIKey.CONSULTATION_SYMPTOM_ID, symptomIndex);

            addRequestAsyncTask(consultationDetailRequest);
        } else {
            showToast(getString(R.string.network_error));
        }

    }

    private void createConsultationFiles() {
        //上传文件
        CommonRequest createConsultationFileRequest = new CommonRequest();
        createConsultationFileRequest.setRequestApiName(ServiceAPIConstant.REQUEST_API_NAME_CONSULTATION_FILE_CREATE);
        createConsultationFileRequest.setRequestID(ServiceAPIConstant.REQUEST_ID_CONSULTATION_FILE_CREATE);
        createConsultationFileRequest.setRequestMethod(CommonRequest.REQUEST_METHOD_POST);
        createConsultationFileRequest.setRequestContentType(CommonRequest.REQUEST_CONTENT_TYPE_FORM_DATA);
        createConsultationFileRequest.addRequestParam(APIKey.COMMON_FILES_FIELD, APIKey.COMMON_PATH);
        if (consultationId != -1) {
            createConsultationFileRequest.addRequestParam(APIKey.COMMON_CONSULTATION_ID, consultationId);
        }
        List<File> files = new ArrayList<File>();
        StringBuffer filesType = new StringBuffer();
        if (signPath != null) {
            File signFile = new File(signPath);
            files.add(signFile);
            filesType.append(ConsultationFileEntity.SIGN);

        }
        if (assayPath != null) {
            File assayFile = new File(assayPath);
            files.add(assayFile);
            filesType.append(",").append(ConsultationFileEntity.ASSAY);

        }
        if (imagePath != null) {
            File imageFile = new File(imagePath);
            files.add(imageFile);
            filesType.append(",").append(ConsultationFileEntity.IAMGE);

        }

        if (files.size() > 1) {
            createConsultationFileRequest.addRequestParam(APIKey.COMMON_FILES, files);
            createConsultationFileRequest.addRequestParam(APIKey.COMMON_MULTI_FIELDS + "[type]", filesType);
        } else if (files.size() > 0) {
            createConsultationFileRequest.addRequestParam(APIKey.COMMON_FILES, files);
            createConsultationFileRequest.addRequestParam(APIKey.COMMON_TYPE, Integer.valueOf(filesType.toString()));
        }
        addRequestAsyncTask(createConsultationFileRequest);
    }

    private int getSex() {
        int sexIndex = sexGroup.getCheckedRadioButtonId();
        switch (sexIndex) {
            case R.id.rdb_male:
                return APIKey.SEX_MALE;
            case R.id.rdb_female:
                return APIKey.SEX_FEMALE;
            default:
                return -1;
        }
    }

    private int getTimely() {
        int timelyIndex = timelyGroup.getCheckedRadioButtonId();
        switch (timelyIndex) {
            case R.id.rdb_yes:
                return APIKey.CONSULTATION_TIMELY_YES;
            case R.id.rdb_no:
                return APIKey.CONSULTATION_TIMELY_NO;
            default:
                return -1;
        }
    }

    private int getOtherOrder() {
        int timely = getTimely();
        if (timely == APIKey.CONSULTATION_TIMELY_YES) {
            boolean isChecked = other_order.isChecked();
            if (isChecked)
                return 1;
        }
        return 0;
    }

    private String getOrderAt() {
        return getOrderDateFromLocate(getEditTextInput(R.id.edt_patient_reservation_date, null));
    }

    @Override
    protected void onResponse(String requestID, boolean isSuccess, int statusCode, String message, Map<String, Object> data, Map<String, Object> additionalArgsmap) {
        super.onResponse(requestID, isSuccess, statusCode, message, data, additionalArgsmap);
        if (requestID == ServiceAPIConstant.REQUEST_ID_CONSULTATION_CREATE) {
            if (isSuccess && statusCode == 201) {
                Log.i("testYJ", data.toString());
                consultationNew = EntityUtils.getConsultationEntity(data);
                Log.i("testYJ", consultationNew.toString());
                if (consultationNew != null) {
                    if (selectImagePickerViewID != -1) {
                        consultationId = consultationNew.getId();
                        createConsultationFiles();
                        return;
                    } else {
                        nextStep();
                    }
                }
            } else {
                showToast(message);
            }
        } else if (requestID == ServiceAPIConstant.REQUEST_ID_CONSULTATION_FILE_CREATE) {
            if (isSuccess) {
                if (data != null) {
                    ConsultationFileEntity consultationFile = EntityUtils.getConsultationFileEntity(data);
                    if (consultationFile != null) {
                        if (consultationNew != null) {
                            Log.i("testYJ", "data");
                            List<ConsultationFileEntity> consultationFiles = new ArrayList<ConsultationFileEntity>();
                            consultationFiles.add(consultationFile);
                            consultationNew.setConsultationFiles(consultationFiles);
                            nextStep();
                        }
                    }
                }
            }
        }
    }

    @Override
    protected void onResponse(String requestID, boolean isSuccess, int statusCode, String message, List<Map<String, Object>> items, Map<String, Object> links, Map<String, Object> meta, Map<String, Object> additionalArgsmap) {
        if (requestID == ServiceAPIConstant.REQUEST_ID_CONSULTATION_FILE_CREATE) {
            if (isSuccess) {
                Log.i("testYJ", "items=" + items);
                if (items != null) {
                    List<ConsultationFileEntity> consultationFiles = EntityUtils.getConsultationFileEntityList(items);
                    if (consultationFiles != null) {
                        if (consultationNew != null) {
                            Log.i("testYJ", "items");
                            consultationNew.setConsultationFiles(consultationFiles);
                            nextStep();
                        }
                    }
                }
            }
        }
    }

    private void nextStep() {
        dimissProgressDialog();
        GsApplication.getInstance(getContext()).setConsultation(consultationNew);
        //                    //跳转界面(如选择了即时跳转到"高手接单界面", 选择了预约,回到专家列表)
        Intent intent = null;
        if (consultationNew.getTimely() == APIKey.CONSULTATION_TIMELY_YES) {//即时
            //                    if (true) {//即时
            intent = new Intent(getContext(), WaitExpertActivity.class);
            intent.putExtra(APIKey.COMMON_ID, consultationNew.getId());
            startActivity(intent);
        } else {//非即时
            intent = new Intent(getContext(), HomeActivity.class);
            intent.putExtra("expertFragment", true);
            startActivity(intent);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case SELECT_ANAMNESIS_ACTIVITY:
                if (resultCode == Activity.RESULT_OK) {
                    anamnesis = data.getStringExtra(getString(R.string.intent_anamnesis_name));
                    //                    anamnesisIndex = data.getIntExtra(getString(R.string.intent_anamnesis_index), -1);
                    setEditText(R.id.edt_patient_anamnesis, anamnesis);
                }
                break;
            //            case SELECT_SYMPTOM_ACTIVITY:
            //                if (resultCode == Activity.RESULT_OK) {
            //                    String symptom = data.getStringExtra(getString(R.string.intent_symptom_name));
            //                    symptomIndex = data.getIntExtra(getString(R.string.intent_symptom_index), -1);
            //                    setEditText(R.id.edt_patient_symptom, symptom);
            //                }
            //                break;
            default:
                Log.i("testYJ", "result");
                if (selectImagePickerViewID != -1) {
                    ImagePickerView view = findView(selectImagePickerViewID);
                    Log.i("testYJ", "view=" + view);
                    if (null != view)
                        view.dealPickPicData(requestCode, resultCode, data);
                }
                break;
        }
    }

    @Override
    public void onStartSelectImage(int viewId, int requestId) {
        Log.i("testYJ", "onStartSelectImage-->viewId==" + viewId);
        selectImagePickerViewID = viewId;
    }

    @Override
    public void onAddImage(String filePath) {
        Log.i("testYJ", "filePath=" + filePath);
        switch (selectImagePickerViewID) {
            case R.id.ipv_sign:
                signPath = filePath;
                break;
            case R.id.ipv_assay:
                assayPath = filePath;
                break;
            case R.id.ipv_image:
                imagePath = filePath;
                break;
        }
    }

    @Override
    public void onDelImage() {
        switch (selectImagePickerViewID) {
            case R.id.ipv_sign:
                signPath = null;
                break;
            case R.id.ipv_assay:
                assayPath = null;
                break;
            case R.id.ipv_image:
                imagePath = null;
                break;
        }
        selectImagePickerViewID = -1;
    }

    @Override
    protected void onDestroy() {
        GsApplication.getInstance(getContext()).setConsultationType(0);
        super.onDestroy();
    }

}
