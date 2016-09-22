package com.geekworld.cheava.yummy.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.geekworld.cheava.yummy.BaseApplication;
import com.geekworld.cheava.yummy.bean.Constants;
import com.geekworld.cheava.yummy.bean.ImageSite;
import com.geekworld.cheava.yummy.bean.Word;
import com.geekworld.cheava.yummy.bean.WordSite;
import com.orhanobut.logger.Logger;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import hugo.weaving.DebugLog;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * The type Net util.
 */
/*
* @class NetUtil
* @desc  网络工具类
* @author wangzh
*/
public class NetUtil {
    /**
     * The Bitmap asy.
     */
    static WeakReference<Bitmap> bitmapAsy;
    /**
     * The Word asy.
     */
    static Word wordAsy;
    /**
     * The Last net word id.
     */
    static int last_net_word_id = 0;
    /**
     * The Last net img id.
     */
    static int last_net_img_id = 0;
    /**
     * The Word site.
     */
    static String word_site = "https://api.acman.cn/";
    /**
     * The Img site.
     */
    static String img_site = "https://unsplash.it/";

    //建立Looper
    static {
        if(Looper.myLooper()==null){
            Looper.prepare();
            Looper.loop();
        }
    }

    /**
     * 重试拦截器
     */
    static public class RetryIntercepter implements Interceptor {

        public int maxRetry;//最大重试次数
        private int retryNum = 0;//假如设置为3次重试的话，则最大可能请求4次（默认1次+3次重试）

        public RetryIntercepter(int maxRetry) {
            this.maxRetry = maxRetry;
        }

        @Override
        public okhttp3.Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            System.out.println("retryNum=" + retryNum);
            okhttp3.Response response = chain.proceed(request);
            while (!response.isSuccessful() && retryNum < maxRetry) {
                retryNum++;
                System.out.println("retryNum=" + retryNum);
                response = chain.proceed(request);
            }
            return response;
        }
    }

    /**
     * The constant client.
     */
//配置client参数
    static OkHttpClient client = new OkHttpClient.Builder()
            // 连接超时时间设置
            .connectTimeout(10, TimeUnit.SECONDS)
            // 读取超时时间设置
            .readTimeout(10, TimeUnit.SECONDS)
            // 失败重试
            .retryOnConnectionFailure(true)
            // 重试次数
            .addInterceptor(new RetryIntercepter(3))
            // 信任的主机名，返回true表示信任，可以根据主机名和SSLSession判断主机是否信任
            .hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            })
            .build();

    /**
     * Query word asy.
     *
     * @param base_url the base url
     * @param id       the id
     */
    @DebugLog
    static public void queryWordAsy(String base_url, int id) {
        Logger.i(Integer.toString(id));
        //1.创建Retrofit对象
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())//解析方法
                .baseUrl(base_url)//主机地址
                .build();

        //2.创建访问API的请求
        Call<Word> call = retrofit.create(WordSite.class).getResult(Integer.toString(id));
        call.enqueue(new Callback<Word>() {
            @Override
            public void onResponse(Call<Word> call, Response<Word> response) {
                //请求成功
                if (response.isSuccess()) {
                    Log.i("retrofit", "response is Success");
                    wordAsy = response.body();
                    Message message = new Message();
                    message.what = Constants.DOWN_WORD;
                    // 将服务器返回的结果存放到Message中
                    message.obj = wordAsy;
                    handler.sendMessage(message);
                }
            }

            @Override
            public void onFailure(Call<Word> call, Throwable t) {
                //请求失败
                Log.e("retrofit", "onFailure_queryWordAsy");
            }
        });
    }

    /**
     * Query image asy.
     *
     * @param base_url the base url
     * @param id       the id
     * @param width    the width
     * @param height   the height
     */
    @DebugLog
    static public void queryImageAsy(String base_url, int id, String width, String height) {
        Logger.i(Integer.toString(id));
        //1.创建Retrofit对象
        Retrofit retrofit = new Retrofit.Builder()
                .client(client)
                .baseUrl(base_url)//主机地址
                .build();

        //2.创建访问API的请求
        Call<ResponseBody> call = retrofit.create(ImageSite.class).getResult(width, height, Integer.toString(id));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccess()) {
                    Log.i("retrofit", "response is Success");
                    ResponseBody responseBody = response.body();
                    try {
                        byte date[] = responseBody.bytes();
                        bitmapAsy = new WeakReference<Bitmap>(BitmapFactory.decodeByteArray(date, 0, date.length));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    Message message = new Message();
                    message.what = Constants.DOWN_IMG;
                    // 将服务器返回的结果存放到Message中
                    message.obj = bitmapAsy.get();
                    handler.sendMessage(message);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                //请求失败
                Log.e("retrofit", "onFailure_queryImage");
            }
        });
    }

    /**
     * Download word.
     */
    static public void downloadWord() {
        int rsi = RandomUtil.Int(1000);
        queryWordAsy(word_site, rsi);
    }

    /**
     * Download img.
     */
    static public void downloadImg() {
        int rsi = RandomUtil.Int(1000);
        String width = Integer.toString(CacheUtil.getScreenWidth());
        String height = Integer.toString(CacheUtil.getScreenHeight());
        queryImageAsy(img_site, rsi, width, height);
    }

    //下载逻辑
    static private Handler handler = new Handler(Looper.myLooper()) {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.DOWN_WORD:
                    Word wordJson = (Word) msg.obj;
                    BaseApplication.get("last_net_word_id", last_net_word_id);
                    last_net_word_id = last_net_word_id + 1;
                    String word = wordJson.getTaici();
                    DataUtils.save(last_net_word_id, word);
                    BaseApplication.set("last_net_word_id", last_net_word_id);

                    //容量控制
                    if (last_net_word_id >= Constants.MAX_WORD) {
                        last_net_word_id = 0;
                        BaseApplication.sendLocalBroadcast(Constants.WORD_DOWNLOAD_DONE);
                    } else{
                        BaseApplication.sendLocalBroadcast(Constants.WORD_DOWNLOADING);
                    }
                    break;
                case Constants.DOWN_IMG:
                    Bitmap bmp = (Bitmap) msg.obj;
                    BaseApplication.get("last_net_img_id", last_net_img_id);
                    last_net_img_id = last_net_img_id + 1;
                    DataUtils.save(last_net_img_id, bmp);
                    BaseApplication.set("last_net_img_id", last_net_img_id);

                    //容量控制
                    if (last_net_img_id >= Constants.MAX_IMG) {
                        BaseApplication.sendLocalBroadcast(Constants.IMG_DOWNLOAD_DONE);
                    } else{
                        BaseApplication.sendLocalBroadcast(Constants.IMG_DOWNLOADING);
                    }
                    break;
            }
        }
    };

}