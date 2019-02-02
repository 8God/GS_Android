package com.gaoshou.android.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.gaoshou.android.R;
import com.gaoshou.android.entity.BaseEntity;
import com.gaoshou.android.entity.ConsultationEntity;
import com.gaoshou.android.entity.DoctorEntity;
import com.gaoshou.android.entity.OrderEntity;
import com.gaoshou.common.base.GsApplication;
import com.gaoshou.common.component.CommonListAdapter;
import com.gaoshou.common.component.CommonViewHolder;
import com.gaoshou.common.constant.CommonConstant;
import com.gaoshou.common.constant.OrderStatus;

public class MsgNotificationAdapter extends CommonListAdapter<BaseEntity> {

    private static final int ITEM_DOCTOR_EXPERT_ACCEPT_ORDER = 0;
    private static final int ITEM_DOCTOR_EXPERT_REFUSE_CONSULTATION = 1;
    private static final int ITEM_DOCTOR_EXPERT_CANCEL_ORDER = 2;
    private static final int ITEM_DOCTOR_EXPERT_CANCEL_OPERATION = 3;
    private static final int ITEM_DOCTOR_UNPAY = 8;

    private static final int ITEM_EXPERT_DOCTOR_COUNSEL_ME = 4;
    private static final int ITEM_EXPERT_DOCTOR_CHOOSE_ME = 5;
    private static final int ITEM_EXPERT_DOCTOR_PAY_ORDER = 6;
    private static final int ITEM_EXPERT_DOCTOR_CANCEL_ORDER = 7;

    private int logonType = CommonConstant.LOGON_TYPE_DOCTOR;

    public MsgNotificationAdapter(Context context, List<BaseEntity> dataList) {
        super(context, dataList);

        logonType = GsApplication.getInstance().getLogonType();
    }

    @Override
    public int getViewTypeCount() {
        return 9;
    }

