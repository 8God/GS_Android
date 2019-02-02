package com.gaoshou.android.adapter;

import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ImageView;

import com.gaoshou.android.R;
import com.gaoshou.android.activity.CancelOrderActivity;
import com.gaoshou.android.activity.CaseDetailActivity;
import com.gaoshou.android.activity.MyCasesActivity;
import com.gaoshou.android.entity.ConsultationEntity;
import com.gaoshou.android.entity.OrderEntity;
import com.gaoshou.common.base.GsApplication;
import com.gaoshou.common.component.CommonViewHolder;
import com.gaoshou.common.constant.CommonConstant;

public class MyCasesExpandableAdapter extends BaseExpandableListAdapter {
    private final int TYPE_CONSULTATION = 0;
    private final int TYPE_CONSULTATION_OPERATION = 1;
    private final int TYPE_ORDER = 0;
    private final int TYPE_ORDERED = 1;

    private List<String> titles;
    private List<List<OrderEntity>> value;
    private Context context;
    private int status;

    public MyCasesExpandableAdapter(Context context, List<String> titles, List<List<OrderEntity>> value, int status) {
        this.titles = titles;
        this.value = value;
        this.context = context;
        this.status = status;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return value.get(groupPosition).get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        CommonViewHolder viewHolder = null;
        int layoutId = -1;
        if (status == MyCasesActivity.UNFINISHED_STATUS) {
            layoutId = R.layout.item_my_case_unfinished;
        } else {
            layoutId = R.layout.item_my_case_finished;
        }
        viewHolder = initUnfinishedLayout(groupPosition, childPosition, convertView, parent, layoutId);

        return viewHolder == null ? null : viewHolder.getConvertView();
    }

    private CommonViewHolder initUnfinishedLayout(int groupPosition, int childPosition, View convertView, ViewGroup parent, int LayoutId) {
        CommonViewHolder viewHolder = CommonViewHolder.getViewHolder(context, childPosition, convertView, parent, LayoutId);
        Log.i("testYJ", "groupPosition-->" + groupPosition);
        Log.i("testYJ", "childPosition-->" + childPosition);
        if (viewHolder != null) {
            if (value != null && value.get(groupPosition) != null) {
                OrderEntity order = value.get(groupPosition).get(childPosition);
                Log.i("testYJ", "order=" + order);
                if (order != null) {
                    ImageView type_iv = viewHolder.getView(R.id.iv_type);
                    final Button cancel = viewHolder.getView(R.id.btn_cancel);
                    if (order.getType() == TYPE_CONSULTATION) {
                        type_iv.setImageResource(R.drawable.list_consultation);
                        viewHolder.setTextView(R.id.tv_type, context.getString(R.string.tv_consultation));
                    } else {
                        type_iv.setImageResource(R.drawable.list_surgery);
                        viewHolder.setTextView(R.id.tv_type, context.getString(R.string.tv_consultation_operation));
                    }
                    if (status == MyCasesActivity.UNFINISHED_STATUS) {
                        if (cancel != null) {
                            Log.i("testYJ", "cancel!=null");
                            if (groupPosition == TYPE_ORDER) {
                                cancel.setText(context.getString(R.string.btn_cancel_order));
                            } else if (groupPosition == TYPE_ORDERED) {
                                cancel.setText(context.getString(R.string.btn_cancel_operation));
                            }
                        }
                    } else {
                        if (groupPosition == TYPE_ORDER) {
                            viewHolder.getView(R.id.btn_evaluate).setOnClickListener(new MyOnClickListener(context, order, groupPosition));
                            viewHolder.getView(R.id.btn_repine).setOnClickListener(new MyOnClickListener(context, order, groupPosition));
                        } else {
                            viewHolder.getView(R.id.rl_btn_container).setVisibility(View.GONE);
                        }
                    }
                    viewHolder.getView(R.id.ll_container).setOnClickListener(new MyOnClickListener(context, order, groupPosition));
                    ConsultationEntity consultation = order.getConsultation();
                    if (consultation != null) {
                        //                        if (consultation.getAnamnesis() != null)
                        //                            viewHolder.setTextView(R.id.tv_anamnesis, consultation.getAnamnesis().getName());
                        //                        if (consultation.getSymptom() != null)
                        //                            viewHolder.setTextView(R.id.tv_symptom, consultation.getSymptom().getName());
                        viewHolder.setTextView(R.id.tv_anamnesis, consultation.getAnamnesis());
                        viewHolder.setTextView(R.id.tv_symptom, consultation.getSymptom());
                        if (consultation.getPatient_illness() != null)
                            viewHolder.setTextView(R.id.tv_illness, consultation.getPatient_illness());
                    }
                    if (cancel != null)
                        cancel.setOnClickListener(new MyOnClickListener(context, order, groupPosition));
                }
            }
        }
        return viewHolder;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return value.get(groupPosition).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return titles.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return titles.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        CommonViewHolder viewHolder = CommonViewHolder.getViewHolder(context, groupPosition, convertView, parent, R.layout.item_my_cases_title);
        viewHolder.setTextView(R.id.tv_my_cases_title, titles.get(groupPosition));
        return viewHolder.getConvertView();
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    private class MyOnClickListener implements OnClickListener {
        private Context context;
        private OrderEntity order;
        private int groupPosition;

        public MyOnClickListener(Context context, OrderEntity order, int groupPosition) {
            this.context = context;
            this.order = order;
            this.groupPosition = groupPosition;
        }

        @Override
        public void onClick(View v) {
            GsApplication.getInstance(context).setOrder(order);
            GsApplication.getInstance(context).setOrderDetailUserType(groupPosition);
            Intent intent = null;
            if (context != null) {
                switch (v.getId()) {
                    case R.id.btn_cancel:
                        Log.i("testYJ", "cancel");
                        String text = ((Button) v).getText().toString().trim();
                        if (context.getString(R.string.btn_cancel_operation).equals(text)) {
                            intent = new Intent(context, CaseDetailActivity.class);
                            intent.putExtra(context.getString(R.string.intent_case_detail_location), MyCasesActivity.LOCATION_CANCEL);
                            if (context instanceof Activity)
                                ((Activity) context).startActivityForResult(intent, MyCasesActivity.TO_CASE_DETAIL_ACTIVITY);
                        } else {
                            intent = new Intent(context, CancelOrderActivity.class);
                            if (context instanceof Activity)
                                ((Activity) context).startActivityForResult(intent, MyCasesActivity.TO_CANCEL_ORDER_ACTIVITY);
                        }
                        break;
                    case R.id.btn_evaluate:
                        Log.i("testYJ", "evaluate");
                        intent = new Intent(context, CaseDetailActivity.class);
                        intent.putExtra(context.getString(R.string.intent_case_detail_location), MyCasesActivity.LOCATION_EVALUATE);
                        ((Activity) context).startActivityForResult(intent, MyCasesActivity.TO_CASE_DETAIL_ACTIVITY);
                        break;
                    case R.id.btn_repine:
                        Log.i("testYJ", "repine");
                        intent = new Intent(context, CaseDetailActivity.class);
                        intent.putExtra(context.getString(R.string.intent_case_detail_location), MyCasesActivity.LOCATION_REPINED);
                        ((Activity) context).startActivityForResult(intent, MyCasesActivity.TO_CASE_DETAIL_ACTIVITY);
                        break;
                    case R.id.ll_container:
                        Log.i("testYJ", "container");
                        intent = new Intent(context, CaseDetailActivity.class);
                        ((Activity) context).startActivityForResult(intent, MyCasesActivity.TO_CASE_DETAIL_ACTIVITY);
                }
            }
        }
    }
}
