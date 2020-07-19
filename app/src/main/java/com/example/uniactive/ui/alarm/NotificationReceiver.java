package com.example.uniactive.ui.alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.SyncStateContract;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.uniactive.MainActivity;
import com.example.uniactive.ui.activity.ActDetailActivity;
import com.example.uniactive.ui.schedule.ScheduleFragment;
import com.example.uniactive.util.JSONGenerator;
import com.example.uniactive.util.LabelManager;

import org.json.JSONException;
import org.json.JSONObject;

import static android.content.Context.MODE_PRIVATE;

public class NotificationReceiver extends BroadcastReceiver {

    private SharedPreferences sp;

    @Override
    public void onReceive(Context context, Intent intent) {
        sp = context.getSharedPreferences("userData", MODE_PRIVATE);
        String email = sp.getString("email", "");
        int act_id = intent.getIntExtra("act_id", -1);
        if (SystemUtils.isAppAlive(context, "com.example.uniactive")) {
            Intent mainIntent = new Intent(context, MainActivity.class);
            mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Intent detailIntent = new Intent(context, ActDetailActivity.class);
            GetActivityMessage(email, act_id, context, detailIntent, new VolleyCallback() {
                @Override
                public void onSuccess() {
                    Intent[] intents = {mainIntent, detailIntent};
                    context.startActivities(intents);
                }
            });
        } else {
            Intent launchIntent = context.getPackageManager().
                    getLaunchIntentForPackage("com.example.uniactive");
            launchIntent.setFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            Bundle args = new Bundle();
            launchIntent.putExtra(SyncStateContract.Constants.ACCOUNT_NAME, args);
            Intent detailIntent = new Intent(context, ActDetailActivity.class);
            GetActivityMessage(email, act_id, context, detailIntent, new VolleyCallback() {
                @Override
                public void onSuccess() {
                    Intent[] intents = {launchIntent, detailIntent};
                    context.startActivities(intents);
                }
            });
        }
    }

    private void GetActivityMessage(String email, int act_id, Context context, Intent intent, final VolleyCallback volleyCallback) {
        JSONGenerator.getDetail(email, act_id, context, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    int status = response.getInt("status");
                    if (status == 1) {
                        String act_name = response.getString("name");
                        String holder = response.getString("holder");
                        String holder_email = response.getString("holder_email");
                        long start_time = response.getLong("start_time");
                        long end_time = response.getLong("end_time");
                        int num = response.getInt("max_num");
                        int count = response.getInt("count");
                        String intro = response.getString("introduction");
                        String holderImageUrl = response.getString("img");
                        String actImageUrl = response.getString("img1");
                        String label1 = response.getString("label1");
                        String label2 = response.getString("label2");
                        String label3 = response.getString("label3");

                        intent.putExtra("act_id", act_id);
                        intent.putExtra("name", act_name);
                        intent.putExtra("intro", intro);
                        intent.putExtra("start_time", start_time);
                        intent.putExtra("end_time", end_time);
                        intent.putExtra("max_num", num);
                        intent.putExtra("count", count);
                        intent.putExtra("holderEmail", holder_email);
                        intent.putExtra("holderName", holder);
                        intent.putExtra("actImageUrl", actImageUrl);
                        intent.putExtra("holderImageUrl", holderImageUrl);
                        intent.putIntegerArrayListExtra("actLabelIndexes", LabelManager.getLabelIndexes(label1, label2, label3));

                        volleyCallback.onSuccess();
                    }
                    else if (status == 0) {
                        Toast.makeText(context, "查找活动错误", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(context, "系统错误", Toast.LENGTH_SHORT).show();
                    }
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "网络错误", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public interface VolleyCallback {
        void onSuccess();
    }
}
