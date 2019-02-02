package com.gaoshou.common.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.util.Log;

import com.gaoshou.android.entity.AnamnesisEntity;
import com.gaoshou.android.entity.AppadEntity;
import com.gaoshou.android.entity.CategoryEntity;
import com.gaoshou.android.entity.CityEntity;
import com.gaoshou.android.entity.ConsultationEntity;
import com.gaoshou.android.entity.ConsultationFileEntity;
import com.gaoshou.android.entity.DoctorEntity;
import com.gaoshou.android.entity.DoctorFileEntity;
import com.gaoshou.android.entity.EvaluateEntity;
import com.gaoshou.android.entity.ExpertiseEntity;
import com.gaoshou.android.entity.FavoriteEntity;
import com.gaoshou.android.entity.OrderEntity;
import com.gaoshou.android.entity.RepineEntity;
import com.gaoshou.android.entity.SymptomEntity;
import com.gaoshou.android.entity.UserEntity;
import com.gaoshou.android.entity.VersionEntity;
import com.gaoshou.common.component.ScrollPagerEntity;
import com.gaoshou.common.constant.APIKey;

public class EntityUtils {

    public static UserEntity getUserEntity(Map<String, Object> userMap) {
        UserEntity user = null;
        if (null != userMap) {
            if (userMap.get(APIKey.COMMON_ID) != null) {
                user = new UserEntity();

                user.setId(TypeUtil.getInteger(userMap.get(APIKey.COMMON_ID)));
                user.setStatus(TypeUtil.getInteger(userMap.get(APIKey.COMMON_STATUS), 0));
                user.setUsername(TypeUtil.getString(userMap.get(APIKey.USER_USERNAME), ""));
                user.setMobile(TypeUtil.getString(userMap.get(APIKey.COMMON_MOBILE), ""));
                user.setCreatedAt(TypeUtil.getString(userMap.get(APIKey.USER_CREATED_AT), ""));
                user.setUpdatedAt(TypeUtil.getString(userMap.get(APIKey.COMMON_UPDATED_AT), ""));
                user.setEmail(TypeUtil.getString(userMap.get(APIKey.COMMON_EMAIL), ""));
            }

        }
        return user;

    }

