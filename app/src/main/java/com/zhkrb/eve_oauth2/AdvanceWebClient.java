package com.zhkrb.eve_oauth2;

import android.app.Notification;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;

import com.zhkrb.eve_oauth2.util.RegexUtil;

import java.net.CookieHandler;
import java.net.CookiePolicy;
import java.net.HttpCookie;
import java.util.ArrayList;
import java.util.List;

public class AdvanceWebClient extends WebViewClient {

    private final Context mContext;
    private final WebView mWebView;
    private ProgressBar mProgressBar;
    private WebSettings mWebSettings;
    private LoginSuccessListener mListener;
    private String mUrl;
    private static final String APP_CACAHE_DIRNAME = "/webcache";
    private static final String User_agent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/68.0.3440.106 Safari/537.36";

    public AdvanceWebClient(Context context, WebView webView) {
        mContext = context;
        mWebView = webView;
    }

    public void initWebView(String url) {
        mUrl = url;
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    mProgressBar.setVisibility(View.GONE);
                } else {
                    mProgressBar.setProgress(newProgress);
                }
            }
        });

        mWebSettings = mWebView.getSettings();
        mWebSettings.setJavaScriptEnabled(true);
        mWebSettings.setUseWideViewPort(true);
        mWebSettings.setLoadWithOverviewMode(true);
        mWebSettings.setUserAgentString(User_agent);
        mWebSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        mWebSettings.setCacheMode(WebSettings.LOAD_DEFAULT);  //设置 缓存模式
        // 开启 DOM storage API 功能
        mWebSettings.setDomStorageEnabled(true);
        //开启 database storage API 功能
        mWebSettings.setDatabaseEnabled(true);
        String cacheDirPath = mContext.getFilesDir().getAbsolutePath()+APP_CACAHE_DIRNAME;
        //      String cacheDirPath = getCacheDir().getAbsolutePath()+Constant.APP_DB_DIRNAME;
        Log.e("WebView","cacheDirPath="+cacheDirPath);
        //设置数据库缓存路径
        mWebSettings.setDatabasePath(cacheDirPath);
        //设置  Application Caches 缓存目录
        mWebSettings.setAppCachePath(cacheDirPath);
        //开启 Application Caches 功能
        mWebSettings.setAppCacheEnabled(true);
        Log.e("WebView","H5--->" + url);
        mWebView.setWebViewClient(this);

        CookieManager manager = CookieManager.getInstance();
        manager.removeAllCookies(null);

        mWebView.loadUrl(mUrl);
    }

    @Nullable
    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
        Log.e("webView",request.getMethod());
        Log.e("webView", String.valueOf(request.getUrl()));
        return super.shouldInterceptRequest(view, request);
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        String url = request.getUrl().toString();
        if (url.contains("oauth2-redirect.html#access_token")){
            if (mListener != null){
                List<String> list = RegexUtil.regex(url,"oauth2-redirect.html#access_token=(.+?)&");
                if (list == null || list.size() == 0){
                    mListener.onFail();
                }else {
                    String token = list.get(0).
                            replace("oauth2-redirect.html#access_token=","");
                    mListener.onSuccess(token.substring(0,token.length()-1));
                }
            }
        }
        return super.shouldOverrideUrlLoading(view, request);
    }

    public void setProgressBar(ProgressBar progressBar) {
        mProgressBar = progressBar;
    }

    public void setListener(LoginSuccessListener listener) {
        mListener = listener;
    }


    public interface LoginSuccessListener{
        void onSuccess(String token);
        void onFail();
    }
}
