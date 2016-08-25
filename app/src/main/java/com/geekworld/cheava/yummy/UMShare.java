package com.geekworld.cheava.yummy;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.Toast;

import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.editorpage.ShareActivity;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.utils.ShareBoardlistener;

import java.util.Map;
import java.util.StringTokenizer;

import cn.pedant.SweetAlert.SweetAlertDialog;
import hugo.weaving.DebugLog;

/**
 * Created by Cheava on 2016/8/18 0018.
 */
public class UMShare {
    private Activity activity;
    private String text;
    private Bitmap bmp;
    private UMShareAPI mShareAPI;
    private SHARE_MEDIA platform ;

    public UMShare(Activity activity){
        this.activity = activity;
        mShareAPI = UMShareAPI.get(activity);
    }

    private void setShareInfo(String text1, Bitmap bmp1){
        this.text = text1;
        this.bmp = bmp1;
    }

    @DebugLog
    public void share(SHARE_MEDIA platform,String text, Bitmap bmp){
        UMImage umimage = new UMImage(activity,bmp);
        new ShareAction(activity)
                .setPlatform(platform)
                .setCallback(umShareListener)
                .withText(text)
                .withMedia(umimage)
                .share();
    }



    private UMShareListener umShareListener = new UMShareListener() {
        @Override
        public void onResult(SHARE_MEDIA platform) {
            Log.d("plat","platform"+platform);
            if(platform.name().equals("WEIXIN_FAVORITE")){
                Toast.makeText(activity,platform + " 收藏成功啦",Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(activity, platform + " 分享成功啦", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            Toast.makeText(activity,platform + " 分享失败啦", Toast.LENGTH_SHORT).show();
            if(t!=null){
                Log.d("throw","throw:"+t.getMessage());
            }
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
            Toast.makeText(activity,platform + " 分享取消了", Toast.LENGTH_SHORT).show();
        }
    };

}