    public static DoctorEntity getDoctorEntity(Map<String, Object> doctorMap) {
        if (null != doctorMap) {
            DoctorEntity doctor = new DoctorEntity();
            doctor.setId(TypeUtil.getInteger(doctorMap.get(APIKey.COMMON_ID)));
            doctor.setUserId(TypeUtil.getInteger(doctorMap.get(APIKey.USER_ID), 0));
            doctor.setName(TypeUtil.getString(doctorMap.get(APIKey.COMMON_NAME)));
            doctor.setSex(TypeUtil.getInteger(doctorMap.get(APIKey.DOCTOR_SEX), 0));
            doctor.setEmail(TypeUtil.getString(doctorMap.get(APIKey.COMMON_EMAIL)));
            doctor.setQq(TypeUtil.getString(doctorMap.get(APIKey.DOCTOR_QQ)));
            doctor.setWeixin(TypeUtil.getString(doctorMap.get(APIKey.DOCTOR_WEIXIN)));
            doctor.setMobile(TypeUtil.getString(doctorMap.get(APIKey.COMMON_MOBILE)));
            doctor.setAddress(TypeUtil.getString(doctorMap.get(APIKey.DOCTOR_ADDRESS)));
            doctor.setCurrentAddress(TypeUtil.getString(doctorMap.get(APIKey.DOCTOR_CURRENT_ADDRESS)));
            doctor.setHospital(TypeUtil.getString(doctorMap.get(APIKey.DOCTOR_HOSPITAL)));
            doctor.setDept(TypeUtil.getString(doctorMap.get(APIKey.DOCTOR_DEPT)));
            doctor.setPosition(TypeUtil.getString(doctorMap.get(APIKey.DOCTOR_POSITION)));
            doctor.setTitle(TypeUtil.getString(doctorMap.get(APIKey.DOCTOR_TITLE)));
            doctor.setCityId(TypeUtil.getInteger(doctorMap.get(APIKey.DOCTOR_CITY_ID), 0));
            doctor.setExpertiseId(TypeUtil.getInteger(doctorMap.get(APIKey.DOCTOR_EXPERTISE_ID), 0));
            doctor.setCategoryId(TypeUtil.getInteger(doctorMap.get(APIKey.DOCTOR_CATEGORY_ID), 0));
            doctor.setIdentity(TypeUtil.getString(doctorMap.get(APIKey.DOCTOR_IDENTITY)));
            doctor.setIntro(TypeUtil.getString(doctorMap.get(APIKey.DOCTOR_INTRO)));
            doctor.setScoreTimes(TypeUtil.getInteger(doctorMap.get(APIKey.DOCTOR_SCORE_TIMES), 0));
            doctor.setAvgScore(TypeUtil.getString(doctorMap.get(APIKey.DOCTOR_AVG_SCORE)));
            doctor.setType(TypeUtil.getInteger(doctorMap.get(APIKey.COMMON_TYPE), 0));
            doctor.setLongitude(TypeUtil.getFloat(doctorMap.get(APIKey.DOCTOR_LONGITUDE), 0f));
            doctor.setLatitude(TypeUtil.getFloat(doctorMap.get(APIKey.DOCTOR_LATITUDE), 0f));
            doctor.setRecommended(TypeUtil.getInteger(doctorMap.get(APIKey.DOCTOR_RECOMMENDED), 0));
            doctor.setCertified(TypeUtil.getInteger(doctorMap.get(APIKey.DOCTOR_CERTIFIED), 0));
            doctor.setConsultationFee(TypeUtil.getString(doctorMap.get(APIKey.DOCTOR_CONSULTATION_FEE)));
            doctor.setConsultationOperationFee(TypeUtil.getString(doctorMap.get(APIKey.DOCTOR_CONSULTATION_OPERATION_FEE)));
            doctor.setStatus(TypeUtil.getInteger(doctorMap.get(APIKey.COMMON_STATUS), -1));
            doctor.setCreatedAt(TypeUtil.getString(doctorMap.get(APIKey.COMMON_CREATED_AT)));
            doctor.setUpdatedAt(TypeUtil.getString(doctorMap.get(APIKey.COMMON_UPDATED_AT)));
            doctor.setExpertise0(getExpertiseEntity(TypeUtil.getMap(doctorMap.get(APIKey.DOCTOR_EXPERTISE0))));
            doctor.setExpertise(TypeUtil.getString(doctorMap.get(APIKey.DOCTOR_EXPERTISE)));
            doctor.setCategory(getCategoryEntity(TypeUtil.getMap(doctorMap.get(APIKey.DOCTOR_CATEGORY))));
            doctor.setCity(getCityEntity(TypeUtil.getMap(doctorMap.get(APIKey.COMMON_CITY))));
            doctor.setUser(getUserEntity(TypeUtil.getMap(doctorMap.get(APIKey.COMMON_USER))));
            List<DoctorFileEntity> doctorFileList = getDoctorFileEntityList(TypeUtil.getList(doctorMap.get(APIKey.COMMON_DOCTOR_FILES)));
            if (null != doctorFileList) {
                doctor.setDoctorFiles(doctorFileList);

                for (int i = 0; i < doctorFileList.size(); i++) {
                    DoctorFileEntity doctorFileEntity = doctorFileList.get(i);
                    if (null != doctorFileEntity) {
                        int type = doctorFileEntity.getType();
                        switch (type) {
                            case APIKey.DOCTOR_FILE_TYEP_HEAD_PIC:
                                doctor.setHeadPicPath(doctorFileEntity.getPath());
                                break;
                            case APIKey.DOCTOR_FILE_TYEP_CERTIFICATION:
                                doctor.setCertificationPath(doctorFileEntity.getPath());
                                break;
                            case APIKey.DOCTOR_FILE_TYEP_IDENTITY:
                                doctor.setIdentityPath(doctorFileEntity.getPath());
                                break;
                            default:
                                break;
                        }
                    }
                }
            }
//            Log.i("testYJ", doctor.toString());
            return doctor;
        }
        return null;
    }

    public static List<DoctorEntity> getDoctorEntityList(List<Map<String, Object>> rawDoctorList) {
        List<DoctorEntity> doctorEntityList = new ArrayList<DoctorEntity>();
        if (null != rawDoctorList) {
            for (int i = 0; i < rawDoctorList.size(); i++) {
                Map<String, Object> rawDoctor = rawDoctorList.get(i);
                DoctorEntity doctor = getDoctorEntity(rawDoctor);
                if (null != doctor) {
                    doctorEntityList.add(doctor);
                }
            }

        }
        return doctorEntityList;

    }

    public static DoctorFileEntity getDoctorFileEntity(Map<String, Object> doctorFileEntityMap) {
        if (doctorFileEntityMap != null) {
            DoctorFileEntity doctorFile = new DoctorFileEntity();
            doctorFile.setId(TypeUtil.getInteger(doctorFileEntityMap.get(APIKey.COMMON_ID), 0));
            doctorFile.setDoctor_id(TypeUtil.getInteger(doctorFileEntityMap.get(APIKey.COMMON_DOCTOR_ID), 0));
            doctorFile.setPath(TypeUtil.getString(doctorFileEntityMap.get(APIKey.COMMON_PATH)));
            doctorFile.setType(TypeUtil.getInteger(doctorFileEntityMap.get(APIKey.COMMON_TYPE), 0));
            doctorFile.setStatus(TypeUtil.getInteger(doctorFileEntityMap.get(APIKey.COMMON_STATUS), -1));
            doctorFile.setCreated_at(TypeUtil.getString(doctorFileEntityMap.get(APIKey.COMMON_CREATED_AT)));
            doctorFile.setDoctor(getDoctorEntity(TypeUtil.getMap(doctorFileEntityMap.get(APIKey.COMMON_DOCTOR))));
            return doctorFile;
        }
        return null;
    }

