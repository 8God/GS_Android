package com.gaoshou.android.fragment;

import java.util.Date;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.gaoshou.android.R;
import com.gaoshou.android.activity.ConsultationDetailActivity;
import com.gaoshou.android.adapter.EvaluateListAdapter;
import com.gaoshou.android.entity.DoctorEntity;
import com.gaoshou.android.entity.EvaluateEntity;
import com.gaoshou.common.base.BaseListFragment;
import com.gaoshou.common.base.GsApplication;
import com.gaoshou.common.component.CommonListAdapter;
import com.gaoshou.common.constant.APIKey;
import com.gaoshou.common.constant.CommonConstant;
import com.gaoshou.common.constant.ServiceAPIConstant;
import com.gaoshou.common.network.CommonRequest;
import com.gaoshou.common.utils.EntityUtils;
import com.gaoshou.common.utils.NetworkUtil;
import com.gaoshou.common.utils.OpenFileUtil;
import com.gaoshou.common.utils.TypeUtil;

public class ExpertDetailFragment extends BaseListFragment<CommonListAdapter<EvaluateEntity>, EvaluateEntity> implements OnClickListener {
    private DoctorEntity expert;
    private View headerView;

    @Override
    public void onStart() {
        super.onStart();
        if (headerView == null) {
            headerView = LayoutInflater.from(getActivity()).inflate(R.layout.evaluation_list_header, null);
            initHeaderView();
            listView.addHeaderView(headerView);
        }
    }

    private void initHeaderView() {
        expert = GsApplication.getInstance(getActivity()).getCurrentExpert();
        if (expert != null) {
            setImageView(headerView, R.id.iv_icon, expert.getHeadPicPath(), R.drawable.common_icon_default_user_head);
            setTextView(headerView, R.id.tv_name, expert.getName());
            setTextView(headerView, R.id.tv_professional_title, expert.getTitle());
            setTextView(headerView, R.id.tv_department, expert.getDept());
            setTextView(headerView, R.id.tv_intro, expert.getIntro());

            Float consultationPrice = Float.valueOf(expert.getConsultationFee());
            Float operationPrice = Float.valueOf(expert.getConsultationOperationFee());
            if (consultationPrice != null || consultationPrice != 0f) {
                setTextView(headerView, R.id.tv_consultation, consultationPrice + " 元");
                findView(headerView, R.id.btn_consultation).setOnClickListener(this);
            } else {
                findView(headerView, R.id.ll_consultation_container).setVisibility(View.GONE);
                findView(headerView, R.id.btn_consultation).setVisibility(View.GONE);
            }
            if (operationPrice != null && operationPrice != 0f) {
                setTextView(headerView, R.id.tv_operation, operationPrice + " 元");
                findView(headerView, R.id.btn_consultation).setOnClickListener(this);
            } else {
                findView(headerView, R.id.rl_operation_container).setVisibility(View.GONE);
                findView(headerView, R.id.btn_consultation).setVisibility(View.GONE);
            }

        }
        findView(headerView, R.id.btn_consultation).setOnClickListener(this);
        findView(headerView, R.id.btn_operation).setOnClickListener(this);
        findView(headerView, R.id.iv_slide).setOnClickListener(this);
        findView(headerView, R.id.btn_authentication).setOnClickListener(this);

    }

    @Override
    protected void dealItemClick(int clickPosition, EvaluateEntity entiry) {
        //跳转到案例详情界面
        //        OrderEntity order = entiry.getOrder();
        //        if (order != null) {
        //            GsApplication.getInstance(getActivity()).setOrder(order);
        //            Intent intent = new Intent(getActivity(), CaseDetailActivity.class);
        //            startActivity(intent);
        //        }
    }

