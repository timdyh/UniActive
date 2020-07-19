package com.example.uniactive.ui.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.provider.Settings;

import java.util.ArrayList;

import androidx.annotation.Nullable;


public class SendNotificationActivity extends Service {

    private SharedPreferences sp;
    public static final String ACTION = "android.intent.action.RESPOND_VIA_MESSAGE";
    private int length;
    private ArrayList<Integer> ids;
    private ArrayList<Integer> statuses;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        sp = getSharedPreferences("userData", MODE_PRIVATE);
        length = sp.getInt("num_act_to_start", -1);
        ids = new ArrayList<>();
        statuses = new ArrayList<>();

        for (int j = 0; j < length; j++) {
            int status = sp.getInt("status_act_" + j, 1);
            String act = sp.getString("act_" + j, "");
            int act_id = sp.getInt("id_act_" + j, -1);
            ids.add(act_id);

            if (status == 1 || status == -1 || status == 2) {
                statuses.add(status);
                continue;
            }

            if (status == -2) {
                Intent i = new Intent(this, AlarmReceiver.class);
                PendingIntent pi = PendingIntent.getBroadcast(this, act_id, i, 0);
                manager.cancel(pi);
                CancelSp(j);
                continue;
            }

            String[] factors = act.split(" ", 2);
            long triggerAtTime = Long.parseLong(factors[0]) - 10 * 60 * 1000;
            String name = factors[1];
            RefreshSp(j);
            Intent i = new Intent(this, AlarmReceiver.class);
            i.putExtra("name", name);
            i.putExtra("act_to", act_id);
            i.putExtra("id", j);
            PendingIntent pi = PendingIntent.getBroadcast(this, j, i, 0);
            manager.set(AlarmManager.RTC_WAKEUP, triggerAtTime, pi);
        }

        return super.onStartCommand(intent, flags, startId);
    }

    private void RefreshSp(int j) {
        statuses.add(1);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("status_act_" + j, 1);
        editor.apply();
    }

    private  void CancelSp(int j) {
        statuses.add(-1);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("status_act_" + j, -1);
        editor.apply();
    }

    @Override
    public void onDestroy() {
        SendNotificationActivity.this.stopSelf();
        super.onDestroy();
        //在Service结束后关闭AlarmManager
        sp = getSharedPreferences("userData", MODE_PRIVATE);
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent i = new Intent(this, AlarmReceiver.class);
        for (int j = 0; j < length; j++) {
            int status = statuses.get(j);
            if (status != 1 && status != -2) {
                continue;
            }
            int act_id = ids.get(j);
            PendingIntent pi = PendingIntent.getBroadcast(this, act_id, i, 0);
            manager.cancel(pi);
        }
    }

}
