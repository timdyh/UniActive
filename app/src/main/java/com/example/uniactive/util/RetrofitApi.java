package com.example.uniactive.util;

import android.content.Context;
import android.net.Uri;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitApi {
    public static MultipartBody.Part getMultipartFromUri(Context appContext, String name, Uri uri) {
        // if NullPointerException thrown, try to allow storage permission in system settings
        File f = new File(ResourceUtils.getRealPath(appContext, uri));
        //将文件转化为RequestBody对象
        //需要在表单中进行文件上传时，就需要使用该格式：multipart/form-data
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), f);
        //将文件转化为MultipartBody.Part
        //第一个参数：上传文件的key；第二个参数：文件名；第三个参数：RequestBody对象
        return MultipartBody.Part.createFormData(name, f.getName(), requestFile);
    }

    public static void postPicture(String email, Uri image, Context appContext, Callback<PostPictureResponse> callback) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://114.115.134.188")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        retrofit.create(PictureServive.class).createPicture(email,
                getMultipartFromUri(appContext, "image", image)).enqueue(callback);
    }

    public static void checkNew(String version, Callback<CheckNewResponse> callback) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://114.115.134.188")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        retrofit.create(CheckNewService.class).checkNew(version).enqueue(callback);
    }
}
