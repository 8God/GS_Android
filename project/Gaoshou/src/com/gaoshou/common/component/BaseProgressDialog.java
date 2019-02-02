package com.gaoshou.common.component;

import com.gaoshou.android.R;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

public class BaseProgressDialog extends Dialog {

    private View layout;

    public BaseProgressDialog(Context context) {
        super(context);
        initView();
    }

    public BaseProgressDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        initView();
    }

    private void initView() {
        layout = LayoutInflater.from(getContext()).inflate(R.layout.dialog_base_progress, null);

        Window window = getWindow();

        window.requestFeature(Window.FEATURE_NO_TITLE);
        window.setBackgroundDrawableResource(android.R.color.transparent);

        WindowManager.LayoutParams params = window.getAttributes();
        params.gravity = Gravity.CENTER;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(params);

        setContentView(layout, params);
    }

    public void setMessage(String message) {
        TextView tipsTv = (TextView) layout.findViewById(R.id.tv_base_progress_message);
        if (null != tipsTv && !TextUtils.isEmpty(message)) {
            tipsTv.setText(message);
            tipsTv.setVisibility(View.VISIBLE);
        } else {
            tipsTv.setVisibility(View.GONE);
        }
    }

}