    @Override
    public int getItemViewType(int position) {
        int itemType = ITEM_EXPERT_DOCTOR_CANCEL_ORDER;
        BaseEntity baseEntity = getItem(position);
        if (null != baseEntity) {
            if (baseEntity instanceof ConsultationEntity) {
                ConsultationEntity consultation = ((ConsultationEntity) baseEntity);
                switch (consultation.getStatus()) {
                    case OrderStatus.CONSULTATION_STATUS_INIT:
                        itemType = ITEM_EXPERT_DOCTOR_COUNSEL_ME;
                        break;
                    case OrderStatus.CONSULTATION_STATUS_CANCEL:
                        itemType = ITEM_DOCTOR_EXPERT_REFUSE_CONSULTATION;
                        break;
                    default:
                        break;
                }
            } else if (baseEntity instanceof OrderEntity) {
                OrderEntity order = ((OrderEntity) baseEntity);
                switch (order.getStatus()) {
                    case OrderStatus.ORDER_STATUS_INIT:
                        itemType = ITEM_DOCTOR_EXPERT_ACCEPT_ORDER;
                        break;
                    case OrderStatus.ORDER_STATUS_CANCEL:
                        if (logonType == CommonConstant.LOGON_TYPE_DOCTOR) {
                            itemType = ITEM_DOCTOR_EXPERT_CANCEL_ORDER;
                        } else if (logonType == CommonConstant.LOGON_TYPE_EXPERT) {
                            itemType = ITEM_EXPERT_DOCTOR_CANCEL_ORDER;
                        }
                        break;
                    case OrderStatus.ORDER_STATUS_CANCEL_OPERATION:
                        itemType = ITEM_DOCTOR_EXPERT_CANCEL_OPERATION;
                        break;
                    case OrderStatus.ORDER_STATUS_UNPAY:
                        if (logonType == CommonConstant.LOGON_TYPE_DOCTOR) {
                            itemType = ITEM_DOCTOR_UNPAY;
                        } else if (logonType == CommonConstant.LOGON_TYPE_EXPERT) {
                            itemType = ITEM_EXPERT_DOCTOR_CHOOSE_ME;
                        }
                        break;
                    case OrderStatus.ORDER_STATUS_PAY:
                        itemType = ITEM_EXPERT_DOCTOR_PAY_ORDER;
                        break;
                    default:
                        break;
                }
            }
        }

        return itemType;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CommonViewHolder holder = CommonViewHolder.getViewHolder(context, position, convertView, parent, R.layout.item_msg_notification);
        int itemType = getItemViewType(position);
        BaseEntity baseEntity = getItem(position);
        switch (itemType) {
            case ITEM_DOCTOR_EXPERT_ACCEPT_ORDER:
                OrderEntity expertAcceptOrder = ((OrderEntity) baseEntity);
                bindExpertAcceptOrder(holder, expertAcceptOrder, position);
                break;
            case ITEM_DOCTOR_EXPERT_REFUSE_CONSULTATION:
                ConsultationEntity refuseConsultation = ((ConsultationEntity) baseEntity);
                bindExpertRefuseConsultation(holder, refuseConsultation, position);
                break;
            case ITEM_DOCTOR_EXPERT_CANCEL_ORDER:
                OrderEntity cancelOrder = ((OrderEntity) baseEntity);
                bindExpertCancelOrder(holder, cancelOrder, position);
                break;
            case ITEM_DOCTOR_EXPERT_CANCEL_OPERATION:
                OrderEntity cancelOperationOrder = ((OrderEntity) baseEntity);
                bindExpertCancelOperation(holder, cancelOperationOrder, position);
                break;
            case ITEM_DOCTOR_UNPAY:
                OrderEntity doctorUnpayOrder = ((OrderEntity) baseEntity);
                bindDoctorUnpay(holder, doctorUnpayOrder, position);
                break;
            case ITEM_EXPERT_DOCTOR_COUNSEL_ME:
                ConsultationEntity counselConsultation = ((ConsultationEntity) baseEntity);
                bindDoctorCounselMe(holder, counselConsultation, position);
                break;
            case ITEM_EXPERT_DOCTOR_CHOOSE_ME:
                OrderEntity chooseMeOrder = ((OrderEntity) baseEntity);
                bindDoctorChooseMe(holder, chooseMeOrder, position);
                break;
            case ITEM_EXPERT_DOCTOR_PAY_ORDER:
                OrderEntity doctorPayOrder = ((OrderEntity) baseEntity);
                bindDoctorPayOrder(holder, doctorPayOrder, position);
                break;
            case ITEM_EXPERT_DOCTOR_CANCEL_ORDER:
                OrderEntity doctorCancelOrder = ((OrderEntity) baseEntity);
                bindDoctorCancelOrder(holder, doctorCancelOrder, position);
                break;

            default:
                break;
        }
        return holder.getConvertView();
    }

    /**
     * 订单：专家接非即时订单提醒； *订单初始化状态* （咨询预约成功：您成功预约XX专家，预约时间XXXX年XX月XX日，请确认。）
     * 
     * @param holder
     * @param expertAcceptOrder
     * @param position
     */
    private void bindExpertAcceptOrder(CommonViewHolder holder, OrderEntity expertAcceptOrder, int position) {
        if (null != holder && expertAcceptOrder != null) {
            DoctorEntity orderDoctor = expertAcceptOrder.getOrder_doctor();
            if (null != orderDoctor) {
                ConsultationEntity consultationEntity = expertAcceptOrder.getConsultation();
                if (null != consultationEntity) {
                    String orderAt = consultationEntity.getOrder_at();
                    String content = "您成功预约" + orderDoctor.getName() + "专家，预约时间" + orderAt + "，请确认。";
                    holder.setImageView(R.id.sriv_user_head_pic, orderDoctor.getHeadPicPath(), R.drawable.common_icon_default_user_head);
                    holder.setTextView(R.id.tv_msg_content, content);
                }
            }
            holder.setTextView(R.id.tv_msg_title, "咨询预约成功");
            holder.setTextView(R.id.tv_msg_time, expertAcceptOrder.getCreated_at());
        }
    }

