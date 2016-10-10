package com.geekworld.cheava.yummy.presenter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.geekworld.cheava.yummy.BaseApplication;
import com.orhanobut.logger.Logger;

public class LockScreenReceiver extends BroadcastReceiver {
    public LockScreenReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Logger.i("Static Receiver");
        Intent startIntent = new Intent(BaseApplication.context(),LockService.class);
        startIntent.setPackage(BaseApplication.context().getPackageName());
        context.startService(startIntent);
        startIntent = new Intent(BaseApplication.context(),DownloadService.class);
        startIntent.setPackage(BaseApplication.context().getPackageName());
        context.startService(startIntent);
    }
}
