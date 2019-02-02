package com.gaoshou.android.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ZoomControls;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionResult.SuggestionInfo;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;
import com.gaoshou.android.R;
import com.gaoshou.android.adapter.StringAdapter;
import com.gaoshou.android.entity.BaiduLocationEntity;
import com.gaoshou.android.entity.BaiduLocationEntity.MyLocationListener;
import com.gaoshou.android.fragment.ExpertFragment;
import com.gaoshou.common.base.BaseActivity;

public class MapActivity extends BaseActivity implements MyLocationListener, TextWatcher {
    private EditText location_et;
    private MapView mapView;
    private PopupWindow popupWindow;
    private View view;
    private ImageView iv_location;
    private ListView popuList;

    private BaiduMap map;
    private GeoCoder mSearch;

    private BaiduLocationEntity location;

    private ArrayList<String> hintDatas;
    private StringAdapter<String> adapter;

    private boolean isLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        initView();

        initToolbar(getString(R.string.actionbar_title_map));
        ;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mSearch != null) {
            mSearch.destroy();
            mSearch = null;
        }
        if (location != null) {
            location.stopLocate();
        }
        if (map != null) {
            map.setMyLocationEnabled(false);
        }
        mapView.onDestroy();
    }

    private void initView() {
        iv_location = findView(R.id.iv_location);
        iv_location.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isLocation) {
                    if (iv_location != null) {
                        iv_location.setImageDrawable(getResources().getDrawable(R.drawable.location_yes));
                    }
                    initLocationClient();
                    isLocation = true;
                }
            }
        });

        initHintData();

        initMapView();

        initBaiDuMap();

        initLocationClient();

    }

    private void initHintData() {
        location_et = findView(R.id.edt_location);
        location_et.addTextChangedListener(this);
    }

    private void initBaiDuMap() {
        // 获取百度地图实例
        map = mapView.getMap();
        // 设置地图为普通地图
        map.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        // 允许定位
        map.setMyLocationEnabled(true);
    }

    private void initMapView() {
        mapView = findView(R.id.iv_baidu_map);
        // 隐藏地图中的缩放控件
        int childCount = mapView.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childView = mapView.getChildAt(i);
            if (childView instanceof ZoomControls && childView != null) {
                childView.setVisibility(View.GONE);
                break;
            }
        }
    }

    // 定位
    private void initLocationClient() {
        location = BaiduLocationEntity.getInstance(MapActivity.this);
        location.setLocationListener(MapActivity.this);
        location.startLocate();
    }

    // 自动定位
    private void showLocation(BDLocation location, int locType) {
        MyLocationData locData = new MyLocationData.Builder().direction(100).latitude(location.getLatitude()).longitude(location.getLongitude()).build();
        map.setMyLocationData(locData);

        LatLng point = new LatLng(location.getLatitude(), location.getLongitude());
        map.animateMapStatus(MapStatusUpdateFactory.newLatLngZoom(point, 19f));
        // 若为网络定位或GPS定位,则停止定位
        if (locType == 61 || locType == 161) {
            // map.setMyLocationEnabled(false);
            this.location.stopLocate();
        }

        if (iv_location != null) {
            iv_location.setImageDrawable(getResources().getDrawable(R.drawable.location_no));
        }

        isLocation = false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_map, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.submit_btn:
                Intent intent = new Intent(getContext(), HomeActivity.class);
                intent.putExtra(getString(R.string.current_longitude), map.getLocationData().longitude);
                intent.putExtra(getString(R.string.current_latitude), map.getLocationData().latitude);
                setResult(Activity.RESULT_OK, intent);
                finish();
                break;
        }
        return true;
    }

    private void searchLocation() {
        String address = location_et.getText().toString().trim();
        Log.i("testYJ", address);
        if (mSearch == null) {
            mSearch = GeoCoder.newInstance();
        }
        mSearch.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {
            // 反向地图编码(经纬度转换为地址)
            @Override
            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {

            }

            // 正向地图编码(地址转换为经纬度)
            @Override
            public void onGetGeoCodeResult(GeoCodeResult result) {
                if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                    // 没有检索到结果
                    showToast(getString(R.string.search_fail));
                } else {
                    // 获取地理编码结果并移动
                    MyLocationData mLocation = new MyLocationData.Builder().direction(100).latitude(result.getLocation().latitude).longitude(result.getLocation().longitude).build();
                    map.setMyLocationData(mLocation);
                    map.animateMapStatus(MapStatusUpdateFactory.newLatLngZoom(result.getLocation(), 17f));
                }

            }
        });
        mSearch.geocode(new GeoCodeOption().city(address).address(address));
    }

    @Override
    protected void onResponseAsyncTaskRender(int status, String message, int toBeContinued, Map<String, Object> resultMap, String requestID, Map<String, Object> additionalArgsMap) {

    }

    @Override
    public void onReceiveLocation(BDLocation location, int locType) {
        showLocation(location, locType);
    }

    @Override
    public void afterTextChanged(Editable s) {
        final String address = s.toString();
        final SuggestionSearch psearch = SuggestionSearch.newInstance();
        psearch.setOnGetSuggestionResultListener(new OnGetSuggestionResultListener() {
            @Override
            public void onGetSuggestionResult(SuggestionResult result) {
                if (result != null && result.getAllSuggestions() != null) {
                    Log.i("testYJ", result.getAllSuggestions() + "");
                    List<SuggestionInfo> infos = result.getAllSuggestions();
                    if (infos != null && infos.size() > 0) {
                        hintDatas = new ArrayList<String>();
                        int maxSize = infos.size() >= 5 ? 5 : infos.size();
                        for (int i = 0; i < maxSize; i++) {
                            SuggestionInfo info = infos.get(i);
                            Log.i("testYJ", "address-->" + info.city + info.district + info.key);
                            hintDatas.add(info.city + info.district + info.key);
                        }
                        showDataHint();
                        psearch.destroy();
                    }
                }

            }

            private void showDataHint() {
                if (popupWindow == null) {
                    view = getLayoutInflater().inflate(R.layout.popupwinodw_view, null);
                    popuList = (ListView) view.findViewById(R.id.lv_popupwindow);
                    popupWindow = new PopupWindow(view, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
                }
                popupWindow.setOutsideTouchable(true);
                popupWindow.setBackgroundDrawable(new BitmapDrawable());
                popupWindow.showAsDropDown(location_et, 0, 0);
                adapter = new StringAdapter<String>(getContext(), hintDatas);
                popuList.setAdapter(adapter);
                popuList.setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                        location_et.removeTextChangedListener(MapActivity.this);
                        location_et.setText(hintDatas.get(position));
                        location_et.addTextChangedListener(MapActivity.this);

                        popupWindow.dismiss();

                        searchLocation();
                    }
                });
            }
        });
        psearch.requestSuggestion(new SuggestionSearchOption().city(address).keyword(address));

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

}