    public static List<DoctorFileEntity> getDoctorFileEntityList(List<Map<String, Object>> rawDoctorFileEntityList) {
        List<DoctorFileEntity> doctorFileEntityList = new ArrayList<DoctorFileEntity>();
        if (null != rawDoctorFileEntityList) {
            for (int i = 0; i < rawDoctorFileEntityList.size(); i++) {
                Map<String, Object> rawDoctorFile = rawDoctorFileEntityList.get(i);
                DoctorFileEntity doctorFile = getDoctorFileEntity(rawDoctorFile);
                if (null != doctorFile) {
                    doctorFileEntityList.add(doctorFile);
                }
            }

        }
        return doctorFileEntityList;

    }

    public static AppadEntity getAppadEntity(Map<String, Object> appadMap) {
        if (null != appadMap) {
            AppadEntity appad = new AppadEntity();
            appad.setId(TypeUtil.getInteger(appadMap.get(APIKey.COMMON_ID), 0));
            appad.setPath(TypeUtil.getString(appadMap.get(APIKey.APP_AD_PATH)));
            appad.setUri(TypeUtil.getString(appadMap.get(APIKey.COMMON_URI)));
            appad.setTitle(TypeUtil.getString(appadMap.get(APIKey.APP_AD_TITLE)));
            appad.setStatus(TypeUtil.getInteger(appadMap.get(APIKey.COMMON_STATUS), -1));
            appad.setCreated_at(TypeUtil.getString(appadMap.get(APIKey.COMMON_CREATED_AT)));

            return appad;
        }
        return null;

    }

    public static List<AppadEntity> getAppadEntityList(List<Map<String, Object>> rawAppadList) {
        List<AppadEntity> appadEntityList = new ArrayList<AppadEntity>();
        if (null != rawAppadList) {
            for (int i = 0; i < rawAppadList.size(); i++) {
                Map<String, Object> rawAppad = rawAppadList.get(i);
                AppadEntity appAd = getAppadEntity(rawAppad);
                if (null != appAd) {
                    appadEntityList.add(appAd);
                }
            }

        }
        return appadEntityList;

    }

    public static AnamnesisEntity getAnamnesisEntity(Map<String, Object> anamnesisMap) {
        if (null != anamnesisMap) {
            AnamnesisEntity anamnesis = new AnamnesisEntity();
            anamnesis.setId(TypeUtil.getInteger(anamnesisMap.get(APIKey.COMMON_ID), 0));
            anamnesis.setName(TypeUtil.getString(anamnesisMap.get(APIKey.COMMON_NAME)));
            anamnesis.setStatus(TypeUtil.getInteger(anamnesisMap.get(APIKey.COMMON_STATUS), -1));
            anamnesis.setCreated_at(TypeUtil.getString(anamnesisMap.get(APIKey.COMMON_CREATED_AT)));

            return anamnesis;
        }
        return null;

    }

    public static List<AnamnesisEntity> getAnamnesisEntityList(List<Map<String, Object>> rawAnamnesisList) {
        List<AnamnesisEntity> anamnesisEntityList = new ArrayList<AnamnesisEntity>();
        if (null != rawAnamnesisList) {
            for (int i = 0; i < rawAnamnesisList.size(); i++) {
                Map<String, Object> rawAnamnesis = rawAnamnesisList.get(i);
                AnamnesisEntity anamnesis = getAnamnesisEntity(rawAnamnesis);
                if (null != anamnesis) {
                    anamnesisEntityList.add(anamnesis);
                }
            }

        }
        return anamnesisEntityList;

    }

    public static CategoryEntity getCategoryEntity(Map<String, Object> categoryMap) {
        if (categoryMap != null) {
            CategoryEntity category = new CategoryEntity();
            category.setId(TypeUtil.getInteger(categoryMap.get(APIKey.COMMON_ID), -1));
            category.setCategory_id(TypeUtil.getInteger(categoryMap.get(APIKey.CAETEGORY_CATEGORY_ID), -1));
            category.setName(TypeUtil.getString(categoryMap.get(APIKey.CAETEGORY_NAME)));
            category.setStatus(TypeUtil.getInteger(categoryMap.get(APIKey.COMMON_STATUS), -1));
            category.setCreated_at(TypeUtil.getString(categoryMap.get(APIKey.COMMON_CREATED_AT)));
            return category;
        }
        return null;
    }

    public static List<CategoryEntity> getCategoryEntityList(List<Map<String, Object>> rawCategoryList) {
        List<CategoryEntity> categoryEntityList = new ArrayList<CategoryEntity>();
        if (null != rawCategoryList) {
            for (int i = 0; i < rawCategoryList.size(); i++) {
                Map<String, Object> rawCategory = rawCategoryList.get(i);
                CategoryEntity category = getCategoryEntity(rawCategory);
                if (null != category) {
                    categoryEntityList.add(category);
                }
            }

        }
        return categoryEntityList;

    }

