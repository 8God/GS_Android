package com.gaoshou.android.fragment;

import java.util.Date;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gaoshou.android.R;
import com.gaoshou.android.activity.CityActivity;
import com.gaoshou.android.activity.ExpertDetailActivity;
import com.gaoshou.android.activity.ExpertiseActivity;
import com.gaoshou.android.activity.MapActivity;
import com.gaoshou.android.activity.CategoryActivity;
import com.gaoshou.android.adapter.ExpertAdapter;
import com.gaoshou.android.entity.DoctorEntity;
import com.gaoshou.common.base.BaseFragment;
import com.gaoshou.common.base.GsApplication;
import com.gaoshou.common.component.AutoLoadMoreListView;
import com.gaoshou.common.component.AutoLoadMoreListView.IAutoLoadMoreListViewListener;
import com.gaoshou.common.constant.APIKey;
import com.gaoshou.common.constant.CommonConstant;
import com.gaoshou.common.constant.ServiceAPIConstant;
import com.gaoshou.common.network.CommonRequest;
import com.gaoshou.common.utils.EntityUtils;
import com.gaoshou.common.utils.TypeUtil;

@SuppressLint("NewApi")
public class ExpertFragment extends BaseFragment implements OnClickListener {
    /** Activity RequestCode */
    private final int START_CITY_ACTIVITY = 301;
    private final int START_MAP_ACTIVITY = 302;
    private final int START_EXPERTISE_ACTIVITY = 303;
    private final int START_CATEGORY_ACTIVITY = 304;

    private boolean isRefreshing;
    private boolean isMoreDataLoading;
    private boolean isPullLoadEnable;

    private View contentView;
    private AutoLoadMoreListView expertsList_xlv;
    private List<DoctorEntity> expertsList;
    private RelativeLayout progress_bar_container_rl;
    private TextView no_record_tv;

    private ExpertAdapter expertAdapter;

    //用于筛选
    private int currentCityID = -1;
    private int currentExpertiseID = -1;
    private int currentCategoryID = -1;
    private double currentLongitude = -1;
    private double currentLatitude = -1;

