package com.gaoshou.android.activity;

import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.gaoshou.android.R;
import com.gaoshou.android.adapter.StringAdapter;
import com.gaoshou.android.entity.ExpertiseEntity;
import com.gaoshou.common.base.BaseActivity;
import com.gaoshou.common.constant.ServiceAPIConstant;
import com.gaoshou.common.network.CommonRequest;
import com.gaoshou.common.utils.EntityUtils;
import com.gaoshou.common.utils.NetworkUtil;

public class ExpertiseActivity extends BaseActivity {
    private List<ExpertiseEntity> expertises;
    private StringAdapter<ExpertiseEntity> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_experty);

        initToolbar(getString(R.string.actionbar_title_select_expertise));

        init();
    }

    private void init() {
        fetchExpertiseList();
    }

    private void fetchExpertiseList() {
        if (NetworkUtil.isNetworkAvaliable(ExpertiseActivity.this)) {
            CommonRequest fetchExpertiseRequest = new CommonRequest();
            fetchExpertiseRequest.setRequestApiName(ServiceAPIConstant.REQUEST_API_NAME_EXPERTISE_INDEX);
            fetchExpertiseRequest.setRequestID(ServiceAPIConstant.REQUEST_ID_EXPERTISE_INDEX);
            addRequestAsyncTask(fetchExpertiseRequest);
        } else {
            showToast(getString(R.string.network_error));
        }
    }

    @Override
    protected void onResponse(String requestID, boolean isSuccess, int statusCode, String message, List<Map<String, Object>> items, Map<String, Object> links, Map<String, Object> meta, Map<String, Object> additionalArgsmap) {
        if (requestID == ServiceAPIConstant.REQUEST_ID_EXPERTISE_INDEX) {
            if (isSuccess && statusCode == 200) {
                Log.i("testYJ", items.toString());
                List<ExpertiseEntity> expertiseList = EntityUtils.getExpertiseEntityList(items);
                if (expertiseList != null) {
                    expertises = expertiseList;
                }

                showExpertiseList();
            }
        }
    }

    private void showExpertiseList() {
        ListView expertises_lv = findView(R.id.lv_expertise);
        if (adapter == null) {
            adapter = new StringAdapter<ExpertiseEntity>(ExpertiseActivity.this, expertises);
            expertises_lv.setAdapter(adapter);

            expertises_lv.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    int clickPosition = position - ((ListView) parent).getHeaderViewsCount();
                    Intent intent = new Intent(ExpertiseActivity.this, HomeActivity.class);
                    intent.putExtra(getString(R.string.current_experty), expertises.get(clickPosition).getId());
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                }

            });
        } else {
            adapter.notifyDataSetChanged();
        }
        
        findView(R.id.xlistview_main_loading_container_rl).setVisibility(View.GONE);
        
        if (expertises != null && expertises.size() > 0) {
            expertises_lv.setVisibility(View.VISIBLE);
        } else {
            findView(R.id.xlistview_main_no_record_tv).setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
