package com.example.uniactive.ui.alarm;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;

import com.example.uniactive.R;

import static android.content.Context.MODE_PRIVATE;
import static android.content.Context.NOTIFICATION_SERVICE;

public class AlarmReceiver extends BroadcastReceiver {

    private SharedPreferences sp;

    @Override
    public void onReceive(Context context, Intent intent) {
        //设置通知内容并在onReceive()这个函数执行时开启
        String name = intent.getStringExtra("name");
        System.out.println(name);
        int act_id = intent.getIntExtra("act_to", -1);
        int id = intent.getIntExtra("id", -1);

        Intent broadcastIntent = new Intent(context, NotificationReceiver.class);
        broadcastIntent.putExtra("act_id", act_id);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, broadcastIntent, PendingIntent.FLAG_UPDATE_CURRENT);


        sp = context.getSharedPreferences("userData", MODE_PRIVATE);
        NotificationManager manager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        String Channelid = "channel_001";
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        Notification notification = null;
        Notification.Builder mBuilder = new Notification.Builder(context);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(Channelid, name, NotificationManager.IMPORTANCE_LOW);
            notificationManager.createNotificationChannel(mChannel);
            mBuilder.setChannelId(Channelid)
                    .setContentTitle(name)
                    .setContentText("活动即将于10分钟内开始") //内容
                    .setSubText("你有参与的活动即将开始") //内容下面的一小段文字
                    .setTicker("收到活动提醒")   //收到信息后状态显示的文字信息
                    .setWhen(System.currentTimeMillis()) //设置通知时间
                    .setSmallIcon(R.mipmap.ic_launcher) //设置小图片
                    .setAutoCancel(true);
        }
        else {
            mBuilder.setContentTitle(name)
                    .setContentText("活动即将于10分钟内开始") //内容
                    .setSubText("你有参与的活动即将开始") //内容下面的一小段文字
                    .setTicker("收到活动提醒")   //收到信息后状态显示的文字信息
                    .setWhen(System.currentTimeMillis()) //设置通知时间
                    .setSmallIcon(R.mipmap.ic_launcher) //设置小图片
                    .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE) //设置默认的三色灯与振动器
                    .setAutoCancel(true); //设置点击后取消Notification
        }
        mBuilder.setContentIntent(pendingIntent);
        notification = mBuilder.build();
        notification.defaults = Notification.DEFAULT_ALL;
        manager.notify(1, notification);
        RefreshSp(id);
    }

    private void RefreshSp(int i) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("status_act_" + i, 2);
        editor.apply();
    }
}



