package com.gaoshou.android.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.gaoshou.android.R;
import com.gaoshou.android.entity.ConsultationFileEntity;
import com.gaoshou.android.entity.DoctorEntity;
import com.gaoshou.android.entity.DoctorFileEntity;
import com.gaoshou.common.base.BaseActivity;
import com.gaoshou.common.base.GsApplication;
import com.gaoshou.common.component.BasicDialog;
import com.gaoshou.common.component.MyActivityManager;
import com.gaoshou.common.constant.APIKey;
import com.gaoshou.common.constant.CommonConstant;
import com.gaoshou.common.constant.ServiceAPIConstant;
import com.gaoshou.common.network.CommonRequest;
import com.gaoshou.common.utils.DialogUtils;
import com.gaoshou.common.utils.EntityUtils;
import com.gaoshou.common.utils.FileUtil;
import com.gaoshou.common.utils.ImageUtil;
import com.gaoshou.common.utils.TypeUtil;
import com.gaoshou.common.utils.Validator;
import com.gaoshou.common.widget.ImagePickerView;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;

public class RegisterationFillInfoActivity extends BaseActivity implements OnClickListener {
    /** 相机拍照request code **/
    public static final int REQUESTCODE_TAKE_PHOTO = 1;
    /** 相册选择图片request code **/
    public static final int REQUESTCODE_PICK_PHOTO = 2;

    private static final String PIC_TYPE_JPG = ".jpg";
    private static final String CACHE_PIC_PATH = "CachePics";

    private final int CHANGE_HEAD_PHOTO = 11;
    private final int CHANGE_CERTIFICATION = 12;
    private final int CHANGE_ID_PHOTO = 13;

    private String mobile;
    private String psw;

    private String name;
    private String email;
    private String qq;
    private String weixin;
    private String phone;
    private String address;
    private String hospital;
    private String dept;
    private String position;
    private String title;
    private String expertise;
    private String identity;
    private String intro;

    private String headPhotoPath;
    private String certificationPath;
    private String idPhotoPath;

    private int whichToChange = -1;

    private BasicDialog pickerPicDialog;

