package com.geekworld.cheava.yummy;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StreamCorruptedException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Cheava on 2016/7/3 0003.
 */
public class ContentProvider {

    Handler handler;

    public ContentProvider(Handler handler) {
        this.handler = handler;
    }



    public void getWord(Context context){

    }

    public void getImg(Context context){

    }

}
