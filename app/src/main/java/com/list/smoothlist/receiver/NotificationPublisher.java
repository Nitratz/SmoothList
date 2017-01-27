package com.list.smoothlist.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.PowerManager;
import android.util.Log;

import com.list.smoothlist.R;
import com.list.smoothlist.database.DBManager;
import com.list.smoothlist.model.ToDo;

public class NotificationPublisher extends BroadcastReceiver {

    public static String NOTIFICATION_ID = "notif-id";
    public static String NOTIFICATION = "notif";

    public void onReceive(Context context, Intent intent) {

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        int id = intent.getIntExtra(NOTIFICATION_ID, 0);
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wakeLock = pm.newWakeLock((PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP), "TAG");
        Log.d("myReceiver", "Receiver new notification for id " + id);

        ToDo todo = DBManager.getInstance(context).getNoteById(id);
        Notification.Builder builder = new Notification.Builder(context);
        if (todo != null) {
            builder.setContentTitle(todo.getTitle());
            builder.setContentText(todo.getDesc());
        }
        else {
            builder.setContentTitle("New notification from SmoothList");
            builder.setContentText("a Task is finished");
        }
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher));
        Notification notification = builder.build();

        wakeLock.acquire();
        notificationManager.notify(id, notification);
        wakeLock.release();
    }
}
