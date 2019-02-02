package com.gaoshou.android.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.gaoshou.android.R;
import com.gaoshou.android.entity.AnamnesisEntity;
import com.gaoshou.android.entity.ConsultationEntity;
import com.gaoshou.common.component.CommonListAdapter;
import com.gaoshou.common.component.CommonViewHolder;
import com.gaoshou.common.constant.CommonConstant;

public class ExpertAcceptOrdersAdapter extends CommonListAdapter<ConsultationEntity> {

    private static final int ITEM_TYPE_MY_CONSULTATION_TITLE = 0;
    private static final int ITEM_TYPE_MY_CONSULTATION = 1;
    private static final int ITEM_TYPE_CONSULTATION_TITLE = 2;
    private static final int ITEM_TYPE_CONSULTATION = 3;

    //预约我的订单
    private List<ConsultationEntity> myConsultationList;
    //符合我专长的订单
    private List<ConsultationEntity> consultationList;

    public ExpertAcceptOrdersAdapter(Context context, List<ConsultationEntity> dataList) {
        super(context, dataList);
    }

    public ExpertAcceptOrdersAdapter(Context context, List<ConsultationEntity> myConsultationList, List<ConsultationEntity> consultationList) {
        super(context, null);
        this.myConsultationList = myConsultationList;
        this.consultationList = consultationList;
    }

    @Override
    public int getCount() {
        int count = 0;
        if (null != myConsultationList && myConsultationList.size() > 0) {
            count += myConsultationList.size() + 1;
        }

        if (null != consultationList && consultationList.size() > 0) {
            count += consultationList.size() + 1;
        }
        return count;
    }

    @Override
    public ConsultationEntity getItem(int position) {
        ConsultationEntity item = null;

        if (null != myConsultationList && myConsultationList.size() > 0) {
            if (position - 1 >= 0 && position - 1 < myConsultationList.size()) {
                item = myConsultationList.get(position - 1);
            } else if (null != consultationList && consultationList.size() > 0) {
                position -= myConsultationList.size() + 1;
                if (position - 1 >= 0 && position - 1 < myConsultationList.size()) {
                    item = consultationList.get(position - 1);
                }
            }
        } else if (null != consultationList && consultationList.size() > 0) {
            if (position - 1 >= 0 && position - 1 < consultationList.size()) {
                item = consultationList.get(position - 1);
            }
        }

        return item;
    }

    @Override
    public int getItemViewType(int position) {
        if (null != myConsultationList && myConsultationList.size() > 0) {
            if (position == 0) {
                return ITEM_TYPE_MY_CONSULTATION_TITLE;
            } else if (position > 0 && position < myConsultationList.size() + 1) {
                return ITEM_TYPE_MY_CONSULTATION;
            } else if (position == myConsultationList.size() + 1) {
                return ITEM_TYPE_CONSULTATION_TITLE;
            } else if (position > myConsultationList.size() + 1) {
                return ITEM_TYPE_CONSULTATION;
            }

        } else {
            if (null != consultationList && consultationList.size() > 0) {
                if (position == 0) {
                    return ITEM_TYPE_CONSULTATION_TITLE;
                } else if (position > 0 && position < consultationList.size() + 1) {
                    return ITEM_TYPE_CONSULTATION;
                }
            }
        }

        return ITEM_TYPE_MY_CONSULTATION_TITLE;
    }

    @Override
    public int getViewTypeCount() {
        int count = 0;

        if (null != myConsultationList && myConsultationList.size() > 0) {
            count += 2;
        }

        if (null != consultationList && consultationList.size() > 0) {
            count += 2;
        }

        return count;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CommonViewHolder holder = null;
        int itemType = getItemViewType(position);
        switch (itemType) {
            case ITEM_TYPE_MY_CONSULTATION_TITLE:
                holder = CommonViewHolder.getViewHolder(context, position, convertView, parent, R.layout.headview_my_consultation_title);
                break;
            case ITEM_TYPE_MY_CONSULTATION:
                holder = CommonViewHolder.getViewHolder(context, position, convertView, parent, R.layout.item_consultation);
                ConsultationEntity myConsultationEntity = getItem(position);
                if (null != myConsultationEntity) {
                    bindConsultationItem(holder, myConsultationEntity);
                }
                break;
            case ITEM_TYPE_CONSULTATION_TITLE:
                holder = CommonViewHolder.getViewHolder(context, position, convertView, parent, R.layout.headview_consultation_title);
                break;
            case ITEM_TYPE_CONSULTATION:
                holder = CommonViewHolder.getViewHolder(context, position, convertView, parent, R.layout.item_consultation);
                //                if (null != myConsultationList && myConsultationList.size() > 0) {
                //                    position -= myConsultationList.size() + 1;
                //                }
                //                if (null != consultationList && position - 1 >= 0 && position - 1 < consultationList.size()) {
                //                    ConsultationEntity consultationEntity = consultationList.get(position - 1);
                ConsultationEntity consultationEntity = getItem(position);
                if (null != consultationEntity) {
                    bindConsultationItem(holder, consultationEntity);
                }
                //                }
                break;
            default:
                break;
        }
        return holder.getConvertView();
    }

    private void bindConsultationItem(CommonViewHolder holder, ConsultationEntity consultationEntity) {
        if (null != holder && consultationEntity != null) {
            //            AnamnesisEntity anamnesisEntity = consultationEntity.getAnamnesis();
            //            if (null != anamnesisEntity) {
            //                holder.setTextView(R.id.tv_anamnesis, anamnesisEntity.getName());
            //            }
            holder.setTextView(R.id.tv_anamnesis, consultationEntity.getAnamnesis());
            holder.setTextView(R.id.tv_symptom, consultationEntity.getSymptom());
            holder.setTextView(R.id.tv_illness, consultationEntity.getPatient_illness());

            int consultation = consultationEntity.getType();
            switch (consultation) {
                case CommonConstant.CONSULTATION_TYPE_CONSULTATION:
                    holder.getView(R.id.ll_consultation).setVisibility(View.VISIBLE);
                    holder.getView(R.id.ll_operation).setVisibility(View.GONE);
                    break;
                case CommonConstant.CONSULTATION_TYPE_OPERATION:
                    holder.getView(R.id.ll_consultation).setVisibility(View.GONE);
                    holder.getView(R.id.ll_operation).setVisibility(View.VISIBLE);
                    break;
                default:
                    break;
            }
        }
    }
}