    public static CityEntity getCityEntity(Map<String, Object> cityMap) {
        if (cityMap != null) {
            CityEntity city = new CityEntity();
            city.setId(TypeUtil.getInteger(cityMap.get(APIKey.COMMON_ID), 0));
            city.setName(TypeUtil.getString(cityMap.get(APIKey.COMMON_NAME)));
            city.setStatus(TypeUtil.getInteger(cityMap.get(APIKey.COMMON_STATUS), -1));
            city.setCreated_at(TypeUtil.getString(cityMap.get(APIKey.COMMON_CREATED_AT)));
            return city;
        }
        return null;
    }

    public static List<CityEntity> getCityEntityList(List<Map<String, Object>> rawCityList) {
        List<CityEntity> cityEntityList = new ArrayList<CityEntity>();
        if (null != rawCityList) {
            for (int i = 0; i < rawCityList.size(); i++) {
                Map<String, Object> rawCity = rawCityList.get(i);
                CityEntity city = getCityEntity(rawCity);
                if (null != city) {
                    cityEntityList.add(city);
                }
            }

        }
        return cityEntityList;

    }

    public static ConsultationEntity getConsultationEntity(Map<String, Object> consultationMap) {
        if (consultationMap != null) {
            ConsultationEntity consultation = new ConsultationEntity();
            //            Log.i("testYJ","id="+TypeUtil.getString(consultationMap.get(APIKey.CONSULTATION_PATIENT_AGE)));
            consultation.setId(TypeUtil.getInteger(consultationMap.get(APIKey.COMMON_ID), 0));
            consultation.setDoctor_id(TypeUtil.getInteger(consultationMap.get(APIKey.COMMON_DOCTOR_ID), 0));
            consultation.setExpert_id(TypeUtil.getInteger(consultationMap.get(APIKey.COMMON_EXPERT_ID), 0));
            consultation.setPatient_name(TypeUtil.getString(consultationMap.get(APIKey.CONSULTATION_PATIENT_NAME)));
            consultation.setPatient_sex(TypeUtil.getInteger(consultationMap.get(APIKey.CONSULTATION_PATIENT_SEX), -1));
            consultation.setPatient_age(TypeUtil.getInteger(consultationMap.get(APIKey.CONSULTATION_PATIENT_AGE), -1));
            consultation.setPatient_mobile(TypeUtil.getString(consultationMap.get(APIKey.CONSULTATION_PATIENT_MOBILE)));
            consultation.setPatient_address(TypeUtil.getString(consultationMap.get(APIKey.CONSULTATION_PATIENT_ADDRESS)));
            consultation.setPatient_hospital(TypeUtil.getString(consultationMap.get(APIKey.CONSULTATION_PATIENT_HOSPITAL)));
            consultation.setPatient_dept(TypeUtil.getString(consultationMap.get(APIKey.CONSULTATION_PATIENT_DEPT)));
            consultation.setPatient_city(TypeUtil.getString(consultationMap.get(APIKey.CONSULTATION_PATIENT_CITY)));
            consultation.setPatient_illness(TypeUtil.getString(consultationMap.get(APIKey.CONSULTATION_PATIENT_ILLNESS)));
            consultation.setAnamnesis_id(TypeUtil.getInteger(consultationMap.get(APIKey.CONSULTATION_ANAMNESIS_ID), -1));
            consultation.setSymptom_id(TypeUtil.getInteger(consultationMap.get(APIKey.CONSULTATION_SYMPTOM_ID), -1));
            consultation.setRemark(TypeUtil.getString(consultationMap.get(APIKey.CONSULTATION_PATIENT_REMARK)));
            consultation.setPurpose(TypeUtil.getString(consultationMap.get(APIKey.CONSULTATION_PURPOSE)));
            consultation.setTimely(TypeUtil.getInteger(consultationMap.get(APIKey.CONSULTATION_TIMELY), -1));
            //            consultation.setTimely(Integer.parseInt(TypeUtil.getString(consultationMap.get(APIKey.CONSULTATION_TIMELY), "")));
            consultation.setType(TypeUtil.getInteger(consultationMap.get(APIKey.COMMON_TYPE), 0));
            consultation.setOther_order(TypeUtil.getInteger(consultationMap.get(APIKey.CONSULTATION_OTHER_ORDER), 1));
            consultation.setOrder_at(TypeUtil.getString(consultationMap.get(APIKey.CONSULTATION_ORDER_AT)));
            consultation.setStatus(TypeUtil.getInteger(consultationMap.get(APIKey.COMMON_STATUS), -1));
            consultation.setCreated_at(TypeUtil.getString(consultationMap.get(APIKey.COMMON_CREATED_AT)));
            consultation.setDoctor(getDoctorEntity(TypeUtil.getMap(consultationMap.get(APIKey.COMMON_DOCTOR))));
            consultation.setExpert(getDoctorEntity(TypeUtil.getMap(consultationMap.get(APIKey.COMMON_EXPERT))));
            consultation.setAnamnesis(TypeUtil.getString(consultationMap.get(APIKey.COMMON_ANAMNESIS)));
            consultation.setSymptom(TypeUtil.getString(consultationMap.get(APIKey.COMMON_SYMPTOM)));
            consultation.setConsultationFiles(getConsultationFileEntityList((TypeUtil.getList(consultationMap.get(APIKey.COMMON_CONSULTATION_FILES)))));
            //            consultation.setAnamnesis(getAnamnesisEntity(TypeUtil.getMap(consultationMap.get(APIKey.COMMON_ANAMNESIS))));
            //            consultation.setSymptom(getSymptomEntity(TypeUtil.getMap(consultationMap.get(APIKey.COMMON_SYMPTOM))));
            return consultation;
        }
        return null;
    }