    private boolean sortByScore = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i("testYJ", "onCreateView");
        contentView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_expert, null);
        setHasOptionsMenu(true);

        LinearLayout city = findView(contentView, R.id.ll_city_container);
        LinearLayout map = findView(contentView, R.id.ll_map_container);
        LinearLayout experty = findView(contentView, R.id.ll_experty_container);
        LinearLayout score = findView(contentView, R.id.ll_score_container);
        LinearLayout sort = findView(contentView, R.id.ll_sort_container);

        city.setOnClickListener(this);
        map.setOnClickListener(this);
        experty.setOnClickListener(this);
        score.setOnClickListener(this);
        sort.setOnClickListener(this);

        progress_bar_container_rl = findView(contentView, R.id.rl_expert_progress_bar_container);
        no_record_tv = findView(contentView, R.id.tv_no_record);

        expertsList = GsApplication.getInstance(getActivity()).getExpertList();
        expertsList_xlv = findView(contentView, R.id.expert_lv);
        expertsList_xlv.setDividerHeight(0);
        expertsList_xlv.setPullLoadEnable(true);
        expertsList_xlv.setPullRefreshEnable(true);
        expertsList_xlv.setXListViewListener(new ShowExpertyXListViewListener());

        return contentView;
    }

    @Override
    public void onStart() {
        super.onStart();
        fetchExperts();
    }

    private void fetchExperts() {
        if (expertsList == null || expertsList.size() == 0) {
            //显示正在加载界面
            expertsList_xlv.setVisibility(View.GONE);
            progress_bar_container_rl.setVisibility(View.VISIBLE);
            no_record_tv.setVisibility(View.GONE);
            fetchExperts(0);
        } else if (expertsList.size() > 0) {
            int toBeContinued = GsApplication.getInstance(getActivity()).getDoctorListToBeContinued();
            switch (toBeContinued) {
                case 0:
                    isPullLoadEnable = false;
                    break;
                case 1:
                    isPullLoadEnable = true;
                    break;

                default:
                    break;
            }
            expertsList_xlv.setPullLoadEnable(isPullLoadEnable);
            showExpertsList();
        }

    }

    private void fetchExperts(int offset) {
        fetchExperts(offset, CommonConstant.MSG_PAGE_SIZE);
    }

    private void fetchExperts(int offset, int pageSize) {
        expertsList_xlv.setVisibility(View.GONE);
        progress_bar_container_rl.setVisibility(View.VISIBLE);
        no_record_tv.setVisibility(View.GONE);
        //发请求刷新
        CommonRequest fetchExpertsRequest = new CommonRequest();
        if (currentLongitude != -1 && currentLatitude != -1) {
            fetchExpertsRequest.setRequestApiName(ServiceAPIConstant.REQUEST_API_NAME_DOCTOR_COORDINATE + "&" + APIKey.COMMON_EXPAND + "=" + APIKey.COMMON_DOCTOR_FILES);
            fetchExpertsRequest.setRequestID(ServiceAPIConstant.REQUEST_ID_DOCTOR_COORDINATE);
            fetchExpertsRequest.addRequestParam(APIKey.DOCTOR_LONGITUDE, currentLongitude);
            fetchExpertsRequest.addRequestParam(APIKey.DOCTOR_LATITUDE, currentLatitude);
            fetchExpertsRequest.addRequestParam(APIKey.COMMON_DISTANCE, 50);
        } else {
            fetchExpertsRequest.setRequestApiName(ServiceAPIConstant.REQUEST_API_NAME_EXPERT_INDEX + "&" + APIKey.COMMON_EXPAND + "=" + APIKey.COMMON_DOCTOR_FILES);
            fetchExpertsRequest.setRequestID(ServiceAPIConstant.REQUEST_ID_EXPERT_INDEX);
            fetchExpertsRequest.addRequestParam(APIKey.COMMON_PAGE, offset / pageSize + 1);

            //筛选条件
            if (currentCityID != -1) {
                fetchExpertsRequest.addRequestParam(APIKey.EXPERT_SEARCH_CITY_ID, currentCityID);
            }
            if (currentExpertiseID != -1) {
                fetchExpertsRequest.addRequestParam(APIKey.EXPERT_SEARCH_EXPERTISE_ID, currentExpertiseID);
            }
            if (currentCategoryID != -1) {
                fetchExpertsRequest.addRequestParam(APIKey.EXPERT_SEARCH_CATEGORY_ID, currentCategoryID);
            }
            //        if (currentLongitude != -1 && currentLatitude != -1) {
            //            fetchExpertsRequest.addRequestParam(APIKey.EXPERT_SEARCH_LONGITUDE, currentLongitude);
            //            fetchExpertsRequest.addRequestParam(APIKey.EXPERT_SEARCH_LATITUDE, currentLatitude);
            //        }
            if (sortByScore) {
                sortByScore = false;
                fetchExpertsRequest.addRequestParam(APIKey.COMMON_SORT, "-" + APIKey.DOCTOR_AVG_SCORE);
            }
        }
        Log.i("testYJ", "fetchExpertsRequsetUrl---->" + fetchExpertsRequest.getRequestParamsSortedMap().toString());
        addRequestAsyncTask(contentView, fetchExpertsRequest);
    }

    private void showExpertsList() {
        Log.i("testYJ", "showExpertList");
        if (null != expertsList && 0 != expertsList.size()) {
            if (expertAdapter == null) {
                expertAdapter = new ExpertAdapter(getActivity(), expertsList);
                expertsList_xlv.setAdapter(expertAdapter);
                expertsList_xlv.setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Log.i("testYJ", "onItemClick");
                        int clickPosition = position - ((ListView) parent).getHeaderViewsCount();
                        GsApplication.getInstance(getActivity()).setCurrentExpert(expertsList.get(clickPosition));
                        Intent intent = new Intent(getActivity(), ExpertDetailActivity.class);
                        startActivity(intent);
                    }
                });

            } else {
                expertAdapter.notifyDataSetChanged();
            }
            expertsList_xlv.setVisibility(View.VISIBLE);
            progress_bar_container_rl.setVisibility(View.GONE);
            no_record_tv.setVisibility(View.GONE);
        } else {
            expertsList_xlv.setVisibility(View.GONE);
            progress_bar_container_rl.setVisibility(View.GONE);
            no_record_tv.setVisibility(View.VISIBLE);
        }
        expertsList_xlv.stopLoadMore();
        expertsList_xlv.stopRefresh();
        isMoreDataLoading = false;
        isRefreshing = false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_city_container:
                myStartActivityForResult(CityActivity.class, START_CITY_ACTIVITY);
                break;
            case R.id.ll_map_container:
                myStartActivityForResult(MapActivity.class, START_MAP_ACTIVITY);
                break;
            case R.id.ll_experty_container:
                myStartActivityForResult(ExpertiseActivity.class, START_EXPERTISE_ACTIVITY);
                break;
            case R.id.ll_score_container:
                clearFilter();
                sortByScore = true;
                autoListRefresh();
                break;
            case R.id.ll_sort_container:
                myStartActivityForResult(CategoryActivity.class, START_CATEGORY_ACTIVITY);
                break;
        }
    }

    @Override
    protected void onResponse(String requestID, boolean isSuccess, int statusCode, String message, List<Map<String, Object>> items, Map<String, Object> links, Map<String, Object> meta, Map<String, Object> additionalArgsmap) {
        if (requestID == ServiceAPIConstant.REQUEST_ID_EXPERT_INDEX || requestID == ServiceAPIConstant.REQUEST_ID_DOCTOR_COORDINATE) {
            if (isSuccess && statusCode == 200) {
                Log.i("testYJ", "Success");
                Log.i("testYJ", "doctorList-->" + items.toString());
                List<DoctorEntity> expertsTemp = EntityUtils.getDoctorEntityList(items);
                if (expertsTemp != null) {
                    Log.i("testYJ", "expertsTemp!=null");
                    Log.i("testYJ", "expertsTemp-->" + expertsTemp.toString());
                    if (!isMoreDataLoading) {
                        expertsList = expertsTemp;
                    } else {
                        expertsList.addAll(expertsTemp);
                    }
                }
                GsApplication.getInstance(getActivity()).setExpertList(expertsList);
                expertsList_xlv.setRefreshTime(CommonConstant.serverTimeFormat.format(new Date()));

                int toBeContinued = 0;
                if (currentLongitude == -1 && currentLatitude == -1) {
                    int pageCount = TypeUtil.getInteger(meta.get(APIKey.COMMON_PAGE_COUNT));
                    int currentPage = TypeUtil.getInteger(meta.get(APIKey.COMMON_CURRENT_PAGE));
                    toBeContinued = pageCount == currentPage ? 0 : 1;
                }
                switch (toBeContinued) {
                    case 0:
                        isPullLoadEnable = false;
                        break;
                    case 1:
                        isPullLoadEnable = true;
                        break;
                    default:
                        break;
                }
                GsApplication.getInstance(getActivity()).setDoctorListToBeContinued(toBeContinued);
                expertsList_xlv.setPullLoadEnable(isPullLoadEnable);
                showExpertsList();
            }
        }
    }

    private void myStartActivityForResult(Class<?> toClass, int requestCode) {
        Intent intent = new Intent(getActivity(), toClass);
        startActivityForResult(intent, requestCode);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case START_CITY_ACTIVITY:
                    //清空其他分类数据
                    clearFilter();
                    currentCityID = data.getIntExtra(getString(R.string.current_city), -1);
                    Log.i("testYJ", "currentCity-->" + currentCityID);
                    break;
                case START_MAP_ACTIVITY:
                    clearFilter();
                    currentLongitude = data.getDoubleExtra(getString(R.string.current_longitude), -1);
                    currentLatitude = data.getDoubleExtra(getString(R.string.current_latitude), -1);
                    Log.i("testYJ", "currentLocation-->" + currentLongitude + "," + currentLatitude);
                    break;
                case START_EXPERTISE_ACTIVITY:
                    clearFilter();
                    currentExpertiseID = data.getIntExtra(getString(R.string.current_experty), -1);
                    Log.i("testYJ", "currentExperty-->" + currentExpertiseID);
                    break;
                case START_CATEGORY_ACTIVITY:
                    clearFilter();
                    currentCategoryID = data.getIntExtra(getString(R.string.current_sort), -1);
                    Log.i("testYJ", "currentSort-->" + currentCategoryID);
                    break;
            }
            autoListRefresh();
        }
    }

    class ShowExpertyXListViewListener implements IAutoLoadMoreListViewListener {
        @Override
        //下拉刷新
        public void onRefresh() {
            autoListRefresh();
        }

        @Override
        //上滑读取更多
        public void onLoadMore() {
            if (!isRefreshing && !isMoreDataLoading) {
                isMoreDataLoading = true;
                if (expertsList == null) {
                    fetchExperts(0);
                } else {
                    fetchExperts(expertsList.size(), CommonConstant.MSG_PAGE_SIZE);
                }
            }
        }

    }

    private void autoListRefresh() {
        if (!isRefreshing && !isMoreDataLoading) {
            isRefreshing = true;
            expertsList = null;
            expertAdapter = null;
            fetchExperts(0);
        }
    }

    private void clearFilter() {
        currentCategoryID = -1;
        currentCityID = -1;
        currentExpertiseID = -1;
        currentLatitude = -1;
        currentLongitude = -1;
        sortByScore = false;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_fragment_expert, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.auto_sort:
                clearFilter();
                autoListRefresh();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroyView() {
        Log.i("testYJ", "ondestroyView");
        expertAdapter = null;
        expertsList = null;
        super.onDestroyView();
    }
}
