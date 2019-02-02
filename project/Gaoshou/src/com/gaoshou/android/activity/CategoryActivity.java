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
import com.gaoshou.android.entity.CategoryEntity;
import com.gaoshou.common.base.BaseActivity;
import com.gaoshou.common.constant.ServiceAPIConstant;
import com.gaoshou.common.network.CommonRequest;
import com.gaoshou.common.utils.EntityUtils;
import com.gaoshou.common.utils.NetworkUtil;

public class CategoryActivity extends BaseActivity {
    private List<CategoryEntity> categorys;
    private StringAdapter<CategoryEntity> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sort);

        initToolbar(getString(R.string.actionbar_title_category));

        init();
    }

    private void init() {
        fetchExpertiseList();
    }

    private void fetchExpertiseList() {
        if (NetworkUtil.isNetworkAvaliable(CategoryActivity.this)) {
            CommonRequest fetchExpertiseRequest = new CommonRequest();
            fetchExpertiseRequest.setRequestApiName(ServiceAPIConstant.REQUEST_API_NAME_CATEGORY_INDEX);
            fetchExpertiseRequest.setRequestID(ServiceAPIConstant.REQUEST_ID_CATEGORY_INDEX);
            addRequestAsyncTask(fetchExpertiseRequest);
        } else {
            showToast(getString(R.string.network_error));
        }
    }

    @Override
    protected void onResponse(String requestID, boolean isSuccess, int statusCode, String message, List<Map<String, Object>> items, Map<String, Object> links, Map<String, Object> meta, Map<String, Object> additionalArgsmap) {
        if (requestID == ServiceAPIConstant.REQUEST_ID_CATEGORY_INDEX) {
            if (isSuccess && statusCode == 200) {
                Log.i("testYJ", items.toString());
                List<CategoryEntity> categorysList = EntityUtils.getCategoryEntityList(items);
                if (categorysList != null) {
                    Log.i("testYJ", categorysList.get(0).getName());
                    categorys = categorysList;
                }

                showExpertiseList();
            }
        }
    }

    private void showExpertiseList() {
        ListView categorys_lv = findView(R.id.lv_category);
        if (adapter == null) {
            adapter = new StringAdapter<CategoryEntity>(CategoryActivity.this, categorys);
            categorys_lv.setAdapter(adapter);

            categorys_lv.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    int clickPosition = position - ((ListView) parent).getHeaderViewsCount();
                    Intent intent = new Intent(CategoryActivity.this, HomeActivity.class);
                    intent.putExtra(getString(R.string.current_sort), categorys.get(clickPosition).getId());
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                }

            });
        } else {
            adapter.notifyDataSetChanged();
        }

        findView(R.id.xlistview_main_loading_container_rl).setVisibility(View.GONE);

        if (categorys != null && categorys.size() > 0) {
            categorys_lv.setVisibility(View.VISIBLE);
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