    /**
     * 咨询：拒绝咨询； *取消咨询* （专家拒绝咨询：XX专家拒绝了您的订单）
     * 
     * @param holder
     * @param refuseConsultation
     * @param position
     */
    private void bindExpertRefuseConsultation(CommonViewHolder holder, ConsultationEntity refuseConsultation, int position) {
        if (null != holder && refuseConsultation != null) {
            DoctorEntity expert = refuseConsultation.getExpert();

            if (null != expert) {
                String content = expert.getName() + "专家拒绝了您的订单";
                holder.setImageView(R.id.sriv_user_head_pic, expert.getHeadPicPath(), R.drawable.common_icon_default_user_head);
                holder.setTextView(R.id.tv_msg_content, content);
            }

            holder.setTextView(R.id.tv_msg_title, "专家拒绝咨询");
            holder.setTextView(R.id.tv_msg_time, refuseConsultation.getCreated_at());
        }
    }

    /**
     * 订单：医生取消订单； *订单取消状态* （退款提醒：您已成功取消对XX专家的订单，费用将会在三到五个工作日退还您的账户。）
     * 
     * @param holder
     * @param cancelOrder
     * @param position
     */
    private void bindExpertCancelOrder(CommonViewHolder holder, OrderEntity cancelOrder, int position) {
        if (null != holder && cancelOrder != null) {
            DoctorEntity orderDoctor = cancelOrder.getOrder_doctor();
            if (null != orderDoctor) {
                String content = "您已成功取消对" + orderDoctor.getName() + "专家的订单，费用将会在三到五个工作日退还您的账户。";
                holder.setImageView(R.id.sriv_user_head_pic, orderDoctor.getHeadPicPath(), R.drawable.common_icon_default_user_head);
                holder.setTextView(R.id.tv_msg_content, content);
            }
            holder.setTextView(R.id.tv_msg_title, "退款提醒");
            holder.setTextView(R.id.tv_msg_time, cancelOrder.getCreated_at());
        }
    }

    /**
     * 订单：取消手术； *取消手术* （专家取消手术：XX专家取消了手术，手术费用将会在三到五个工作日退还您的账户。）
     * 
     * @param holder
     * @param cancelOperationOrder
     * @param position
     */
    private void bindExpertCancelOperation(CommonViewHolder holder, OrderEntity cancelOperationOrder, int position) {
        if (null != holder && cancelOperationOrder != null) {
            DoctorEntity orderDoctor = cancelOperationOrder.getOrder_doctor();
            if (null != orderDoctor) {
                String content = orderDoctor.getName() + "专家取消了手术，手术费用将会在三到五个工作日退还您的账户。";
                holder.setImageView(R.id.sriv_user_head_pic, orderDoctor.getHeadPicPath(), R.drawable.common_icon_default_user_head);
                holder.setTextView(R.id.tv_msg_content, content);
            }
            holder.setTextView(R.id.tv_msg_title, "专家取消手术");
            holder.setTextView(R.id.tv_msg_time, cancelOperationOrder.getCreated_at());
        }
    }

    /**
     * 订单：医生未支付； *订单未付款状态* （支付提醒：您预约XX专家的订单尚未付款，请及时支付。）
     * 
     * @param holder
     * @param doctorUnpayOrder
     * @param position
     */
    private void bindDoctorUnpay(CommonViewHolder holder, OrderEntity doctorUnpayOrder, int position) {
        if (null != holder && doctorUnpayOrder != null) {
            DoctorEntity orderDoctor = doctorUnpayOrder.getOrder_doctor();
            if (null != orderDoctor) {
                String content = "您预约" + orderDoctor.getName() + "专家的订单尚未付款，请及时支付。";
                holder.setImageView(R.id.sriv_user_head_pic, orderDoctor.getHeadPicPath(), R.drawable.common_icon_default_user_head);
                holder.setTextView(R.id.tv_msg_content, content);
            }
            holder.setTextView(R.id.tv_msg_title, "支付提醒");
            holder.setTextView(R.id.tv_msg_time, doctorUnpayOrder.getCreated_at());
        }
    }

