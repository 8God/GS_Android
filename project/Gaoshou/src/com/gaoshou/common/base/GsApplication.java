package com.gaoshou.common.base;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;
import android.view.Gravity;
import android.widget.Toast;
import cn.jpush.android.api.JPushInterface;

import com.baidu.mapapi.SDKInitializer;
import com.gaoshou.android.entity.AlipayOrderEntity;
import com.gaoshou.android.entity.BaseEntity;
import com.gaoshou.android.entity.CityEntity;
import com.gaoshou.android.entity.ConsultationEntity;
import com.gaoshou.android.entity.ConsultationFileEntity;
import com.gaoshou.android.entity.DoctorEntity;
import com.gaoshou.android.entity.DoctorFileEntity;
import com.gaoshou.android.entity.EvaluateEntity;
import com.gaoshou.android.entity.OrderEntity;
import com.gaoshou.android.entity.RepineEntity;
import com.gaoshou.android.entity.UserEntity;
import com.gaoshou.android.geo.Address;
import com.gaoshou.common.component.AutoScrollPagerFragment.ScrollPager;
import com.gaoshou.common.component.MyActivityManager;
import com.gaoshou.common.constant.APIKey;
import com.gaoshou.common.constant.CommonConstant;
import com.gaoshou.common.utils.CalendarUtils;
import com.gaoshou.common.utils.Validator;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

public class GsApplication extends Application {

    public static GsApplication mApplication;
    private SharedPreferences sharedPreferences;
    private Editor sharedPreferencesEditor;

    private static boolean isNetworkSettingsMessageEnabled = false;
    private boolean isUpdateDownloading = false;
    //    private boolean isLogon = true; //TODO 未完成登陆模块，默认为true，完成登陆模块改为false
    private boolean isLogon = false;

    private int logonType = CommonConstant.LOGON_TYPE_DOCTOR;

    private int doctorListToBeContinued;
    private int consultationType;//会诊类别

    //    private int userId = 1805296298; //TODO 未完成登陆模块，手动添加userId，完成登陆模块则去掉
    private int userId = -1;
    private UserEntity user;
    private DoctorEntity myself;//当前用户的医生资料
    private DoctorEntity currentExpert;//咨询专家资料

    private DoctorFileEntity myHeadPhoto;//个人头像
    private DoctorFileEntity myCertificate;//医生资格证
    private DoctorFileEntity myIdPhoto;//身份证照片

    private Address latestAddress;

    private ConsultationEntity expertAcceptConsultation;//咨询订单
    private ConsultationEntity consultation;//咨询订单
    private OrderEntity order;//案例详情
    private int orderDetailUserType = -1;

    private List<RepineEntity> repineList;
    private List<RepineEntity> repinedList;
    private RepineEntity repine;

    /************************** 数据缓存区 ************************/
    private List<ScrollPager> homeBannerList;
    private List<BaseEntity> msgList;

    /*************************************************************/

    private List<ConsultationFileEntity> consultationFileList;
    private List<DoctorEntity> expertList;
    private List<DoctorEntity> recommendedExpertList;
    private List<EvaluateEntity> evaluatelist;

    /************************* 我的案例缓存 ***********************/
    //用户端
    private List<OrderEntity> finishedOrderList;
    private List<OrderEntity> unfinishedOrderList;

    //医生端
    private List<OrderEntity> unfinishedDoctorOrderList;
    private List<OrderEntity> unfinishedExpertOrderList;
    private List<OrderEntity> finishedDoctorOrderList;
    private List<OrderEntity> finishedExpertOrderList;
    /************************************************************/

    //缓存支付商品信息
    private AlipayOrderEntity alipayOrderDetail;

    public boolean isUpdateDownloading() {
        return isUpdateDownloading;
    }

    public void setUpdateDownloading(boolean isUpdateDownloading) {
        this.isUpdateDownloading = isUpdateDownloading;
    }

    public int getLogonType() {
        logonType = sharedPreferences.getInt(APIKey.DOCTOR_TYPE, CommonConstant.LOGON_TYPE_DOCTOR);
        return logonType;
    }

    public void setLogonType(int logonType) {
        this.logonType = logonType;
        sharedPreferencesEditor.putInt(APIKey.DOCTOR_TYPE, logonType).commit();
    }

