package com.gaoshou.android.fragment;

import java.util.Date;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.gaoshou.android.R;
import com.gaoshou.android.activity.CancelOrderActivity;
import com.gaoshou.android.activity.CaseDetailActivity;
import com.gaoshou.android.activity.MyCasesActivity;
import com.gaoshou.android.entity.ConsultationEntity;
import com.gaoshou.android.entity.OrderEntity;
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

public class DoctorCasesFragment extends BaseListFragment<CommonListAdapter<OrderEntity>, OrderEntity> {
    private int status;

    public DoctorCasesFragment(int status) {
        this.status = status;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        contentView = LayoutInflater.from(getActivity()).inflate(R.layout.xlistview_main, null);

        initUI();

        if (status == MyCasesActivity.UNFINISHED_STATUS) {
            dataList = GsApplication.getInstance(getActivity()).getUnfinishedOrderList();
        } else if (status == MyCasesActivity.FINISHED_STATUS) {
            dataList = GsApplication.getInstance(getActivity()).getFinishedOrderList();
        }
        if (dataList == null) {
            fetchDataList(0);
        } else {
            showDataList();
        }
        return contentView;
    }

    @Override
    protected void dealItemClick(int clickPosition, OrderEntity entiry) {
        GsApplication.getInstance(getActivity()).setOrder(entiry);
        Intent intent = new Intent(getActivity(), CaseDetailActivity.class);
        startActivity(intent);
    }

    @Override
    protected void fetchDataList(int offset, int page_sizes) {
        if (NetworkUtil.isNetworkAvaliable(getActivity())) {
            CommonRequest fetchOrdersRequest = new CommonRequest();
            fetchOrdersRequest.setRequestApiName(ServiceAPIConstant.REQUEST_API_NAME_ORDER_INDEX + "&" + APIKey.COMMON_EXPAND + "=" + APIKey.COMMON_CONSULTATION);
            fetchOrdersRequest.setRequestID(ServiceAPIConstant.REQUEST_ID_ORDER_INDEX);
            if (GsApplication.getInstance(getActivity()).getMyself() != null)
                fetchOrdersRequest.addRequestParam(APIKey.ORDER_SEARCH_DOCTOR_ID, GsApplication.getInstance(getActivity()).getMyself().getId());
            fetchOrdersRequest.addRequestParam(APIKey.COMMON_SORT, "-" + APIKey.COMMON_CREATED_AT);
            fetchOrdersRequest.addRequestParam(APIKey.COMMON_PAGE, offset / page_sizes + 1);
            fetchOrdersRequest.addRequestParam(APIKey.ORDER_SEARCH_STATUS, status);

            addRequestAsyncTask(contentView, fetchOrdersRequest);
        } else {
            showToast(getString(R.string.network_error));
        }
    }

