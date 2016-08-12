package com.geekworld.cheava.yummy;


import retrofit2.Call;
import retrofit2.http.GET;

import retrofit2.http.Query;

/**
 * Created by wangzh on 2016/8/2.
 */

public interface WordSite {
    @GET("zhaiyan/taici")
    Call<Word> getResult(@Query("id") String id);
}
