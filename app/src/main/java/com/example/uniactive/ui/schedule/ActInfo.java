package com.example.uniactive.ui.schedule;

import com.example.uniactive.util.LabelManager;

import java.util.ArrayList;
import java.util.List;

public class ActInfo {

    private int actId;
    private String place;
    private String holderEmail;
    private String holderName;
    private String holderImageUrl;
    private long startTime;
    private long endTime;
    private String actName;
    private String actIntro;
    private String actImageUrl;
    private int maxNum;
    private int count;
    private ArrayList<Integer> actLabelIndexes;

    public ActInfo(int actId, String place, String holderEmail, String holderName, String holderImageUrl,
                   long startTime, long endTime, int maxNum, int count,
                   String actName, String actIntro, String actImageUrl,
                   String label1, String label2, String label3) {
        this.actId = actId;
        this.place = place;
        this.holderEmail = holderEmail;
        this.holderName = holderName;
        this.holderImageUrl = holderImageUrl;
        this.startTime = startTime;
        this.endTime = endTime;
        this.maxNum = maxNum;
        this.count = count;
        this.actName = actName;
        this.actIntro = actIntro;
        this.actImageUrl = actImageUrl;
        this.actLabelIndexes = LabelManager.getLabelIndexes(label1, label2, label3);
    }

    public ArrayList<Integer> getActLabelIndexes() {
        return actLabelIndexes;
    }

    public int getActId() {
        return actId;
    }

    public String getPlace() {
        return place;
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

    public String getActImageUrl() {
        return actImageUrl;
    }
}
