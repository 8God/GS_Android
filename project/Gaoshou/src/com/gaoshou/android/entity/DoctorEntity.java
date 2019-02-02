package com.gaoshou.android.entity;

import java.util.List;

public class DoctorEntity {
    /*
     * 标识 id 用户标识 user_id 姓名 name 性别 sex 邮箱 email QQ qq 微信 weixin 手机 mobile 地址
     * address 医院 hosptital 科室 dept 职务 position 职称 title 城市标识 city_id 专长标识
     * expertise_id 用户填写专长 expertise 分类标识 category_id 身份证 identity 介绍 intro 评分次数
     * score_times 平均评分 avg_score 类型 type 经度 longitude 纬度 latitude 推荐
     * recommended 认证 certified 会诊费 consultation_fee 会诊手术费
     * consultation_operation_fee 状态 status 创建时间 created_at 修改时间 update_at 专业
     * expertise0 分类 category 城市 city 用户 user 医生文件 doctorFiles
     */

    private int id;
    private int user_id;
    private String name;
    private int sex;
    private String email;
    private String qq;
    private String weixin;
    private String mobile;
    private String address;
    private String currentAddress;
    private String hospital;
    private String dept;
    private String position;
    private String title;
    private int city_id;
    private int expertise_id;
    private String expertise;
    private int category_id;
    private String identity;
    private String intro;
    private int score_times;
    private String avg_score;
    private int type;
    private float longitude;
    private float latitude;
    private int recommended;
    private int certified;
    private String consultation_fee;
    private String consultation_operation_fee;
    private int status;
    private String created_at;
    private String updated_at;
    private ExpertiseEntity expertise0;
    private CategoryEntity category;
    private CityEntity city;
    private UserEntity user;
    private List<DoctorFileEntity> doctorFiles;

    private String headPicPath;
    private String certificationPath;
    private String identityPath;
    private String pswMD5;

    public DoctorEntity() {
        super();
    }