    @Override
    protected void onDataResponse(String requestID, boolean isSuccess, int statusCode, String message, Map<String, Object> links, Map<String, Object> meta, List<Map<String, Object>> items, Map<String, Object> additionalArgsmap) {
        if (requestID == ServiceAPIConstant.REQUEST_ID_ORDER_INDEX) {
            if (isSuccess && statusCode == 200) {
                List<OrderEntity> orders = EntityUtils.getOrderEntityList(items);
                if (orders != null) {
                    if (!isLoadingMore) {
                        dataList = orders;
                    } else {
                        dataList.addAll(orders);
                    }
                }
                if (status == MyCasesActivity.UNFINISHED_STATUS) {
                    GsApplication.getInstance(getActivity()).setUnfinishedOrderList(dataList);
                } else if (status == MyCasesActivity.FINISHED_STATUS) {
                    GsApplication.getInstance(getActivity()).setFinishedOrderList(dataList);
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
    protected CommonListAdapter<OrderEntity> initAdapter(Context context, List<OrderEntity> dataList) {
        adapter = new CommonListAdapter<OrderEntity>(context, dataList) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                CommonViewHolder viewHolder = null;
                if (dataList != null) {
                    final OrderEntity order = dataList.get(position);
                    if (order != null) {
                        if (order.getStatus() == MyCasesActivity.UNFINISHED_STATUS) {
                            viewHolder = CommonViewHolder.getViewHolder(context, position, convertView, parent, R.layout.item_my_case_unfinished);
                            if (viewHolder != null)
                                viewHolder.getView(R.id.btn_cancel).setOnClickListener(new MyOnClickListener(context, order));

                        } else {
                            viewHolder = CommonViewHolder.getViewHolder(context, position, convertView, parent, R.layout.item_my_case_finished);
                            if (viewHolder != null) {
                                viewHolder.getView(R.id.btn_evaluate).setOnClickListener(new MyOnClickListener(context, order));
                                viewHolder.getView(R.id.btn_repine).setOnClickListener(new MyOnClickListener(context, order));
                            }
                        }

                        if (viewHolder != null) {
                            ImageView type_iv = viewHolder.getView(R.id.iv_type);
                            viewHolder.getView(R.id.ll_container).setOnClickListener(new MyOnClickListener(getActivity(), order));

                            if (order.getType() == 0) {
                                if (type_iv != null)
                                    type_iv.setImageResource(R.drawable.list_consultation);
                                viewHolder.setTextView(R.id.tv_type, getString(R.string.tv_consultation));
                            } else {
                                if (type_iv != null)
                                    type_iv.setImageResource(R.drawable.list_surgery);
                                viewHolder.setTextView(R.id.tv_type, getString(R.string.tv_consultation_operation));
                            }

                            ConsultationEntity consultation = order.getConsultation();
                            if (consultation != null) {
                                //                                if (consultation.getAnamnesis() != null)
                                //                                    viewHolder.setTextView(R.id.tv_anamnesis, order.getConsultation().getAnamnesis().getName());
                                //                                if (consultation.getSymptom() != null)
                                //                                    viewHolder.setTextView(R.id.tv_symptom, order.getConsultation().getSymptom().getName());
                                viewHolder.setTextView(R.id.tv_anamnesis, order.getConsultation().getAnamnesis());
                                viewHolder.setTextView(R.id.tv_symptom, order.getConsultation().getSymptom());
                                viewHolder.setTextView(R.id.tv_illness, order.getConsultation().getPatient_illness());
                            }
                        }
                    }
                }
                return viewHolder != null ? viewHolder.getConvertView() : null;
            }
        };
        return adapter;
    }

    public void myActivityResult(int requestCode, Intent data) {
        if (!isRefreshing && !isLoadingMore) {
            isRefreshing = true;
            dataList = null;
            adapter = null;
            fetchDataList(0);
        }
    }

    private class MyOnClickListener implements OnClickListener {
        private Context context;
        private OrderEntity order;

        public MyOnClickListener(Context context, OrderEntity order) {
            this.context = context;
            this.order = order;
        }

        @Override
        public void onClick(View v) {
            GsApplication.getInstance(context).setOrder(order);
            GsApplication.getInstance(context).setOrderDetailUserType(CommonConstant.LOGON_TYPE_DOCTOR);
            switch (v.getId()) {
                case R.id.btn_cancel:
                    Log.i("testYJ", "cancel");
                    myStartActivityForResult(CancelOrderActivity.class, MyCasesActivity.TO_CANCEL_ORDER_ACTIVITY, -1);
                    break;
                case R.id.btn_evaluate:
                    Log.i("testYJ", "evaluate");
                    myStartActivityForResult(CaseDetailActivity.class, MyCasesActivity.TO_CASE_DETAIL_ACTIVITY, MyCasesActivity.LOCATION_REPINED);
                    break;
                case R.id.btn_repine:
                    Log.i("testYJ", "repine");
                    myStartActivityForResult(CaseDetailActivity.class, MyCasesActivity.TO_CASE_DETAIL_ACTIVITY, MyCasesActivity.LOCATION_EVALUATE);
                    break;
                case R.id.ll_container:
                    Log.i("testYJ", "container");
                    myStartActivityForResult(CaseDetailActivity.class, MyCasesActivity.TO_CASE_DETAIL_ACTIVITY, -1);
                    break;
            }
        }

    }

    private void myStartActivityForResult(Class<?> toClass, int requestCode, int extra) {
        Intent intent = new Intent(getActivity(), toClass);
        if (extra != -1) {
            intent.putExtra(getString(R.string.intent_case_detail_location), extra);
        }
        startActivityForResult(intent, requestCode);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        dataList = null;
        adapter = null;
    }

}
