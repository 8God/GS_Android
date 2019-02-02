package com.gaoshou.common.constant;

public class APIKey {

    public static final int TYEP_EXPERT = 1;//专家
    public static final int TYEP_DOCTOR = 2;//医生

    public static final int SEX_MALE = 1;//男
    public static final int SEX_FEMALE = 2;//女

    public static final int DOCTOR_FILE_TYEP_HEAD_PIC = 1; //医生头像
    public static final int DOCTOR_FILE_TYEP_CERTIFICATION = 2;//医生职业资格证
    public static final int DOCTOR_FILE_TYEP_IDENTITY = 3; //医生身份证
    
    public static final int CONSULTATION_FILE_TYPE_BODY_SIGN = 1;//体征
    public static final int CONSULTATION_FILE_TYPE_ASSAY = 2;//化验
    public static final int CONSULTATION_FILE_TYPE_SCREENAGE = 3;//影像
    
    public static final int CONSULTATION_TIMELY_NO = 0;
    public static final int CONSULTATION_TIMELY_YES = 1;

    public static final int STATUS_SUCCESSFUL = 0;//返回结果成功的状态值
    public static final int COMMON_STATUS_LEGAL = 1;//所有数据合法的状态，用于网络请求
    public static final String SORT_ASC = "asc"; //递增排序
    public static final String SORT_DESC = "desc"; //递减排序
    public static final String PAGE_TYPE_ALL = "all";
    public static final String PAGE_TYPE_PAGE = "page";

    public static final String COMMON_ID = "id";
    public static final String COMMON_STATUS = "status";
    public static final String COMMON_MESSAGE = "message";
    public static final String COMMON_RESULT = "result";
    public static final String COMMON_OFFSET = "offset";
    public static final String COMMON_ALL = "all";
    public static final String COMMON_PAGE = "page";
    public static final String COMMON_ALL_OR_PAGE = "allOrPage";
    public static final String COMMON_PAGE_SIZE = "page_size";
    public static final String COMMON_SORT_TYPES = "sort_types";
    public static final String COMMON_SORT_FIELDS = "sort_fields";
    public static final String COMMON_SORT = "sort";
    public static final String COMMON_ALLORPAGE = "allOrPage";
    public static final String COMMON_CREATED_AT = "created_at"; //创建时间
    public static final String COMMON_UPDATED_AT = "updated_at"; //更新时间
    public static final String COMMON_TO_BE_CONTINUED = "to_be_continued";
    public static final String COMMON_PATH = "path";
    public static final String COMMON_TYPE = "type";
    public static final String COMMON_NAME = "name";
    public static final String COMMON_CONTENT = "content";
    public static final String COMMON_URI = "uri";
    public static final String COMMON_EMAIL = "email";
    public static final String COMMON_MOBILE = "mobile";
    public static final String COMMON_DOCTOR_ID = "doctor_id";
    public static final String COMMON_DOCTOR = "doctor";
    public static final String COMMON_DOCTOR_FILES = "doctorFiles";
    public static final String COMMON_EXPERT_ID = "expert_id";
    public static final String COMMON_EXPERT = "expert";
    public static final String COMMON_ANAMNESIS = "anamnesis";
    public static final String COMMON_SYMPTOM = "symptom";
    public static final String COMMON_CITY = "city";
    public static final String COMMON_USER = "user";
    public static final String COMMON_CONSULTATION_ID = "consultation_id";
    public static final String COMMON_CONSULTATION = "consultation";
    public static final String COMMON_CONSULTATION_FILES = "consultationFiles";
    public static final String COMMON_EVALUATES = "evaluates";
    public static final String COMMON_FAVORITES = "favorites";
    public static final String COMMON_ORDER_ID = "order_id";
    public static final String COMMON_ORDER = "order";
    public static final String COMMON_ORDERS = "orders";
    public static final String COMMON_SUCCESS = "success";
    public static final String COMMON_STATUS_CODE = "statusCode";
    public static final String COMMON_DATA = "data";
    public static final String COMMON_IMTES = "items";
    public static final String COMMON_LINKS = "_links";
    public static final String COMMON_META = "_meta";
    public static final String COMMON_CURRENT_PAGE = "currentPage";
    public static final String COMMON_TOTAL_COUNT = "totalCount";
    public static final String COMMON_EXPAND = "expand";
    public static final String COMMON_PAGE_COUNT = "pageCount";
    public static final String COMMON_ACTION = "action";
    public static final String COMMON_WHERE_STATUS = "where[status]";
    public static final String COMMON_JOIN = "join"; //二级筛选用到
    public static final String COMMON_DISTANCE = "distance";
    public static final String COMMON_WHERE_ORDER_ID = "where[order_id]";
    public static final String COMMON_WHERE_DOCOTR_ID = "where[doctor_id]";
    //文件上传
    public static final String COMMON_FILES="files";
    public static final String COMMON_FILES_FIELD="filesField";
    public static final String COMMON_MULTI_FIELDS="multiFields";

