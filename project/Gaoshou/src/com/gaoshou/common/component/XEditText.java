package com.gaoshou.common.component;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

public class XEditText extends EditText {

    private DrawableLeftListener mLeftListener;
    private DrawableRightListener mRightListener;

    final int DRAWABLE_LEFT = 0;
    final int DRAWABLE_TOP = 1;
    final int DRAWABLE_RIGHT = 2;
    final int DRAWABLE_BOTTOM = 3;

    @SuppressLint("NewApi")
    public XEditText(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public XEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public XEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public XEditText(Context context) {
        super(context);
    }

    public void setDrawableLeftListener(DrawableLeftListener listener) {
        this.mLeftListener = listener;
    }

    public void setDrawableRightListener(DrawableRightListener listener) {
        this.mRightListener = listener;
    }

    public interface DrawableLeftListener {
        public void onDrawableLeftClick(View view);
    }

    public interface DrawableRightListener {
        public void onDrawableRightClick(View view);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
            if (mRightListener != null) {
                Drawable drawableRight = getCompoundDrawables()[DRAWABLE_RIGHT];
                int[] location = new int[2];
                getLocationOnScreen(location);
                int right =  location[0] + getMeasuredWidth() - getPaddingRight();
                if (drawableRight != null && event.getRawX() <= right && event.getRawX() >= (right - drawableRight.getBounds().width())) {
                    mRightListener.onDrawableRightClick(this);
                    return true;
                }
            }

            if (mLeftListener != null) {
                Drawable drawableLeft = getCompoundDrawables()[DRAWABLE_LEFT];
                int[] location = new int[2];
                getLocationOnScreen(location);
                int left = location[0] + getPaddingLeft();
                if (drawableLeft != null && event.getRawX() >= left && event.getRawX() <= (left + drawableLeft.getBounds().width())) {
                    mLeftListener.onDrawableLeftClick(this);
                    return true;
                }
            }
            break;
        }

        return super.onTouchEvent(event);
    }
}
