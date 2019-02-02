package com.gaoshou.android.entity;

import java.util.List;

public class ConsultationEntity extends BaseEntity {
    public static final int CONSULTATION = 0;
    public static final int CONSULTATION_OPERATATION = 1;
    /*
     * 标识 id
     *  医生标识 doctor_id 
     *  患者姓名 patient_name 
     *  患者性别 patient_sex 
     *  患者年龄 patient_age
     *  患者手机 patient_mobile 
     *  患者地址 patient_address 
     *  患者医院 patient_hospital 
     *  患者科室 patient_dept
     *  患者城市 patient_city 
     *  患者疾病 patient_illness 
     *  病史标识 anamnesis_id
     *  症状标识 symptom_id 
     *  备注 remark 
     *  会诊目的 purpose 
     *  即时 timely 
     *  其他专家接单 other_order; 
     *  预约时间    order_at
     *  状态 status 
     *  创建时间 created_at 
     *  医生表 doctor 
     *  病史表 anamnesis 
     *  症状表 symptom
     */
    private int id;
    private int doctor_id;
    private int expert_id;
    private String patient_name;
    private int patient_sex;
    private int patient_age;
    private String patient_mobile;
    private String patient_address;
    private String patient_hospital;
    private String patient_dept;
    private String patient_city;
    private String patient_illness;
    private int anamnesis_id;
    private int symptom_id;
    private String remark;
    private String purpose;
    private int timely;
    private int type;
    private int other_order;
    private String order_at;
    private int status;
    private String created_at;
    private DoctorEntity doctor;
    private DoctorEntity expert;
//    private AnamnesisEntity anamnesis;
//    private SymptomEntity symptom;
    private String anamnesis;
    private String symptom;
    private List<ConsultationFileEntity> consultationFiles;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDoctor_id() {
        return doctor_id;
    }

    public void setDoctor_id(int doctor_id) {
        this.doctor_id = doctor_id;
    }

    public int getExpert_id() {
        return expert_id;
    }

    public void setExpert_id(int expert_id) {
        this.expert_id = expert_id;
    }

    public String getPatient_name() {
        return patient_name;
    }

    public void setPatient_name(String patient_name) {
        this.patient_name = patient_name;
    }

    public int getPatient_sex() {
        return patient_sex;
    }

    public void setPatient_sex(int patient_sex) {
        this.patient_sex = patient_sex;
    }

    public int getPatient_age() {
        return patient_age;
    }

    public void setPatient_age(int patient_age) {
        this.patient_age = patient_age;
    }

    public String getPatient_mobile() {
        return patient_mobile;
    }

    public void setPatient_mobile(String patient_mobile) {
        this.patient_mobile = patient_mobile;
    }

    public String getPatient_address() {
        return patient_address;
    }

    public void setPatient_address(String patient_address) {
        this.patient_address = patient_address;
    }

    public String getPatient_hospital() {
        return patient_hospital;
    }

    public void setPatient_hospital(String patient_hospital) {
        this.patient_hospital = patient_hospital;
    }

    public String getPatient_dept() {
        return patient_dept;
    }

    public void setPatient_dept(String patient_dept) {
        this.patient_dept = patient_dept;
    }

    public String getPatient_city() {
        return patient_city;
    }

    public void setPatient_city(String patient_city) {
        this.patient_city = patient_city;
    }

    public String getPatient_illness() {
        return patient_illness;
    }

    public void setPatient_illness(String patient_illness) {
        this.patient_illness = patient_illness;
    }

    public int getAnamnesis_id() {
        return anamnesis_id;
    }

    public void setAnamnesis_id(int anamnesis_id) {
        this.anamnesis_id = anamnesis_id;
    }

    public int getSymptom_id() {
        return symptom_id;
    }

    public void setSymptom_id(int symptom_id) {
        this.symptom_id = symptom_id;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public int getTimely() {
        return timely;
    }

    public void setTimely(int timely) {
        this.timely = timely;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getOther_order() {
        return other_order;
    }

    public void setOther_order(int other_order) {
        this.other_order = other_order;
    }

    public String getOrder_at() {
        return order_at;
    }

    public void setOrder_at(String order_at) {
        this.order_at = order_at;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public DoctorEntity getDoctor() {
        return doctor;
    }

    public void setDoctor(DoctorEntity doctor) {
        this.doctor = doctor;
    }

    public DoctorEntity getExpert() {
        return expert;
    }

    public void setExpert(DoctorEntity expert) {
        this.expert = expert;
    }

//    public AnamnesisEntity getAnamnesis() {
//        return anamnesis;
//    }
//
//    public void setAnamnesis(AnamnesisEntity anamnesis) {
//        this.anamnesis = anamnesis;
//    }
//
//    public SymptomEntity getSymptom() {
//        return symptom;
//    }
//
//    public void setSymptom(SymptomEntity symptom) {
//        this.symptom = symptom;
//    }
//    
    public String getAnamnesis() {
        return anamnesis;
    }

    public void setAnamnesis(String anamnesis) {
        this.anamnesis = anamnesis;
    }

    public String getSymptom() {
        return symptom;
    }

    public void setSymptom(String symptom) {
        this.symptom = symptom;
    }

    @Override
    public String toString() {
        return "ConsultationEntity [id=" + id + ", doctor_id=" + doctor_id + ", patient_name=" + patient_name + ", patient_sex=" + patient_sex + ", patient_age=" + patient_age + ", patient_mobile=" + patient_mobile + ", patient_address=" + patient_address + ", patient_hospital=" + patient_hospital + ", patient_dept=" + patient_dept + ", patient_city=" + patient_city + ", patient_illness=" + patient_illness + ", anamnesis_id=" + anamnesis_id + ", symptom_id=" + symptom_id + ", remark=" + remark + ", purpose=" + purpose + ", timely=" + timely + ", other_order=" + other_order + ", order_at=" + order_at + ", status=" + status + ", created_at=" + created_at + ", doctor=" + doctor + ", anamnesis=" + anamnesis + ", symptom=" + symptom + "]";
    }

    public List<ConsultationFileEntity> getConsultationFiles() {
        return consultationFiles;
    }

    public void setConsultationFiles(List<ConsultationFileEntity> consultationFiles) {
        this.consultationFiles = consultationFiles;
    }

}
