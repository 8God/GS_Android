package com.gaoshou.android.activity;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RatingBar;
import android.widget.TextView;

import com.alipay.sdk.app.PayTask;
import com.gaoshou.android.R;
import com.gaoshou.android.entity.AlipayOrderEntity;
import com.gaoshou.android.entity.ConsultationEntity;
import com.gaoshou.android.entity.DoctorEntity;
import com.gaoshou.android.entity.OrderEntity;
import com.gaoshou.common.alipay.PayResult;
import com.gaoshou.common.alipay.SignUtils;
import com.gaoshou.common.base.BaseActivity;
import com.gaoshou.common.base.GsApplication;
import com.gaoshou.common.constant.APIKey;
import com.gaoshou.common.constant.ServiceAPIConstant;
import com.gaoshou.common.network.CommonRequest;
import com.gaoshou.common.utils.EntityUtils;
import com.gaoshou.common.utils.NetworkUtil;
import com.gaoshou.common.utils.OpenFileUtil;

public class OrderConfirmActivity extends BaseActivity implements OnClickListener {
    //商户PID
    public static final String PARTNER = "2088612117404563";
    //商户收款账号
    public static final String SELLER = "info@kswiki.com";
    //商户私钥，pkcs8格式
    public static final String RSA_PRIVATE = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAJoDAQHVkOxP0sscLyWTL8IlOKWMq75+lvIXmJTGZJ3NsoMV2lNMX7vklOazvlPrZN7BZHkHkYIzjwxNWE1u+QS+i6vCslzmnRMoO/hFiZ7fHPkyifklXfmb/efhc2p06w3nzwtoVceASInTh6iHibGMaCjifpKlV6sl17lvoT79AgMBAAECgYB1v7ozZs8ofVcShvfc6I1pCAApQkXEnRBXA4dap9whcjT7V+fWK9w90WOuhtoLWzuBu6ZPimPLghPqOfA7M48ay9gv7HMhVt9dWVIgf0DmmtNeCEEu0S5ex9x82d2t36PRbcAtBVTBQK4OJKSQ3V1sAxylZS6TZ1CgcSTksyHdgQJBAMiWSwPjRlWE6c0VHyb/J0F1zAtS3zdCHrDDoK+54D3wNIvrkIvG9p+YwL3MUETnQIXxKBBuAfz9imBLDhQoQ1ECQQDEjtvH9Y0RnfWWInnBNa0cN64CIwwkypGHkmr3ghi2hDqo/4kZJL8hnhVHiubzErars8ThdCPIJCK0QNRk3t3tAkAG4WDhWUJoXI7Ighj3dXkbPbcqDEWr15DF72/rlyyh80NaKVJj+Qcsoki6Oe/m7SfBcGw3ZA6dZvUAKJLrDhaBAkAn+q61YzqIRMq4+NYu+E33mVOpV5uWuCUVoDBlm26PYSHVUfR+yrydh9voK1aCRmIlVnFLMiY9BSyR4UXSJoqZAkAhBxGmwYu4dI1sbMbkFeFShNNLpFUoic1xo5kFQLlWCNkcRue9zxQQp2jQTbLjSnibclptWxxk/53+ZuSUVZE6";
    //支付宝公钥
    public static final String RSA_PUBLIC = "";

    private static final int SDK_PAY_FLAG = 1;

    private static final int SDK_CHECK_FLAG = 2;

    //专家ID
    private int expertId;
    //订单ID
    private int orderId = -1;
    //测试的商品
    private String orderTitle;
    //该测试商品的详细描述
    private String orderContent;
    //价格
    private float orderPrice;
    
    private String certificationPath;

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    PayResult payResult = new PayResult((String) msg.obj);

                    // 支付宝返回此次支付结果及加签，建议对支付宝签名信息拿签约时支付宝提供的公钥做验签
                    String resultInfo = payResult.getResult();

                    String resultStatus = payResult.getResultStatus();

