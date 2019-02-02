package com.gaoshou.android.fragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.gaoshou.android.R;
import com.gaoshou.android.activity.MyCasesActivity;
import com.gaoshou.android.adapter.MyCasesExpandableAdapter;
import com.gaoshou.android.entity.OrderEntity;
import com.gaoshou.common.base.BaseFragment;
import com.gaoshou.common.base.GsApplication;
import com.gaoshou.common.constant.APIKey;
import com.gaoshou.common.constant.ServiceAPIConstant;
import com.gaoshou.common.network.CommonRequest;
import com.gaoshou.common.utils.EntityUtils;
import com.gaoshou.common.utils.NetworkUtil;

public class ExpertCasesFragment extends BaseFragment {
    private List<String> expandTitles;
    private List<List<OrderEntity>> expandValue;

    private ExpandableListView exList;
    private View contentView;
    private MyCasesExpandableAdapter adapter;

    private int status;

    public ExpertCasesFragment(int status) {
        this.status = status;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        contentView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_my_cases_expert, null);
        exList = findView(contentView, R.id.elv_cases);
        initList();

        return contentView;
    }

    private void initList() {
        expandTitles = new ArrayList<String>();
        expandTitles.add(getString(R.string.my_order));
        expandTitles.add(getString(R.string.my_ordered));

        expandValue = new ArrayList<List<OrderEntity>>();

        if (status == MyCasesActivity.UNFINISHED_STATUS) {
            List<OrderEntity> unfinishedDoctorOrders = GsApplication.getInstance(getActivity()).getUnfinishedDoctorOrderList();
            if (unfinishedDoctorOrders != null)
                expandValue.add(unfinishedDoctorOrders);
            if (expandValue.size() > 0) {
                List<OrderEntity> unfinishedExpertOrders = GsApplication.getInstance(getActivity()).getUnfinishedExpertOrderList();
                if (unfinishedExpertOrders != null)
                    expandValue.add(unfinishedExpertOrders);
            }
        } else if (status == MyCasesActivity.FINISHED_STATUS) {
            List<OrderEntity> finishedDoctorOrders = GsApplication.getInstance(getActivity()).getFinishedDoctorOrderList();
            if (finishedDoctorOrders != null)
                expandValue.add(finishedDoctorOrders);
            if (expandValue.size() > 0) {
                List<OrderEntity> finishedExpertOrders = GsApplication.getInstance(getActivity()).getFinishedExpertOrderList();
                if (finishedExpertOrders != null)
                    expandValue.add(finishedExpertOrders);
            }
        }

        if (expandValue != null && expandValue.size() < 2) {
            fetchMyCases();
        } else {
            showListView();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private void fetchMyCases() {
        if (NetworkUtil.isNetworkAvaliable(getActivity())) {
            CommonRequest fetchOrdersRequest = new CommonRequest();
            fetchOrdersRequest.setRequestApiName(ServiceAPIConstant.REQUEST_API_NAME_ORDER_INDEX + "&" + APIKey.COMMON_EXPAND + "=" + APIKey.COMMON_CONSULTATION);
            fetchOrdersRequest.setRequestID(ServiceAPIConstant.REQUEST_ID_ORDER_INDEX_DCOTOR);
            if (GsApplication.getInstance(getActivity()).getMyself() != null)
                fetchOrdersRequest.addRequestParam(APIKey.ORDER_SEARCH_DOCTOR_ID, GsApplication.getInstance(getActivity()).getMyself().getId());
            fetchOrdersRequest.addRequestParam(APIKey.COMMON_SORT, "-" + APIKey.COMMON_CREATED_AT);
            fetchOrdersRequest.addRequestParam(APIKey.ORDER_SEARCH_STATUS, status);
            fetchOrdersRequest.addRequestParam(APIKey.COMMON_ALLORPAGE, APIKey.COMMON_ALL);
            addRequestAsyncTask(contentView, fetchOrdersRequest);

        } else {
            showToast(getString(R.string.network_error));
            findView(contentView, R.id.rl_loading_container).setVisibility(View.GONE);
            findView(contentView, R.id.tv_no_record).setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onResponse(String requestID, boolean isSuccess, int statusCode, String message, List<Map<String, Object>> items, Map<String, Object> links, Map<String, Object> meta, Map<String, Object> additionalArgsmap) {
        if (requestID == ServiceAPIConstant.REQUEST_ID_ORDER_INDEX_DCOTOR) {
            if (isSuccess && statusCode == 200) {
                List<OrderEntity> orders = EntityUtils.getOrderEntityList(items);
                Log.i("testYJ","orders="+orders);
                if (orders != null) {
                    if (expandValue == null) {
                        expandValue = new ArrayList<List<OrderEntity>>();
                    }
                    expandValue.add(orders);
                    if (status == MyCasesActivity.UNFINISHED_STATUS) {
                        GsApplication.getInstance(getActivity()).setUnfinishedDoctorOrderList(orders);
                    } else if (status == MyCasesActivity.FINISHED_STATUS) {
                        GsApplication.getInstance(getActivity()).setFinishedDoctorOrderList(orders);
                    }

                } else {
                    showToast(message);
                }

                CommonRequest fetchOrderedRequest = new CommonRequest();
                fetchOrderedRequest.setRequestApiName(ServiceAPIConstant.REQUEST_API_NAME_ORDER_INDEX + "&" + APIKey.COMMON_EXPAND + "=" + APIKey.COMMON_CONSULTATION);
                fetchOrderedRequest.setRequestID(ServiceAPIConstant.REQUEST_ID_ORDER_INDEX_EXPERT);
                if (GsApplication.getInstance(getActivity()).getMyself() != null)
                    fetchOrderedRequest.addRequestParam(APIKey.ORDER_SEARCH_ORDER_DOCTOR_ID, GsApplication.getInstance(getActivity()).getMyself().getId());
                fetchOrderedRequest.addRequestParam(APIKey.COMMON_SORT, "-" + APIKey.COMMON_CREATED_AT);
                fetchOrderedRequest.addRequestParam(APIKey.ORDER_SEARCH_STATUS, status);
                addRequestAsyncTask(contentView, fetchOrderedRequest);
            }
        } else if (requestID == ServiceAPIConstant.REQUEST_ID_ORDER_INDEX_EXPERT) {
            if (isSuccess && statusCode == 200) {
                List<OrderEntity> orders = EntityUtils.getOrderEntityList(items);
                if (orders != null) {
                    expandValue.add(orders);

                    if (status == MyCasesActivity.UNFINISHED_STATUS) {
                        GsApplication.getInstance(getActivity()).setUnfinishedExpertOrderList(orders);
                    } else if (status == MyCasesActivity.FINISHED_STATUS) {
                        GsApplication.getInstance(getActivity()).setFinishedExpertOrderList(orders);
                    }
                } else {
                    showToast(message);
                }
            }

            showListView();
        }

    }

    private void showListView() {
        if (expandTitles != null && expandValue != null) {
            adapter = new MyCasesExpandableAdapter(getActivity(), expandTitles, expandValue, status);

            exList.setAdapter(adapter);
            exList.setVisibility(View.VISIBLE);
            for (int i = 0; i < expandTitles.size(); i++) {
                exList.expandGroup(i);
            }
        } else {
            findView(contentView, R.id.tv_no_record).setVisibility(View.VISIBLE);
        }
        findView(contentView, R.id.rl_loading_container).setVisibility(View.GONE);
    }

    public void myActivityResult(int requestCode, Intent data) {
        expandValue = null;
        adapter = null;
        fetchMyCases();
    }

}
