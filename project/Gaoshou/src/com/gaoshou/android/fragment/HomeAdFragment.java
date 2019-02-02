package com.gaoshou.android.fragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gaoshou.android.R;
import com.gaoshou.common.base.GsApplication;
import com.gaoshou.common.component.AutoScrollPagerFragment;
import com.gaoshou.common.component.ScrollPagerEntity;
import com.gaoshou.common.constant.APIKey;
import com.gaoshou.common.constant.ServiceAPIConstant;
import com.gaoshou.common.network.CommonRequest;
import com.gaoshou.common.utils.EntityUtils;

public class HomeAdFragment extends AutoScrollPagerFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        contentView = inflater.inflate(R.layout.fragment_home_ad, null);

        return contentView;
    }

    @Override
    public void getSrcollPagerList() {

        scrollPagerList = GsApplication.getInstance(getActivity()).getHomeBannerList();

        if (null != scrollPagerList && scrollPagerList.size() > 0) {
            onFinishLoadScrollPagerList();
        } else {
            CommonRequest fetchBannerList = new CommonRequest();
            fetchBannerList.setRequestApiName(ServiceAPIConstant.REQUEST_API_NAME_ADVERTISEMENT_INDEX);
            fetchBannerList.setRequestID(ServiceAPIConstant.REQUEST_ID_ADVERTISEMENT_INDEX);
            fetchBannerList.addRequestParam(APIKey.APP_AD_SEARCH_TYPE, 1);

            addRequestAsyncTask(contentView, fetchBannerList);
        }
    }

    @Override
    protected void onResponse(String requestID, boolean isSuccess, int statusCode, String message, List<Map<String, Object>> items, Map<String, Object> links, Map<String, Object> meta, Map<String, Object> additionalArgsmap) {
        super.onResponse(requestID, isSuccess, statusCode, message, items, links, meta, additionalArgsmap);
        if (ServiceAPIConstant.REQUEST_ID_ADVERTISEMENT_INDEX.equals(requestID)) {
            if (isSuccess) {
                if (null != items && items.size() > 0) {
                    List<ScrollPagerEntity> scrollPagerEntityList = EntityUtils.getScrollPagerEntityList(items);

                    if (null != scrollPagerEntityList && scrollPagerEntityList.size() > 0) {
                        scrollPagerList = new ArrayList<AutoScrollPagerFragment.ScrollPager>();

                        for (int i = 0; i < scrollPagerEntityList.size(); i++) {
                            ScrollPagerEntity scrollPagerEntity = scrollPagerEntityList.get(i);
                            if (null != scrollPagerEntity) {
                                ScrollPager scrollPager = new ScrollPager(getActivity(), scrollPagerEntity);

                                scrollPagerList.add(scrollPager);

                            }
                        }

                        if (null != scrollPagerList && scrollPagerList.size() > 0) {
                            GsApplication.getInstance(getActivity()).setHomeBannerList(scrollPagerList);
                        }
                        onFinishLoadScrollPagerList();
                    }
                }
            } else {
                showToast(message);
            }
        }
    }

}
