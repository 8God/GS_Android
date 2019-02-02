package com.gaoshou.common.constant;

public class OrderStatus {
    //'consultation-status' => [ 0 => '冻结', 1 => '初始值', 4 => '已取消', 9 => '已完成',]

    public static final int CONSULTATION_STATUS_DISABLE = 0;
    public static final int CONSULTATION_STATUS_INIT = 1;
    public static final int CONSULTATION_STATUS_CANCEL = 4;
    public static final int CONSULTATION_STATUS_FINISH = 9;

    // 'order-status' => [ 0 => '冻结', 1 => '初始值', 2 => '未付款', 3 => '已付款', 4 => '取消手术', 5 => '未付款取消',6 => '取消订单', 9 => '已完成', ]

    public static final int ORDER_STATUS_DISABLE = 0;
    public static final int ORDER_STATUS_INIT = 1;
    public static final int ORDER_STATUS_UNPAY = 2;
    public static final int ORDER_STATUS_PAY = 3;
    public static final int ORDER_STATUS_CANCEL_OPERATION = 4;
    public static final int ORDER_STATUS_UNPAY_CANCEL = 5;
    public static final int ORDER_STATUS_CANCEL = 6;
    public static final int ORDER_STATUS_FINISH = 9;
}
