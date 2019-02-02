package com.gaoshou.android.activity;

import android.os.Bundle;
import android.view.MenuItem;

import com.gaoshou.android.R;
import com.gaoshou.android.fragment.DoctorRepinesFragment;
import com.gaoshou.android.fragment.ExpertRepinesFragment;
import com.gaoshou.common.base.BaseActivity;
import com.gaoshou.common.base.BaseFragment;
import com.gaoshou.common.base.GsApplication;
import com.gaoshou.common.constant.CommonConstant;

public class MyRepinesActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_repines);

        initToolbar(getString(R.string.actionbar_title_my_repines));

        initFragment();
    }

    private void initFragment() {
        int logonType = GsApplication.getInstance().getLogonType();
        BaseFragment currentFragment = null;
        switch (logonType) {
            case CommonConstant.LOGON_TYPE_DOCTOR:
                currentFragment = new DoctorRepinesFragment();
                break;
            case CommonConstant.LOGON_TYPE_EXPERT:
                currentFragment = new ExpertRepinesFragment();
                break;
        }
        if(currentFragment!=null){
            getSupportFragmentManager().beginTransaction().replace(R.id.fl_container,currentFragment).commit();
        }
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
    protected void onDestroy() {
        GsApplication.getInstance().setRepineList(null);
        GsApplication.getInstance().setRepinedList(null);
        super.onDestroy();
    }
}