    public boolean isLogon() {
        if (!this.isLogon) {
            this.isLogon = sharedPreferences.getBoolean("isLogon", false);
        }
        return this.isLogon;
    }

    public void setLogon(boolean isLogon) {
        this.isLogon = isLogon;
        sharedPreferencesEditor.putBoolean("isLogon", isLogon);
        sharedPreferencesEditor.commit();
    }

    public int getUserId() {
        if (-1 == userId) {
            userId = sharedPreferences.getInt(APIKey.COMMON_ID, -1);
        }
        return userId;
    }

    public void setUserId(int userId) {
        if (-1 != userId) {
            this.userId = userId;
            sharedPreferencesEditor.putInt(APIKey.COMMON_ID, userId).commit();
        }
    }

    public void setUser(UserEntity user) {
        if (null != user) {
            this.user = user;

            this.userId = user.getId();
            if (Validator.isIdValid(userId)) {
                setLogon(true);

                sharedPreferencesEditor.putInt(APIKey.USER_ID, -1);
                sharedPreferencesEditor.putString(APIKey.USER_USERNAME, user.getUsername());
                sharedPreferencesEditor.putString(APIKey.USER_CREATED_AT, user.getCreatedAt());
                sharedPreferencesEditor.putString(APIKey.COMMON_MOBILE, user.getMobile());
                sharedPreferencesEditor.putString(APIKey.COMMON_EMAIL, user.getEmail());

                sharedPreferencesEditor.commit();
            }
        }

    }

    public UserEntity getUser() {
        if (null == this.user) {
            user = new UserEntity();

        }
        return this.user;
    }

    public void setCurrentExpert(DoctorEntity currentExpert) {
        this.currentExpert = currentExpert;
    }

    public DoctorEntity getCurrentExpert() {
        return this.currentExpert;
    }

