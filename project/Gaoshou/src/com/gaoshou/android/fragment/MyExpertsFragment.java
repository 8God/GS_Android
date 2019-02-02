package com.gaoshou.android.fragment;

import java.util.Date;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;

import com.gaoshou.android.R;
import com.gaoshou.android.activity.ExpertDetailActivity;
import com.gaoshou.android.entity.DoctorEntity;
import com.gaoshou.android.entity.FavoriteEntity;
import com.gaoshou.common.base.BaseListFragment;
import com.gaoshou.common.base.GsApplication;
import com.gaoshou.common.component.CommonListAdapter;
import com.gaoshou.common.component.CommonViewHolder;
import com.gaoshou.common.constant.APIKey;
import com.gaoshou.common.constant.CommonConstant;
import com.gaoshou.common.constant.ServiceAPIConstant;
import com.gaoshou.common.network.CommonRequest;
import com.gaoshou.common.utils.EntityUtils;
import com.gaoshou.common.utils.NetworkUtil;
import com.gaoshou.common.utils.TypeUtil;

public class MyExpertsFragment extends BaseListFragment<CommonListAdapter<FavoriteEntity>, FavoriteEntity> {
    @SuppressLint("NewApi")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        listView.setBackgroundResource(R.drawable.shape_normal_layout_bg);
        return contentView;
    }
    
    @Override
    protected void dealItemClick(int clickPosition, FavoriteEntity entiry) {
        GsApplication.getInstance(getActivity()).setCurrentExpert(entiry.getFavouriteDoctor());
        Intent intent = new Intent(getActivity(), ExpertDetailActivity.class);
        startActivity(intent);
    }

    @Override
    protected void fetchDataList(int offset, int page_sizes) {
        if (NetworkUtil.isNetworkAvaliable(getActivity())) {
            CommonRequest fetchFavoritesRequest = new CommonRequest();
            fetchFavoritesRequest.setRequestApiName(ServiceAPIConstant.REQUEST_API_NAME_FAVORITE_INDEX + "&" + APIKey.COMMON_EXPAND + "=" + APIKey.FAVORITE_FAVORITE_DOCTOR);
            fetchFavoritesRequest.setRequestID(ServiceAPIConstant.REQUEST_ID_FAVORITE_INDEX);
                fetchFavoritesRequest.addRequestParam(APIKey.FAVORITE_FAVORITE_SEARCH_DOCTOR_ID, GsApplication.getInstance(getActivity()).getUserId());

            addRequestAsyncTask(contentView, fetchFavoritesRequest);
        } else {
            showToast(getString(R.string.network_error));
        }
    }

    @Override
    protected void onDataResponse(String requestID, boolean isSuccess, int statusCode, String message, Map<String, Object> links, Map<String, Object> meta, List<Map<String, Object>> items, Map<String, Object> additionalArgsmap) {
        if (requestID == ServiceAPIConstant.REQUEST_ID_FAVORITE_INDEX) {
            if (isSuccess && statusCode == 200) {
                Log.i("testYJ","Itemsize-->"+items.size());
                List<FavoriteEntity> favorites = EntityUtils.getfavoriteEntityList(items);
                if (favorites != null) {
                    Log.i("testYJ","Favoritessize-->"+favorites.size());
                    if (!isLoadingMore) {
                        dataList = favorites;
                    } else {
                        dataList.addAll(favorites);
                    }
                }

                listView.setRefreshTime(CommonConstant.serverTimeFormat.format(new Date()));

                int pageCount = TypeUtil.getInteger(meta.get(APIKey.COMMON_PAGE_COUNT));
                int currentPage = TypeUtil.getInteger(meta.get(APIKey.COMMON_CURRENT_PAGE));
                toBeContinued = pageCount == currentPage ? 0 : 1;
                showDataList();
            } else {
                showToast(message);
            }
        }
    }

    @Override
    protected CommonListAdapter<FavoriteEntity> initAdapter(Context context, List<FavoriteEntity> dataList) {
        adapter = new CommonListAdapter<FavoriteEntity>(context, dataList) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                CommonViewHolder viewHolder = CommonViewHolder.getViewHolder(context, position, convertView, parent, R.layout.item_experts_list);
                if (viewHolder != null && dataList != null && dataList.get(position) != null) {
                    DoctorEntity doctor = dataList.get(position).getFavouriteDoctor();
                    if (doctor != null) {
                            viewHolder.setImageView(R.id.expert_icon_iv, doctor.getHeadPicPath(), R.drawable.common_icon_default_user_head);
                            viewHolder.setTextView(R.id.tv_name, doctor.getName());
                            viewHolder.setTextView(R.id.tv_professional_title, doctor.getTitle());
                            viewHolder.setTextView(R.id.tv_intro, doctor.getIntro());

                            if (doctor.getConsultationOperationFee() == null) {
                                viewHolder.getView(R.id.tv_operation).setVisibility(View.GONE);
                            }
                            RatingBar rb = viewHolder.getView(R.id.rtb_score);
                            rb.setRating(Float.valueOf(((doctor.getAvgScore()))));
                    }
                    if (dataList.size() < 1) {
                        viewHolder.getView(R.id.line).setVisibility(View.GONE);
                    }
                    return viewHolder.getConvertView();
                }
                return null;
            }
        };
        return adapter;
    }

}
