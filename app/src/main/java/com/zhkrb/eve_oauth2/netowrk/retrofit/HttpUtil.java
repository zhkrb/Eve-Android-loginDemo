package com.zhkrb.eve_oauth2.netowrk.retrofit;


import com.zhkrb.eve_oauth2.netowrk.retrofit.bean.GetBean;
import com.zhkrb.eve_oauth2.netowrk.retrofit.manager.RequestManager;

import java.util.HashMap;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import okhttp3.ResponseBody;

public class HttpUtil {

    public static void init(String url){
        HttpClient.getInstance().init(url);
    }

    public static void reSetUrl(String url){
        HttpClient.getInstance().reSet(url);
    }

    public static void cancel(String tag){
        RequestManager.getInstance().cancel(tag);
    }

    public static void cancelAll(){
        RequestManager.getInstance().cancelAll();
    }

    public static Observable<ResponseBody> getBody(String api){
        return HttpClient.getInstance().get(api,null,null);
    }

    public static Observable<ResponseBody> getBodyWithoutHost(String url){
        return HttpClient.getInstance().getFullUrl(url,null,null);
    }

    public static void testSend(String tag, @NonNull RetrofitCallback callback){
        HttpClient.getInstance().get("aa",
                new GetBean()
                        .param("test",1)
                        .param("cc",2)
                ,null).subscribe(callback.addTag(tag));
    }


    public static void getConfig(String tag,@NonNull RetrofitCallback callback) {
        HttpClient.getInstance().get("/latest/swagger.json",null,null)
                .subscribe(callback.addTag(tag));
    }

    public static void getDeviceId(String tag,@NonNull RetrofitCallback callback) {
        GetBean bean = new GetBean()
                .param("game_id","aecfu6bgiuaaaal2-g-ma79")
                .param("device_model","64")
                .param("resolution","1920*185")
                .param("system_version","10")
                .param("system_name","Windows")
                .param("device_type","PC");
        HashMap<String,String> header = new HashMap<>();
        header.put("Origin","https://esi.evepc.163.com");
        header.put("Content-Type","application/x-www-form-urlencoded");
        HttpClient.getInstance().getFullUrl("https://mpay-web.g.mkey.163.com/device/init",bean,header)
                .subscribe(callback.addTag(tag));
    }
}
