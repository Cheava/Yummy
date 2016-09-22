package com.geekworld.cheava.yummy.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.geekworld.cheava.yummy.utils.ImageUtil;
import com.geekworld.cheava.yummy.R;
import com.gitonway.lee.niftymodaldialogeffects.lib.Effectstype;
import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;


/**
 * The type Share dialog.
 */
/*
* @class ShareDialog
* @desc  分享对话框
* @author wangzh
*/
public class ShareDialog extends Dialog {
    /**
     * The Ly.
     */
    LinearLayout ly;
    /**
     * The Context.
     */
    Context context;

    NaviHelper naviHelper;

    /**
     * Instantiates a new Share dialog.
     *
     * @param context the context
     */
    public ShareDialog(Context context) {
        super(context);
        this.context = context;
    }


    /**
     * Create preview.
     *
     * @param path the path
     */
    public void createPreview(String path){
        if(path == null){
            Toast.makeText(context,"无法预览图片，请确认图片是否存在",Toast.LENGTH_SHORT).show();
            return;
        }
        ly = (LinearLayout)LayoutInflater.from(context).inflate(R.layout.preview, null);
        ImageView wechat = (ImageView)ly.findViewById(R.id.preview);
        Bitmap bmp  = ImageUtil.getThumb(path,3);
        wechat.setImageBitmap(bmp);
    }

    /**
     * Show dialog.
     *
     * @param path the path
     */
    public void showDialog(final String path){
        if(path==null){
            Toast.makeText(context,"无法分享，请确认图片是否存在",Toast.LENGTH_SHORT);
            return;
        }
        createPreview(path);
        final NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(context);
        dialogBuilder
                .withTitleColor(context.getResources().getColor(R.color.shareDialogTittle))                                  //def
                .withDividerColor(context.getResources().getColor(R.color.shareDialogDivider))                              //def
                .withMessage(context.getResources().getString(R.string.shareDialogMessage))                     //.withMessage(null)  no Msg
                .withMessageColor(context.getResources().getColor(R.color.shareDialogMessage))                              //def  | withMessageColor(int resid)
                .withDialogColor(context.getResources().getColor(R.color.shareDialog))                               //def  | withDialogColor(int resid)
                .withDuration(700)
                .withTitle(context.getResources().getString(R.string.shareDialogTitle))
                .withEffect(Effectstype.Fadein)
                .withButton1Text(context.getResources().getString(R.string.shareDialogButton1Text))
                .withButton2Text(context.getResources().getString(R.string.shareDialogButton2Text))
                .isCancelableOnTouchOutside(true)
                //.withIcon(content.getResources().getDrawable(R.drawable.icon))
                .setCustomView(ly,context)
                .setButton1Click(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogBuilder.dismiss();
                        naviHelper =  NaviHelper.getInstance((Activity) context);
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

    @Override
    protected void onStop(){
        naviHelper.destroy();
        naviHelper = null;
        context = null;
    }
}

