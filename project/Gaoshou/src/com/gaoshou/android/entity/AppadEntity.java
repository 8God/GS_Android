package com.gaoshou.android.entity;

public class AppadEntity {
    /*
     * 标志       id
     * 文件名      path
     * 路径       uri
     * 标题       title
     * 类型       type
     * 状态       status
     * 创建时间     created_at*/
    private int id;
    private String path;
    private String uri;
    private String title;
    private int type;
    private int status;
    private String created_at;
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getPath() {
        return path;
    }
    public void setPath(String path) {
        this.path = path;
    }
    public String getUri() {
        return uri;
    }
    public void setUri(String uri) {
        this.uri = uri;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
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
    
    
}
