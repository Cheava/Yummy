package com.geekworld.cheava.yummy.utils;

import android.util.Log;

import com.geekworld.cheava.yummy.BaseApplication;
import com.geekworld.cheava.yummy.R;
import com.geekworld.cheava.yummy.bean.Config;
import com.geekworld.cheava.yummy.bean.Screenshot;

import java.io.Serializable;

import hugo.weaving.DebugLog;

/**
 * Created by wangzh on 2016/9/14.
 */
public class CacheUtil {
    static private String default_time = "2014-08-27 01:02:03";
    static private String KEY_SCREENSHOT = "Screenshot";
    static private String KEY_SCREEN_SIZE = "Screen Size";
    static private String KEY_NEED_REFRESH = "Need To Refresh";
    static private String KEY_LAST_REFRESH_TIME = "Last Time Refresh";
    static private String KEY_CAMERA_ATTAIN = "is camera attain";

    private static ACache mCache = ACache.get(BaseApplication.context());
    private static Config config = new Config();
    private static Config.ScreenSize screenSize = config.new ScreenSize(720,1080);
    private static Config.NeedRefresh needRefresh =  config.new NeedRefresh(true,true);
    private static Config.LastRefreshTime refreshTime = config.new LastRefreshTime(default_time,default_time);

    static  {
        //if(mCache.getAsObject(KEY_SCREEN_SIZE)==null)
        {
            mCache.put(KEY_SCREEN_SIZE,screenSize);
            mCache.put(KEY_NEED_REFRESH,needRefresh);
            mCache.put(KEY_LAST_REFRESH_TIME,refreshTime);
        }
    }

    public static void setScreenSize(int width ,int height){
        screenSize.setWidth(width);
        screenSize.setHeight(height);
        Log.i("CacheUtil",Boolean.toString(screenSize instanceof Serializable));
        mCache.put(KEY_SCREEN_SIZE,screenSize);
    }

    @DebugLog
    public static int getScreenWidth() {
        Config.ScreenSize screenSize = (Config.ScreenSize)mCache.getAsObject(KEY_SCREEN_SIZE);
        return screenSize.getWidth();
        //return getPreferences().getInt("screen_width", 720);
    }

    @DebugLog
    public static int getScreenHeight() {
        return ((Config.ScreenSize)mCache
                .getAsObject(KEY_SCREEN_SIZE))
                .getHeight();
        //return getPreferences().getInt("screen_height", 1280);
    }

    public static boolean isCameraAttain() {
        return ((Boolean) mCache.getAsObject(KEY_CAMERA_ATTAIN));
    }

    public static void setCameraAttain(boolean value) {
        mCache.put(KEY_CAMERA_ATTAIN,value);
    }


    public static boolean isNeedRefreshWord(){
        return ((Config.NeedRefresh)mCache.getAsObject(KEY_NEED_REFRESH)).isWordNeed();
        //return get(KEY_NEED_REFRESH_WORD, true);
    }

    public static void setNeedRefreshWord(Boolean refreshWord) {
        needRefresh.setWordNeed(refreshWord);
        mCache.put(KEY_NEED_REFRESH,needRefresh);
        //set(KEY_NEED_REFRESH_WORD,refreshWord);
    }

    public static boolean isNeedRefreshImg(){
        return ((Config.NeedRefresh)mCache.getAsObject(KEY_NEED_REFRESH)).isImgNeed();
        //return get(KEY_NEED_REFRESH_IMG, true);
    }

    public static void setNeedRefreshImg(Boolean refreshImg) {
        needRefresh.setImgNeed(refreshImg);
        mCache.put(KEY_NEED_REFRESH,needRefresh);
        //set(KEY_NEED_REFRESH_IMG,refreshWord);
    }

    public static void setLastRefreshWord(String time){
        refreshTime.setWordTime(time);
        mCache.put(KEY_LAST_REFRESH_TIME,refreshTime);
        //set(KEY_LAST_REFRESH_WORD, DateTimeUtil.getCurrentDateTimeString());
    }

    public static String getLastRefreshWord(){
        return  ((Config.LastRefreshTime)mCache.getAsObject(KEY_LAST_REFRESH_TIME)).getWordTime();
        //return get(KEY_LAST_REFRESH_WORD,"2014-08-27 01:02:03");
    }

    public static void setLastRefreshImg(String time){
        refreshTime.setImgTime(time);
        mCache.put(KEY_LAST_REFRESH_TIME,refreshTime);
        //set(KEY_LAST_REFRESH_IMG,DateTimeUtil.getCurrentDateTimeString());
    }

    public static String getLastRefreshImg(){
        return  ((Config.LastRefreshTime)mCache.getAsObject(KEY_LAST_REFRESH_TIME)).getImgTime();
        //return get(KEY_LAST_REFRESH_IMG,"2014-08-27 01:02:03");
    }

    public static void setScreenshot(Screenshot screenshot){
        mCache.put(KEY_SCREENSHOT,screenshot);
    }

    public static String getScreenshotPath(){
        return  ((Screenshot)mCache.getAsObject(KEY_SCREENSHOT)).getImgPath();
    }

    public static String getScreenshotUriString(){
        return  ((Screenshot)mCache.getAsObject(KEY_SCREENSHOT)).getUriString();
    }
}
