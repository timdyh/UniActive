package com.example.uniactive.ui.activity;

public class CommentCard {
    private String name;
    private int rate;
    private String comment;
    private String time;
    private String url;

    public CommentCard(String name, int rate, String comment, String time, String url) {
        this.name = name;
        this.rate = rate;
        this.comment = comment;
        this.time = time;
        this.url = url;
    }

    public String getCom_Name() {
        return name;
    }

    public int getRate() {
        return rate;
    }

    public String getComment() {
        return comment;
    }

    public String getCom_Time() {
        return time;
    }

    public String getUrl() {
        return url;
    }
}
