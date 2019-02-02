package com.gaoshou.android.entity;

public class CategoryEntity {
    /*
     * 标志       id
     * 父分类标志    category_id 默认为-1
     * 名称       name
     * 状态       status
     * 创建时间     created_at
     * */
    
    private int id;
    private int category_id;
    private String name;
    private int status;
    private String created_at;
    
    
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public int getCategory_id() {
        return category_id;
    }
    public void setCategory_id(int category_id) {
        this.category_id = category_id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
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
