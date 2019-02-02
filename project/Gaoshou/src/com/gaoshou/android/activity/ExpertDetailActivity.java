package com.gaoshou.android.activity;

import java.util.Map;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.gaoshou.android.R;
import com.gaoshou.android.entity.DoctorEntity;
import com.gaoshou.android.entity.FavoriteEntity;
import com.gaoshou.android.fragment.ExpertDetailFragment;
import com.gaoshou.common.base.BaseActivity;
import com.gaoshou.common.base.GsApplication;
import com.gaoshou.common.constant.APIKey;
import com.gaoshou.common.constant.ServiceAPIConstant;
import com.gaoshou.common.network.CommonRequest;
import com.gaoshou.common.utils.EntityUtils;
import com.gaoshou.common.utils.NetworkUtil;
import com.gaoshou.common.utils.OpenFileUtil;

public class ExpertDetailActivity extends BaseActivity {
    private DoctorEntity expert;

    private MenuItem collect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expert_detail);

        initToolbar(getString(R.string.expert_detail_title));

        expert = GsApplication.getInstance(ExpertDetailActivity.this).getCurrentExpert();
        
        initFragment();

    }


    private void initFragment() {
        ExpertDetailFragment evaluationListFragment = new ExpertDetailFragment();
        FragmentManager fm = getSupportFragmentManager();
        if (fm != null)
            fm.beginTransaction().replace(R.id.fl_container, evaluationListFragment).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_expert_detail, menu);
        collect = menu.findItem(R.id.item_collection);
        if(expert!=null)
        if (GsApplication.getInstance(getContext()).getExpertyHasLikes(expert.getId(), false)) {
            if (collect != null)
                collect.setTitle(getString(R.string.actionbar_menu_collectioned));
            collect.setIcon(null);
            collect.setEnabled(false);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case R.id.item_collection:
                //收藏专家
                collectExpert();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void collectExpert() {
        if (NetworkUtil.isNetworkAvaliable(getContext())) {
            showProgressDialog(getString(R.string.collecting));
            CommonRequest createFavoriteRequest = new CommonRequest();
            createFavoriteRequest.setRequestApiName(ServiceAPIConstant.REQUEST_API_NAME_FAVORITE_CREATE + "&" + APIKey.COMMON_EXPAND + "=" + APIKey.COMMON_DOCTOR + "," + APIKey.FAVORITE_FAVORITE_DOCTOR);
            createFavoriteRequest.setRequestID(ServiceAPIConstant.REQUEST_ID_FAVORITE_CREATE);
            createFavoriteRequest.setRequestMethod(CommonRequest.REQUEST_METHOD_POST);
            if (GsApplication.getInstance(getContext()).getMyself() != null)
                createFavoriteRequest.addRequestParam(APIKey.COMMON_DOCTOR_ID, GsApplication.getInstance(getContext()).getMyself().getId());
            createFavoriteRequest.addRequestParam(APIKey.FAVORITE_FAVORITE_DOCTOR_ID, expert.getId());

            addRequestAsyncTask(createFavoriteRequest);
        } else {
            showToast(getString(R.string.network_error));
        }
    }

    @Override
    protected void onResponse(String requestID, boolean isSuccess, int statusCode, String message, Map<String, Object> data, Map<String, Object> additionalArgsmap) {
        if (requestID == ServiceAPIConstant.REQUEST_ID_FAVORITE_CREATE) {
            if (isSuccess & statusCode == 201) {
                if (data != null) {
                    FavoriteEntity favorite = EntityUtils.getfavoriteEntity(data);
                    if (favorite != null) {
                        showToast(getString(R.string.success_to_collect));
                        GsApplication.getInstance(getContext()).setExpertyHasLikes(expert.getId(), true);
                        if (collect != null) {
                            collect.setTitle(getString(R.string.actionbar_menu_collectioned));
                            collect.setIcon(null);
                            collect.setEnabled(false);
                        }
                        dimissProgressDialog();
                        return;
                    }
                }
                showToast(getString(R.string.faild_to_collect));

            } else {
                showToast(message);
            }
        }
        dimissProgressDialog();
    }


    @Override
    protected void onDestroy() {
        GsApplication.getInstance(getContext()).setCurrentExpert(null);
        super.onDestroy();
    }
}
