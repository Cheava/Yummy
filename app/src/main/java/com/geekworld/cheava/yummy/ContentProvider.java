package com.geekworld.cheava.yummy;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.SurfaceHolder;

import com.orhanobut.logger.Logger;

import org.apache.commons.lang3.RandomUtils;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StreamCorruptedException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.HashSet;
import java.util.Random;

import hugo.weaving.DebugLog;

/**
 * Created by Cheava on 2016/7/3 0003.
 */
public class ContentProvider {
    Context context = BaseApplication.context();
    Handler handler;
    static int show_word_id = 0;
    static int show_img_id = 0;
    static private String KEY_SHOW_WORD_ID = "last_show_word_id";
    static private String KEY_SHOW_IMG_ID = "last_show_image_id";
    static private HashSet<Integer> wordHashSet=new HashSet<Integer>();
    static private HashSet<Integer> imgHashSet = new HashSet<Integer>();


    public ContentProvider(Handler handler) {
        this.handler = handler;
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_TIME_CHANGED);
        intentFilter.addAction(Intent.ACTION_TIME_TICK);
        intentFilter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
        context.registerReceiver(receiver, intentFilter);
    }

    public void updateImage() {
        int sum = DataUtils.getImgSum();
        if(sum<=0){
            //暂无内容，一般是安装后第一次运行程序
            BaseApplication.setNeedRefreshImg(true);
        }else if(sum==1){
            show_img_id = 1;
            sendMessage(Constants.SHOW_IMG, DataUtils.loadImage(show_img_id));
        }else{
            show_img_id = getImgId(sum);
            sendMessage(Constants.SHOW_IMG, DataUtils.loadImage(show_img_id));
        }
    }

    public void updateWord() {
        int sum = DataUtils.getWordSum();
        if(sum<=0){
            //暂无内容，一般是安装后第一次运行程序
            BaseApplication.setNeedRefreshWord(true);
        }else if(sum==1){
            show_word_id = 1;
            String result = DataUtils.loadWord(show_word_id);
            if("ID不存在".equals(result)){
                result = context.getString(R.string.dummy_content);
            }
            sendMessage(Constants.SHOW_WORD,result);
        }else{
            show_word_id = getWordId(sum);
            String result = DataUtils.loadWord(show_word_id);
            if("ID不存在".equals(result)){
                show_word_id = show_word_id+1;
                result =  DataUtils.loadWord(show_word_id);
            }
            sendMessage(Constants.SHOW_WORD, result);
        }
    }

    public void updateTime(){
        sendMessage(Constants.SHOW_TIME ,DateTimeUtil.getCurrentTimeString());
    }

    @DebugLog
    public void sendMessage(int what, Object obj) {
        //Handler handler = new Handler(Looper.getMainLooper());
        Message message = new Message();
        message.what = what;
        if (obj != null) {
            message.obj = obj;
        }
        handler.sendMessage(message);
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            updateTime();
        }
    };

    static public int getWordId(int sum) {
        int retry = 0;
        int random = 1;
        for(;retry<3;retry++)
        {
            random = new RandomUtil().Int(sum);
            if(!wordHashSet.contains(random)){
                wordHashSet.add(random);
                break;
            }
        }
        if(retry>=3)wordHashSet.clear();
        return random;
    }

    static public int getImgId(int sum) {
        int retry = 0;
        int random = 1;
        for(;retry<3;retry++)
        {
            random = RandomUtil.Int(sum);
            if(!imgHashSet.contains(random)){
                imgHashSet.add(random);
                break;
            }
        }
        if(retry>=3)imgHashSet.clear();
        return random;
    }

    public void destroy(){
        Logger.d("unregisterReceiver");
        context.unregisterReceiver(receiver);
    }
}
