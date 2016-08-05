package com.geekworld.cheava.yummy;

import android.graphics.Bitmap;

import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.Log;


import org.apache.commons.lang3.RandomUtils;

import okhttp3.ResponseBody;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by Cheava on 2016/8/2 0002.
 */
public class NetUtil {
    static Bitmap bitmapAsy;
    static Word wordAsy;
    static Bitmap bitmap;
    static Word word;
    static int word_id = 0;
    static int img_id = 0;
    static String word_site = "https://api.acman.cn/";
    static String img_site = "https://unsplash.it/";

    static ACache img_Acache = ACache.get(BaseApplication.context(), 50, Constants.MAX_IMG);
    static ACache word_Acache = ACache.get(BaseApplication.context(), 50, Constants.MAX_WORD);

    static public Word queryWord(String base_url, int id) {
        //1.创建Retrofit对象
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())//解析方法
                .baseUrl(base_url)//主机地址
                .build();

        //2.创建访问API的请求
        Call<Word> call = retrofit.create(WordSite.class).getResult(Integer.toString(id));
        try {
            word = call.execute().body();
        } catch (Exception e) {
            e.printStackTrace();
            word = null;
        }
        return word;
    }

    static public Bitmap queryImage(String base_url, int id, String width, String height) {
        //1.创建Retrofit对象
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(base_url)//主机地址
                .build();

        //2.创建访问API的请求
        Call<ResponseBody> call = retrofit.create(ImageSite.class).getResult(width, height, Integer.toString(id));
        try {
            ResponseBody responseBody = call.execute().body();
            try {
                byte date[] = responseBody.bytes();
                bitmap = BitmapFactory.decodeByteArray(date, 0, date.length);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
            bitmap = null;
        }
        return bitmap;
    }

    static public void queryWordAsy(String base_url, int id) {
        //1.创建Retrofit对象
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())//解析方法
                .baseUrl(base_url)//主机地址
                .build();

        //2.创建访问API的请求
        Call<Word> call = retrofit.create(WordSite.class).getResult(Integer.toString(id));
        call.enqueue(new Callback<Word>() {
            @Override
            public void onResponse(Call<Word> call, Response<Word> response) {
                //请求成功
                if (response.isSuccess()) {
                    wordAsy = response.body();
                    Message message = new Message();
                    message.what = Constants.DOWN_WORD;
                    // 将服务器返回的结果存放到Message中
                    message.obj = wordAsy;
                    handler.sendMessage(message);
                }
            }

            @Override
            public void onFailure(Call<Word> call, Throwable t) {
                //请求失败
                Log.e("retrofit", "onFailure_queryWordAsy");
            }
        });
    }

    static public void queryImageAsy(String base_url, int id, String width, String height) {
        //1.创建Retrofit对象
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(base_url)//主机地址
                .build();

        //2.创建访问API的请求
        Call<ResponseBody> call = retrofit.create(ImageSite.class).getResult(width, height, Integer.toString(id));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccess()) {
                    ResponseBody responseBody = response.body();
                    try {
                        byte date[] = responseBody.bytes();
                        bitmapAsy = BitmapFactory.decodeByteArray(date, 0, date.length);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    Message message = new Message();
                    message.what = Constants.DOWN_IMG;
                    // 将服务器返回的结果存放到Message中
                    message.obj = bitmapAsy;
                    handler.sendMessage(message);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                //请求失败
                Log.e("retrofit", "onFailure_queryImage");
            }
        });
    }

    static public void downloadWord() {
        int rsi = RandomUtils.nextInt(0, 1000);
        int id = rsi % Constants.MAX_WORD;
        queryWordAsy(word_site, id);
    }

    static public void downloadImg() {
        int rsi = RandomUtils.nextInt(0, 1000);
        int id = rsi % Constants.MAX_IMG;
        int[] screen_size = BaseApplication.getDisplaySize();
        String width = Integer.toString(screen_size[0]);
        String height = Integer.toString(screen_size[1]);
        queryImageAsy(img_site, id, width, height);
    }

    static private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.DOWN_WORD:
                    Word wordJson = (Word) msg.obj;
                    String word = wordJson.getTaici();
                    DataUtils.save(word_id, word);
                    word_id = word_id + 1;
                    if (word_id >= Constants.MAX_WORD) {
                        word_id = 0;
                        BaseApplication.sendLocalBroadcast(Constants.WORD_DOWNLOAD_DONE);
                    } else {
                        BaseApplication.sendLocalBroadcast(Constants.WORD_DOWNLOADING);
                    }
                    break;
                case Constants.DOWN_IMG:
                    Bitmap bmp = (Bitmap) msg.obj;
                    DataUtils.save(img_id, bmp);
                    img_id = img_id + 1;
                    if (img_id >= Constants.MAX_WORD) {
                        BaseApplication.sendLocalBroadcast(Constants.IMG_DOWNLOAD_DONE);

                    } else {
                        BaseApplication.sendLocalBroadcast(Constants.IMG_DOWNLOADING);
                    }
                    break;
            }
        }
    };

    static public int getImgSum() {
        return img_id;
    }

    static public int getWordSum() {
        return word_id;
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
                    img_site = getRealImgSite(id);
                    URL url = new URL(img_site);
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
                    URL url = new URL(word_site);
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