    /**
     * 咨询：医生指定我的预约； *咨询初始化状态* （咨询预约：XX医生预约了您，预约时间XXXX年XX月XX日，请确认。）
     * 
     * @param holder
     * @param counselConsultation
     * @param position
     */
    private void bindDoctorCounselMe(CommonViewHolder holder, ConsultationEntity counselConsultation, int position) {
        if (null != holder && counselConsultation != null) {
            DoctorEntity doctor = counselConsultation.getDoctor();
            if (null != doctor) {
                String content = doctor.getName() + "医生预约了您，预约时间" + counselConsultation.getOrder_at() + "，请确认。";
                holder.setImageView(R.id.sriv_user_head_pic, doctor.getHeadPicPath(), R.drawable.common_icon_default_user_head);
                holder.setTextView(R.id.tv_msg_content, content);
            }
            holder.setTextView(R.id.tv_msg_title, "咨询预约");
            holder.setTextView(R.id.tv_msg_time, counselConsultation.getCreated_at());
        }
    }

    /**
     * 订单：医生选择专家； *订单未付款状态* （医生选择咨询：在XX患者的咨询订单中，XX医生选择了您。）
     * 
     * @param holder
     * @param chooseMeOrder
     * @param position
     */
    private void bindDoctorChooseMe(CommonViewHolder holder, OrderEntity chooseMeOrder, int position) {
        if (null != holder && chooseMeOrder != null) {
            DoctorEntity doctor = chooseMeOrder.getDoctor();
            if (null != doctor) {
                ConsultationEntity consultationEntity = chooseMeOrder.getConsultation();
                if (null != consultationEntity) {
                    String content = "在" + consultationEntity.getPatient_name() + "患者的咨询订单中，" + doctor.getName() + "医生选择了您。";
                    holder.setImageView(R.id.sriv_user_head_pic, doctor.getHeadPicPath(), R.drawable.common_icon_default_user_head);
                    holder.setTextView(R.id.tv_msg_content, content);
                }
            }
            holder.setTextView(R.id.tv_msg_title, "医生选择咨询");
            holder.setTextView(R.id.tv_msg_time, chooseMeOrder.getCreated_at());
        }
    }

    /**
     * 订单：医生付款； *订单已付款状态* （医生付款：XX医生已对关于XX患者的会诊订单付款。）
     * 
     * @param holder
     * @param doctorPayOrder
     * @param position
     */
    private void bindDoctorPayOrder(CommonViewHolder holder, OrderEntity doctorPayOrder, int position) {
        if (null != holder && doctorPayOrder != null) {
            DoctorEntity doctor = doctorPayOrder.getDoctor();
            if (null != doctor) {
                ConsultationEntity consultationEntity = doctorPayOrder.getConsultation();
                if (null != consultationEntity) {
                    String content = doctor.getName() + "医生已对关于" + consultationEntity.getPatient_name() + "患者的会诊订单付款。";
                    holder.setImageView(R.id.sriv_user_head_pic, doctor.getHeadPicPath(), R.drawable.common_icon_default_user_head);
                    holder.setTextView(R.id.tv_msg_content, content);
                }
            }
            holder.setTextView(R.id.tv_msg_title, "医生付款");
            holder.setTextView(R.id.tv_msg_time, doctorPayOrder.getCreated_at());
        }
    }

    /**
     * 订单：医生取消订单； *订单取消状态* （医生取消订单：XX医生取消了关于XX患者的咨询。）
     * 
     * @param holder
     * @param doctorCancelOrder
     * @param position
     */
    private void bindDoctorCancelOrder(CommonViewHolder holder, OrderEntity doctorCancelOrder, int position) {
        if (null != holder && doctorCancelOrder != null) {
            DoctorEntity doctor = doctorCancelOrder.getDoctor();
            if (null != doctor) {
                ConsultationEntity consultationEntity = doctorCancelOrder.getConsultation();
                if (null != consultationEntity) {
                    String content = doctor.getName() + "医生取消了关于" + consultationEntity.getPatient_name() + "患者的咨询。";
                    holder.setImageView(R.id.sriv_user_head_pic, doctor.getHeadPicPath(), R.drawable.common_icon_default_user_head);
                    holder.setTextView(R.id.tv_msg_content, content);
                }
            }
            holder.setTextView(R.id.tv_msg_title, "医生取消订单");
            holder.setTextView(R.id.tv_msg_time, doctorCancelOrder.getCreated_at());
        }
    }

}
