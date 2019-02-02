package com.gaoshou.android.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;

import com.gaoshou.android.R;
import com.gaoshou.android.entity.CategoryEntity;
import com.gaoshou.android.entity.CityEntity;
import com.gaoshou.android.entity.DoctorEntity;
import com.gaoshou.android.entity.ExpertiseEntity;
import com.gaoshou.common.component.CommonListAdapter;
import com.gaoshou.common.component.CommonViewHolder;
import com.gaoshou.common.utils.TypeUtil;

public class StringAdapter<T> extends CommonListAdapter<T> {

    public StringAdapter(Context context, List<T> dataList) {
        super(context, dataList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CommonViewHolder viewHolder = null;
        Object object = dataList.get(position);
        if (object != null) {
            //城市
            if (object instanceof CityEntity) {
                viewHolder = CommonViewHolder.getViewHolder(context, position, convertView, parent, R.layout.item_cities_list);
                if (viewHolder != null)
                    viewHolder.setTextView(R.id.tv_name, ((CityEntity) object).getName());
            }
            //专长或分类
            else {
                viewHolder = CommonViewHolder.getViewHolder(context, position, convertView, parent, R.layout.item_experty_or_sort_list);
                if (viewHolder != null) {
                    if (object instanceof ExpertiseEntity) {
                        viewHolder.setTextView(R.id.tv_experty_or_sort, ((ExpertiseEntity) object).getName());
                    } else if (object instanceof CategoryEntity) {
                        viewHolder.setTextView(R.id.tv_experty_or_sort, ((CategoryEntity) object).getName());
                    } else {
                        viewHolder.setTextView(R.id.tv_experty_or_sort, (String) object);
                    }
                    if (position > 0 & position < dataList.size()) {
                            viewHolder.getView(R.id.v_line).setVisibility(View.VISIBLE);
                    }
                }
            }
        }
        return viewHolder != null ? viewHolder.getConvertView() : null;
    }

}
