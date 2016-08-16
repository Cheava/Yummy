package com.geekworld.cheava.yummy;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;

/**
 * Created by Cheava on 2016/8/14 0014.
 */
public class ImageUtil {

    static String screenshotPath;

    static MediaScannerConnection msc = new MediaScannerConnection(BaseApplication.context(), new MediaScannerConnection.MediaScannerConnectionClient() {
        public void onMediaScannerConnected() {
            msc.scanFile(screenshotPath, "image/jpeg");
        }
        public void onScanCompleted(String path, Uri uri) {
            msc.disconnect();
        }
    });

    public static String screenShot(Activity activity){
        //生成相同大小的图片
/*        Bitmap tempBmp = Bitmap.createBitmap(
                BaseApplication.getScreenWidth(),
                BaseApplication.getScreenHeight(),
                Bitmap.Config.RGB_565 );*/
        //找到当前页面的跟布局
        View view =  activity.getWindow().getDecorView().getRootView();
        //设置缓存
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        //从缓存中获取当前屏幕的图片
        Bitmap tempBmp = view.getDrawingCache();
        String str = MediaStore.Images.Media.insertImage(BaseApplication.context().getContentResolver(),tempBmp,null, null);
        Uri uri = Uri.parse(str);
        screenshotPath = getFilePathByContentResolver(BaseApplication.context(),uri);
        msc.connect();
        return screenshotPath;
    }

    /**
     * 根据Uri获取图片路径
     * @param context
     * @param uri
     * @return
     */
    public static String getFilePathByContentResolver(Context context, Uri uri) {
        if (null == uri) {
            return null;
        }
        Cursor c = context.getContentResolver().query(uri, null, null, null, null);
        String filePath  = null;
        if (null == c) {
            throw new IllegalArgumentException(
                    "Query on " + uri + " returns null result.");
        }
        try {
            if ((c.getCount() != 1) || !c.moveToFirst()) {
            } else {
                filePath = c.getString(c.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA));
            }
        } finally {
            c.close();
        }
        return filePath;
    }

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
