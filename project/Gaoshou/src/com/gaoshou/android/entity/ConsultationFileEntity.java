package com.gaoshou.android.entity;

public class ConsultationFileEntity {
    public static final int SIGN = 1;
    public static final int ASSAY = 2;
    public static final int IAMGE = 3;
    /*
     * 标识       id
     * 会诊标识     consultation_id
     * 文件路径     path
     * 类型       type
     * 状态       status
     * 创建时间     created_at*/
    private int id;
    private int consultation_id;
    private String path;
    private int type;
    private int status;
    private String created_at;
    private ConsultationEntity consultation;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getConsultation_id() {
        return consultation_id;
    }

    public void setConsultation_id(int consultation_id) {
        this.consultation_id = consultation_id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
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

    public ConsultationEntity getConsultation() {
        return consultation;
    }

    public void setConsultation(ConsultationEntity consultation) {
        this.consultation = consultation;
    }

}
