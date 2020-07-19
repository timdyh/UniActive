package com.example.uniactive.util;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface CheckNewService {
    @GET("api/check_new/")
    Call<CheckNewResponse>
    checkNew(
            @Query("version") String version
    );
}
