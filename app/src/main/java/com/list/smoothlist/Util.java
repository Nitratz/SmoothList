package com.list.smoothlist;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.SystemClock;
import android.util.Log;

import com.list.smoothlist.activity.MainActivity;
import com.list.smoothlist.model.ToDo;
import com.list.smoothlist.receiver.NotificationPublisher;

import java.util.Date;

public class Util {

    public static void scheduleNotification(Context context, long futureInMillis, ToDo todo) {

        Notification.Builder builder = new Notification.Builder(context);
        builder.setContentTitle(todo.getTitle());
        builder.setContentText(todo.getDesc());
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher));
        Notification notification = builder.build();
        notification.flags |= Notification.FLAG_SHOW_LIGHTS;
        notification.defaults |= Notification.DEFAULT_VIBRATE;
        notification.ledARGB = 0xff00ff00;
        notification.ledOnMS = 300;
        notification.ledOffMS = 1000;

        Intent notificationIntent = new Intent(context, NotificationPublisher.class);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, todo.getId());
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION, notification);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, todo.getId(), notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        builder.setContentIntent(pendingIntent);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 10000, pendingIntent);
        Log.d("UtilAlarm", "Alarm set for " + new Date(futureInMillis).toString());
    }

    public static void unScheduleNotification(Context context, ToDo todo) {
        Intent notificationIntent = new Intent(context, NotificationPublisher.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, todo.getId(), notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }
}
