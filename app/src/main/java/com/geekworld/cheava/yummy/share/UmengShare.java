package com.geekworld.cheava.yummy.share;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.Toast;

import com.geekworld.cheava.yummy.R;
import com.geekworld.cheava.yummy.view.FabMenuFactory;
import com.geekworld.cheava.yummy.view.WaitingDialog;
import com.umeng.socialize.Config;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.ShareContent;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;

/**
 * Created by Cheava on 2016/9/15 0015.
 */
public class UmengShare {
    private Context context;
    private SHARE_MEDIA umeng_platform;
    private String path = null;

    public UmengShare(Context context, SHARE_MEDIA umeng_platform,String path) {
        this.context = context;
        this.path = path;
        this.umeng_platform = umeng_platform;
    }

    public void shareImage(){
        Config.dialog = new WaitingDialog(context);
        String url = "http://en-yummy.strikingly.com/";
        ShareContent content = new ShareContent();
        UMImage umImage = new UMImage(context, BitmapFactory.decodeFile(path));
        content.mMedia = umImage;
        ShareAction umeng = new ShareAction((Activity) context)
                .setPlatform(umeng_platform)
                .setCallback(umShareListener)
                .withMedia(umImage);
        if (umeng_platform == SHARE_MEDIA.QZONE) {
            umeng.withTitle(context.getResources().getString(R.string.app_name))
                    .withTargetUrl(url)
                    .withText(context.getResources().getString(R.string.app_purpose));
        }
        umeng.share();
    }


    /**
     * The Um share listener.
     */
    public UMShareListener umShareListener = new UMShareListener() {
        @Override
        public void onResult(SHARE_MEDIA platform) {
            Log.d("plat", "platform" + platform);
            if (platform.name().equals("WEIXIN_FAVORITE")) {
                Toast.makeText(context, platform + " 收藏成功啦", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, platform + " 分享成功啦", Toast.LENGTH_SHORT).show();
            }

        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            Toast.makeText(context, platform + " 分享失败啦", Toast.LENGTH_SHORT).show();
            if (t != null) {
                Log.d("throw", "throw:" + t.getMessage());
            }

        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
            Toast.makeText(context, platform + " 分享取消了", Toast.LENGTH_SHORT).show();

        }
    };

}
