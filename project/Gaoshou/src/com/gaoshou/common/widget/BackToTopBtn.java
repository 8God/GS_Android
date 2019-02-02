package com.gaoshou.common.widget;

import java.util.ArrayList;
import java.util.List;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ListView;

import com.gaoshou.android.R;


public class BackToTopBtn extends ImageButton implements OnClickListener {
    private int visiblePosition = 4;

    private boolean isShowing = false;

    private AnimatorSet showAnimSet;
    private AnimatorSet dimissAnimSet;

    private List<Animator> showAnimList;
    private List<Animator> dimissAnimList;

    private ListView listView;

    public BackToTopBtn(Context context) {
        super(context);

        init();
    }

    public BackToTopBtn(Context context, AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    public BackToTopBtn(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    @SuppressWarnings("deprecation")
    @SuppressLint("NewApi")
    private void init() {
        setBackground(getResources().getDrawable(R.drawable.common_btn_back2top));
        setOnClickListener(this);

        setAlpha(0f);
        initShowAnimatorSet();
        initDimissAnimatorSet();

//        setVisibility(GONE);
    }

    private void initShowAnimatorSet() {
        showAnimSet = new AnimatorSet();
        showAnimList = new ArrayList<Animator>();
        ObjectAnimator alpha = ObjectAnimator.ofFloat(this, "alpha", 0f, 1f);
        alpha.setDuration(800);
        showAnimList.add(alpha);

        showAnimSet.playTogether(showAnimList);
    }

    private void initDimissAnimatorSet() {
        dimissAnimSet = new AnimatorSet();
        dimissAnimList = new ArrayList<Animator>();
        ObjectAnimator alpha = ObjectAnimator.ofFloat(this, "alpha", 1f, 0f);
        alpha.setDuration(800);
        dimissAnimList.add(alpha);

        dimissAnimSet.playTogether(dimissAnimList);

    }

    public void onVisibilityChanged(int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (firstVisibleItem >= visiblePosition) {
            if (!isShowing) {
                isShowing = true;
                show();
            }
        } else {
            if (isShowing) {
                isShowing = false;
                dismiss();
            }
        }
    }

    public void show() {
        Log.i("cth","backtotop:show");
        showAnimSet.start();
        setVisibility(View.VISIBLE);
    }

    public void dismiss() {
        Log.i("cth","backtotop:dismiss");
        dimissAnimSet.start();
        setVisibility(View.GONE);
    }

    public void addShowAnimator(Animator animator) {
        showAnimList.add(animator);
    }

    public void addDimissAnimator(Animator animator) {
        dimissAnimList.add(animator);
    }

    public void bindListView(ListView listView) {
        this.listView = listView;

//        setVisibility(VISIBLE);
    }

    public int getVisiblePosition() {
        return visiblePosition;
    }

    public void setVisiblePosition(int visiblePosition) {
        this.visiblePosition = visiblePosition;
    }

    @Override
    public void onClick(View v) {
        if (null != listView) {
            listView.setSelection(4);
            //            listView.smoothScrollToPositionFromTop(0, 0);
            //            listView.setSelectionAfterHeaderView();
            listView.setSelectionFromTop(0, 0);
        }
    }

}
