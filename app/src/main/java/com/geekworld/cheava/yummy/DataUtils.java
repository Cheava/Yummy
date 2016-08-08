package com.geekworld.cheava.yummy;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.util.Log;

import com.geekworld.cheava.greendao.DaoMaster;
import com.geekworld.cheava.greendao.DaoSession;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * Created by wangzh on 2016/8/4.
 */
public class DataUtils {
    static Context context = BaseApplication.context();

    static public void save(int key, String str) {

    }

    static public void save(int key, Bitmap bmp) {
        if(saveImg(key,bmp)){
            BaseApplication.getImgPath(key);
        }
    }

    static public boolean saveImg(int key, Bitmap bmp) {
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

    static public void deleteImg(int key){
        context.deleteFile(BaseApplication.getImgName(key));
    }
}
