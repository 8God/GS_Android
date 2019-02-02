package com.gaoshou.android.entity;

public class OrderEntity  extends BaseEntity {
    public static final int STATUS_DELETED = 0; // 冻结
    public static final int STATUS_INITIAL = 1; // 初始值
    public static final int STATUS_NOT_PAYMENT = 2; // 未付款
    public static final int STATUS_PAYMENT = 3; // 已付款
    public static final int STATUS_CANCEL = 4; // 取消
    public static final int STATUS_PRE_PAYMENT_CANCEL = 5; // 未付款取消
    public static final int STATUS_COMPLETE = 9; // 已完成
    /*
     * 标识       id
     * 医生标识     doctor_id
     * 接单医生标识   order_doctor_id
     * 订单标识     consultation_id
     * 类型       type
     * 状态       status
     * 退单理由     remark
     * 创建时间     created_at
     * 医生       doctor
     * 接单医生     order_doctor
     * 订单       consultation
     */
    private int id;
    private int doctor_id;
    private int order_doctor_id;
    private int consultation_id;
    private int type;
    private int status;
    private String created_at;
    private String remark;
    private DoctorEntity doctor;
    private DoctorEntity order_doctor;
    private ConsultationEntity consultation;
    
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
    public int getOrder_doctor_id() {
        return order_doctor_id;
    }
    public void setOrder_doctor_id(int order_doctor_id) {
        this.order_doctor_id = order_doctor_id;
    }
    public int getConsultation_id() {
        return consultation_id;
    }
    public void setConsultation_id(int consultation_id) {
        this.consultation_id = consultation_id;
    }
    public int getType() {
        return type;
    }
    public void setType(int type) {
        this.type = type;
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
    public DoctorEntity getOrder_doctor() {
        return order_doctor;
    }
    public void setOrder_doctor(DoctorEntity order_doctor) {
        this.order_doctor = order_doctor;
    }
    public ConsultationEntity getConsultation() {
        return consultation;
    }
    public void setConsultation(ConsultationEntity consultation) {
        this.consultation = consultation;
    }
    public String getRemark() {
        return remark;
    }
    public void setRemark(String remark) {
        this.remark = remark;
    }
    
    
    
}       
