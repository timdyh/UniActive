package com.example.uniactive.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LabelManager {

    private static final String[] labels = {"博雅", "讲座", "科技", "体育", "音乐", "影视", "电竞",
            "竞赛", "冯如杯", "座谈", "学术", "专业选择", "升学", "留学", "就业"};

    public static List<String> getLabels() {
        return Arrays.asList(labels);
    }

    public static ArrayList<Integer> getLabelIndexes(String... queryLabels) {
        ArrayList<Integer> labelIndexes = new ArrayList<>();
        for (String label : queryLabels) {
            for (int i = 0; i < labels.length; i++) {
                if (label.equals(labels[i])) {
                    labelIndexes.add(i);
                    break;
                }
            }
        }
        return labelIndexes;
    }
}