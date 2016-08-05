package com.geekworld.cheava.yummy;

import android.app.Application;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.os.Parcelable;
import android.util.Log;

public class LockService extends Service {
    public static final String TAG = "LockService";

    public LockService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(Constants.IMG_DOWNLOAD_DONE);
        filter.addAction(Constants.WORD_DOWNLOAD_DONE);
        filter.addAction(Constants.IMG_DOWNLOADING);
        filter.addAction(Constants.WORD_DOWNLOADING);
        registerReceiver(receiver, filter);
        // 注册广播监听器
    }

    BroadcastReceiver receiver = new BroadcastReceiver() {

        /**
         * @param context
         * @param intent
         */
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Intent.ACTION_SCREEN_OFF.equals(intent.getAction())) {
                Log.d("LockService", "收到锁屏广播");
                Intent lockscreen = new Intent(LockService.this, LockScreenActivity.class);
                lockscreen.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(lockscreen);
            }

            // 这个监听wifi的连接状态即是否连上了一个有效无线路由，当上边广播的状态是WifiManager.WIFI_STATE_DISABLING，和WIFI_STATE_DISABLED的时候，根本不会接到这个广播。
            if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent.getAction())) {
                Parcelable parcelableExtra = intent
                        .getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                if (null != parcelableExtra) {
                    NetworkInfo networkInfo = (NetworkInfo) parcelableExtra;
                    NetworkInfo.State state = networkInfo.getState();
                    boolean isConnected = state == NetworkInfo.State.CONNECTED;// 当然，这边可以更精确的确定状态
                    Log.e(TAG, "isConnected " + isConnected);
                    if (isConnected) {
                        NetUtil.downloadImg();
                        NetUtil.downloadWord();
                        BaseApplication.setConnected(isConnected);
                    } else {
                        BaseApplication.setConnected(isConnected);
                    }
                }
            }

            if (Constants.IMG_DOWNLOADING.equals(intent.getAction())) {
                NetUtil.downloadImg();
            }
            if (Constants.WORD_DOWNLOADING.equals(intent.getAction())) {
                NetUtil.downloadWord();
            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }



}
