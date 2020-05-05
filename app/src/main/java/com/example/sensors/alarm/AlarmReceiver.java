package com.example.sensors.alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver {

    private static String TAG = "AlarmReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "onReceive called in broadcast receiver");       //todo change the log.e
        Intent alarmActivityIntent = new Intent(context, AlarmActivity.class);
        alarmActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(alarmActivityIntent);
    }
}
