package com.example.uniactive.util;

import com.google.gson.annotations.SerializedName;

public class PostPictureResponse {

    @SerializedName("url")   private String url;
    @SerializedName("success")   private boolean success;

    public String getUrl() {
        return url;
    }

    public boolean isSuccess() {
        return success;
    }
}
