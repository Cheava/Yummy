package com.geekworld.cheava.yummy.utils;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;

import com.geekworld.cheava.yummy.BaseApplication;
import com.geekworld.cheava.yummy.bean.Screenshot;

import hugo.weaving.DebugLog;


/**
 * The type Image util.
 */
/*
* @class ImageUtil
* @desc  图片处理类
* @author wangzh
*/
public class ImageUtil {
    /**
     * The Context.
     */
    static Context context = BaseApplication.context();
    /**
     * The Screenshot path.
     */
    static String screenshotPath;

    /**
     * The Msc.
     */
    static MediaScannerConnection msc = new MediaScannerConnection(context, new MediaScannerConnection.MediaScannerConnectionClient() {
        public void onMediaScannerConnected() {
            msc.scanFile(screenshotPath, "image/jpeg");
        }
        public void onScanCompleted(String path, Uri uri) {
            msc.disconnect();
        }
    });

    /**
     * Screen shot string.
     *
     * @param activity the activity
     * @return the string
     */
    @DebugLog
    public static Screenshot screenShot(Activity activity){
        //生成相同大小的图片
/*        Bitmap tempBmp = Bitmap.createBitmap(
                CacheUtil.getScreenWidth(),
                CacheUtil.getScreenHeight(),
                Bitmap.Config.RGB_565 );*/
        //找到当前页面的跟布局
        Screenshot screenshot = new Screenshot();
        View view =  activity.getWindow().getDecorView().getRootView();
        //设置缓存
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        //从缓存中获取当前屏幕的图片
        Bitmap tempBmp = view.getDrawingCache();
        String str = MediaStore.Images.Media.insertImage(context.getContentResolver(),tempBmp,null, null);
        screenshot.setUriString(str);
        Uri uri = Uri.parse(str);
        screenshotPath = BaseApplication.getRealFilePath(context,uri);
        screenshot.setImgPath(screenshotPath);
        //将图片显示在系统相册中
        msc.connect();
        view.destroyDrawingCache();
        return screenshot;
    }

    /**
     * Get thumb bitmap.
     *
     * @param resid the resid
     * @return the bitmap
     */
    public static Bitmap getThumb(int resid){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(context.getResources(),resid,options);

        options.inJustDecodeBounds = false;
        options.inDither=false;    /*不进行图片抖动处理*/
        options.inPreferredConfig=null;  /*设置让解码器以最佳方式解码*/
        /* 下面两个字段需要组合使用 */
        options.inPurgeable = true;
        options.inInputShareable = true;
        Bitmap bmp = BitmapFactory.decodeResource(context.getResources(),resid,options);
        return bmp;
    }

    /**
     * Get thumb bitmap.
     *
     * @param path  the path
     * @param scale the scale
     * @return the bitmap
     */
    public static Bitmap getThumb(String path,int scale){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        options.inSampleSize = scale;
        options.inJustDecodeBounds = false;
        options.inDither=false;    /*不进行图片抖动处理*/
        options.inPreferredConfig=null;  /*设置让解码器以最佳方式解码*/
        /* 下面两个字段需要组合使用 */
        options.inPurgeable = true;
        options.inInputShareable = true;

        Bitmap bmp = BitmapFactory.decodeFile(path, options);
        return bmp;
    }

}