    private int userType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_registeration_fill_info);

        init();
    }

    private void init() {
        initData();
        initToolbar("填写完整资料");
        initUI();
    }

    private void initData() {

        mobile = getIntent().getStringExtra(APIKey.COMMON_MOBILE);
        psw = getIntent().getStringExtra(APIKey.USER_PASSWORD);

    }

    private void initUI() {
        setViewClickListener(R.id.btn_register, this);
        setViewClickListener(R.id.sriv_user_head_pic, this);
        setViewClickListener(R.id.imv_certification, this);
        setViewClickListener(R.id.imv_identity, this);

        final LinearLayout expertiseLayout = findView(R.id.ll_expertise);
        final EditText expertiseEdt = findView(R.id.edt_expertise);
        RadioGroup registerType = findView(R.id.rdogrp_type);
        registerType.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rdobtn_doctor:
                        expertiseLayout.setVisibility(View.GONE);
                        expertiseEdt.setText(null);
                        break;
                    case R.id.rdobtn_expert:
                        expertiseLayout.setVisibility(View.VISIBLE);
                        break;
                    default:
                        break;
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_register:
                registeration();
                break;
            case R.id.sriv_user_head_pic:
                whichToChange = CHANGE_HEAD_PHOTO;
                showPickerPicDialog();
                break;
            case R.id.imv_certification:
                whichToChange = CHANGE_CERTIFICATION;
                showPickerPicDialog();
                break;
            case R.id.imv_identity:
                whichToChange = CHANGE_ID_PHOTO;
                showPickerPicDialog();
                break;
            case R.id.ll_take_pic:
                Intent openTakePic = ImageUtil.getStartCameraIntent();
                ((BaseActivity) getContext()).startActivityForResult(openTakePic, REQUESTCODE_TAKE_PHOTO);
                dimissPickerPicDialog();
                break;
            case R.id.ll_select_pic:
                Intent openGallery = ImageUtil.getStartGalleryIntent();
                ((BaseActivity) getContext()).startActivityForResult(openGallery, REQUESTCODE_PICK_PHOTO);
                dimissPickerPicDialog();
                break;
            default:
                break;
        }
    }

    private void registeration() {
        name = getEditTextInput(R.id.edt_name, "");
        email = getEditTextInput(R.id.edt_email, "");
        qq = getEditTextInput(R.id.edt_qq, "");
        weixin = getEditTextInput(R.id.edt_weixin, "");
        phone = getEditTextInput(R.id.edt_phone, "");
        address = getEditTextInput(R.id.edt_address, "");
        hospital = getEditTextInput(R.id.edt_hospital, "");
        dept = getEditTextInput(R.id.edt_dept, "");
        position = getEditTextInput(R.id.edt_position, "");
        title = getEditTextInput(R.id.edt_title, "");
        expertise = getEditTextInput(R.id.edt_expertise, "");
        identity = getEditTextInput(R.id.edt_identity, "");
        intro = getEditTextInput(R.id.edt_intro, "");

        List<String> mustFillInfoArray = new ArrayList<String>();
        mustFillInfoArray.add(mobile);
        mustFillInfoArray.add(name);
        mustFillInfoArray.add(hospital);
        mustFillInfoArray.add(dept);
        mustFillInfoArray.add(position);
        mustFillInfoArray.add(title);
        mustFillInfoArray.add(identity);

        for (int i = 0; i < mustFillInfoArray.size(); i++) {
            String input = mustFillInfoArray.get(i);
            if (TextUtils.isEmpty(input)) {
                showToast("信息填写不完整");

                return;
            }
        }

        CommonRequest createUser = new CommonRequest();
        createUser.setRequestApiName(ServiceAPIConstant.REQUEST_API_NAME_SITE_SIGNUP);
        createUser.setRequestID(ServiceAPIConstant.REQUEST_ID_SITE_SIGNUP);
        createUser.setRequestMethod(CommonRequest.REQUEST_METHOD_POST);
        createUser.addRequestParam(APIKey.USER_USERNAME, mobile);
        createUser.addRequestParam(APIKey.USER_PASSWORD, psw);

        int type = CommonConstant.LOGON_TYPE_EXPERT;
        RadioGroup typeRadionGroup = findView(R.id.rdogrp_type);
        switch (typeRadionGroup.getCheckedRadioButtonId()) {
            case R.id.rdobtn_doctor:
                type = CommonConstant.LOGON_TYPE_DOCTOR;
                createUser.addRequestParam(APIKey.COMMON_STATUS, 1); //直接通过
                break;
            case R.id.rdobtn_expert:
                type = CommonConstant.LOGON_TYPE_EXPERT;
                createUser.addRequestParam(APIKey.COMMON_STATUS, 0); //冻结状态，待审核
                break;
            default:
                break;
        }

        addRequestAsyncTask(createUser);

        showProgressDialog("注册中");
    }

    private void createDoctor(int userId) {
        CommonRequest createDoctor = new CommonRequest();
        createDoctor.setRequestID(ServiceAPIConstant.REQUEST_ID_DOCTOR_CREATE);

        int type = CommonConstant.LOGON_TYPE_EXPERT;
        RadioGroup typeRadionGroup = findView(R.id.rdogrp_type);
        switch (typeRadionGroup.getCheckedRadioButtonId()) {
            case R.id.rdobtn_doctor:
                type = CommonConstant.LOGON_TYPE_DOCTOR;
                createDoctor.setRequestApiName(ServiceAPIConstant.REQUEST_API_NAME_DOCTOR_CREATE);
                break;
            case R.id.rdobtn_expert:
                type = CommonConstant.LOGON_TYPE_EXPERT;
                createDoctor.setRequestApiName(ServiceAPIConstant.REQUEST_API_NAME_EXPERT_CREATE);
                break;
            default:
                break;
        }

        int sex = APIKey.SEX_MALE;
        RadioGroup sexRadionGroup = findView(R.id.rdogrp_sex);
        switch (sexRadionGroup.getCheckedRadioButtonId()) {
            case R.id.rdobtn_male:
                sex = APIKey.SEX_MALE;
                break;
            case R.id.rdobtn_female:
                sex = APIKey.SEX_FEMALE;
                break;
            default:
                break;
        }

        name = getEditTextInput(R.id.edt_name, "");
        email = getEditTextInput(R.id.edt_email, "");
        qq = getEditTextInput(R.id.edt_qq, "");
        weixin = getEditTextInput(R.id.edt_weixin, "");
        phone = getEditTextInput(R.id.edt_phone, "");
        address = getEditTextInput(R.id.edt_address, "");
        hospital = getEditTextInput(R.id.edt_hospital, "");
        dept = getEditTextInput(R.id.edt_dept, "");
        position = getEditTextInput(R.id.edt_position, "");
        title = getEditTextInput(R.id.edt_title, "");
        expertise = getEditTextInput(R.id.edt_expertise, "");
        identity = getEditTextInput(R.id.edt_identity, "");
        intro = getEditTextInput(R.id.edt_intro, "");

        createDoctor.setRequestMethod(CommonRequest.REQUEST_METHOD_POST);

        createDoctor.addRequestParam(APIKey.COMMON_TYPE, type);
        createDoctor.addAdditionalArg(APIKey.COMMON_TYPE, type);
        if (!TextUtils.isEmpty(mobile)) {

            createDoctor.addRequestParam(APIKey.COMMON_MOBILE, mobile);
        }
        if (!TextUtils.isEmpty(name)) {

            createDoctor.addRequestParam(APIKey.COMMON_NAME, name);
        }
        if (!TextUtils.isEmpty(email)) {

            createDoctor.addRequestParam(APIKey.COMMON_EMAIL, email);
        }
        if (!TextUtils.isEmpty(qq)) {

            createDoctor.addRequestParam(APIKey.DOCTOR_QQ, qq);
        }
        if (!TextUtils.isEmpty(weixin)) {

            createDoctor.addRequestParam(APIKey.DOCTOR_WEIXIN, weixin);
        }
        if (!TextUtils.isEmpty(phone)) {

            createDoctor.addRequestParam(APIKey.DOCTOR_PHONE, phone);
        }
        if (!TextUtils.isEmpty(address)) {

            createDoctor.addRequestParam(APIKey.DOCTOR_ADDRESS, address);
        }
        if (!TextUtils.isEmpty(hospital)) {

            createDoctor.addRequestParam(APIKey.DOCTOR_HOSPITAL, hospital);
        }
        if (!TextUtils.isEmpty(dept)) {

            createDoctor.addRequestParam(APIKey.DOCTOR_DEPT, dept);
        }
        if (!TextUtils.isEmpty(position)) {

            createDoctor.addRequestParam(APIKey.DOCTOR_POSITION, position);
        }
        if (!TextUtils.isEmpty(title)) {

            createDoctor.addRequestParam(APIKey.DOCTOR_TITLE, title);
        }
        if (!TextUtils.isEmpty(expertise)) {

            createDoctor.addRequestParam(APIKey.DOCTOR_EXPERTISE, expertise);
        }
        if (!TextUtils.isEmpty(identity)) {

            createDoctor.addRequestParam(APIKey.DOCTOR_IDENTITY, identity);
        }
        if (!TextUtils.isEmpty(intro)) {

            createDoctor.addRequestParam(APIKey.DOCTOR_INTRO, intro);
        }
        createDoctor.addRequestParam(APIKey.USER_ID, userId);
        createDoctor.addRequestParam(APIKey.USER_SEX, sex);

        addRequestAsyncTask(createDoctor);
    }

    private void createDoctorFiles(DoctorEntity doctor) {
        //上传文件
        CommonRequest createDoctorFilesRequest = new CommonRequest();
        createDoctorFilesRequest.setRequestApiName(ServiceAPIConstant.REQUEST_API_NAME_DOCTOR_FILE_CREATE);
        createDoctorFilesRequest.setRequestID(ServiceAPIConstant.REQUEST_ID_DOCTOR_FILE_CREATE);
        createDoctorFilesRequest.setRequestMethod(CommonRequest.REQUEST_METHOD_POST);
        createDoctorFilesRequest.addRequestParam(APIKey.COMMON_FILES_FIELD, APIKey.COMMON_PATH);
        createDoctorFilesRequest.addRequestParam(APIKey.COMMON_DOCTOR_ID, doctor.getId());

        List<File> files = new ArrayList<File>();
        StringBuffer filesType = new StringBuffer();
        if (headPhotoPath != null) {
            File headPhotoFile = new File(headPhotoPath);
            files.add(headPhotoFile);
            filesType.append(APIKey.DOCTOR_FILE_TYEP_HEAD_PIC);

        }
        if (certificationPath != null) {
            File certificationFile = new File(certificationPath);
            files.add(certificationFile);
            filesType.append(",").append(APIKey.DOCTOR_FILE_TYEP_CERTIFICATION);

        }
        if (idPhotoPath != null) {
            File idPhotoFile = new File(idPhotoPath);
            files.add(idPhotoFile);
            filesType.append(",").append(APIKey.DOCTOR_FILE_TYEP_IDENTITY);

        }

        if (files.size() > 1) {
            createDoctorFilesRequest.addRequestParam(APIKey.COMMON_FILES, files);
            createDoctorFilesRequest.addRequestParam(APIKey.COMMON_MULTI_FIELDS + "[type]", filesType);
        } else if (files.size() > 0) {
            createDoctorFilesRequest.addRequestParam(APIKey.COMMON_FILES, files);
            createDoctorFilesRequest.addRequestParam(APIKey.COMMON_TYPE, Integer.valueOf(filesType.toString()));
        }
        addRequestAsyncTask(createDoctorFilesRequest);
    }

    @Override
    protected void onResponse(String requestID, boolean isSuccess, int statusCode, String message, Map<String, Object> data, Map<String, Object> additionalArgsmap) {
        super.onResponse(requestID, isSuccess, statusCode, message, data, additionalArgsmap);
        if (ServiceAPIConstant.REQUEST_ID_SITE_SIGNUP.equals(requestID)) {
            boolean isCreatedUser = false;
            int userId = -1;
            if (isSuccess) {
                if (null != data) {
                    mobile = TypeUtil.getString(data.get(APIKey.USER_USERNAME), "");
                    userId = TypeUtil.getInteger(data.get(APIKey.COMMON_ID), -1);
                    if (Validator.isPhoneNumber(mobile) && Validator.isIdValid(userId)) {
                        isCreatedUser = true;
                    }
                }
            }
            if (isCreatedUser && userId != -1) {
                createDoctor(userId);
            } else {
                showToast(message);
                dimissProgressDialog();
            }
        } else if (ServiceAPIConstant.REQUEST_ID_DOCTOR_CREATE.equals(requestID)) {
            if (isSuccess) {
                showToast("注册成功");

                userType = TypeUtil.getInteger(additionalArgsmap.get(APIKey.COMMON_TYPE), CommonConstant.LOGON_TYPE_DOCTOR);
                if (headPhotoPath == null && certificationPath == null && idPhotoPath == null) {
                    backToLogonActivity();
                } else {
                    DoctorEntity doctor = EntityUtils.getDoctorEntity(data);
                    if (null != doctor) {
                        createDoctorFiles(doctor);
                    }
                }

            } else {
                showToast(message);
            }

            dimissProgressDialog();
        }
        //        else if (ServiceAPIConstant.REQUEST_ID_DOCTOR_FILE_CREATE.equals(requestID)) {
        //            if (isSuccess && data != null) {
        //                backToLogonActivity();
        //            }
        //        }
    }

    @Override
    protected void onResponse(String requestID, boolean isSuccess, int statusCode, String message, List<Map<String, Object>> items, Map<String, Object> links, Map<String, Object> meta, Map<String, Object> additionalArgsmap) {
        if (ServiceAPIConstant.REQUEST_ID_DOCTOR_FILE_CREATE.equals(requestID)) {
            if (isSuccess && items != null) {
                backToLogonActivity();
            }
        }
    }

    private void backToLogonActivity() {
        if (userType == CommonConstant.LOGON_TYPE_EXPERT) {
            showToast("专家需工作人员审核通过后才可启用");
            DialogUtils.showAlertDialog(getContext(), "专家需工作人员审核通过后才可启用，届时会有工作人员联系您", getString(R.string.yes), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    MyActivityManager.getInstance().clearAllActivity();

                    startActivity(new Intent(getContext(), LogonActivity.class));
                    finish();
                }
            });
        } else {
            MyActivityManager.getInstance().clearAllActivity();

            startActivity(new Intent(getContext(), LogonActivity.class));
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            default:
                Log.i("testYJ", "result");
                dealPickPicData(requestCode, resultCode, data);
                break;
        }
    }

    /*************** 照片 ****************/
    private void showPickerPicDialog() {
        if (null == pickerPicDialog) {
            BasicDialog.Builder bBuilder = new BasicDialog.Builder(getContext());
            View contentView = LayoutInflater.from(getContext()).inflate(R.layout.view_picker_pic, null);
            contentView.findViewById(R.id.ll_take_pic).setOnClickListener(this);
            contentView.findViewById(R.id.ll_select_pic).setOnClickListener(this);

            bBuilder.setContentView(contentView);
            bBuilder.setTitle("添加图片");

            pickerPicDialog = bBuilder.create();
        }
        pickerPicDialog.show();

    }

    private void dimissPickerPicDialog() {
        if (null != pickerPicDialog && pickerPicDialog.isShowing()) {
            pickerPicDialog.dismiss();
        }
    }

    public void dealPickPicData(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == ImagePickerView.REQUESTCODE_TAKE_PHOTO) {
                Bundle bundle = data.getExtras();
                Bitmap bitmap = (Bitmap) bundle.get("data");// 获取相机返回的数据，并转换为Bitmap图片格式  

                if (null != bitmap) {
                    File tmpImageFile = FileUtil.getCacheFile(getContext(), CACHE_PIC_PATH, bitmap.hashCode() + PIC_TYPE_JPG);//创建压缩图片文件
                    ImageUtil.saveEncodeJpegImageFile(tmpImageFile, bitmap, getEncodePercent(bitmap));
                    setImageByPath(tmpImageFile.getAbsolutePath());
                }

            } else if (requestCode == ImagePickerView.REQUESTCODE_PICK_PHOTO) {
                Uri imvUri = data.getData();
                String imagePath = ImageUtil.getAbsoluteImagePathByUri(getContext(), imvUri);

                if (null != imagePath) {
                    File imageFile = new File(imagePath);
                    if (null != imageFile && imageFile.isFile()) {
                        HashCodeFileNameGenerator generator = new HashCodeFileNameGenerator();
                        String tmpImageFileName = generator.generate(imageFile.getName());
                        File tmpImageFile = FileUtil.getCacheFile(getContext(), CACHE_PIC_PATH, tmpImageFileName + PIC_TYPE_JPG);//创建压缩图片文件

                        Bitmap imageBitmap = BitmapFactory.decodeFile(imagePath);
                        ImageUtil.saveEncodeJpegImageFile(tmpImageFile, imageBitmap, getEncodePercent(imageBitmap));
                        setImageByPath(tmpImageFile.getAbsolutePath());

                    }
                }
            }
        }

    }

    public void setImageByPath(String imagePath) {
        int width = 280;
        int height = 280;

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;//不加载bitmap到内存中  
        BitmapFactory.decodeFile(imagePath, options);
        int outWidth = options.outWidth;
        int outHeight = options.outHeight;
        options.inDither = false;
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        options.inSampleSize = 1;

        if (outWidth != 0 && outHeight != 0) {
            options.inSampleSize = (outWidth / width + outHeight / height) / 2;
        }
        options.inJustDecodeBounds = false;

        Bitmap bm = BitmapFactory.decodeFile(imagePath, options);

        int postPicImv_id = -1;
        switch (whichToChange) {
            case CHANGE_HEAD_PHOTO:
                headPhotoPath = imagePath;
                postPicImv_id = R.id.sriv_user_head_pic;
                break;
            case CHANGE_CERTIFICATION:
                certificationPath = imagePath;
                postPicImv_id = R.id.imv_certification;
                break;
            case CHANGE_ID_PHOTO:
                idPhotoPath = imagePath;
                postPicImv_id = R.id.imv_identity;
                break;
            default:
                break;
        }
        ImageView postPicImv = findView(postPicImv_id);
        if (postPicImv != null)
            postPicImv.setImageBitmap(bm);
    }

    private int getEncodePercent(Bitmap bitmap) {
        int encodePercent = 100;
        if (bitmap.getByteCount() > 100 * 1000 * 1000) {
            encodePercent = 5;
        } else if (bitmap.getByteCount() > 50 * 1000 * 1000) {
            encodePercent = 10;
        } else if (bitmap.getByteCount() > 30 * 1000 * 1000) {
            encodePercent = 30;
        } else if (bitmap.getByteCount() > 10 * 1000 * 1000) {
            encodePercent = 50;
        } else if (bitmap.getByteCount() > 1000 * 1000) {
            encodePercent = 70;
        } else if (bitmap.getByteCount() > 500 * 1000) {
            encodePercent = 90;
        }
        return encodePercent;
    }

}
