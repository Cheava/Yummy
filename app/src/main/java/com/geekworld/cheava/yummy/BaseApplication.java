package com.geekworld.cheava.yummy;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

import com.geekworld.cheava.greendao.DaoMaster;
import com.geekworld.cheava.greendao.DaoSession;
import com.geekworld.cheava.greendao.ScreenContentDao;
import com.geekworld.cheava.greendao.ScreenImageDao;

import de.greenrobot.dao.async.AsyncSession;


/**
 * Created by Cheava on 2016/7/27 0027.
 */
public class BaseApplication extends Application {
    static private String PREF_NAME = "geekworld.pref";
    static private String KEY_CONNECTED = "connected";
    static private String KEY_NEED_REFRESH_WORD = "need to refresh word";
    static private String KEY_NEED_REFRESH_IMG = "need to refresh image";
    static private String KEY_LAST_REFRESH_WORD = "last time refresh word";
    static private String KEY_LAST_REFRESH_IMG = "last time refresh image";
    static BaseApplication _context;
    public static DaoMaster daoMaster;
    public static DaoSession daoSession;
    public static SQLiteDatabase db;
    public static DaoMaster.DevOpenHelper helper;


    public static DaoSession getDaoSession() {
        return daoSession;
    }

    public static SQLiteDatabase getSqLiteDatabase() {
        return db;
    }

    public static ScreenContentDao getScreenContentDao() {
        return getDaoSession().getScreenContentDao();
    }

    public static ScreenImageDao getScreenImageDao() {
        return getDaoSession().getScreenImageDao();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        _context = this;
        /*
      * 通过 DaoMaster 的内部类 DevOpenHelper，你可以得到一个便利的
        SQLiteOpenHelper 对象
      */
        helper = new DaoMaster.DevOpenHelper(BaseApplication.context(), "Content.db", null);
        db = helper.getWritableDatabase();
        daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
    }

    public static synchronized BaseApplication context() {
        return  _context;
    }

    public static SharedPreferences getPreferences() {
        return  context().getSharedPreferences(PREF_NAME,Context.MODE_MULTI_PROCESS);
    }

    public static SharedPreferences getPreferences(String prefName) {
        return context().getSharedPreferences(prefName, Context.MODE_MULTI_PROCESS);
    }

    public static void set(String key, int value) {
        SharedPreferences.Editor editor = getPreferences().edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public static void set(String key, boolean value) {
        SharedPreferences.Editor editor = getPreferences().edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public static void set(String key, String value) {
        SharedPreferences.Editor editor = getPreferences().edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static int get(String key, int defValue) {
        return getPreferences().getInt(key, defValue);
    }

    public static boolean get(String key, boolean defValue) {
        return getPreferences().getBoolean(key, defValue);
    }

    public static String get(String key, String defValue) {
        return getPreferences().getString(key, defValue);
    }

    public static void saveDisplaySize(Context context) {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager)context.getSystemService(context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay()
                .getRealMetrics(displaymetrics);
        SharedPreferences.Editor editor = getPreferences().edit();
        editor.putInt("screen_width", displaymetrics.widthPixels);
        editor.putInt("screen_height", displaymetrics.heightPixels);
        editor.commit();
    }

    public static int getScreenWidth() {
        return getPreferences().getInt("screen_width", 720);
    }

    public static int getScreenHeight() {
        return getPreferences().getInt("screen_height", 1280);
    }

    public static int[] getDisplayY() {
        return new int[]{getPreferences().getInt("screen_width", 720),
                getPreferences().getInt("screen_height", 1280)};
    }

    public static boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) context().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo networkInfo = cm.getActiveNetworkInfo();
            if (networkInfo != null
                    && networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                return true;
            }else{
                return false;
            }
        }
        return false;
    }

    public static boolean isNeedRefreshWord(){
        return get(KEY_NEED_REFRESH_WORD, true);
    }

    public static void setNeedRefreshWord(Boolean refreshWord) {
        set(KEY_NEED_REFRESH_WORD,refreshWord);
    }

    public static boolean isNeedRefreshImg(){
        return get(KEY_NEED_REFRESH_IMG, true);
    }

    public static void setNeedRefreshImg(Boolean refreshWord) {
        set(KEY_NEED_REFRESH_IMG,refreshWord);
    }
    public static void sendLocalBroadcast(String info) {
        Intent intent = new Intent(info);
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(context());
        localBroadcastManager.sendBroadcast(intent); // 发送本地广播
    }

    public static String getImgName(int key){
        return Integer.toString(key)+".jpg"; //获得私有文件的目录
    }
    public static String getImgPath(int key){
        return context().getFilesDir().getAbsolutePath()+"/"+getImgName(key); //获得私有文件的目录
    }

    public static void setLastRefreshWord(){
        set(KEY_LAST_REFRESH_WORD,DateTimeUtil.getCurrentDateTimeString());
    }

    public static String getLastRefreshWord(){
       return get(KEY_LAST_REFRESH_WORD,"2014-08-27 01:02:03");
    }

    public static void setLastRefreshImg(){
        set(KEY_LAST_REFRESH_IMG,DateTimeUtil.getCurrentDateTimeString());
    }

    public static String getLastRefreshImg(){
       return get(KEY_LAST_REFRESH_IMG,"2014-08-27 01:02:03");
    }

    public static String prettifyText(String text){
        String result;
        result = text.replace("，","，\r\n");
        result = result.replace("。","。\r\n");
        result = result.replace("！","！\r\n");
        result = result.replace("？","？\r\n");
        result = result.replace(" "," \r\n");
        result = result.replace(";","; \r\n");
        return result;
    }
}
