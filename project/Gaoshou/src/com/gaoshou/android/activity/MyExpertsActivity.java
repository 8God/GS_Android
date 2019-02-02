package com.gaoshou.android.activity;

import android.os.Bundle;
import android.view.MenuItem;

import com.gaoshou.android.R;
import com.gaoshou.android.fragment.MyExpertsFragment;
import com.gaoshou.common.base.BaseActivity;
import com.gaoshou.common.base.GsApplication;

public class MyExpertsActivity extends BaseActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_experts);
        
        initToolbar(getString(R.string.actionbar_title_my_experts));
        
        initFragment();
    }

    private void initFragment() {
        MyExpertsFragment myExpertsFragment = new MyExpertsFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.fl_container, myExpertsFragment).commit();
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch(itemId){
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    
}
