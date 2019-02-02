package com.gaoshou.android.activity;

import android.os.Bundle;
import android.view.MenuItem;

import com.gaoshou.android.R;
import com.gaoshou.android.fragment.SelectSymptomFragment;
import com.gaoshou.common.base.BaseActivity;

public class SelectSymptomActivity extends BaseActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_symptom);
        
        initToolbar(getString(R.string.actionbar_title_symptom));
        
        initFragment();
    }

    private void initFragment() {
        SelectSymptomFragment symptomFragment  = new SelectSymptomFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.fl_container, symptomFragment).commit();
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
