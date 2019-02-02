package com.gaoshou.android.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.gaoshou.android.R;
import com.gaoshou.android.adapter.StringAdapter;
import com.gaoshou.android.entity.BaiduLocationEntity;
import com.gaoshou.android.entity.BaiduLocationEntity.MyLocationListener;
import com.gaoshou.android.entity.CityEntity;
import com.gaoshou.common.base.BaseActivity;
import com.gaoshou.common.component.AutoLoadMoreListView;
import com.gaoshou.common.component.AutoLoadMoreListView.IAutoLoadMoreListViewListener;
import com.gaoshou.common.constant.APIKey;
import com.gaoshou.common.constant.CommonConstant;
import com.gaoshou.common.constant.ServiceAPIConstant;
import com.gaoshou.common.network.CommonRequest;
import com.gaoshou.common.utils.EntityUtils;
import com.gaoshou.common.utils.NetworkUtil;
import com.gaoshou.common.widget.ListDialogBuilder;

public class CityActivity extends BaseActivity implements MyLocationListener {
    private CityEntity currentCity;
    private List<CityEntity> joindedCities = new ArrayList<CityEntity>();

    private boolean isRefresh;
    private boolean isLoadMore;

    private StringAdapter<CityEntity> adapter;

    private BaiduLocationEntity location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city);

        currentCity = new CityEntity();

        initToolbar(getString(R.string.actionbar_title_select_city));

        init();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void init() {
        // 定位获取当前城市
        initLocationClient();

        fetchJoindCitiesList();
    }

    private void initLocationClient() {
        location = BaiduLocationEntity.getInstance(CityActivity.this);
        location.setLocationListener(CityActivity.this);
        location.startLocate();
    }

    protected void fetchJoindCitiesList() {
        if (NetworkUtil.isNetworkAvaliable(CityActivity.this)) {
            CommonRequest fetchCityRequest = new CommonRequest();
            fetchCityRequest.setRequestApiName(ServiceAPIConstant.REQUEST_API_NAME_CITY_INDEX);
            fetchCityRequest.setRequestID(ServiceAPIConstant.REQUEST_ID_CITY_INDEX);
            addRequestAsyncTask(fetchCityRequest);
        } else {
            showToast(getString(R.string.network_error));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (android.R.id.home == item.getItemId()) {
            backStep();
        }
        return true;
    }

    @Override
    protected void onResponse(String requestID, boolean isSuccess, int statusCode, String message, List<Map<String, Object>> items, Map<String, Object> links, Map<String, Object> meta, Map<String, Object> additionalArgsmap) {
        if (requestID == ServiceAPIConstant.REQUEST_ID_CITY_INDEX) {
            if (isSuccess && statusCode == 200) {
                Log.i("testYJ", items.toString());
                List<CityEntity> citiesList = EntityUtils.getCityEntityList(items);
                if (citiesList != null) {
                    joindedCities = citiesList;
                }

                showJoinedCitiesList();
            }
        }
    }

    private void showJoinedCitiesList() {
        if (adapter == null) {
            ListView joinedCities_lv = findView(R.id.lv_joined_cities);
            adapter = new StringAdapter<CityEntity>(CityActivity.this, joindedCities);
            joinedCities_lv.setAdapter(adapter);

            joinedCities_lv.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    int clickPosition = position - ((ListView) parent).getHeaderViewsCount();
                    currentCity = joindedCities.get(clickPosition);
                    setTextView(R.id.tv_current_city, currentCity.getName());
                }

            });
        } else {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onReceiveLocation(BDLocation location, int locType) {
        currentCity.setName(location.getCity().substring(0, location.getCity().indexOf('市')));
        setTextView(R.id.tv_current_city, currentCity.getName());
        this.location.stopLocate();
    }

    @Override
    public void onBackPressed() {
        backStep();
    }

    private void backStep() {
        Intent intent = new Intent(CityActivity.this, HomeActivity.class);
        intent.putExtra(getString(R.string.current_city), currentCity.getId());
        setResult(RESULT_OK, intent);
        finish();
    }

}
