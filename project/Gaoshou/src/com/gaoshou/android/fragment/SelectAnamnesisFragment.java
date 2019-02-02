package com.gaoshou.android.fragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.gaoshou.android.R;
import com.gaoshou.android.activity.ConsultationDetailActivity;
import com.gaoshou.android.entity.AnamnesisEntity;
import com.gaoshou.common.base.BaseListFragment;
import com.gaoshou.common.component.CommonListAdapter;
import com.gaoshou.common.component.CommonViewHolder;
import com.gaoshou.common.constant.APIKey;
import com.gaoshou.common.constant.ServiceAPIConstant;
import com.gaoshou.common.network.CommonRequest;
import com.gaoshou.common.utils.EntityUtils;
import com.gaoshou.common.utils.NetworkUtil;
import com.gaoshou.common.utils.TypeUtil;

public class SelectAnamnesisFragment extends BaseListFragment<CommonListAdapter<AnamnesisEntity>, AnamnesisEntity> {
    private MenuItem submit;
    private StringBuffer anamnesisesStr;
    private List<Boolean> itemClicked;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }
    
    @Override
    protected void dealItemClick(int clickPosition, AnamnesisEntity entiry) {

    }

    @Override
    protected void dealItemClick(int clickPosition, AnamnesisEntity entity, View view) {
        Log.i("testYJ","dealItemClick");
        if (itemClicked != null && entity != null) {
            Boolean clicked = itemClicked.get(clickPosition);
            Log.i("testYJ","clicked="+clicked);
            if (clicked != null) {//添加病史
                if (!clicked) {
                    itemClicked.set(clickPosition, true);
                    if (submit != null) {
                        submit.setVisible(true);
                        if (view != null)
                            view.setActivated(true);
                    }
                } else {//取消病史
                    itemClicked.set(clickPosition, false);
                    boolean isClicked = false;
                    if (view != null)
                        view.setActivated(false);
                    if (submit != null) {
                        for (Boolean isClickedTemp : itemClicked) {
                            if (isClickedTemp) {
                                isClicked = true;
                                break;
                            }
                        }
                        submit.setVisible(isClicked);
                    }
                }
            }

        }
    }

    @Override
    protected void fetchDataList(int offset, int page_sizes) {
        if (NetworkUtil.isNetworkAvaliable(getActivity())) {
            CommonRequest anamnesisRequest = new CommonRequest();
            anamnesisRequest.setRequestApiName(ServiceAPIConstant.REQUEST_API_NAME_ANAMNESIS_INDEX);
            anamnesisRequest.setRequestID(ServiceAPIConstant.REQUEST_ID_ANAMNESIS_INDEX);
            anamnesisRequest.addRequestParam(APIKey.COMMON_PAGE_SIZE, offset / 20 + 1);
            anamnesisRequest.addRequestParam(APIKey.COMMON_WHERE_STATUS, 0);
            addRequestAsyncTask(contentView, anamnesisRequest);
        } else {
            showToast(getString(R.string.network_error));
        }
    }

    @Override
    protected CommonListAdapter<AnamnesisEntity> initAdapter(Context context, List<AnamnesisEntity> dataList) {
        adapter = new CommonListAdapter<AnamnesisEntity>(context, dataList) {
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
        if (requestID == ServiceAPIConstant.REQUEST_ID_ANAMNESIS_INDEX) {
            if (isSuccess && statusCode == 200) {
                List<AnamnesisEntity> anamnesisList = EntityUtils.getAnamnesisEntityList(items);
                if (anamnesisList != null) {
                    if (isLoadingMore) {
                        dataList.addAll(anamnesisList);
                    } else {
                        dataList = anamnesisList;
                    }
                    if (itemClicked == null)
                        itemClicked = new ArrayList<Boolean>();
                    for (int i = 0; i < anamnesisList.size(); i++) {
                        Boolean isClicked = false;
                        itemClicked.add(isClicked);
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_activity_user_setting, menu);
        submit = menu.findItem(R.id.item_submit);
        if (submit != null) {
            Log.i("testYJ","submit");
            submit.setVisible(false);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case android.R.id.home:
                getActivity().finish();
                break;
            case R.id.item_submit:
                submitAnamnesises();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void submitAnamnesises() {
        for (int i = 0; i < dataList.size(); i++) {
            Boolean isClicked = itemClicked.get(i);
            if (isClicked) {
                AnamnesisEntity anamnesis = dataList.get(i);
                if (anamnesis != null) {
                    if (anamnesisesStr == null) {
                        anamnesisesStr = new StringBuffer();
                        anamnesisesStr.append(anamnesis.getName());
                    } else {
                        anamnesisesStr.append(",").append(anamnesis.getName());
                    }
                }
            }
        }

        Intent intent = new Intent(getActivity(), ConsultationDetailActivity.class);
        intent.putExtra(getString(R.string.intent_anamnesis_name), anamnesisesStr.toString());
        intent.putExtra(getString(R.string.intent_anamnesis_index), 1);
        Log.i("testYJ", "anamnesisesStr = " + anamnesisesStr.toString());
        getActivity().setResult(Activity.RESULT_OK, intent);
        getActivity().finish();
    }
}