    public DoctorEntity getMyself() {
        if (myself == null) {

            myself = new DoctorEntity();

            myself.setId(sharedPreferences.getInt(APIKey.COMMON_ID, -1));
            myself.setUserId(sharedPreferences.getInt(APIKey.USER_ID, -1));
            myself.setName(sharedPreferences.getString(APIKey.COMMON_NAME, ""));
            myself.setSex(sharedPreferences.getInt(APIKey.DOCTOR_SEX, 0));
            myself.setEmail(sharedPreferences.getString(APIKey.COMMON_EMAIL, ""));
            myself.setQq(sharedPreferences.getString(APIKey.DOCTOR_QQ, ""));
            myself.setWeixin(sharedPreferences.getString(APIKey.DOCTOR_WEIXIN, ""));
            myself.setMobile(sharedPreferences.getString(APIKey.COMMON_MOBILE, ""));
            myself.setAddress(sharedPreferences.getString(APIKey.DOCTOR_ADDRESS, ""));
            myself.setHospital(sharedPreferences.getString(APIKey.DOCTOR_HOSPITAL, ""));
            myself.setDept(sharedPreferences.getString(APIKey.DOCTOR_DEPT, ""));
            myself.setPosition(sharedPreferences.getString(APIKey.DOCTOR_POSITION, ""));
            myself.setTitle(sharedPreferences.getString(APIKey.DOCTOR_TITLE, ""));
            myself.setCityId(sharedPreferences.getInt(APIKey.DOCTOR_CITY_ID, -1));
            myself.setExpertiseId(sharedPreferences.getInt(APIKey.DOCTOR_EXPERTISE_ID, -1));
            myself.setCategoryId(sharedPreferences.getInt(APIKey.DOCTOR_CATEGORY_ID, -1));
            myself.setIdentity(sharedPreferences.getString(APIKey.DOCTOR_IDENTITY, ""));
            myself.setIntro(sharedPreferences.getString(APIKey.DOCTOR_INTRO, ""));
            myself.setScoreTimes(sharedPreferences.getInt(APIKey.DOCTOR_SCORE_TIMES, -1));
            myself.setAvgScore(sharedPreferences.getString(APIKey.DOCTOR_AVG_SCORE, ""));
            myself.setType(sharedPreferences.getInt(APIKey.COMMON_TYPE, 0));
            myself.setLongitude(sharedPreferences.getFloat(APIKey.DOCTOR_LONGITUDE, 0f));
            myself.setLatitude(sharedPreferences.getFloat(APIKey.DOCTOR_LATITUDE, 0f));
            myself.setRecommended(sharedPreferences.getInt(APIKey.DOCTOR_RECOMMENDED, 0));
            myself.setCertified(sharedPreferences.getInt(APIKey.DOCTOR_CERTIFIED, 0));
            myself.setConsultationFee(sharedPreferences.getString(APIKey.DOCTOR_CONSULTATION_FEE, ""));
            myself.setConsultationOperationFee(sharedPreferences.getString(APIKey.DOCTOR_CONSULTATION_OPERATION_FEE, ""));
            myself.setStatus(sharedPreferences.getInt(APIKey.COMMON_STATUS, 0));
            myself.setCreatedAt(sharedPreferences.getString(APIKey.COMMON_CREATED_AT, ""));
            myself.setUpdatedAt(sharedPreferences.getString(APIKey.COMMON_UPDATED_AT, ""));
            myself.setExpertise(sharedPreferences.getString(APIKey.DOCTOR_EXPERTISE, ""));
            myself.setHeadPicPath(sharedPreferences.getString(APIKey.DOCTOR_HEAD_PIC, ""));
            myself.setCertificationPath(sharedPreferences.getString(APIKey.DOCTOR_CERTIFICATION_PATH, ""));
            myself.setIdentityPath(sharedPreferences.getString(APIKey.DOCTOR_IDENTITY_PATH, ""));

            DoctorFileEntity headPhoto = new DoctorFileEntity(sharedPreferences.getInt(APIKey.DOCTOR_FILE_HEAD_PHOTO_ID, -1), myself.getHeadPicPath(), APIKey.DOCTOR_FILE_TYEP_HEAD_PIC);
            DoctorFileEntity certificate = new DoctorFileEntity(sharedPreferences.getInt(APIKey.DOCTOR_FILE_CERTIFICATE_ID, -1), myself.getCertificationPath(), APIKey.DOCTOR_FILE_TYEP_CERTIFICATION);
            DoctorFileEntity identity = new DoctorFileEntity(sharedPreferences.getInt(APIKey.DOCTOR_FILE_ID_PHOTO_ID, -1), myself.getIdentityPath(), APIKey.DOCTOR_FILE_TYEP_IDENTITY);

            List<DoctorFileEntity> doctorFiles = new ArrayList<DoctorFileEntity>();
            doctorFiles.add(headPhoto);
            doctorFiles.add(certificate);
            doctorFiles.add(identity);
            myself.setDoctorFiles(doctorFiles);

            setMyHeadPhoto(headPhoto);
            setMyCertificate(certificate);
            setMyIdPhoto(identity);
        }

        myself.setPswMD5(sharedPreferences.getString(APIKey.USER_PASSWORD + myself.getId(), ""));

        //取出城市表
        String cityName = sharedPreferences.getString(APIKey.COMMON_CITY, "");
        if (!Validator.isEmpty(cityName)) {
            CityEntity city = new CityEntity();
            city.setId(myself.getCityId());
            city.setName(cityName);
            myself.setCity(city);
        }

        return myself;
    }

