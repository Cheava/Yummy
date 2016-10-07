package com.geekworld.cheava.yummy;

import android.app.ActivityManager;
import android.app.Application;
import android.app.KeyguardManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.FeatureInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.android.internal.telephony.ITelephony;
import com.geekworld.cheava.greendao.DaoMaster;
import com.geekworld.cheava.greendao.DaoSession;
import com.geekworld.cheava.greendao.ScreenContentDao;
import com.geekworld.cheava.greendao.ScreenImageDao;
import com.geekworld.cheava.yummy.utils.CacheUtil;
import com.geekworld.cheava.yummy.utils.DateTimeUtil;
import com.orhanobut.logger.Logger;
import com.umeng.socialize.PlatformConfig;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;

import cn.bmob.v3.Bmob;
import hugo.weaving.DebugLog;

/*
* @class BaseApplication
* @desc  全局控制
* @author wangzh
*/
public class BaseApplication extends Application {
    static private String PREF_NAME = "geekworld.pref";

    static private String KEY_NEED_REFRESH_WORD = "need to refresh word";
    static private String KEY_NEED_REFRESH_IMG = "need to refresh image";
    static private String KEY_LAST_REFRESH_WORD = "last time refresh word";
    static private String KEY_LAST_REFRESH_IMG = "last time refresh image";

    static private SensorManager mManager;//传感器管理对象
    static volatile private Boolean isEarClose = false;



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

        Bmob.initialize(this, "245cb719cff6be64e6791c0032c776bc");

        com.umeng.socialize.utils.Log.LOG = true;

