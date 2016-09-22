package com.geekworld.cheava.yummy.view;

import android.content.Context;
import android.graphics.Color;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by Cheava on 2016/9/15 0015.
 */
public class WaitingDialog extends SweetAlertDialog {
    public WaitingDialog(Context context) {
        super(context, SweetAlertDialog.PROGRESS_TYPE);
        this.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        this.setTitleText("Loading");
        this.setCancelable(false);
    }
}
