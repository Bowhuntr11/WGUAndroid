package com.example.wguandroid;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;


public class CourseAlarmService extends IntentService {
    private NotificationManager alarmNotificationManager;

    public CourseAlarmService() {
        super("Course Notification!");
    }

    @Override
    public void onHandleIntent(Intent intent) {
        sendNotification("Course Starts/Ends within 24 hours!");
    }

    private void sendNotification(String msg) {
        Log.d("CourseAlarmService", "Preparing to send notification...: " + msg);
        alarmNotificationManager = (NotificationManager) this
                .getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), 0);

        NotificationCompat.Builder alarmNotificationBuilder = new NotificationCompat.Builder(
                this).setContentTitle("Course Notification!").setSmallIcon(R.mipmap.launcher)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                .setContentText(msg);


        alarmNotificationBuilder.setContentIntent(contentIntent);
        alarmNotificationManager.notify(1, alarmNotificationBuilder.build());
        Log.d("CourseAlarmService", "Notification sent.");
    }
}
