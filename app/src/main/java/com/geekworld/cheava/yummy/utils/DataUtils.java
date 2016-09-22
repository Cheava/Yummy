package com.geekworld.cheava.yummy.utils;

import android.content.Context;
import android.graphics.Bitmap;

import com.geekworld.cheava.greendao.DaoSession;
import com.geekworld.cheava.greendao.ScreenContent;
import com.geekworld.cheava.greendao.ScreenContentDao;
import com.geekworld.cheava.greendao.ScreenImage;
import com.geekworld.cheava.greendao.ScreenImageDao;
import com.geekworld.cheava.yummy.BaseApplication;
import com.orhanobut.logger.Logger;

import java.io.FileOutputStream;
import java.io.IOException;

import de.greenrobot.dao.query.QueryBuilder;
import hugo.weaving.DebugLog;

/**
 * The type Data utils.
 */
/*
* @class DataUtils
* @desc  本地数据缓存工具类
* @author wangzh
*/
public class DataUtils {
    /**
     * The Context.
     */
    static Context context = BaseApplication.context();
    /**
     * The Dao session.
     */
    static DaoSession daoSession = BaseApplication.getDaoSession();
    /**
     * The Content dao.
     */
    static ScreenContentDao contentDao = BaseApplication.getScreenContentDao();
    /**
     * The Image dao.
     */
    static ScreenImageDao imageDao = BaseApplication.getScreenImageDao();

    /**
     * Is word exist boolean.
     *
     * @param key the key
     * @return the boolean
     */
    static public Boolean isWordExist(int key) {
        QueryBuilder queryBuilder = contentDao.queryBuilder();
        queryBuilder.where(ScreenContentDao.Properties.Id.eq((long) key));
        return (queryBuilder.buildCount().count() > 0);
    }

    /**
     * Is image exist boolean.
     *
     * @param key the key
     * @return the boolean
     */
    static public Boolean isImageExist(int key) {
        QueryBuilder queryBuilder = imageDao.queryBuilder();
        queryBuilder.where(ScreenImageDao.Properties.Id.eq((long) key));
        return (queryBuilder.buildCount().count() > 0);
    }

    /**
     * Get word sum int.
     *
     * @return the int
     */
    static public int getWordSum(){
        QueryBuilder queryBuilder = contentDao.queryBuilder();
        Long result = queryBuilder.buildCount().count();
        //Logger.i(Long.toString(result));
        return result.intValue();
    }

    /**
     * Get img sum int.
     *
     * @return the int
     */
    static public int getImgSum(){
        QueryBuilder queryBuilder = imageDao.queryBuilder();
        Long result = queryBuilder.buildCount().count();
        //Logger.i(Long.toString(result));
        return result.intValue();
    }

    /**
     * Save.
     * 保存文字
     * @param key the key
     * @param str the str
     */
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

    /**
     * Save.
     * 保存图片（路径）
     * @param key the key
     * @param bmp the bmp
     */
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

    /**
     * Load word string.
     *
     * @param key the key
     * @return the string
     */
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

    /**
     * Load image string.
     *
     * @param key the key
     * @return the string
     */
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

    /**
     * Save img boolean.
     * 缓存图片到本地
     * @param key the key
     * @param bmp the bmp
     * @return the boolean
     */
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
