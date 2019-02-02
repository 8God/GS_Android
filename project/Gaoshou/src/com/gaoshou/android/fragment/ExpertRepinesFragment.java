package com.gaoshou.android.fragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;

import com.gaoshou.android.R;
import com.gaoshou.android.activity.CaseDetailActivity;
import com.gaoshou.android.adapter.MyRepinesExpandableAdapter;
import com.gaoshou.android.entity.OrderEntity;
import com.gaoshou.android.entity.RepineEntity;
import com.gaoshou.common.base.BaseFragment;
import com.gaoshou.common.base.GsApplication;
import com.gaoshou.common.constant.APIKey;
import com.gaoshou.common.constant.CommonConstant;
import com.gaoshou.common.constant.ServiceAPIConstant;
import com.gaoshou.common.network.CommonRequest;
import com.gaoshou.common.utils.EntityUtils;
import com.gaoshou.common.utils.NetworkUtil;

public class ExpertRepinesFragment extends BaseFragment {
    private List<String> expandTitles;
    private List<List<RepineEntity>> expandValue;

    private ExpandableListView exList;
    private MyRepinesExpandableAdapter adapter;

    private View contentView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        contentView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_my_repines_expert, null);
        exList = findView(contentView, R.id.elv_repinds);
        initListView();

        return contentView;
    }

    private void initListView() {
        exList = findView(contentView, R.id.elv_repinds);

        expandTitles = new ArrayList<String>();
        expandTitles.add(getString(R.string.elv_title_repine));
        expandTitles.add(getString(R.string.elv_title_repined));

        expandValue = new ArrayList<List<RepineEntity>>();
        List<RepineEntity> repineList = GsApplication.getInstance().getRepineList();
        if (repineList != null) {
            expandValue.add(repineList);
            if (expandValue.size() > 0) {
                List<RepineEntity> repinedList = GsApplication.getInstance().getRepinedList();
                if (repinedList != null) {
                    expandValue.add(repinedList);
                }
            }
        }
        if (expandValue != null && expandValue.size() < 2) {
            fetchMyRepines();
        } else {
            showListView();
        }
    }

    private void showListView() {
        if (expandTitles != null && expandValue != null) {
            adapter = new MyRepinesExpandableAdapter(expandTitles, expandValue, getActivity());

            exList.setAdapter(adapter);
            exList.setVisibility(View.VISIBLE);
            for (int i = 0; i < expandTitles.size(); i++) {
                exList.expandGroup(i);
            }
            exList.setOnChildClickListener(new OnChildClickListener() {
                @Override
                public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                    if (expandValue != null) {
                        List<RepineEntity> repines = expandValue.get(groupPosition);
                        if (repines != null) {
                            RepineEntity repine = repines.get(childPosition);
                            if (repine != null) {
                                OrderEntity order = repine.getOrder();
                                if (order != null) {
                                    GsApplication.getInstance().setOrder(order);
                                    int userType = 0;
                                    if (groupPosition == 0) {
                                        userType = CommonConstant.LOGON_TYPE_DOCTOR;
                                    }else if(groupPosition == 1){
                                        userType = CommonConstant.LOGON_TYPE_EXPERT;
                                    }
                                    GsApplication.getInstance().setOrderDetailUserType(userType);
                                    Intent intent = new Intent(getActivity(), CaseDetailActivity.class);
                                    startActivity(intent);
                                }
                            }
                        }
                    }
                    return false;
                }
            });
        } else {
            findView(contentView, R.id.tv_no_record).setVisibility(View.VISIBLE);
        }
        findView(contentView, R.id.rl_loading_container).setVisibility(View.GONE);
    }

    private void fetchMyRepines() {
        if (NetworkUtil.isNetworkAvaliable(getActivity())) {
            CommonRequest fetchRepineRequest = new CommonRequest();
            fetchRepineRequest.setRequestApiName(ServiceAPIConstant.REQUEST_API_NAME_REPINE_INDEX + "&" + APIKey.COMMON_EXPAND + "=" + APIKey.COMMON_ORDER + "," + APIKey.REPINE_REPINED_DOCTOR);
            fetchRepineRequest.setRequestID(ServiceAPIConstant.REQUEST_ID_REPINE_INDEX + APIKey.COMMON_DOCTOR);
            if (GsApplication.getInstance(getActivity()).getMyself() != null) {
                if (GsApplication.getInstance(getActivity()).getLogonType() == CommonConstant.LOGON_TYPE_EXPERT) {
                    fetchRepineRequest.addRequestParam(APIKey.COMMON_WHERE_DOCOTR_ID, GsApplication.getInstance(getActivity()).getMyself().getId());
                }
            }
            fetchRepineRequest.addRequestParam(APIKey.COMMON_SORT, "-" + APIKey.COMMON_CREATED_AT);
            fetchRepineRequest.addRequestParam(APIKey.COMMON_ALL_OR_PAGE, APIKey.COMMON_ALL);

            addRequestAsyncTask(contentView, fetchRepineRequest);
        } else {
            showToast(getString(R.string.network_error));
        }
    }

    @Override
    protected void onResponse(String requestID, boolean isSuccess, int statusCode, String message, List<Map<String, Object>> items, Map<String, Object> links, Map<String, Object> meta, Map<String, Object> additionalArgsmap) {
        if (requestID.equals(ServiceAPIConstant.REQUEST_ID_REPINE_INDEX + APIKey.COMMON_DOCTOR)) {
            if (isSuccess) {
                List<RepineEntity> repines = EntityUtils.getRepineEntityList(items);
                if (repines != null) {
                    if (expandValue == null) {
                        expandValue = new ArrayList<List<RepineEntity>>();
                    }
                    expandValue.add(repines);
                    GsApplication.getInstance().setRepineList(repines);
                }

            } else {
                showToast(message);
            }

            CommonRequest fetchRepineRequest = new CommonRequest();
            fetchRepineRequest.setRequestApiName(ServiceAPIConstant.REQUEST_API_NAME_REPINE_INDEX + "&" + APIKey.COMMON_EXPAND + "=" + APIKey.COMMON_ORDER + "," + APIKey.COMMON_DOCTOR);
            fetchRepineRequest.setRequestID(ServiceAPIConstant.REQUEST_ID_REPINE_INDEX + APIKey.COMMON_EXPERT);
            if (GsApplication.getInstance(getActivity()).getMyself() != null) {
                if (GsApplication.getInstance(getActivity()).getLogonType() == CommonConstant.LOGON_TYPE_EXPERT) {
                    fetchRepineRequest.addRequestParam(APIKey.REPINE_WHERE_REPINED_DOCTOR_ID, GsApplication.getInstance(getActivity()).getMyself().getId());
                }
            }
            fetchRepineRequest.addRequestParam(APIKey.COMMON_SORT, "-" + APIKey.COMMON_CREATED_AT);
            fetchRepineRequest.addRequestParam(APIKey.COMMON_ALL_OR_PAGE, APIKey.COMMON_ALL);

            addRequestAsyncTask(contentView, fetchRepineRequest);
        } else if (requestID.equals(ServiceAPIConstant.REQUEST_ID_REPINE_INDEX + APIKey.COMMON_EXPERT)) {
            if (isSuccess) {
                List<RepineEntity> repined = EntityUtils.getRepineEntityList(items);
                if (repined != null) {
                    expandValue.add(repined);
                    GsApplication.getInstance().setRepinedList(repined);
                }

            } else {
                showToast(message);
            }

            showListView();
        }
    }
}
