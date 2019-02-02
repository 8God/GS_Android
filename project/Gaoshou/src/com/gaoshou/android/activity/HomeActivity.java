package com.gaoshou.android.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.jpush.android.api.JPushInterface;

import com.gaoshou.android.R;
import com.gaoshou.android.entity.DoctorEntity;
import com.gaoshou.android.entity.FavoriteEntity;
import com.gaoshou.android.fragment.ExpertFragment;
import com.gaoshou.android.fragment.HomeFragment;
import com.gaoshou.android.fragment.MsgNotificationFragment;
import com.gaoshou.android.fragment.PersonFragment;
import com.gaoshou.android.geo.Address;
import com.gaoshou.android.geo.GeoService;
import com.gaoshou.android.geo.GeoService.IGeoServiceToRenderListener;
import com.gaoshou.common.base.BaseActivity;
import com.gaoshou.common.base.BaseFragment;
import com.gaoshou.common.base.GsApplication;
import com.gaoshou.common.component.MyActivityManager;
import com.gaoshou.common.constant.APIKey;
import com.gaoshou.common.constant.CommonConstant;
import com.gaoshou.common.constant.ServiceAPIConstant;
import com.gaoshou.common.network.CommonRequest;
import com.gaoshou.common.utils.DialogUtils;
import com.gaoshou.common.utils.EntityUtils;
import com.gaoshou.common.utils.Validator;

public class HomeActivity extends BaseActivity implements OnClickListener {

    private Address latestAddress;

    private static final String TAG = "RestAPIRequester";
    private static final int INIT_CURRENT_ITEM = 0;
    private static final int EXPERT_ITEM = 1;
    private int currentItem = INIT_CURRENT_ITEM;

    private int[] titleArray = new int[] { R.string.title_tab_home, R.string.title_tab_expert, R.string.title_tab_msg, R.string.title_tab_person };
    private List<BaseFragment> fragmentList;
    private List<ImageView> tabIconList;
    private List<TextView> tabTitleList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int userId = GsApplication.getInstance(getContext()).getUserId();
        JPushInterface.setAlias(getContext(), userId + "", null);

        setContentView(R.layout.activity_home);

        init();