    public static List<ConsultationEntity> getConsultationEntityList(List<Map<String, Object>> rawConsultationList) {
        List<ConsultationEntity> consultationEntityList = new ArrayList<ConsultationEntity>();
        if (null != rawConsultationList) {
            for (int i = 0; i < rawConsultationList.size(); i++) {
                Map<String, Object> rawConsultation = rawConsultationList.get(i);
                ConsultationEntity consultation = getConsultationEntity(rawConsultation);
                if (null != consultation) {
                    consultationEntityList.add(consultation);
                }
            }

        }
        return consultationEntityList;

    }

    public static ConsultationFileEntity getConsultationFileEntity(Map<String, Object> consultationFileMap) {
        if (consultationFileMap != null) {
            ConsultationFileEntity consultationFile = new ConsultationFileEntity();
            consultationFile.setId(TypeUtil.getInteger(consultationFileMap.get(APIKey.COMMON_ID), 0));
            consultationFile.setConsultation_id(TypeUtil.getInteger(consultationFileMap.get(APIKey.COMMON_CONSULTATION_ID), 0));
            consultationFile.setPath(TypeUtil.getString(consultationFileMap.get(APIKey.COMMON_PATH)));
            consultationFile.setType(TypeUtil.getInteger(consultationFileMap.get(APIKey.COMMON_TYPE), 0));
            consultationFile.setStatus(TypeUtil.getInteger(consultationFileMap.get(APIKey.COMMON_STATUS), -1));
            consultationFile.setCreated_at(TypeUtil.getString(consultationFileMap.get(APIKey.COMMON_CREATED_AT)));
            consultationFile.setConsultation(getConsultationEntity(TypeUtil.getMap(consultationFileMap.get(APIKey.COMMON_CONSULTATION))));
            return consultationFile;
        }
        return null;
    }

    public static List<ConsultationFileEntity> getConsultationFileEntityList(List<Map<String, Object>> rawConsultationFileEntityList) {
        List<ConsultationFileEntity> consultFileEntityList = new ArrayList<ConsultationFileEntity>();
        if (null != rawConsultationFileEntityList) {
            for (int i = 0; i < rawConsultationFileEntityList.size(); i++) {
                Map<String, Object> rawConsultationFile = rawConsultationFileEntityList.get(i);
                ConsultationFileEntity consultationFile = getConsultationFileEntity(rawConsultationFile);
                if (null != consultationFile) {
                    consultFileEntityList.add(consultationFile);
                }
            }

        }
        return consultFileEntityList;

    }

    public static EvaluateEntity getEvaluateEntity(Map<String, Object> evaluateEntityMap) {
        if (evaluateEntityMap != null) {
            EvaluateEntity evaluateFile = new EvaluateEntity();
            evaluateFile.setId(TypeUtil.getInteger(evaluateEntityMap.get(APIKey.COMMON_ID), 0));
            evaluateFile.setDoctor_id(TypeUtil.getInteger(evaluateEntityMap.get(APIKey.COMMON_DOCTOR_ID), 0));
            evaluateFile.setEvaluated_doctor_id(TypeUtil.getInteger(evaluateEntityMap.get(APIKey.EVALUATE_EVALUATED_DOCTOR_ID), 0));
            evaluateFile.setOrder_id(TypeUtil.getInteger(evaluateEntityMap.get(APIKey.COMMON_ORDER_ID), 0));
            evaluateFile.setScore(TypeUtil.getInteger(evaluateEntityMap.get(APIKey.EVALUATE_SCORE), 0));
            evaluateFile.setContent(TypeUtil.getString(evaluateEntityMap.get(APIKey.COMMON_CONTENT)));
            evaluateFile.setStatus(TypeUtil.getInteger(evaluateEntityMap.get(APIKey.COMMON_STATUS), -1));
            evaluateFile.setCreated_at(TypeUtil.getString(evaluateEntityMap.get(APIKey.COMMON_CREATED_AT)));
            evaluateFile.setDoctor(getDoctorEntity(TypeUtil.getMap(evaluateEntityMap.get(APIKey.COMMON_DOCTOR))));
            evaluateFile.setEvaluated_doctor(getDoctorEntity(TypeUtil.getMap(evaluateEntityMap.get(APIKey.EVALUATE_EVALUATED_DOCTOR))));
            evaluateFile.setOrder(getOrderEntity(TypeUtil.getMap(evaluateEntityMap.get(APIKey.COMMON_ORDER))));
            return evaluateFile;
        }
        return null;
    }

