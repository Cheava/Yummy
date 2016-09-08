package com.geekworld.cheava.yummy;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gitonway.lee.niftymodaldialogeffects.lib.Effectstype;
import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;
import com.melnykov.fab.FloatingActionButton;
import com.tiancaicc.springfloatingactionmenu.OnMenuActionListener;
import com.tiancaicc.springfloatingactionmenu.SpringFloatingActionMenu;


/**
 * Created by wangzh on 2016/8/15.
 */
public class ShareDialog extends Dialog {
    LinearLayout ly;
    Context context;

    public ShareDialog(Context context) {
        super(context);
        this.context = context;
    }


    public void createPreview(String path){
        ly = (LinearLayout)LayoutInflater.from(context).inflate(R.layout.preview, null);
        ImageView wechat = (ImageView)ly.findViewById(R.id.preview);
        Bitmap bmp  = ImageUtil.getThumb(path,3);
        wechat.setImageBitmap(bmp);
    }

    public void showDialog(final String path){
        createPreview(path);
        final NiftyDialogBuilder dialogBuilder= NiftyDialogBuilder.getInstance(context);
        dialogBuilder
                .withTitleColor("#FFFFFF")                                  //def
                .withDividerColor("#11000000")                              //def
                .withMessage("This is a modal Dialog.")                     //.withMessage(null)  no Msg
                .withMessageColor("#87CEFA")                              //def  | withMessageColor(int resid)
                .withDialogColor("#87CEFA")                               //def  | withDialogColor(int resid)
                .withDuration(700)
                .withTitle("已保存")
                .withEffect(Effectstype.Fadein)
                .withButton1Text("分享")
                .withButton2Text("完成")
                .isCancelableOnTouchOutside(true)
                //.withIcon(content.getResources().getDrawable(R.drawable.icon))
                .setCustomView(ly,context)
                .setButton1Click(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogBuilder.dismiss();
                        NaviHelper.getInstance((Activity) context).share(path);
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