        saveDisplaySize();
        CacheUtil.setCameraAttain(checkCameraAttain());
        //LeakCanary.install(this);
    }

    public static synchronized BaseApplication context() {
        return _context;
    }

    public static SharedPreferences getPreferences() {
        return context().getSharedPreferences(PREF_NAME, Context.MODE_MULTI_PROCESS);
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
        WindowManager windowManager = (WindowManager) context().getSystemService(context().WINDOW_SERVICE);
        windowManager.getDefaultDisplay()
                .getRealMetrics(displaymetrics);
        CacheUtil.setScreenSize(displaymetrics.widthPixels, displaymetrics.heightPixels);
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
            } else {
                return false;
            }
        }
        return false;
    }


    public static String getImgName(int key) {
        return Integer.toString(key) + ".jpg"; //获得私有文件的目录
    }

    public static String getImgPath(int key) {
        return context().getFilesDir().getAbsolutePath() + "/" + getImgName(key); //获得私有文件的目录
    }

    public static void sendLocalBroadcast(String info) {
        Intent intent = new Intent(info);
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(context());
        localBroadcastManager.sendBroadcast(intent); // 发送本地广播
    }

    public static String prettifyText(String text) {
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
    static public boolean isSpecialDay() {
        String now = DateTimeUtil.getCurrentDateTimeString();
        com.orhanobut.logger.Logger.i(now);
        //String record =  get(context().getResources().getString(R.string.special_day),null);
        String record = context().getResources().getString(R.string.special_day);
        com.orhanobut.logger.Logger.i(record);
        long result = DateTimeUtil.getDifMillis(now, record);
        return (result > 0 && result < 24 * 3600 * 1000);
    }

    static public void disableSysLock(Context context, String activity) {
        KeyguardManager mKeyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        KeyguardManager.KeyguardLock mKeyguardLock = mKeyguardManager.newKeyguardLock(activity);
        mKeyguardLock.disableKeyguard();
        Log.i("BaseApplication", "disableSysLock");
    }


    static public boolean phoneIsInUse() {
        boolean phoneInUse = false;
        try {
            TelephonyManager telephonyManager = (TelephonyManager) context().getSystemService(TELEPHONY_SERVICE);
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

    public static String getRealFilePath(final Context context, final Uri uri) {
        if (null == uri) return null;
        final String scheme = uri.getScheme();
        String data = null;
        if (scheme == null)
            data = uri.getPath();
        else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            Cursor cursor = context.getContentResolver().query(uri, new String[]{MediaStore.Images.ImageColumns.DATA}, null, null, null);
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    if (index > -1) {
                        data = cursor.getString(index);
                    }
                }
                cursor.close();
            }
        }
        return data;
    }

    /**
     * 根据Uri获取图片路径
     *
     * @param context the context
     * @param uri     the uri
     * @return file path by content resolver
     */
    public static String getFilePathByContentResolver(Context context, Uri uri) {
        if (null == uri) {
            return null;
        }
        Cursor c = context.getContentResolver().query(uri, null, null, null, null);
        String filePath = null;
        if (null == c) {
            throw new IllegalArgumentException(
                    "Query on " + uri + " returns null result.");
        }
        try {
            if ((c.getCount() != 1) || !c.moveToFirst()) {
            } else {
                filePath = c.getString(c.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA));
            }
        } finally {
            c.close();
        }
        return filePath;
    }

    static public boolean isMySMS(Intent intent) {
        Bundle bundle = intent.getExtras();
        Object[] pdus = (Object[]) bundle.get("pdus"); // 提取短信消息
        SmsMessage[] messages = new SmsMessage[pdus.length];
        for (int i = 0; i < messages.length; i++) {
            messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
        }
        String address = messages[0].getOriginatingAddress(); // 获取发送方号码
        Logger.i(address + "/" + context().getString(R.string.my_num));
        return (address.equals(context().getString(R.string.my_num)));
    }

    static public void lightScreen(String activity) {
        PowerManager pm = (PowerManager) context().getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock mWakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP
                | PowerManager.ON_AFTER_RELEASE, "BaseApplication");
        mWakeLock.acquire(10);
        disableSysLock(context(), activity);
    }

    static public boolean isCalling() {
        AudioManager audioManager = (AudioManager) context().getSystemService(Context.AUDIO_SERVICE);
        int result = audioManager.getMode();
        Logger.i(Integer.toString(result));
        return result != AudioManager.MODE_NORMAL;
    }

    static public String getTopActivity() {
        ActivityManager mActivityManager = (ActivityManager) context().getSystemService(
                Context.ACTIVITY_SERVICE);
        String packname = ""; /* Android5.0之后获取程序锁的方式是不一样的*/
        if (Build.VERSION.SDK_INT > 20) {
            // 5.0及其以后的版本
//            List<ActivityManager.RunningAppProcessInfo> tasks = mActivityManager.getRunningAppProcesses();
//            if (null != tasks && tasks.size() > 0) {
//                for(Iterator iter = tasks.iterator(); iter.hasNext();){
//                    Log.i("DEBUG",((ActivityManager.RunningAppProcessInfo)iter.next()).processName);
//                }
//                packname = tasks.get(0).processName;
//            }
            packname = getRunningApp();
        } else {
            // 5.0之前
            // 获取正在运行的任务栈(一个应用程序占用一个任务栈) 最近使用的任务栈会在最前面
            // 1表示给集合设置的最大容量 List<RunningTaskInfo> infos = am.getRunningTasks(1);
            // 获取最近运行的任务栈中的栈顶Activity(即用户当前操作的activity)的包名
            List<ActivityManager.RunningTaskInfo> infos = mActivityManager.getRunningTasks(1);
            packname = infos.get(0).topActivity.getPackageName();
        }
        Logger.i(packname);
        return packname;
    }

    static public boolean isWxRunning() {
        String WxPackage = "com.tencent.mm";
        return (WxPackage.equals(getTopActivity()));
    }

    static public void startProximity(){
        mManager = (SensorManager)context().getSystemService(Context.SENSOR_SERVICE);
        mManager.registerListener(mSensorEventListener, mManager.getDefaultSensor(Sensor.TYPE_PROXIMITY),// 距离感应器
                SensorManager.SENSOR_DELAY_FASTEST);//注册传感器，第一个参数为距离监听器，第二个是传感器类型，第三个是延迟类型
    }

    static public void stopProximity(){
        if(mManager != null){
            mManager.unregisterListener(mSensorEventListener);//注销传感器监听
        }
        mManager = null;
    }

    static public boolean isProximityNear(){
        Logger.i(Boolean.toString(isEarClose));
        return isEarClose;
    }

    static private SensorEventListener mSensorEventListener = new SensorEventListener(){
        @Override
        public void onSensorChanged(SensorEvent event) {
            float[] its = event.values;
            if (its != null && event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
                //经过测试，当手贴近距离感应器的时候its[0]返回值为0.0，当手离开时返回1.0
                if (its[0] == 0.0) {// 贴近手机
                    isEarClose = true;
                } else {// 远离手机
                    isEarClose = false;
                }
            }

        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // TODO 自动生成的方法存根

        }
    };

    static public boolean isWxVoiceRunning(){
        boolean result = false;
        if(isWxRunning()){
            startProximity();
            result = isProximityNear();
            stopProximity();
        }
        Logger.i(Boolean.toString(result));
        return result;
    }

    static public boolean isEarpieceUsing(){
        Boolean result;
        if(isCalling() || isWxVoiceRunning()){
            result = true;
        }else{
            result = false;
        }
        Logger.i(Boolean.toString(result));
        return result;
    }

    static private String getRunningApp() {
        UsageStatsManager usageStatsManager = (UsageStatsManager)
                context().getSystemService(Context.USAGE_STATS_SERVICE);
        long ts = System.currentTimeMillis();
        List<UsageStats> queryUsageStats = usageStatsManager.queryUsageStats(
                UsageStatsManager.INTERVAL_BEST,ts-2000, ts);
        if (queryUsageStats == null || queryUsageStats.isEmpty()) {
            return null;
        }
        UsageStats recentStats = null;
        for (UsageStats usageStats : queryUsageStats) {
            if (recentStats == null ||
                    recentStats.getLastTimeUsed() < usageStats.getLastTimeUsed()) {
                recentStats = usageStats;
            }
        }
        return recentStats.getPackageName();
    }

    //判断调用该设备中“有权查看使用权限的应用”这个选项的APP有没有打开
    static public boolean isNoSwitch() {
        long ts = System.currentTimeMillis();
        UsageStatsManager usageStatsManager = (UsageStatsManager)
                context().getSystemService(Context.USAGE_STATS_SERVICE);
        List queryUsageStats = usageStatsManager.queryUsageStats(
                UsageStatsManager.INTERVAL_BEST, 0, ts);
        if (queryUsageStats == null || queryUsageStats.isEmpty()) {
            return false;
        }
        return true;
    }

    /**
     * 获取版本号
     * @return 当前应用的版本号
     */
    static public String getVersion() {
        try {
            PackageManager manager = context().getPackageManager();
            PackageInfo info = manager.getPackageInfo(context().getPackageName(), 0);
            String version = info.versionName;
            return version;
        } catch (Exception e) {
            e.printStackTrace();

            return null;
        }
    }
}
