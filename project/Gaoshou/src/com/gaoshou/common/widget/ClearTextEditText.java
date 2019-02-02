package com.gaoshou.common.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;

import com.gaoshou.android.R;
import com.gaoshou.common.component.XEditText;
import com.gaoshou.common.component.XEditText.DrawableRightListener;

public class ClearTextEditText extends XEditText {

    private Drawable rightDrawable;

    public ClearTextEditText(Context context, AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    private void init() {
        
        rightDrawable = getResources().getDrawable(R.drawable.log_btn_delwords_normal);
        rightDrawable.setBounds(0, 0, rightDrawable.getMinimumWidth(), rightDrawable.getMinimumHeight());

        addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s)) {
                    setDrawableVisible(true);
                } else {
                    setDrawableVisible(false);
                }
            }
        });

        setDrawableRightListener(new DrawableRightListener() {

            @Override
            public void onDrawableRightClick(View view) {
                setText(null);
            }
        });

    }

    private void setDrawableVisible(boolean isVisible) {
        if (isVisible) {
            super.setCompoundDrawables(null, null, rightDrawable, null);
        } else {
            super.setCompoundDrawables(null, null, null, null);
        }
    }

    @Override
    public void setCompoundDrawables(Drawable left, Drawable top, Drawable right, Drawable bottom) {
        //do nothing
    }

}