    public static List<EvaluateEntity> getEvaluateEntityList(List<Map<String, Object>> rawEvaluateEntityList) {
        List<EvaluateEntity> evaluateEntityList = new ArrayList<EvaluateEntity>();
        if (null != rawEvaluateEntityList) {
            for (int i = 0; i < rawEvaluateEntityList.size(); i++) {
                Map<String, Object> rawEvaluate = rawEvaluateEntityList.get(i);
                EvaluateEntity evaluate = getEvaluateEntity(rawEvaluate);
                if (null != evaluate) {
                    evaluateEntityList.add(evaluate);
                }
            }

        }
        return evaluateEntityList;

    }

    public static ExpertiseEntity getExpertiseEntity(Map<String, Object> expertiseEntityMap) {
        if (expertiseEntityMap != null) {
            ExpertiseEntity expertise = new ExpertiseEntity();
            expertise.setId(TypeUtil.getInteger(expertiseEntityMap.get(APIKey.COMMON_ID), 0));
            expertise.setName(TypeUtil.getString(expertiseEntityMap.get(APIKey.COMMON_NAME)));
            expertise.setStatus(TypeUtil.getInteger(expertiseEntityMap.get(APIKey.COMMON_STATUS), -1));
            expertise.setCreated_at(TypeUtil.getString(expertiseEntityMap.get(APIKey.COMMON_CREATED_AT)));
            return expertise;
        }
        return null;
    }

    public static List<ExpertiseEntity> getExpertiseEntityList(List<Map<String, Object>> rawExpertiseEntityList) {
        List<ExpertiseEntity> expertiseEntityList = new ArrayList<ExpertiseEntity>();
        if (null != rawExpertiseEntityList) {
            for (int i = 0; i < rawExpertiseEntityList.size(); i++) {
                Map<String, Object> rawExpertise = rawExpertiseEntityList.get(i);
                ExpertiseEntity expertise = getExpertiseEntity(rawExpertise);
                if (null != expertise) {
                    expertiseEntityList.add(expertise);
                }
            }

        }
        return expertiseEntityList;

    }

    public static FavoriteEntity getfavoriteEntity(Map<String, Object> favoriteEntityMap) {
        if (favoriteEntityMap != null) {
            FavoriteEntity favortie = new FavoriteEntity();
            favortie.setId(TypeUtil.getInteger(favoriteEntityMap.get(APIKey.COMMON_ID), 0));
            favortie.setDoctor_id(TypeUtil.getInteger(favoriteEntityMap.get(APIKey.COMMON_DOCTOR_ID), 0));
            favortie.setFavourite_doctor_id(TypeUtil.getInteger(favoriteEntityMap.get(APIKey.FAVORITE_FAVORITE_DOCTOR_ID), 0));
            favortie.setStatus(TypeUtil.getInteger(favoriteEntityMap.get(APIKey.COMMON_STATUS), -1));
            favortie.setCreated_at(TypeUtil.getString(favoriteEntityMap.get(APIKey.COMMON_CREATED_AT)));
            favortie.setDoctor(getDoctorEntity(TypeUtil.getMap(favoriteEntityMap.get(APIKey.COMMON_DOCTOR))));
            favortie.setFavouriteDoctor(getDoctorEntity(TypeUtil.getMap(favoriteEntityMap.get(APIKey.FAVORITE_FAVORITE_DOCTOR))));
            return favortie;
        }
        return null;
    }

    public static List<FavoriteEntity> getfavoriteEntityList(List<Map<String, Object>> rawfavoriteEntityList) {
        List<FavoriteEntity> favoriteEntityList = new ArrayList<FavoriteEntity>();
        if (null != rawfavoriteEntityList) {
            for (int i = 0; i < rawfavoriteEntityList.size(); i++) {
                Map<String, Object> rawfavorite = rawfavoriteEntityList.get(i);
                FavoriteEntity favorite = getfavoriteEntity(rawfavorite);
                if (null != favorite) {
                    favoriteEntityList.add(favorite);
                }
            }

        }
        return favoriteEntityList;

    }

