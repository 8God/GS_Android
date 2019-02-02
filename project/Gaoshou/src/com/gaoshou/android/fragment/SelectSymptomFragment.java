package com.gaoshou.android.fragment;

import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.gaoshou.android.R;
import com.gaoshou.android.activity.ConsultationDetailActivity;
import com.gaoshou.android.entity.SymptomEntity;
import com.gaoshou.common.base.BaseListFragment;
import com.gaoshou.common.component.CommonListAdapter;
import com.gaoshou.common.component.CommonViewHolder;
import com.gaoshou.common.constant.APIKey;
import com.gaoshou.common.constant.ServiceAPIConstant;
import com.gaoshou.common.network.CommonRequest;
import com.gaoshou.common.utils.EntityUtils;
import com.gaoshou.common.utils.NetworkUtil;
import com.gaoshou.common.utils.TypeUtil;

public class SelectSymptomFragment extends BaseListFragment<CommonListAdapter<SymptomEntity>, SymptomEntity> {
    @Override
    protected void dealItemClick(int clickPosition, SymptomEntity entiry) {
        if (entiry != null) {
            Intent intent = new Intent(getActivity(), ConsultationDetailActivity.class);
            intent.putExtra(getString(R.string.intent_symptom_name), entiry.getName());
            intent.putExtra(getString(R.string.intent_symptom_index), clickPosition + 1);
            getActivity().setResult(Activity.RESULT_OK, intent);
            getActivity().finish();
        }
    }

    @Override
    protected void fetchDataList(int offset, int page_sizes) {
        if (NetworkUtil.isNetworkAvaliable(getActivity())) {
            CommonRequest symptomRequest = new CommonRequest();
            symptomRequest.setRequestApiName(ServiceAPIConstant.REQUEST_API_NAME_SYMPTOM_INDEX);
            symptomRequest.setRequestID(ServiceAPIConstant.REQUEST_ID_SYMPTOM_INDEX);
            symptomRequest.addRequestParam(APIKey.COMMON_PAGE_SIZE, offset / 20 + 1);
            symptomRequest.addRequestParam(APIKey.COMMON_WHERE_STATUS, 0);
            addRequestAsyncTask(contentView, symptomRequest);
        } else {
            showToast(getString(R.string.network_error));
        }
    }

    @Override
    protected CommonListAdapter<SymptomEntity> initAdapter(Context context, List<SymptomEntity> dataList) {
        adapter = new CommonListAdapter<SymptomEntity>(context, dataList) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                CommonViewHolder viewHolder = CommonViewHolder.getViewHolder(getActivity(), position, convertView, parent, R.layout.item_experty_or_sort_list);
                if (dataList != null && viewHolder != null) {
                    viewHolder.setTextView(R.id.tv_experty_or_sort, dataList.get(position).getName());
                    return viewHolder.getConvertView();
                }
                return null;
            }
        };
        return adapter;
    }

    @Override
    protected void onDataResponse(String requestID, boolean isSuccess, int statusCode, String message, Map<String, Object> links, Map<String, Object> meta, List<Map<String, Object>> items, Map<String, Object> additionalArgsmap) {
        if (requestID == ServiceAPIConstant.REQUEST_ID_SYMPTOM_INDEX) {
            if (isSuccess && statusCode == 200) {
                List<SymptomEntity> symptomList = EntityUtils.getSymptomEntityList(items);
                if (symptomList != null) {
                    if (isLoadingMore) {
                        dataList.addAll(symptomList);
                    } else {
                        dataList = symptomList;
                    }
                }

                int currentPage = TypeUtil.getInteger(meta.get(APIKey.COMMON_CURRENT_PAGE));
                int pageCount = TypeUtil.getInteger(meta.get(APIKey.COMMON_PAGE_COUNT));
                Log.i("testYJ", "currentPage=" + currentPage);
                Log.i("testYJ", "totleCount=" + pageCount);
                toBeContinued = (pageCount == currentPage ? 0 : 1);
                showDataList();

            } else {
                showToast(message);
            }
        }

    }

}