    //用户字段
    public static final String USER = "user";
    public static final String USERS = "users";
    public static final String USER_ID = "user_id";
    public static final String USER_NICKNAME = "nickname";
    public static final String USER_USERNAME = "username";
    public static final String USER_BIRTHDAY = "birthday";
    public static final String USER_CITY = "city";
    public static final String USER_WEIXIN = "weixin";
    public static final String USER_QQ = "qq";
    public static final String USER_WEIBO = "weibo";
    public static final String USER_CREATED_AT = "created_at";
    public static final String USER_LEVEL = "level";
    public static final String USER_SEX = "sex";
    public static final String USER_MARITAL_STATUS = "marital_status";
    public static final String USER_COLLECTIONS_COUNT = "collections_count";
    public static final String USER_FANS_COUNT = "fans_count";
    public static final String USER_FOLLOWS_COUNT = "follows_count";
    public static final String USER_THREADS_COUNT = "threads_count";
    public static final String USER_PASSWORD = "password";

    //版本
    public static final String VERSION_NAME = "version_name";
    public static final String VERSION_CODE = "version_code";
    public static final String PACKAGE_NAME = "package_name";
    public static final String DOWNLOAD_URL = "download_url";
    public static final String VERSION_DES = "version_des";
    public static final String VERSIONS = "versions";

    //医生字段
    public static final String DOCTOR = "doctor";
    public static final String DOCTOR_TYPE = "type";
    public static final String DOCTOR_SEX = "sex";
    public static final String DOCTOR_QQ = "qq";
    public static final String DOCTOR_WEIXIN = "weixin";
    public static final String DOCTOR_ADDRESS = "address";
    public static final String DOCTOR_PHONE = "phone";
    public static final String DOCTOR_HOSPITAL = "hospital";
    public static final String DOCTOR_DEPT = "dept";
    public static final String DOCTOR_POSITION = "position";
    public static final String DOCTOR_TITLE = "title";
    public static final String DOCTOR_CITY_ID = "city_id";
    public static final String DOCTOR_EXPERTISE_ID = "expertise_id";
    public static final String DOCTOR_CATEGORY_ID = "category_id";
    public static final String DOCTOR_IDENTITY = "identity";
    public static final String DOCTOR_INTRO = "intro";
    public static final String DOCTOR_SCORE_TIMES = "score_times";
    public static final String DOCTOR_AVG_SCORE = "avg_score";
    public static final String DOCTOR_LONGITUDE = "longitude";
    public static final String DOCTOR_LATITUDE = "latitude";
    public static final String DOCTOR_RECOMMENDED = "recommended";
    public static final String DOCTOR_CERTIFIED = "certified";
    public static final String DOCTOR_CONSULTATION_FEE = "consultation_fee";
    public static final String DOCTOR_CONSULTATION_OPERATION_FEE = "consultation_operation_fee";
    public static final String DOCTOR_EXPERTISE = "expertise";
    public static final String DOCTOR_EXPERTISE0 = "expertise0";
    public static final String DOCTOR_CATEGORY = "category";
    public static final String DOCTOR_HEAD_PIC = "doctor_head_pic";
    public static final String DOCTOR_CERTIFICATION_PATH = "doctor_certification_path";
    public static final String DOCTOR_IDENTITY_PATH = "doctor_identity_path";
    public static final String DOCTOR_CURRENT_ADDRESS = "current_address";
    public static final String DOCTOR_FILE_HEAD_PHOTO_ID = "doctorFile_head_photo_id";
    public static final String DOCTOR_FILE_CERTIFICATE_ID = "doctorFile_certificate_id";
    public static final String DOCTOR_FILE_ID_PHOTO_ID = "doctorFile_id_photo_id";
    public static final String DOCTOR_SEARCH_CITY_ID = "DoctorSearch[city_id]";
    public static final String DOCTOR_SEARCH_EXPERTISE_ID = "DoctorSearch[expertise_id]";
    public static final String DOCTOR_SEARCH_LONGITUDE = "DoctorSearch[longitude]";
    public static final String DOCTOR_SEARCH_LATITUDE = "DoctorSearch[latitude]";
    public static final String DOCTOR_SEARCH_CATEGORY_ID = "DoctorSearch[category_id]";
    public static final String DOCTOR_WHERE_DOCTOR_ID = "where[doctor.id]";

    //专家
    public static final String EXPERT_SEARCH_CITY_ID = "ExpertSearch[city_id]";
    public static final String EXPERT_SEARCH_EXPERTISE_ID = "ExpertSearch[expertise_id]";
    public static final String EXPERT_SEARCH_LONGITUDE = "ExpertSearch[longitude]";
    public static final String EXPERT_SEARCH_LATITUDE = "ExpertSearch[latitude]";
    public static final String EXPERT_SEARCH_CATEGORY_ID = "ExpertSearch[category_id]";
    public static final String EXPERT = "expert";
    //广告
    public static final String APP_AD_PATH = "path";
    public static final String APP_AD_TITLE = "title";
    public static final String APP_AD_SEARCH_TYPE = "AdvertisementSearch[type]";

