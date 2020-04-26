package com.zhkrb.eve_oauth2.netowrk.retrofit;

import com.zhkrb.eve_oauth2.netowrk.retrofit.bean.GetBean;
import com.zhkrb.eve_oauth2.netowrk.retrofit.bean.PostBean;
import com.zhkrb.eve_oauth2.netowrk.retrofit.model.GetModel;
import com.zhkrb.eve_oauth2.netowrk.retrofit.model.PostModel;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

public class HttpClient {

    private static String mUrl;
    private static HttpClient mClient;
    private static final int TIMEOUT = 30000;
    private static OkHttpClient mOkHttpClient;
    private static Retrofit mRetrofit;

    private HttpClient(){
    }

    public static HttpClient getInstance(){
        if (mClient==null){
            synchronized (HttpClient.class){
                if (mClient == null){
                    mClient = new HttpClient();
                }
            }
        }
        return mClient;
    }

    public void init(String url){
        mUrl = url;
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(TIMEOUT, TimeUnit.MILLISECONDS);
        builder.readTimeout(TIMEOUT, TimeUnit.MILLISECONDS);
        builder.writeTimeout(TIMEOUT, TimeUnit.MILLISECONDS);
//        builder.cookieJar(new CookieJarImpl(new MemoryCookieStore()));
        builder.retryOnConnectionFailure(true);

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor("http");
        loggingInterceptor.setPrintLevel(HttpLoggingInterceptor.Level.BASIC);
        builder.addInterceptor(loggingInterceptor);
        mOkHttpClient = builder.build();

        mRetrofit = new Retrofit.Builder()
                .baseUrl(mUrl)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(mOkHttpClient)
                .build();
    }

    public void reSet(String url) {
        mUrl = url;
        mRetrofit = new Retrofit.Builder()
                .baseUrl(mUrl)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(mOkHttpClient)
                .build();
    }

    Observable<ResponseBody> get(String apiName, GetBean bean, Map<String, String> headers){
        GetModel getModel = mRetrofit.create(GetModel.class);
        String a = "";
        if (bean!=null){
            a = bean.create();
        }
        if (headers == null){
            return getModel.get(mUrl+apiName+a).compose(SchedulerProvider.getInstance().applaySchedulers());
        }
            return getModel.get(headers,mUrl+apiName+a).compose(SchedulerProvider.getInstance().applaySchedulers());
    }

    Observable<ResponseBody> getFullUrl(String url, GetBean bean, Map<String, String> headers){
        GetModel getModel = mRetrofit.create(GetModel.class);
        String a = "";
        if (bean!=null){
            a = bean.create();
        }
        if (headers == null){
            return getModel.get(url+a).compose(SchedulerProvider.getInstance().applaySchedulers());
        }
        return getModel.get(headers,url+a).compose(SchedulerProvider.getInstance().applaySchedulers());
    }

    Observable<ResponseBody> post(String apiName, PostBean bean, Map<String, String> headers){
        PostModel postModel = mRetrofit.create(PostModel.class);
        RequestBody body = null;
        if (bean!=null&&!bean.isEmpry()){
            body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"),bean.create());
        }
        if (headers == null){
            return postModel.post(mUrl+apiName,body).compose(SchedulerProvider.getInstance().applaySchedulers());
        }
        return postModel.post(headers,mUrl+apiName,body).compose(SchedulerProvider.getInstance().applaySchedulers());
    }



}
