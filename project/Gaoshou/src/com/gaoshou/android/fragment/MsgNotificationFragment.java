package com.gaoshou.android.fragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gaoshou.android.R;
import com.gaoshou.android.adapter.MsgNotificationAdapter;
import com.gaoshou.android.entity.BaseEntity;
import com.gaoshou.android.entity.ConsultationEntity;
import com.gaoshou.android.entity.OrderEntity;
import com.gaoshou.common.base.BaseFragment;
import com.gaoshou.common.base.GsApplication;
import com.gaoshou.common.component.AutoLoadMoreListView;
import com.gaoshou.common.component.AutoLoadMoreListView.IAutoLoadMoreListViewListener;
import com.gaoshou.common.constant.APIKey;
import com.gaoshou.common.constant.CommonConstant;
import com.gaoshou.common.constant.OrderStatus;
import com.gaoshou.common.constant.ServiceAPIConstant;
import com.gaoshou.common.network.CommonRequest;
import com.gaoshou.common.utils.EntityUtils;

public class MsgNotificationFragment extends BaseFragment {

    private View contentView;

    private int logonType;
    private List<BaseEntity> msgList;
    private List<ConsultationEntity> consultationList;
    private List<OrderEntity> orderList;

    private AutoLoadMoreListView msgListView;
    private MsgNotificationAdapter msgsAdapter;

    //    private boolean isCanLoadMore = false;

    private boolean isFinishFetchConsultationList = false;
    private boolean isFinishFetchTimelyConsultationList = false;
    private boolean isFinishFetchOrderList = false;

    private boolean isRefreshing = false;
    private boolean isLoadingMore = false;

    //    private int currentOrderListPage = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        contentView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_msg_notification, null);

        init();