    public void setMyself(DoctorEntity myself) {
        if (null != myself) {
            this.myself = myself;

            int id = myself.getId();
            int userId = myself.getUserId();
            if (Validator.isIdValid(id) && Validator.isIdValid(userId)) {

                this.userId = id;
                this.logonType = myself.getType();//登陆类型(医生、专家)
                setLogon(true);

                sharedPreferencesEditor.putInt(APIKey.COMMON_ID, id);
                sharedPreferencesEditor.putInt(APIKey.USER_ID, myself.getUserId());
                sharedPreferencesEditor.putString(APIKey.COMMON_NAME, myself.getName());
                sharedPreferencesEditor.putInt(APIKey.DOCTOR_SEX, myself.getSex());
                sharedPreferencesEditor.putString(APIKey.COMMON_EMAIL, myself.getEmail());
                sharedPreferencesEditor.putString(APIKey.DOCTOR_QQ, myself.getQq());
                sharedPreferencesEditor.putString(APIKey.DOCTOR_WEIXIN, myself.getWeixin());
                sharedPreferencesEditor.putString(APIKey.COMMON_MOBILE, myself.getMobile());
                sharedPreferencesEditor.putString(APIKey.DOCTOR_ADDRESS, myself.getAddress());
                sharedPreferencesEditor.putString(APIKey.DOCTOR_HOSPITAL, myself.getHospital());
                sharedPreferencesEditor.putString(APIKey.DOCTOR_DEPT, myself.getDept());
                sharedPreferencesEditor.putString(APIKey.DOCTOR_POSITION, myself.getPosition());
                sharedPreferencesEditor.putString(APIKey.DOCTOR_TITLE, myself.getTitle());
                sharedPreferencesEditor.putInt(APIKey.DOCTOR_CITY_ID, myself.getCityId());
                sharedPreferencesEditor.putInt(APIKey.DOCTOR_EXPERTISE_ID, myself.getExpertiseId());
                sharedPreferencesEditor.putInt(APIKey.DOCTOR_CATEGORY_ID, myself.getCategoryId());
                sharedPreferencesEditor.putString(APIKey.DOCTOR_IDENTITY, myself.getIdentity());
                sharedPreferencesEditor.putString(APIKey.DOCTOR_INTRO, myself.getIntro());
                sharedPreferencesEditor.putInt(APIKey.DOCTOR_SCORE_TIMES, myself.getScoreTimes());
                sharedPreferencesEditor.putString(APIKey.DOCTOR_AVG_SCORE, myself.getAvgScore());
                sharedPreferencesEditor.putInt(APIKey.COMMON_TYPE, myself.getType());
                sharedPreferencesEditor.putFloat(APIKey.DOCTOR_LONGITUDE, myself.getLongitude());
                sharedPreferencesEditor.putFloat(APIKey.DOCTOR_LATITUDE, myself.getLatitude());
                sharedPreferencesEditor.putInt(APIKey.DOCTOR_RECOMMENDED, myself.getRecommended());
                sharedPreferencesEditor.putInt(APIKey.DOCTOR_CERTIFIED, myself.getCertified());
                sharedPreferencesEditor.putString(APIKey.DOCTOR_CONSULTATION_FEE, myself.getConsultationFee());
                sharedPreferencesEditor.putString(APIKey.DOCTOR_CONSULTATION_OPERATION_FEE, myself.getConsultationOperationFee());
                sharedPreferencesEditor.putInt(APIKey.COMMON_STATUS, myself.getStatus());
                sharedPreferencesEditor.putString(APIKey.COMMON_CREATED_AT, myself.getCreatedAt());
                sharedPreferencesEditor.putString(APIKey.COMMON_UPDATED_AT, myself.getUpdatedAt());
                //                sharedPreferencesEditor.putString(APIKey.DOCTOR_EXPERTISE0 ,myself.getExpertise0(getExpertiseEntity());  
                sharedPreferencesEditor.putString(APIKey.DOCTOR_EXPERTISE, myself.getExpertise());
                sharedPreferencesEditor.putString(APIKey.DOCTOR_HEAD_PIC, myself.getHeadPicPath());
                sharedPreferencesEditor.putString(APIKey.DOCTOR_CERTIFICATION_PATH, myself.getCertificationPath());
                sharedPreferencesEditor.putString(APIKey.DOCTOR_IDENTITY_PATH, myself.getIdentityPath());
                //                sharedPreferencesEditor.putString(APIKey.DOCTOR_CATEGORY, myself.getCategory());
                //                sharedPreferencesEditor.putString(APIKey.COMMON_CITY, myself.getCity(getCityEntity());                                                                             
                //                sharedPreferencesEditor.putString(APIKey.COMMON_USER,myself.getUser(getUserEntity()); 

                List<DoctorFileEntity> doctorFileList = myself.getDoctorFiles();
                if (doctorFileList != null) {
                    for (int i = 0; i < doctorFileList.size(); i++) {
                        DoctorFileEntity doctorFileEntity = doctorFileList.get(i);
                        if (null != doctorFileEntity) {
                            int type = doctorFileEntity.getType();
                            switch (type) {
                                case APIKey.DOCTOR_FILE_TYEP_HEAD_PIC:
                                    sharedPreferencesEditor.putInt(APIKey.DOCTOR_FILE_HEAD_PHOTO_ID, doctorFileEntity.getId());
                                    setMyHeadPhoto(doctorFileEntity);
                                    break;
                                case APIKey.DOCTOR_FILE_TYEP_CERTIFICATION:
                                    sharedPreferencesEditor.putInt(APIKey.DOCTOR_FILE_CERTIFICATE_ID, doctorFileEntity.getId());
                                    setMyCertificate(doctorFileEntity);
                                    break;
                                case APIKey.DOCTOR_FILE_TYEP_IDENTITY:
                                    sharedPreferencesEditor.putInt(APIKey.DOCTOR_FILE_ID_PHOTO_ID, doctorFileEntity.getId());
                                    setMyIdPhoto(doctorFileEntity);
                                    break;
                                default:
                                    break;
                            }
                        }
                    }
                }

                if (!TextUtils.isEmpty(myself.getPswMD5())) {
                    sharedPreferencesEditor.putString(APIKey.USER_PASSWORD + myself.getId(), myself.getPswMD5()); //存储密码MD5值，KEY为“password”+userId
                }

                //保存城市表
                if (myself.getCity() != null){
                    sharedPreferencesEditor.putString(APIKey.COMMON_CITY, myself.getCity().getName());
                }else{
                    sharedPreferencesEditor.putString(APIKey.COMMON_CITY, "");
                }

                sharedPreferencesEditor.commit();
            }
        }
    }

