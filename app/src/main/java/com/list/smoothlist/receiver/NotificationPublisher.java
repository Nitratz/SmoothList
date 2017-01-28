package com.list.smoothlist.receiver;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.RingtoneManager;
import android.os.PowerManager;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.list.smoothlist.R;
import com.list.smoothlist.database.DBManager;
import com.list.smoothlist.model.ToDo;

import java.util.Date;

public class NotificationPublisher extends BroadcastReceiver {

    public static String NOTIFICATION_ID = "notif-id";

    public void onReceive(Context context, Intent intent) {

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification.BigTextStyle big = new Notification.BigTextStyle();
        int id = intent.getIntExtra(NOTIFICATION_ID, 0);
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wakeLock = pm.newWakeLock((PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP), "TAG");
        Log.d("myReceiver", "Receiver new notification for id " + id);

        ToDo todo = DBManager.getInstance(context).getNoteById(id);
        Notification.Builder builder = new Notification.Builder(context);
        int ledColor;
        if (todo != null) {
            big.bigText(todo.getDesc());
            builder.setContentTitle(todo.getTitle());
            builder.setContentText(todo.getDesc());
            builder.setLargeIcon(setLargeIconByLevel(context, todo.getLevelNb()));
            ledColor = setLightByLevel(todo.getLevelNb());
        }
        else {
            builder.setContentTitle(context.getString(R.string.new_notif));
            builder.setContentText(context.getString(R.string.notif_todo));
            builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher));
            ledColor = 0xFF0000FF;
        }
        builder.setStyle(big);
        builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        builder.setSmallIcon(R.mipmap.ic_launcher);

        Notification notification = builder.build();
        notification.ledARGB = ledColor;
        notification.flags = Notification.FLAG_SHOW_LIGHTS;
        notification.defaults |= Notification.DEFAULT_VIBRATE;
        notification.ledOnMS = 200;
        notification.ledOffMS = 500;

        wakeLock.acquire();
        notificationManager.notify(id, notification);
        wakeLock.release();
    }

    public static void scheduleNotification(Context context, long futureInMillis, ToDo todo) {

        Intent notificationIntent = new Intent(context, NotificationPublisher.class);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, todo.getId());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, todo.getId(), notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Log.d("UtilAlarm", "Pending intent is : " + pendingIntent);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, futureInMillis, pendingIntent);
        Log.d("UtilAlarm", "Alarm set for " + new Date(futureInMillis).toString() + " with id : " + todo.getId());
    }

    public static void unScheduleNotification(Context context, ToDo todo) {
        Intent notificationIntent = new Intent(context, NotificationPublisher.class);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, todo.getId());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, todo.getId(), notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
        Log.d("UtilAlarm", "Alarm canceled for id : " + todo.getId());
    }

    private int setLightByLevel(int level) {
        switch (level) {
            case 0:
                return 0xFF00FF00;
            case 1:
                return 0xFFFF2500;
            case 2:
                return 0xFFFF0000;
        }
        return 0xFF0000FF;
    }

    private Bitmap setLargeIconByLevel(Context context, int level) {
        switch (level) {
            case 0:
                return BitmapFactory.decodeResource(context.getResources(), R.drawable.normal);
            case 1:
                return BitmapFactory.decodeResource(context.getResources(), R.drawable.important);
            case 2:
                return BitmapFactory.decodeResource(context.getResources(), R.drawable.vimportant);
        }
        return null;
    }
}
