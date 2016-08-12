package com.geekworld.cheava.yummy;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
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

import org.apache.commons.lang3.RandomUtils;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StreamCorruptedException;
import java.net.HttpURLConnection;
import java.net.URL;

import hugo.weaving.DebugLog;

/**
 * Created by Cheava on 2016/7/3 0003.
 */
public class ContentProvider {
    ACache acache = ACache.get(BaseApplication.context());
    Context context = BaseApplication.context();
    Handler handler;

    public ContentProvider(Handler handler) {
        this.handler = handler;
    }

    public void updateImage() {
        int id = RandomUtils.nextInt(0, NetUtil.getImgSum());
        sendMessage(Constants.SHOW_IMG, DataUtils.loadImage(id));
    }

    public void updateWord() {
        int id = RandomUtils.nextInt(0, NetUtil.getWordSum());
        sendMessage(Constants.SHOW_WORD, DataUtils.loadWord(id));
    }

    BroadcastReceiver receiver = new BroadcastReceiver() {

        /**
         * @param context
         * @param intent
         */
        @Override
        public void onReceive(Context context, Intent intent) {
            sendMessage(Constants.SHOW_IMG, BitmapFactory.decodeResource(context.getResources(), R.drawable.unusual));
            sendMessage(Constants.SHOW_WORD, R.string.unusual);
            Log.d("ContentProvider", "onReceive");
        }
    };

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

}
