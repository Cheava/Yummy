package com.geekworld.cheava.yummy;

import android.app.Application;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.FeatureInfo;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.android.internal.telephony.ITelephony;
import com.geekworld.cheava.greendao.DaoMaster;
import com.geekworld.cheava.greendao.DaoSession;
import com.geekworld.cheava.greendao.ScreenContentDao;
import com.geekworld.cheava.greendao.ScreenImageDao;
import com.geekworld.cheava.yummy.utils.DateTimeUtil;
import com.umeng.socialize.PlatformConfig;

import java.lang.reflect.Method;

import hugo.weaving.DebugLog;


/**
 * Created by Cheava on 2016/7/27 0027.
 */
public class BaseApplication extends Application {
    static private String PREF_NAME = "geekworld.pref";
    static private String KEY_CAMERA_ATTAIN = "is camera attain";
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

        helper = new DaoMaster.DevOpenHelper(BaseApplication.context(), "Content.db", null);
        db = helper.getWritableDatabase();
        daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();

        //微信 appid appsecret
        PlatformConfig.setWeixin("wxa44c5fa389a202b3", "6cd36b214c2c49dbf9bcf24d37db015f");
        //新浪微博 appkey appsecret
        PlatformConfig.setSinaWeibo("1115284799", "5207f2233ae89aaf4645cfcc35a89649");
        // qq qzone appid appkey

        PlatformConfig.setQQZone("1105543981", "ywjkSrKO0MzkDKzN");

        com.umeng.socialize.utils.Log.LOG = true;

        saveDisplaySize();
        setCameraAttain(checkCameraAttain());
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

    public static void set(String key, long value) {
        SharedPreferences.Editor editor = getPreferences().edit();
        editor.putLong(key, value);
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

    public static long get(String key, long defValue) {
        return getPreferences().getLong(key, defValue);
    }

    public static void saveDisplaySize() {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager)context().getSystemService(context().WINDOW_SERVICE);
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

    public static boolean isCameraAttain() {
        return get(KEY_CAMERA_ATTAIN, checkCameraAttain());
    }

    public static void setCameraAttain(boolean value) {
        set(KEY_CAMERA_ATTAIN, value);
    }

    public static boolean checkCameraAttain() {
        PackageManager pm = context().getPackageManager();
        FeatureInfo[] features = pm.getSystemAvailableFeatures();
        for (FeatureInfo f : features) {
            if (PackageManager.FEATURE_CAMERA_FLASH.equals(f.name)) {
                return true;
            }
        }
        return false;
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
        set(KEY_LAST_REFRESH_WORD, DateTimeUtil.getCurrentDateTimeString());
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
        result = text.replace("，", "，\r\n")
                .replace("。", "。\r\n")
                .replace("！", "！\r\n")
                .replace("？", "？\r\n")
                .replace(" ", " \r\n")
                .replace(";", "; \r\n");
        return result;
    }

    @DebugLog
    static public boolean isSpecialDay(){
        String now = DateTimeUtil.getCurrentDateTimeString();
        com.orhanobut.logger.Logger.i(now);
        //String record =  get(context().getResources().getString(R.string.special_day),null);
        String record =  context().getResources().getString(R.string.special_day);
        com.orhanobut.logger.Logger.i(record);
        long result = DateTimeUtil.getDifMillis(now,record);
        return (result>0 && result<24*3600*1000);
    }

    static public void  disableSysLock(){
        KeyguardManager mKeyguardManager = (KeyguardManager)context().getSystemService(Context.KEYGUARD_SERVICE);
        KeyguardManager.KeyguardLock mKeyguardLock = mKeyguardManager.newKeyguardLock("LockScreenActivity");
        mKeyguardLock.disableKeyguard();
    }


    static public boolean phoneIsInUse() {
        boolean phoneInUse = false;
        try {
            TelephonyManager telephonyManager = (TelephonyManager)context().getSystemService(TELEPHONY_SERVICE);
            Class<?> clz = Class.forName(telephonyManager.getClass().getName());
            Method method = clz.getDeclaredMethod("getITelephony");
            method.setAccessible(true);
            ITelephony iTelephony = (ITelephony) method.invoke(telephonyManager);
            if (iTelephony != null) phoneInUse = !iTelephony.isIdle();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return phoneInUse;
    }

}