        return contentView;
    }

    private void init() {
        initUI();

        msgList = new ArrayList<BaseEntity>();
        consultationList = new ArrayList<ConsultationEntity>();
        orderList = new ArrayList<OrderEntity>();

        logonType = GsApplication.getInstance().getLogonType();

        msgList = GsApplication.getInstance().getMsgList();
        if (null != msgList) {
            Log.i("cth", "init : msgList.size = " + msgList.size());
        }
        if (null != msgList && msgList.size() > 0) {
            isFinishFetchConsultationList = true;
            isFinishFetchOrderList = true;
            isFinishFetchTimelyConsultationList = true;

            if (null != consultationList) {
                consultationList.clear();
            }
            if (null != orderList) {
                orderList.clear();
            }

            msgsAdapter = null;
            showMsgList();
        } else {
            fetchMsgList();
        }
    }

    private void initUI() {
        msgListView = findView(contentView, R.id.allv_msg);
        msgListView.setPullRefreshEnable(true);
        msgListView.setPullLoadEnable(false);
        msgListView.setXListViewListener(new IAutoLoadMoreListViewListener() {

            @Override
            public void onRefresh() {
                if (!isRefreshing && !isLoadingMore) {
                    isRefreshing = true;

                    isFinishFetchConsultationList = false;
                    isFinishFetchTimelyConsultationList = false;
                    isFinishFetchOrderList = false;

                    msgList.clear();
                    consultationList.clear();
                    orderList.clear();

                    fetchMsgList();
                }
            }

            @Override
            public void onLoadMore() {

            }
        });
    }

    private void fetchMsgList() {
        switch (logonType) {
            case CommonConstant.LOGON_TYPE_EXPERT:
                fetchTimelyOrderList();
                break;
            case CommonConstant.LOGON_TYPE_DOCTOR:
                isFinishFetchTimelyConsultationList = true;
                break;
            default:
                break;
        }
        fetchConsultationList();
        fetchOrderList();
    }

    private void fetchTimelyOrderList() {
        List<String> joinStatus = new ArrayList<String>();
        joinStatus.add(APIKey.CONSULTATION);
        joinStatus.add(APIKey.DOCTOR);

        int userId = GsApplication.getInstance().getUserId();
        CommonRequest fetchDoctorTimelyOrderList = new CommonRequest();
        fetchDoctorTimelyOrderList.setRequestApiName(ServiceAPIConstant.REQUEST_API_NAME_ORDER_INDEX);
        fetchDoctorTimelyOrderList.setRequestID(ServiceAPIConstant.REQUEST_ID_ORDER_INDEX + "timely");
        fetchDoctorTimelyOrderList.addRequestParam(APIKey.COMMON_ALL_OR_PAGE, APIKey.PAGE_TYPE_ALL);
        fetchDoctorTimelyOrderList.addRequestParam(APIKey.COMMON_JOIN, joinStatus);
        fetchDoctorTimelyOrderList.addRequestParam(APIKey.COMMON_EXPAND, APIKey.CONSULTATION + "," + APIKey.DOCTOR);
        fetchDoctorTimelyOrderList.addRequestParam(APIKey.DOCTOR_WHERE_DOCTOR_ID + "[]", userId);
        fetchDoctorTimelyOrderList.addRequestParam(APIKey.ORDER_WHERE_STATUS + "[]", OrderStatus.ORDER_STATUS_INIT);
        fetchDoctorTimelyOrderList.addRequestParam(APIKey.CONSULTATION_WHERE_TIMELY + "[]", 0);
        fetchDoctorTimelyOrderList.addRequestParam(APIKey.COMMON_SORT, "-" + APIKey.COMMON_CREATED_AT);

        addRequestAsyncTask(contentView, fetchDoctorTimelyOrderList);
    }

    private void fetchConsultationList() {
        switch (logonType) {
            case CommonConstant.LOGON_TYPE_EXPERT:
                fetchDoctorConsultationList();//专家也可以作为普通医生去下单
                //                fetchExpertConsultationList();
                break;
            case CommonConstant.LOGON_TYPE_DOCTOR:
                fetchDoctorConsultationList();
                break;
            default:
                break;
        }

    }

    private void fetchExpertConsultationList() {
        int userId = GsApplication.getInstance().getUserId();
        CommonRequest fetchExpertConsultationList = new CommonRequest();
        fetchExpertConsultationList.setRequestApiName(ServiceAPIConstant.REQUEST_API_NAME_CONSULTATION_INDEX);
        fetchExpertConsultationList.setRequestID(ServiceAPIConstant.REQUEST_ID_CONSULTATION_INDEX + APIKey.EXPERT);
        fetchExpertConsultationList.addRequestParam(APIKey.COMMON_WHERE_STATUS, OrderStatus.CONSULTATION_STATUS_INIT);
        fetchExpertConsultationList.addRequestParam(APIKey.CONSULTATION_SEARCH_EXPERT_ID, userId);
        fetchExpertConsultationList.addRequestParam(APIKey.COMMON_ALL_OR_PAGE, APIKey.PAGE_TYPE_ALL);
        fetchExpertConsultationList.addRequestParam(APIKey.COMMON_SORT, "-" + APIKey.COMMON_CREATED_AT);
        fetchExpertConsultationList.addRequestParam(APIKey.COMMON_EXPAND, APIKey.COMMON_EXPERT + "," + APIKey.COMMON_DOCTOR);

        addRequestAsyncTask(contentView, fetchExpertConsultationList);
    }

    private void fetchDoctorConsultationList() {
        int userId = GsApplication.getInstance().getUserId();
        CommonRequest fetchDoctorConsultationList = new CommonRequest();
        fetchDoctorConsultationList.setRequestApiName(ServiceAPIConstant.REQUEST_API_NAME_CONSULTATION_INDEX);
        fetchDoctorConsultationList.setRequestID(ServiceAPIConstant.REQUEST_ID_CONSULTATION_INDEX);
        fetchDoctorConsultationList.addRequestParam(APIKey.COMMON_WHERE_STATUS, OrderStatus.CONSULTATION_STATUS_CANCEL);
        fetchDoctorConsultationList.addRequestParam(APIKey.CONSULTATION_SEARCH_DOCTOR_ID, userId);
        fetchDoctorConsultationList.addRequestParam(APIKey.COMMON_ALL_OR_PAGE, APIKey.PAGE_TYPE_ALL);
        fetchDoctorConsultationList.addRequestParam(APIKey.COMMON_EXPAND, APIKey.COMMON_EXPERT + "," + APIKey.COMMON_DOCTOR);
        fetchDoctorConsultationList.addRequestParam(APIKey.COMMON_SORT, "-" + APIKey.COMMON_CREATED_AT);

        addRequestAsyncTask(contentView, fetchDoctorConsultationList);
    }

    private void fetchOrderList() {
        switch (logonType) {
            case CommonConstant.LOGON_TYPE_EXPERT:
                fetchDoctorOrderList();//专家也可以作为普通医生去下单
                //                fetchExpertOrderList(page);
                break;
            case CommonConstant.LOGON_TYPE_DOCTOR:
                fetchDoctorOrderList();
                break;
            default:
                break;
        }
    }

    private void fetchExpertOrderList() {
        List<Integer> statusList = new ArrayList<Integer>();
        statusList.add(OrderStatus.ORDER_STATUS_UNPAY);
        statusList.add(OrderStatus.ORDER_STATUS_PAY);
        statusList.add(OrderStatus.ORDER_STATUS_CANCEL);

        int userId = GsApplication.getInstance().getUserId();
        CommonRequest fetchExpertOrderList = new CommonRequest();
        fetchExpertOrderList.setRequestApiName(ServiceAPIConstant.REQUEST_API_NAME_ORDER_INDEX);
        fetchExpertOrderList.setRequestID(ServiceAPIConstant.REQUEST_ID_ORDER_INDEX + APIKey.EXPERT);
        fetchExpertOrderList.addRequestParam(APIKey.COMMON_WHERE_STATUS, statusList);
        fetchExpertOrderList.addRequestParam(APIKey.CONSULTATION_SEARCH_EXPERT_ID, userId);
        fetchExpertOrderList.addRequestParam(APIKey.COMMON_ALL_OR_PAGE, APIKey.PAGE_TYPE_ALL);
        fetchExpertOrderList.addRequestParam(APIKey.COMMON_SORT, "-" + APIKey.COMMON_CREATED_AT);
        fetchExpertOrderList.addRequestParam(APIKey.COMMON_EXPAND, "consultation,doctor,orderDoctor");
        fetchExpertOrderList.addRequestParam(APIKey.COMMON_SORT, "-" + APIKey.COMMON_CREATED_AT);

        addRequestAsyncTask(contentView, fetchExpertOrderList);
    }

    private void fetchDoctorOrderList() {
        List<Integer> statusList = new ArrayList<Integer>();
        statusList.add(OrderStatus.ORDER_STATUS_INIT);
        statusList.add(OrderStatus.ORDER_STATUS_CANCEL);
        statusList.add(OrderStatus.ORDER_STATUS_CANCEL_OPERATION);
        statusList.add(OrderStatus.ORDER_STATUS_UNPAY);

        int userId = GsApplication.getInstance().getUserId();
        CommonRequest fetchDoctorOrderList = new CommonRequest();
        fetchDoctorOrderList.setRequestApiName(ServiceAPIConstant.REQUEST_API_NAME_ORDER_INDEX);
        fetchDoctorOrderList.setRequestID(ServiceAPIConstant.REQUEST_ID_ORDER_INDEX);
        fetchDoctorOrderList.addRequestParam(APIKey.COMMON_WHERE_STATUS, statusList);
        fetchDoctorOrderList.addRequestParam(APIKey.CONSULTATION_SEARCH_DOCTOR_ID, userId);
        fetchDoctorOrderList.addRequestParam(APIKey.COMMON_ALL_OR_PAGE, APIKey.PAGE_TYPE_ALL);
        fetchDoctorOrderList.addRequestParam(APIKey.COMMON_SORT, "-" + APIKey.COMMON_CREATED_AT);
        fetchDoctorOrderList.addRequestParam(APIKey.COMMON_EXPAND, "consultation,doctor,orderDoctor");
        fetchDoctorOrderList.addRequestParam(APIKey.COMMON_SORT, "-" + APIKey.COMMON_CREATED_AT);

        addRequestAsyncTask(contentView, fetchDoctorOrderList);
    }

    @Override
    protected void onResponse(String requestID, boolean isSuccess, int statusCode, String message, List<Map<String, Object>> items, Map<String, Object> links, Map<String, Object> meta, Map<String, Object> additionalArgsmap) {
        super.onResponse(requestID, isSuccess, statusCode, message, items, links, meta, additionalArgsmap);
        if ((ServiceAPIConstant.REQUEST_ID_ORDER_INDEX + "timely").equals(requestID)) {
            if (isSuccess) {
                if (items != null) {
                    List<OrderEntity> tmpTimelyOrderList = EntityUtils.getOrderEntityList(items);
                    if (null != tmpTimelyOrderList && tmpTimelyOrderList.size() > 0) {
                        if (null == msgList) {
                            msgList = new ArrayList<BaseEntity>();
                            msgsAdapter = null;
                        }
                        msgList.addAll(0, tmpTimelyOrderList);
                    }
                }
            }

            isFinishFetchTimelyConsultationList = true;
            showMsgList();
        } else if (ServiceAPIConstant.REQUEST_ID_CONSULTATION_INDEX.equals(requestID)) {
            if (isSuccess) {
                if (items != null) {
                    List<ConsultationEntity> tmpConsultationList = EntityUtils.getConsultationEntityList(items);
                    if (null != tmpConsultationList && tmpConsultationList.size() > 0) {
                        consultationList.addAll(tmpConsultationList);
                    }
                }
            }
            switch (logonType) {
                case CommonConstant.LOGON_TYPE_DOCTOR:
                    isFinishFetchConsultationList = true;
                    showMsgList();
                    break;
                case CommonConstant.LOGON_TYPE_EXPERT:
                    fetchExpertConsultationList();
                    break;
                default:
                    break;
            }

        } else if ((ServiceAPIConstant.REQUEST_ID_CONSULTATION_INDEX + APIKey.EXPERT).equals(requestID)) {
            if (isSuccess) {
                if (items != null) {
                    List<ConsultationEntity> tmpConsultationList = EntityUtils.getConsultationEntityList(items);
                    if (null != tmpConsultationList && tmpConsultationList.size() > 0) {
                        consultationList.addAll(tmpConsultationList);
                    }
                }
            }
            isFinishFetchConsultationList = true;
            showMsgList();
        } else if (ServiceAPIConstant.REQUEST_ID_ORDER_INDEX.equals(requestID)) {
            if (isSuccess) {
                if (items != null) {
                    List<OrderEntity> tmpOrderList = EntityUtils.getOrderEntityList(items);
                    if (null != tmpOrderList && tmpOrderList.size() > 0) {
                        orderList.addAll(tmpOrderList);
                    }
                }
            }
            switch (logonType) {
                case CommonConstant.LOGON_TYPE_DOCTOR:
                    isFinishFetchOrderList = true;
                    showMsgList();
                    break;
                case CommonConstant.LOGON_TYPE_EXPERT:
                    fetchExpertOrderList();
                    break;
                default:
                    break;
            }
        } else if ((ServiceAPIConstant.REQUEST_ID_ORDER_INDEX + APIKey.EXPERT).equals(requestID)) {
            if (isSuccess) {
                if (items != null) {
                    List<OrderEntity> tmpOrderList = EntityUtils.getOrderEntityList(items);
                    if (null != tmpOrderList && tmpOrderList.size() > 0) {
                        orderList.addAll(tmpOrderList);
                    }
                }
            }
            //TODO 暂时不做分页
            //            if (null != meta) {
            //                int currentPage = TypeUtil.getInteger(meta.get(APIKey.COMMON_CURRENT_PAGE), 0);
            //                int pageCount = TypeUtil.getInteger(meta.get(APIKey.COMMON_PAGE_COUNT), 0);
            //                if (currentPage < pageCount) {
            //                    isCanLoadMore = true;
            //                } else {
            //                    isCanLoadMore = false;
            //                }
            //            }
            isFinishFetchOrderList = true;
            showMsgList();
        }
    }

    private void showMsgList() {
        if (isFinishFetchConsultationList && isFinishFetchOrderList && isFinishFetchTimelyConsultationList) {
            RelativeLayout loadingLayout = findView(contentView, R.id.xlistview_main_loading_container_rl);
            loadingLayout.setVisibility(View.GONE);
            TextView noDataTv = findView(contentView, R.id.xlistview_main_no_record_tv);

            if (null == msgList) {
                msgList = new ArrayList<BaseEntity>();
            }

            if (null != consultationList && consultationList.size() > 0) {
                msgList.addAll(consultationList);
            }
            if (null != orderList && orderList.size() > 0) {
                msgList.addAll(orderList);
            }
            Log.i("cth", "showMsgList : msgList.size = " + msgList.size());
            if (null != msgList && msgList.size() > 0) {
                GsApplication.getInstance().setMsgList(msgList);

                if (null == msgsAdapter) {
                    msgsAdapter = new MsgNotificationAdapter(getActivity(), msgList);
                    msgListView.setAdapter(msgsAdapter);
                } else {
                    msgsAdapter.notifyDataSetChanged();
                }

                msgListView.setVisibility(View.VISIBLE);
                noDataTv.setVisibility(View.GONE);
            } else {
                msgListView.setVisibility(View.GONE);
                noDataTv.setVisibility(View.VISIBLE);
            }

            //            msgListView.setPullLoadEnable(isCanLoadMore);
            isRefreshing = false;
            msgListView.stopRefresh();
        }
    }
    //TODO 暂时不做分页
    //    private void showMoreOrderList() {
    //
    //        isRefreshing = false;
    //        isLoadingMore = false;
    //        msgListView.stopRefresh();
    //        msgListView.stopLoadMore();
    //    }
}