    public static OrderEntity getOrderEntity(Map<String, Object> orderEntityMap) {
        if (orderEntityMap != null) {
            OrderEntity order = new OrderEntity();
            order.setId(TypeUtil.getInteger(orderEntityMap.get(APIKey.COMMON_ID), 0));
            order.setDoctor_id(TypeUtil.getInteger(orderEntityMap.get(APIKey.COMMON_DOCTOR_ID), 0));
            order.setOrder_doctor_id(TypeUtil.getInteger(orderEntityMap.get(APIKey.ORDER_ORDER_DOCTOR_ID), 0));
            order.setConsultation_id(TypeUtil.getInteger(orderEntityMap.get(APIKey.COMMON_CONSULTATION_ID), 0));
            order.setType(TypeUtil.getInteger(orderEntityMap.get(APIKey.COMMON_TYPE), 0));
            order.setStatus(TypeUtil.getInteger(orderEntityMap.get(APIKey.COMMON_STATUS), -1));
            order.setRemark(TypeUtil.getString(orderEntityMap.get(APIKey.ORDER_REMARK)));
            order.setCreated_at(TypeUtil.getString(orderEntityMap.get(APIKey.COMMON_CREATED_AT)));
            order.setDoctor(getDoctorEntity(TypeUtil.getMap(orderEntityMap.get(APIKey.COMMON_DOCTOR))));
            order.setOrder_doctor(getDoctorEntity(TypeUtil.getMap(orderEntityMap.get(APIKey.ORDER_ORDER_DOCTOR))));
            order.setConsultation(getConsultationEntity(TypeUtil.getMap(orderEntityMap.get(APIKey.COMMON_CONSULTATION))));
            return order;
        }
        return null;
    }

    public static List<OrderEntity> getOrderEntityList(List<Map<String, Object>> rawOrderEntityList) {
        List<OrderEntity> orderEntityList = new ArrayList<OrderEntity>();
        if (null != rawOrderEntityList) {
            for (int i = 0; i < rawOrderEntityList.size(); i++) {
                Map<String, Object> rawOrder = rawOrderEntityList.get(i);
                OrderEntity order = getOrderEntity(rawOrder);
                if (null != order) {
                    orderEntityList.add(order);
                }
            }

        }
        return orderEntityList;

    }

    public static List<Map<String, OrderEntity>> getOrderEntityMap(List<Map<String, Object>> rawOrderEntityList, String title) {
        List<Map<String, OrderEntity>> orderEntityList = new ArrayList<Map<String, OrderEntity>>();
        if (null != rawOrderEntityList) {
            for (int i = 0; i < rawOrderEntityList.size(); i++) {
                Map<String, Object> rawOrder = rawOrderEntityList.get(i);
                OrderEntity order = getOrderEntity(rawOrder);
                if (null != order) {
                    HashMap<String, OrderEntity> orderMap = new HashMap<String, OrderEntity>();
                    orderMap.put(title, order);
                    orderEntityList.add(orderMap);
                }
            }

        }
        return orderEntityList;

    }

    public static RepineEntity getRepineEntity(Map<String, Object> repineEntityMap) {
        if (repineEntityMap != null) {
            RepineEntity repine = new RepineEntity();
            repine.setId(TypeUtil.getInteger(repineEntityMap.get(APIKey.COMMON_ID), 0));
            repine.setDoctor_id(TypeUtil.getInteger(repineEntityMap.get(APIKey.COMMON_DOCTOR_ID), 0));
            repine.setRepined_doctor_id(TypeUtil.getInteger(repineEntityMap.get(APIKey.REPINE_REPINED_DOCTOR_ID), 0));
            repine.setContent(TypeUtil.getString(repineEntityMap.get(APIKey.COMMON_CONTENT)));
            repine.setStatus(TypeUtil.getInteger(repineEntityMap.get(APIKey.COMMON_STATUS), -1));
            repine.setCreated_at(TypeUtil.getString(repineEntityMap.get(APIKey.COMMON_CREATED_AT)));
            repine.setDoctor(getDoctorEntity(TypeUtil.getMap(repineEntityMap.get(APIKey.COMMON_DOCTOR))));
            repine.setRepined_doctor(getDoctorEntity(TypeUtil.getMap(repineEntityMap.get(APIKey.REPINE_REPINED_DOCTOR))));
            repine.setOrder(getOrderEntity(TypeUtil.getMap(repineEntityMap.get(APIKey.COMMON_ORDER))));
            return repine;
        }
        return null;
    }

    public static List<RepineEntity> getRepineEntityList(List<Map<String, Object>> rawRepineEntityList) {
        List<RepineEntity> repineEntityList = new ArrayList<RepineEntity>();
        if (null != rawRepineEntityList) {
            for (int i = 0; i < rawRepineEntityList.size(); i++) {
                Map<String, Object> rawRepine = rawRepineEntityList.get(i);
                RepineEntity repine = getRepineEntity(rawRepine);
                if (null != repine) {
                    repineEntityList.add(repine);
                }
            }

        }
        return repineEntityList;

    }

