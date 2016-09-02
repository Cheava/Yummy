package com.geekworld.cheava.yummy;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;

import com.geekworld.cheava.greendao.DaoMaster;
import com.geekworld.cheava.greendao.DaoSession;
import com.geekworld.cheava.greendao.ScreenContent;
import com.geekworld.cheava.greendao.ScreenContentDao;
import com.geekworld.cheava.greendao.ScreenImage;
import com.geekworld.cheava.greendao.ScreenImageDao;
import com.orhanobut.logger.Logger;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import de.greenrobot.dao.async.AsyncSession;
import de.greenrobot.dao.query.QueryBuilder;
import hugo.weaving.DebugLog;

/**
 * Created by wangzh on 2016/8/4.
 */
public class DataUtils {
    static Context context = BaseApplication.context();
    static DaoSession daoSession = BaseApplication.getDaoSession();
    static ScreenContentDao contentDao = BaseApplication.getScreenContentDao();
    static ScreenImageDao imageDao = BaseApplication.getScreenImageDao();

    static public Boolean isWordExist(int key) {
        QueryBuilder queryBuilder = contentDao.queryBuilder();
        queryBuilder.where(ScreenContentDao.Properties.Id.eq((long) key));
        return (queryBuilder.buildCount().count() > 0);
    }

    static public Boolean isImageExist(int key) {
        QueryBuilder queryBuilder = imageDao.queryBuilder();
        queryBuilder.where(ScreenImageDao.Properties.Id.eq((long) key));
        return (queryBuilder.buildCount().count() > 0);
    }

    static public int getWordSum(){
        QueryBuilder queryBuilder = contentDao.queryBuilder();
        Long result = queryBuilder.buildCount().count();
        //Logger.i(Long.toString(result));
        return result.intValue();
    }

    static public int getImgSum(){
        QueryBuilder queryBuilder = imageDao.queryBuilder();
        Long result = queryBuilder.buildCount().count();
        //Logger.i(Long.toString(result));
        return result.intValue();
    }

    @DebugLog
    static public void save(int key, String str) {
        try {
            if (str != null) {
                ScreenContent content = new ScreenContent((long) key, str);
                if (isWordExist(key)) {
                    contentDao.update(content);
                } else {
                    contentDao.insert(content);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static public void save(int key, Bitmap bmp) {
        if(saveImg(key,bmp)){
            ScreenImage image = new ScreenImage((long) key, BaseApplication.getImgPath(key));
            if (isImageExist(key)) {
                imageDao.update(image);
            } else {
                imageDao.insert(image);
            }
        }
    }

    @DebugLog
    static public String loadWord(int key) {
        String result = null;
        if (isWordExist(key)) {
            ScreenContent screenContent = contentDao.load((long) key);
            try {
                result = screenContent.getWord();
                Logger.i(result);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    static public String loadImage(int key) {
        String result = null;
        if (isImageExist(key)) {
            ScreenImage screenImage = imageDao.load((long) key);
            try {
                result = screenImage.getImagepath();
                Logger.i(result);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    static public boolean saveImg(int key, Bitmap bmp) {
        if (bmp == null) return false;
        FileOutputStream writer = null;
        try {
            writer = context.openFileOutput(BaseApplication.getImgName(key), Context.MODE_PRIVATE);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, writer);
            writer.flush();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
    }

}
