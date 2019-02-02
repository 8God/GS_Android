package com.gaoshou.android.entity;

public class DoctorFileEntity {
    /*
     * 标号 id 医生标志 doctor_id 文件路径 path 类型 type 状态 status 创建时间 created_at 医生表
     * doctor
     */
    private int id;
    private int doctor_id;
    private String path;
    private int type = -1;
    private int status;
    private String created_at;
    private DoctorEntity doctor;

    public DoctorFileEntity() {
        super();
    }
    
    public DoctorFileEntity(int id,String path,int type){
        super();
        this.id = id;
        this.path = path;
        this.type = type;
    }
    
    public DoctorFileEntity(DoctorFileEntity doctorfile) {
        super();
        if (doctorfile != null) {
            this.id = doctorfile.getId();
            this.doctor_id = doctorfile.getDoctor_id();
            this.path = doctorfile.getPath();
            this.type = doctorfile.getType();
            this.status = doctorfile.getStatus();
            this.created_at = doctorfile.getCreated_at();
            this.doctor = doctorfile.getDoctor();
        }
    }

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

    public DoctorEntity getDoctor() {
        return doctor;
    }

    public void setDoctor(DoctorEntity doctor) {
        this.doctor = doctor;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((created_at == null) ? 0 : created_at.hashCode());
        result = prime * result + ((doctor == null) ? 0 : doctor.hashCode());
        result = prime * result + doctor_id;
        result = prime * result + id;
        result = prime * result + ((path == null) ? 0 : path.hashCode());
        result = prime * result + status;
        result = prime * result + type;
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
        DoctorFileEntity other = (DoctorFileEntity) obj;
        if (created_at == null) {
            if (other.created_at != null)
                return false;
        } else if (!created_at.equals(other.created_at))
            return false;
        if (doctor == null) {
            if (other.doctor != null)
                return false;
        } else if (!doctor.equals(other.doctor))
            return false;
        if (doctor_id != other.doctor_id)
            return false;
        if (id != other.id)
            return false;
        if (path == null) {
            if (other.path != null)
                return false;
        } else if (!path.equals(other.path))
            return false;
        if (status != other.status)
            return false;
        if (type != other.type)
            return false;
        return true;
    }

}
