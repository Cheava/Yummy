package com.geekworld.cheava.yummy;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.gitonway.lee.niftymodaldialogeffects.lib.Effectstype;
import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;
import com.melnykov.fab.FloatingActionButton;
import com.tiancaicc.springfloatingactionmenu.OnMenuActionListener;
import com.tiancaicc.springfloatingactionmenu.SpringFloatingActionMenu;


/**
 * Created by wangzh on 2016/8/15.
 */
public class ShareDialog {
    private static ShareDialog instance = null;
    final Activity activity;
    FrameLayout ly;

    /* 私有构造方法，防止被实例化 */
    private ShareDialog(Activity activity) {
        this.activity = activity;
    }

    /* 1:懒汉式，静态工程方法，创建实例 */
    public static ShareDialog getInstance(Activity activity) {
        if (instance == null) {
            instance = new ShareDialog(activity);
        }
        return instance;
    }


    public void createPreview(String path){
        ly = (FrameLayout)LayoutInflater.from(activity).inflate(R.layout.preview, null);
        ImageView wechat = new ImageView(activity);
        Bitmap bmp  = ImageUtil.getThumb(path,2);
        wechat.setImageBitmap(bmp);
        ly.addView(wechat);
    }

    public void showDialog(final String path){
        createPreview(path);
        final NiftyDialogBuilder dialogBuilder= NiftyDialogBuilder.getInstance(activity);
        dialogBuilder
                .withTitle("已保存")
                .withEffect(Effectstype.Fadein)
                .withButton1Text("分享")
                .withButton2Text("完成")
                .isCancelableOnTouchOutside(true)
                //.withIcon(content.getResources().getDrawable(R.drawable.icon))
                .setCustomView(ly,activity)
                .setButton1Click(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogBuilder.dismiss();
                        NaviHelper naviHelper = NaviHelper.getInstance(activity);
                        naviHelper.share(path);
                    }
                })
                .setButton2Click(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogBuilder.dismiss();

                    }
                })
                .show();
    }
}

