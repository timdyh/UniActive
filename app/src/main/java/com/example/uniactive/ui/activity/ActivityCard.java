package com.example.uniactive.ui.activity;

import com.example.uniactive.util.LabelManager;

import java.util.ArrayList;
import java.util.List;

public class ActivityCard {

    private int actId;
    private String holderEmail;
    private String holderName;
    private String holderImageUrl;
    private long startTime;
    private long endTime;
    private String reject;
    private String actName;
    private String actIntro;
    private String actPlace;
    private String actImageUrl;
    private int maxNum;
    private int count;
    private int score = -1;
    private String comment = "";
    private int actStatus = 0;
    private ArrayList<Integer> actLabelIndexes;

    public ActivityCard(int actId, String holderEmail, String holderName,
                        String holderImageUrl, long startTime, long endTime,
                        int maxNum, int count, String actName, String actIntro,
                        String actPlace, String actImageUrl, String label1, String label2,
                        String label3) {
        this.actId = actId;
        this.holderEmail = holderEmail;
        this.holderName = holderName;
        this.holderImageUrl = holderImageUrl;
        this.startTime = startTime;
        this.endTime = endTime;
        this.maxNum = maxNum;
        this.count = count;
        this.actName = actName;
        this.actIntro = actIntro;
        this.actPlace = actPlace;
        this.actImageUrl = actImageUrl;
        this.actLabelIndexes = LabelManager.getLabelIndexes(label1, label2, label3);
    }

    // 我参与的活动列表，还需要知道是否已评价过
    public ActivityCard(int actId, String holderEmail, String holderName,
                        String holderImageUrl, long startTime, long endTime,
                        int maxNum, int count, String actName, String actIntro,
                        String actPlace, String actImageUrl, String label1, String label2,
                        String label3, int score, String comment) {
        this.actId = actId;
        this.holderEmail = holderEmail;
        this.holderName = holderName;
        this.holderImageUrl = holderImageUrl;
        this.startTime = startTime;
        this.endTime = endTime;
        this.maxNum = maxNum;
        this.count = count;
        this.actName = actName;
        this.actIntro = actIntro;
        this.actPlace = actPlace;
        this.actImageUrl = actImageUrl;
        this.score = score;
        this.comment = comment;
        this.actLabelIndexes = LabelManager.getLabelIndexes(label1, label2, label3);
    }

    // 我发布的活动列表，还需要知道活动审核状态
    public ActivityCard(int actId, String holderEmail, String holderName,
                        String holderImageUrl, long startTime, long endTime,
                        String reject,
                        int maxNum, int count, String actName, String actIntro,
                        String actPlace, String actImageUrl, String label1, String label2,
                        String label3, int actStatus) {
        this.actId = actId;
        this.holderEmail = holderEmail;
        this.holderName = holderName;
        this.holderImageUrl = holderImageUrl;
        this.startTime = startTime;
        this.endTime = endTime;
        this.reject = reject;
        this.maxNum = maxNum;
        this.count = count;
        this.actName = actName;
        this.actIntro = actIntro;
        this.actPlace = actPlace;
        this.actImageUrl = actImageUrl;
        this.actStatus = actStatus;
        this.actLabelIndexes = LabelManager.getLabelIndexes(label1, label2, label3);
    }

    public ArrayList<Integer> getActLabelIndexes() {
        return actLabelIndexes;
    }

    public int getActId() {
        return actId;
    }

    public String getHolderEmail() {
        return holderEmail;
    }

    public String getHolderName() {
        return holderName;
    }

    public String getHolderImageUrl() {
        return holderImageUrl;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public String getReject() {
        return reject;
    }

    public int getMaxNum() {
        return maxNum;
    }

    public int getCount() {
        return count;
    }

    public String getActName() {
        return actName;
    }

    public String getActIntro() {
        return actIntro;
    }

    public String getActPlace() {
        return actPlace;
    }

    public String getActImageUrl() {
        return actImageUrl;
    }

    public int getScore() {
        return score;
    }

    public String getComment() {
        return comment;
    }

    public int getActStatus() {
        return actStatus;
    }
}
