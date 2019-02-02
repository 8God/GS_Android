package com.gaoshou.android.entity;

public class FavoriteEntity {
    /*
     * 标识       id
     * 医生标识     doctor_id
     * 被收藏的医生标识 favourites_doctor_id
     * 状态       status
     * 创建时间     created_at
     * 医生       doctor
     * 被收藏医生    favourities_doctor*/
    private int id;
    private int doctor_id;
    private int favourite_doctor_id;
    private int status;
    private String created_at;
    private DoctorEntity doctor;
    private DoctorEntity favouriteDoctor;
    
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
    public int getFavourite_doctor_id() {
        return favourite_doctor_id;
    }
    public void setFavourite_doctor_id(int favourite_doctor_id) {
        this.favourite_doctor_id = favourite_doctor_id;
    }
    public DoctorEntity getFavouriteDoctor() {
        return favouriteDoctor;
    }
    public void setFavouriteDoctor(DoctorEntity favouriteDoctor) {
        this.favouriteDoctor = favouriteDoctor;
    }
    
    
}
