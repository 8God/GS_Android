package com.gaoshou.android.adapter;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.gaoshou.android.R;
import com.gaoshou.android.activity.OrderConfirmActivity;
import com.gaoshou.android.activity.WaitExpertActivity;
import com.gaoshou.android.entity.AlipayOrderEntity;
import com.gaoshou.android.entity.ConsultationEntity;
import com.gaoshou.android.entity.DoctorEntity;
import com.gaoshou.android.entity.OrderEntity;
import com.gaoshou.common.base.GsApplication;
import com.gaoshou.common.component.BaseProgressDialog;
import com.gaoshou.common.component.CommonListAdapter;
import com.gaoshou.common.component.CommonViewHolder;
import com.gaoshou.common.constant.APIKey;
import com.gaoshou.common.constant.CommonConstant;
import com.gaoshou.common.constant.OrderStatus;
import com.gaoshou.common.constant.ServiceAPIConstant;
import com.gaoshou.common.network.CommonRequest;
import com.gaoshou.common.network.RestAPIRequester;
import com.gaoshou.common.network.RestAPIRequester.OnResponseCallback;
import com.gaoshou.common.utils.TypeUtil;

public class WaitingExpertsAdapter extends CommonListAdapter<OrderEntity> {

    public WaitingExpertsAdapter(Context context, List<OrderEntity> dataList) {
        super(context, dataList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CommonViewHolder holder = CommonViewHolder.getViewHolder(context, position, convertView, parent, R.layout.item_wait_expert_list);

        OrderEntity order = getItem(position);
        if (null != order) {
            bindItemView(holder, order);
        }

        return holder.getConvertView();
    }

    private void bindItemView(final CommonViewHolder holder, final OrderEntity order) {
        DoctorEntity orderDoctor = order.getOrder_doctor();
        if (null != orderDoctor) {
            holder.setTextView(R.id.tv_name, orderDoctor.getName());
            holder.setTextView(R.id.tv_title, orderDoctor.getTitle());
            holder.setTextView(R.id.tv_current_address, orderDoctor.getCurrentAddress());
            holder.setTextView(R.id.tv_consultation_fee, orderDoctor.getConsultationFee() + "元");
            holder.setTextView(R.id.tv_operation_fee, orderDoctor.getConsultationOperationFee() + "元");

            holder.setImageView(R.id.imv_expert_head_photo, orderDoctor.getHeadPicPath(), R.drawable.common_icon_default_user_head);
        }
        final ConsultationEntity consultation = order.getConsultation();
        if (null != consultation) {
            int consultationType = consultation.getType();

            if (consultationType == CommonConstant.CONSULTATION_TYPE_CONSULTATION) {
                holder.getView(R.id.ll_operation_type).setVisibility(View.GONE);
            }
        }

        holder.getView(R.id.btn_select).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (null != order) {
                    final BaseProgressDialog bpDialog = new BaseProgressDialog(context);
                    bpDialog.setCancelable(false);
                    bpDialog.setMessage("下单中");

                    CommonRequest updateOrderStatus = new CommonRequest();
                    updateOrderStatus.setRequestApiName(ServiceAPIConstant.REQUEST_API_NAME_ORDER_UPDATE);
                    updateOrderStatus.setRequestID(ServiceAPIConstant.REQUEST_ID_ORDER_UPDATE);
                    updateOrderStatus.addRequestParam(APIKey.COMMON_STATUS, OrderStatus.ORDER_STATUS_UNPAY);
                    updateOrderStatus.addRequestParam(APIKey.COMMON_ID, order.getId());

                    RestAPIRequester restAPIRequester = new RestAPIRequester(context);
                    restAPIRequester.setOnResponseCallback(new OnResponseCallback() {

                        @Override
                        public void onSuccess(boolean isSuccess, int statusCode, String message, Map<String, Object> data) {
                            boolean isCreateOrderSuccess = false;
                            if (null != data && isSuccess) {
                                order.setStatus(OrderStatus.ORDER_STATUS_UNPAY);
                                if (null != order) {
                                    int orderId = order.getId();
                                    int expertId = order.getOrder_doctor_id();
                                    int consultationType = consultation.getType();
                                    DoctorEntity orderdoctor = order.getOrder_doctor();
                                    DoctorEntity doctor = order.getDoctor();
                                    Log.i("cth", "consultationType = " + consultationType);
                                    Log.i("cth", "orderdoctor = " + orderdoctor);
                                    Log.i("cth", "doctor = " + doctor);
                                    if (null != orderdoctor && doctor != null) {
                                        float orderPrice = 0f;
                                        switch (consultationType) {
                                            case CommonConstant.CONSULTATION_TYPE_CONSULTATION:
                                                if (!TextUtils.isEmpty(orderdoctor.getConsultationFee())) {
                                                    orderPrice = Float.parseFloat(orderdoctor.getConsultationFee());
                                                }
                                                break;
                                            case CommonConstant.CONSULTATION_TYPE_OPERATION:
                                                if (!TextUtils.isEmpty(orderdoctor.getConsultationOperationFee())) {
                                                    orderPrice = Float.parseFloat(orderdoctor.getConsultationOperationFee());
                                                }
                                                break;
                                            default:
                                                break;
                                        }
                                        Log.i("cth", "orderPrice = " + orderPrice);
                                        if (orderPrice > 0) {
                                            isCreateOrderSuccess = true;
                                            String orderTitle = doctor.getName() + "医生支付订单";
                                            String orderContent = doctor.getName() + "医生选择了" + orderdoctor.getName() + "专家，并支付订单";
                                            //TODO 跳转支付页面
                                            AlipayOrderEntity alipayOrderDetail = new AlipayOrderEntity();
                                            alipayOrderDetail.setExpertId(expertId);
                                            alipayOrderDetail.setOrderId(orderId);
                                            alipayOrderDetail.setOrderPrice(orderPrice);
                                            alipayOrderDetail.setOrderTitle(orderTitle);
                                            alipayOrderDetail.setOrderContent(orderContent);
                                            GsApplication.getInstance(context).setAlipayOrderDetail(alipayOrderDetail);
                                            GsApplication.getInstance(context).setOrder(order);
                                            context.startActivity(new Intent(context, OrderConfirmActivity.class));

                                            if (null != context && context instanceof WaitExpertActivity) {
                                                ((WaitExpertActivity) context).finish();
                                            }
                                        }
                                    }
                                }
                                if (null != consultation) {
                                    updateConsultationStatus(consultation.getId());
                                }
                            }
                            if (isCreateOrderSuccess) {
                                showToast("下单成功");

                            } else {
                                showToast("下单失败");
                            }

                            if (null != bpDialog && bpDialog.isShowing()) {
                                bpDialog.dismiss();
                            }

                        }

                        @Override
                        public void onFailure(boolean isSuccess, int statusCode, String message, Map<String, Object> data) {
                            showToast("下单失败");

                            if (null != bpDialog && bpDialog.isShowing()) {
                                bpDialog.dismiss();
                            }
                        }
                    });

                    restAPIRequester.sendRequest(updateOrderStatus);

                    bpDialog.show();

                }
            }
        });
    }

    private void updateConsultationStatus(int consultationId) {
        CommonRequest updateConsultationStatus = new CommonRequest();
        updateConsultationStatus.setRequestApiName(ServiceAPIConstant.REQUEST_API_NAME_CONSULTATION_UPDATE);
        updateConsultationStatus.setRequestID(ServiceAPIConstant.REQUEST_ID_CONSULTATION_UPDATE);
        updateConsultationStatus.addRequestParam(APIKey.COMMON_ID, consultationId);
        updateConsultationStatus.addRequestParam(APIKey.COMMON_STATUS, OrderStatus.CONSULTATION_STATUS_FINISH);

        addRequestAsyncTask(updateConsultationStatus);
    }
}
