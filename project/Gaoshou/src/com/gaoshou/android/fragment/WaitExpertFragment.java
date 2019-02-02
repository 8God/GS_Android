package com.gaoshou.android.fragment;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.gaoshou.android.R;
import com.gaoshou.android.activity.ExpertDetailActivity;
import com.gaoshou.android.adapter.WaitingExpertsAdapter;
import com.gaoshou.android.entity.DoctorEntity;
import com.gaoshou.android.entity.OrderEntity;
import com.gaoshou.common.base.BaseListFragment;
import com.gaoshou.common.base.GsApplication;
import com.gaoshou.common.constant.APIKey;
import com.gaoshou.common.constant.OrderStatus;
import com.gaoshou.common.constant.ServiceAPIConstant;
import com.gaoshou.common.network.CommonRequest;
import com.gaoshou.common.utils.EntityUtils;

public class WaitExpertFragment extends BaseListFragment<WaitingExpertsAdapter, OrderEntity> {

    private int consultationId;
    private View headView;

    public WaitExpertFragment(int consultationId) {
        super();
        this.consultationId = consultationId;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

    }

    @Override
    public void onStart() {
        super.onStart();

        listView.setPullLoadEnable(false);
        listView.setPullRefreshEnable(false);

        if (null == headView) {
            headView = LayoutInflater.from(getActivity()).inflate(R.layout.headview_wating_expert_list, null);
            listView.addHeaderView(headView);
            setNoDataTextTip("暂无专家接单");
        }
    }

    @Override
    protected void dealItemClick(int clickPosition, OrderEntity entity) {
        if (null != entity) {
            DoctorEntity orderDoctor = entity.getOrder_doctor();
            if (null != orderDoctor) {
                GsApplication.getInstance(getActivity()).setCurrentExpert(orderDoctor);
                startActivity(new Intent(getActivity(), ExpertDetailActivity.class));
            }
        }
    }

    @Override
    protected WaitingExpertsAdapter initAdapter(Context context, List<OrderEntity> dataList) {
        adapter = new WaitingExpertsAdapter(getActivity(), dataList);
        return adapter;
    }

    @Override
    protected void fetchDataList(int offset, int page_sizes) {
        CommonRequest fetchOrderList = new CommonRequest();
        fetchOrderList.setRequestApiName(ServiceAPIConstant.REQUEST_API_NAME_ORDER_INDEX);
        fetchOrderList.setRequestID(ServiceAPIConstant.REQUEST_ID_ORDER_INDEX);
        fetchOrderList.addRequestParam(APIKey.ORDER_SEARCH_CONSULTATION_ID, consultationId);
        fetchOrderList.addRequestParam(APIKey.ORDER_SEARCH_STATUS, OrderStatus.ORDER_STATUS_INIT);
        fetchOrderList.addRequestParam(APIKey.COMMON_EXPAND, "orderDoctor,consultation,doctor");

        addRequestAsyncTask(contentView, fetchOrderList);
    }

    @Override
    protected void onDataResponse(String requestID, boolean isSuccess, int statusCode, String message, Map<String, Object> links, Map<String, Object> meta, List<Map<String, Object>> items, Map<String, Object> additionalArgsmap) {
        if (ServiceAPIConstant.REQUEST_ID_ORDER_INDEX.equals(requestID)) {
            if (isSuccess && items != null) {
                if (null == dataList) {
                    dataList = EntityUtils.getOrderEntityList(items);
                    adapter = null;
                }
                Log.i("cth", "dataList = " + dataList);
            }
            showDataList();
        }
    }

    public void updateDataList() {
        dataList = null;
        fetchDataList(0);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_fragment_wait_expert, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case R.id.close:
                getActivity().finish();
                break;
            case android.R.id.home:
                //TODO 发请求取消订单,并关闭页面
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        dataList = null;
        adapter = null;
    }

}
