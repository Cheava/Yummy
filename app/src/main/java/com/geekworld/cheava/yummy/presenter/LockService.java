package com.geekworld.cheava.yummy.presenter;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

import com.geekworld.cheava.yummy.BaseApplication;
import com.geekworld.cheava.yummy.utils.ACache;
import com.geekworld.cheava.yummy.view.LockScreenActivity;
import com.geekworld.cheava.yummy.view.SpecialMoment;
import com.orhanobut.logger.Logger;
import com.sina.weibo.sdk.api.share.Base;

/*
* @class LockService
* @desc  维护并更新展示数据，并缓存在本地
* @author wangzh
*/
public class LockService extends Service {
    public static final String TAG = "LockService";
    private ACache mCache;
    private Intent intent;

    public LockService() {
        mCache = ACache.get(BaseApplication.context());
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        IntentFilter lockscreen_filter = new IntentFilter();
        lockscreen_filter.addAction(Intent.ACTION_SCREEN_OFF);
        lockscreen_filter.addAction("android.provider.Telephony.SMS_RECEIVED");
        lockscreen_filter.setPriority(500);
        // 注册广播监听器
        BaseApplication.context().registerReceiver(lockscreen_receiver, lockscreen_filter);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        intent = new Intent(this, DownloadService.class);
        intent.setPackage(BaseApplication.context().getPackageName());
        startService(intent);
        return Service.START_STICKY;
    }

    public BroadcastReceiver lockscreen_receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Intent lockscreen;
            if("android.provider.Telephony.SMS_RECEIVED".equals(intent.getAction())){
                if(BaseApplication.isMySMS(intent)&&BaseApplication.isSpecialDay()){
                    mCache.put("is SMS Receiverd",true,5);
                    BaseApplication.lightScreen("SpecialMoment");
                    lockscreen = new Intent(LockService.this, SpecialMoment.class);
                    lockscreen.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(lockscreen);
                }
            }

            if (Intent.ACTION_SCREEN_ON.equals(intent.getAction())
                    &&
                    !BaseApplication.phoneIsInUse()
                    ) {
                Log.i("LockService", "收到开屏广播");
                Log.i("DEBUG", "收到开屏广播");
/*                if(BaseApplication.isSpecialDay())
                {
                    lockscreen = new Intent(LockService.this, SpecialMoment.class);
                }else{
                    lockscreen = new Intent(LockService.this, LockScreenActivity.class);
                }*/
                try {
                    Boolean result = (Boolean)mCache.getAsObject("is SMS Receiverd");
//                    lockscreen = new Intent(LockService.this, SpecialMoment.class);
//                    lockscreen.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    startActivity(lockscreen);
                    Logger.i(Boolean.toString(result));
                }catch (Exception e){
                }
            }
            if (Intent.ACTION_SCREEN_OFF.equals(intent.getAction())
                    &&
                    !BaseApplication.isEarpieceUsing()
                    ) {
                Log.i("LockService", "收到锁屏广播");
                lockscreen = new Intent(LockService.this, LockScreenActivity.class);
                lockscreen.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(lockscreen);
                //BaseApplication.disableSysLock(context);
            }
        }
    };


    @Override
    public void onDestroy() {
        //反注册
        BaseApplication.context().unregisterReceiver(lockscreen_receiver);
        super.onDestroy();
        intent = new Intent(this, LockService.class);
        intent.setPackage(BaseApplication.context().getPackageName());
        startService(intent);
    }
}
