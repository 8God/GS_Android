package com.gaoshou.android.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;

import com.gaoshou.android.R;
import com.gaoshou.android.entity.DoctorEntity;
import com.gaoshou.common.component.CommonListAdapter;
import com.gaoshou.common.component.CommonViewHolder;
import com.gaoshou.common.utils.TypeUtil;

public class ExpertAdapter extends CommonListAdapter<DoctorEntity> {

    public ExpertAdapter(Context context, List<DoctorEntity> dataList) {
        super(context, dataList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CommonViewHolder viewHolder = CommonViewHolder.getViewHolder(context, position, convertView, parent, R.layout.item_experts_list);
        if (viewHolder != null && dataList != null && dataList.get(position) != null) {
            viewHolder.setImageView(R.id.expert_icon_iv, dataList.get(position).getHeadPicPath(), R.drawable.common_icon_default_user_head);
            viewHolder.setTextView(R.id.tv_name, dataList.get(position).getName());
            viewHolder.setTextView(R.id.tv_professional_title, dataList.get(position).getTitle());
            viewHolder.setTextView(R.id.tv_intro, dataList.get(position).getIntro());

            if (dataList.get(position).getConsultationOperationFee() == null) {
                viewHolder.getView(R.id.tv_operation).setVisibility(View.GONE);
            }
            RatingBar rb = viewHolder.getView(R.id.rtb_score);
            rb.setRating(Float.valueOf(((dataList.get(position).getAvgScore()))));

            if (dataList.size() < 1) {
                viewHolder.getView(R.id.line).setVisibility(View.GONE);
            }
            return viewHolder.getConvertView();
        }
        return null;

    }

}
