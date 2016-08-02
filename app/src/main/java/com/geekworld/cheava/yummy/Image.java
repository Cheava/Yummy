package com.geekworld.cheava.yummy;

import android.graphics.Bitmap;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by wangzh on 2016/8/2.
 */

public interface Image {
    @GET("{screen_width}/{screen_height}/{image}")
    Call<Bitmap> getResult(@Path("screen_width") String screen_width, @Path("screen_height") String screen_height, @Query("image") String id);
}
