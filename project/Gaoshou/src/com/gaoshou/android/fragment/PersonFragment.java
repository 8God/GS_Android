package com.gaoshou.android.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.gaoshou.android.R;
import com.gaoshou.android.activity.AboutActivity;
import com.gaoshou.android.activity.LegalSafeguardActivity;
import com.gaoshou.android.activity.LogonActivity;
import com.gaoshou.android.activity.MyCasesActivity;
import com.gaoshou.android.activity.MyExpertsActivity;
import com.gaoshou.android.activity.MyRepinesActivity;
import com.gaoshou.android.activity.UpdatePasswordActivity;
import com.gaoshou.android.activity.UserSettingActivity;
import com.gaoshou.android.entity.DoctorEntity;
import com.gaoshou.common.base.BaseFragment;
import com.gaoshou.common.base.GsApplication;
import com.gaoshou.common.base.SimpleWebViewActivity;
import com.gaoshou.common.component.BasicDialog;

public class PersonFragment extends BaseFragment implements OnClickListener {
    public static final String ACTION_REFRESH_USER_DATA = "ACTION_REFRESH_USER_DATA";

    private View contentView;

    private DoctorEntity myself;

    private RefreshUserDataReceiver reFreshUserDataReceiver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_REFRESH_USER_DATA);
        reFreshUserDataReceiver = new RefreshUserDataReceiver();
        getActivity().registerReceiver(reFreshUserDataReceiver, filter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        contentView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_person, null);

        findView(contentView, R.id.rl_first_part_container).setOnClickListener(this);
        findView(contentView, R.id.ll_update_pwd).setOnClickListener(this);
        findView(contentView, R.id.ll_my_case).setOnClickListener(this);
        findView(contentView, R.id.ll_my_expert).setOnClickListener(this);
        findView(contentView, R.id.ll_my_complaint).setOnClickListener(this);
        findView(contentView, R.id.ll_legal_safeguard).setOnClickListener(this);
        findView(contentView, R.id.ll_service_items).setOnClickListener(this);
        findView(contentView, R.id.ll_about).setOnClickListener(this);
        findView(contentView, R.id.btn_logout).setOnClickListener(this);

        initUserUI();

        return contentView;
    }

    private void initUserUI() {
        //初始化用户信息
        myself = GsApplication.getInstance(getActivity()).getMyself();
        if (myself != null) {
            Log.i("testYJ", "headPhoto=" + myself.getHeadPicPath());
            setImageView(contentView, R.id.expert_icon_iv, myself.getHeadPicPath(), R.drawable.common_icon_default_user_head);
            setTextView(contentView, R.id.tv_name, myself.getName());
            setTextView(contentView, R.id.tv_position, myself.getPosition());
            setTextView(contentView, R.id.tv_address, myself.getAddress());

        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        Intent intent = null;
        switch (id) {
            case R.id.rl_first_part_container:
                intent = new Intent(getActivity(), UserSettingActivity.class);
                break;
            case R.id.ll_update_pwd:
                intent = new Intent(getActivity(), UpdatePasswordActivity.class);
                break;
            case R.id.ll_my_case:
                intent = new Intent(getActivity(), MyCasesActivity.class);
                break;
            case R.id.ll_my_expert:
                intent = new Intent(getActivity(), MyExpertsActivity.class);
                break;
            case R.id.ll_my_complaint:
                intent = new Intent(getActivity(), MyRepinesActivity.class);
                break;
            case R.id.ll_legal_safeguard:
                intent = new Intent(getActivity(), LegalSafeguardActivity.class);
                break;
            case R.id.ll_service_items:
                intent = new Intent(getActivity(), SimpleWebViewActivity.class);
                intent.putExtra(SimpleWebViewActivity.WEB_URL_KEY, "");
                intent.putExtra(SimpleWebViewActivity.ACTION_BAR_TITLE_KEY, getString(R.string.service_items));
                break;
            case R.id.ll_about:
                intent = new Intent(getActivity(), AboutActivity.class);
                break;
            case R.id.btn_logout:
                new BasicDialog.Builder(getActivity()).setMessage(getString(R.string.logout)).setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //清除用户数据
                        GsApplication.getInstance(getActivity()).uesrLogout();
                        dialog.dismiss();
                        Intent intent = new Intent(getActivity(), LogonActivity.class);
                        startActivity(intent);
                        //                        getActivity().finish();
                    }
                }).setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create().show();
                break;
        }
        if (intent != null)
            getActivity().startActivity(intent);
    }

    @Override
    public void onDestroy() {
        getActivity().unregisterReceiver(reFreshUserDataReceiver);
        super.onDestroy();
    }

    class RefreshUserDataReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.i("testYJ","action="+action);
            if (action != null) {
                if (ACTION_REFRESH_USER_DATA.equals(action)) {
                    Log.i("testYJ","initUI=");
                    initUserUI();
                }
            }
        }

    }

}
