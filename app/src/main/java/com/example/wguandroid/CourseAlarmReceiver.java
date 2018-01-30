package com.example.wguandroid;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.content.WakefulBroadcastReceiver;

public class CourseAlarmReceiver extends WakefulBroadcastReceiver {

    Ringtone ringtone;

    @Override
    public void onReceive(final Context context, Intent intent) {
        //this will update the UI with message
        CourseInfo info = CourseInfo.instance();
        info.setAlarmText("Course Notification!");

        //this will sound the alarm tone
        //this will sound the alarm once, if you wish to
        //raise alarm in loop continuously then use MediaPlayer and setLooping(true)
        Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        if (alarmUri == null) {
            alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        }
        ringtone = RingtoneManager.getRingtone(context, alarmUri);
        ringtone.play();

        //After 1s stop the alarm
        // You can adjust the time depending upon your requirement.
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ringtone.stop();
            }
        }, 1000);

        //this will send a notification message
        ComponentName comp = new ComponentName(context.getPackageName(),
                CourseAlarmService.class.getName());
        startWakefulService(context, (intent.setComponent(comp)));
        setResultCode(Activity.RESULT_OK);
    }
}
