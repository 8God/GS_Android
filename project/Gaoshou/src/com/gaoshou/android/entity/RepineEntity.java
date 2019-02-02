package com.gaoshou.android.entity;

public class RepineEntity {
    /*
     * 标识       id
     * 医生标识     doctor_id
     * 被投诉医生标识  repined_doctor_id
     * 被投诉订单标识  order_id
     * 内容       content
     * 状态       status;
     * 创建时间     created_at;
     * 医生       doctor
     * 被投诉医生    repined_doctor
     * 被投诉订单    order
     */
    private int id;
    private int doctor_id;
    private int repined_doctor_id;
    private int order_id;
    private String content;
    private int status;
    private String created_at;
    private DoctorEntity doctor;
    private DoctorEntity repined_doctor;
    private OrderEntity order;
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
    public int getRepined_doctor_id() {
        return repined_doctor_id;
    }
    public void setRepined_doctor_id(int repined_doctor_id) {
        this.repined_doctor_id = repined_doctor_id;
    }
    public int getOrder_id() {
        return order_id;
    }
    public void setOrder_id(int order_id) {
        this.order_id = order_id;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
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
    public DoctorEntity getRepined_doctor() {
        return repined_doctor;
    }
    public void setRepined_doctor(DoctorEntity repined_doctor) {
        this.repined_doctor = repined_doctor;
    }
    public OrderEntity getOrder() {
        return order;
    }
    public void setOrder(OrderEntity order) {
        this.order = order;
    }
    
    
}
