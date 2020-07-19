package com.example.uniactive.util;

import com.google.gson.annotations.SerializedName;

public class CheckNewResponse {
    @SerializedName("url")   private String url;
    @SerializedName("success")   private boolean success;
    @SerializedName("message")   private String message;

    public String getUrl() {
        return url;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }
}