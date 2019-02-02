package com.gaoshou.android.activity;

import android.os.Bundle;
import android.view.MenuItem;

import com.gaoshou.android.R;
import com.gaoshou.common.base.BaseActivity;

public class LegalSafeguardActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_legal_safeguard);

        initToolbar(getString(R.string.actionbar_legal_safeguard));
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
}
