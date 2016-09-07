package com.geekworld.cheava.yummy;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;

import com.orhanobut.logger.Logger;

/**
 * Created by Cheava on 2016/8/14 0014.
 */
public class SpacialDay extends LockScreenActivity {

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            background.setImageResource(R.mipmap.unusual);
            content.setText(R.string.unusual);
            Logger.i("onReceive");
        }
    };

}
