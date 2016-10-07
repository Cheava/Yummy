package com.geekworld.cheava.yummy.bean;


import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/*
* @class WordSite
* @desc  网络语录接口
* @author wangzh
*/

public interface WordSite {
    @GET("api/json")
    Call<Word> getResult(@Query("id") String id);
}
