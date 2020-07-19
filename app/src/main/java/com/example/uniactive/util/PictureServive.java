package com.example.uniactive.util;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface PictureServive {
    @Multipart
    @POST("api/post_picture/")
    Call<PostPictureResponse>
    createPicture(
            @Query("email") String email,
            @Part MultipartBody.Part image
    );
}
