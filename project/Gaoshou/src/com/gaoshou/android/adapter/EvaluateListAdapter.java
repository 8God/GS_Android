package com.gaoshou.android.adapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;

import com.gaoshou.android.R;
import com.gaoshou.android.entity.DoctorEntity;
import com.gaoshou.android.entity.EvaluateEntity;
import com.gaoshou.common.base.GsApplication;
import com.gaoshou.common.component.CommonListAdapter;
import com.gaoshou.common.component.CommonViewHolder;
import com.gaoshou.common.constant.CommonConstant;

public class EvaluateListAdapter extends CommonListAdapter<EvaluateEntity> {

    public EvaluateListAdapter(Context context, List<EvaluateEntity> dataList) {
        super(context, dataList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CommonViewHolder viewHolder = CommonViewHolder.getViewHolder(context, position, convertView, parent, R.layout.item_evaluation);
        Log.i("testYJ", "viewHolder=" + viewHolder);
        if (dataList != null && viewHolder != null) {
            EvaluateEntity evaluate = dataList.get(position);
            Log.i("testYJ", "evaluate-->" + evaluate);
            if (evaluate != null) {
                viewHolder.getView(R.id.ll_evaluation_container).setVisibility(View.VISIBLE);
                viewHolder.getView(R.id.tv_no_record).setVisibility(View.GONE);

                DoctorEntity doctor = dataList.get(position).getDoctor();
                if (doctor != null) {
                    Log.i("testYJ", "doctor-->" + doctor);
                    DoctorEntity mySelf = GsApplication.getInstance().getMyself();
                    if (mySelf != null) {
                        viewHolder.setImageView(R.id.expert_icon_iv, mySelf.getHeadPicPath(), R.drawable.common_icon_default_user_head);
                        viewHolder.setTextView(R.id.tv_name, doctor.getName());
                    }
                }
                viewHolder.setTextView(R.id.tv_date, getShowTime(evaluate.getCreated_at()));
                viewHolder.setTextView(R.id.tv_evaluation, evaluate.getContent());

                ((RatingBar) viewHolder.getView(R.id.rtb_score)).setRating(evaluate.getScore());

                if (position == dataList.size() - 1) {
                    viewHolder.getView(R.id.line).setVisibility(View.GONE);
                    viewHolder.getView(R.id.line_last).setVisibility(View.VISIBLE);
                } else {
                    viewHolder.getView(R.id.line).setVisibility(View.VISIBLE);
                    viewHolder.getView(R.id.line_last).setVisibility(View.GONE);

                }
            } else {
                viewHolder.getView(R.id.ll_evaluation_container).setVisibility(View.GONE);
                viewHolder.getView(R.id.tv_no_record).setVisibility(View.VISIBLE);
            }
        }
        return viewHolder != null ? viewHolder.getConvertView() : null;
    }

    private String getShowTime(String time) {
        SimpleDateFormat serviceTimeFormat = CommonConstant.serverTimeFormat;
        SimpleDateFormat showTimeFormat = new SimpleDateFormat("yyyy.MM.dd");
        String formatTime = null;
        try {
            formatTime = showTimeFormat.format(serviceTimeFormat.parse(time));
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return formatTime;
    }

}