    public DoctorFileEntity getMyHeadPhoto() {
        return myHeadPhoto;
    }

    public void setMyHeadPhoto(DoctorFileEntity myHeadPhoto) {
        this.myHeadPhoto = myHeadPhoto;
    }

    public DoctorFileEntity getMyCertificate() {
        return myCertificate;
    }

    public void setMyCertificate(DoctorFileEntity myCertificate) {
        this.myCertificate = myCertificate;
    }

    public DoctorFileEntity getMyIdPhoto() {
        return myIdPhoto;
    }

    public void setMyIdPhoto(DoctorFileEntity myIdPhoto) {
        this.myIdPhoto = myIdPhoto;
    }

    public ConsultationEntity getConsultation() {
        return consultation;
    }

    public void setConsultation(ConsultationEntity consultation) {
        this.consultation = consultation;
    }

    public void setConsultationHasReceive(int consultationId, boolean hasReceive) {
        sharedPreferencesEditor.putBoolean(getUserId() + "has_receive" + consultationId, hasReceive).commit();
    }

    public boolean isConsultationHasReceive(int consultationId) {
        return sharedPreferences.getBoolean(getUserId() + "has_receive" + consultationId, false);
    }

    public ConsultationEntity getExpertAcceptConsultation() {
        return expertAcceptConsultation;
    }

    public void setExpertAcceptConsultation(ConsultationEntity expertAcceptConsultation) {
        this.expertAcceptConsultation = expertAcceptConsultation;
    }

    public OrderEntity getOrder() {
        return order;
    }

    public void setOrder(OrderEntity order) {
        this.order = order;
    }

    public List<ConsultationFileEntity> getConsultationFileList() {
        return consultationFileList;
    }

    public void setConsultationFile(List<ConsultationFileEntity> consultationFileList) {
        this.consultationFileList = consultationFileList;
    }

    public List<DoctorEntity> getExpertList() {
        return expertList;
    }

    public void setExpertList(List<DoctorEntity> expertList) {
        this.expertList = expertList;
    }

    public List<DoctorEntity> getRecommendedExpertList() {
        return recommendedExpertList;
    }

    public void setRecommendedExpertList(List<DoctorEntity> recommendedExpertList) {
        this.recommendedExpertList = recommendedExpertList;
    }

    public void setCurrentVersionCode(int versionCode) {
        sharedPreferencesEditor.putInt("versionCode", versionCode).commit();
    }

    public int getCurrentVersionCode() {
        return sharedPreferences.getInt("versionCode", 0);
    }

    public void resetNetworkSettingsMessageEnabled() {
        GsApplication.isNetworkSettingsMessageEnabled = true;
    }

