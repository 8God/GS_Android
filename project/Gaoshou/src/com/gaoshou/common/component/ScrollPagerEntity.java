package com.gaoshou.common.component;

/**
 * Scroll pager data entity
 * 
 * @author CTH
 *
 */
public class ScrollPagerEntity {

    private String id;
    private String bannerBgUrl; //背景图url
    private String contentUrl; //内容链接
    private String bannerTitle; //标题
    private int status; //状态
    private int type; //类型

    private String createdAt;
    private String description;

    public String getBannerBgUrl() {
        return bannerBgUrl;
    }

    public void setBannerBgUrl(String bannerBgUrl) {
        this.bannerBgUrl = bannerBgUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContentUrl() {
        return contentUrl;
    }

    public void setContentUrl(String contentUrl) {
        this.contentUrl = contentUrl;
    }

    public String getBannerTitle() {
        return bannerTitle;
    }

    public void setBannerTitle(String bannerTitle) {
        this.bannerTitle = bannerTitle;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

}