    @Override
    protected void fetchDataList(int offset, int page_sizes) {
        if (NetworkUtil.isNetworkAvaliable(getActivity())) {
            CommonRequest fetchEvaluateRequest = new CommonRequest();
            fetchEvaluateRequest.setRequestApiName(ServiceAPIConstant.REQUEST_API_NAME_EVALUATE_INDEX + "&" + APIKey.COMMON_EXPAND + "=" + APIKey.COMMON_DOCTOR);
            fetchEvaluateRequest.setRequestID(ServiceAPIConstant.REQUEST_ID_EVALUATE_INDEX);
            if (GsApplication.getInstance(getActivity()).getCurrentExpert() != null)
                fetchEvaluateRequest.addRequestParam(APIKey.EVALUATE_WHERE_DOCTOR_ID, GsApplication.getInstance(getActivity()).getUserId());
            fetchEvaluateRequest.addRequestParam(APIKey.COMMON_SORT, "-" + APIKey.COMMON_CREATED_AT);
            fetchEvaluateRequest.addRequestParam(APIKey.COMMON_PAGE, offset / page_sizes);

            addRequestAsyncTask(contentView, fetchEvaluateRequest);
        } else {
            showToast(getString(R.string.network_error));
        }

    }

    @Override
    protected void onDataResponse(String requestID, boolean isSuccess, int statusCode, String message, Map<String, Object> links, Map<String, Object> meta, List<Map<String, Object>> items, Map<String, Object> additionalArgsmap) {
        if (requestID == ServiceAPIConstant.REQUEST_ID_EVALUATE_INDEX) {
            if (isSuccess && statusCode == 200) {
                List<EvaluateEntity> evaluates = EntityUtils.getEvaluateEntityList(items);
                Log.i("testYJ", "evaluates-->" + evaluates);
                if (evaluates != null) {
                    if (!isLoadingMore) {
                        dataList = evaluates;
                    } else {
                        dataList.addAll(evaluates);
                    }
                }
                Log.i("testYJ", "dataList.size-->" + dataList.size());
                if (dataList != null & dataList.size() > 0) {
                    GsApplication.getInstance(getActivity()).setEvaluatelist(dataList);
                } else {
                    dataList.add(null);
                }
                listView.setRefreshTime(CommonConstant.serverTimeFormat.format(new Date()));

                int pageCount = TypeUtil.getInteger(meta.get(APIKey.COMMON_PAGE_COUNT));
                int currentPage = TypeUtil.getInteger(meta.get(APIKey.COMMON_CURRENT_PAGE));
                toBeContinued = pageCount <= currentPage ? 0 : 1;
                showDataList();
                Log.i("testYJ", "toBeContinued=" + toBeContinued);
            } else {
                showToast(message);
            }
        }
    }

    @Override
    protected CommonListAdapter<EvaluateEntity> initAdapter(Context context, List<EvaluateEntity> dataList) {
        adapter = new EvaluateListAdapter(context, dataList);
        return adapter;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_authentication:
                Log.i("testYJ", "isClick");
                Log.i("testYJ", "expert=" + expert);
                Log.i("testYJ", "getActivity=" + getActivity());
                if (expert != null) {
                    Log.i("testYJ", "expert.getCertificationPath()=" + expert.getCertificationPath());
                    OpenFileUtil.openFile(getActivity(), expert.getCertificationPath());
                }
                break;
            case R.id.btn_consultation:
                GsApplication.getInstance(getActivity()).setConsultationType(ConsultationDetailActivity.ORDER_TYPE_CONSULTATION);
                Intent intent = new Intent(getActivity(), ConsultationDetailActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_operation:
                GsApplication.getInstance(getActivity()).setConsultationType(ConsultationDetailActivity.ORDER_TYPE_CONSULTATION_ORPERATION);
                Intent intent2 = new Intent(getActivity(), ConsultationDetailActivity.class);
                startActivity(intent2);
                break;
            case R.id.iv_slide:
                changeIntroState();
                break;
        }

    }

    private void changeIntroState() {
        TextView intro = findView(headerView, R.id.tv_intro);
        ImageView slide = findView(headerView, R.id.iv_slide);
        if (intro != null) {
            if (intro.getVisibility() == View.GONE) {
                intro.setVisibility(View.VISIBLE);
                slide.setImageDrawable(getResources().getDrawable(R.drawable.btn_pack_up));
            } else {
                intro.setVisibility(View.GONE);
                slide.setImageDrawable(getResources().getDrawable(R.drawable.btn_normal));
            }
        }
    }

}
