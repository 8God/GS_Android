package com.gaoshou.android.view;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import com.gaoshou.android.R;
import com.gaoshou.android.activity.ExpertDetailActivity;
import com.gaoshou.android.entity.DoctorEntity;
import com.gaoshou.common.base.BasePager;
import com.gaoshou.common.base.GsApplication;

public class RecommendedExpertView extends BasePager {

    private DoctorEntity doctorEntity;

    private View layout;

    public RecommendedExpertView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    public RecommendedExpertView(Context context, DoctorEntity doctorEntity) {
        super(context);

        this.doctorEntity = doctorEntity;

        init();
    }

    private void init() {
        layout = LayoutInflater.from(getContext()).inflate(R.layout.view_recommended_expert, null);
        addView(layout);

        if (null != doctorEntity) {
            initUI();
        }
    }

    private void initUI() {
        setImageView(layout, R.id.srimv_expert_head_pic, doctorEntity.getHeadPicPath(), R.drawable.common_icon_default_user_head);
        setTextView(layout, R.id.tv_doctor_name, doctorEntity.getName());
        setTextView(layout, R.id.tv_doctor_dept, doctorEntity.getDept());

        setViewClickListener(layout, R.id.srimv_expert_head_pic, new OnClickListener() {

            @Override
            public void onClick(View v) {
                GsApplication.getInstance(getContext()).setCurrentExpert(doctorEntity);
                getContext().startActivity(new Intent(getContext(), ExpertDetailActivity.class));
            }
        });
    }

}
