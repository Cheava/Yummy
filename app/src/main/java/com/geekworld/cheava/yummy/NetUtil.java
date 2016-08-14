package com.geekworld.cheava.yummy;

import android.graphics.Bitmap;

import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;


import com.orhanobut.logger.Logger;

import org.apache.commons.lang3.RandomUtils;

import java.util.Random;

import hugo.weaving.DebugLog;
import okhttp3.ResponseBody;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by Cheava on 2016/8/2 0002.
 */
public class NetUtil {
    static Bitmap bitmapAsy;
    static Word wordAsy;
    static Bitmap bitmap;
    static Word word;
    static int last_net_word_id = 0;
    static int last_net_img_id = 0;
    static String word_site = "https://api.acman.cn/";
    static String img_site = "https://unsplash.it/";

    static {
        if(Looper.myLooper()==null){
            Looper.prepare();
            Looper.loop();
        }
    }

    @DebugLog
    static public void queryWordAsy(String base_url, int id) {
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
                    Logger.i("response is Success");
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
                Logger.e("retrofit", "onFailure_queryWordAsy");
            }
        });
    }

    @DebugLog
    static public void queryImageAsy(String base_url, int id, String width, String height) {
        //1.创建Retrofit对象
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(base_url)//主机地址
                .build();

        //2.创建访问API的请求
        Call<ResponseBody> call = retrofit.create(ImageSite.class).getResult(width, height, Integer.toString(id));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccess()) {
                    Logger.i("response is Success");
                    ResponseBody responseBody = response.body();
                    try {
                        byte date[] = responseBody.bytes();
                        bitmapAsy = BitmapFactory.decodeByteArray(date, 0, date.length);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    Message message = new Message();
                    message.what = Constants.DOWN_IMG;
                    // 将服务器返回的结果存放到Message中
                    message.obj = bitmapAsy;
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

    static public void downloadWord() {
        int rsi = new Random().nextInt(1000);
        int id = rsi % Constants.MAX_WORD;
        queryWordAsy(word_site, id);
    }

    static public void downloadImg() {
        int rsi = new Random().nextInt(1000);
        //int rsi = RandomUtils.nextInt(0, 1000);
        int id = rsi % Constants.MAX_IMG;
        String width = Integer.toString(BaseApplication.getScreenWidth());
        String height = Integer.toString(BaseApplication.getScreenHeight());
        queryImageAsy(img_site, id, width, height);
    }


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