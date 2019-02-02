package com.gaoshou.android.adapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;

import com.gaoshou.android.R;
import com.gaoshou.android.entity.DoctorEntity;
import com.gaoshou.android.entity.RepineEntity;
import com.gaoshou.common.component.CommonViewHolder;
import com.gaoshou.common.constant.CommonConstant;

public class MyRepinesExpandableAdapter extends BaseExpandableListAdapter {
    private List<String> titles;
    private List<List<RepineEntity>> value;
    private Context context;

    public MyRepinesExpandableAdapter(List<String> titles, List<List<RepineEntity>> value, Context context) {
        super();
        this.titles = titles;
        this.value = value;
        this.context = context;
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
        CommonViewHolder viewHolder = CommonViewHolder.getViewHolder(context, childPosition, convertView, parent, R.layout.item_my_case_unfinished);
        if (viewHolder != null) {
            viewHolder.getView(R.id.btn_cancel).setVisibility(View.INVISIBLE);
            DoctorEntity doctor = null;
            RepineEntity repine = value.get(groupPosition).get(childPosition);
            if (repine != null) {
                if (groupPosition == 0) {
                    doctor = repine.getDoctor();
                } else if (groupPosition == 1) {
                    doctor = repine.getRepined_doctor();
                }
                if(doctor!=null)
                viewHolder.setTextView(R.id.tv_anamnesis, doctor.getName());
                viewHolder.setTextView(R.id.tv_symptom, getShowTime(repine.getCreated_at()));
                viewHolder.setTextView(R.id.tv_illness, repine.getContent());
            }
        }
        return viewHolder == null ? null : viewHolder.getConvertView();
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

    private String getShowTime(String time) {
        if (time != null) {
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
        } else {
            return null;
        }
    }
}
