package com.gaoshou.common.base;

import com.gaoshou.android.R;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

public class SimpleWebViewActivity extends BaseActivity {

    public static final String WEB_URL_KEY = "WEB_URL_KEY";

    public static final String ACTION_BAR_TITLE_KEY = "ACTION_BAR_TITLE_KEY";

    private WebView simple_web_view_browser_webview;

    private boolean isHistoryClearable = false;

    private String webURL;
    private String actionBarTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.simple_web_view_activity);

        Intent intent = getIntent();
        if (null != intent) {
            this.webURL = intent.getStringExtra(WEB_URL_KEY);
            this.actionBarTitle = intent.getStringExtra(ACTION_BAR_TITLE_KEY);
        }

        if (!TextUtils.isEmpty(actionBarTitle)) {
            initToolbar(actionBarTitle);
        } else {
            initToolbar(getString(R.string.app_name));
        }

        simple_web_view_browser_webview = (WebView) findViewById(R.id.simple_web_view_browser_webview);
        final ProgressBar simple_web_view_browser_progress_bar_pb = (ProgressBar) findViewById(R.id.simple_web_view_browser_progress_bar_pb);

        WebSettings webSettings = simple_web_view_browser_webview.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setSupportZoom(true);
        simple_web_view_browser_webview.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                // TODO Auto-generated method stub
                if (isHistoryClearable) {
                    isHistoryClearable = false;
                    view.clearHistory();
                }
                super.onPageFinished(view, url);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                // TODO Auto-generated method stub
                super.onPageStarted(view, url, favicon);

            }

        });

        simple_web_view_browser_webview.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                // TODO Auto-generated method stub
                if (newProgress == 100) {
                    simple_web_view_browser_progress_bar_pb.setVisibility(View.GONE);
                } else {
                    if (simple_web_view_browser_progress_bar_pb.getVisibility() == View.GONE) {
                        simple_web_view_browser_progress_bar_pb.setVisibility(View.VISIBLE);
                    }
                    simple_web_view_browser_progress_bar_pb.setProgress(newProgress);
                }
                super.onProgressChanged(view, newProgress);
            }
        });

        //        simple_web_view_browser_webview.loadUrl(DEFAULT_ACCOUNT_SERVICE_INTRO_URL);

    }

    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();

        if (!TextUtils.isEmpty(webURL)) {
            simple_web_view_browser_webview.loadUrl(webURL);
            isHistoryClearable = true;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;

            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        if (simple_web_view_browser_webview.canGoBack()) {
            simple_web_view_browser_webview.goBack();
        } else {
            simple_web_view_browser_webview.clearHistory();
            simple_web_view_browser_webview.clearFormData();
            simple_web_view_browser_webview.clearCache(true);
            //            deleteDatabase("webview.db");
            //            deleteDatabase("webviewCache.db");
            super.onBackPressed();
        }
    }
}
