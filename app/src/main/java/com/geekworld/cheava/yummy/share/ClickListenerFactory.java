package com.geekworld.cheava.yummy.share;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.hardware.Camera;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.geekworld.cheava.yummy.BaseApplication;
import com.geekworld.cheava.yummy.R;
import com.geekworld.cheava.yummy.bean.FlashLight;
import com.geekworld.cheava.yummy.utils.CacheUtil;
import com.geekworld.cheava.yummy.utils.FastTools;
import com.geekworld.cheava.yummy.view.FabMenuFactory;
import com.orhanobut.logger.Logger;
import com.umeng.socialize.Config;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.ShareContent;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;

import java.io.File;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * The type Click listener factory.
 */
/*
* @class ClickListenerFactory
* @desc  按键回调工厂类
* @author wangzh
*/
public class ClickListenerFactory implements View.OnClickListener {
    private UmengShare umengshare;
    private FastTools fastTools;
    private SimpleShare simpleShare;
    /**
     * Instantiates a new Click listener factory.
     * 生成分享的按键回调
     * @param context  the context
     * @param platform the platform
     * @param path     the path
     */
    public ClickListenerFactory(Context context, SHARE_MEDIA platform, String path) {
        umengshare = new UmengShare(context,platform,path);
    }

    public ClickListenerFactory(Context context, FabMenuFactory.SharePlatform platform, String path) {
        simpleShare = new SimpleShare(context,platform,path);
    }

    public ClickListenerFactory(Context context, FabMenuFactory.SharePlatform platform, Uri uri) {
        simpleShare = new SimpleShare(context,platform,uri);
    }

    /**
     * Instantiates a new Click listener factory.
     * 生成快捷工具的回调
     * @param context the context
     * @param tools   the tools
     */
    public ClickListenerFactory(Context context, FabMenuFactory.FastTools tools) {
        fastTools = new FastTools(context,tools);
    }


    @Override
    public void onClick(View v) {
        if (fastTools != null) {
            fastTools.toolsHandler();
        } else if (simpleShare != null) {
            simpleShare.shareImage();
        }else {
            umengshare.shareImage();
        }
    }
}
