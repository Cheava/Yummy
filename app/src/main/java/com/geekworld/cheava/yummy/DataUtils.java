package com.geekworld.cheava.yummy;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;

import com.geekworld.cheava.greendao.DaoMaster;
import com.geekworld.cheava.greendao.DaoSession;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * Created by wangzh on 2016/8/4.
 */
public class DataUtils {
    public static DaoMaster daoMaster;
    public static DaoSession daoSession;
    public static SQLiteDatabase db;
    public static DaoMaster.DevOpenHelper helper;


    public DataUtils() {
      /*
      * 通过 DaoMaster 的内部类 DevOpenHelper，你可以得到一个便利的
        SQLiteOpenHelper 对象
      */
        helper = new DaoMaster.DevOpenHelper(BaseApplication.context(), "Content.db", null);
        db = helper.getWritableDatabase();
        daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
    }


    public static DaoSession getDaoSession() {
        return daoSession;
    }

    public static SQLiteDatabase getSqLiteDatabase() {
        return db;
    }

    static public void save(int key, String str) {

    }

    static public void save(int key, Bitmap bmp) {
        FileOutputStream out = null;
        BufferedWriter writer = null;
        try {
            out = openFileOutput("data", Context.MODE_PRIVATE);
            writer = new BufferedWriter(new OutputStreamWriter(out));
            writer.write(bmp);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
