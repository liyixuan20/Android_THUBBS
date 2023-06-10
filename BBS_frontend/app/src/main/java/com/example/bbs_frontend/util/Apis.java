package com.example.bbs_frontend.util;

import okhttp3.FormBody;
import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface Apis {
    @POST("/post/init/")
    Call<ResponseBody> upload(@Body MultipartBody file);

    @POST("/draft/init/")
    Call<ResponseBody> uploadDraft(@Body FormBody body);

    @POST("/draft/edit/")
    Call<ResponseBody> editDraft(@Body FormBody body);
}
