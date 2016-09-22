package com.geekworld.cheava.yummy.utils;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

/**
 * Created by Cheava on 2016/9/16 0016.
 */
public class ToastUtil{

    public static void showShort(Context context, String msg) {
        Toast toast =  Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER | Gravity.BOTTOM,0,0);
        toast.show();
    }
    public static void showLong(Context context, String msg) {
        Toast toast =  Toast.makeText(context, msg, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER | Gravity.BOTTOM,0,0);
        toast.show();
    }
}
