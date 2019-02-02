package com.gaoshou.common.widget;

import com.gaoshou.android.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

public class CountDownButton extends Button {

    private OnCountDownListener onCountDownListener;

    private static final String BRACKETS_LEFT = "(";
    private static final String BRACKETS_RIGHT = ")";

    private int countDownTextColor;

    private String text;
    private String CountDownText = "重新发送";

    private long millisInFuture = 60000;
    private long countDownInterval = 1000;
    private CountDownTimer countDownTimer;

    public CountDownButton(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        initCountDownTextColor(context, attrs);
        saveText();

        initCountDownTask();
    }

    private void initCountDownTextColor(Context context, AttributeSet attrs) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.CountDownButton);
        int textColor = 0;
        try {
            countDownTextColor = array.getColor(R.styleable.CountDownButton_countDouwn_textColor, R.color.disabled_text_color);
            textColor = getTextColors().getDefaultColor();
        } finally {
            array.recycle();
        }
        int[][] states = new int[2][];
        states[0] = new int[] { android.R.attr.state_enabled };
        states[1] = new int[] {};
        int[] colors = new int[] { textColor,countDownTextColor };

        ColorStateList colorStateList = new ColorStateList(states, colors);

        setTextColor(colorStateList);
    }

    private void saveText() {
        text = getText().toString();
    }

    private void initCountDownTask() {
        countDownTimer = new CountDownTimer(millisInFuture, countDownInterval) {

            @Override
            public void onTick(long millisUntilFinished) {
                long secondUntilFinished = millisUntilFinished / countDownInterval;
                setText(CountDownText + BRACKETS_LEFT + secondUntilFinished + BRACKETS_RIGHT);
            }

            @Override
            public void onFinish() {
                finishCountDown();
            }
        };
    }

    public final void startCountDown() {
        setEnabled(false);
        countDownTimer.start();

        if (null != onCountDownListener) {
            onCountDownListener.onStartCountDown();
        }
    }

    private void finishCountDown() {
        setEnabled(true);
        setText(text);

        if (null != onCountDownListener) {
            onCountDownListener.onFinishCountDown();
        }
    }

    public String getCountDownText() {
        return CountDownText;
    }

    public void setCountDownText(String countDownText) {
        CountDownText = countDownText;
    }

    public void setCountDownParams(long millisInFuture, long countDownInterval) {
        this.millisInFuture = millisInFuture;
        this.countDownInterval = countDownInterval;

        initCountDownTask();
    }

    public void setOnCountDownListener(OnCountDownListener onCountDownListener) {
        this.onCountDownListener = onCountDownListener;
    }

    public interface OnCountDownListener {
        void onStartCountDown();

        void onFinishCountDown();
    }
}
