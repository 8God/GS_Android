package com.gaoshou.android.fragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gaoshou.android.R;
import com.gaoshou.android.activity.OrderDetailActivity;
import com.gaoshou.android.adapter.ExpertAcceptOrdersAdapter;
import com.gaoshou.android.entity.ConsultationEntity;
import com.gaoshou.android.entity.DoctorEntity;
import com.gaoshou.common.base.BaseListFragment;
import com.gaoshou.common.base.GsApplication;
import com.gaoshou.common.constant.APIKey;
import com.gaoshou.common.constant.OrderStatus;
import com.gaoshou.common.constant.ServiceAPIConstant;
import com.gaoshou.common.network.CommonRequest;
import com.gaoshou.common.utils.EntityUtils;
import com.gaoshou.common.utils.Validator;

public class ExpertAcceptOrderFragment extends BaseListFragment<ExpertAcceptOrdersAdapter, ConsultationEntity> {

    boolean isFinishFetchMyConsultationList = false;
    boolean isFinishFetchConsultationList = false;

    //预约我的订单
    private List<ConsultationEntity> myConsultationList;
    //符合我专长的订单
    private List<ConsultationEntity> consultationList;

    @Override
    public void onResume() {
        super.onResume();

        listView.setPullLoadEnable(false);
        listView.setPullRefreshEnable(false);

        setNoDataTextTip("暂无可接订单");
    }

    @Override
    protected void dealItemClick(int clickPosition, ConsultationEntity entity) {
        int expertId = entity.getExpert_id();
        int userId = GsApplication.getInstance(getActivity()).getUserId();
        Intent openConsultationDetail = new Intent(getActivity(), OrderDetailActivity.class);
        GsApplication.getInstance(getActivity()).setExpertAcceptConsultation(entity);
        if (Validator.isIdValid(expertId) && Validator.isIdValid(userId) && expertId == userId) {
            openConsultationDetail.putExtra(OrderDetailActivity.KEY_IS_MY_CONSULTATION, true);
        }
        startActivity(openConsultationDetail);
    }

    @Override
    protected void fetchDataList(int offset, int page_sizes) {
        fetchMyConsultationList(offset, page_sizes);
        fetchFitMyExpertiseConsultationList(offset, page_sizes);
    }

    private void fetchMyConsultationList(int offset, int page_sizes) {
        int userId = GsApplication.getInstance(getActivity()).getUserId();
        CommonRequest fetchMyConsultationRequest = new CommonRequest();
        fetchMyConsultationRequest.setRequestApiName(ServiceAPIConstant.REQUEST_API_NAME_CONSULTATION_INDEX);
        fetchMyConsultationRequest.setRequestID(ServiceAPIConstant.REQUEST_ID_CONSULTATION_INDEX);
        fetchMyConsultationRequest.addRequestParam(APIKey.CONSULTATION_SEARCH_EXPERT_ID, userId);
        fetchMyConsultationRequest.addRequestParam(APIKey.COMMON_WHERE_STATUS, OrderStatus.CONSULTATION_STATUS_INIT);
        fetchMyConsultationRequest.addRequestParam(APIKey.COMMON_SORT, "-" + APIKey.COMMON_CREATED_AT);
        fetchMyConsultationRequest.addRequestParam(APIKey.COMMON_ALL_OR_PAGE, APIKey.PAGE_TYPE_ALL);
        fetchMyConsultationRequest.addRequestParam(APIKey.COMMON_EXPAND, APIKey.CONSULTATION_FILES);

        addRequestAsyncTask(contentView, fetchMyConsultationRequest);
    }

