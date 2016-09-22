package com.geekworld.cheava.yummy.share;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import com.geekworld.cheava.yummy.BaseApplication;
import com.geekworld.cheava.yummy.R;
import com.geekworld.cheava.yummy.utils.CacheUtil;
import com.geekworld.cheava.yummy.view.FabMenuFactory;
import com.orhanobut.logger.Logger;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.io.File;
import java.util.List;

/**
 * Created by Cheava on 2016/9/15 0015.
 */
public class SimpleShare {
    private enum SHARE_TYPE{
        TEXT,IMAGE
    }
    Context context;
    FabMenuFactory.SharePlatform platform;
    String path;
    Uri uri;

    public SimpleShare(Context context, FabMenuFactory.SharePlatform platform, String path) {
        this.context = context;
        this.platform = platform;
        this.path = path;
    }

    public SimpleShare(Context context, FabMenuFactory.SharePlatform platform, Uri uri) {
        this.context = context;
        this.platform = platform;
        this.uri = uri;
    }

    public void shareImage(){
        switch (platform) {
            case WEIXIN:
                shareWeChatFriend(null,null,SHARE_TYPE.IMAGE,uri);
                break;
            case WEIXIN_CIRCLE:
                shareWeChatFriendCircle(null,null,SHARE_TYPE.IMAGE,uri);
                break;
            case QQ:
                shareQQFriend(null,null,SHARE_TYPE.IMAGE,uri);
                break;
            case QZONE:
                shareQzone(context.getString(R.string.app_name),
                        context.getString(R.string.app_purpose),
                        SHARE_TYPE.IMAGE,uri);
                break;
            case SINA:
                shareSina(null,null,SHARE_TYPE.IMAGE,uri);
                break;
            default:
                Logger.e("unknown platform");
                break;
        }
    }
    
    
    public void shareQQFriend(String msgTitle, String msgText, SHARE_TYPE type,
                              Uri uri) {

        shareMsg("com.tencent.mobileqq",
                "com.tencent.mobileqq.activity.JumpActivity", "QQ", msgTitle,
                msgText, type, uri);
    }

    public void shareQzone(String msgTitle, String msgText, SHARE_TYPE type,
                              Uri uri) {
        new UmengShare(context, SHARE_MEDIA.QZONE, BaseApplication.getRealFilePath(context,uri)).shareImage();
    }
    
    public void shareWeChatFriend(String msgTitle, String msgText, SHARE_TYPE type,
                                  Uri uri) {

        shareMsg("com.tencent.mm", "com.tencent.mm.ui.tools.ShareImgUI", "微信",
                msgTitle, msgText, type, uri);
    }

    public void shareWeChatFriendCircle(String msgTitle, String msgText,SHARE_TYPE type,Uri uri) {
        shareMsg("com.tencent.mm", "com.tencent.mm.ui.tools.ShareToTimeLineUI",
                "微信", msgTitle, msgText, type, uri);
    }

    public void shareSina(String msgTitle, String msgText,SHARE_TYPE type, Uri uri) {

        shareMsg("com.sina.weibo", "com.sina.weibo.composerinde.ComposerDispatchActivity",
                "新浪微博", msgTitle, msgText, type, uri);
    }

    @SuppressLint("NewApi")
    private void shareMsg(String packageName, String activityName,
                          String appname, String msgTitle, String msgText, SHARE_TYPE type,
                          Uri uri) {
        if (!packageName.isEmpty() && !isAppInstalled(context, packageName)) {// 判断APP是否存在
            Toast.makeText(context, "请先安装" + appname, Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        Intent intent = new Intent("android.intent.action.SEND");
        if (type == SHARE_TYPE.TEXT) {
            intent.setType("text/plain");
        } else if (type == SHARE_TYPE.IMAGE) {
            intent.setType("image/jpeg");
            intent.putExtra(Intent.EXTRA_STREAM, uri);
        }

        intent.putExtra(Intent.EXTRA_SUBJECT, msgTitle);
        intent.putExtra(Intent.EXTRA_TEXT, msgText);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        if (!packageName.isEmpty()) {
            if(activityName!= null){
                intent.setComponent(new ComponentName(packageName, activityName));
            }else{
                intent.setPackage(packageName);
            }
            context.startActivity(intent);
        } else {
            context.startActivity(Intent.createChooser(intent, msgTitle));
        }
    }
    
    public boolean isAppInstalled(Context context, String packageName) {
        PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        for (int i = 0; i < pinfo.size(); i++) {
            if (((PackageInfo) pinfo.get(i)).packageName
                    .equalsIgnoreCase(packageName))
                return true;
        }
        return false;
    }
}