    //分类
    public static final String CAETEGORY_CATEGORY_ID = "category_id";
    public static final String CAETEGORY_NAME = "name";

    //会诊
    public static final String CONSULTATION = "consultation";
    public static final String CONSULTATION_PATIENT_NAME = "patient_name";
    public static final String CONSULTATION_PATIENT_SEX = "patient_sex";
    public static final String CONSULTATION_PATIENT_AGE = "patient_age";
    public static final String CONSULTATION_PATIENT_MOBILE = "patient_mobile";
    public static final String CONSULTATION_PATIENT_ADDRESS = "patient_address";
    public static final String CONSULTATION_PATIENT_HOSPITAL = "patient_hospital";
    public static final String CONSULTATION_PATIENT_DEPT = "patient_dept";
    public static final String CONSULTATION_PATIENT_CITY = "patient_city";
    public static final String CONSULTATION_PATIENT_ILLNESS = "patient_illness";
    public static final String CONSULTATION_ANAMNESIS_ID = "anamnesis_id";
    public static final String CONSULTATION_SYMPTOM_ID = "symptom_id";
    public static final String CONSULTATION_PATIENT_REMARK = "remark";
    public static final String CONSULTATION_PURPOSE = "purpose";
    public static final String CONSULTATION_TIMELY = "timely";
    public static final String CONSULTATION_OTHER_ORDER = "other_order";
    public static final String CONSULTATION_ORDER_AT = "order_at";
    public static final String CONSULTATION_SEARCH_EXPERT_ID = "ConsultationSearch[expert_id]";
    public static final String CONSULTATION_SEARCH_DOCTOR_ID = "ConsultationSearch[doctor_id]";
    public static final String CONSULTATION_SEARCH_STATUS = "ConsultationSearch[status]";
    public static final String CONSULTATION_WHERE_EXPERTISE_ID = "where[expertise.id]";
    public static final String CONSULTATION_WHERE_STATUS = "where[consultation.status]";
    public static final String CONSULTATION_WHERE_TIMELY = "where[consultation.timely]";

    //会诊文件
    public static final String CONSULTATION_FILE_TYPE = "type";
    public static final String CONSULTATION_FILES = "consultationFiles";

    //评价
    public static final String EVALUATE_EVALUATED_DOCTOR_ID = "evaluated_doctor_id";
    public static final String EVALUATE_SCORE = "score";
    public static final String EVALUATE_EVALUATED_DOCTOR = "evaluatedDoctor";
    public static final String EVALUATE_SEARCH_EVALUATED_DOCTOR_ID = "EvaluateSearch[evaluated_doctor_id]";
    public static final String EVALUATE_WHERE_DOCTOR_ID = "where[doctor_id]";

    //收藏
    public static final String FAVORITE_FAVORITE_DOCTOR_ID = "favorite_doctor_id";
    public static final String FAVORITE_FAVORITE_DOCTOR = "favoriteDoctor";
    public static final String FAVORITE_FAVORITE_SEARCH_DOCTOR_ID = "FavoriteSearch[doctor_id]";
    public static final String FAVORITE_WHERE_FAVORITE_DOCTOR_ID = "where[favorite.doctor_id]";

    //订单
    public static final String ORDER_REMARK = "remark";
    public static final String ORDER_ORDER_DOCTOR_ID = "order_doctor_id";
    public static final String ORDER_ORDER_DOCTOR = "orderDoctor";
    public static final String ORDER_SEARCH_DOCTOR_ID = "OrderSearch[doctor_id]";
    public static final String ORDER_SEARCH_ORDER_DOCTOR_ID = "OrderSearch[order_doctor_id]";
    public static final String ORDER_SEARCH_STATUS = "OrderSearch[status]";
    public static final String ORDER_SEARCH_CONSULTATION_ID = "OrderSearch[consultation_id]";
    public static final String ORDER_WHERE_ID = "where[order.id]";
    public static final String ORDER_WHERE_STATUS = "where[order.status]";
    public static final String ORDER_WHERE_DOCTOR_ID = "where[order.doctor_id]";
    public static final String ORDER_WHERE_ORDER_DOCTOR_ID = "where[order.order_doctor_id]";

    //投诉
    public static final String REPINE_REPINED_DOCTOR_ID = "repined_doctor_id";
    public static final String REPINE_REPINED_DOCTOR = "repinedDoctor";
    public static final String REPINE_WHERE_REPINED_DOCTOR_ID = "where[repined_doctor_id]";

    //版本
    public static final String VERSION_PACKAGE_NAME = "package_name";
    public static final String VERSION_VERSION_NAME = "version_name";
    public static final String VERSION_VERSION_CODE = "version_code";
    public static final String VERSION_VERSION_DESC = "version_desc";

    //验证码
    public static final String VERIFY_CODE = "verifyCode";
}