    public void showNetworkSettingsMessage() {

        if (isNetworkSettingsMessageEnabled) {
            isNetworkSettingsMessageEnabled = false;
            Toast toast = Toast.makeText(this, "网络异常，请检查网络设置。", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
    }

    public String getKey(String modelName, String modelId) {
        return userId + "_" + modelName + "_" + modelId;
    }

    public Date getUpdateTriggerTime() {
        return CalendarUtils.strToDate(sharedPreferences.getString("updateTriggerTime", ""));
    }

    public void setUpdateTriggerTime(Date updateTriggerTime) {
        sharedPreferencesEditor.putString("updateTriggerTime", CommonConstant.serverTimeFormat.format(updateTriggerTime));
        sharedPreferencesEditor.commit();
    }

    public boolean getExpertyHasLikes(int expertId, boolean defaultValue) {
        boolean hasLikes = defaultValue;
        if (isLogon) {
            if (Validator.isIdValid(expertId)) {
                String hasLikesKey = getSharedPrefKey("partner_hasLikes", expertId, getUserId());
                if (null != hasLikesKey) {
                    hasLikes = sharedPreferences.getBoolean(hasLikesKey, defaultValue);
                }
            }
        }
        return hasLikes;
    }

    public void setExpertyHasLikes(int expertId, boolean hasLikes) {
        if (isLogon) {
            if (Validator.isIdValid(expertId)) {
                String hasLikesKey = getSharedPrefKey("partner_hasLikes", expertId, getUserId());
                if (null != hasLikesKey) {
                    sharedPreferencesEditor.putBoolean(hasLikesKey, hasLikes).commit();
                }
            }
        }
    }

    private String getSharedPrefKey(String keyTpye, int key, int userId) {
        StringBuffer keySB = new StringBuffer();
        keySB.append(keyTpye);
        keySB.append("_");
        keySB.append(key);
        keySB.append("_");
        keySB.append(userId);
        return keySB.toString();
    }

    public static GsApplication getInstance() {
        return mApplication;
    }

    public static GsApplication getInstance(Context context) {
        return (GsApplication) context.getApplicationContext();
    }

    public int getDoctorListToBeContinued() {
        return doctorListToBeContinued;
    }

    public void setDoctorListToBeContinued(int doctorListToBeContinued) {
        this.doctorListToBeContinued = doctorListToBeContinued;
    }

    public int getConsultationType() {
        return consultationType;
    }

    public void setConsultationType(int consultationType) {
        this.consultationType = consultationType;
    }

    public List<EvaluateEntity> getEvaluatelist() {
        return evaluatelist;
    }

    public void setEvaluatelist(List<EvaluateEntity> evaluatelist) {
        this.evaluatelist = evaluatelist;
    }

    public void uesrLogout() {
        setUser(null);
        setMyself(null);
        setLogon(false);
        setConsultationFile(null);
        setDoctorListToBeContinued(0);
        setExpertList(null);
        setConsultationType(0);
        setMyIdPhoto(null);
        setMyHeadPhoto(null);
        setMyCertificate(null);
        setConsultation(null);
        setCurrentExpert(null);
        setEvaluatelist(null);
        setOrder(null);

        MyActivityManager.getInstance().clearAllActivity();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mApplication = this;

        initImageLoader(getApplicationContext());
        //初始化百度地图SDK
        SDKInitializer.initialize(getApplicationContext());

        //初始化极光服务
        JPushInterface.setDebugMode(CommonConstant.IS_DEBUG_MODE);
        JPushInterface.init(this);
        JPushInterface.setLatestNotificationNumber(this, 3);

        sharedPreferences = getSharedPreferences(GsApplication.class.getName(), 0);
        sharedPreferencesEditor = sharedPreferences.edit();
    }

    public static void initImageLoader(Context context) {
        // This configuration tuning is custom. You can tune every option, you may tune some of them,
        // or you can create default configuration by
        //  ImageLoaderConfiguration.createDefault(this);
        // method.
        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(context);
        config.threadPriority(Thread.NORM_PRIORITY - 2);
        config.denyCacheImageMultipleSizesInMemory();
        config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
        config.diskCacheSize(50 * 1024 * 1024); // 50 MiB
        config.tasksProcessingOrder(QueueProcessingType.LIFO);
        config.writeDebugLogs(); // Remove for release app

        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config.build());
    }

    public void setLatestAddress(Address latestAddress) {
        this.latestAddress = latestAddress;

        if (null != this.latestAddress && null != sharedPreferencesEditor) {
            sharedPreferencesEditor.putString("latestAddressLocationName", latestAddress.getLocationName());
            sharedPreferencesEditor.putString("latestAddressProvince", latestAddress.getProvince());
            sharedPreferencesEditor.putString("latestAddressCity", latestAddress.getCity());
            sharedPreferencesEditor.putString("latestAddressDistrict", latestAddress.getDistrict());
            sharedPreferencesEditor.putString("latestAddressStreet", latestAddress.getStreet());
            sharedPreferencesEditor.putString("latestAddressStreetNumber", latestAddress.getStreetNumber());
            sharedPreferencesEditor.putString("latestAddressAltitude", Double.toString(latestAddress.getAltitude()));
            sharedPreferencesEditor.putString("latestAddressLatitude", Double.toString(latestAddress.getLatitude()));
            sharedPreferencesEditor.putString("latestAddressLongitude", Double.toString(latestAddress.getLongitude()));

            sharedPreferencesEditor.commit();
        } // if (null != this.latestAddress && null != sharedPreferencesEditor)

    }

    public List<ScrollPager> getHomeBannerList() {
        return homeBannerList;
    }

    public void setHomeBannerList(List<ScrollPager> homeBannerList) {
        this.homeBannerList = homeBannerList;
    }

    public List<BaseEntity> getMsgList() {
        return msgList;
    }

    public void setMsgList(List<BaseEntity> msgList) {
        this.msgList = msgList;
    }

    public List<OrderEntity> getFinishedOrderList() {
        return finishedOrderList;
    }

    public void setFinishedOrderList(List<OrderEntity> finishedOrderList) {
        this.finishedOrderList = finishedOrderList;
    }

    public List<OrderEntity> getUnfinishedOrderList() {
        return unfinishedOrderList;
    }

    public void setUnfinishedOrderList(List<OrderEntity> unfinishedOrderList) {
        this.unfinishedOrderList = unfinishedOrderList;
    }

    public List<OrderEntity> getFinishedExpertOrderList() {
        return finishedExpertOrderList;
    }

    public void setFinishedExpertOrderList(List<OrderEntity> finishedExpertOrderList) {
        this.finishedExpertOrderList = finishedExpertOrderList;
    }

    public List<OrderEntity> getFinishedDoctorOrderList() {
        return finishedDoctorOrderList;
    }

    public void setFinishedDoctorOrderList(List<OrderEntity> finishedDoctorOrderList) {
        this.finishedDoctorOrderList = finishedDoctorOrderList;
    }

    public List<OrderEntity> getUnfinishedExpertOrderList() {
        return unfinishedExpertOrderList;
    }

    public void setUnfinishedExpertOrderList(List<OrderEntity> unfinishedExpertOrderList) {
        this.unfinishedExpertOrderList = unfinishedExpertOrderList;
    }

    public List<OrderEntity> getUnfinishedDoctorOrderList() {
        return unfinishedDoctorOrderList;
    }

    public void setUnfinishedDoctorOrderList(List<OrderEntity> unfinishedDoctorOrderList) {
        this.unfinishedDoctorOrderList = unfinishedDoctorOrderList;
    }

    public AlipayOrderEntity getAlipayOrderDetail() {
        return alipayOrderDetail;
    }

    public void setAlipayOrderDetail(AlipayOrderEntity alipayOrderDetail) {
        this.alipayOrderDetail = alipayOrderDetail;
    }

    public int getOrderDetailUserType() {
        return orderDetailUserType;
    }

    public void setOrderDetailUserType(int orderDetailUserType) {
        this.orderDetailUserType = orderDetailUserType;
    }

    public RepineEntity getRepine() {
        return repine;
    }

    public void setRepine(RepineEntity repine) {
        this.repine = repine;
    }

    public List<RepineEntity> getRepineList() {
        return repineList;
    }

    public void setRepineList(List<RepineEntity> repineList) {
        this.repineList = repineList;
    }

    public List<RepineEntity> getRepinedList() {
        return repinedList;
    }

    public void setRepinedList(List<RepineEntity> repinedList) {
        this.repinedList = repinedList;
    }

}
