package com.geekworld.cheava.yummy.presenter;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.os.Parcelable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.geekworld.cheava.yummy.BaseApplication;
import com.geekworld.cheava.yummy.bean.Constants;
import com.geekworld.cheava.yummy.utils.CacheUtil;
import com.geekworld.cheava.yummy.utils.DateTimeUtil;
import com.geekworld.cheava.yummy.utils.NetUtil;

import cn.bmob.v3.update.BmobUpdateAgent;

public class DownloadService extends Service {
    public static final String TAG = "DownloadService";

    public DownloadService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        // 注册广播监听器
        BaseApplication.context().registerReceiver(receiver, filter);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.IMG_DOWNLOAD_DONE);
        intentFilter.addAction(Constants.WORD_DOWNLOAD_DONE);
        intentFilter.addAction(Constants.IMG_DOWNLOADING);
        intentFilter.addAction(Constants.WORD_DOWNLOADING);
        // 注册本地广播监听器
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, intentFilter);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //更新缓存逻辑
        if(DateTimeUtil.getDayNumDif(
                CacheUtil.getLastRefreshImg(),
                DateTimeUtil.getCurrentDateTimeString())
                >=Constants.REFRESH_DUTY){
            CacheUtil.setNeedRefreshImg(true);
        }
        if(DateTimeUtil.getDayNumDif(
                CacheUtil.getLastRefreshWord(),
                DateTimeUtil.getCurrentDateTimeString())
                >=Constants.REFRESH_DUTY){
            CacheUtil.setNeedRefreshWord(true);
        }

        //更新缓存的策略
        if(BaseApplication.isConnected()){
            if(CacheUtil.isNeedRefreshWord()){
                NetUtil.downloadWord();
            }
            if(CacheUtil.isNeedRefreshImg()){
                NetUtil.downloadImg();
            }
        }
        return Service.START_STICKY;
    }

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            // 这个监听wifi的连接状态即是否连上了一个有效无线路由
            if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(action)) {
                Parcelable parcelableExtra = intent
                        .getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                if (null != parcelableExtra) {
                    NetworkInfo networkInfo = (NetworkInfo) parcelableExtra;
                    NetworkInfo.State state = networkInfo.getState();
                    boolean isConnected = state == NetworkInfo.State.CONNECTED;// 确定状态
                    Log.d(TAG, "isConnected " + isConnected);
                    if (isConnected) {
                        if(CacheUtil.isNeedRefreshWord()){
                            NetUtil.downloadWord();
                        }
                        if(CacheUtil.isNeedRefreshImg()){
                            NetUtil.downloadImg();
                        }
                    }
                }
            }
            //下载中
            if(Constants.WORD_DOWNLOADING.equals(action)) {
                if(BaseApplication.isConnected()){
                    NetUtil.downloadWord();
                }
            }

            if(Constants.IMG_DOWNLOADING.equals(action)) {
                if(BaseApplication.isConnected()){
                    NetUtil.downloadImg();
                }
            }

            //下载完成
            if(Constants.WORD_DOWNLOAD_DONE.equals(action)) {
                CacheUtil.setNeedRefreshWord(false);
                CacheUtil.setLastRefreshWord(DateTimeUtil.getCurrentDateTimeString());
            }
            if(Constants.IMG_DOWNLOAD_DONE.equals(action)) {
                CacheUtil.setNeedRefreshImg(false);
                CacheUtil.setLastRefreshImg(DateTimeUtil.getCurrentDateTimeString());
            }
        }
    };

    @Override
    public void onDestroy() {
        //反注册
        BaseApplication.context().unregisterReceiver(receiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        super.onDestroy();
    }

}
