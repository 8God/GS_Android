package com.gaoshou.common.component;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.gaoshou.android.R;
import com.gaoshou.common.base.BaseFragment;
import com.gaoshou.common.base.BasePager;
import com.gaoshou.common.base.SimpleWebViewActivity;

public abstract class AutoScrollPagerFragment extends BaseFragment {
    protected int SCROLL_DELAY = 6000;
    protected int DOT_MARGIN = 12;

    protected int currentPagerPosition = 0;
    protected int updateIndex = 0;
    protected List<ScrollPager> scrollPagerList;
    protected ImageView[] dotArray;

    protected View contentView;
    protected ViewPager bbsBannerVp;

    protected Handler updateHandler = new Handler();
    private Runnable updateRunnable = new Runnable() {

        @Override
        public void run() {
            bbsBannerVp.setCurrentItem(++updateIndex, true);

            updateHandler.postDelayed(updateRunnable, SCROLL_DELAY);

        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        contentView = inflater.inflate(R.layout.fragment_auto_scroll_pager, null);

        return contentView;
    }

    @Override
    public final void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        init();
    }

    protected void init() {
        getSrcollPagerList();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (null != scrollPagerList && scrollPagerList.size() > 1) {
            updateHandler.removeCallbacks(updateRunnable);
            updateHandler.postDelayed(updateRunnable, SCROLL_DELAY);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i("cth", "AutoScrollPagerFragment : onPause");
        if (null != scrollPagerList && scrollPagerList.size() > 1) {
            updateHandler.removeCallbacks(updateRunnable);
        }
    }

    @SuppressLint("NewApi")
    protected void initDotView() {
        if (scrollPagerList != null && scrollPagerList.size() > 0) {
            LinearLayout dot_ll = findView(contentView, R.id.ll_banner_dot);

            dotArray = new ImageView[scrollPagerList.size()];

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(DOT_MARGIN, DOT_MARGIN, DOT_MARGIN, DOT_MARGIN);

            for (int i = 0; i < scrollPagerList.size(); i++) {
                ImageView dotView = new ImageView(getActivity());
                dotView.setBackground(getResources().getDrawable(R.drawable.selector_dot_scroll_indicator));
                dotView.setLayoutParams(params);
                dotArray[i] = dotView;
                dotView.setEnabled(false);

                dot_ll.addView(dotView, i);
            }

            dotArray[0].setEnabled(true);
        }
    }

    protected void initBannerPager() {
        if (scrollPagerList != null && scrollPagerList.size() > 0) {
            bbsBannerVp = findView(contentView, R.id.vp_bbs_banner);
            ScrollPagerAdapter bbsBannerAdapter = new ScrollPagerAdapter(getActivity(), scrollPagerList);
            bbsBannerVp.setAdapter(bbsBannerAdapter);
            bbsBannerVp.setOnPageChangeListener(new OnPageChangeListener() {

                @Override
                public void onPageSelected(int selectedPosition) {
                    updateIndex = selectedPosition;
                    selectedPosition %= scrollPagerList.size();
                    changeDotState(currentPagerPosition, selectedPosition);

                    currentPagerPosition = selectedPosition;
                }

                @Override
                public void onPageScrolled(int arg0, float arg1, int arg2) {

                }

                @Override
                public void onPageScrollStateChanged(int arg0) {

                }
            });

            initDotView();
        }
    }

    private void changeDotState(int currentPagerPosition, int selectedPosition) {
        if (null != dotArray && currentPagerPosition < dotArray.length && selectedPosition < dotArray.length) {
            dotArray[currentPagerPosition].setEnabled(false);
            dotArray[selectedPosition].setEnabled(true);
        }
    }

    public abstract void getSrcollPagerList();

    protected void onFinishLoadScrollPagerList() {

        initBannerPager();
        dimissLoadingProgressBar();

        if (null != scrollPagerList && scrollPagerList.size() > 1) {
            updateHandler.removeCallbacks(updateRunnable);
            updateHandler.postDelayed(updateRunnable, SCROLL_DELAY);
        }
    }

    public void showLoadingProgressBar() {
        ProgressBar loadingPb = findView(contentView, R.id.pb_loading_pagerlist);
        loadingPb.setVisibility(View.VISIBLE);
    }

    public void dimissLoadingProgressBar() {
        ProgressBar loadingPb = findView(contentView, R.id.pb_loading_pagerlist);
        loadingPb.setVisibility(View.GONE);
    }

    /**
     * Scroll pager view
     * 
     * @author CTH
     *
     */
    public class ScrollPager extends BasePager implements OnClickListener {

        private ScrollPagerEntity bannerEntity;

        public ScrollPager(Context context, ScrollPagerEntity bannerEntity) {
            super(context);
            this.bannerEntity = bannerEntity;

            setDefaultImage(R.drawable.loading_bg);

            initLayout();
        }

        private void initLayout() {
            View layout = LayoutInflater.from(getContext()).inflate(R.layout.pager_bbs_banner, null);

            setImageView(layout, R.id.imv_banner_bg, bannerEntity.getBannerBgUrl());
            setViewClickListener(layout, R.id.imv_banner_bg, this);

            addView(layout);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.imv_banner_bg:
                    if (null != bannerEntity) {
                        if (!TextUtils.isEmpty(bannerEntity.getContentUrl())) {
                            Intent openContent = new Intent(getContext(), SimpleWebViewActivity.class);
                            openContent.putExtra(SimpleWebViewActivity.WEB_URL_KEY, bannerEntity.getContentUrl());
                            openContent.putExtra(SimpleWebViewActivity.ACTION_BAR_TITLE_KEY, bannerEntity.getBannerTitle());
                            getContext().startActivity(openContent);
                        }
                    }
                    break;

                default:
                    break;
            }
        }

    }

    public class ScrollPagerAdapter extends PagerAdapter {

        private Context context;
        private List<ScrollPager> pagers;

        public ScrollPagerAdapter(Context context, List<ScrollPager> pagers) {
            super();
            this.context = context;
            this.pagers = pagers;
        }

        @Override
        public int getCount() {
            return Integer.MAX_VALUE;
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            int index = position % pagers.size();
            View pager = pagers.get(index);
            if (null != pager) {
                if (null != pager.getParent()) {
                    ViewGroup parentView = (ViewGroup) pager.getParent();
                    parentView.removeView(pager);
                }
                ((ViewPager) container).addView(pager);

                return pager;
            }
            return super.instantiateItem(container, position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            //deal by instantiateItem method
        }

    }
}
