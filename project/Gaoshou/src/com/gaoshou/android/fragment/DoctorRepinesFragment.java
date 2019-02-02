package com.gaoshou.android.fragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;

import com.gaoshou.android.R;
import com.gaoshou.android.activity.CaseDetailActivity;
import com.gaoshou.android.entity.DoctorEntity;
import com.gaoshou.android.entity.OrderEntity;
import com.gaoshou.android.entity.RepineEntity;
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

public class DoctorRepinesFragment extends BaseListFragment<CommonListAdapter<RepineEntity>, RepineEntity> {
    @Override
    protected void dealItemClick(int clickPosition, RepineEntity entity) {
        if (entity != null) {
            OrderEntity order = entity.getOrder();
            if (order != null) {
                GsApplication.getInstance(getActivity()).setOrder(order);
            }
            Intent intent = new Intent(getActivity(), CaseDetailActivity.class);
            startActivity(intent);
        }
    }

    @Override
    protected void fetchDataList(int offset, int page_sizes) {
        dataList = GsApplication.getInstance().getRepineList();
        if (dataList == null) {
            if (NetworkUtil.isNetworkAvaliable(getActivity())) {
                CommonRequest fetchDoctorRepineRequest = new CommonRequest();
                fetchDoctorRepineRequest.setRequestApiName(ServiceAPIConstant.REQUEST_API_NAME_REPINE_INDEX + "&" + APIKey.COMMON_EXPAND + "=" + APIKey.REPINE_REPINED_DOCTOR + "," + APIKey.COMMON_ORDER);
                fetchDoctorRepineRequest.setRequestID(ServiceAPIConstant.REQUEST_ID_REPINE_INDEX);
                DoctorEntity myself = GsApplication.getInstance(getActivity()).getMyself();
                if (myself != null)
                    fetchDoctorRepineRequest.addRequestParam(APIKey.COMMON_WHERE_DOCOTR_ID, myself.getId());

                addRequestAsyncTask(contentView, fetchDoctorRepineRequest);
            } else {
                showToast(getString(R.string.network_error));
            }
        } else {
            showDataList();
        }
    }

    @Override
    protected void onDataResponse(String requestID, boolean isSuccess, int statusCode, String message, Map<String, Object> links, Map<String, Object> meta, List<Map<String, Object>> items, Map<String, Object> additionalArgsmap) {
        if (requestID == ServiceAPIConstant.REQUEST_ID_REPINE_INDEX) {
            if (isSuccess) {
                List<RepineEntity> repines = EntityUtils.getRepineEntityList(items);
                if (repines != null) {
                    if (!isLoadingMore) {
                        dataList = repines;
                    } else {
                        dataList.addAll(repines);
                    }

                    GsApplication.getInstance().setRepineList(dataList);

                    listView.setRefreshTime(CommonConstant.serverTimeFormat.format(new Date()));

                    int pageCount = TypeUtil.getInteger(meta.get(APIKey.COMMON_PAGE_COUNT));
                    int currentPage = TypeUtil.getInteger(meta.get(APIKey.COMMON_CURRENT_PAGE));
                    toBeContinued = pageCount == currentPage ? 0 : 1;
                    showDataList();
                }
            } else {
                showToast(message);
            }
        }
    }

    @Override
    protected CommonListAdapter<RepineEntity> initAdapter(Context context, List<RepineEntity> dataList) {
        adapter = new CommonListAdapter<RepineEntity>(context, dataList) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                CommonViewHolder viewHolder = CommonViewHolder.getViewHolder(context, position, convertView, null, R.layout.item_my_case_unfinished);
                if (dataList != null && viewHolder != null) {
                    //隐藏按钮      
                    viewHolder.getView(R.id.btn_cancel).setVisibility(View.GONE);

                    RepineEntity repine = dataList.get(position);
                    if (repine != null) {
                        //被投诉医生
                        DoctorEntity repineDoctor = repine.getRepined_doctor();
                        if (repineDoctor != null) {
                            viewHolder.setTextView(R.id.tv_anamnesis, repineDoctor.getName());
                        }

                        viewHolder.setTextView(R.id.tv_symptom, getShowTime(repine.getCreated_at()));
                        viewHolder.setTextView(R.id.tv_illness, repine.getContent());
                    }

                }
                return viewHolder == null ? null : viewHolder.getConvertView();
            }
        };
        return adapter;
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

    @Override
    public void onDestroyView() {
        dataList = null;
        adapter = null;
        super.onDestroyView();
    }

}
