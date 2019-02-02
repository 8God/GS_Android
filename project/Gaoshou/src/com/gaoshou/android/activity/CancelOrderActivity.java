package com.gaoshou.android.activity;

import java.util.Map;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.gaoshou.android.R;
import com.gaoshou.android.entity.OrderEntity;
import com.gaoshou.common.base.BaseActivity;
import com.gaoshou.common.base.GsApplication;
import com.gaoshou.common.constant.APIKey;
import com.gaoshou.common.constant.ServiceAPIConstant;
import com.gaoshou.common.network.CommonRequest;
import com.gaoshou.common.utils.NetworkUtil;
import com.gaoshou.common.utils.Validator;

public class CancelOrderActivity extends BaseActivity {

    private EditText reason_et;
    private MenuItem submit;

    private OrderEntity order;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cancel_order);

        initToolbar(getString(R.string.actionbar_title_cancel_order));

        init();
    }

    private void init() {
        order = GsApplication.getInstance(getContext()).getOrder();

        initView();
    }

    private void initView() {
        reason_et = findView(R.id.edt_reason);
        reason_et.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (Validator.isEmpty(s.toString())) {
                    if (submit != null) {
                        submit.setVisible(false);
                    }
                } else {
                    if (submit != null) {
                        submit.setVisible(true);
                    }
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_cancel_order, menu);
        submit = menu.findItem(R.id.item_submit);
        submit.setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case R.id.item_submit:
                cancelOrder();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void cancelOrder() {
        if (NetworkUtil.isNetworkAvaliable(getContext())) {
            if (order != null) {
                showProgressDialog(getString(R.string.cancel_submitting));

                String reason = getEditTextInput(R.id.edt_reason, "");

                CommonRequest updateOrderStatusRequest = new CommonRequest();
                updateOrderStatusRequest.setRequestApiName(ServiceAPIConstant.REQUEST_API_NAME_ORDER_UPDATE);
                updateOrderStatusRequest.setRequestID(ServiceAPIConstant.REQUEST_ID_ORDER_UPDATE);
                updateOrderStatusRequest.setRequestMethod(CommonRequest.REQUEST_METHOD_PUT);
                if (order != null) {
                    updateOrderStatusRequest.addRequestParam(APIKey.COMMON_ID, order.getId());
                    if (order.getStatus() == OrderEntity.STATUS_PAYMENT) {
                        updateOrderStatusRequest.addRequestParam(APIKey.COMMON_STATUS, OrderEntity.STATUS_CANCEL);
                    } else {
                        updateOrderStatusRequest.addRequestParam(APIKey.COMMON_STATUS, OrderEntity.STATUS_PRE_PAYMENT_CANCEL);
                    }
                }

                addRequestAsyncTask(updateOrderStatusRequest);
            } else {
                showToast(getString(R.string.faild_to_submit));
            }
        } else {
            showToast(getString(R.string.network_error));
        }
    }

    @Override
    protected void onResponse(String requestID, boolean isSuccess, int statusCode, String message, Map<String, Object> data, Map<String, Object> additionalArgsmap) {
        if (requestID == ServiceAPIConstant.REQUEST_ID_ORDER_UPDATE) {
            if (isSuccess && statusCode == 200) {
                if (data != null) {
                    showToast(getString(R.string.success_to_submit));
                    Intent intent = new Intent(getContext(), MyCasesActivity.class);
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                }
            } else {
                showToast(message);
            }
        }
        dimissProgressDialog();

    }
}
