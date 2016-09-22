package com.geekworld.cheava.yummy.bean;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/*
* @class ImageSite
* @desc  网络图片接口
* @author wangzh
*/

public interface ImageSite {
    @GET("{screen_width}/{screen_height}/")
    Call<ResponseBody> getResult(@Path("screen_width") String screen_width, @Path("screen_height") String screen_height, @Query("image") String id);
}
