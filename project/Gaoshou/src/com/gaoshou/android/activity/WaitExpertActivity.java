package com.gaoshou.android.activity;

import java.util.List;
import java.util.Map;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;

import com.gaoshou.android.R;
import com.gaoshou.android.fragment.WaitExpertFragment;
import com.gaoshou.common.base.BaseActivity;
import com.gaoshou.common.base.SimpleWebViewActivity;
import com.gaoshou.common.component.ScrollPagerEntity;
import com.gaoshou.common.constant.APIKey;
import com.gaoshou.common.constant.ServiceAPIConstant;
import com.gaoshou.common.network.CommonRequest;
import com.gaoshou.common.utils.EntityUtils;

public class WaitExpertActivity extends BaseActivity {

    private int consultationId;

    public static final String FILTER_UPDATE_WATING_EXPERT_LIST = "FILTER_UPDATE_WATING_EXPERT_LIST";

    private WaitExpertFragment waitExpertFragment;

    private UpdateWatingListReceiver updateWatingListReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wait_expert);

        init();

        IntentFilter filter = new IntentFilter();
        filter.addAction(FILTER_UPDATE_WATING_EXPERT_LIST);
        updateWatingListReceiver = new UpdateWatingListReceiver();
        registerReceiver(updateWatingListReceiver, filter);

    }

    private void init() {
        fetchAdData();
        initData();
        initToolbar("高手在接招");
        initFragment();
    }

    private void initData() {
        consultationId = getIntent().getIntExtra(APIKey.COMMON_ID, -1);
    }

    private void fetchAdData() {
        CommonRequest fetchBannerList = new CommonRequest();
        fetchBannerList.setRequestApiName(ServiceAPIConstant.REQUEST_API_NAME_ADVERTISEMENT_INDEX);
        fetchBannerList.setRequestID(ServiceAPIConstant.REQUEST_ID_ADVERTISEMENT_INDEX);
        fetchBannerList.addRequestParam(APIKey.APP_AD_SEARCH_TYPE, 2);

        addRequestAsyncTask(fetchBannerList);
    }

    @Override
    protected void onResponse(String requestID, boolean isSuccess, int statusCode, String message, List<Map<String, Object>> items, Map<String, Object> links, Map<String, Object> meta, Map<String, Object> additionalArgsmap) {
        super.onResponse(requestID, isSuccess, statusCode, message, items, links, meta, additionalArgsmap);
        if (ServiceAPIConstant.REQUEST_ID_ADVERTISEMENT_INDEX.equals(requestID)) {
            if (isSuccess) {
                if (null != items && items.size() > 0) {
                    List<ScrollPagerEntity> scrollPagerEntityList = EntityUtils.getScrollPagerEntityList(items);

                    if (null != scrollPagerEntityList && scrollPagerEntityList.size() > 0) {
                        final ScrollPagerEntity scrollPagerEntity = scrollPagerEntityList.get(0);
                        if (null != scrollPagerEntity) {
                            setImageView(R.id.imv_wating_expert_ad, scrollPagerEntity.getBannerBgUrl(), R.drawable.loading_bg);
                            findView(R.id.ll_wating_expert_ad).setVisibility(View.VISIBLE);

                            setViewClickListener(R.id.imv_wating_expert_ad, new OnClickListener() {

                                @Override
                                public void onClick(View v) {
                                    Intent openContent = new Intent(getContext(), SimpleWebViewActivity.class);
                                    openContent.putExtra(SimpleWebViewActivity.WEB_URL_KEY, scrollPagerEntity.getContentUrl());
                                    openContent.putExtra(SimpleWebViewActivity.ACTION_BAR_TITLE_KEY, scrollPagerEntity.getBannerTitle());
                                    getContext().startActivity(openContent);
                                }
                            });
                        }
                    }
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unregisterReceiver(updateWatingListReceiver);
    }

    private void initFragment() {
        waitExpertFragment = new WaitExpertFragment(consultationId);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fl_container, waitExpertFragment).commit();

    }

    class UpdateWatingListReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (FILTER_UPDATE_WATING_EXPERT_LIST.equals(action)) {
                if (null != waitExpertFragment) {
                    waitExpertFragment.updateDataList();
                }
            }
        }

    }
}