    public static SymptomEntity getSymptomEntity(Map<String, Object> symptomEntityMap) {
        if (symptomEntityMap != null) {
            SymptomEntity symptom = new SymptomEntity();
            symptom.setId(TypeUtil.getInteger(symptomEntityMap.get(APIKey.COMMON_ID), 0));
            symptom.setName(TypeUtil.getString(symptomEntityMap.get(APIKey.COMMON_NAME)));
            symptom.setStatus(TypeUtil.getInteger(symptomEntityMap.get(APIKey.COMMON_STATUS), -1));
            symptom.setCreated_at(TypeUtil.getString(symptomEntityMap.get(APIKey.COMMON_CREATED_AT)));
            return symptom;
        }
        return null;
    }

    public static List<SymptomEntity> getSymptomEntityList(List<Map<String, Object>> rawSymptomEntityList) {
        List<SymptomEntity> symptomEntityList = new ArrayList<SymptomEntity>();
        if (null != rawSymptomEntityList) {
            for (int i = 0; i < rawSymptomEntityList.size(); i++) {
                Map<String, Object> rawSymptom = rawSymptomEntityList.get(i);
                SymptomEntity symptom = getSymptomEntity(rawSymptom);
                if (null != symptom) {
                    symptomEntityList.add(symptom);
                }
            }

        }
        return symptomEntityList;

    }

    public static VersionEntity getVersionEntity(Map<String, Object> versionEntityMap) {
        if (versionEntityMap != null) {
            VersionEntity version = new VersionEntity();
            version.setId(TypeUtil.getInteger(versionEntityMap.get(APIKey.COMMON_ID), 0));
            version.setPackage_name(TypeUtil.getString(versionEntityMap.get(APIKey.VERSION_PACKAGE_NAME)));
            version.setName(TypeUtil.getString(versionEntityMap.get(APIKey.COMMON_NAME)));
            version.setUri(TypeUtil.getString(versionEntityMap.get(APIKey.COMMON_URI)));
            version.setVersion_name(TypeUtil.getString(versionEntityMap.get(APIKey.VERSION_VERSION_NAME)));
            version.setVersion_code(TypeUtil.getInteger(versionEntityMap.get(APIKey.VERSION_VERSION_CODE), 0));
            version.setVersion_desc(TypeUtil.getString(versionEntityMap.get(APIKey.VERSION_VERSION_DESC)));
            version.setCreated_at(TypeUtil.getString(versionEntityMap.get(APIKey.COMMON_CREATED_AT)));
            return version;
        }
        return null;
    }

    public static List<VersionEntity> getVersionEntityList(List<Map<String, Object>> rawVersionEntityList) {
        List<VersionEntity> versionEntityList = new ArrayList<VersionEntity>();
        if (null != rawVersionEntityList) {
            for (int i = 0; i < rawVersionEntityList.size(); i++) {
                Map<String, Object> rawVersion = rawVersionEntityList.get(i);
                VersionEntity version = getVersionEntity(rawVersion);
                if (null != version) {
                    versionEntityList.add(version);
                }
            }

        }
        return versionEntityList;

    }

    public static List<ScrollPagerEntity> getScrollPagerEntityList(List<Map<String, Object>> scrollPagerMapList) {
        List<ScrollPagerEntity> scrollPagerEntityList = new ArrayList<ScrollPagerEntity>();

        if (null != scrollPagerMapList && scrollPagerMapList.size() > 0) {
            for (int i = 0; i < scrollPagerMapList.size(); i++) {
                Map<String, Object> scrollPagerMap = scrollPagerMapList.get(i);
                if (null != scrollPagerMap) {
                    ScrollPagerEntity scrollPagerEntity = getScrollPagerEntity(scrollPagerMap);
                    if (null != scrollPagerEntity) {
                        scrollPagerEntityList.add(scrollPagerEntity);
                    }
                }
            }
        }
        return scrollPagerEntityList;
    }

    private static ScrollPagerEntity getScrollPagerEntity(Map<String, Object> scrollPagerMap) {
        ScrollPagerEntity scrollPagerEntity = null;

        if (null != scrollPagerMap) {
            scrollPagerEntity = new ScrollPagerEntity();

            scrollPagerEntity.setId(TypeUtil.getId(scrollPagerMap.get(APIKey.COMMON_ID)));
            scrollPagerEntity.setBannerBgUrl(TypeUtil.getString(scrollPagerMap.get(APIKey.APP_AD_PATH)));
            scrollPagerEntity.setContentUrl(TypeUtil.getString(scrollPagerMap.get(APIKey.COMMON_URI)));
            scrollPagerEntity.setBannerTitle(TypeUtil.getString(scrollPagerMap.get(APIKey.APP_AD_TITLE)));
            scrollPagerEntity.setCreatedAt(TypeUtil.getString(scrollPagerMap.get(APIKey.COMMON_CREATED_AT)));
            scrollPagerEntity.setStatus(TypeUtil.getInteger(scrollPagerMap.get(APIKey.COMMON_CREATED_AT), 1));
            scrollPagerEntity.setType(TypeUtil.getInteger(scrollPagerMap.get(APIKey.COMMON_TYPE), 1));
        }
        return scrollPagerEntity;
    }

}
