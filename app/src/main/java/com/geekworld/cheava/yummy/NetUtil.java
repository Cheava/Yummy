package com.geekworld.cheava.yummy;

import android.graphics.Bitmap;

import android.os.Message;
import android.util.Log;

import com.google.gson.Gson;


import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by Cheava on 2016/8/2 0002.
 */
public class NetUtil {
    String wordsite = "https://api.acman.cn/";
    String imgsite = "https://unsplash.it/";
    String web_res = "Hello World";

    public void queryWord(String id) {
        //1.创建Retrofit对象
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())//解析方法
                .baseUrl(wordsite)//主机地址
                .build();

        //2.创建访问API的请求
        Call<WordJson> call = retrofit.create(Word.class).getResult(id);
        call.enqueue(new Callback<WordJson>() {
            @Override
            public void onResponse(Call<WordJson> call, Response<WordJson> response) {
                //请求成功
                if (response.isSuccess()) {
                    WordJson result = response.body();
                    if (result != null) {
                        String word = result.getTaici();
                    }
                }
            }

            @Override
            public void onFailure(Call<WordJson> call, Throwable t) {
                //请求失败
                Log.e("retrofit", "onFailure_queryWord");
            }
        });
    }

    public void queryImage(String id) {
        int[] screensize = BaseApplication.getDisplaySize();
        String width = Integer.toString(screensize[0]);
        String height = Integer.toString(screensize[1]);
        //1.创建Retrofit对象
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(imgsite)//主机地址
                .build();

        //2.创建访问API的请求
        Call<Bitmap> call = retrofit.create(Image.class).getResult(width, height, id);
        call.enqueue(new Callback<Bitmap>() {
            @Override
            public void onResponse(Call<Bitmap> call, Response<Bitmap> response) {
                if (response.isSuccess()) {
                    Bitmap result = response.body();
                }
            }

            @Override
            public void onFailure(Call<Bitmap> call, Throwable t) {
                //请求失败
                Log.e("retrofit", "onFailure_queryImage");
            }
        });
    }


/*
    public String getRealImgSite(String id){
        int [] screensize = BaseApplication.getDisplaySize();
        String url = "https://unsplash.it/"+screensize[0]+"/"+screensize[1]+"/"+"?image="+id;
        Log.d("getRealImgSite",url);
        return url;
    }

    public void getImgFromNet(String id) {
        // 开启线程来发起网络请求
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                try {
                    imgsite = getRealImgSite(id);
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
                        Message message = new Message();
                        message.what = Constants.SHOW_IMG;
                        // 将服务器返回的结果存放到Message中
                        message.obj = bmp;
                        handler.sendMessage(message);
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
                    Message message = new Message();
                    message.what = Constants.SHOW_WORD;
                    // 将服务器返回的结果存放到Message中
                    message.obj = result;
                    Log.d("getWordFromNet",result);
                    handler.sendMessage(message);
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
*/
}
