package com.gaoshou.android.entity;

public class VersionEntity {
    /*
     * 标识       id
     * 包名       package_name
     * 名称     name
     * 版本地址     uri
     * 版本名称     version_name
     * 版本号码     version_code
     * 版本说明     version_desc
     * 创建时间     created_at
     */
    private int id;
    private String package_name;
    private String name;
    private String uri;
    private String version_name;
    private int version_code;
    private String version_desc;
    private String created_at;
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getPackage_name() {
        return package_name;
    }
    public void setPackage_name(String package_name) {
        this.package_name = package_name;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getUri() {
        return uri;
    }
    public void setUri(String uri) {
        this.uri = uri;
    }
    public String getVersion_name() {
        return version_name;
    }
    public void setVersion_name(String version_name) {
        this.version_name = version_name;
    }
    public int getVersion_code() {
        return version_code;
    }
    public void setVersion_code(int version_code) {
        this.version_code = version_code;
    }
    public String getVersion_desc() {
        return version_desc;
    }
    public void setVersion_desc(String version_desc) {
        this.version_desc = version_desc;
    }
    public String getCreated_at() {
        return created_at;
    }
    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }
    
    
}