    private void fetchFitMyExpertiseConsultationList(int offset, int page_sizes) {
        //TODO 添加二级筛选专家专长
        DoctorEntity mySelf = GsApplication.getInstance().getMyself();
        if (null != mySelf) {
            int expertiseId = mySelf.getExpertiseId();
            
            List<String> joinStatus = new ArrayList<String>();
            joinStatus.add(APIKey.EXPERT);
            joinStatus.add("expert.expertise0");

            CommonRequest fetchMyConsultationRequest = new CommonRequest();
            fetchMyConsultationRequest.setRequestApiName(ServiceAPIConstant.REQUEST_API_NAME_CONSULTATION_INDEX);
            fetchMyConsultationRequest.setRequestID(ServiceAPIConstant.REQUEST_ID_CONSULTATION_INDEX + "_FIT_ME");
            fetchMyConsultationRequest.addRequestParam(APIKey.CONSULTATION_WHERE_STATUS + "[]", OrderStatus.CONSULTATION_STATUS_INIT);
            fetchMyConsultationRequest.addRequestParam(APIKey.COMMON_ALL_OR_PAGE, APIKey.PAGE_TYPE_ALL);
            fetchMyConsultationRequest.addRequestParam(APIKey.COMMON_SORT, "-" + APIKey.COMMON_CREATED_AT);
            fetchMyConsultationRequest.addRequestParam(APIKey.COMMON_EXPAND, APIKey.EXPERT);
            fetchMyConsultationRequest.addRequestParam(APIKey.COMMON_JOIN, joinStatus);
            fetchMyConsultationRequest.addRequestParam(APIKey.CONSULTATION_WHERE_EXPERTISE_ID + "[]", expertiseId);

            SortedMap<String, Object> requestParamsSortedMap = fetchMyConsultationRequest.getRequestParamsSortedMap();
            if (null != requestParamsSortedMap) {
                requestParamsSortedMap.remove(APIKey.COMMON_STATUS); //去掉通用的status筛选方法
            }

            addRequestAsyncTask(contentView, fetchMyConsultationRequest);
        } else {
            isFinishFetchConsultationList = true;
        }

    }

    @Override
    protected void onDataResponse(String requestID, boolean isSuccess, int statusCode, String message, Map<String, Object> links, Map<String, Object> meta, List<Map<String, Object>> items, Map<String, Object> additionalArgsmap) {
        if (ServiceAPIConstant.REQUEST_ID_CONSULTATION_INDEX.equals(requestID)) {
            if (isSuccess) {
                if (items != null) {
                    myConsultationList = EntityUtils.getConsultationEntityList(items);
                }
            }
            isFinishFetchMyConsultationList = true;
            if (isFinishFetchMyConsultationList && isFinishFetchConsultationList) {
                showDataList();
            }
        } else if (requestID.equals(ServiceAPIConstant.REQUEST_ID_CONSULTATION_INDEX + "_FIT_ME")) {
            if (isSuccess) {
                if (items != null) {
                    adapter = null;
                    consultationList = EntityUtils.getConsultationEntityList(items);
                }
            }
            isFinishFetchConsultationList = true;
            if (isFinishFetchMyConsultationList && isFinishFetchConsultationList) {
                showDataList();
            }
        }
    }

    @Override
    protected ExpertAcceptOrdersAdapter initAdapter(Context context, List<ConsultationEntity> dataList) {
        adapter = new ExpertAcceptOrdersAdapter(getActivity(), myConsultationList, consultationList);
        return adapter;
    }

    /**
     * 更新符合我专长的咨询列表
     */
    public void updateConsultationList() {
        consultationList = null;
        isFinishFetchConsultationList = false;

        fetchFitMyExpertiseConsultationList(0, 0);
    }

    /**
     * 更新指定我的咨询列表
     * 
     * @param consultationEntity
     */
    public void updateMyConsulationList(ConsultationEntity consultationEntity) {
        if (myConsultationList == null) {
            myConsultationList = new ArrayList<ConsultationEntity>();
        }

        adapter = null;

        myConsultationList.add(0, consultationEntity);

        showDataList();
    }

    public void dealMyConsultation(int consultationId) {
        Log.i("cth", "consultationId = " + consultationId);
        if (null != myConsultationList && myConsultationList.size() > 0) {
            for (int i = 0; i < myConsultationList.size(); i++) {
                ConsultationEntity consultationEntity = myConsultationList.get(i);
                if (null != consultationEntity) {
                    int tmpId = consultationEntity.getId();
                    if (Validator.isIdValid(tmpId) && consultationId == tmpId) {
                        myConsultationList.remove(i);

                        showDataList();

                        break;
                    }
                }
            }
        }
    }

    @Override
    protected void showDataList() {
        RelativeLayout loadingRl = findView(contentView, R.id.xlistview_main_loading_container_rl);
        loadingRl.setVisibility(View.GONE);
        if ((null != myConsultationList && myConsultationList.size() > 0) || (null != consultationList && consultationList.size() > 0)) {
            if (null == adapter) {
                adapter = initAdapter(getActivity(), dataList);
                listView.setAdapter(adapter);
            } else {
                adapter.notifyDataSetChanged();
            }
        }

        TextView noRecordTv = findView(contentView, R.id.xlistview_main_no_record_tv);
        if ((null != myConsultationList && myConsultationList.size() > 0) || (null != consultationList && consultationList.size() > 0)) {
            noRecordTv.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
        } else {
            noRecordTv.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
        }

        setListCanLoadMore();

        isRefreshing = false;
        isLoadingMore = false;
        listView.stopRefresh();
        listView.stopLoadMore();
    }

}
