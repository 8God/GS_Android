package com.gaoshou.android.activity;

import android.os.Bundle;
import android.view.MenuItem;

import com.gaoshou.android.R;
import com.gaoshou.android.fragment.SelectAnamnesisFragment;
import com.gaoshou.common.base.BaseActivity;

public class SelectAnamnesisActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_anamnesis);
        
        initToolbar(getString(R.string.actionbar_title_anamnesis));
        
        initFragment();
    }

    private void initFragment() {
        SelectAnamnesisFragment anamnesisFragment  = new SelectAnamnesisFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.fl_container, anamnesisFragment).commit();
    }
    
}
