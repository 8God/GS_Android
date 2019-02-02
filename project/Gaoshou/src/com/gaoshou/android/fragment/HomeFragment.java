package com.gaoshou.android.fragment;

import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.gaoshou.android.R;
import com.gaoshou.android.activity.ExpertAcceptOrderActivity;
import com.gaoshou.android.entity.DoctorEntity;
import com.gaoshou.android.view.RecommendedExpertView;
import com.gaoshou.common.base.BaseFragment;
import com.gaoshou.common.base.GsApplication;
import com.gaoshou.common.constant.APIKey;
import com.gaoshou.common.constant.CommonConstant;
import com.gaoshou.common.constant.ServiceAPIConstant;
import com.gaoshou.common.network.CommonRequest;
import com.gaoshou.common.utils.EntityUtils;

public class HomeFragment extends BaseFragment implements OnClickListener {

    private View contentView;
    private List<DoctorEntity> expertList;

    private DoctorEntity mySelf;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        contentView = inflater.inflate(R.layout.fragment_home, null);

        init();

        return contentView;
    }

    private void init() {
        initData();
        fetchRecommendedExpertList();
        initUI();
    }

    private void initData() {
        mySelf = GsApplication.getInstance(getActivity()).getMyself();
    }

    private void initUI() {
        setViewClickListener(contentView, R.id.imv_specialists_expert, this);
        setViewClickListener(contentView, R.id.imv_surgery_expert, this);
        setViewClickListener(contentView, R.id.imv_accept_order, this);

        if (null != mySelf) {
            LinearLayout receivingOrderLayout = findView(contentView, R.id.rl_receiving_order);
            int type = mySelf.getType();
            switch (type) {
                case CommonConstant.LOGON_TYPE_DOCTOR:
                    receivingOrderLayout.setVisibility(View.GONE);
                    break;
                case CommonConstant.LOGON_TYPE_EXPERT:
                    receivingOrderLayout.setVisibility(View.VISIBLE);
                    break;
                default:
                    break;
            }
        }

    }

    private void fetchRecommendedExpertList() { //获取推荐专家列表
        expertList = GsApplication.getInstance(getActivity()).getRecommendedExpertList();
        if (null != expertList && expertList.size() > 0) {
            showRecommendedExpertList();
        } else {
            CommonRequest fetchRecommendExpert = new CommonRequest();
            fetchRecommendExpert.setRequestApiName(ServiceAPIConstant.REQUEST_API_NAME_EXPERT_INDEX);
            fetchRecommendExpert.setRequestID(ServiceAPIConstant.REQUEST_ID_EXPERT_INDEX);
            fetchRecommendExpert.addRequestParam(APIKey.DOCTOR_RECOMMENDED, 1);
            fetchRecommendExpert.addRequestParam(APIKey.COMMON_EXPAND, APIKey.COMMON_DOCTOR_FILES);

            addRequestAsyncTask(contentView, fetchRecommendExpert);
        }
    }

    @Override
    protected void onResponse(String requestID, boolean isSuccess, int statusCode, String message, List<Map<String, Object>> items, Map<String, Object> links, Map<String, Object> meta, Map<String, Object> additionalArgsmap) {
        super.onResponse(requestID, isSuccess, statusCode, message, items, links, meta, additionalArgsmap);
        if (ServiceAPIConstant.REQUEST_ID_EXPERT_INDEX.equals(requestID)) {
            if (isSuccess) {
                if (null != items) {
                    expertList = EntityUtils.getDoctorEntityList(items);

                    if (null != expertList && expertList.size() > 0) {
                        GsApplication.getInstance(getActivity()).setRecommendedExpertList(expertList);
                    }
                }
            } else {
                showToast(message);
            }

            showRecommendedExpertList();
        }

    }

    private void showRecommendedExpertList() {
        LinearLayout recommendedExpertListLayout = findView(contentView, R.id.ll_recommend_experts_list);

        if (null != expertList && expertList.size() > 0) {
            for (int i = 0; i < expertList.size(); i++) {
                DoctorEntity expert = expertList.get(i);
                if (null != expert) {
                    RecommendedExpertView recommendedExpertView = new RecommendedExpertView(getActivity(), expert);

                    recommendedExpertListLayout.addView(recommendedExpertView, i);
                }
            }
        }

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.imv_specialists_expert:

                break;
            case R.id.imv_surgery_expert:

                break;
            case R.id.imv_accept_order:
                startActivity(new Intent(getActivity(), ExpertAcceptOrderActivity.class));
                break;
            default:
                break;
        }

    }

}