        changeShowingFragment(INIT_CURRENT_ITEM);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Intent intent = getIntent();
        if (intent != null)
            if (intent.getBooleanExtra("expertFragment", false))
                changeShowingFragment(EXPERT_ITEM);
    }

    private void init() {
        initGeoService();
        initToolbar(getString(R.string.title_tab_home), false);
        initFragmentList();
        initUI();

        updateUserInfo();
    }

    private void initFragmentList() {
        fragmentList = new ArrayList<BaseFragment>();

        HomeFragment homeFragment = new HomeFragment();
        ExpertFragment bbsFragment = new ExpertFragment();
        MsgNotificationFragment quesAnswerFragment = new MsgNotificationFragment();
        PersonFragment personFragment = new PersonFragment();

        fragmentList.add(homeFragment);
        fragmentList.add(bbsFragment);
        fragmentList.add(quesAnswerFragment);
        fragmentList.add(personFragment);
    }

    private void initUI() {
        initTabbar();

        //init tab button
        RelativeLayout tabHomeRl = findView(R.id.rl_home_tab_home);
        RelativeLayout tabExpertRl = findView(R.id.rl_home_tab_expert);
        RelativeLayout tabMsgARl = findView(R.id.rl_home_tab_msg);
        RelativeLayout tabPersonRl = findView(R.id.rl_home_tab_person);

        tabHomeRl.setOnClickListener(this);
        tabExpertRl.setOnClickListener(this);
        tabMsgARl.setOnClickListener(this);
        tabPersonRl.setOnClickListener(this);
    }

    private void initTabbar() {
        ImageView homeImv = findView(R.id.imv_tab_home);
        ImageView expertImv = findView(R.id.imv_tab_expert);
        ImageView msgImv = findView(R.id.imv_tab_msg);
        ImageView personImv = findView(R.id.imv_tab_person);

        tabIconList = new ArrayList<ImageView>();
        tabIconList.add(homeImv);
        tabIconList.add(expertImv);
        tabIconList.add(msgImv);
        tabIconList.add(personImv);

        TextView homeTv = findView(R.id.tv_tab_home);
        TextView expertTv = findView(R.id.tv_tab_expert);
        TextView msgTv = findView(R.id.tv_tab_msg);
        TextView personTv = findView(R.id.tv_tab_person);

        tabTitleList = new ArrayList<TextView>();
        tabTitleList.add(homeTv);
        tabTitleList.add(expertTv);
        tabTitleList.add(msgTv);
        tabTitleList.add(personTv);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.rl_home_tab_home:
                changeShowingFragment(0);
                break;
            case R.id.rl_home_tab_expert:
                changeShowingFragment(1);
                break;
            case R.id.rl_home_tab_msg:
                changeShowingFragment(2);
                break;
            case R.id.rl_home_tab_person:
                changeShowingFragment(3);
                break;
            default:
                break;
        }

    }

    private void changeShowingFragment(int clickedItem) {
        if (null != fragmentList) {
            BaseFragment showingFragment = fragmentList.get(clickedItem);
            if (null != showingFragment) {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.fragment_container, showingFragment).commitAllowingStateLoss();

                setActionBarTitle(clickedItem);
                changeTabSelectedStatus(clickedItem);

                currentItem = clickedItem;
            }
        }
    }

    private void setActionBarTitle(int position) {
        ActionBar actionBar = getSupportActionBar();
        if (null != actionBar) {
            String title = getString(titleArray[position]);
            actionBar.setTitle(title);
        }
    }

    private void changeTabSelectedStatus(int position) {
        if (null != tabIconList) {
            tabIconList.get(currentItem).setEnabled(true);
            tabIconList.get(position).setEnabled(false);
        }

        if (null != tabTitleList) {
            tabTitleList.get(currentItem).setEnabled(true);
            tabTitleList.get(position).setEnabled(false);
        }

        currentItem = position;
    }

    private void updateUserInfo() {
        DoctorEntity doctorEntity = GsApplication.getInstance(getContext()).getMyself();
        if (null != doctorEntity) {
            int userId = doctorEntity.getId();
            if (Validator.isIdValid(userId)) {
                CommonRequest updateUserInfoRequest = new CommonRequest();
                updateUserInfoRequest.setRequestApiName(ServiceAPIConstant.REQUEST_API_NAME_DOCTOR_VIEW);
                updateUserInfoRequest.setRequestID(ServiceAPIConstant.REQUEST_ID_DOCTOR_VIEW);
                updateUserInfoRequest.addRequestParam(APIKey.COMMON_ID, userId);
                updateUserInfoRequest.addRequestParam(APIKey.COMMON_EXPAND, APIKey.COMMON_DOCTOR_FILES + "," + APIKey.DOCTOR_EXPERTISE + "," + APIKey.DOCTOR_CATEGORY + "," + APIKey.COMMON_CITY);

                addRequestAsyncTask(updateUserInfoRequest);
            }
        }
    }

    /**
     * 每次打开APP，定位后更新用户最新位置和经纬度
     * 
     * @param currentAddress
     * @param latitude
     * @param longitude
     */
    private void updateUserCurrentPosition(String currentAddress, double latitude, double longitude) {
        DoctorEntity doctorEntity = GsApplication.getInstance(getContext()).getMyself();
        if (null != doctorEntity) {
            int userId = doctorEntity.getId();
            if (Validator.isIdValid(userId)) {
                CommonRequest updateUserInfoRequest = new CommonRequest();
                switch (GsApplication.getInstance().getLogonType()) {
                    case CommonConstant.LOGON_TYPE_DOCTOR:
                        updateUserInfoRequest.setRequestApiName(ServiceAPIConstant.REQUEST_API_NAME_DOCTOR_UPDATE);
                        updateUserInfoRequest.setRequestID(ServiceAPIConstant.REQUEST_ID_DOCTOR_UPDATE);
                        break;
                    case CommonConstant.LOGON_TYPE_EXPERT:
                        updateUserInfoRequest.setRequestApiName(ServiceAPIConstant.REQUEST_API_NAME_EXPERT_UPDATE);
                        updateUserInfoRequest.setRequestID(ServiceAPIConstant.REQUEST_ID_EXPERT_UPDATE);
                        break;
                    default:
                        break;
                }
                updateUserInfoRequest.addRequestParam(APIKey.COMMON_ID, userId);
                updateUserInfoRequest.addRequestParam(APIKey.DOCTOR_CURRENT_ADDRESS, currentAddress);
                updateUserInfoRequest.addRequestParam(APIKey.DOCTOR_LATITUDE, latitude);
                updateUserInfoRequest.addRequestParam(APIKey.DOCTOR_LONGITUDE, longitude);

                addRequestAsyncTask(updateUserInfoRequest);

                CommonRequest updateFavoriteExpertRequest = new CommonRequest();
                updateFavoriteExpertRequest.setRequestApiName(ServiceAPIConstant.REQUEST_API_NAME_FAVORITE_INDEX);
                updateFavoriteExpertRequest.setRequestID(ServiceAPIConstant.REQUEST_ID_FAVORITE_INDEX);
                updateFavoriteExpertRequest.addRequestParam(APIKey.FAVORITE_FAVORITE_SEARCH_DOCTOR_ID, userId);

                addRequestAsyncTask(updateFavoriteExpertRequest);
            }
        }
    }

    @Override
    protected void onResponse(String requestID, boolean isSuccess, int statusCode, String message, Map<String, Object> data, Map<String, Object> additionalArgsmap) {
        super.onResponse(requestID, isSuccess, statusCode, message, data, additionalArgsmap);
        if (ServiceAPIConstant.REQUEST_ID_DOCTOR_VIEW.equals(requestID)) {
            if (isSuccess && data != null) {
                DoctorEntity doctorEntity = EntityUtils.getDoctorEntity(data);
                if (null != doctorEntity) {
                    GsApplication.getInstance(getContext()).setMyself(doctorEntity);
                }
            }
        }
    }

    @Override
    protected void onResponse(String requestID, boolean isSuccess, int statusCode, String message, List<Map<String, Object>> items, Map<String, Object> links, Map<String, Object> meta, Map<String, Object> additionalArgsmap) {
        if (requestID == ServiceAPIConstant.REQUEST_ID_FAVORITE_INDEX) {
            if (isSuccess && statusCode == 200) {
                List<FavoriteEntity> favorites = EntityUtils.getfavoriteEntityList(items);
                if (favorites != null) {
                    for (FavoriteEntity favorite : favorites) {
                        if (favorite != null) {
                            GsApplication.getInstance(getContext()).setExpertyHasLikes(favorite.getFavourite_doctor_id(), true);
                            Log.i("testYJ", "获取收藏专家成功");
                        }
                    }
                } else {
                    Log.i("testYJ", "获取收藏专家出错");
                }
            } else {
                showToast(message);
            }
        }
    }

    /**
     * 定位当前位置
     */
    public void initGeoService() {
        GeoService geoService = new GeoService(this);
        geoService.setGeoServiceToRenderListener(new IGeoServiceToRenderListener() {

            @Override
            public void toRender(Address address) {
                GsApplication.getInstance(getContext()).setLatestAddress(address);
                if (null != address) {
                    String currentAddress = address.getLocationName();
                    double latitude = address.getLatitude();
                    double longitude = address.getLongitude();
                    updateUserCurrentPosition(currentAddress, latitude, longitude);
                }
            }
        });
        geoService.execute();
    }

    @Override
    public void onBackPressed() {
        //        super.onBackPressed();

        DialogUtils.showAlertDialog(getContext(), "提示", "是否退出" + getString(R.string.app_name), "退出", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                MyActivityManager.getInstance().clearAllActivity();

                System.exit(0);
            }
        }, "取消", null);
    }

}
