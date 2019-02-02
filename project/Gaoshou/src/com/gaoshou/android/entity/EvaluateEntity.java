package com.gaoshou.android.entity;

public class EvaluateEntity {
    public static final int PERFICT = 0;
    public static final int SATIFY = 1;
    public static final int NORMAL = 2;
    /*
     * 标识       id
     * 医生标识     doctor_id
     * 被评价医生标识    evaluated_doctor_id
     * 被评价订单标识    order_id
     * 评分       score
     * 内容       content
     * 状态       status
     * 创建时间     created_at
     * 评价医生     doctor;
     * 被评价医生    evaluated_doctor
     * 被评价订单    order*/
    private int id;
    private int doctor_id;
    private int evaluated_doctor_id;
    private int order_id;
    private int score;
    private String content;
    private int status;
    private String created_at;
    private DoctorEntity doctor;
    private DoctorEntity evaluated_doctor;
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
    public int getEvaluated_doctor_id() {
        return evaluated_doctor_id;
    }
    public void setEvaluated_doctor_id(int evaluated_doctor_id) {
        this.evaluated_doctor_id = evaluated_doctor_id;
    }
    public int getOrder_id() {
        return order_id;
    }
    public void setOrder_id(int order_id) {
        this.order_id = order_id;
    }
    public int getScore() {
        return score;
    }
    public void setScore(int score) {
        this.score = score;
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
    public DoctorEntity getEvaluated_doctor() {
        return evaluated_doctor;
    }
    public void setEvaluated_doctor(DoctorEntity evaluated_doctor) {
        this.evaluated_doctor = evaluated_doctor;
    }
    public OrderEntity getOrder() {
        return order;
    }
    public void setOrder(OrderEntity order) {
        this.order = order;
    }
    
    
}
