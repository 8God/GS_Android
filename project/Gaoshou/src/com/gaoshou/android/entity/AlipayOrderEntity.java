package com.gaoshou.android.entity;

/**
 * 支付订单实体类
 */
public class AlipayOrderEntity {
    //接单医生ID
    private int expertId;
    //订单ID
    private int orderId = -1;
    //测试的商品
    private String orderTitle;
    //该测试商品的详细描述
    private String orderContent;
    //价格
    private float orderPrice;
    
    
    public AlipayOrderEntity() {
        super();
    }

    public String getOrderTitle() {
        return orderTitle;
    }

    public void setOrderTitle(String orderTitle) {
        this.orderTitle = orderTitle;
    }

    public String getOrderContent() {
        return orderContent;
    }

    public void setOrderContent(String orderContent) {
        this.orderContent = orderContent;
    }

    public float getOrderPrice() {
        return orderPrice;
    }

    public void setOrderPrice(float orderPrice) {
        this.orderPrice = orderPrice;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getExpertId() {
        return expertId;
    }

    public void setExpertId(int expertId) {
        this.expertId = expertId;
    }

}