    public DoctorEntity(DoctorEntity doctor) {
        super();
        if (doctor != null) {
            this.id = doctor.getId();
            this.user_id = doctor.getUserId();
            this.name = doctor.getName();
            this.sex = doctor.getSex();
            this.email = doctor.getEmail();
            this.qq = doctor.getEmail();
            this.weixin = doctor.getWeixin();
            this.mobile = doctor.getMobile();
            this.address = doctor.getAddress();
            this.currentAddress = doctor.getCurrentAddress();
            this.hospital = doctor.getHospital();
            this.dept = doctor.getDept();
            this.position = doctor.getPosition();
            this.title = doctor.getTitle();
            this.city_id = doctor.getCityId();
            this.expertise_id = doctor.getExpertiseId();
            this.expertise = doctor.getExpertise();
            this.category_id = doctor.getCategoryId();
            this.identity = doctor.getIdentity();
            this.intro = doctor.getIntro();
            this.score_times = doctor.getScoreTimes();
            this.avg_score = doctor.getAvgScore();
            this.type = doctor.getType();
            this.longitude = doctor.getLongitude();
            this.latitude = doctor.getLatitude();
            this.recommended = doctor.getRecommended();
            this.certified = doctor.getCertified();
            this.consultation_fee = doctor.getConsultationFee();
            this.consultation_operation_fee = doctor.getConsultationOperationFee();
            this.status = doctor.getStatus();
            this.created_at = doctor.getCreatedAt();
            this.updated_at = doctor.getUpdatedAt();
            this.expertise0 = doctor.getExpertise0();
            this.category = doctor.getCategory();
            this.city = doctor.getCity();
            this.user = doctor.getUser();
            this.doctorFiles = doctor.getDoctorFiles();
            this.headPicPath = doctor.getHeadPicPath();
            this.certificationPath = doctor.getCertificationPath();
            this.identityPath = doctor.getIdentityPath();
            this.pswMD5 = doctor.getPswMD5();
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return user_id;
    }

    public void setUserId(int user_id) {
        this.user_id = user_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getQq() {
        return qq;
    }

    public void setQq(String qq) {
        this.qq = qq;
    }

    public String getWeixin() {
        return weixin;
    }

    public void setWeixin(String weixin) {
        this.weixin = weixin;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCurrentAddress() {
        return currentAddress;
    }

    public void setCurrentAddress(String currentAddress) {
        this.currentAddress = currentAddress;
    }

    public String getHospital() {
        return hospital;
    }

    public void setHospital(String hospital) {
        this.hospital = hospital;
    }

    public String getDept() {
        return dept;
    }

    public void setDept(String dept) {
        this.dept = dept;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getCityId() {
        return city_id;
    }

    public void setCityId(int city_id) {
        this.city_id = city_id;
    }

    public int getExpertiseId() {
        return expertise_id;
    }

    public void setExpertiseId(int expertise_id) {
        this.expertise_id = expertise_id;
    }

    public String getExpertise() {
        return expertise;
    }

    public void setExpertise(String expertise) {
        this.expertise = expertise;
    }

    public int getCategoryId() {
        return category_id;
    }

    public void setCategoryId(int category_id) {
        this.category_id = category_id;
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public int getScoreTimes() {
        return score_times;
    }

    public void setScoreTimes(int score_times) {
        this.score_times = score_times;
    }

    public String getAvgScore() {
        return avg_score;
    }

    public void setAvgScore(String avg_score) {
        this.avg_score = avg_score;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public int getRecommended() {
        return recommended;
    }

    public void setRecommended(int recommended) {
        this.recommended = recommended;
    }

    public int getCertified() {
        return certified;
    }

    public void setCertified(int certified) {
        this.certified = certified;
    }

    public String getConsultationFee() {
        return consultation_fee;
    }

    public void setConsultationFee(String consultation_fee) {
        this.consultation_fee = consultation_fee;
    }

    public String getConsultationOperationFee() {
        return consultation_operation_fee;
    }

    public void setConsultationOperationFee(String consultation_operation_fee) {
        this.consultation_operation_fee = consultation_operation_fee;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getCreatedAt() {
        return created_at;
    }

    public void setCreatedAt(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdatedAt() {
        return updated_at;
    }

    public void setUpdatedAt(String updated_at) {
        this.updated_at = updated_at;
    }

    public ExpertiseEntity getExpertise0() {
        return expertise0;
    }

    public void setExpertise0(ExpertiseEntity expertise0) {
        this.expertise0 = expertise0;
    }

    public CategoryEntity getCategory() {
        return category;
    }

    public void setCategory(CategoryEntity category) {
        this.category = category;
    }

    public CityEntity getCity() {
        return city;
    }

    public void setCity(CityEntity city) {
        this.city = city;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public List<DoctorFileEntity> getDoctorFiles() {
        return doctorFiles;
    }

    public void setDoctorFiles(List<DoctorFileEntity> doctorFiles) {
        this.doctorFiles = doctorFiles;
    }

    public String getHeadPicPath() {
        return headPicPath;
    }

    public void setHeadPicPath(String headPicPath) {
        this.headPicPath = headPicPath;
    }

    public String getCertificationPath() {
        return certificationPath;
    }

    public void setCertificationPath(String certificationPath) {
        this.certificationPath = certificationPath;
    }

    public String getIdentityPath() {
        return identityPath;
    }

    public void setIdentityPath(String identityPath) {
        this.identityPath = identityPath;
    }

    public String getPswMD5() {
        return pswMD5;
    }

    public void setPswMD5(String pswMD5) {
        this.pswMD5 = pswMD5;
    }

    @Override
    public String toString() {
        return "DoctorEntity [id=" + id + ", user_id=" + user_id + ", name=" + name + ", sex=" + sex + ", email=" + email + ", qq=" + qq + ", weixin=" + weixin + ", address=" + address + ", hospital=" + hospital + ", dept=" + dept + ", position=" + position + ", title=" + title + ", city_id=" + city_id + ", expertise_id=" + expertise_id + ", expertise=" + expertise + ", category_id=" + category_id + ", identity=" + identity + ", intro=" + intro + ", score_times=" + score_times + ", avg_score=" + avg_score + ", type=" + type + ", longitude=" + longitude + ", latitude=" + latitude + ", recommended=" + recommended + ", certified=" + certified + ", consultation_fee=" + consultation_fee + ", consultation_operation_fee=" + consultation_operation_fee + ", status=" + status + ", created_at=" + created_at + ", updated_at=" + updated_at + ", expertise0=" + expertise0 + ", category=" + category + ", city=" + city + ", user=" + user + ", doctorFiles=" + doctorFiles + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((address == null) ? 0 : address.hashCode());
        result = prime * result + ((avg_score == null) ? 0 : avg_score.hashCode());
        result = prime * result + ((category == null) ? 0 : category.hashCode());
        result = prime * result + category_id;
        result = prime * result + certified;
        result = prime * result + ((city == null) ? 0 : city.hashCode());
        result = prime * result + city_id;
        result = prime * result + ((consultation_fee == null) ? 0 : consultation_fee.hashCode());
        result = prime * result + ((consultation_operation_fee == null) ? 0 : consultation_operation_fee.hashCode());
        result = prime * result + ((created_at == null) ? 0 : created_at.hashCode());
        result = prime * result + ((dept == null) ? 0 : dept.hashCode());
        result = prime * result + ((doctorFiles == null) ? 0 : doctorFiles.hashCode());
        result = prime * result + ((email == null) ? 0 : email.hashCode());
        result = prime * result + ((expertise == null) ? 0 : expertise.hashCode());
        result = prime * result + ((expertise0 == null) ? 0 : expertise0.hashCode());
        result = prime * result + expertise_id;
        result = prime * result + ((hospital == null) ? 0 : hospital.hashCode());
        result = prime * result + id;
        result = prime * result + ((identity == null) ? 0 : identity.hashCode());
        result = prime * result + ((intro == null) ? 0 : intro.hashCode());
        result = prime * result + Float.floatToIntBits(latitude);
        result = prime * result + Float.floatToIntBits(longitude);
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((position == null) ? 0 : position.hashCode());
        result = prime * result + ((qq == null) ? 0 : qq.hashCode());
        result = prime * result + recommended;
        result = prime * result + score_times;
        result = prime * result + sex;
        result = prime * result + status;
        result = prime * result + ((title == null) ? 0 : title.hashCode());
        result = prime * result + type;
        result = prime * result + ((updated_at == null) ? 0 : updated_at.hashCode());
        result = prime * result + ((user == null) ? 0 : user.hashCode());
        result = prime * result + user_id;
        result = prime * result + ((weixin == null) ? 0 : weixin.hashCode());
        result = prime * result + ((headPicPath == null) ? 0 : headPicPath.hashCode());
        result = prime * result + ((certificationPath == null) ? 0 : certificationPath.hashCode());
        result = prime * result + ((identityPath == null) ? 0 : identityPath.hashCode());
        result = prime * result + ((pswMD5 == null) ? 0 : pswMD5.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        DoctorEntity other = (DoctorEntity) obj;
        if (address == null) {
            if (other.address != null)
                return false;
        } else if (!address.equals(other.address))
            return false;
        if (avg_score == null) {
            if (other.avg_score != null)
                return false;
        } else if (!avg_score.equals(other.avg_score))
            return false;
        if (category == null) {
            if (other.category != null)
                return false;
        } else if (!category.equals(other.category))
            return false;
        if (category_id != other.category_id)
            return false;
        if (certified != other.certified)
            return false;
        if (city == null) {
            if (other.city != null)
                return false;
        } else if (!city.equals(other.city))
            return false;
        if (city_id != other.city_id)
            return false;
        if (consultation_fee == null) {
            if (other.consultation_fee != null)
                return false;
        } else if (!consultation_fee.equals(other.consultation_fee))
            return false;
        if (consultation_operation_fee == null) {
            if (other.consultation_operation_fee != null)
                return false;
        } else if (!consultation_operation_fee.equals(other.consultation_operation_fee))
            return false;
        if (created_at == null) {
            if (other.created_at != null)
                return false;
        } else if (!created_at.equals(other.created_at))
            return false;
        if (dept == null) {
            if (other.dept != null)
                return false;
        } else if (!dept.equals(other.dept))
            return false;
        if (doctorFiles == null) {
            if (other.doctorFiles != null)
                return false;
        } else if (!doctorFiles.equals(other.doctorFiles))
            return false;
        if (email == null) {
            if (other.email != null)
                return false;
        } else if (!email.equals(other.email))
            return false;
        if (expertise == null) {
            if (other.expertise != null)
                return false;
        } else if (!expertise.equals(other.expertise))
            return false;
        if (expertise0 == null) {
            if (other.expertise0 != null)
                return false;
        } else if (!expertise0.equals(other.expertise0))
            return false;
        if (expertise_id != other.expertise_id)
            return false;
        if (hospital == null) {
            if (other.hospital != null)
                return false;
        } else if (!hospital.equals(other.hospital))
            return false;
        if (id != other.id)
            return false;
        if (identity == null) {
            if (other.identity != null)
                return false;
        } else if (!identity.equals(other.identity))
            return false;
        if (intro == null) {
            if (other.intro != null)
                return false;
        } else if (!intro.equals(other.intro))
            return false;
        if (Float.floatToIntBits(latitude) != Float.floatToIntBits(other.latitude))
            return false;
        if (Float.floatToIntBits(longitude) != Float.floatToIntBits(other.longitude))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (position == null) {
            if (other.position != null)
                return false;
        } else if (!position.equals(other.position))
            return false;
        if (qq == null) {
            if (other.qq != null)
                return false;
        } else if (!qq.equals(other.qq))
            return false;
        if (recommended != other.recommended)
            return false;
        if (score_times != other.score_times)
            return false;
        if (sex != other.sex)
            return false;
        if (status != other.status)
            return false;
        if (title == null) {
            if (other.title != null)
                return false;
        } else if (!title.equals(other.title))
            return false;
        if (type != other.type)
            return false;
        if (updated_at == null) {
            if (other.updated_at != null)
                return false;
        } else if (!updated_at.equals(other.updated_at))
            return false;
        if (user == null) {
            if (other.user != null)
                return false;
        } else if (!user.equals(other.user))
            return false;
        if (user_id != other.user_id)
            return false;
        if (weixin == null) {
            if (other.weixin != null)
                return false;
        } else if (!weixin.equals(other.weixin))
            return false;
        if (headPicPath == null) {
            if (other.headPicPath != null)
                return false;
        } else if (!headPicPath.equals(other.headPicPath))
            return false;
        if (certificationPath == null) {
            if (other.certificationPath != null)
                return false;
        } else if (!certificationPath.equals(other.certificationPath))
            return false;
        if (identityPath == null) {
            if (other.identityPath != null)
                return false;
        } else if (!identityPath.equals(other.identityPath))
            return false;
        if (pswMD5 == null) {
            if (other.pswMD5 != null)
                return false;
        } else if (!pswMD5.equals(other.pswMD5))
            return false;
        return true;
    }
    
    public boolean equalsWithoutDoctorFiles(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        DoctorEntity other = (DoctorEntity) obj;
        if (address == null) {
            if (other.address != null)
                return false;
        } else if (!address.equals(other.address))
            return false;
        if (avg_score == null) {
            if (other.avg_score != null)
                return false;
        } else if (!avg_score.equals(other.avg_score))
            return false;
        if (category == null) {
            if (other.category != null)
                return false;
        } else if (!category.equals(other.category))
            return false;
        if (category_id != other.category_id)
            return false;
        if (certified != other.certified)
            return false;
        if (city == null) {
            if (other.city != null)
                return false;
        } else if (!city.equals(other.city))
            return false;
        if (city_id != other.city_id)
            return false;
        if (consultation_fee == null) {
            if (other.consultation_fee != null)
                return false;
        } else if (!consultation_fee.equals(other.consultation_fee))
            return false;
        if (consultation_operation_fee == null) {
            if (other.consultation_operation_fee != null)
                return false;
        } else if (!consultation_operation_fee.equals(other.consultation_operation_fee))
            return false;
        if (created_at == null) {
            if (other.created_at != null)
                return false;
        } else if (!created_at.equals(other.created_at))
            return false;
        if (dept == null) {
            if (other.dept != null)
                return false;
        } else if (!dept.equals(other.dept))
            return false;
        if (email == null) {
            if (other.email != null)
                return false;
        } else if (!email.equals(other.email))
            return false;
        if (expertise == null) {
            if (other.expertise != null)
                return false;
        } else if (!expertise.equals(other.expertise))
            return false;
        if (expertise0 == null) {
            if (other.expertise0 != null)
                return false;
        } else if (!expertise0.equals(other.expertise0))
            return false;
        if (expertise_id != other.expertise_id)
            return false;
        if (hospital == null) {
            if (other.hospital != null)
                return false;
        } else if (!hospital.equals(other.hospital))
            return false;
        if (id != other.id)
            return false;
        if (identity == null) {
            if (other.identity != null)
                return false;
        } else if (!identity.equals(other.identity))
            return false;
        if (intro == null) {
            if (other.intro != null)
                return false;
        } else if (!intro.equals(other.intro))
            return false;
        if (Float.floatToIntBits(latitude) != Float.floatToIntBits(other.latitude))
            return false;
        if (Float.floatToIntBits(longitude) != Float.floatToIntBits(other.longitude))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (position == null) {
            if (other.position != null)
                return false;
        } else if (!position.equals(other.position))
            return false;
        if (qq == null) {
            if (other.qq != null)
                return false;
        } else if (!qq.equals(other.qq))
            return false;
        if (recommended != other.recommended)
            return false;
        if (score_times != other.score_times)
            return false;
        if (sex != other.sex)
            return false;
        if (status != other.status)
            return false;
        if (title == null) {
            if (other.title != null)
                return false;
        } else if (!title.equals(other.title))
            return false;
        if (type != other.type)
            return false;
        if (updated_at == null) {
            if (other.updated_at != null)
                return false;
        } else if (!updated_at.equals(other.updated_at))
            return false;
        if (user == null) {
            if (other.user != null)
                return false;
        } else if (!user.equals(other.user))
            return false;
        if (user_id != other.user_id)
            return false;
        if (weixin == null) {
            if (other.weixin != null)
                return false;
        } else if (!weixin.equals(other.weixin))
            return false;
        if (pswMD5 == null) {
            if (other.pswMD5 != null)
                return false;
        } else if (!pswMD5.equals(other.pswMD5))
            return false;
        return true;
    }

}