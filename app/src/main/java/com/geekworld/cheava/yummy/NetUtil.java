package com.geekworld.cheava.yummy;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Cheava on 2016/8/2 0002.
 */
public class NetUtil {
    String wordsite = "https://api.acman.cn/zhaiyan/taici";
    String imgsite;
    String web_res = "Hello World";

    public String getRealImgSite(){
        int [] screensize = BaseApplication.getDisplaySize();
        String url = "https://unsplash.it/"+screensize[0]+"/"+screensize[1]+"/"+"?random";
        Log.d("getRealImgSite",url);
        return url;
    }

    public void getImgFromNet() {
        // 开启线程来发起网络请求
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                try {
                    imgsite = getRealImgSite();
                    URL url = new URL(imgsite);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(10000);
                    connection.setReadTimeout(10000);
                    connection.setDoInput(true);
                    int response_code = connection.getResponseCode();
                    if(response_code == 200){
                        InputStream in = connection.getInputStream();
                        Bitmap bmp  = BitmapFactory.decodeStream(in);

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (connection != null) {
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }


    public void getWordFromNet() {
        // 开启线程来发起网络请求
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                try {
                    URL url = new URL(wordsite);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    connection.setDoInput(true);
                    InputStream in = connection.getInputStream();
                    // 下面对获取到的输入流进行读取
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    String result =  parseJSONWithJSONObject(response.toString());

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (connection != null) {
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }

    private String  parseJSONWithJSONObject(String jsonData) {
        try {
            JSONObject jsonObject  = new JSONObject(jsonData);
            return jsonObject.getString("taici");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
