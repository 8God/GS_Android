package com.gaoshou.android.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.gaoshou.android.R;
import com.gaoshou.android.entity.OrderEntity;
import com.gaoshou.android.fragment.DoctorCasesFragment;
import com.gaoshou.android.fragment.ExpertCasesFragment;
import com.gaoshou.common.base.BaseActivity;
import com.gaoshou.common.base.BaseFragment;
import com.gaoshou.common.base.GsApplication;
import com.gaoshou.common.constant.CommonConstant;

public class MyCasesActivity extends BaseActivity implements OnClickListener {
    public static final int TO_CANCEL_ORDER_ACTIVITY = 1;
    public static final int TO_CASE_DETAIL_ACTIVITY = 2;

    public static final int LOCATION_CANCEL = 10;
    public static final int LOCATION_EVALUATE = 11;
    public static final int LOCATION_REPINED = 12;
    
    public static final int UNFINISHED_STATUS = OrderEntity.STATUS_PAYMENT;
    public static final int FINISHED_STATUS = OrderEntity.STATUS_COMPLETE;
    private int currentItem;

    private int userType;

    private List<BaseFragment> fragmentList;
    private List<TextView> textViewList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_cases);

        initToolbar(getString(R.string.actionbar_title_my_cases));

        init();
    }

    private void init() {
        userType = GsApplication.getInstance(getContext()).getLogonType();

        findView(R.id.tv_unfinished).setOnClickListener(this);
        findView(R.id.tv_finished).setOnClickListener(this);
        findView(R.id.tv_unfinished).setEnabled(false);

        textViewList = new ArrayList<TextView>();
        textViewList.add((TextView) findView(R.id.tv_unfinished));
        textViewList.add((TextView) findView(R.id.tv_finished));

        initFragment();
    }

    private void initFragment() {
        fragmentList = new ArrayList<BaseFragment>();
        BaseFragment unfinishedFragment = null;
        BaseFragment finishedFragment = null;
        if (userType == CommonConstant.LOGON_TYPE_DOCTOR) {
            unfinishedFragment = new DoctorCasesFragment(UNFINISHED_STATUS);
            finishedFragment = new DoctorCasesFragment(FINISHED_STATUS);
        } else if (userType == CommonConstant.LOGON_TYPE_EXPERT) {
            unfinishedFragment = new ExpertCasesFragment(UNFINISHED_STATUS);
            finishedFragment = new ExpertCasesFragment(FINISHED_STATUS);
        }
        fragmentList.add(unfinishedFragment);
        fragmentList.add(finishedFragment);

        getSupportFragmentManager().beginTransaction().replace(R.id.fl_container, unfinishedFragment).commit();

        currentItem = 0;
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        switch (viewId) {
            case R.id.tv_unfinished:
                changeFragment(0);
                break;
            case R.id.tv_finished:
                changeFragment(1);
                break;
        }
    }

    private void changeFragment(int index) {
        if (fragmentList != null) {
            BaseFragment fragment = fragmentList.get(index);
            getSupportFragmentManager().beginTransaction().replace(R.id.fl_container, fragment).commit();
        }

        if (textViewList != null) {
            textViewList.get(index).setEnabled(false);
            textViewList.get(currentItem).setEnabled(true);
            textViewList.get(currentItem).setPressed(true);
            ;
        }

        currentItem = index;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            BaseFragment currentFragment = fragmentList.get(currentItem);
            if (userType == CommonConstant.LOGON_TYPE_DOCTOR) {
                Log.i("testYJ","ActivityResult=Doctor");
                ((DoctorCasesFragment) currentFragment).myActivityResult(requestCode, data);
            } else if (userType == CommonConstant.LOGON_TYPE_EXPERT) {
                Log.i("testYJ","ActivityResult=Expert");
                ((ExpertCasesFragment) currentFragment).myActivityResult(requestCode, data);
            }
        }
    }

    @Override
    protected void onDestroy() {
        GsApplication.getInstance(getContext()).setUnfinishedOrderList(null);
        GsApplication.getInstance(getContext()).setFinishedOrderList(null);

        GsApplication.getInstance(getContext()).setUnfinishedDoctorOrderList(null);
        GsApplication.getInstance(getContext()).setUnfinishedExpertOrderList(null);
        GsApplication.getInstance(getContext()).setFinishedDoctorOrderList(null);
        GsApplication.getInstance(getContext()).setFinishedExpertOrderList(null);
        super.onDestroy();
    }
}