                    // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                    if (TextUtils.equals(resultStatus, "9000")) {
                        //更新服务器状态，并显示地址
                        updateOrderStatus();
                    } else {
                        // 判断resultStatus 为非“9000”则代表可能支付失败
                        // “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                        if (TextUtils.equals(resultStatus, "8000")) {
                            showToast("支付确认中");

                        } else {
                            // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                            showToast("支付失败");

                        }
                    }
                    break;
                }
            }
        };
    };

    private OrderEntity order;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_confirm);

        init();
    }

    private void init() {
        initToolbar(getString(R.string.actionbar_title_order_confirm));

        AlipayOrderEntity alipay = GsApplication.getInstance(getContext()).getAlipayOrderDetail();
        if (alipay != null) {
            expertId = alipay.getExpertId();
            orderId = alipay.getOrderId();
            orderTitle = alipay.getOrderTitle();
            orderContent = alipay.getOrderContent();
            orderPrice = alipay.getOrderPrice();
        }

        Log.i("testYJ", "expertId=" + expertId);
        Log.i("testYJ", "orderId=" + orderId);
        Log.i("testYJ", "orderTitle=" + orderTitle);
        Log.i("testYJ", "orderContent=" + orderContent);
        Log.i("testYJ", "orderPrice=" + orderPrice);

        initView();
    }

    private void initView() {
        //        dialog = new ProgressDialog(getContext());
        //        dialog.setMessage(getString(R.string.collecting));

        order = GsApplication.getInstance(getContext()).getOrder();
        fetchDoctor();

    }

    private void initOrderData(DoctorEntity doctor) {
        setImageView(R.id.iv_icon, doctor.getHeadPicPath(), R.drawable.common_icon_default_user_head);

        setTextView(R.id.tv_name, doctor.getName());
        setTextView(R.id.tv_professional_title, doctor.getTitle());
        setTextView(R.id.tv_department, doctor.getDept());
        setTextView(R.id.tv_intro, doctor.getIntro());

        RatingBar rb = findView(R.id.rtb_score);
        rb.setRating(Float.valueOf(doctor.getAvgScore()));

        findView(R.id.btn_authentication).setOnClickListener(this);
        findView(R.id.iv_slide).setOnClickListener(this);
        findView(R.id.btn_repine).setOnClickListener(this);
        findView(R.id.btn_play).setOnClickListener(this);

        setTextView(R.id.tv_mobile, "电话: " + doctor.getMobile());
        setTextView(R.id.tv_address, "地址: " + doctor.getAddress());

        int status = order.getStatus();
        if (status == OrderEntity.STATUS_COMPLETE || status == OrderEntity.STATUS_PAYMENT) {
            findView(R.id.ll_contact_way_container).setVisibility(View.VISIBLE);
            findView(R.id.ll_hide_contact_container).setVisibility(View.GONE);
            findView(R.id.ll_pay_container).setVisibility(View.GONE);
        }
        
        certificationPath  = doctor.getCertificationPath();
    }

    private void changeIntroState() {
        TextView intro = findView(R.id.tv_intro);
        if (intro != null) {
            if (intro.getVisibility() == View.GONE) {
                intro.setVisibility(View.VISIBLE);
            } else {
                intro.setVisibility(View.GONE);
            }
        }
    }

    private void fetchDoctor() {
        if (orderId != -1) {
            if (NetworkUtil.isNetworkAvaliable(getContext())) {
                CommonRequest fetchOrderRequest = new CommonRequest();
                fetchOrderRequest.setRequestApiName(ServiceAPIConstant.REQUEST_API_NAME_EXPERT_VIEW+"&"+APIKey.COMMON_EXPAND+"="+APIKey.COMMON_DOCTOR_FILES);
                fetchOrderRequest.setRequestID(ServiceAPIConstant.REQUEST_ID_EXPERT_VIEW);
                fetchOrderRequest.addRequestParam(APIKey.COMMON_ID, expertId);

                addRequestAsyncTask(fetchOrderRequest);
            } else {
                showToast(getString(R.string.network_error));
            }
        } else {
            showToast("订单ID有问题");
        }
    }

    private void updateOrderStatus() {
        if (orderId != -1) {
            if (NetworkUtil.isNetworkAvaliable(getContext())) {
                CommonRequest updateOrderStatusRequest = new CommonRequest();
                updateOrderStatusRequest.setRequestApiName(ServiceAPIConstant.REQUEST_API_NAME_ORDER_UPDATE);
                updateOrderStatusRequest.setRequestID(ServiceAPIConstant.REQUEST_ID_ORDER_UPDATE);
                updateOrderStatusRequest.setRequestMethod(CommonRequest.REQUEST_METHOD_PUT);
                updateOrderStatusRequest.addRequestParam(APIKey.COMMON_ID, orderId);
                updateOrderStatusRequest.addRequestParam(APIKey.COMMON_STATUS, OrderEntity.STATUS_PAYMENT);

                addRequestAsyncTask(updateOrderStatusRequest);
            } else {
                showToast(getString(R.string.network_error));
            }
        } else {
            showToast("订单ID有问题");
        }
    }

    @Override
    protected void onResponse(String requestID, boolean isSuccess, int statusCode, String message, Map<String, Object> data, Map<String, Object> additionalArgsmap) {
        if (requestID == ServiceAPIConstant.REQUEST_ID_EXPERT_VIEW) {
            if (isSuccess && statusCode == 200) {
                DoctorEntity expertTemp = EntityUtils.getDoctorEntity(data);
                if (expertTemp != null) {
                    initOrderData(expertTemp);
                    return;
                }
            } else {
                showToast(message);
            }
        } else if (requestID == ServiceAPIConstant.REQUEST_ID_ORDER_UPDATE) {
            if (isSuccess && statusCode == 200) {
                OrderEntity orderTemp = EntityUtils.getOrderEntity(data);
                if (orderTemp != null) {
                    Log.i("testYJ","orderTemp="+orderTemp);
                    int status = orderTemp.getStatus();
                    Log.i("testYJ","status="+status);
                    if (status == OrderEntity.STATUS_PAYMENT) {
                        Log.i("testYJ","status="+status);
                        findView(R.id.ll_contact_way_container).setVisibility(View.VISIBLE);
                        findView(R.id.ll_hide_contact_container).setVisibility(View.GONE);
                        findView(R.id.ll_pay_container).setVisibility(View.GONE);
                    }
                }
            } else {
                showToast(message);
            }
        }
    }

    public void pay() {
        // 订单
        String orderInfo = getOrderInfo(orderTitle, orderContent, String.valueOf(orderPrice));

        // 对订单做RSA 签名
        String sign = sign(orderInfo);
        try {
            // 仅需对sign 做URL编码
            sign = URLEncoder.encode(sign, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        // 完整的符合支付宝参数规范的订单信息
        final String payInfo = orderInfo + "&sign=\"" + sign + "\"&" + getSignType();

        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                // 构造PayTask 对象
                PayTask alipay = new PayTask((Activity) getContext());
                // 调用支付接口，获取支付结果
                String result = alipay.pay(payInfo);

                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };

        // 必须异步调用
        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }

    /**
     * create the order info. 创建订单信息
     * 
     */
    public String getOrderInfo(String subject, String body, String price) {
        // 签约合作者身份ID
        String orderInfo = "partner=" + "\"" + PARTNER + "\"";

        // 签约卖家支付宝账号
        orderInfo += "&seller_id=" + "\"" + SELLER + "\"";

        //        // 商户网站唯一订单号
        //        orderInfo += "&out_trade_no=" + "\"" + getOutTradeNo() + "\"";
        // 商户网站唯一订单号
        orderInfo += "&out_trade_no=" + "\"" + order.getId() + "\"";

        // 商品名称
        orderInfo += "&subject=" + "\"" + subject + "\"";

        // 商品详情
        orderInfo += "&body=" + "\"" + body + "\"";

        // 商品金额
        if (order.getType() == ConsultationEntity.CONSULTATION) {
            orderInfo += "&total_fee=" + "\"" + orderPrice + "\"";
        } else {
            orderInfo += "&total_fee=" + "\"" + orderPrice + "\"";
        }

        // 服务器异步通知页面路径
        orderInfo += "&notify_url=" + "\"" + "http://notify.msp.hk/notify.htm" + "\"";

        // 服务接口名称， 固定值
        orderInfo += "&service=\"mobile.securitypay.pay\"";

        // 支付类型， 固定值
        orderInfo += "&payment_type=\"1\"";

        // 参数编码， 固定值
        orderInfo += "&_input_charset=\"utf-8\"";

        // 设置未付款交易的超时时间
        // 默认30分钟，一旦超时，该笔交易就会自动被关闭。
        // 取值范围：1m～15d。
        // m-分钟，h-小时，d-天，1c-当天（无论交易何时创建，都在0点关闭）。
        // 该参数数值不接受小数点，如1.5h，可转换为90m。
        orderInfo += "&it_b_pay=\"30m\"";

        // extern_token为经过快登授权获取到的alipay_open_id,带上此参数用户将使用授权的账户进行支付
        // orderInfo += "&extern_token=" + "\"" + extern_token + "\"";

        // 支付宝处理完请求后，当前页面跳转到商户指定页面的路径，可空
        // orderInfo += "&return_url=\"m.alipay.com\"";

        // 调用银行卡支付，需配置此参数，参与签名， 固定值 （需要签约《无线银行卡快捷支付》才能使用）
        // orderInfo += "&paymethod=\"expressGateway\"";

        return orderInfo;
    }

    /**
     * get the out_trade_no for an order. 生成商户订单号，该值在商户端应保持唯一（可自定义格式规范）
     * 
     */
    public String getOutTradeNo() {
        SimpleDateFormat format = new SimpleDateFormat("MMddHHmmss", Locale.getDefault());
        Date date = new Date();
        String key = format.format(date);

        Random r = new Random();
        key = key + r.nextInt();
        key = key.substring(0, 15);
        return key;
    }

    /**
     * sign the order info. 对订单信息进行签名
     * 
     * @param content
     *            待签名订单信息
     */
    public String sign(String content) {
        return SignUtils.sign(content, RSA_PRIVATE);
    }

    /**
     * get the sign type we use. 获取签名方式
     * 
     */
    public String getSignType() {
        return "sign_type=\"RSA\"";
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_authentication:
                if(certificationPath!=null){
                    OpenFileUtil.openFile(getContext(), certificationPath);
                }
                break;
            case R.id.iv_slide:
                changeIntroState();
                break;
            case R.id.btn_play:
                    pay();
                    break;
        }
    }

    @Override
    protected void onDestroy() {
        GsApplication.getInstance(getContext()).setAlipayOrderDetail(null);
        GsApplication.getInstance(getContext()).setOrder(null);
        super.onDestroy();
    }
}
