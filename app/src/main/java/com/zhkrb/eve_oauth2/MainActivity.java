package com.zhkrb.eve_oauth2;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Base64;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zhkrb.eve_oauth2.bean.ConfigBean;
import com.zhkrb.eve_oauth2.netowrk.retrofit.HttpUtil;
import com.zhkrb.eve_oauth2.netowrk.retrofit.RetrofitCallback;
import com.zhkrb.eve_oauth2.util.TimeUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, WebViewDialog.LoginListener {

    private static final String MAIN_URL = "https://esi.evepc.163.com";
    private static final String ID = "bc90aa496a404724a93f41b4f4e97761";
    private List<String> sc = new ArrayList<>();
    private TextView logView;
    private TextView tokenView;
    private String deviceId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        logView = findViewById(R.id.log_view);
        tokenView = findViewById(R.id.text_token);
        logView.setMovementMethod(ScrollingMovementMethod.getInstance());
        tokenView.setMovementMethod(ScrollingMovementMethod.getInstance());
        HttpUtil.init(MAIN_URL);
        findViewById(R.id.btn).setOnClickListener(this);
    }

    private void setText(String str){
        logView.setText(String.format("%s%s", logView.getText(), String.format("%s\n", str)));
    }


    @Override
    protected void onDestroy() {
        HttpUtil.cancel("config");
        HttpUtil.cancel("getid");
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        getDeviceId();
    }

    RetrofitCallback configCallback = new RetrofitCallback() {
        @Override
        public void onStart() {
        }


        @Override
        public void onSuccess(int code, String msg, String info) {
            setText("config success");
            ConfigBean bean = JSONObject.parseObject(JSONObject.parseObject(info).getJSONObject("securityDefinitions")
                    .getJSONObject("evesso").toJSONString(),ConfigBean.class);
            if (bean != null){
                setText("parse ok");
                setText(ID);
                setText(bean.getAuthorizationUrl());
                Map<String,Object> array = JSON.parseObject(bean.getScopes());
                sc.addAll(array.keySet());
                setText(sc.get(0));
                String time = TimeUtil.getDateToString(new Date().getTime(),
                        "EEE MMM dd yyyy HH:mm:ss 'GMT+0800' (中国标准时间)",
                        Locale.ENGLISH);
                setText(time);
                String url = bean.getAuthorizationUrl() + "?" + "response_type=token"
                        + "&" + "client_id="+ ID
                        + "&" + "redirect_uri=https://esi.evepc.163.com/ui/oauth2-redirect.html"
                        + "&" + "scope=" + sc.get(0)
                        + "&" + "state=" + Base64.encodeToString(time.getBytes(),Base64.DEFAULT)
                        + "&" + "realm=ESI"
                        + "&" + "device_id=" + deviceId;
                WebViewDialog webViewDialog = new WebViewDialog();
                Bundle bundle = new Bundle();
                bundle.putString("url",url);
                webViewDialog.setArguments(bundle);
                webViewDialog.show(getSupportFragmentManager(),"WebViewDialog");
            }else {
                setText("parse err");
            }
        }

        @Override
        public void onFinish() {

        }

        @Override
        public void onError(int code, String msg) {
            setText("config error");
        }
    };

    RetrofitCallback getDeviceIdCallback = new RetrofitCallback() {
        @Override
        public void onStart() {

        }

        @Override
        public void onSuccess(int code, String msg, String info) {
            JSONObject object = JSON.parseObject(info);
            if (object != null){
                int reqCode = object.getIntValue("code");
                if (reqCode == 0){
                    setText("get id success");
                    deviceId = object.getJSONObject("device").getString("id");
                    setText("id: "+deviceId);
                    HttpUtil.getConfig("config", configCallback);
                    return;
                }
            }
            setText("get id err");
        }

        @Override
        public void onFinish() {

        }

        @Override
        public void onError(int code, String msg) {

        }
    };

    private void getDeviceId() {
        HttpUtil.getDeviceId("getid",getDeviceIdCallback);
    }


    @Override
    public void onSuccess(String token) {
        tokenView.setText(String.format("Token: %s", token));
    }

    @Override
    public void onFail(String msg) {
        tokenView.setText("获取token失败");
    }
}
