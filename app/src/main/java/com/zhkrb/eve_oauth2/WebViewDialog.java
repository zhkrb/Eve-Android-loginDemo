package com.zhkrb.eve_oauth2;

import android.app.Application;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;

public class WebViewDialog extends AbsDialogFragment implements AdvanceWebClient.LoginSuccessListener {

    private LoginListener mListener;
    private WebView mWebView;
    private AdvanceWebClient mAdvanceWebClient;

    @Override
    protected void setWindowAttributes(Window window) {
        window.setWindowAnimations(R.style.bottomToTopAnim);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = (int) dp2px(300);
        params.height = (int) dp2px(500);
        params.gravity = Gravity.CENTER;
        window.setAttributes(params);
    }

    @Override
    protected boolean canCancel() {
        return false;
    }

    @Override
    protected int getDialogStyle() {
        return R.style.dialog;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_view;
    }

    @Override
    protected void main() {
        Bundle bundle = getArguments();
        mListener = (LoginListener) getActivity();
        if (mListener == null){
            this.dismiss();
            return;
        }
        if (bundle == null){
            mListener.onFail("未传递参数");
            this.dismiss();
            return;
        }
        String url = bundle.getString("url","");
        mWebView = mRootView.findViewById(R.id.webview);
        mAdvanceWebClient = new AdvanceWebClient(getContext(),mWebView);
        mAdvanceWebClient.setProgressBar(mRootView.findViewById(R.id.progress));
        mAdvanceWebClient.initWebView(url);
        mAdvanceWebClient.setListener(this);
    }

    @Override
    public void onSuccess(String token) {
        if (mListener != null){
            mListener.onSuccess(token);
        }
        this.dismiss();
    }

    @Override
    public void onFail() {
        if (mListener != null){
            mListener.onFail("获取失败");
        }
        this.dismiss();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mWebView != null){
            mWebView.destroy();
        }
    }

    public interface LoginListener{
        void onSuccess(String token);
        void onFail(String msg);
    }

    public float dp2px(float dpVal) {
        if (getActivity() == null){
            return 0;
        }
        return  TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpVal,
                getActivity().getApplicationContext().getResources().getDisplayMetrics());
    }

}
