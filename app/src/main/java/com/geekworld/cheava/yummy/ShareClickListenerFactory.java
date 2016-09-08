package com.geekworld.cheava.yummy;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.FeatureInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Color;
import android.hardware.Camera;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.orhanobut.logger.Logger;
import com.umeng.socialize.Config;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.ShareContent;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.WeiXinShareContent;

import org.greenrobot.eventbus.EventBus;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by wangzh on 2016/9/6.
 */
public class ShareClickListenerFactory implements View.OnClickListener {
    private Context context;
    private SHARE_MEDIA platform;
    private String path = null;
    private FabMenuFactory.FastTools tools = null;

    private Camera camera = null;
    private boolean isOpen = false;

    public ShareClickListenerFactory(Context context, SHARE_MEDIA platform, String path) {
        this.context = context;
        this.platform = platform;
        this.path = path;
    }

    public ShareClickListenerFactory(Context context, FabMenuFactory.FastTools tools) {
        this.context = context;
        this.tools = tools;
    }


    @Override
    public void onClick(View v) {
        if (tools == null) {
            shareHandler();
            //EventBus.getDefault().post("SHARE_FINISH");
        } else {
            toolsHandler();
        }
    }

    private void toolsHandler() {
        switch (tools) {
            case CAMERA:
                startCommonTools(MediaStore.ACTION_IMAGE_CAPTURE);
                break;
            case PHONE:
                startCommonTools(Intent.ACTION_DIAL);
                break;
            case LIGHT:
                startLightTools();
                break;
            case MESSAGE:
                startMsgTools();
                break;
            default:
                Logger.e("unexpected tools");
                break;
        }
    }

    private void startCommonTools(String tools) {
        Intent intent = new Intent();
        intent.setAction(tools);
        context.startActivity(intent);
    }

    private void startMsgTools() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setType("vnd.android-dir/mms-sms");
        context.startActivity(intent);
    }

    private void startLightTools() {
        if (BaseApplication.isCameraAttain()) {
            openFlashLight();
        } else {
            Toast.makeText(BaseApplication.context(), "没有发现闪光灯", Toast.LENGTH_SHORT).show();
        }
    }

    private void openFlashLight() {
        if (camera == null) {
            camera = Camera.open();
        }
        Camera.Parameters param = camera.getParameters();
        if (!isOpen) {
            // getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            param.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
        } else {
            param.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
        }
        camera.setParameters(param);
        isOpen = !isOpen;
    }

    private void releaseLight() {
        camera.release();
        camera = null;
    }

    private void shareHandler() {
        Config.dialog = getDialog();
        String url = "http://en-yummy.strikingly.com/";
        ShareContent content = new ShareContent();
        UMImage umImage = new UMImage(context, BitmapFactory.decodeFile(path));
        content.mMedia = umImage;
        ShareAction umeng = new ShareAction((Activity) context)
                .setPlatform(platform)
                .setCallback(umShareListener)
                .withMedia(umImage);
        if (platform == SHARE_MEDIA.QZONE) {
            umeng.withTitle(context.getResources().getString(R.string.app_name))
                    .withTargetUrl(url)
                    .withText(context.getResources().getString(R.string.app_purpose));
        }
        umeng.share();
    }

    private SweetAlertDialog getDialog() {
        SweetAlertDialog pDialog = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Loading");
        pDialog.setCancelable(false);
        return pDialog;
    }


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
