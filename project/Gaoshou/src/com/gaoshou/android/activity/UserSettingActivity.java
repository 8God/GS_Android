package com.gaoshou.android.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;

import com.gaoshou.android.R;
import com.gaoshou.android.entity.DoctorEntity;
import com.gaoshou.android.entity.DoctorFileEntity;
import com.gaoshou.android.fragment.PersonFragment;
import com.gaoshou.common.base.BaseActivity;
import com.gaoshou.common.base.GsApplication;
import com.gaoshou.common.component.MyActivityManager;
import com.gaoshou.common.constant.APIKey;
import com.gaoshou.common.constant.CommonConstant;
import com.gaoshou.common.constant.ServiceAPIConstant;
import com.gaoshou.common.network.CommonRequest;
import com.gaoshou.common.utils.EntityUtils;
import com.gaoshou.common.utils.NetworkUtil;
import com.gaoshou.common.utils.OpenFileUtil;

public class UserSettingActivity extends BaseActivity implements OnClickListener {
    private final int CHANGE_USER_PHOTO = 1;
    private final int CHANGE_CERTIFICATE = 2;
    private final int CHANGE_ID_PHOTO = 3;

    private DoctorEntity oldDoctor;
    private DoctorEntity newDoctor;

    private DoctorFileEntity oldHeadPhotoFile;
    private DoctorFileEntity newHeadPhotoFile;
    private DoctorFileEntity oldCertificateFile;
    private DoctorFileEntity newCertificateFile;
    private DoctorFileEntity oldIdPhotoFile;
    private DoctorFileEntity newIdPhotoFile;

    private boolean isHeadPhotoChanged;
    private boolean isCertificateChanged;
    private boolean isIdPhotoChanged;

