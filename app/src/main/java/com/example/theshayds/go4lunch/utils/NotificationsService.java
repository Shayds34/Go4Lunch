package com.example.theshayds.go4lunch.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.theshayds.go4lunch.R;

public class NotificationsService extends BroadcastReceiver {
    private static final String TAG = "NotificationsService";

    public static final int NOTIFICATION_ID = 1;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive.");

        Notification.Builder builder = new Notification.Builder(context);
        Notification notification = builder.setSmallIcon(R.drawable.go4launch_icon)
                .setContentTitle(context.getResources().getString(R.string.app_name))
                .setContentText("Message") // TODO
                .setAutoCancel(true)
                .build();

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, notification);
    }
}