    private MenuItem submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_setting);

        initToolbar(getString(R.string.actionbar_title_user_setting));

        initView();
    }

    private void initView() {
        oldDoctor = GsApplication.getInstance(getContext()).getMyself();
        if (oldDoctor == null)
            oldDoctor = new DoctorEntity();
        newDoctor = new DoctorEntity(oldDoctor);

        oldHeadPhotoFile = GsApplication.getInstance(getContext()).getMyHeadPhoto();
        if (oldHeadPhotoFile == null)
            oldHeadPhotoFile = new DoctorFileEntity();
        newHeadPhotoFile = new DoctorFileEntity(oldHeadPhotoFile);

        oldCertificateFile = GsApplication.getInstance(getContext()).getMyCertificate();
        if (oldCertificateFile == null)
            oldCertificateFile = new DoctorFileEntity();
        newCertificateFile = new DoctorFileEntity(oldCertificateFile);

        oldIdPhotoFile = GsApplication.getInstance(getContext()).getMyIdPhoto();
        if (oldIdPhotoFile == null)
            oldIdPhotoFile = new DoctorFileEntity();
        newIdPhotoFile = new DoctorFileEntity(oldIdPhotoFile);

        if (oldDoctor != null) {
            setTextView(R.id.tv_name, oldDoctor.getName());
            setTextView(R.id.tv_sex, oldDoctor.getSex() == APIKey.SEX_MALE ? "男" : "女");
            setTextView(R.id.tv_mobile, oldDoctor.getMobile());
            setEditText(R.id.edt_email, oldDoctor.getEmail());
            setEditText(R.id.edt_qq, oldDoctor.getQq());
            setEditText(R.id.edt_weixin, oldDoctor.getWeixin());
            setEditText(R.id.edt_phone, oldDoctor.getMobile());
            setEditText(R.id.edt_address, oldDoctor.getAddress());
            setEditText(R.id.edt_hospital, oldDoctor.getHospital());
            setEditText(R.id.edt_dept, oldDoctor.getDept());
            setEditText(R.id.edt_position, oldDoctor.getPosition());
            setEditText(R.id.edt_title, oldDoctor.getTitle());
            if (GsApplication.getInstance(getContext()).getLogonType() == CommonConstant.LOGON_TYPE_EXPERT) {
                setEditText(R.id.edt_expertise, oldDoctor.getExpertise());
            } else {
                findView(R.id.ll_user_expertise_container).setVisibility(View.GONE);
            }
            setEditText(R.id.edt_identity, oldDoctor.getIdentity());
            setEditText(R.id.edt_summary, oldDoctor.getIntro());

            if (oldHeadPhotoFile != null)
                setImageView(R.id.iv_user_icon, oldHeadPhotoFile.getPath(), R.drawable.common_icon_default_user_head);
            if (oldCertificateFile != null)
                setImageView(R.id.iv_qualification_certificate, oldCertificateFile.getPath(), R.drawable.photo);
            if (oldIdPhotoFile != null)
                setImageView(R.id.iv_id_photo, oldIdPhotoFile.getPath(), R.drawable.photo);
        }

        findView(R.id.tv_update_head_photo).setOnClickListener(this);
        findView(R.id.iv_user_icon).setOnClickListener(this);
        findView(R.id.iv_qualification_certificate).setOnClickListener(this);
        findView(R.id.iv_id_photo).setOnClickListener(this);

        ((EditText) findView(R.id.edt_email)).addTextChangedListener(new myTextWatcher(R.id.edt_email));
        ((EditText) findView(R.id.edt_qq)).addTextChangedListener(new myTextWatcher(R.id.edt_qq));
        ((EditText) findView(R.id.edt_weixin)).addTextChangedListener(new myTextWatcher(R.id.edt_weixin));
        ((EditText) findView(R.id.edt_phone)).addTextChangedListener(new myTextWatcher(R.id.edt_phone));
        ((EditText) findView(R.id.edt_address)).addTextChangedListener(new myTextWatcher(R.id.edt_address));
        ((EditText) findView(R.id.edt_hospital)).addTextChangedListener(new myTextWatcher(R.id.edt_hospital));
        ((EditText) findView(R.id.edt_dept)).addTextChangedListener(new myTextWatcher(R.id.edt_dept));
        ((EditText) findView(R.id.edt_position)).addTextChangedListener(new myTextWatcher(R.id.edt_position));
        ((EditText) findView(R.id.edt_title)).addTextChangedListener(new myTextWatcher(R.id.edt_title));
        ((EditText) findView(R.id.edt_expertise)).addTextChangedListener(new myTextWatcher(R.id.edt_expertise));
        ((EditText) findView(R.id.edt_identity)).addTextChangedListener(new myTextWatcher(R.id.edt_identity));
        ((EditText) findView(R.id.edt_summary)).addTextChangedListener(new myTextWatcher(R.id.edt_summary));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_user_setting, menu);
        submit = menu.findItem(R.id.item_submit);
        submit.setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case android.R.id.home:
                finish();
                break;
            case R.id.item_submit:
                if (NetworkUtil.isNetworkAvaliable(getContext())) {
                    showProgressDialog(getString(R.string.user_updating));
                    if (isHeadPhotoChanged || isCertificateChanged || isIdPhotoChanged) {
                        updateDoctorFilesRequest();
                    } else {
                        addUpdateDoctorRequest();
                    }
                } else {
                    showToast(getString(R.string.network_error));
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //    private void updateData() {
    //        if (NetworkUtil.isNetworkAvaliable(getContext())) {
    //            showProgressDialog(getString(R.string.user_updating));
    //            if (!oldDoctor.equals(newDoctor)) {
    //                CommonRequest updateUserRequest = new CommonRequest();
    //                updateUserRequest.setRequestApiName(ServiceAPIConstant.REQUEST_API_NAME_USER_UPDATE);
    //                updateUserRequest.setRequestID(ServiceAPIConstant.REQUEST_ID_USER_UPDATE);
    //                updateUserRequest.setRequestMethod(CommonRequest.REQUEST_METHOD_PUT);
    //                updateUserRequest.addRequestParam(APIKey.USER_ID, newDoctor.getId());
    //                updateUserRequest.addRequestParam(APIKey.COMMON_EMAIL, newDoctor.getEmail());
    //
    //
    //                addRequestAsyncTask(updateUserRequest);
    //            } else if (!oldDoctor.equals(newDoctor)) {
    //                addUpdateDoctorRequest();
    //            }
    //        } else {
    //            showToast(getString(R.string.network_error));
    //        }
    //    }

    private void updateDoctorFilesRequest() {
        CommonRequest updateDoctorFilesRequest = new CommonRequest();
        updateDoctorFilesRequest.setRequestApiName(ServiceAPIConstant.REQUEST_API_NAME_DOCTOR_FILE_CREATE);
        updateDoctorFilesRequest.setRequestID(ServiceAPIConstant.REQUEST_ID_DOCTOR_FILE_CREATE);
        updateDoctorFilesRequest.setRequestMethod(CommonRequest.REQUEST_METHOD_POST);
        updateDoctorFilesRequest.setRequestContentType(CommonRequest.REQUEST_CONTENT_TYPE_FORM_DATA);
        updateDoctorFilesRequest.addRequestParam(APIKey.COMMON_FILES_FIELD, APIKey.COMMON_PATH);
        int userId = GsApplication.getInstance().getUserId();
        if (userId != -1) {
            updateDoctorFilesRequest.addRequestParam(APIKey.COMMON_DOCTOR_ID, userId);
        }
        List<File> files = new ArrayList<File>();
        StringBuffer filesType = new StringBuffer();
        if (isHeadPhotoChanged && newHeadPhotoFile.getPath() != null) {
            File headPhotoFile = new File(newHeadPhotoFile.getPath());
            files.add(headPhotoFile);
            Log.i("testYJ", "fileLength-->" + headPhotoFile.length());
            filesType.append(APIKey.DOCTOR_FILE_TYEP_HEAD_PIC);

        }
        if (isCertificateChanged && newCertificateFile.getPath() != null) {
            File certificateFile = new File(newCertificateFile.getPath());
            files.add(certificateFile);
            filesType.append(",").append(APIKey.DOCTOR_FILE_TYEP_CERTIFICATION);

        }
        if (isIdPhotoChanged && newIdPhotoFile.getPath() != null) {
            File idPhotoFile = new File(newIdPhotoFile.getPath());
            files.add(idPhotoFile);
            filesType.append(",").append(APIKey.DOCTOR_FILE_TYEP_IDENTITY);

        }
        if (files.size() > 1) {
            updateDoctorFilesRequest.addRequestParam(APIKey.COMMON_FILES, files);
            updateDoctorFilesRequest.addRequestParam(APIKey.COMMON_MULTI_FIELDS + "[type]", filesType);
        } else if (files.size() > 0) {
            updateDoctorFilesRequest.addRequestParam(APIKey.COMMON_FILES, files);
            updateDoctorFilesRequest.addRequestParam(APIKey.COMMON_TYPE, Integer.valueOf(filesType.toString()));
        }

        addRequestAsyncTask(updateDoctorFilesRequest);

    }

    private void addUpdateDoctorRequest() {
        CommonRequest updateDoctorRequest = new CommonRequest();
        int logonType = GsApplication.getInstance().getLogonType();
        if (logonType == CommonConstant.LOGON_TYPE_DOCTOR) {
            updateDoctorRequest.setRequestApiName(ServiceAPIConstant.REQUEST_API_NAME_DOCTOR_UPDATE + "&" + APIKey.COMMON_EXPAND + "=" + APIKey.COMMON_DOCTOR_FILES + "," + APIKey.DOCTOR_EXPERTISE0 + "," + APIKey.DOCTOR_CATEGORY);
        } else if (logonType == CommonConstant.LOGON_TYPE_EXPERT) {
            updateDoctorRequest.setRequestApiName(ServiceAPIConstant.REQUEST_API_NAME_EXPERT_UPDATE + "&" + APIKey.COMMON_EXPAND + "=" + APIKey.COMMON_DOCTOR_FILES + "," + APIKey.DOCTOR_EXPERTISE0 + "," + APIKey.DOCTOR_CATEGORY);
        } else {
            showToast("请先登录");
            return;
        }
        updateDoctorRequest.setRequestID(ServiceAPIConstant.REQUEST_ID_DOCTOR_UPDATE);
        updateDoctorRequest.setRequestMethod(CommonRequest.REQUEST_METHOD_PUT);
        updateDoctorRequest.addRequestParam(APIKey.COMMON_ID, newDoctor.getId());
        updateDoctorRequest.addRequestParam(APIKey.DOCTOR_ADDRESS, newDoctor.getAddress());
        updateDoctorRequest.addRequestParam(APIKey.DOCTOR_HOSPITAL, newDoctor.getHospital());
        updateDoctorRequest.addRequestParam(APIKey.COMMON_EMAIL, newDoctor.getEmail());
        updateDoctorRequest.addRequestParam(APIKey.USER_QQ, newDoctor.getQq());
        updateDoctorRequest.addRequestParam(APIKey.USER_WEIXIN, newDoctor.getWeixin());
        updateDoctorRequest.addRequestParam(APIKey.DOCTOR_DEPT, newDoctor.getDept());
        updateDoctorRequest.addRequestParam(APIKey.DOCTOR_POSITION, newDoctor.getPosition());
        updateDoctorRequest.addRequestParam(APIKey.DOCTOR_TITLE, newDoctor.getTitle());
        updateDoctorRequest.addRequestParam(APIKey.DOCTOR_EXPERTISE_ID, newDoctor.getExpertiseId());
        updateDoctorRequest.addRequestParam(APIKey.DOCTOR_IDENTITY, newDoctor.getIdentity());
        updateDoctorRequest.addRequestParam(APIKey.DOCTOR_INTRO, newDoctor.getIntro());

        addRequestAsyncTask(updateDoctorRequest);
    }

    @Override
    protected void onResponse(String requestID, boolean isSuccess, int statusCode, String message, Map<String, Object> data, Map<String, Object> additionalArgsmap) {
        if (requestID == ServiceAPIConstant.REQUEST_ID_DOCTOR_UPDATE) {
            if (isSuccess && statusCode == 200) {
                DoctorEntity doctor = EntityUtils.getDoctorEntity(data);
                if (doctor != null) {
                    oldDoctor = doctor;
                    newDoctor = new DoctorEntity(oldDoctor);
                    updateSuccess();
                } else {
                    showToast(getString(R.string.user_update_faild));
                }
            } else {
                showToast(message);
            }
            dimissProgressDialog();

        } else if (requestID == ServiceAPIConstant.REQUEST_ID_DOCTOR_FILE_CREATE) {
            if (isSuccess) {
                if (data != null) {
                    DoctorFileEntity doctorFile = EntityUtils.getDoctorFileEntity(data);
                    if (doctorFile != null) {
                        //                        List<DoctorFileEntity> newDoctorFiles = new ArrayList<DoctorFileEntity>();
                        //                        if (!newDoctor.equalsWithoutDoctorFiles(oldDoctor)) {
                        //                            addUpdateDoctorRequest();
                        //                        } else {
                        //                            newDoctorFiles.add(doctorFile);
                        //                            newDoctor.setDoctorFiles(newDoctorFiles);
                        //                            updateSuccess();
                        //                        }

                        addUpdateDoctorRequest();
                    }
                }
            } else {
                showToast(message);
                dimissProgressDialog();
            }
        }
    }

    @Override
    protected void onResponse(String requestID, boolean isSuccess, int statusCode, String message, List<Map<String, Object>> items, Map<String, Object> links, Map<String, Object> meta, Map<String, Object> additionalArgsmap) {
        if (requestID == ServiceAPIConstant.REQUEST_ID_DOCTOR_FILE_CREATE) {
            if (isSuccess) {
                if (items != null) {
                    List<DoctorFileEntity> newDoctorFiles = EntityUtils.getDoctorFileEntityList(items);
                    if (newDoctorFiles != null) {
                        //                        if (!newDoctor.equalsWithoutDoctorFiles(oldDoctor)) {
                        //                            addUpdateDoctorRequest();
                        //                        } else {
                        //                            newDoctor.setDoctorFiles(newDoctorFiles);
                        //                            updateSuccess();
                        //                        }
                        addUpdateDoctorRequest();

                    }
                }
            } else {
                showToast(message);
                dimissProgressDialog();
            }
        }
    }

    private void updateSuccess() {
        GsApplication.getInstance(getContext()).setMyself(newDoctor);
        dimissProgressDialog();
        if (submit != null)
            submit.setVisible(false);
        showToast(getString(R.string.user_update_success));

        Intent intent = new Intent();
        intent.setAction(PersonFragment.ACTION_REFRESH_USER_DATA);
        sendBroadcast(intent);
        Log.i("testYJ", "sendBroadcaset");
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        Intent intent = new Intent(getContext(), ChangePhotoActivity.class);
        switch (viewId) {
            case R.id.tv_update_head_photo:
                intent.putExtra(getString(R.string.intent_change_photo_title), getString(R.string.actionbar_title_change_user_head_photo));
                intent.putExtra(getString(R.string.intent_change_photo_uri), newHeadPhotoFile.getPath());
                intent.putExtra(getString(R.string.intent_is_photo_changed), isHeadPhotoChanged);
                startActivityForResult(intent, CHANGE_USER_PHOTO);
                break;
            case R.id.iv_user_icon:
                OpenFileUtil.openFile(getContext(), newHeadPhotoFile.getPath());
                break;
            case R.id.iv_qualification_certificate:
                intent.putExtra(getString(R.string.intent_change_photo_title), getString(R.string.actionbar_title_change_certificate));
                intent.putExtra(getString(R.string.intent_change_photo_uri), newCertificateFile.getPath());
                intent.putExtra(getString(R.string.intent_is_photo_changed), isCertificateChanged);
                startActivityForResult(intent, CHANGE_CERTIFICATE);
                break;
            case R.id.iv_id_photo:
                intent.putExtra(getString(R.string.intent_change_photo_title), getString(R.string.actionbar_title_change_id_photo));
                intent.putExtra(getString(R.string.intent_change_photo_uri), newIdPhotoFile.getPath());
                intent.putExtra(getString(R.string.intent_is_photo_changed), isIdPhotoChanged);
                startActivityForResult(intent, CHANGE_ID_PHOTO);
                break;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case CHANGE_USER_PHOTO:
                    newHeadPhotoFile.setPath(data.getStringExtra(getString(R.string.intent_change_photo_uri)));
                    isHeadPhotoChanged = true;
                    ((ImageView) findView(R.id.iv_user_icon)).setImageBitmap(BitmapFactory.decodeFile(newHeadPhotoFile.getPath()));
                    break;
                case CHANGE_CERTIFICATE:
                    newCertificateFile.setPath(data.getStringExtra(getString(R.string.intent_change_photo_uri)));
                    isCertificateChanged = true;
                    ((ImageView) findView(R.id.iv_qualification_certificate)).setImageBitmap(BitmapFactory.decodeFile(newCertificateFile.getPath()));
                    break;
                case CHANGE_ID_PHOTO:
                    newIdPhotoFile.setPath(data.getStringExtra(getString(R.string.intent_change_photo_uri)));
                    isIdPhotoChanged = true;
                    ((ImageView) findView(R.id.iv_id_photo)).setImageBitmap(BitmapFactory.decodeFile(newIdPhotoFile.getPath()));
                    break;
            }

            isAvailableUpdate();
        }
    }

    private void isAvailableUpdate() {
        Log.i("testYJ", "oldUser==newUser?=" + oldDoctor.equals(newDoctor));
        if (!oldDoctor.equals(newDoctor) || !oldHeadPhotoFile.equals(newHeadPhotoFile) || !oldCertificateFile.equals(newCertificateFile) || !oldIdPhotoFile.equals(newIdPhotoFile)) {
            Log.i("testYJ", "显示");
            if (submit != null) {
                submit.setVisible(true);
            }
        } else {
            Log.i("testYJ", "不显示");
            if (submit != null) {
                submit.setVisible(false);
            }
        }
    }

    private class myTextWatcher implements TextWatcher {
        private int editId;

        @SuppressWarnings("unused")
        public myTextWatcher(int editId) {
            this.editId = editId;
        }

        @Override
        public void afterTextChanged(Editable s) {
            String value = s.toString();
            Log.i("testYJ", "value=" + value);
            switch (editId) {
                case R.id.edt_email:
                    newDoctor.setEmail(value);
                    break;
                case R.id.edt_qq:
                    newDoctor.setQq(value);
                    break;
                case R.id.edt_weixin:
                    newDoctor.setWeixin(value);
                    break;
                case R.id.edt_phone:
                    newDoctor.setMobile(value);
                    break;
                case R.id.edt_address:
                    newDoctor.setAddress(value);
                    break;
                case R.id.edt_hospital:
                    newDoctor.setHospital(value);
                    break;
                case R.id.edt_position:
                    newDoctor.setPosition(value);
                    break;
                case R.id.edt_title:
                    newDoctor.setTitle(value);
                    break;
                case R.id.edt_expertise:
                    newDoctor.setExpertise(value);
                    break;
                case R.id.edt_identity:
                    newDoctor.setIdentity(value);
                    break;
                case R.id.edt_summary:
                    newDoctor.setIntro(value);
                    break;
            }

            isAvailableUpdate();
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }
    }
}
